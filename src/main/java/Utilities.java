import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
}
