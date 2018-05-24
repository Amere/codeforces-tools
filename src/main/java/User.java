import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import helpers.Utils;

import java.util.HashMap;
import java.util.HashSet;

public class User {

    /**
     * @param handle : User name
     * @param firstSubmission : the relative time in milliseconds
     *                        of the first submission in the contest
     *                        for this problem for that user
     * @param acceptedProblems : Hashset of accepted problems of this user
     * @param contestRanking : the rank of this user in each contest
     */

    String handle;
    HashMap<String, Integer> firstSubmission;
    HashSet<String> acceptedProblems;
    HashMap<Integer, Integer> contestRanking;
    public User(String handle) throws Exception{
        firstSubmission = new HashMap<String, Integer>();
        contestRanking = new HashMap<Integer, Integer>();
        acceptedProblems = new HashSet<String> ();
        this.handle = handle;
        processFirstSubmissions();
        processContestsRanking();
    }
    public void processFirstSubmissions() throws Exception{
        JsonArray STATUS = Utils.getUserStatus(this.handle);
        for(JsonElement record : STATUS) {
            JsonObject data = record.getAsJsonObject();
            if(data.get("author").getAsJsonObject().get("participantType").getAsString().equals("CONTESTANT")) {
                String problemName = data.get("problem").getAsJsonObject().get("contestId").getAsInt() +
                        data.get("problem").getAsJsonObject().get("index").getAsString();
                Integer curTime = data.get("relativeTimeSeconds").getAsInt();
                Integer cur = firstSubmission.get(problemName);
                if(cur == null) cur = curTime;
                cur = Math.min(cur, curTime);
                firstSubmission.put(problemName, cur);
                if(data.get("verdict").getAsString().equals("OK")) {
                    acceptedProblems.add(problemName);
                }
            }
        }
    }
    public void processContestsRanking() throws  Exception{
        JsonArray RATING = Utils.getUserRating(this.handle);
        for(JsonElement record : RATING) {
            JsonObject data = record.getAsJsonObject();
            System.out.println(data.get("contestId").getAsInt() + " " + data.get("rank").getAsInt());
            contestRanking.put(data.get("contestId").getAsInt(), data.get("rank").getAsInt());
        }
    }
}
