//import com.sun.org.apache.xpath.internal.operations.String;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    /*
    each degree of longitude is SL = 288,200 feet.
     */
    private final int S_L = 288200;

    public Rasterer() {
        // YOUR CODE HERE
        super();
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        //System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        /*System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
                           + "your browser.");*/
        double ullon = params.get("ullon");
        if (ullon < MapServer.ROOT_ULLON) {
            ullon = MapServer.ROOT_ULLON;

        }
        double lrlon = params.get("lrlon");
        if (lrlon > MapServer.ROOT_LRLON) {
            lrlon = MapServer.ROOT_LRLON;
        }
        double ullat = params.get("ullat");
        if (ullat > MapServer.ROOT_ULLAT) {
            ullat = MapServer.ROOT_ULLAT;
        }
        double lrlat = params.get("lrlat");
        if (lrlat < MapServer.ROOT_LRLAT) {
            lrlat = MapServer.ROOT_LRLAT;
        }
        //browser width
        double w = params.get("w");
        //browser height
        double h = params.get("h");
        //depth i
        int i = getLeastdi(lonDPP(ullon, lrlon, w));
        //get up left point and low right point image numbers
        int[][] upleftandlowrigh = ulandlr(ullon, lrlon, ullat, lrlat, w, h, i);
        int totalnum = Math.abs(upleftandlowrigh[0][0] - upleftandlowrigh[0][1] + 1)
                * Math.abs(upleftandlowrigh[1][0] - upleftandlowrigh[1][1] + 1);
        Double rasterullon = getlonofupleft(i, upleftandlowrigh[0][0]);
        Integer depth = i;
        Double rasterlrlon = getlonoflowright(i, upleftandlowrigh[1][0]);
        Double rasterlrat = getlatoflowright(i, upleftandlowrigh[1][1]);
        Double rasterullat = getlatofupleft(i, upleftandlowrigh[0][1]);
        boolean querysuccess = (upleftandlowrigh[0][0] != -1);
        int n = (int) Math.abs(upleftandlowrigh[0][0] - upleftandlowrigh[1][0]) + 1;
        int height = (int) Math.abs(upleftandlowrigh[0][1] - upleftandlowrigh[1][1]) + 1;
        java.lang.String[][] rendergrid = new java.lang.String[height][n];
        java.lang.String stringi = Integer.toString(i);
        for (int j = 0; j < height; j++) {
            for (int m = 0; m < n; m++) {
                java.lang.String name = "d";
                java.lang.String x = "" + (upleftandlowrigh[0][0] + m);
                java.lang.String y = "" + (upleftandlowrigh[0][1] + j);
                name = name + i + "_x" + x + "_y" + y + ".png";
                rendergrid[j][m] = name;
            }
        }
        results.put("raster_ul_lon", rasterullon);
        results.put("depth", depth);
        if (rasterlrlon > MapServer.ROOT_LRLON) {
            rasterlrlon = MapServer.ROOT_LRLON;
        }
        results.put("raster_lr_lon", rasterlrlon);
        results.put("raster_lr_lat", rasterlrat);
        results.put("raster_ul_lat", rasterullat);
        results.put("query_success", querysuccess);
        results.put("render_grid", rendergrid);
        return results;
    }

    private double getlonofupleft(int i, int x) {
        Double dis = Math.abs(MapServer.ROOT_LRLON - MapServer.ROOT_ULLON);
        return MapServer.ROOT_ULLON + dis / Math.pow(2, i) * x;
    }

    private double getlonoflowright(int i, int x) {
        Double dis = Math.abs(MapServer.ROOT_LRLON - MapServer.ROOT_ULLON);
        return MapServer.ROOT_ULLON + dis / Math.pow(2, i) * (x + 1);
    }

    private double getlatofupleft(int i, int y) {
        Double dis = Math.abs(MapServer.ROOT_LRLAT - MapServer.ROOT_ULLAT);
        return MapServer.ROOT_ULLAT - dis / Math.pow(2, i) * y;
    }

    private double getlatoflowright(int i, int x) {
        Double dis = Math.abs(MapServer.ROOT_LRLAT - MapServer.ROOT_ULLAT);
        return MapServer.ROOT_ULLAT - dis / Math.pow(2, i) * (x + 1);
    }


    /*
    Return the LonDPP after input ullon lrlon and width.
     */
    private Double lonDPP(double ullon, double lrlon, double w) {
        return ((lrlon - ullon) / w) * S_L;
    }

    /*
    Return the LonDPP of di'th tile
     */
    private Double lonDPPimage(int i) {
        int p = (int) Math.pow(2, i);
        /*
         longdif is the different between
         root ullon and lrlon can be seen as distance
        */
        Double londif = Math.abs(MapServer.ROOT_ULLON - MapServer.ROOT_LRLON);
        return lonDPP(MapServer.ROOT_ULLON,
                MapServer.ROOT_ULLON + londif / Math.pow(2, i), MapServer.TILE_SIZE);
    }

    private int getLeastdi(Double lonpp) {
        int i = 0;
        Double a = lonDPPimage(0);
        while (i < 7) {
            if (a <= lonpp) {
                return i;
            }
            a = a / 2;
            i++;
        }
        return i;
    }

    /*private boolean is_query_sucess (int i, ) {

        return
    }*/

    private int[][] ulandlr(double ullon, double lrlon, double ullat, double lrlat,
                            double w, double h, int i) {
        int[][] result = new int[2][2];
        /*if (ullon < MapServer.ROOT_ULLON || lrlon > MapServer.ROOT_LRLON) {
            result[0][0] = -1;
            return result;
        }
        if (ullat > MapServer.ROOT_ULLAT || lrlat < MapServer.ROOT_LRLAT) {
            result[0][0] = -1;
            return result;
        }*/

        // this is width of an image in depth i
        Double londif = Math.abs(MapServer.ROOT_ULLON - MapServer.ROOT_LRLON) / Math.pow(2, i);

        Double latdif = Math.abs(MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / Math.pow(2, i);

        double uleft = ullon - MapServer.ROOT_ULLON;
        double lleft = lrlon - MapServer.ROOT_ULLON;
        int upleftn = (int) (uleft / londif);
        int lowleftn = (int) (lleft / londif);
        double uup = MapServer.ROOT_ULLAT - ullat;
        double lup = MapServer.ROOT_ULLAT - lrlat;
        int upupn = (int) (uup / latdif);
        int lowupn = (int) (lup / latdif);
        if (lrlon >= MapServer.ROOT_LRLON) {
            lowleftn--;
        }
        if (lrlat <= MapServer.ROOT_LRLAT) {
            lowupn--;
        }
        result[0][0] = upleftn;
        result[1][0] = lowleftn;
        result[0][1] = upupn;
        result[1][1] = lowupn;
        return result;
    }
}
