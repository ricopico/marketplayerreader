/**
 * Created by caneba on 12/20/15.
 */
public class Constants {

    public static String[] dataFileExtensions = {"txt"};
    public static String[] years = {"2010","2011","2012","2013","2014","2015"};
    public static int k = 5;
    public static int iterations = 50;
    public static int numberOfSymbolsToTrack = 100;  //check for
    public static long firstDate = 20100101;
    public static long lastDate = 20151009;
    public static String outputFile = "output/output.txt";
    public static String marketAnalysisFile = "output/marketOutput.txt";
    public static String betaSort = "output/beta.txt";
    public static String alphaSort = "output/alpha.txt";
    public static String mostStableStocks = "output/stabilitySortOutput.txt";
    public static String maxAlphaBetaSort = "output/maxAlphaBeta.txt";
    public static String SP500FilePath = "src/main/resources/SP500.csv";
    public static String NASDAQFilePath = "src/main/resources/NASDAQCOM.csv";
    public static double riskFreeRate = .015;
    public enum ACTION {
        BUY, SELL, HOLD, IGNORE
    }

    private Constants() throws Exception {
        //do not instantiate this
        throw new Exception("do not instantiate Constants.java");
    }



}
