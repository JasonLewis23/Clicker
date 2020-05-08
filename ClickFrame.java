import javax.swing.JFrame;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Vector;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;

/*
 * ClickFrame class. This class is the application window. It handles all
 * of the key presses, mouse clicks, drawing, and high level data lists.
 */
public class ClickFrame extends JFrame implements KeyListener, MouseListener {

    private String [] extensions = {".png", ".jpg", ".jpeg", ".gif", ".ppm"};
    private int pixels;
    private Logger logger;
    
    private int counter;
    private int total_images;
    private Vector <File> image_files;
    
    private int index = 0;
    private int index_max;
    private Data [] data;

    private int write_file;
    private String output_path;
    private String image_path;
    private boolean verbose;
    private boolean rgb;
    private boolean log;


    /*
     * Frame constructor. Sets up the application.
     */
    public ClickFrame(String output_path, String image_path, boolean verbose, boolean rgb, boolean log) {
        super();
        logger = Logger.getInstance();
        logger.log("Setting up...");

        this.verbose = verbose;
        this.rgb = rgb;
        this.log = log;
        write_file = 5;

        commonSetup(output_path, image_path);
    }

    /*
     * Frame constructor with write_file arg. Sets up the application.
     */
    public ClickFrame(String output_path, String image_path, int write_file, boolean verbose, boolean rgb, boolean log) {
        super();
        logger = Logger.getInstance();
        logger.log("Setting up...");

        this.write_file = write_file;
        this.verbose = verbose;
        this.rgb = rgb;
        this.log = log;

        commonSetup(output_path, image_path);
    }

