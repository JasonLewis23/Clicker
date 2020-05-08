import java.util.Calendar;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;

/*
 * Basic logger class with four levels. Provides an ability to write to a log file, 
 * verbose output, or both. This is a singleton class.
 */
public class Logger {

    /*
     * Enum that determines the level of the logger which is determined by
     * arguments that are passed in. OFF is verbose and log. LOW is only 
     * verbose. MEDIUM is only log. HIGH is verbose and log.
     */
    enum Level {
        OFF, LOW, MEDIUM, HIGH
    }

    Level level;
    String log_file;
    static Logger logger = null;

    /*
     * Used to initialize the logger variable if it hasn't been before and gives the
     * calling function the instance for the logger.
     */
    public static Logger getInstance(boolean verbose, boolean log) {
        if(logger == null) {
            logger = new Logger(verbose, log); 
        }
        return logger;
    }

    /*
     * Used to initialize the logger variable if it hasn't been before and gives the
     * calling function the instance for the logger. This is used opposed to the one
     * above if you weren't provide with the verbose and log flag or you know getInstance()
     * has already been called and you don't want to pass args in.
     */
    public static Logger getInstance() {
        if(logger == null) {
            logger = new Logger(false, false);
        }
        return logger;
    }

    /*
     * Constructor for the Logger class which only gets called once. The 
     * level is determined by the verbose and log that are originally passed 
     * to the getInstance() function. The log file is also created which is 
     * given the filename that corresponds to the date and timestamp.
     */
    private Logger(boolean verbose, boolean log) {
        if(verbose && log) {
            level = Level.HIGH;
			Calendar cal = Calendar.getInstance();
			String cur_time = String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			log_file = "clicker_" + cal.get(Calendar.YEAR) + "-" +(cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cur_time + ".log";
			log_file = log_file.replace(":", "-");
			log_file = log_file.replace(" ", "_");
			System.out.println(log_file);
        }
        else if(log && !verbose) {
            level = Level.MEDIUM;
        }
        else if(verbose && !log) {
            level = Level.LOW;
        }
        else {
            level = Level.OFF;
        }
    }

    /*
     * Chooses how the output is printed based on the level.
     */
    public void log(String s) {
        switch(level) {
            case OFF: 
                break;
            case LOW:
                printToScreen(s);
                break;
            case MEDIUM:
                printToFile(s);
                break;
            case HIGH:
                printToScreen(s);
                printToFile(s);
                break;
            default:
                break;
        }
    }

    /*
     * Verbose output that is just printing to the screen.
     */
    private void printToScreen(String s) {
        System.out.println(s);
    }

    /*
     * Log output that will printed to the file. This file is appended.
     */
    private void printToFile(String s) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(log_file, true));
            bw.append(s);
            bw.append(System.getProperty("line.separator"));
            bw.close();
        }
        catch(IOException e) {
            System.out.println("31: Error while writing to log file");
            System.exit(31);
        }
    }
}