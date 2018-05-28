import helpers.Deserializer;
import helpers.Utils;

import java.util.*;

public class ProblemSelection {

    private static SegmentTree problemCountSegmentTree;
    private static HashMap<String, HashSet<String>> problemToTags;

    public static void main(String[] args) throws Exception {
        prepareProblems();
    }

    public static HashSet<String> filterProblems(int min, int max, String tag){
        String[] ids = problemCountSegmentTree.rangeSolved(min, max);
        HashSet<String> filteredIds = new HashSet<>(Arrays.asList(ids));
        if(tag == null)
            return filteredIds;
        for(String s : ids)
            if(!problemToTags.get(s).contains(tag))
                filteredIds.remove(s);
        return filteredIds;
    }

    public static void prepareProblems() throws Exception {
        Deserializer des = new Deserializer(Utils.PROBLEMS_SERIALIZED_HASH_COUNT, Utils.PROBLEMS_SERIALIZED_HASH_COUNT_FILE);
        HashMap<String, Integer> problemToCount = (HashMap<String, Integer>) des.readObject();
        des = new Deserializer(Utils.PROBLEMS_SERIALIZED_HASH_TAG, Utils.PROBLEMS_SERIALIZED_HASH_TAG_FILE);
        problemToTags =  (HashMap<String, HashSet<String>>) des.readObject();
        problemCountSegmentTree = new SegmentTree(90000);
        Iterator it = problemToCount.entrySet().iterator();
        System.out.println(problemToCount.size());
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String) pair.getKey();
            int value = (int) pair.getValue();
            it.remove();
            problemCountSegmentTree.set(value, new Node(new String[]{name},value));
        }
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
