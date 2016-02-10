import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
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

        Map<String, List<Stock>> symbolToStockSequence_normalized = StockReader.getNormalizedSymbolToStockList(stockData.getSymbolToStockSequenceList(), stockData.getListOfAllDates());
        Map<String, double[]> symbolToTransitionValuesArray_normalized = StockReader.getNormalizedSymbolToTransitionValuesDoubleArray(symbolToStockSequence_normalized);
        List<Long> listOfAllDates = stockData.getListOfAllDates();

        // do the clustering using the normalized list
        //Map<String, double[]> symbolToValuesArray = StockReader.getSymbolToValueTransitionsAsDoubleArray(symbolToStockSequence_normalized);
        Map<String, double[]> symbolToValuesArray = StockReader.generateSymbolToStockValueTransitionArray_small(stockData.getSymbolToStockSequenceList(), stockData.getListOfAllDates());


        Map<String, Instance> symbolToInstance = generateSymbolToInstanceMapping(symbolToValuesArray);
        Map<Instance, String> instanceToSymbol = generateInstanceToSymbolMapping(symbolToInstance);

        //create the data set
        Dataset dataset = new DefaultDataset();
        for(String symbol : symbolToInstance.keySet()) {
            if(Constants.numberOfSymbolsToTrack<dataset.size()) {
                break;
            }
            dataset.add(symbolToInstance.get(symbol));
        }

        System.out.println("clusterer activated");
        //activate clusterer
        net.sf.javaml.clustering.Clusterer km = new KMeans(Constants.k, Constants.iterations);
        Dataset[] clusters = km.cluster(dataset);

        double score = evaluateClusters(clusters);

        System.out.println("clusterer done");

        //write the clusters to file
        String clusterString = "CLUSTERING SCORE : " + score + "\n----------------------------\n";
        for(int j=0; j<clusters.length; j++) {
            Dataset cluster = clusters[j];
            clusterString += "CLUSTER " + j + "; \n";
            int sizeOfCluster = cluster.size();
            clusterString += "CLUSTER SIZE: " + sizeOfCluster + ";\n";

//            for(int i2=0; i2<cluster.size(); i2++) {
//                Instance instance = cluster.get(i2);
//                String symbol = instanceToSymbol.get(instance);
//                clusterString += symbol + " == ";
//                clusterString += instance;
//                clusterString += "\n";
//            }
        }
        Utilities.writeToFile(Constants.outputFile, clusterString);

    }
    private double evaluateClusters(Dataset[] clusters) {
        ClusterEvaluation sse = new SumOfSquaredErrors();
        double score = sse.score(clusters);

        return score;
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
