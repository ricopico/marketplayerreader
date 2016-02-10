/**
 * Created by caneba on 12/21/15.
 */
public class Trainer {

    private StockData stockData;

    public Trainer(StockData sd) {
        this.stockData = sd;
    }

    public void train() throws Exception {
        //clustering
//        Clusterer clusterer = new Clusterer(this.stockData);
//        clusterer.cluster();
//
//        //try to gather information about the market as a whole
//        MarketAnalyzer marketAnalyzer = new MarketAnalyzer(this.stockData);
//        MarketAnalyzer.MarketDataAnalysis marketDataAnalysis = marketAnalyzer.analyze();
//
//        String marketDataAnalysisString = "";
//        for(Long date : marketDataAnalysis.getDateToAverageStockValue().keySet()) {
//            double average = marketDataAnalysis.getDateToAverageStockValue().get(date);
//            marketDataAnalysisString += date;
//            marketDataAnalysisString += " => ";
//            marketDataAnalysisString += average;
//            marketDataAnalysisString += "\n";
//        }
//        Utilities.writeToFile(Constants.marketAnalysisFile, marketDataAnalysisString);

        IndividualStockAnalyzer individualStockAnalyzer = new IndividualStockAnalyzer(this.stockData);
        IndividualStockAnalyzer.IndividualStockPerformanceMappings ispm = individualStockAnalyzer.analyze();

        String betaString = "";
        for(String beta : ispm.getSortedBetaList()) {
            betaString += beta;
            betaString += "\n";
        }
        Utilities.writeToFile(Constants.betaSort, betaString);

        String alphaString = "";
        for(String alpha : ispm.getSortedAlphaList()) {
            alphaString += alpha;
            alphaString += "\n";
        }
        Utilities.writeToFile(Constants.alphaSort, alphaString);

        String maxAlphABetaString = "";
        for(String max : ispm.maximizeVolatilityAndReturns()) {
            maxAlphABetaString += max;
            maxAlphABetaString += "\n";
        }
        Utilities.writeToFile(Constants.maxAlphaBetaSort, maxAlphABetaString);

    }

}
