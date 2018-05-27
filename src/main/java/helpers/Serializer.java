package helpers;

import java.io.FileOutputStream;
import java.io.*;

/**
 * To work as a factory of Serialization process
 * not to forget important stuff like closing files
 */

public class Serializer {
    private FileOutputStream fileOutputStream;
    private ObjectOutputStream ojectOutputStream;

    public Serializer(String path, String fileName) throws Exception {
        fileOutputStream =  new FileOutputStream(path + fileName + ".ser/");
        ojectOutputStream = new ObjectOutputStream(fileOutputStream);
    }

    public void writeObject(Object o) throws Exception {
        ojectOutputStream.writeObject(o);
        ojectOutputStream.close();
        fileOutputStream.close();
    }
}
