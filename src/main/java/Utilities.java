import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


/**
 * Created by caneba on 12/20/15.
 */
public class Utilities {

    private Utilities() throws Exception {
        //never instantiate this
        throw new Exception("do not instantiate Utilities.java");
    }


    public static Set<String> getAllFileNamesInDirectory(String path) throws IOException {
        Set<String> fileNames = new HashSet<String>();

        File dir = new File(path);
        List<File> files = (List<File>) FileUtils.listFiles(dir, Constants.dataFileExtensions, true);
        for (File file : files) {
            fileNames.add(file.getCanonicalPath());
        }
        return fileNames;
    }

    public static List<String> returnCommaDelimitedStringAsList(String s) {
        List<String> toReturn = new LinkedList<String>();
        String[] split = s.split(",");
        for(int i=0; i<split.length; i++) {
            toReturn.add(split[i]);
        }
        return toReturn;
    }

    public static List<Stock> sortStockListByDate(List<Stock> listToSort) {

        //convert to array so I can use comparator

        Stock[] stockArray = new Stock[listToSort.size()];
        for(Stock stock : listToSort) {
            stockArray[listToSort.indexOf(stock)] = stock;
        }

        Arrays.sort(stockArray);

        List<Stock> sortedList = new LinkedList<Stock>();

        for(int i=0; i<stockArray.length; i++) {
            sortedList.add(stockArray[i]);
        }

        return sortedList;
    }

    public static String stockArrayByDateToString(Stock[] array) {
        String toReturn = "";
        for(int i=0; i<array.length; i++) {
            toReturn += array[i].getDate();
            if(i<array.length-1) {
                toReturn += ", ";
            }
        }
        return toReturn;
    }

    public static void plotStockSequence(List<Stock> stockSequence, String symbol) {

        System.out.println(symbol + " : " + stockSequence.size());
        long start = stockSequence.get(0).getDate();
        long end = stockSequence.get(stockSequence.size()-1).getDate();

        Stage stage = new Stage();
        stage.setTitle("Stock performance plot");
        //TODO: this stuff


    }

    public static int getIndexOfCorrectInsertOfDoubleValueInList(List<Double> list, double toCheck) {
        for(int i=0; i<list.size(); i++) {
            if(list.get(i) > toCheck) {
                return i;
            }
        }

        return list.size();
    }

    public static void writeToFile(String outputFilePath, String string) {
        try {
            PrintWriter writer = new PrintWriter(outputFilePath, "UTF-8");
            writer.println(string);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.err.println("could not print to output file : " + e.getMessage());
        }
    }

    public static Double[] convertDoubleSetToArray(Set<Double> toConvert) {
        Double[] toReturn = new Double[toConvert.size()];
        int i=0;
        for(Double d : toConvert) {
            toReturn[i] = d;
        }
        return toReturn;
    }

    public static double[] convertDoubleListToArray(List<Double> toConvert) {
        double[] toReturn = new double[toConvert.size()];

        for(int i=0; i<toConvert.size(); i++) {
            toReturn[i] = toConvert.get(i);
        }

        return toReturn;
    }
    public static double getAverageOfDoubleArray(double[] arr) {
        double sum = 0;
        for(int i=0; i<arr.length; i++) {
            sum += arr[i];
        }
        return sum/arr.length;
    }
}
