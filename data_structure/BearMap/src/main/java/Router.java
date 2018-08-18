import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {

    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     *
     * @param g       The graph to use.
     * @param stlon   The longitude of the start location.
     * @param stlat   The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    //public static int ti = 0;
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        long s = g.closest(stlon, stlat); // start node id
        long d = g.closest(destlon, destlat); // destination node id
        Node start = new Node(null, s, g);
        Node destination = new Node(null, d, g);
        start.setDistance(0);
        destination.setDistance(-2);
        //ArrayDeque<Node> a = new ArrayDeque<>();
        //Map<Long, Node> openrecord = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueue<>();
        //ArrayDeque<Long> openid = new ArrayDeque<>();
        //ArrayDeque<Node> close = new ArrayDeque<>();
        ArrayDeque<Long> closeid = new ArrayDeque<>();
        open.add(start);
        //openid.add(start.id);
        //openrecord.put(start.id, start);
        while (!open.isEmpty()) {
            //ti ++;
            Node j = open.remove();
            //openid.remove(j.id);
            //openrecord.remove(j.id);
            //j.used = true;
            //close.add(j);
            if (j.v.flag) {
                continue;
            }
            closeid.add(j.id);
            j.v.flag = true;
            if (j.id.equals(destination.id)) {
                destination.pre = j.pre;
                destination.d = j.d;
                //System.out.println("destination pre is " + j.pre.id + " j is " + j.id);
                break;
            }
            for (Long i : g.adjacent(j.id)) {
                Node neighbor = new Node(j, i, g);
                neighbor.setDistance(0);
                neighbor.setHeristic(destination);
                //openid.contains(neighbor.id)

               /* if (openrecord.keySet().contains(neighbor.id)) {
                    Node temp = openrecord.get(neighbor.id);
                    if (temp.d > neighbor.d) {
                        temp.pre = neighbor.pre;
                        temp.d = neighbor.d;
                        temp.h = neighbor.h;
                        continue;
                    }
                } else */

                if (!neighbor.v.flag) {
                    open.add(neighbor);
                }
            }
        }
        ArrayDeque<Long> revresult = new ArrayDeque<>();
        List<Long> result = new LinkedList<>();
        Node path = destination;
        //System.out.println("the pre id is " + destination.pre.id);
        while (path != null) {
            revresult.add(path.id);
            path = path.pre;
        }
        while (!revresult.isEmpty()) {
            result.add(revresult.removeLast());
        }
        //System.out.println("The result" + result.toString());
        for (Long i : closeid) {
            g.getVertice(i).flag = false;
        }
        return result;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     *
     * @param g     The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        Long a = -1L;
        ArrayDeque<Long> cproute = new ArrayDeque<>();
        Double distance = 0.0;
        ArrayDeque<String> rawrout = new ArrayDeque<>();
        List<NavigationDirection> result = new LinkedList<>();
        Long recordroad = 0L;
        Long previous = 0L;
        String way = "";
        for (Long i : route) {
            cproute.add(i);
        }
        while (!cproute.isEmpty()) {
            if (a.equals(-1L)) {
                a = cproute.removeFirst();
                previous = a;
                GraphDB.Vertice v = g.getVertice(a);
                ArrayDeque<Long> ways = g.vertexinway(v);
                if (ways.size() <= 1) {
                    recordroad = ways.getFirst();
                    GraphDB.Edge w = g.getEdge(recordroad);
                    if (!(w.name.equals("unknown road"))) {
                        way = "Start on " + "unknown road" + " for ";
                        rawrout.add(way);
                    } else {
                        way = "Start on " + w.name + " for ";
                        rawrout.add(way);
                    }
                } else {
                    a = -2L;
                    way = "Start on ";
                }
            } else if (a.equals(-2L)) {
                a = cproute.removeFirst();
                GraphDB.Vertice v = g.getVertice(a);
                ArrayDeque<Long> ways = g.vertexinway(v);
                GraphDB.Edge w = g.getEdge(ways.removeFirst());
                way += w.name + " for ";
                distance += g.distance(previous, a);
                recordroad = w.id;
            } else {
                a = cproute.removeFirst();
                GraphDB.Vertice v = g.getVertice(a);
                ArrayDeque<Long> ways = g.vertexinway(v);
                if (ways.contains(recordroad)) {
                    distance += g.distance(previous, a);
                } else {
                    String val = rawrout.removeFirst() + String.valueOf(distance);
                    result.add(NavigationDirection.fromString(val));
                    Double degree = g.bearing(a, cproute.removeFirst());
                    if (degree < 15 || degree > -15) {
                        way = "Go straight on ";
                    } else if ((degree < 30 || degree > -30) && (degree > 15 || degree < -15)) {
                        if (degree < 0) {
                            way = "Slight left";
                        } else {
                            way = "Slight right";
                        }
                    } else if ((degree > 30 || degree < -30) && (degree < 100 || degree > -100)) {
                        if (degree < 0) {
                            way = "Turn left";
                        } else {
                            way = "Turn right";
                        }
                    } else {
                        if (degree < 0) {
                            way = "Sharp left";
                        } else {
                            way = "Sharp right";
                        }
                    }
                    GraphDB.Edge w = g.getEdge(ways.removeFirst());
                    way += w.name + " for ";
                    distance += g.distance(previous, a);
                    recordroad = w.id;
                }
            }
        }
        return result;
    }

    private static class Node implements Comparable<Node> {
        //boolean used;
        Node pre;
        Long id;
        Double d; // distance from start to this node
        Double h; // heristic
        GraphDB.Vertice v;
        GraphDB graph;

        Node(Node p, long idinput, GraphDB g) {
            //used = false;
            id = idinput;
            v = g.getVertice(id);
            d = -1.0;
            h = -1.0;
            graph = g;
            pre = p;
            setDistance(0);
        }

        private void setDistance(double a) {
            if (pre == null) {
                d = 0.0;
                if (a != 0.0) {
                    d = a;
                }
            } else {
                d = pre.d + graph.distance(pre.id, id);
            }
        }

        private void setHeristic(Node dd) {
            h = graph.distance(this.id, dd.id);
        }

        private Double getPriority(Node a) {
            if (a.d < 0) {
                return -100.0;
            }
            return a.d + a.h;
        }

        @Override
        public int compareTo(Node o) {
            Double key = this.d + this.h;
            Double okey = o.d + o.h;
            if (key > okey) {
                return 1;
            } else if (key.equals(okey)) {
                return 0;
            } else {
                return -1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) {
                return false;
            }
            if (this.compareTo((Node) o) == 0) {

                if (((Node) o).id == this.id) {
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.id);
        }

    }

    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /**
         * Integer constants representing directions.
         */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /**
         * Number of directions supported.
         */
        public static final int NUM_DIRECTIONS = 8;

        /**
         * A mapping of integer values to directions.
         */
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /**
         * Default name for an unknown way.
         */
        public static final String UNKNOWN_ROAD = "unknown road";

        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /**
         * The direction a given NavigationDirection represents.
         */
        int direction;
        /**
         * The name of the way I represent.
         */
        String way;
        /**
         * The distance along this way I represent.
         */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         *
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                        && way.equals(((NavigationDirection) o).way)
                        && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
