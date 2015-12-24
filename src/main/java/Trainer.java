/**
 * Created by caneba on 12/21/15.
 */
public class Trainer {

    private StockData stockData;

    public Trainer(StockData sd) {
        this.stockData = sd;
    }

    public void train() {
        //clustering
        Clusterer clusterer = new Clusterer(this.stockData);
        clusterer.cluster();


    }

}
