import javafx.util.Pair;

import java.util.*;

/**
 * Created by caneba on 12/20/15.
 */
public class StockData {

    private Map<String, List<Stock>> symbolToStockSequenceList;
    private Map<String, double[]> symbolToValueTransitionArray;
    private List<Pair<Long, Double>> SP500DateAndValue;
    private List<Pair<Long, Double>> NASDAQDateAndValue;
    private List<Long> allDates;

    public StockData(Map<String, List<Stock>> symbolToStockSequenceList, Map<String, double[]> symbolToValueTransitionArray, List<Pair<Long, Double>> SP500Data, List<Pair<Long, Double>> NASDAQData) {
        this.symbolToStockSequenceList = symbolToStockSequenceList;
        this.symbolToValueTransitionArray = symbolToValueTransitionArray;
        this.allDates = generateAllDates();
        this.SP500DateAndValue = SP500Data;
        this.NASDAQDateAndValue = NASDAQData;
    }

    public List<Pair<Long, Double>> getSP500DateAndValue() {
        return this.SP500DateAndValue;
    }
    public List<Pair<Long, Double>> getNASDAQDateAndValue() {
        return this.NASDAQDateAndValue;
    }

    public List<Stock> getStockSequenceForSymbol(String symbol) throws Exception {
        if(this.symbolToStockSequenceList == null) {
            throw new Exception("symbol to stock sequence list not initialized");
        }
        if(!this.symbolToStockSequenceList.containsKey(symbol)) {
            throw new Exception("symbol not found: " + symbol);
        }
        return this.symbolToStockSequenceList.get(symbol);
    }
    public void plotStockPerformance(String symbol) {
        Utilities.plotStockSequence(this.symbolToStockSequenceList.get(symbol), symbol);
    }

    public List<Long> getListOfAllDates() { return this.allDates; }
    public Map<String, List<Stock>> getSymbolToStockSequenceList() {
        return symbolToStockSequenceList;
    }
    public Map<String, double[]> getSymbolToValueTransitionArray() {
        return symbolToValueTransitionArray;
    }

    private List<Long> generateAllDates() {
        // get list of all dates
        Set<Long> setOfAllDates = new HashSet<Long>();
        List<Long> listOfAllDates = new LinkedList<Long>();
        for(String symbol : getSymbolToStockSequenceList().keySet()) {
            List<Stock> stockList = getSymbolToStockSequenceList().get(symbol);
            for(Stock stock : stockList) {
                setOfAllDates.add(stock.getDate());
            }
        }
        listOfAllDates.addAll(setOfAllDates);
        Long[] arrayOfAllDates = new Long[listOfAllDates.size()];
        for(int i=0; i<listOfAllDates.size(); i++) {
            arrayOfAllDates[i] = listOfAllDates.get(i);
        }
        Arrays.sort(arrayOfAllDates);
        listOfAllDates.clear();
        for(int i=0; i<arrayOfAllDates.length; i++) {
            listOfAllDates.add(arrayOfAllDates[i]);
        }
        return listOfAllDates;
    }


}
