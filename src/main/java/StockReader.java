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
            String inputStreamString = new Scanner(inputStream,"UTF-8").useDelimiter("\n").next();
            List<String> entry = Utilities.returnCommaDelimitedStringAsList(inputStreamString);

            Stock s = parseStock(entry);

            if(!symbolToStockSequence.containsKey(s.getSymbol())) {
                symbolToStockSequence.put(s.getSymbol(), new LinkedList<Stock>());
            }
            symbolToStockSequence.get(s.getSymbol()).add(s);
        }

        //sort the stock sequences by time
        Map<String, List<Stock>> sortedSymbolToStockSequence = new HashMap<String, List<Stock>>();

        for(String symbol : symbolToStockSequence.keySet()) {
            List<Stock> sortedList = Utilities.sortStockListByDate(symbolToStockSequence.get(symbol));
            sortedSymbolToStockSequence.put(symbol, sortedList);
        }

        //generate value transitions
        for(String symbol : sortedSymbolToStockSequence.keySet()) {
            List<Stock> stockList = sortedSymbolToStockSequence.get(symbol);
            for(int i=0; i<stockList.size()-1; i++) {
                Stock first = stockList.get(i);
                Stock second = stockList.get(i+1);
                first.setValueTransition(second);
            }
        }


        return new StockData(sortedSymbolToStockSequence);
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
    private static Stock parseStock(List<String> entry) throws Exception {
        return new Stock(entry);
    }



}



