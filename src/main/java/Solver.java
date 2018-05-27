import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helpers.Deserializer;
import helpers.Serializer;
import helpers.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Solver {

    /**
     * Problem unique identifier is ContestId + problem index
     * So a problem id could be 65C which means problem C on contest 65
     * */

    public Solver() {
    }


    public void prepareProblems() throws Exception {
        HashMap<String, Integer> problemToPoints = new HashMap<String, Integer> ();
        HashMap<String, ArrayList<String>> problemToTags = new HashMap<String, ArrayList<String>> ();
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
        Serializer ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_POINTS, Utils.PROBLEMS_SERIALIZED_HASH_POINTS_FILE );
        ser.writeObject(problemToPoints);
        ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_TAG, Utils.PROBLEMS_SERIALIZED_HASH_TAG_FILE );
        ser.writeObject(problemToTags);
        System.err.println("Problems deleted from RAM");
    }

    public void loadUsers() throws Exception {
        /**
         * Here we should go over all users in the directory
         * and get their currentRating from the object created
         *
         * then add it to an arraylist created locally here
         * arraylist of pair of the handle and the corresponding rating
         * then sort this arraylist based on rating
         */
        ArrayList<String> fileNames = Utils.getFolderNamesInADirectory(Utils.USERS_DATA_PATH);
        ArrayList<User.UserRatingDataPair> ratings = new ArrayList<>();
        for (String fileName : fileNames) {
            User user = new User(fileName);
            User.UserRatingDataPair pair = new User.UserRatingDataPair(user.handle,user.currentRating);
            ratings.add(pair);
        }
        Collections.sort(ratings);
        Serializer ser = new Serializer("/Users/mohamedalattal/Documents/Semester10/", "users_ratings" );
        ser.writeObject(ratings);
    }


    /**
     * I'll complete the contests here the same with prepare problems
     * @throws Exception
     */

    public void prepareContests() throws Exception {
        ArrayList<Contest> contests = new ArrayList<Contest> ();
        ArrayList<String> fileNames = Utils.getFileNamesInADirectory(Utils.CONTESTS_DATA_PATH);
        for(String fileName : fileNames)
            contests.add(new Contest(fileName));
        System.err.println("Contests loaded to RAM, contests size : " + contests.size());
    }

    public HashMap<Integer, Integer> evaluateConstestPerformance(String handle, boolean plot) throws Exception {
        User curUser = new User(handle);
        System.err.println("User : " + curUser.handle + " loaded successfully");
        System.err.println("Contests cnt : " + curUser.contestRanking.size());
        System.err.println("Problems cnt : " + curUser.firstSubmission.size());
//        System.err.println("Accepted problems cnt : " + curUser.acceptedProblems.size());
//        return null;
        return null;
    }

    public static void main(String[] args) throws Exception {
        Solver s = new Solver();
        s.loadUsers();
    }
}
