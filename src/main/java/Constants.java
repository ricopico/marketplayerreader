/**
 * Created by caneba on 12/20/15.
 */
public class Constants {

    public static String[] dataFileExtensions = {"txt"};
    public static String[] years = {"2010","2011","2012","2013","2014","2015"};
    public static int k = 100;
    public static int iterations = 500;
    public static long firstDate = 20100101;
    public static long lastDate = 20151009;
    public static String outputFile = "output/output.txt";

    private Constants() throws Exception {
        //do not instantiate this
        throw new Exception("do not instantiate Constants.java");
    }



}
