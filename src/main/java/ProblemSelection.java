import helpers.Deserializer;
import helpers.Utils;
import java.util.*;

public class ProblemSelection {

    private static SegmentTree problemCountSegmentTree;
    private static HashMap<String, HashSet<String>> problemToTags;
    static ArrayList<Integer>[] adjList;
    private static HashMap<Integer, ArrayList<Integer>> deadlocks;

    public static void main(String[] args) throws Exception {

        String[] users = new String[]{"lauer", "SaraGuru", "ssor96","stacy992"
                ,"Nekrolm","ConnorZhong","HulkHoggan","Lollipop","mahdinafar",
                "Sojal","OpalDshawn","escepta","nic11","Erilyth"};

        selectProblems(users,null,200,500, 10, 10);
    }

    /*
        Uses the helpers to load data, build and solve the graph
        Then gets a set of deadlocks to insert later to the solution
        Does topological sorting on the graph and returns the result inserting the result of deadlocks
     */
    public static ArrayList<String> selectProblems(String[] handles, String tag, int minSolved,
                                               int maxSolved, int p, int cnt) throws Exception {
        prepareProblem();
        ArrayList<String> filtered = filterProblemsByTag(minSolved, maxSolved, tag);
        int[][] links = loadUsersProblems(handles,filtered);
        buildGraph(links,p, handles.length);
        ArrayList<String> problems = new ArrayList<>();
        Stack<Integer> sorted = topologicalSort();
        while (!sorted.isEmpty() && cnt > 0){
            int index = sorted.pop();
            problems.add(filtered.get(index));
            if(deadlocks.containsKey(index)){
                Collections.sort(deadlocks.get(index));
                for(int i = deadlocks.size() - 1; i >= 0; i--)
                    problems.add(filtered.get(deadlocks.get(index).get(i)));
                cnt -= deadlocks.get(index).size();
            }
            cnt--;
        }
        System.out.println("Result" + Arrays.toString(problems.toArray()));
        return problems;
    }

    /*
        Constructs a graph of problems with links to each other
        uses TarjanSCC to find linked Components
        Replaces These Components to a single node getting the deadlock sets
    */
    public static void buildGraph(int[][] links, int p, int totalUserSize){
        int minCount = (int) Math.floor((p*1.0/100)*totalUserSize);
        sz = links.length;
        adjList = new ArrayList[sz];
        for (int i = 0; i < adjList.length; i++)
            adjList[i] = new ArrayList<>();

        for(int i = 0; i < links.length; i++){
            for(int j = 0; j < links[i].length; j++){
                if(links[i][j] >= minCount)
                    adjList[i].add(j);
            }
        }
        tarjanSCC();
        //remove deadlocks and replace them with a single node
        deadlocks = new HashMap<>();
        for(int i = 0; i < SCCIndex.length; i++){
            for(int j = i+1; j < SCCIndex.length; j++){
                if(SCCIndex[i] == SCCIndex[j]){
                    if(deadlocks.containsKey(i))
                        deadlocks.get(i).add(j);
                    else {
                        ArrayList<Integer> set = new ArrayList<>();
                        set.add(j);
                        deadlocks.put(i, set);
                    }
                    while (!adjList[j].isEmpty()){
                        int edge = adjList[j].remove(0);
                        if(!adjList[i].contains(edge))
                            adjList[i].add(edge);
                    }
                }
            }
        }
    }

    //Leaves only problems with required tags
    public static ArrayList<String> filterProblemsByTag(int min, int max, String tag){
        String[] ids = problemCountSegmentTree.rangeSolved(min, max);
        System.out.println(Arrays.toString(ids));
        if(tag == null)
            return new ArrayList<>(Arrays.asList(ids));
        ArrayList<String> filtered = new ArrayList<>();
        for(String s : ids)
            if (problemToTags.get(s).contains(tag))
                filtered.add(s);
        return filtered;
    }

    //Loads each user with the handle and starts to count if problem x is solved before y and vice verse
    public static int[][] loadUsersProblems(String[] handles, ArrayList<String> problems) throws Exception {
        int[][] links = new int[problems.size()][problems.size()];
        for(int[] l : links)
            Arrays.fill(l, 0);
        for(String handle : handles){
            Deserializer deser = new Deserializer(Utils.USERS_DATA_PATH + handle + "/", "sortedAcceptedProblems");
            ArrayList<User.AcceptedProblemDataPair> accepted = (ArrayList<User.AcceptedProblemDataPair>) deser.readObject();
            for(int i = 0; i < accepted.size(); i++){
                int problemBeforeIndex = problems.indexOf(accepted.get(i).problemName);
                if(problemBeforeIndex == -1) {
                    continue;
                }
                for(int j = i+1; j < accepted.size(); j++){
                    int problemAfterIndex = problems.indexOf(accepted.get(j).problemName);
                    if(problemAfterIndex == -1)
                        continue;
                    links[problemBeforeIndex][problemAfterIndex]++;
                }
            }
        }
        return links;
    }

