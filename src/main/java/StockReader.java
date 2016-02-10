import javafx.util.Pair;
import org.apache.commons.io.IOUtils;

import java.io.*;
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

        //extract the SP500 and Nasdaq performance data
        List<Pair<Long, Double>> SP500Data = extractSP500DateAndValue();
        List<Pair<Long, Double>> NASDAQData = extractNasdaqDateAndValue();

        return new StockData(sortedSymbolToStockSequence, symbolToValueTransitionArray, SP500Data, NASDAQData);
    }

    private static List<Pair<Long, Double>> extractMarketDataFromFile(String filePath) throws IOException {

        List<Pair<Long, Double>> DateAndValue = new LinkedList<>();
        File data = new File(filePath);

        InputStream inputStream = new FileInputStream(data);

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String inputStreamString = writer.toString();

        String[] split = inputStreamString.split("\\r\\n");

        for(int i=1; i<split.length; i++) {

            String entry = split[i];
            String date = entry.split(",")[0];

            //convert the date to the same format as the EOD data.

            int year = Integer.parseInt(date.split("-")[0]);
            int month = Integer.parseInt(date.split("-")[1]);
            int day = Integer.parseInt(date.split("-")[2]);

            String convertedDate = date.replace("-", "");
            Long dateToAdd = Long.parseLong(convertedDate);

            Double value = (double)0;
            if(".".equals(entry.split(",")[1])) {
                value = null;
            }
            else {
                value = Double.parseDouble(entry.split(",")[1]);
            }

            Pair<Long, Double> newPair = new Pair<>(dateToAdd, value);
            DateAndValue.add(newPair);
        }


        return DateAndValue;
    }

    private static List<Pair<Long, Double>> extractSP500DateAndValue() throws IOException {
        return extractMarketDataFromFile(Constants.SP500FilePath);
    }
    private static List<Pair<Long, Double>> extractNasdaqDateAndValue() throws IOException {
        return extractMarketDataFromFile(Constants.NASDAQFilePath);
    }

    public static Map<String, List<Pair<Long, Double>>> getSymbolToDateAndClosingValuePair(StockData stockData) {
        Map<String, List<Pair<Long, Double>>> toReturn = new HashMap<>();

        for(String symbol : stockData.getSymbolToStockSequenceList().keySet()) {
            List<Pair<Long, Double>> dateAndClosingValue = new LinkedList<>();

            for(Stock stock : stockData.getSymbolToStockSequenceList().get(symbol)) {
                long date = stock.getDate();
                double closingValue = stock.getValues().getValue_4();
                dateAndClosingValue.add(new Pair(date, closingValue));
            }

            toReturn.put(symbol, dateAndClosingValue);
        }

        return toReturn;
    }

    public static Map<String, Map<Long, Double>> getSymbolToDateToClosingValueMap(StockData stockData) {

        Map<String, Map<Long, Double>> toReturn = new HashMap<>();

        for(String symbol : stockData.getSymbolToStockSequenceList().keySet()) {
            Map<Long, Double> dateToClosingValue = new HashMap<>();

            for(Stock stock : stockData.getSymbolToStockSequenceList().get(symbol)) {
                long date = stock.getDate();
                double closingValue = stock.getValues().getValue_4();
                dateToClosingValue.put(date, closingValue);
            }

            toReturn.put(symbol, dateToClosingValue);
        }

        return toReturn;

    }

    public static Map<String, List<Double>> getSymbolToClosingValues(StockData stockData) {
        Map<String, List<Double>> toReturn = new HashMap<String, List<Double>>();

        for(String symbol : stockData.getSymbolToStockSequenceList().keySet()) {
            List<Double> closingValues = new LinkedList<>();

            for(Stock stock : stockData.getSymbolToStockSequenceList().get(symbol)) {
                double closingValue = stock.getValues().getValue_4();
                closingValues.add(closingValue);
            }

            toReturn.put(symbol, closingValues);
        }

        return toReturn;
    }

    public static Map<String, double[]> generateSymbolToStockValueTransitionArray_small(Map<String, List<Stock>> unnormalizedSymbolToStockSequence, List<Long> listOfAllDates) {
        //normalize the sequence
        Map<String, double[]> toReturn = new HashMap<String, double[]>();

        Map<String, List<Stock>> symbolToStockSequence = getNormalizedSymbolToStockList(unnormalizedSymbolToStockSequence, listOfAllDates);

        for(String symbol : symbolToStockSequence.keySet()) {
            List<Double> valuesList = new LinkedList<Double>();
            for(int i=0; i<symbolToStockSequence.get(symbol).size()-1; i++) {
                Stock curStock = symbolToStockSequence.get(symbol).get(i);
                Stock nextStock = symbolToStockSequence.get(symbol).get(i+1);
                if(curStock != null && nextStock != null) {
                    Stock.ValueTransitionObject vto = curStock.getNextValueTransition();
                    valuesList.add(vto.getAveragePercentValueDiff());
                } else {
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

    public static Map<String, List<Stock>> getNormalizedSymbolToStockList(Map<String, List<Stock>> symbolToStockSequenceList, List<Long> listOfAllDates) {
        Map<String, List<Stock>> symbolToStockSequence_normalized = new HashMap<String, List<Stock>>();

        // insert
        for(String symbol : symbolToStockSequenceList.keySet()) {
            int curStockIndex = 0;   //pointer to current stock to examine
            int curDateIndex = 0;
            List<Stock> stockSequence = symbolToStockSequenceList.get(symbol);
            List<Stock> normalizedList = new LinkedList<Stock>();

            //keep incrementing date pointer until matching date to cur stock is found
            while(curDateIndex < listOfAllDates.size()) {
                if(curStockIndex == stockSequence.size()) {
                    break;
                }
                Stock curStock = stockSequence.get(curStockIndex);
                long curDate = listOfAllDates.get(curDateIndex);
                if(curDate == curStock.getDate()) {
                    //clone the stock, and add the clone
                    Stock clone = curStock.clone();
                    normalizedList.add(clone);
                    curDateIndex++;
                    curStockIndex++;
                } else {
                    normalizedList.add(null);
                    curDateIndex++;
                }
            }
            if(normalizedList.size() < listOfAllDates.size()) {
                int nullEltsToAdd = listOfAllDates.size() - normalizedList.size();
                for(int i=0; i<nullEltsToAdd; i++) {
                    normalizedList.add(null);
                }
            }
            symbolToStockSequence_normalized.put(symbol, normalizedList);
        }

        //clear the value transitions, and add new value transition data
        for(String symbol : symbolToStockSequence_normalized.keySet()) {
            StockReader.clearValueTransitionsFromStockList(symbolToStockSequence_normalized.get(symbol));
        }
        StockReader.generateValueTransitions(symbolToStockSequence_normalized);

        return symbolToStockSequence_normalized;
    }

    public static Map<String, double[]> getNormalizedSymbolToTransitionValuesDoubleArray(Map<String, List<Stock>> symbolToTransitionValuesObject_normalized) {
        return StockReader.getSymbolToValueTransitionsAsDoubleArray(symbolToTransitionValuesObject_normalized);
    }



    private static Stock parseStock(List<String> entry) throws Exception {
        return new Stock(entry);
    }

}



