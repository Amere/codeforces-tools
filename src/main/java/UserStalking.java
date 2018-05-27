import helpers.Deserializer;

import java.util.ArrayList;
import java.util.Collections;

public class UserStalking {

    ArrayList<User> users;

    public UserStalking(){

    }

    public int getStart(int val){
        /**
         * used to get the start of the intervel to search
         * for users
         */
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
        /**
         * gets all the users in the desired range
         */
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
        /**
         * gets the count of the problems in the desired time range for a user
         */
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
                hi=mid;
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

        /**
         * gets the active users with the desired parameters
         */
        Deserializer des = new Deserializer("/Users/mohamedalattal/Documents/Semester10/","user_ratings");
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
