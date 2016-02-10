import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caneba on 12/23/15.
 */
public class MarketAnalyzer {

    private StockData stockData;
    private MarketDataAnalysis marketDataAnalysis;

    public MarketAnalyzer(StockData stockData) {
        this.stockData = stockData;
    }

    public MarketDataAnalysis analyze() {
        this.marketDataAnalysis = new MarketDataAnalysis();
        return this.marketDataAnalysis;
    }

    //generate date to average change over day percentage
    private Map<Long, Double> generateDateToAverageStockValue() {
        Map<Long, Double> dateToAverageStockValue = new HashMap<Long, Double>();

        //get the normalized map from symbol to stock values
        Map<String, List<Stock>> normalizedSymbolToStockList = StockReader.getNormalizedSymbolToStockList(stockData.getSymbolToStockSequenceList(), stockData.getListOfAllDates());

        for(long date : stockData.getListOfAllDates()) {
            double sumOfAllStocks = 0;
            for(String symbol : stockData.getSymbolToStockSequenceList().keySet()) {
                for(Stock stock : stockData.getSymbolToStockSequenceList().get(symbol)) {
                    if(date == stock.getDate()) {
                        sumOfAllStocks += stock.getValues().getAverageOfAllValues();
                        break;
                    }
                }
            }
            double average = sumOfAllStocks/stockData.getListOfAllDates().size();
            dateToAverageStockValue.put(date, average);
        }
        return dateToAverageStockValue;
    }

    public class MarketDataAnalysis {

        private Map<Long, Double> dateToAverageStockValue;

        public MarketDataAnalysis() {
            this.dateToAverageStockValue = generateDateToAverageStockValue();
        }
        public Map<Long, Double> getDateToAverageStockValue() { return this.dateToAverageStockValue; }


    }

}
