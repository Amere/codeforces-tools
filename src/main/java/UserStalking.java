import helpers.Deserializer;
import helpers.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class UserStalking {

    ArrayList<User.UserRatingDataPair> users;

    public UserStalking(){


    }

    public int getStart(int val){
        /**
         * used to get the start of the intervel to search
         * for users
         */
        System.out.println("entered get start");
        int lo = 0;int hi= users.size()-1;
        while(lo<hi){
            int mid = (lo+hi)/2;
            if(val == users.get(mid).currentRating){
                return mid;
            }
            if(val<users.get(mid).currentRating){
                hi = mid-1;
            }else{
                lo=mid+1;
            }
        }
        System.out.println("finish get start");
        return -1;
    }

    public ArrayList<User.UserRatingDataPair> getUsersInRange(int rLo,int rHi){
        /**
         * gets all the users in the desired range
         */

        ArrayList<User.UserRatingDataPair> result = new ArrayList<>();
        System.out.println("entered get range");
        int start = getStart(rLo);
        if(start ==-1)
            return null;
        for (int i = start; i < users.size(); i++) {
            if(users.get(i).currentRating> rHi)
                break;
            result.add(users.get(i));
        }
        System.out.println("ezit get range");
        return result;
    }

    public int getAcceptedProblemsCount(ArrayList<User.AcceptedProblemDataPair> user, long t1, long t2){
        /**
         * gets the count of the problems in the desired time range for a user
         */
        ArrayList<User.AcceptedProblemDataPair> sortedAcceptedProblems = user;
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
                lo = mid+1;
            }
        }
        if(lo>hi || mid ==-1)
            return 0;

        int count = 0;
        for (int i = mid; i < sortedAcceptedProblems.size(); i++) {
            if(sortedAcceptedProblems.get(i).timeCreated> t2)
                break;
            count ++;
        }
        return count;
    }

    ArrayList<String> getActiveUsers(long t1, long t2, int rLo, int rHi, int cnt) throws Exception {

        /**
         * gets the active users with the desired parameters
         */
        Deserializer des = new Deserializer(Utils.RATINGS_PATH, Utils.RATINGS_FILE);
        users = ((ArrayList<User.UserRatingDataPair>)des.readObject());
        ArrayList<User.UserRatingDataPair> users = getUsersInRange(rLo,rHi);
        ArrayList<User.UserActivityDataPair> active = new ArrayList<>();
        ArrayList<String> res = new ArrayList<>();
        ArrayList<String> fileNames = Utils.getFolderNamesInADirectory(Utils.USERS_DATA_PATH);
        for(String name: fileNames){
            System.out.println(Utils.USERS_DATA_PATH+name+"/");
            Deserializer des2 = new Deserializer(Utils.USERS_DATA_PATH+name+"/","sortedAcceptedProblems");
            ArrayList<User.AcceptedProblemDataPair> acceptedProblemDataPairs = ((ArrayList<User.AcceptedProblemDataPair>)des2.readObject());
            int rat = getAcceptedProblemsCount(acceptedProblemDataPairs,t1,t2);
            active.add(new User.UserActivityDataPair(name,rat));
        }
        Collections.sort(active);
        for (int i = 0; i < cnt; i++) {
            res.add(active.get(i).handle);
        }

        return res;
    }

    public static void main(String[] args) throws Exception {
        UserStalking ys  = new UserStalking();
        ArrayList<String> ss = ys.getActiveUsers(2147483647l,2147689647l,1000,1800,10);
        System.out.println(ss);
    }


}
