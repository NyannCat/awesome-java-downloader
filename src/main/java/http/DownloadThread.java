package http;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadThread implements Runnable {

    private final int id;
    private final URL url;
    private final File downloadFile;
    private final RecordObject record;
    private final AtomicLong progress;
    private final CountDownLatch latch;

    private volatile boolean stop = false;

    public DownloadThread(int id, URL url, File downloadFile,
                          RecordObject record, AtomicLong progress, CountDownLatch latch) {
        this.id = id;
        this.url = url;
        this.downloadFile = downloadFile;
        this.record = record;
        this.progress = progress;
        this.latch = latch;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        long start = record.getStartPos()[id];
        long end = record.getEndPos()[id];
        if (start > end) {
            return;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Range", String.format("bytes=%d-%d", start, end));
            conn.connect();
            try (var input = conn.getInputStream();
                 var output = new RandomAccessFile(downloadFile, "rw")) {
                output.seek(start);
                byte[] buffer = new byte[8192];
                long currentPos = start;
                int readCount;
                while ((readCount = input.read(buffer, 0, buffer.length)) != 0 && !stop) {
                    output.write(buffer, 0, readCount);
                    progress.addAndGet(readCount);
                    currentPos += readCount;
                    record.getStartPos()[id] = currentPos;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
