import javafx.util.Pair;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import java.util.*;

/**
 * Created by caneba on 12/26/15.
 */
public class IndividualStockAnalyzer {

    StockData stockData;

    public IndividualStockAnalyzer(StockData stockData) {
        this.stockData = stockData;
    }

    public IndividualStockPerformanceMappings analyze() throws Exception {

        //calculate beta on close

        //TODO: continue this


        IndividualStockPerformanceMappings ispm = new IndividualStockPerformanceMappings();
        ispm.generateMappings(stockData);

        return ispm;

    }

    public class IndividualStockPerformanceMappings {

        private Map<String, Double> symbolToBeta;
        private List<String> sortedBetaList;
        private Map<String, Double> symbolToAlpha;
        private List<String> sortedAlphaList;

        public IndividualStockPerformanceMappings() {
            this.symbolToBeta = new HashMap<>();
            this.sortedBetaList = new LinkedList<>();
            this.symbolToAlpha = new HashMap<>();
            this.sortedAlphaList = new LinkedList<>();
        }

        public List<String> maximizeVolatilityAndReturns() {

            List<String> toReturn = new LinkedList<>();

//            Map<String, Double> symbolToSum = new HashMap<String, Double>();
//            Map<Double, String> sumToSymbol = new HashMap<Double, String>();

            List<Double> sums = new LinkedList<>();


            for(String symbol : this.symbolToBeta.keySet()) {
                if(this.symbolToAlpha.containsKey(symbol)) {
                    Double sum = this.symbolToBeta.get(symbol) + this.symbolToAlpha.get(symbol);

                    int insertAt = Utilities.getIndexOfCorrectInsertOfDoubleValueInList(sums, sum);

                    sums.add(insertAt, sum);

                    String toAdd = symbol + " -> " + sum;
                    toReturn.add(insertAt, toAdd);


//                    symbolToSum.put(symbol, sum);
//                    sumToSymbol.put(sum, symbol);
                }
            }

//            Double[] sums = Utilities.convertDoubleSetToArray(sumToSymbol.keySet());
//
//            Arrays.sort(sums);


//            for(int i=0; i<sums.length; i++) {
//                String symbol = sumToSymbol.get(sums[i]);
//                String toAdd = symbol + " -> " + sums[i].toString();
//                toReturn.add(toAdd);
//            }

            return toReturn;

        }

        private void generateMappings(StockData stockData) throws Exception {
            this.generateBeta(stockData);
            this.generateAlpha(stockData);

        }

        private void generateAlpha(StockData stockData) {
            //TODO: this
        }
        private void generateBeta(StockData stockData) throws Exception {
            //get the mapping from symbol to closing value sequences
            Map<String, Map<Long, Double>> symbolToDateToClosingValueMap = StockReader.getSymbolToDateToClosingValueMap(stockData);

            Map<Double, String> betaToSymbol = new HashMap<>();
            Map<Double, String> alphaToSymbol = new HashMap<>();

            for(String symbol : symbolToDateToClosingValueMap.keySet()) {
                Map<Long, Double> dateToClosingValueMap = symbolToDateToClosingValueMap.get(symbol);

                //calculate beta

                //1. create two arrays/lists, one to represent the value change for the benchmark, and to represent change for asset

                List<Double> benchmark = new LinkedList<>();
                List<Double> asset = new LinkedList<>();

                //for(Pair<Long, Double> dateAndValue : stockData.getNASDAQDateAndValue()) {
                for(Pair<Long, Double> dateAndValue : stockData.getSP500DateAndValue()) {
                    long date = dateAndValue.getKey();
                    if(dateToClosingValueMap.containsKey(date)) {
                        if(dateAndValue.getValue() != null) {
                            benchmark.add(dateAndValue.getValue());
                            asset.add(dateToClosingValueMap.get(date));
                        }
                    }
                }

                //2. both lists are now populated.  check to make sure there are no errors.
                if(benchmark.size() != asset.size()) {
                    throw new Exception("benchmark and asset comparisons do not match");
                }

                //create new array, with percentage differences in growth for each.

                List<Double> benchmarkPercentageDifferences = new LinkedList<>();
                List<Double> assetPercentageDifferences = new LinkedList<>();

                int sizeOfLists = benchmark.size();

                for(int i=1; i<sizeOfLists; i++) {
                    //calculate the percentage diffs
                    double benchmarkDiff = (benchmark.get(i) - benchmark.get(i-1))/benchmark.get(i-1);
                    double assetDiff = (asset.get(i) - asset.get(i-1))/asset.get(i-1);

                    benchmarkPercentageDifferences.add(benchmarkDiff);
                    assetPercentageDifferences.add(assetDiff);
                }



                //now that both lists are populated, calculate covariance
                Covariance covariance = new Covariance();
                Variance variance = new Variance();

                double[] benchmarkArray = Utilities.convertDoubleListToArray(benchmarkPercentageDifferences);
                double[] assetArray = Utilities.convertDoubleListToArray(assetPercentageDifferences);

                double covarianceValue = covariance.covariance(benchmarkArray, assetArray);
                double varianceValue = variance.evaluate(benchmarkArray);

                Double beta = covarianceValue/varianceValue;

                betaToSymbol.put(beta, symbol);
                this.symbolToBeta.put(symbol, beta);

                //calculate alpha now that you know beta
                //alpha = returnsOfPortfolio - (riskFreeRate + beta(marketReturns - riskFreeRate))

                double returnsOfPortfolio = Utilities.getAverageOfDoubleArray(assetArray);
                double riskFreeRate = Constants.riskFreeRate;
                double marketReturns = Utilities.getAverageOfDoubleArray(benchmarkArray);

                Double alpha = returnsOfPortfolio - (riskFreeRate + beta * (marketReturns - riskFreeRate));
                this.symbolToAlpha.put(symbol, alpha);
                alphaToSymbol.put(alpha, symbol);
                //System.out.println(symbol + " : " + covarianceValue + " / " + varianceValue + " = " + covarianceValue/varianceValue);

            }

            Double[] toSort =  new Double[betaToSymbol.keySet().size()];
            int index = 0;
            for(Double beta : betaToSymbol.keySet()) {
                toSort[index] = beta;
                index++;
            }

            Arrays.sort(toSort);

            for(int i=0; i<toSort.length; i++) {
                String toAdd = "";
                toAdd += betaToSymbol.get(toSort[i]);
                toAdd += " : ";
                toAdd += toSort[i];
                this.sortedBetaList.add(toAdd);
            }

            Double[] alphaToSort = new Double[alphaToSymbol.keySet().size()];
            index = 0;
            for(Double alpha : alphaToSymbol.keySet()) {
                alphaToSort[index] = alpha;
                index++;
            }

            Arrays.sort(alphaToSort);
            for(int i=0; i<alphaToSort.length; i++) {
                String toAdd = "";
                toAdd += alphaToSymbol.get(alphaToSort[i]);
                toAdd += " : ";
                toAdd += alphaToSort[i];
                this.sortedAlphaList.add(toAdd);
            }
        }

        public Map<String, Double> getSymbolToBeta() {
            return this.symbolToBeta;
        }
        public List<String> getSortedBetaList() {
            return this.sortedBetaList;
        }
        public Map<String, Double> getSymbolToAlpha() {
            return this.symbolToAlpha;
        }
        public List<String> getSortedAlphaList() {
            return this.sortedAlphaList;
        }
    }
}
