package http;

import util.StorageUnit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 测试下载链接：
 * https://dldir1.qq.com/qqfile/qq/PCTIM/TIM3.2.0/TIM3.2.0.21856.exe
 * http://dx.weidown.com/202005/tbgjx_2020.05.7z
 */
public class HttpDownloadTask {
    private static final int THREAD_NUMBER = 8;
    private static final ExecutorService service = Executors.newFixedThreadPool(THREAD_NUMBER);

    private final String urlStr;
    private final String filePrefixDir;
    private final CountDownLatch latch;
    private final AtomicLong progress;
    private final Map<String, String> statusMap;

    private String tempFilename;
    private URL url;
    private DownloadThread[] threads;
    private DownloadStatus status;
    private RecordObject record;
    private long fileLength;
    private long previousLength;

    public HttpDownloadTask(String urlStr, String filePrefixDir) {
        this.urlStr = urlStr;
        this.filePrefixDir = filePrefixDir;
        this.latch = new CountDownLatch(THREAD_NUMBER);
        this.progress = new AtomicLong(0L);
        this.status = DownloadStatus.NEW;
        this.statusMap = new HashMap<>();
    }

    public DownloadStatus getStatus() {
        return status;
    }

    /**
     * 获取下载进度百分比
     * @return 进度百分比
     */
    public int getProgressPercentage() {
        if (fileLength == 0L) {
            return 0;
        }
        return (int) ((progress.get() * 1.0 / fileLength) * 100);
    }

    /**
     * 返回任务状态信息
     *
     * @param duration 更新频率
     * @return 状态信息
     */
    public Map<String, String> getStatusMap(float duration) {
        //计算下载速度
        long currentLength = progress.get();
        float speed = (currentLength - previousLength) / duration;
        previousLength = currentLength;
        statusMap.put("speed", StorageUnit.convertTo(speed));
        //计算剩余下载时间
        float time = (fileLength - currentLength) / speed;
        statusMap.put("time", String.format("%.2f sec", time));
        //当前下载大小
        statusMap.put("currentSize", StorageUnit.convertTo(currentLength));
        //总共下载大小
        statusMap.put("totalSize", StorageUnit.convertTo(fileLength));
        return statusMap;
    }

    public void stop() {
        if (status == DownloadStatus.DOWNLOADING) {
            System.out.println("正在暂停任务");
            for (DownloadThread thread : threads) {
                thread.setStop(true);
            }
            status = DownloadStatus.STOPPED;
            //Save Record
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(filePrefixDir, tempFilename)))) {
                record.setCurrentLength(progress.get());
                out.writeObject(record);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化下载连接
     * 判断是否支持多线程， 并开启下载任务
     * @throws RuntimeException 程序异常（包括描述）
     */
    public void resolve() throws RuntimeException {
        status = DownloadStatus.INIT;
        String filename = urlStr.substring(urlStr.lastIndexOf("/") + 1);
        //检测url是否包含http参数
        if (filename.contains("?")) {
            //删除多余的参数数据
            //范围区间左闭右开
            filename = filename.substring(0, filename.indexOf("?"));
        }
        tempFilename = filename + ".tmp";

        HttpURLConnection conn = null;
        try {
            url = new URL(urlStr);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();

            //判断状态码
            if (conn.getResponseCode() > 299) {
                throw new RuntimeException("网页无法访问，错误码 " + conn.getResponseCode());
            }

            //获取文件大小
            fileLength = conn.getContentLengthLong();
            //下载文件和临时文件
            File file = new File(filePrefixDir, filename);
            File tempFile = new File(filePrefixDir, tempFilename);

            if (file.exists() && !tempFile.exists() && file.length() == fileLength) {
                System.out.println("文件已存在");
                throw new RuntimeException("文件已存在");
            }
            //判断是否支持多线程下载
            boolean acceptRanges = conn.getHeaderField("accept-ranges") != null;
            System.out.printf("开始下载任务:%s, 文件大小: %s\n", filename, StorageUnit.convertTo(fileLength));
            System.out.printf("是否支持多线程下载:%s\n", acceptRanges);
            status = DownloadStatus.DOWNLOADING;
            if (acceptRanges) {
                //检测断点，初始化下载位置
                setBreakPoint(tempFile);
                //创建下载线程
                threads = new DownloadThread[THREAD_NUMBER];
                for (int i = 0; i < THREAD_NUMBER; i++) {
                    threads[i] = new DownloadThread(i, url, file, record, progress, latch);
                    service.submit(threads[i]);
                }
                //等待所有线程下载完成
                latch.await();
                //删除临时文件, 暂停不删除文件
                if (tempFile.exists() && status == DownloadStatus.DOWNLOADING) {
                    tempFile.delete();
                }
            } else {
                directDownload(file);
            }
            status = DownloadStatus.FINISHED;

        } catch (MalformedURLException e) {
            throw new RuntimeException("URL 解析失败");
        } catch (IOException e) {
            System.out.println("I/O错误");
            throw new RuntimeException("无法连接到服务器");
        } catch (InterruptedException e) {
            System.out.println("下载被中断");
            throw new RuntimeException("线程被中断");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 检测多点续传文件，并设置下载区间
     *
     * @param tempFile 临时文件
     * @throws IOException IO异常
     */
    private void setBreakPoint(File tempFile) throws IOException {
        if (tempFile.exists()) {
            //检测到临时文件，开始断点续传
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile))) {
                record = (RecordObject) in.readObject();
                progress.set(record.getCurrentLength());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            //未检测到临时文件，创建临时文件
            //初始化下载位置
            long[] startPos = new long[THREAD_NUMBER];
            long[] endPos = new long[THREAD_NUMBER];
            long chuckSize = fileLength / (long) THREAD_NUMBER;
            for (int i = 0; i < THREAD_NUMBER; i++) {
                long start = (long) i * chuckSize;
                long end = start + chuckSize - 1;
                if (i == THREAD_NUMBER - 1) {
                    end = fileLength;
                }
                startPos[i] = start;
                endPos[i] = end;
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile))) {
                record = new RecordObject(startPos, endPos);
                out.writeObject(record);
            }
        }
    }

    /**
     * 直接下载（单线程）
     *
     * @param file 下载文件
     * @throws IOException io异常
     */
    private void directDownload(File file) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            try (var input = new BufferedInputStream(conn.getInputStream());
                 var output = new FileOutputStream(file)) {
                input.transferTo(output);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
