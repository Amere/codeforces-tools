import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helpers.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class Solver {

    /**
     * Problem unique identifier is ContestId + problem index
     * So a problem id could be 65C which means problem C on contest 65
     * */

    public static HashMap<String, Integer> problemToPoints;
    public static HashMap<String, ArrayList<String>> problemToTags;
    public static ArrayList<Contest> contests;
    public Solver() {
        problemToPoints = new HashMap<String, Integer> ();
        problemToTags = new HashMap<String, ArrayList<String>> ();
    }

    public void prepareProblems() throws Exception {
        ArrayList<String> fileNames = Utils.getFileNamesInADirectory(Utils.PROBLEMS_DATA_PATH);
        for (String fileName : fileNames) {
            JsonObject request = new JsonParser().parse(Utils.readFileAsString(Utils.PROBLEMS_DATA_PATH, fileName)).getAsJsonObject();
            JsonArray problems = request.get("problems").getAsJsonArray();
            for(int i = 0; i < problems.size(); i++) {
                JsonObject problem = problems.get(i).getAsJsonObject();
                String name = problem.get("contestId").getAsInt() + problem.get("index").getAsString();
                JsonArray tags = problem.get("tags").getAsJsonArray();
                for(int j = 0; j < tags.size(); j++) {
                    String tag = tags.get(j).getAsString();
                    if(problemToTags.get(name) == null)
                        problemToTags.put(name, new ArrayList<String>());
                    problemToTags.get(name).add(tag);
                }
                try {
                    int points = problem.get("points").getAsInt();
                    problemToPoints.put(name, points);
                } catch (Exception e) {
                    problemToPoints.put(name, 0);
                }
            }
        }
        System.err.println("Problems loaded to RAM, problems size : " + problemToTags.size());
    }

    public void prepareContests() throws Exception {
        contests = new ArrayList<Contest> ();
        ArrayList<String> fileNames = Utils.getFileNamesInADirectory(Utils.CONTESTS_DATA_PATH);
        for(String fileName : fileNames)
            contests.add(new Contest(fileName));
        System.err.println("Contests loaded to RAM, contests size : " + contests.size());
    }

    public HashMap<Integer, Integer> evaluateConstestPerformance(String handle, boolean plot) throws Exception{
        User curUser = new User(handle);
        System.err.println("User : " + curUser.handle + " loaded successfully");
        System.err.println("Contests cnt : " + curUser.contestRanking.size());
        System.err.println("Problems cnt : " + curUser.firstSubmission.size());
        System.err.println("Accepted problems cnt : " + curUser.acceptedProblems.size());
        return null;
    }

}
