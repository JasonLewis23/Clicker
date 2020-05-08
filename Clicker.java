import java.util.Vector;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Calendar;
import java.io.File;

/*
 * Main class
 */
public class Clicker {

    /*
     * Parse arguments, check for valid arguments, archive files if necessary
     * and call the correct constructor to start the application.
     * 
     * params:
     *      args - command line arguments
     */
    public static void main(String[] args) {
        int index = 0;              // current arg processing index
        Logger logger;              // custom logger object
        String output_path = null;  // location of saved image click files
        String image_path = null;   // location to the images
        int write_file = -1;        // how often to write image files (every # files)
        boolean archive = false;    // archive previous files
        boolean verbose = false;    // flag for printing to the terminal
        boolean rgb = false;        // print rgb values to x/y clicked file
        boolean log = false;        // flag for printing to the log file
    
        if(args.length == 0) {
            System.out.println("0: No args given. Use --help for usage.");
            System.exit(0);
        }

        System.out.println("Parsing args...");
        while(index < args.length) {

            // image path given
            if(args[index].equals("--imgs")) {
                index++;
                if(index >= args.length) {
                    System.out.println("34: Path must be privded with --imgs.");
                    System.exit(34);
                }

                // check for a valid path
                image_path = args[index];
                File f = new File(image_path);
                if(!f.exists()) {
                    System.out.println("4: invalid arg for " + args[index - 1] + ", \"" + image_path + "\" does not exist");
                    System.exit(4);
                    if(!f.isDirectory()) {
                        System.out.println("5: invalid arg for " + args[index - 1] + ", \"" + image_path + "\" is not a directory");
                        System.exit(5);
                    }
                }
                System.out.println("--imgs provided: " + image_path);
            }
            // verbos flag given
            else if(args[index].equals("--verbose")) {
                verbose = true;
                System.out.println("--verbose provided: true");
            }
            // rgb flag given
            else if(args[index].equals("--rgb")) {
                rgb = true;
                System.out.println("--rgb provided: true");
            }
            // log flag given
            else if(args[index].equals("--log")) {
                log = true;
                System.out.println("--log provided: true");
            }
            //output path location given
            else if(args[index].equals("--out")) {
                index++;
                if(index >= args.length) {
                    System.out.println("35: Path must be privded with --out.");
                    System.exit(35);
                }

                // check that we have a valid path
                output_path = args[index];
                File f = new File(output_path);
                if(!f.exists()) {
                    System.out.println("6: invalid arg for " + args[index - 1] + ", \"" + output_path + "\" does not exist");
                    System.exit(6);
                    if(!f.isDirectory()) {
                        System.out.println("7: invalid arg for " + args[index - 1] + ", \"" + output_path + "\" is not a directory");
                        System.exit(7);
                    }
                }
                System.out.println("--out provided: " + output_path);
            }
            // archiving flag given 
            else if(args[index].equals("--save")) {
                archive = true;
                System.out.println("--save provided: true");
            }
            // numeric for how many files to store before writing given
            else if(args[index].equals("--wf")) {
                index++;
                if(index >= args.length) {
                    System.out.println("13: integer must be privded with --wf.");
                    System.exit(13);
                }

                // ensure a valid integer is given
                try {
                    write_file = Integer.parseInt(args[index]);
                    if(write_file <= 0) {
                        System.out.println("8: invalid arg for " + args[index - 1] + ", \"" + write_file + " is not greater than 0");
                        System.exit(8);
                    }
                }
                catch(NumberFormatException e) {
                    System.out.println("3: invalid arg for " + args[index - 1] + ", expected int and observed " + args[index]);
                    System.exit(3);
                }
                System.out.println("--wf provided: " + write_file);
            }
            // help option given
            else if(args[index].equals("--help")) {
                System.out.println("1:");
                System.out.println("------------------------------------------------------------------------------");
                System.out.println("pointclicking");
                System.out.println("  --imgs <images path>     path to the directory of images");
                System.out.println("  --out <output path>      path to the directory where the output will go");
                System.out.println("  [--wf <integer>]         how often writes to file occur");
                System.out.println("  [--save]                 archives previous results inside the output folder");
                System.out.println("  [--help]                 displays a usage message");
                System.out.println("  [--verbose]              display output of program");
                System.out.println("  [--log]                  log output of program to file");
                System.out.println("  [--rgb]                  print rgb values for points to file");
                System.out.println("------------------------------------------------------------------------------");
                System.exit(1);
            }
            // invalid arg
            else {
                System.out.println("2: invalid arg \"" + args[index] + "\"");
                System.exit(2);
            }
            index++;
        }

        // create the logger by combination of verbose and log to set the level
        logger = Logger.getInstance(verbose, log);
        logger.log("Checking if --imgs arg was provided...");

        if(image_path == null) {
            System.out.println("9: --imgs arg must be provided. Use --help for usage.");
            System.exit(9);
        }
        else {
            // replace windows style escaped paths with linux style and pad end
            image_path.replace('\\', '/');
            if(!image_path.endsWith("/")) {
                image_path = image_path + "/";
            }
        }

        logger.log("--imgs arg provided.");
        logger.log("Checking if --out arg was provided...");

        if(output_path == null) {
            System.out.println("10: --out arg must be provided. Use --help for usage.");
            System.exit(10);
        }
        else {
            // replace windows style escaped paths with linux style and pad end
            output_path.replace('\\', '/');
            if(!output_path.endsWith("/")) {
                output_path = output_path + "/";
            }
        }

        logger.log("--out arg provided.");
        logger.log("output_path = " + output_path);
        logger.log("image_path = " + image_path);

        // if archive was given, archive previous files
        if(archive) {
            logger.log("Archiving previous files in folder...");
            archiveFiles(output_path);
            logger.log("Archiving done.");
        }

        // start application
        logger.log("Starting application...");
        if(write_file == -1)  {
            new ClickFrame(output_path, image_path, verbose, rgb, log);
        }
        else {
            new ClickFrame(output_path, image_path, write_file, verbose, rgb, log);
        }
    }

