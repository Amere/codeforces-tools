import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import helpers.Serializer;
import helpers.Utils;

import java.io.Serializable;
import java.util.*;


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
    }

    public void processFirstSubmissions() throws Exception {
        JsonArray STATUS = Utils.getUserStatus(this.handle);
        for(JsonElement record : STATUS) {
            JsonObject data = record.getAsJsonObject();
            try {
                String problemName = data.get("contestId").getAsInt() +
                        data.get("problem").getAsJsonObject().get("index").getAsString();
                Integer curTime = data.get("relativeTimeSeconds").getAsInt();
                Integer createdTime = data.get("creationTimeSeconds").getAsInt();
                /**
                 * If verdic is OK, add to the sorted ArrayList of
                 * accepted submissions of this user
                 * to be binary-searched on later
                 */
                if(data.get("verdict").getAsString().equals("OK")) {
                    AcceptedProblemDataPair pair = new AcceptedProblemDataPair(problemName, createdTime);
                    if(!sortedAcceptedProblems.contains(pair))
                        sortedAcceptedProblems.add(pair);
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
            catch (Exception e){
                System.err.println("This problem data is corrupted");
            }
        }
        Collections.sort(sortedAcceptedProblems);
        Serializer ser = new Serializer(Utils.USERS_DATA_PATH + "/" + handle + "/", "sortedAcceptedProblems");
        ser.writeObject(sortedAcceptedProblems);
        ser = new Serializer(Utils.USERS_DATA_PATH + "/" + handle + "/", "firstSubmissions");
        ser.writeObject(firstSubmission);
    }


    public void processContestsRanking() throws  Exception {
        JsonArray RATING = Utils.getUserRating(this.handle);
        for(JsonElement record : RATING) {
            /**
             * Add the contests ranking hashmap of contest id
             * and the contestant rank in it
             */
            JsonObject data = record.getAsJsonObject();
            contestRanking.put(data.get("contestId").getAsInt(), data.get("rank").getAsInt());
        }

        Serializer ser = new Serializer(Utils.USERS_DATA_PATH + "/" + handle + "/", "contestRanking");
        ser.writeObject(contestRanking);
        /**
         * Users final rarting at current time
         */

        if(RATING.size() > 0)
            this.currentRating = RATING.get(RATING.size() - 1).getAsJsonObject().get("newRating").getAsInt();
        else
            this.currentRating = 0;
    }

    /**
     *
     * CLASS ACCEPTED PROBLEM PAIR
     * To be user in sorting
     */

    public class AcceptedProblemDataPair implements Comparable, Serializable {
        public int timeCreated;
        public String problemName;

        private static final long serialVersionUID = 9135453663667502917L;

        public AcceptedProblemDataPair(String problemName, int timeCreated) {
            this.timeCreated = timeCreated;
            this.problemName = problemName;
        }

        @Override
        public int compareTo(Object o) {
            return this.timeCreated - ((AcceptedProblemDataPair) o).timeCreated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AcceptedProblemDataPair that = (AcceptedProblemDataPair) o;
            return Objects.equals(problemName, that.problemName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(problemName);
        }

        @Override
        public String toString() {
            return "AcceptedProblemDataPair{" +
                    "timeCreated=" + timeCreated +
                    ", problemName='" + problemName + '\'' +
                    '}';
        }
    }
     static class UserRatingDataPair implements Comparable, Serializable {
        public int currentRating;
        public String handle;
        public UserRatingDataPair(String handle, int currentRating) {
            this.currentRating = currentRating;
            this.handle = handle;
        }

        int compareTo(UserRatingDataPair  other) {
            return this.currentRating - other.currentRating;
        }

        @Override
        public int compareTo(Object o) {
            return this.currentRating - ((UserRatingDataPair) o).currentRating;
        }
    }
    static class UserActivityDataPair implements Comparable, Serializable {
        public int numOfProblems;
        public String handle;
        public UserActivityDataPair(String handle, int numOfProblems) {
            this.numOfProblems = numOfProblems;
            this.handle = handle;
        }

        int compareTo(UserActivityDataPair  other) {
            return this.numOfProblems - other.numOfProblems;
        }

        @Override
        public int compareTo(Object o) {
            return this.numOfProblems - ((UserActivityDataPair) o).numOfProblems;
        }
    }
}
