import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helpers.Utils;

import java.io.IOException;
import java.util.ArrayList;


public class Main {




    public static void main(String[] args) throws Exception {


        Solver solver = new Solver();
        solver.prepareProblems();
//        solver.prepareContests();
//        solver.prepareUsers();

//        solver.evaluateConstestPerformance("Amerisma", false);


        String[] users = new String[]{"lauer", "SaraGuru", "ssor96","stacy992"
                ,"Nekrolm","ConnorZhong","HulkHoggan","Lollipop","mahdinafar",
                "Sojal","OpalDshawn","escepta","nic11","Erilyth"};

        ProblemSelection.selectProblems(users,null,200,500, 10, 10);
    }
}
