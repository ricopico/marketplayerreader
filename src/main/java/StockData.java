import java.util.List;
import java.util.Map;

/**
 * Created by caneba on 12/20/15.
 */
public class StockData {

    private Map<String, List<Stock>> symbolToStockSequenceList;

    public Map<String, List<Stock>> getSymbolToStockSequenceList() {
        return symbolToStockSequenceList;
    }

    public StockData(Map<String, List<Stock>> symbolToStockSequenceList) {
        this.symbolToStockSequenceList = symbolToStockSequenceList;
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

}
