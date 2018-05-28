import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helpers.Utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Contest implements Serializable {
    /**
     *
     * A Contest Class that contains all contest
     * important details
     * All arrays are sorted according
     * to the rank of the user with this handle
     * int the current contest
     * so handle[0] is the handle of the first user
     * penalty[0][0]
     */



    /**
     * @param handle[0] : means the name of the user who got rank 1
     * @param type : ICPC / CF
     * @param id : contestId
     * @param problemCnt : number of problems in the contest
     * @param penalty[0][0] : means the penalty of the user with rank 1 got in problem A (ICPC)
     * @param points[0][0] : means the points of the user with rank 1 got in problem A (CF)
     */
    public String type;
    public int id;
    public int problemCnt;
    public ArrayList<String> handle;
    public ArrayList<ArrayList<Integer>> penalty, points;
    public Contest(String fileName) throws Exception {
        JsonObject request = new JsonParser().parse(Utils.readFileAsString(Utils.CONTESTS_DATA_PATH, fileName)).getAsJsonObject();
        this.id = request.get("contest").getAsJsonObject().get("id").getAsInt();
        this.type = request.get("contest").getAsJsonObject().get("type").getAsString();
        handle = new ArrayList<String> ();
        penalty = new ArrayList<ArrayList<Integer>> ();
        points = new ArrayList<ArrayList<Integer>> ();
        JsonArray rows = request.get("rows").getAsJsonArray();
        for(JsonElement row : rows) {
            penalty.add(new ArrayList<Integer>());
            points.add(new ArrayList<Integer>());
            JsonArray problemResults = row.getAsJsonObject().get("problemResults").getAsJsonArray();
            problemCnt = problemResults.size();
            for(JsonElement problem : problemResults) {
                if(type.equals("ICPC")) {
                    try {
                        int timeInMinutes = problem.getAsJsonObject().get("bestSubmissionTimeSeconds").getAsInt() / 60000;
                        int wrongSubmissions = problem.getAsJsonObject().get("rejectedAttemptCount").getAsInt();
                        int totPenalty = wrongSubmissions * 2 + timeInMinutes;
                        penalty.get(penalty.size() - 1).add(totPenalty);
                    } catch (Exception e) {
                        // Problem not solved then add -1 as identifier of not solved
                        penalty.get(penalty.size() - 1).add(-1);
                    }
                } else {
                    try {
                        points.get(points.size() - 1).add(problem.getAsJsonObject().get("points").getAsInt());
                    } catch (Exception e) {
                        // Problem not solved then add -1 as identifier of not solved
                        points.get(points.size() - 1).add(-1);
                    }
                }
            }
        }
    }
}
