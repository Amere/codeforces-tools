import helpers.Deserializer;
import helpers.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class UserStalking {

    ArrayList<User> users;

    public UserStalking(){

    }

    public int getStart(int val){
        int lo = 0;int hi= users.size()-1;
        while(lo<hi){
            int mid = (lo+hi)/2;
            if(val == users.get(mid).currentRating){
                return mid;
            }
            if(val<users.get(mid).currentRating){
                hi = mid-1;
            }else{
                hi=mid;
            }
        }

        return -1;
    }

    public ArrayList<User> getUsersInRange(int rLo,int rHi){
        ArrayList<User> result = new ArrayList<>();

        int start = getStart(rLo);
        for (int i = start; i < users.size(); i++) {
            if(users.get(i).currentRating> rHi)
                break;
            result.add(users.get(i));
        }

        return result;
    }

    public int getAcceptedProblemsCount(User user,int t1,int t2){
        ArrayList<User.AcceptedProblemDataPair> sortedAcceptedProblems = user.sortedAcceptedProblems;
        int lo = 0;int hi= users.size()-1;
        int mid = -1;
        while(lo<hi){
            mid = (lo+hi)/2;
            if(t1 == users.get(mid).currentRating){
                break;
            }
            if(t1<users.get(mid).currentRating){
                hi = mid-1;
            }else{
                hi = mid;
            }
        }
        if(lo>hi)
            return 0;

        int count = 0;
        for (int i = mid; i < sortedAcceptedProblems.size(); i++) {
            if(sortedAcceptedProblems.get(i).timeCreated> t2)
                break;
            count ++;
        }
        return count;
    }

    ArrayList<String> getActiveUsers(int t1, int t2, int rLo, int rHi, int cnt) throws Exception {

        Deserializer des = new Deserializer(Utils.RATINGS_PATH, Utils.RATINGS_FILE);
        //ArrayList<User.UserRatingDataPair> ratings = des.readObject();
        ArrayList<User> users = getUsersInRange(rLo,rHi);
        ArrayList<User.UserActivityDataPair> active = new ArrayList<>();
        ArrayList<String> res = new ArrayList<>();
        for(User user : users){
            int rat = getAcceptedProblemsCount(user,t1,t2);
            active.add(new User.UserActivityDataPair(user.handle,rat));
        }
        Collections.sort(active);
        for (int i = 0; i < cnt; i++) {
            res.add(active.get(i).handle);
        }

        return res;
    }


}
