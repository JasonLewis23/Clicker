import java.util.Vector;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Point;

/*
 * Data class. This object holds the image, the point data on that image,
 * and other data pertaining to the images. The max amount of these objects
 * that can exist at one time is equal to the write_file variable from the
 * ClickFrame class.
 */
public class Data {
    private Image image;
    private BufferedImage image_normal;
    private File file;
    private String name;
    private Vector <PointData> points;
    private boolean clicked;

    private double ratio_x;
    private double ratio_y;
    private Logger logger;

    /*
     * Constructor for Data class. Get ratio for the scaled image and set up 
     * data variables for the image, including the name of the image without
     * the extension.
     */
    public Data(File file, Image image, BufferedImage image_normal) {
        logger = Logger.getInstance();
        ratio_x =(double) image_normal.getWidth() / 640.0;
        ratio_y =(double) image_normal.getHeight() / 480.0;
        logger.log("Image scale ratio found: (" + ratio_x + ", " + ratio_y + ")");

        this.image_normal = image_normal;
        this.file = file;
        String temp = file.getName();
        name = temp.substring(0, temp.lastIndexOf('.'));
        this.image = image;

        points = new Vector <PointData>();
        clicked = false;
        logger.log("Data object created.");
    }

    /*
     * Get the name of the image file without the extension.
     */
    public String getName() {
        return name;
    }

    /*
     * Get the file object for the image.
     */
    public File getFile() {
        return file;
    }

    /*
     * Get the image object for the image file.
     */
    public Image getImage() {
        return image;
    }

    /*
     * Get the points for the image.
     */ 
    public Vector <PointData> getPoints() {
        return points;
    }

    /*
     * Add a point along with its rgb value for the image.
     */
    public void setPoint(Point p) {
        int temp_x =(int)(ratio_x * p.getX());
        int temp_y =(int)(ratio_y * p.getY());

        int rgb = image_normal.getRGB(temp_x, temp_y);
        int r =(rgb >> 16) & 0xff;
        int g =(rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        PointData pd = new PointData(p, r, g, b);
        points.add(pd);

        logger.log("Point added(" + p.getX() + ", " + p.getY() + ") and rgb values(" + r + ", " + g + ", " + b + ")");

    }

    /*
     * Clear all points from the current image.
     */ 
    public void clearPoints() {
        logger.log("Clearing points...");
        for(int x = points.size() - 1; x >= 0; x--) {
            points.removeElementAt(x);
        }
        logger.log("Points cleared.");
    }

    /*
     * Set this image as clicked.
     */
    public void setClicked() {
        clicked = true;
    }

    /*
     * Set this image as skipped.
     */
    public void setSkipped() {
        clicked = false;
    }

    /*
     * Remove the last point that was clicked.
     */
    public void removeLastPoint() {
        if(points.size() > 0) {
            logger.log("Point removed:(" + points.get(points.size() - 1).getX() + ", " + points.get(points.size() - 1) + ")");
            points.removeElementAt(points.size() - 1);
        }
    }

    /*
     * Get the value of the image whether it is clicked or not.
     */
    public boolean getClicked() {
        return clicked;
    }
 
    /*
     * Move the current point by the offset in the y plane. Get the new rgb
     * values of the new point.
     */
    public void moveCurrentPointY(int offset) {
        if(points.size() > 0) {
            logger.log("Moving current point...");
            int x =(int)(points.get(points.size() - 1).getX());
            int y =(int)(points.get(points.size() - 1).getY());
            if(y + offset > 0 && y + offset <= 480) {
                int rgb = image_normal.getRGB((int)(x * ratio_x),(int)(y * ratio_y));
                int r =(rgb >> 16) & 0xff;
                int g =(rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                points.get(points.size() - 1).move(x, y + offset, r, g, b);
            }
        }
    }

    /*
     * Move the current point by the offset in the x plane. Get the new rgb
     * values of the new point.
     */
    public void moveCurrentPointX(int offset) {
        if(points.size() > 0) {
            logger.log("Moving current point...");
            int x =(int)(points.get(points.size() - 1).getX());
            int y =(int)(points.get(points.size() - 1).getY());
            if(x + offset > 0 && x + offset <= 640) {
                int rgb = image_normal.getRGB((int)(x * ratio_x),(int)(y * ratio_y));
                int r =(rgb >> 16) & 0xff;
                int g =(rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                points.get(points.size() - 1).move(x + offset, y, r, g, b);
            }
        }
    }

    /*
     * Get the x ratio of the real image to the scaled image.
     */
    public double getRatioX() {
        return ratio_x;
    }

    /*
     * Get the y ratio of the real image to the scaled image.
     */
    public double getRatioY() {
        return ratio_y;
    }
}