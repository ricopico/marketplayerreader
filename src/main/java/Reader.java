import java.io.InputStream;
import java.util.Set;

/**
 * Created by caneba on 12/20/15.
 */
public class Reader {

    public static void main(String[] args) {

        try {
            learn();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void learn() throws Exception {
        StockData sd = StockReader.convertResourcesToStockData();
    }
    public static void learn(Set<InputStream> stockDataInput) throws Exception {
        StockData sd = StockReader.convertResourcesToStockData(stockDataInput);
    }

}
