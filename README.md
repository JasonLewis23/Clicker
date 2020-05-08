Clicker is an application that is used to click feature points on any image. X and Y coordinaes are logged, as well as an option rgb value. The application supports skipping through images, going back and re-clicking, and per-pixel movement of points after they are clicked. For any help running the application, check the usage section. For any help with error codes or the application behaving incorrectly, check the exit codes section.

Below is a line located in ClickFrame.java:

private String [] extensions = {".png", ".jpg", ".jpeg", ".gif", ".ppm"};

These are the supported images files. If there is a file extension in here that you must have supported, you can go to this line and add more extensions. It is simply an array of strings. Just don't forget the dot at the beginning.

The default image scaling algorithm in java can take some time depending upon the size of the image. This is why the verbose command line option is very useful. You will be able to tell from the output if the program is actually frozen or if it is waiting for some images to finish scaling.



Usage:

To compile:

	javac Clicker.java
	
To run:

	java Clicker --out <output path> --imgs <images path> [--help] [--save] [--wf <integer>] [--log] [--rgb] [--verbose]

To run the most basic application:

	java Clicker --out test_output/ --imgs test_images/

To log and print program output:

	 java Clicker --out test_output/ --imgs test_images/ --verbose --log

To include rgb values and archive previous results:

	java Clicker --out test_output/ --imgs test_images/ --rgb –save

Recommended Use:

	java Clicker --out <output path> --imgs <images path> --verbose --rgb --save

Any combinations of arguments can be used. However, if you use –help, the program will immediately exit and display the help message.



Command Line Args:

--out <output path> - requires a valid directory path. This directory path will be used for the output of the program. This argument is required.
	
--imgs <images path> - requires a valid directory path. This directory must be the location of all of the images that will be clicked. This argument is not recursive so all images must be in the immediate directory. This argument is required.
	
--help displays a helpt screen for the user depicting command line arguments and a usage statement. Optional.

--save archives files that are in the output folder. Optional.

-wf <integer> allows the user to set their own save blocks i.e. this value is the number of images you can click through before they are written to a file. The default value is 5. I recommend not using this option unless you absolutely need to. Optional.
	
--log creates a log file for the user. The verbose output is written to the log file. Optional.

--rgb writes rgb values for each clicked point in the point file. Optional.

--verbose print program output to the screen. Optional.



How To:

This section will explain how to use the application. To compile and start the application, look at the section called usage and command line args.

Once the images are displayed, you may start clicking. Use the mouse to click a point. At every clicked coordinate, a small green circle will be drawn. The point that was clicked is at the center of the circle. Any number of points can be clicked so it is up to the users to determine an order and number. 

The 'n' key will go to the next image. If the user presses the 's' key, the image will be skipped and no points will be saved for that image. The 'b' key will allow the user to go back to the previous image, clearing the points for both images. The max a user will be able to go back will be equal to the command line argument --wf (by default is 5). The arrow keys will move the current point one pixel in the direction of the arrow. New rgb values will be computed as well. The 'd' key will delete the most current point so the user can re click that point.

There is a counter that keeps track of the current image and a total number of images. When those counters equal each other, the program will have a successful exit. The verbose output and log output will show this occuring. There may be ways the user wants to check to make sure nothing was messed up in the exit. The user can look in the config file (total will equal counter) or the user can sum up the number of files in skipped.txt and clicked.txt and that will equal the total number of images. Also, the user may want to make sure the number of files in clicked.txt is equal to the number of point files in the output folder.



Exit Codes:

0 – No arguments provided. Use the –help flag to print a usage screen.

1 – Help screen printed because --help provided

2 – Invalid argument provided, argument not recognized.

3 – Expected integer for --wf but observed a different data type

4 – Invalid path for --imgs, path does not exist

5 – Invalid path for --imgs, path not a directory

6 – Invalid path for --out, path does not exist

7 – Invalid path for --out, path not a directory

8 – Negative value or zero provided for --wf

9 – --imgs <images path> argument not provided

10 – --out <output path> argument not provided

11 – archive directory could not be created

12 – I/O error while archiving file

13 – Integer accompanying --wf argument not provided.

14 – No images exist in the image path

15 – counter value in config file outside allowable range (negative or greater than number of images)

16 – I/O error when reading from config file

17 – total value in config file outside allowable range (negative value or 0)

18 – total value in config file not equal to number of images in image path

19 – One of the lines in the config file was formatted incorrectly (correct format is name=value)

20 – Total in config file has an incorrect format. Expected integer.

21 – Counter in config file has an incorrect format. Expected integer.

22 – Invalid variable in config file. Was not counter or total.

23 – I/O error when creating config file

24 – I/O error when reading image from file

25 – I/O error when writing clicked.txt

26 – I/O error when writing skipped.txt

27 – I/O error when writing config.txt

28 – I/O error, could not create clicked.txt file in the output path

29 – I/O error, could not create skipped.txt file in the output path

30 – Done clicking this set of images

31 – I/O error when writing to log file

32 – After reading config file, exits because the application determines you are done

33 – I/O error when writing point files

34 – Path accompanying --imgs argument not provided. 

35 – Path accompanying --out argument not provided.



Other possible errors:

There are multiple places a SecurityException can be thrown. This happens when you don't have permission to access files or directories in the locations you are trying to access them. This will always occur where your images are located, where your output path is located, or where the java class files are located.
