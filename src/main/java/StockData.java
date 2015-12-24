import java.util.List;
import java.util.Map;

/**
 * Created by caneba on 12/20/15.
 */
public class StockData {

    private Map<String, List<Stock>> symbolToStockSequenceList;
    private Map<String, double[]> symbolToValueTransitionArray;

    public Map<String, List<Stock>> getSymbolToStockSequenceList() {
        return symbolToStockSequenceList;
    }
    public Map<String, double[]> getSymbolToValueTransitionArray() {
        return symbolToValueTransitionArray;
    }

    public StockData(Map<String, List<Stock>> symbolToStockSequenceList, Map<String, double[]> symbolToValueTransitionArray) {
        this.symbolToStockSequenceList = symbolToStockSequenceList;
        this.symbolToValueTransitionArray = symbolToValueTransitionArray;
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

}
