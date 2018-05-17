package helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Utils {

    public static final String DATA_PATH = "/home/hossam/data/problems";

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
    public static String readFileAsString(String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(DATA_PATH + fileName)));
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
    }
}
