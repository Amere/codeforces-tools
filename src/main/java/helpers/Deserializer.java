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
    private ObjectInputStream objectInputStream;

    public Deserializer(String path, String fileName) throws Exception {
        fileInputStream =  new FileInputStream(path + fileName + ".ser/");
        objectInputStream = new ObjectInputStream(fileInputStream);
    }

    public Object readObject() throws Exception {
        Object ret = objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return ret;
    }

}
