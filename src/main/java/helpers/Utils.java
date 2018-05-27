package helpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Utils {

    public static final String PROBLEMS_DATA_PATH = "/Users/ahmedamer/Desktop/data/problems/";
    public static final String USERS_DATA_PATH = "/Users/ahmedamer/Desktop/data/users/";
    public static final String CONTESTS_DATA_PATH = "/Users/ahmedamer/Desktop/data/contests/";
    public static final String PROBLEMS_SERIALIZED_HASH_POINTS = "/Users/ahmedamer/Desktop/data/";
    public static final String PROBLEMS_SERIALIZED_HASH_TAG = "/Users/ahmedamer/Desktop/data/";

    public static final String PROBLEMS_SERIALIZED_HASH_POINTS_FILE = "PROBLEMS_SERIALIZED_HASH_POINTS_FILE";
    public static final String PROBLEMS_SERIALIZED_HASH_TAG_FILE = "PROBLEMS_SERIALIZED__HASH_TAG_FILE";


    /**
     * Get FileNames In A Directory
     *
     * @param fileName
     * @return
     */
    public static ArrayList<String> getFileNamesInADirectory(String fileName) {
        File folder = new File(fileName);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                fileNames.add(listOfFile.getName());
            }
        }
        return fileNames;
    }

    /**
     * Read File As A String
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String readFileAsString(String path, String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path + fileName)));
    }

    /**
     * Write to a file
     *
     * @param fileName
     * @param text
     * @throws IOException
     */
    public static void writeToAFile(String fileName, String text) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(text);
        bw.close();
    }

    public static JsonArray getUserStatus(String handle) throws  Exception{
        return new JsonParser().parse(Utils.readFileAsString(Utils.USERS_DATA_PATH + "/"
                + handle + "/", "status.json")).getAsJsonArray();
    }

    public static JsonArray getUserRating(String handle) throws Exception{
        return new JsonParser().parse(Utils.readFileAsString(Utils.USERS_DATA_PATH + "/"
                + handle + "/", "rating.json")).getAsJsonArray();
    }
}
