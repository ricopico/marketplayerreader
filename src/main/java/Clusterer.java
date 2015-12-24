import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import java.util.*;

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
        // todo: this

        Map<String, List<Stock>> symbolToStockSequence_normalized = new HashMap<String, List<Stock>>();
        Map<String, double[]> symbolToTransitionValuesArray_normalized = new HashMap<String, double[]>();

        // get list of all dates
        Set<Long> setOfAllDates = new HashSet<Long>();
        List<Long> listOfAllDates = new LinkedList<Long>();
        for(String symbol : stockData.getSymbolToStockSequenceList().keySet()) {
            List<Stock> stockList = stockData.getSymbolToStockSequenceList().get(symbol);
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

        // insert
        for(String symbol : stockData.getSymbolToStockSequenceList().keySet()) {
            int curStockIndex = 0;   //pointer to current stock to examine
            int curDateIndex = 0;
            List<Stock> stockSequence = stockData.getSymbolToStockSequenceList().get(symbol);
            List<Stock> normalizedList = new LinkedList<Stock>();

            //keep incrementing date pointer until matching date to cur stock is found
            while(curDateIndex < listOfAllDates.size()) {
                if(curStockIndex == stockSequence.size()) {
                    break;
                }
                Stock curStock = stockSequence.get(curStockIndex);
                long curDate = listOfAllDates.get(curDateIndex);
                if(curDate == curStock.getDate()) {
                    normalizedList.add(curStock);
                    curDateIndex++;
                    curStockIndex++;
                } else {
                    normalizedList.add(null);
                    curDateIndex++;
                }
            }
            if(normalizedList.size() < listOfAllDates.size()) {
                int nullEltsToAdd = listOfAllDates.size() - normalizedList.size();
                for(int i=0; i<nullEltsToAdd; i++) {
                    normalizedList.add(null);
                }
            }
            symbolToStockSequence_normalized.put(symbol, normalizedList);
        }

        //clear the value transitions, and add new value transition data
        for(String symbol : symbolToStockSequence_normalized.keySet()) {
            StockReader.clearValueTransitionsFromStockList(symbolToStockSequence_normalized.get(symbol));
        }
        StockReader.generateValueTransitions(symbolToStockSequence_normalized);

        // do the clustering using the normalized list
        Map<String, double[]> symbolToValuesArray = StockReader.getSymbolToValueTransitionsAsDoubleArray(symbolToStockSequence_normalized);

        Map<String, Instance> symbolToInstance = generateSymbolToInstanceMapping(symbolToValuesArray);
        Map<Instance, String> instanceToSymbol = generateInstanceToSymbolMapping(symbolToInstance);

        //create the data set
        Dataset dataset = new DefaultDataset();
        for(String symbol : symbolToInstance.keySet()) {
            dataset.add(symbolToInstance.get(symbol));
        }

        System.out.println("clusterer activated");
        //activate clusterer
        net.sf.javaml.clustering.Clusterer km = new KMeans(Constants.k, Constants.iterations);
        Dataset[] clusters = km.cluster(dataset);

        System.out.println("clusterer done");

        //write the clusters to file
        String clusterString = "";
        for(int j=0; j<clusters.length; j++) {
            Dataset cluster = clusters[j];
            clusterString += "CLUSTER " + j + " : \n";
            for(int i2=0; i2<cluster.size(); i2++) {
                Instance instance = cluster.get(i2);
                String symbol = instanceToSymbol.get(instance);
                clusterString += symbol + " == ";
                clusterString += instance;
                clusterString += "\n";
            }
        }
        Utilities.writeToFile(Constants.outputFile, clusterString);

    }
    private Map<Instance, String> generateInstanceToSymbolMapping(Map<String, Instance> symbolToInstance) {
        Map<Instance, String> toReturn = new HashMap<Instance, String>();

        for(String symbol : symbolToInstance.keySet()) {
            toReturn.put(symbolToInstance.get(symbol), symbol);
        }

        return toReturn;
    }
    private Map<String, Instance> generateSymbolToInstanceMapping(Map<String, double[]> symbolToValuesArray) {
        Map<String, Instance> toReturn = new HashMap<String, Instance>();

        for(String symbol : symbolToValuesArray.keySet()) {
            double[] array = symbolToValuesArray.get(symbol);
            Instance instance = new DenseInstance(array);
            toReturn.put(symbol, instance);
        }
        return toReturn;
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
