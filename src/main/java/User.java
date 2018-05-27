import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import helpers.Serializer;
import helpers.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class User implements Serializable {

    /**
     * @param handle : User name
     * @param firstSubmission : the relative time in milliseconds
     *                        of the first submission in the contest
     *                        for this problem for that user
     * @param acceptedProblems : Hashset of accepted problems of this user
     * @param contestRanking : the rank of this user in each contest
     */

    public String handle;
    public HashMap<String, Integer> firstSubmission;
    public ArrayList<AcceptedProblemDataPair> sortedAcceptedProblems;
    public HashMap<Integer, Integer> contestRanking;
    public transient int currentRating;

    public User(String handle) throws Exception {
        firstSubmission = new HashMap<String, Integer>();
        contestRanking = new HashMap<Integer, Integer>();
        sortedAcceptedProblems = new ArrayList<AcceptedProblemDataPair> ();
        this.handle = handle;
        processFirstSubmissions();
        processContestsRanking();
        Collections.sort(sortedAcceptedProblems);
    }
    public void processFirstSubmissions() throws Exception {
        JsonArray STATUS = Utils.getUserStatus(this.handle);
        for(JsonElement record : STATUS) {
            JsonObject data = record.getAsJsonObject();
            String problemName = data.get("problem").getAsJsonObject().get("contestId").getAsInt() +
                    data.get("problem").getAsJsonObject().get("index").getAsString();
            Integer curTime = data.get("relativeTimeSeconds").getAsInt();
            Integer createdTime = data.get("creationTimeSeconds").getAsInt();

            /**
             * If verdic is OK, add to the sorted ArrayList of
             * accepted submissions of this user
             * to be binary-searched on later
             */
            if(data.get("verdict").getAsString().equals("OK")) {
                sortedAcceptedProblems.add(new AcceptedProblemDataPair(problemName, createdTime));
            }

            if(data.get("author").getAsJsonObject().get("participantType").getAsString().equals("CONTESTANT")) {
                /**
                 * If the submission is Contestant
                 * then we can use later to be the first submission
                 */
                Integer cur = firstSubmission.get(problemName);
                if(cur == null) cur = curTime;
                cur = Math.min(cur, curTime);
                firstSubmission.put(problemName, cur);
            }
        }
        Serializer ser = new Serializer(Utils.USERS_DATA_PATH + "/" + handle + "/", "sortedAcceptedProblems");
        ser.writeObject(sortedAcceptedProblems);
        ser = new Serializer(Utils.USERS_DATA_PATH + "/" + handle + "/", "firstSubmission");

    }
    public void processContestsRanking() throws  Exception {
        JsonArray RATING = Utils.getUserRating(this.handle);
        for(JsonElement record : RATING) {
            /**
             * Add the contests ranking hashmap of contest id
             * and the contestant rank in it
             */
            JsonObject data = record.getAsJsonObject();
            System.out.println(data.get("contestId").getAsInt() + " " + data.get("rank").getAsInt());
            contestRanking.put(data.get("contestId").getAsInt(), data.get("rank").getAsInt());
        }
        /**
         * Users final rarting at current time
         */
        this.currentRating = RATING.get(RATING.size() - 1).getAsJsonObject().get("newRating").getAsInt();
    }

    /**
     *
     * CLASS ACCEPTED PROBLEM PAIR
     * To be user in sorting
     */

    public class AcceptedProblemDataPair implements Comparable, Serializable {
        public int timeCreated;
        public String problemName;
        public AcceptedProblemDataPair(String problemName, int timeCreated) {
            this.timeCreated = timeCreated;
            this.problemName = problemName;
        }

        int compareTo(AcceptedProblemDataPair  other) {
            return this.timeCreated - other.timeCreated;
        }

        @Override
        public int compareTo(Object o) {
            return this.timeCreated - ((AcceptedProblemDataPair) o).timeCreated;
        }
    }
}
