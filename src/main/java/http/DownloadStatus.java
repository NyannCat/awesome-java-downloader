package http;

public enum DownloadStatus {
    NEW("新建任务"),
    INIT("正在初始化"),
    DOWNLOADING("下载中"),
    STOPPED("已暂停"),
    FINISHED("已完成");

    public String description;

    DownloadStatus(String description) {
        this.description = description;
    }
}
