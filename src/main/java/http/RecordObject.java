package http;

import java.io.Serializable;
import java.util.Arrays;

public class RecordObject implements Serializable {
    private long currentLength;
    private long[] startPos;
    private long[] endPos;

    public RecordObject(long[] startPos, long[] endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long[] getStartPos() {
        return startPos;
    }

    public void setStartPos(long[] startPos) {
        this.startPos = startPos;
    }

    public long[] getEndPos() {
        return endPos;
    }

    public void setEndPos(long[] endPos) {
        this.endPos = endPos;
    }

    @Override
    public String toString() {
        return "RecordObject{" +
                ", startPos=" + Arrays.toString(startPos) +
                ", endPos=" + Arrays.toString(endPos) +
                '}';
    }
}
