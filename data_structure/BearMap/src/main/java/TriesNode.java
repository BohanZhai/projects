import java.util.*;

public class TriesNode {
    String character;
    List<Map<String, Object>> location;
    Map<String,TriesNode> children;
    HashSet<String> orn;

    public TriesNode() {
        //character = "";
        children = new HashMap<>();
        location = new LinkedList<>();
        orn = new HashSet<>();
    }
    public TriesNode(String a) {
        character = a;
        children = new HashMap<>();
        location = new LinkedList<>();
        orn = new HashSet<>();
    }
    public void insert (TriesNode root, String name,
                        Map<String, Object> value) {
        TriesNode node = root;
        int i = 0;
        String cleanname = new String(name);
        cleanname = GraphDB.cleanString(cleanname);
        while (i < cleanname.length()) {
            //String test = GraphDB.cleanstringAlter(String.valueOf(name.charAt(i)));
            /*if (test == "") {
                System.out.println("I met a space");
            }*/
            String t = String.valueOf(cleanname.charAt(i));
            if (node.children.containsKey(t)) {
                node = node.children.get(t);
                i++;
                //System.out.println("a");
            }
            else {
                break;
            }
        }
        while (i < cleanname.length()) {
            //System.out.println("d");
            String a = String.valueOf(cleanname.charAt(i));
            node.children.put(a
                    , new TriesNode(a));
            //System.out.println("b");
            node = node.children.get(a);
            i++;
            //System.out.println("c");
        }
        node.location.add(value);
        node.orn.add(name);
    }

    public List<Map<String, Object>> find (String key) {
        TriesNode node = this;
        String clk = GraphDB.cleanString(key);
        for (int i = 0; i < clk.length(); i++) {
            String a = GraphDB.cleanString(String.valueOf(clk.charAt(i)));
            //System.out.println(GraphDB.cleanString(a));
            if (node.children.containsKey(a)) {
                node = node.children.get(a);
            } else {
                //System.out.println("n");
                return null;
            }
        }
        return node.location;
    }


    public List<String> prefixname (TriesNode n, String pre) {
        String cleanpre;
        //System.out.println(pre);
        cleanpre = GraphDB.cleanString(pre);
        TriesNode node = n;
        for (int i = 0; i < cleanpre.length(); i++) {
            String b = GraphDB.cleanString(String.valueOf(cleanpre.charAt(i)));
            if (node.children.containsKey(b)) {
                node = node.children.get(b);
            }
            else {
                return null;
            }

        }
        List<String> result;
        result = prefixhelper(node);
        return result;
    }

    private List<String> prefixhelper (TriesNode n) {
        List<String> result = new LinkedList<>();
        if (n.children.isEmpty()) {
            for (String t : n.orn)
            result.add(t);
            return result;
        }
        Set<String> a = n.children.keySet();
        for (String i : a) {
            if (!n.orn.isEmpty()) {
                for (String t : n.orn)
                result.add(t);
            }
            List<String> mid = prefixhelper(n.children.get(i));
            for (String j : mid)
            result.add(j);
        }
        return result;
    }
}
