import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
//import java.util.

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /**
     * Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc.
     */
    private final Map<Long, Vertice> vertices = new HashMap<>();
    private final Map<Long, Edge> edges = new HashMap<>();
    private final HashSet<Long> idlist = new HashSet<>();
    private final TriesNode tries = new TriesNode();
    //private final ArrayList<Vertice> verticeslist = new ArrayList<>();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }


    static String cleanstringAlter (String s) {
        String temp = s.toLowerCase();
        String tr = "";
        for (int i = 0; i < temp.length(); i++) {
            char c = temp.charAt(i);
            if (c >= 'a' && c <= 'z' || c == ' ') {
                tr += c;
            }
        }
        return tr;
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        HashSet<Long> a = (HashSet<Long>) idlist.clone();
        for (Long i : a) {
            if (vertices.get(i).adjacentid.size() == 0) {
                removeNode(i);
            }
        }
    }

    public void insertTriesNode (Vertice v) {
        Map<String, Object> loc = new HashMap<>();
        loc.put("lon", v.lond);
        loc.put("lat", v.latd);
        loc.put("name", v.name);
        loc.put("id", v.id);
        tries.insert(tries, v.name, loc);
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     *
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> result = new ArrayList<Long>();
        HashSet<Long> a = (HashSet<Long>) idlist.clone();
        for (Long id : a) {
            result.add(id);
        }
        return result;
    }

    /**
     * Returns ids of all vertices adjacent to v.
     *
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        Vertice vertex = vertices.get(v);
        return vertex.adjacentid;
    }

    public void addNode(Long id, Vertice v) {
        vertices.put(id, v);
        idlist.add(id);
    }

    public void addEdge(Long id, Edge e) {
        edges.put(id, e);
    }

    public Vertice getVertice(Long id) {
        return vertices.get(id);
    }

    public Edge getEdge(Long id) {
        return edges.get(id);
    }

    public void removeNode(Long id) {
        vertices.remove(id);
        idlist.remove(id);
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     *
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     *
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        Long result = -1L;
        Double mindistance = -1.0;
        for (Long i : idlist) {
            if (mindistance < 0) {
                Vertice v = vertices.get(i);
                mindistance = distance(lon, lat, v.lond, v.latd);
                //3963 * c;
                result = i;
            } else {
                Double dis;
                Vertice v = vertices.get(i);
                dis = distance(lon, lat, v.lond, v.latd);
                if (dis < mindistance) {
                    mindistance = dis;
                    result = i;
                }
            }
        }
        return result;
    }

    /**
     * Gets the longitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        Vertice vertex = vertices.get(v);
        return vertex.lond;
    }

    /**
     * Gets the latitude of a vertex.
     *
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        Vertice vertex = vertices.get(v);
        return vertex.latd;
    }

    ArrayDeque<Long> vertexinway(Vertice v) {
        return v.way;
    }

    static class Vertice {
        String lat;
        String lon;
        Double latd;
        Double lond;
        String name;
        Long id;
        boolean flag;
        ArrayDeque<Long> way;
        //ArrayDeque<Vertice> adjacentnodes;
        HashSet<Long> adjacentid;

        Vertice(String inputid, String inputlat, String inputlon) {
            id = Long.valueOf(inputid);
            lat = inputlat;
            lon = inputlon;
            //tag = new HashMap<>();
            flag = false;
            lond = Double.valueOf(lon);
            latd = Double.valueOf(lat);
            adjacentid = new HashSet<>();
            //adjacentnodes = new ArrayDeque<>();
            way = new ArrayDeque<>();

        }

    }

    static class Edge {
        boolean flag = true;
        String ids;
        Long id;
        //Double dist;
        String name;
        ArrayDeque<Long> waynodes;
        //HashMap<String, String> tag;
        String maxspeed;
        Edge(String inputid) {
            waynodes = new ArrayDeque<>();
            //tag = new HashMap<>();
            ids = inputid;
            id = Long.valueOf(inputid);
            name  = "unknown road";
        }

        void addnodes(Long vid) {
            waynodes.add(vid);
        }

    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     *
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        //System.out.println("Prefix " + prefix);
        List<String> result = tries.prefixname(tries, prefix);
        //result = new LinkedList<>();
        //result.add("bohan");

        return result;
    }

    /*public List<String> getLocationsByPrefix(String prefix) {
        List<String> result = new LinkedList<>();
        // get all location values from tries if the prefix matches
        List<Map<String, Object>> locationValues = locations.getPrefix(prefix);
        for (int i = 0; i < locationValues.size(); i++) {
            Map<String, Object> value = locationValues.get(i);
            result.add(String.valueOf(value.get("name")));
            System.out.println(value.get("name"));
        }

        return result;
    }*/

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     *
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" : Number, The latitude of the node. <br>
     * "lon" : Number, The longitude of the node. <br>
     * "name" : String, The actual name of the node. <br>
     * "id" : Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        return tries.find(locationName);
    }





}
