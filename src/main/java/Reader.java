import java.io.InputStream;
import java.util.Set;

/**
 * Created by caneba on 12/20/15.
 */
public class Reader {

    public static void main(String[] args) {

        System.out.println("Starting Market Player Reader");

        try {
            learn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Market Player Reader Finished");

    }

    public static void learn() throws Exception {
        StockData sd = StockReader.convertResourcesToStockData();
        train(sd);
    }
    public static void learn(Set<InputStream> stockDataInput) throws Exception {
        StockData sd = StockReader.convertResourcesToStockData(stockDataInput);
        train(sd);
    }

    private static void train(StockData stockData) {
        Trainer trainer = new Trainer(stockData);
        trainer.train();
    }

}