    //Reads data from serialized objects and feeds it to the segment tree
    public static void prepareProblem() throws Exception {
        Deserializer des = new Deserializer(Utils.PROBLEMS_SERIALIZED_HASH_COUNT, Utils.PROBLEMS_SERIALIZED_HASH_COUNT_FILE);
        HashMap<String, Integer> problemToCount = (HashMap<String, Integer>) des.readObject();
        des = new Deserializer(Utils.PROBLEMS_SERIALIZED_HASH_TAG, Utils.PROBLEMS_SERIALIZED_HASH_TAG_FILE);
        problemToTags =  (HashMap<String, HashSet<String>>) des.readObject();
        problemCountSegmentTree = new SegmentTree(90000);
        Iterator it = problemToCount.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String) pair.getKey();
            int value = (int) pair.getValue();
            it.remove();
            problemCountSegmentTree.set(value, new Node(new String[]{name},value));
        }
    }

    static int sz;
    public static void tarjanSCC(){
        tin = new int[sz];
        tlow = new int[sz];
        SCCIndex = new int[sz];
        Root = new int[sz];
        Arrays.fill(SCCIndex, -1);
        stack = new Stack<>();

        for(int i = 0; i < sz; ++i)
            if(tin[i] == 0)
                dfs(i);
    }

    static Stack<Integer> stack;
    static int[] tin, tlow;
    static int[] SCCIndex;
    static int timer, SCCIndexer;
    static int[] Root;

    static void dfs(int u) {
        tin[u] = tlow[u] = ++timer;
        stack.push(u);
        for(int v: adjList[u]) {
            if(tin[v] == 0)
                dfs(v);
            if(SCCIndex[v] == -1)
                tlow[u] = Math.min(tlow[u], tlow[v]);
        }
        if(tin[u] == tlow[u]) {
            Root[SCCIndexer] = u;
            while(true) {
                int v = stack.pop();
                SCCIndex[v] = SCCIndexer;
                if(v == u)
                    break;
            }
            SCCIndexer++;
        }
    }

    public static void topologicalSortUtil(int v, boolean visited[], Stack stack){
        visited[v] = true;
        Integer i;
        Iterator<Integer> it = adjList[v].iterator();
        while (it.hasNext())
        {
            i = it.next();
            if (!visited[i])
                topologicalSortUtil(i, visited, stack);
        }
        stack.push(new Integer(v));
    }

    public static Stack<Integer> topologicalSort(){
        Stack stack = new Stack();
        boolean visited[] = new boolean[sz];
        for (int i = 0; i < sz; i++)
            visited[i] = false;

        for (int i = 0; i < sz; i++)
            if (visited[i] == false)
                topologicalSortUtil(i, visited, stack);

        return stack;
    }

    static class SegmentTree {
        Node tree[];
        int N;

        SegmentTree(int n) {
            N = 1;
            while (N < n)
                N <<= 1;
            tree = new Node[N << 1];
            Arrays.fill(tree, new Node(new String[]{},0));
        }

        void set(int l, Node val){
            set(1,1,N,l, val);
        }

        void set(int node, int b, int e, int l, Node val) {
            if(l < b || e < l)
                return;
            if(e == b){
                Node left = tree[node];
                Node right = val;
                String[] ids = new String[left.id.length + right.id.length];
                System.arraycopy(left.id, 0, ids, 0, left.id.length);
                System.arraycopy(right.id, 0, ids, left.id.length, right.id.length);
                tree[node] = new Node(ids,Math.max(tree[node].max, val.max));
                return;
            }
            int mid = (b+e >> 1);
            set(node << 1, b, mid, l, val);
            set(node << 1 | 1, mid+1, e, l, val);

            Node left = tree[node<<1];
            Node right = tree[node<<1 | 1];
            String[] ids = new String[left.id.length + right.id.length];
            System.arraycopy(left.id, 0, ids, 0, left.id.length);
            System.arraycopy(right.id, 0, ids, left.id.length, right.id.length);
            tree[node] = new Node(ids,Math.max(tree[node<<1].max, tree[node<<1 | 1].max));
        }


        String[] rangeSolved(int l, int r){
            return max(1,1,N,l,r).id;
        }

        Node max(int node, int b, int e, int l, int r) {
            if(r < b || e < l)
                return new Node(new String[]{},0);
            if(l <= b && e <=r)
                return tree[node];
            int mid = (b+e >> 1);
            Node left = max(node << 1, b, mid, l, r);
            Node right = max(node << 1 | 1, mid+1, e, l, r);
            String[] ids = new String[left.id.length + right.id.length];
            System.arraycopy(left.id, 0, ids, 0, left.id.length);
            System.arraycopy(right.id, 0, ids, left.id.length, right.id.length);
            return new Node(ids,Math.max(tree[node<<1].max, tree[node<<1 | 1].max));
        }
    }

    static class Node {
        String[] id;
        int max;

        public Node(String[] id, int max) {
            this.id = id;
            this.max = max;
        }
    }
}
