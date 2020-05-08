import java.awt.Point;

/* 
 * PointData class. This class holds the x and y values of the clicked point
 * along with the rgb values at that point.
 */
public class PointData {
    private Point point;
    private int r;
    private int g;
    private int b;
    private Logger logger = Logger.getInstance();

    /*
     * PointData structure. Set the point and rgb values.
     */
    public PointData(Point point, int r, int g, int b) {
        this.point = point;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /*
     * A wrapper for the Point class move() function. Moves the point and gets the new rgb value.
     */
    public void move(int x, int y, int r, int g, int b) {
        point.move(x, y);
        this.r = r;
        this.g = g;
        this.b = b;
        logger.log("Point moved to(" + point.getX() + ", " + point.getY() + ") and rgb value changed to(" + r + ", " + g + ", " + b + ")");
    }

    /*
     * Get the x value of the point. A wrapper for the Point class getX() function.
     */
    public double getX() {
        return point.getX();
    }

    /*
     * Get the y value of the point. A wrapper for the Point class getY() function.
     */
    public double getY() {
        return point.getY();
    }

    /*
     * Get r value.
     */
    public int getR() {
        return r;
    }

    /*
     * Get g value.
     */
    public int getG() {
        return g;
    }

    /*
     * Get b value.
     */
    public int getB() {
        return b;
    }
}