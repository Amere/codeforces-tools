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
        HashMap<String, Integer> problemToCount = new HashMap<String, Integer> ();
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
            JsonArray problemStatistics = request.get("problemStatistics").getAsJsonArray();
            for(int i = 0; i < problemStatistics.size(); i++){
                JsonObject problem = problemStatistics.get(i).getAsJsonObject();
                String name = problem.get("contestId").getAsInt() + problem.get("index").getAsString();
                Integer solvedCount = problem.get("solvedCount").getAsInt();
                problemToCount.put(name,solvedCount);
            }
        }
        System.err.println("Problems loaded to RAM, problems size : " + problemToTags.size());
        Serializer ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_POINTS, Utils.PROBLEMS_SERIALIZED_HASH_POINTS_FILE );
        ser.writeObject(problemToPoints);
        ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_TAG, Utils.PROBLEMS_SERIALIZED_HASH_TAG_FILE );
        ser.writeObject(problemToTags);
        ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_COUNT, Utils.PROBLEMS_SERIALIZED_HASH_COUNT_FILE );
        ser.writeObject(problemToCount);
        System.err.println("Problems deleted from RAM");
    }

//    public void readProblemsTest() throws  Exception{
//        Deserializer deser = new Deserializer(Utils.PROBLEMS_SERIALIZED_HASH_POINTS, Utils.PROBLEMS_SERIALIZED_HASH_POINTS_FILE);
//        HashMap<String, Integer> Ret = (HashMap<String, Integer>) deser.readObject();
//        System.out.println(Ret.size() + Ret.toString());
//    }

    public void prepareUsers() throws Exception {
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

        System.out.println("Started loading users");
        for (String fileName : fileNames) {
            if(fileName.equals("Amerisma")) {
                System.out.println("Amerisma Insettion");
                User user = new User(fileName);
                User.UserRatingDataPair pair = new User.UserRatingDataPair(user.handle, user.currentRating);
                ratings.add(pair);
                System.out.println("Amerisma Insettion");
            }
        }
        Collections.sort(ratings);
        Serializer ser = new Serializer(Utils.RATINGS_PATH, Utils.RATINGS_FILE );
        ser.writeObject(ratings);
        System.out.println("Finished loading users");
        System.out.println(ratings);
    }



    public void prepareContests() throws Exception {
        ArrayList<Contest> contests = new ArrayList<Contest> ();
        ArrayList<String> fileNames = Utils.getFileNamesInADirectory(Utils.CONTESTS_DATA_PATH);
        for(String fileName : fileNames)
            contests.add(new Contest(fileName));
        System.err.println("Contests loaded to RAM, constests size : " + contests.size());
        Serializer ser = new Serializer(Utils.CONTESTS_ARRAY_PATH, Utils.CONTESTS_ARRAY_FILE);
        ser.writeObject(contests);
        System.err.println("Contests deleted from RAM");
        System.err.println("Contests Array written on disk");
    }

    public HashMap<Integer, Integer> evaluateConstestPerformance(String handle, boolean plot) throws Exception {
//        User curUser = new User(handle);
//        System.err.println("User : " + curUser.handle + " loaded successfully");
//        System.err.println("Contests cnt : " + curUser.contestRanking.size());
//        System.err.println("Problems cnt : " + curUser.firstSubmission.size());

        /**
         * Load users serialized data
         * to answer the problem
         */
        Deserializer deser = new Deserializer(Utils.USERS_DATA_PATH + handle + "/", "contestRanking");
        HashMap<Integer, Integer> contestRanking = (HashMap<Integer, Integer>) deser.readObject();
        deser = new Deserializer(Utils.USERS_DATA_PATH + handle + "/", "firstSubmissions");
        HashMap<String, Integer> firstSubmission = (HashMap<String, Integer>) deser.readObject();
        deser = new Deserializer(Utils.CONTESTS_ARRAY_PATH, Utils.CONTESTS_ARRAY_FILE);
        ArrayList<Contest> contests = (ArrayList<Contest>) deser.readObject();
        System.out.println(contestRanking.size() + contestRanking.toString());
        return null;
    }
}
