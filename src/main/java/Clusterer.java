import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by caneba on 12/21/15.
 */
public class Clusterer {

    private StockData stockData;
    private Set<StockCluster> clusters;

    public Clusterer(StockData stockData) {
        this.stockData = stockData;
    }

    public void cluster() {
        //todo: this
    }

    public Set<StockCluster> getClusters() {
        return this.clusters;
    }

    public class StockCluster {
        private Set<Map<String, List<Stock>>> symbolToStockSequenceGrouping;
        public StockCluster(Set<Map<String, List<Stock>>> symbolToStockSequenceGrouping) {
            this.symbolToStockSequenceGrouping = symbolToStockSequenceGrouping;
        }
        public Set<Map<String, List<Stock>>> getSymbolToStockSequenceGrouping() {
            return symbolToStockSequenceGrouping;
        }
    }
}
