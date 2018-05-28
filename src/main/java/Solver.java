import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helpers.Deserializer;
import helpers.Serializer;
import helpers.Utils;

import java.util.*;

public class Solver {

    /**
     * Problem unique identifier is ContestId + problem index
     * So a problem id could be 65C which means problem C on contest 65
     */

    public Solver() throws Exception {
        /**
         * Preprocessing the data
         */
        this.prepareProblems();
        this.prepareUsers();
        //this.prepareContests();
    }


    public void prepareProblems() throws Exception {
        HashMap<String, Integer> problemToPoints = new HashMap<String, Integer>();
        HashMap<String, Integer> problemToCount = new HashMap<String, Integer>();
        HashMap<String, HashSet<String>> problemToTags = new HashMap<String, HashSet<String>>();
        ArrayList<String> fileNames = Utils.getFileNamesInADirectory(Utils.PROBLEMS_DATA_PATH);
        for (String fileName : fileNames) {
            JsonObject request = new JsonParser().parse(Utils.readFileAsString(Utils.PROBLEMS_DATA_PATH, fileName)).getAsJsonObject();
            JsonArray problems = request.get("problems").getAsJsonArray();
            for (int i = 0; i < problems.size(); i++) {
                JsonObject problem = problems.get(i).getAsJsonObject();
                String name = problem.get("contestId").getAsInt() + problem.get("index").getAsString();
                JsonArray tags = problem.get("tags").getAsJsonArray();
                for (int j = 0; j < tags.size(); j++) {
                    String tag = tags.get(j).getAsString();
                    if (problemToTags.get(name) == null)
                        problemToTags.put(name, new HashSet<String>());
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
            for (int i = 0; i < problemStatistics.size(); i++) {
                JsonObject problem = problemStatistics.get(i).getAsJsonObject();
                String name = problem.get("contestId").getAsInt() + problem.get("index").getAsString();
                Integer solvedCount = problem.get("solvedCount").getAsInt();
                problemToCount.put(name, solvedCount);
            }
        }
        System.err.println("Problems loaded to RAM, problems size : " + problemToTags.size());
        Serializer ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_POINTS, Utils.PROBLEMS_SERIALIZED_HASH_POINTS_FILE);
        ser.writeObject(problemToPoints);
        ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_TAG, Utils.PROBLEMS_SERIALIZED_HASH_TAG_FILE);
        ser.writeObject(problemToTags);
        ser = new Serializer(Utils.PROBLEMS_SERIALIZED_HASH_COUNT, Utils.PROBLEMS_SERIALIZED_HASH_COUNT_FILE);
        ser.writeObject(problemToCount);

        ProblemSelection.SegmentTree problemCountSegmentTree = new ProblemSelection.SegmentTree(90000);
        Iterator it = problemToCount.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String) pair.getKey();
            int value = (int) pair.getValue();
            it.remove();
            problemCountSegmentTree.set(value, new ProblemSelection.Node(new String[]{name},value));
        }
        ser = new Serializer(Utils.PROBLEMS_SERIALIZED_SEG_TREE_COUNT, Utils.PROBLEMS_SERIALIZED_SEG_TREE_COUNT_FILE);
        ser.writeObject(problemCountSegmentTree);

        System.err.println("Problems deleted from RAM");
    }


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
        int count = 0;
        for (String fileName : fileNames) {
            User user = new User(fileName);
            User.UserRatingDataPair pair = new User.UserRatingDataPair(user.handle, user.currentRating);
            ratings.add(pair);
            System.out.println(user.handle);
            if (++count == 50)
                break;
        }
        Collections.sort(ratings);
        Serializer ser = new Serializer(Utils.RATINGS_PATH, Utils.RATINGS_FILE);
        ser.writeObject(ratings);
        System.out.println("Finished loading users");
        System.out.println(ratings);
    }


    public void prepareContests() throws Exception {
        HashMap<Integer, Contest> contests = new HashMap<Integer, Contest>();
        ArrayList<String> fileNames = Utils.getFileNamesInADirectory(Utils.CONTESTS_DATA_PATH);
        for (String fileName : fileNames) {
            Contest c = new Contest(fileName);
            contests.put(c.id, c);
        }
        System.err.println("Contests loaded to RAM, contests size : " + contests.size());
        Serializer ser = new Serializer(Utils.CONTESTS_ARRAY_PATH, Utils.CONTESTS_ARRAY_FILE);
        ser.writeObject(contests);
        System.err.println("Contests deleted from RAM");
        System.err.println("Contests HashMap written on disk");
    }

    public HashMap<Integer, Integer> evaluateConstestPerformance(String handle, boolean plot) throws Exception {
        /**
         * Load users serialized data
         * to answer the problem
         */

        Deserializer deser = new Deserializer(Utils.USERS_DATA_PATH + handle + "/", "contestRanking");
        HashMap<Integer, Integer> contestRanking = (HashMap<Integer, Integer>) deser.readObject();
        deser = new Deserializer(Utils.PROBLEMS_SERIALIZED_HASH_POINTS, Utils.PROBLEMS_SERIALIZED_HASH_POINTS_FILE);
        HashMap<String, Integer> problemToPoints = (HashMap<String, Integer>) deser.readObject();
        deser = new Deserializer(Utils.USERS_DATA_PATH + handle + "/", "firstSubmissions");
        HashMap<String, Integer> firstSubmission = (HashMap<String, Integer>) deser.readObject();
        deser = new Deserializer(Utils.CONTESTS_ARRAY_PATH, Utils.CONTESTS_ARRAY_FILE);

        HashMap<Integer, Contest> contests = (HashMap<Integer, Contest>) deser.readObject();

        System.out.println("Old Rankings : " + contestRanking);

        for (HashMap.Entry<Integer, Integer> entry : contestRanking.entrySet()) {
            int currentContestId = entry.getKey();
            int currentRank = entry.getValue();

            Contest c = contests.get(currentContestId);
            if (c.type.equals("ICPC")) {
                int totPenalty = 0;
                for (Integer cur : c.penalty.get(currentRank)) {
                    if (cur != -1) //NotSolvedProblem
                        totPenalty += cur;
                }
                int newPenalty = 0;
                for (int i = 0; i < c.penalty.get(currentRank).size(); i++) {
                    if (c.penalty.get(currentRank).get(i) != -1) {
                        String problemIndex = ((char) ('A' + i)) + "";
                        Integer contestId = c.id;
                        String problemName = contestId + problemIndex;
                        System.out.println(problemName);
                        int timeForFirstSubmission = firstSubmission.get(problemName) / 60;
                        newPenalty += timeForFirstSubmission;
                    }
                }
                System.out.println("Penalty " + newPenalty + " " + totPenalty);
            } else {
                int totPoints = 0;
                for (Integer cur : c.points.get(currentRank)) {
                    if (cur != -1) //NotSolvedProblem
                        totPoints += cur;
                }
                int newPoints = 0;
                for (int i = 0; i < c.points.get(currentRank).size(); i++) {
                    if (c.points.get(currentRank).get(i) != -1) {
                        String problemIndex = ((char) ('A' + i)) + "";
                        Integer contestId = c.id;
                        String problemName = contestId + problemIndex;
                        System.out.println(problemName + " " + contestId + " " + problemIndex + " " + problemToPoints);
                        int problemPoints = problemToPoints.get(problemName);
                        System.out.println(problemName);
                        int timeForFirstSubmission = firstSubmission.get(problemName) / 60000;
                        newPoints += problemPoints - (problemPoints / 250) * timeForFirstSubmission;
                    }
                }
                System.out.println(totPoints  + "  " + newPoints);
            }
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        System.out.println(firstSubmission.size() + firstSubmission.toString());
        return null;
    }
}
