package helpers;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * To work as a factory of Desrialization process
 * object.read();
 * not forget to close files
 */

public class Deserializer {
    private FileInputStream fileInputStream;
    private ObjectInputStream ojectInputStream;

    public Deserializer(String path, String fileName) throws Exception {
        fileInputStream =  new FileInputStream(path + fileName + ".ser/");
        ojectInputStream = new ObjectInputStream(fileInputStream);
    }

    public Object readObject() throws Exception {
        Object ret = ojectInputStream.readObject();
        ojectInputStream.close();
        fileInputStream.close();
        return ret;
    }

}
