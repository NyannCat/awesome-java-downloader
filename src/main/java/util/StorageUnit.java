package util;

public class StorageUnit {
    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;

    public static String convertTo(long size) {
        return convertTo((float) size);
    }

    public static String convertTo(float size) {
        if (size >= GB) {
            return String.format("%.2fGB", size / GB);
        } else if (size >= MB) {
            return String.format("%.2fMB", size / MB);
        } else {
            return String.format("%.2fKB", size / KB);
        }
    }
}