    /* 
     * Archive files in a unique folder that is named with the date and timestamp
     * from when the application is run. All point files are moved to the archive 
     * and clicked.txt, skipped.txt, and config.txt are copied into the archive 
     * folder but left in the current output folder. The archive folder is created
     * in the output path. Any log files are moved and not copied so each instance 
     * of an archive will have its own log file in the filesystem.
     * 
     * params:
     *      output_path - resulting directory of the archiving
     */
    private static void archiveFiles(String output_path) {
        Logger logger = Logger.getInstance();
        File folder = new File(output_path);
        File [] files = folder.listFiles();
        Vector <File> copy_list = new Vector <File>(files.length);

        logger.log("Finding all files in output path...");

        for(File f : files) {
            if(f.isFile()) {
                copy_list.add(f);
            }
        }

        logger.log("All files found.");

        if(copy_list.size() == 0) {
            logger.log("No files to archive, returning...");
            return;
        }

        Calendar cal = Calendar.getInstance();
        String cur_time = String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		cur_time = cur_time.replace(":", "-");
        String dir_path = output_path + cal.get(Calendar.YEAR) + "-" +(cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cur_time + "/";
        File dir = new File(dir_path);

        if(!dir.mkdir()) {
            System.out.println("11: Archive directory could not be created: " + dir_path);
            System.exit(11);
        }

        logger.log("Files will be archived at " + dir_path);

        for(File f : copy_list) {
            if(f.getName().equals("config.txt") || f.getName().equals("clicked.txt") || f.getName().equals("skipped.txt")) {
                BufferedReader br;
                BufferedWriter bw;
                try {
                    logger.log("copying " + f.getName() + " to archive folder");
                    br = new BufferedReader(new FileReader(output_path + f.getName()));
                    bw = new BufferedWriter(new FileWriter(dir_path + f.getName()));

                    int c;
                    while((c = br.read()) != -1) {
                        bw.write(c);
                    }

                    br.close();
                    bw.close();
					f.delete();
                    logger.log("Finished copying");
                }
                catch(IOException e) {
                    System.out.println("12: Could not copy file to archive");
                    System.exit(12);
                }
            }
            else {
                logger.log("Renaming " + f.getName() + " to archive folder");
                String new_file = dir_path + f.getName();
                f.renameTo(new File(new_file));
            }
        }
    }
}