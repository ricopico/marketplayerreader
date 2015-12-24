import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by caneba on 12/20/15.
 */
public class StockReader {

    public static StockData convertResourcesToStockData(Set<InputStream> stockDataInput) throws Exception {

        Map<String, List<Stock>> symbolToStockSequence = new HashMap<String, List<Stock>>();

        for(InputStream inputStream : stockDataInput) {
            //each input stream is a file with stocks from that particular day

            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "UTF-8");
            String inputStreamString = writer.toString();

            String[] split = inputStreamString.split("\\r\\n");

            for(int i=0; i<split.length; i++) {
                List<String> entry = Utilities.returnCommaDelimitedStringAsList(split[i]);
                if(entry.size()>0) {
                    Stock s = parseStock(entry);

                    if (!symbolToStockSequence.containsKey(s.getSymbol())) {
                        symbolToStockSequence.put(s.getSymbol(), new LinkedList<Stock>());
                    }
                    symbolToStockSequence.get(s.getSymbol()).add(s);
                }
            }
        }

        //sort the stock sequences by time
        Map<String, List<Stock>> sortedSymbolToStockSequence = sortSymbolToStockSequence(symbolToStockSequence);

        //generate the value transitions
        generateValueTransitions(sortedSymbolToStockSequence);

        //get symbol to value transition array
        Map<String, double[]> symbolToValueTransitionArray = getSymbolToValueTransitionsAsDoubleArray(sortedSymbolToStockSequence);

        return new StockData(sortedSymbolToStockSequence, symbolToValueTransitionArray);
    }

    public static void generateValueTransitions(Map<String, List<Stock>> sortedSymbolToStockSequence) {
        //generate value transitions
        for(String symbol : sortedSymbolToStockSequence.keySet()) {
            List<Stock> stockList = sortedSymbolToStockSequence.get(symbol);
            for(int i=0; i<stockList.size()-1; i++) {
                Stock first = stockList.get(i);
                Stock second = stockList.get(i+1);
                if(first == null) {
                    continue;
                }
                if(second == null) {
                    continue;
                }
                first.setValueTransition(second);
            }
        }
    }
    public static Map<String, List<Stock>> sortSymbolToStockSequence(Map<String, List<Stock>> symbolToStockSequence) {
        Map<String, List<Stock>> sortedSymbolToStockSequence = new HashMap<String, List<Stock>>();

        for(String symbol : symbolToStockSequence.keySet()) {
            List<Stock> sortedList = Utilities.sortStockListByDate(symbolToStockSequence.get(symbol));
            sortedSymbolToStockSequence.put(symbol, sortedList);
        }

        return sortedSymbolToStockSequence;
    }
//    private static Map<String, double[]> getSymbolToValueTransitionArray(Map<String, List<Stock>> sortedSymbolToStockSequence) {
//
//        //generate the symbol to value transitions arrays
//        Map<String, double[]> symbolToValueTransitionArray = new HashMap<String, double[]>();
//        for(String symbol : sortedSymbolToStockSequence.keySet()) {
//            List<Stock> stockSequence = sortedSymbolToStockSequence.get(symbol);
//            List<Double> valueTransitionList = new LinkedList<Double>();
//            for(int i=0; i<stockSequence.size()-1; i++) {
//                Stock first = stockSequence.get(i);
//                double percentValueDiff_1 = first.getNextValueTransition().getPercentValueDiff_1();
//                double percentValueDiff_2 = first.getNextValueTransition().getPercentValueDiff_2();
//                double percentValueDiff_3 = first.getNextValueTransition().getPercentValueDiff_3();
//                double percentValueDiff_4 = first.getNextValueTransition().getPercentValueDiff_4();
//                double averagePercentValueDiff = first.getNextValueTransition().getAveragePercentValueDiff();
//                valueTransitionList.add(percentValueDiff_1);
//                valueTransitionList.add(percentValueDiff_2);
//                valueTransitionList.add(percentValueDiff_3);
//                valueTransitionList.add(percentValueDiff_4);
//                valueTransitionList.add(averagePercentValueDiff);
//            }
//            double[] valueTransitionArray = new double[valueTransitionList.size()];
//            for(int i=0; i<valueTransitionList.size(); i++) {
//                valueTransitionArray[i] = valueTransitionList.get(i);
//            }
//            symbolToValueTransitionArray.put(symbol, valueTransitionArray);
//        }
//
//        return symbolToValueTransitionArray;
//    }

    public static void clearValueTransitionsFromStockList(List<Stock> stockList) {
        for(Stock stock : stockList) {
            if(stock != null) {
                stock.clearValueTransitions();
            }
        }
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
    public static Map<String, double[]> getSymbolToValueTransitionsAsDoubleArray(Map<String, List<Stock>> symbolToStockSequence) {
        Map<String, double[]> toReturn = new HashMap<String, double[]>();

        for(String symbol : symbolToStockSequence.keySet()) {
            List<Double> valuesList = new LinkedList<Double>();
            for(int i=0; i<symbolToStockSequence.get(symbol).size()-1; i++) {
                Stock curStock = symbolToStockSequence.get(symbol).get(i);
                Stock nextStock = symbolToStockSequence.get(symbol).get(i+1);
                if(curStock != null && nextStock != null) {
                    Stock.ValueTransitionObject vto = curStock.getNextValueTransition();
                    valuesList.add(vto.getPercentValueDiff_1());
                    valuesList.add(vto.getPercentValueDiff_2());
                    valuesList.add(vto.getPercentValueDiff_3());
                    valuesList.add(vto.getPercentValueDiff_4());
                    valuesList.add(vto.getAveragePercentValueDiff());
                } else {
                    valuesList.add((double)0);
                    valuesList.add((double)0);
                    valuesList.add((double)0);
                    valuesList.add((double)0);
                    valuesList.add((double)0);
                }
            }
            double[] arrayToAdd = new double[valuesList.size()];
            for(int i=0; i<valuesList.size(); i++) {
                arrayToAdd[i] = valuesList.get(i);
            }
            toReturn.put(symbol, arrayToAdd);
        }
        return toReturn;
    }
    private static Stock parseStock(List<String> entry) throws Exception {
        return new Stock(entry);
    }

}



