import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by caneba on 12/20/15.
 */
public class StockReader {

    public static StockData convertResourcesToStockData(Set<InputStream> stockDataInput) throws Exception {

        Map<String, List<Stock>> symbolToStockSequence = new HashMap<String, List<Stock>>();

        for(InputStream inputStream : stockDataInput) {
            String inputStreamString = new Scanner(inputStream,"UTF-8").useDelimiter("\\A").next();
            List<String> entry = Utilities.returnCommaDelimitedStringAsList(inputStreamString);

            Stock s = parseStock(entry);

            if(!symbolToStockSequence.containsKey(s.getSymbol())) {
                symbolToStockSequence.put(s.getSymbol(), new LinkedList<Stock>());
            }
            symbolToStockSequence.get(s.getSymbol()).add(s);
        }

        //TODO: sort the stock sequences by date
        return null;
    }
    public static StockData convertResourcesToStockData() throws Exception {

        Set<InputStream> stockDataInput = new HashSet<InputStream>();

        for(int i=0; i<Constants.years.length; i++) {
            String year = Constants.years[i];
            String folderName = "NASDAQ_" + year;
            String directoryPath = "src/main/resources/daytoday/" + folderName + "/";
            Set<String> fileNames = Utilities.getAllFileNamesInDirectory(directoryPath);

            for(String fileName : fileNames) {
                File file = new File(fileName);
                InputStream inputStream = new FileInputStream(file);
                stockDataInput.add(inputStream);

            }
        }
        return convertResourcesToStockData(stockDataInput);
    }
    private static Stock parseStock(List<String> entry) {
        //TODO: make this function convert list<String> to stock
        return null;
    }



}



