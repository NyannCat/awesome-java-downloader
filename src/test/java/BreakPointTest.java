import http.RecordObject;
import org.junit.Test;

import java.io.*;

public class BreakPointTest {

    @Test
    public void serialize() {
        long[] startPos = new long[] {1,2,3,4};
        long[] endPos = new long[] {1,2,3,4};
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("abc.temp"))) {
            RecordObject data = new RecordObject(startPos, endPos);
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deserialize() {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("abc.temp"))) {
            RecordObject record = (RecordObject) in.readObject();
            System.out.println(record);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
