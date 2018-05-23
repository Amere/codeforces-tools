import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helpers.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class Main {



    public void seed() throws IOException {

    }

    public static void main(String[] args) throws Exception {
        ArrayList<String> fileNames = Utils.getFileNamesInADirectory(Utils.DATA_PATH);
        for (String fileName : fileNames) {
            JsonObject request = new JsonParser().parse(Utils.readFileAsString(fileName)).getAsJsonObject();
            Utils.writeToAFile("problems.json", request.toString());
        }
    }
}