    /*
     * Common setup that both constructors share. The setup includes getting all of the image files, 
     * reading the config file, creating any necessary new files, initializing the images and data,
     * and setting up the application window.
     */
    private void commonSetup(String output_path, String image_path) {
        image_files = new Vector <File>();
        counter = -1;
        total_images = -1;
        pixels = 1;

        logger.log("Getting images...");
        findImages(image_path);
        logger.log("Reading config file...");
        readConfig(output_path);
        logger.log("Creating necessary files...");
        createNewFiles(output_path);
        logger.log("Setting up initial images...");
        initialize();

        this.output_path = output_path;
        this.image_path = image_path;

        addKeyListener(this);
        //addMouseListener(this);

        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(data[index].getImage(), 0, 0, null);
                g.setColor(Color.GREEN);
                Vector <PointData> points = data[index].getPoints();
                for(int x = 0; x < points.size(); x++) {
                    g.fillArc((int)(points.get(x).getX()) - 2,(int)(points.get(x).getY()) - 2, 4, 4, 0, 360);
                }
            }
        };
        p.setPreferredSize( new Dimension(640,480));
		p.addMouseListener(this);
        add(p);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);

        setVisible(true);
        logger.log("Application ready!");
    }

    /*
     * Get all images that are located in the image path.
     */
    private void findImages(String image_path) {
        File folder = new File(image_path);
        File [] files = folder.listFiles();

        for(File f : files) {
            for(String ext : extensions) {
                if(f.getName().toLowerCase().endsWith(ext)) {
                    if(f.isFile()) {
                        logger.log(f.getName() + " added");
                        image_files.add(f);
                        break;
                    }
                }
            }
        }

        if(image_files.size() == 0) {
            System.out.println("14: No images exist in given folder");
            System.exit(14);
        }

        logger.log("All files found.");

        /*
         * Self comparator that will take two file objects and use the file name
         * to sort the vector in order. This way, the files will always be in the 
         * same order in case different machines use listFiles() differently.
         */ 
        class CompareImageFiles implements Comparator <File> {
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        }

        Collections.sort(image_files, new CompareImageFiles());
    }

    /*
     * Create clicked.txt and skipped.txt if they don't exist.
     */
    private void createNewFiles(String output_path) {
        if(!new File(output_path + "skipped.txt").exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(output_path + "clicked.txt"));
                bw.close();
                logger.log("Created clicked.txt");
            }
            catch(IOException e) {
                System.out.println("28: Could not create clicked.txt file");
                System.exit(28);
            }
        }

        if(!new File(output_path + "skipped.txt").exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(output_path + "skipped.txt"));
                bw.close();
                logger.log("Created skipped.txt");
            }
            catch(IOException e) {
                System.out.println("29: Could not create skipped.txt file");
                System.exit(29);
            }
        }
    }

    /*
     * Read config.txt and make sure the values in the file are valid. If this file
     * does not exist, create it.
     */
    private void readConfig(String output_path) {
        String config_path = output_path + "config.txt";
        File config = new File(config_path);

        if(config.isFile()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(config_path));
                String line;

                while((line = br.readLine()) != null) {
                    String [] splits = line.split("=");

                    if(splits.length != 2) {
                        System.out.println("19: Invalid config file");
                        System.exit(19);
                    }

                    if(splits[0].equals("counter")) {
                        try {
                            counter = Integer.parseInt(splits[1]);
                        }
                        catch(NumberFormatException e) {
                            System.out.println("21: Counter in config file has incorrect format, expected integer");
                            System.exit(21);
                        }
                    }
                    else if(splits[0].equals("total")) {
                        try {
                            total_images = Integer.parseInt(splits[1]);
                        }
                        catch(NumberFormatException e) {
                            System.out.println("20: Total in config file has incorrect format, expected integer");
                            System.exit(20);
                        }
                    }
                    else {
                        System.out.println("22: Invalid variable in config file");
                        System.exit(22);
                    }
                }

                br.close();
                logger.log("config file read");
                logger.log("counter = " + counter);
                logger.log("total_images = " + total_images);
            }
            catch(IOException e) {
                System.out.println("16: Error when reading config file");
                System.exit(16);
            }

            if(counter < 0 || counter > total_images) {
                System.out.println("15: Counter outside of valid range");
                System.exit(15);
            }

            if(total_images <= 0) {
                System.out.println("17: Total number of images outside of valid range");
                System.exit(17);
            }

            if(total_images != image_files.size()) {
                System.out.println("18: Number of images in folder does not match config file");
                System.exit(18);
            }

            if(counter == total_images) {
                System.out.println("32: Already done clicking this set");
                System.exit(32);
            }

            logger.log("config file values are valid.");
        }
        else {
            counter = 0;
            total_images = image_files.size();
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(config_path));

                String counter_s = "counter=" + counter;
                String total_s = "total=" + total_images;

                bw.write(counter_s, 0, counter_s.length());
                bw.newLine();
                bw.write(total_s, 0, total_s.length());

                bw.close();
                logger.log("config file was created.");
            }
            catch(IOException e) {
                System.out.println("23: Could not create config file");
                System.exit(23);
            }
        }
    }

    /*
     * Initialize the data. The amount of data initialized at one time is equal to 
     * the write_file variable. This is to avoid scaling every image in the set, which 
     * could take some time if large data sets have to be scaled. New Data objects are 
     * created and each Data object gets its image and scaled image created.
     */
    private void initialize() {
        data = new Data[write_file];

        if(counter + write_file > total_images) {
            index_max = total_images - counter;
        }
        else {
            index_max = write_file;
        }

        /*
         * The for loop has a ternary operator in it to avoid an if statement and code 
         * reuse or having to create a new function with code along with that additional 
         * if statement. This takes into account when total_images is not a multiple of write_file. 
         * There will be a time at the end where we will need to initialize less than write_file
         * images so this ternary operator accounts for that.
         */
        for(int x = counter; x <((counter + write_file <= total_images) ? counter + write_file : total_images); x++) {
            logger.log("Loading " + image_files.get(x) + "...");
            File file = image_files.get(x);

            Image image = null;
            BufferedImage image_normal = null;
            try {
                image_normal = ImageIO.read(file);
                logger.log("Image loaded.");
                image = image_normal.getScaledInstance(640, 480, Image.SCALE_SMOOTH);
                logger.log("Scaled image loaded.");
            } 
            catch(IOException e) {
                System.out.println("24: Error reading image from file");
                System.exit(24);
            }   
            data[x - counter] = new Data(file, image, image_normal);
        }

        index = 0;
        logger.log("All images loaded.");
    }

    /*
     * Write to files config.txt, clicked.txt, skipped.txt, and all of the point
     * files.
     */
    private void writeFiles() {
        String clicked_file = output_path + "clicked.txt";
        String skipped_file = output_path + "skipped.txt";
        String config_file = output_path + "config.txt";

        logger.log("Writing points...");
        try {
            for(int x = 0; x < index_max; x++) {
                if(data[x].getClicked()) {
                    String filename = output_path + data[x].getName() + ".txt";
                    logger.log("Writing to file " + filename);

                    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

                    Vector <PointData> points = data[x].getPoints();
                    double ratio_x = data[x].getRatioX();
                    double ratio_y = data[x].getRatioY();

                    for(int z = 0; z < points.size(); z++) {
                        int xcor =(int)(points.get(z).getX() * ratio_x);
                        int ycor =(int)(points.get(z).getY() * ratio_y);

                        if(rgb) {
                            int r = points.get(z).getR();
                            int g = points.get(z).getG();
                            int b = points.get(z).getB();

                            bw.write(xcor + " " + ycor + " " + r + " " + g + " " + b);
                            logger.log("Writing point with RGB value: " + xcor + " " + ycor + " " + r + " " + g + " " + b);
                        }
                        else {
                            bw.write(xcor + " " + ycor);
                            logger.log("Writing point " + xcor + " " + ycor);
                        }
                        bw.newLine();
                    }
                    bw.close();
                }
            }
        }
        catch(IOException e) {
            System.out.println("33: Could not write point files");
            System.exit(33);
        }

        logger.log("Done writing points.");
        logger.log("Writing clicked.txt...");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(clicked_file, true));
            for(int x = 0; x < index_max; x++) {
                if(data[x].getClicked()) {
                    bw.append(data[x].getFile().getName());
                    bw.append(System.getProperty("line.separator"));
                    logger.log(data[x].getFile().getName());
                }
            }
            bw.close();
        }
        catch(IOException e) {
            System.out.println("25: Could not write to clicked file");
            System.exit(25);
        }

        logger.log("Done writing clicked.txt");
        logger.log("Writing skipped.txt...");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(skipped_file, true));
            for(int x = 0; x < index_max; x++) {
                if(!data[x].getClicked()) {
                    bw.append(data[x].getFile().getName());
                    bw.append(System.getProperty("line.separator"));
                    logger.log(data[x].getFile().getName());
                }
            }
            bw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            System.out.println("26: Could not write to skipped file");
            System.exit(26);
        }

        logger.log("Done writing skipped.txt");
        logger.log("Writing config.txt...");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(config_file));
            bw.write("counter=" + counter);
            logger.log("writing counter " + counter);
            bw.newLine();
            bw.write("total=" + total_images);

            logger.log("Writing total " + total_images);
            bw.close();

        }
        catch(IOException e) {
            System.out.println("27: Could not write config file");
            System.exit(27);
        }
        logger.log("Done writing to files");
    }
    
    /*
     * Get key presses. N is go to the next image. D is delete the previous
     * point. S is skip to the next image. B goes back to the previous image.
     * The arrows will move the current point in the direction the arrow points.
     */  
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch(code) {
            case KeyEvent.VK_N: 
                data[index].setClicked();
                index++;
                counter++;

                if(index == index_max) {
                    writeFiles();
                    if(counter == total_images) {
                        System.out.println("30: Done");
                        System.exit(30);
                    }
                    else {
                        logger.log("Setting up next set of images...");
                        initialize();
                    }
                }

                repaint();
                break;
            case KeyEvent.VK_D:
                data[index].removeLastPoint();
                repaint();
                break;
            case KeyEvent.VK_S:
                logger.log("Skipping to next image");
                data[index].setSkipped();
                data[index].clearPoints();
                counter++;
                index++;

                if(index == index_max) {
                    writeFiles();
                    if(counter == total_images) {
                        System.out.println("30: Done");
                        System.exit(30);
                    }
                    else {
                        initialize();
                    }
                }

                repaint();
                break;
            case KeyEvent.VK_B:
                if(index > 0) {
                    logger.log("Going back to previous image.");
                    data[index].clearPoints();
                    data[index].setSkipped();
                    index--;
                    data[index].clearPoints();
                    data[index].setSkipped();
                }

                repaint();
                break;
            case KeyEvent.VK_UP:
                logger.log("Moving point up...");
                data[index].moveCurrentPointY(-pixels);
                repaint();
                break;
            case KeyEvent.VK_DOWN:
                logger.log("Moving point down...");
                data[index].moveCurrentPointY(pixels);
                repaint();
                break;
            case KeyEvent.VK_LEFT:
                logger.log("Moving point left...");
                data[index].moveCurrentPointX(-pixels);
                repaint();
                break;
            case KeyEvent.VK_RIGHT:
                logger.log("Moving point right...");
                data[index].moveCurrentPointX(pixels);
                repaint();
                break;
            default:
                break;
        }

    }

    /*
     * Add the clicked (x,y) coordinate to the current image as a point.
     */
    public void mousePressed(MouseEvent e) {
        logger.log("Adding point...");
        Point p = new Point(e.getX(), e.getY());
        data[index].setPoint(p);
        repaint();
    }

    public void mouseReleased(MouseEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}