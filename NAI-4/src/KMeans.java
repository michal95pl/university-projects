import java.util.*;

public class KMeans {

    private static class KMeansData{
        public double[] x;
        public int groupNumber;

        KMeansData(double[] x) {
            this.x = x;
            this.groupNumber = -1;
        }
        KMeansData(double[] x, int group) {
            this.x = x;
            this.groupNumber = group;
        }

    }

    private final int k;
    private final double[][] centroids;
    KMeansData[] data;

    private double[][] initializeCentroids(double[][] x, int k) {

        double[][] centroids = new double[k][x[0].length];

        for (int i=0; i < k; i++) {
            for (int j=0; j < x[0].length; j++) {
                double coordinate = Math.random() * 5;
                coordinate = Math.round(coordinate * 5.0) / 5.0;
                centroids[i][j] = coordinate;
            }
        }

        return centroids;
    }

    KMeans(int k, double[][] x) {
        this.k = k;
        this.data = new KMeansData[x.length];

        // initialize data
        for (int i=0; i < data.length; i++) {
            double coordinate = Math.random() * (k-1);
            coordinate = Math.round(coordinate * (double)(k-1)) / (double)(k-1);
            data[i] = new KMeansData(x[i], (int)coordinate);
        }

        centroids = initializeCentroids(x, k);

        //assignVectorToCentroid();
    }

    private void setCentroids() {

        // clear centroids
        for (double[] centroid : centroids)
            Arrays.fill(centroid, 0.0);


        int[] centroidDividers = new int[centroids.length];
        Arrays.fill(centroidDividers, 0);


        for (KMeansData datum : data) {
            double[] dataVector = datum.x;

            centroidDividers[datum.groupNumber] += 1;

            for (int j = 0; j < dataVector.length; j++)
                centroids[datum.groupNumber][j] += datum.x[j];
        }

        for (int i=0; i < centroids.length; i++) {
            for (int j=0; j < centroids[i].length; j++)
                centroids[i][j] /= centroidDividers[i];
        }

    }

    private int getIndexNearestCentroid(double[] data) {

        double minDistance = Vector.euclideanDistance(data, centroids[0]);
        int indexCentroidMinDistance = 0;

        for (int i=1; i < centroids.length; i++) {
            double distance = Vector.euclideanDistance(data, centroids[i]);

            if (distance < minDistance) {
                minDistance = distance;
                indexCentroidMinDistance = i;
            }
        }

        return indexCentroidMinDistance;
    }

    private void assignVectorToCentroid() {
        for (KMeansData datum : data)
            datum.groupNumber = getIndexNearestCentroid(datum.x);
    }

    private double getSumDistanceCentroidInClusters() {

        double distance = 0;

        for (KMeansData temp : data) {
            distance += Vector.euclideanDistance(temp.x, centroids[temp.groupNumber]);
        }

        return distance;
    }


    public void fit() {

        boolean run = true;

        double distance = getSumDistanceCentroidInClusters();
        System.out.println("Distance: " + distance);

        while(run) {
            run = false;
            setCentroids();
            assignVectorToCentroid();

            double tempDistance = getSumDistanceCentroidInClusters();

            if (distance != tempDistance)
                run = true;

            distance = tempDistance;
            System.out.println("Distance: " + distance);
        }
    }

    private double[] getClustersEntropy(double[][] x, String[] y) {

        double[] entropy = new double[k];
        Map<String, Integer>[] counter = new HashMap[k];

        for (int i=0; i < k; i++)
            counter[i] = new HashMap<>();

        Arrays.fill(entropy, 0);

        for (int i=0; i < x.length; i++) {
            int index = getIndexNearestCentroid(x[i]);
            String key = y[i];

            if (counter[index].containsKey(key))
                counter[index].put(key, counter[index].get(key) + 1);
            else
                counter[index].put(key, 1);
        }

        for (int i=0; i < k; i++) {
            int sum = 0;
            for (String key : counter[i].keySet())
                sum += counter[i].get(key);

            for (String key : counter[i].keySet()) {
                double p = (double)counter[i].get(key) / sum;
                entropy[i] += p * Math.log(p) / Math.log(2);
            }

            entropy[i] = -entropy[i];
        }

        return entropy;
    }

    public void showClusters(double[][] x, String[] y) {

        double[] entropy = getClustersEntropy(x, y);

        for (int indexCluster = 0; indexCluster < k; indexCluster++) {
            System.out.println("\nCluster: " + indexCluster);

            for (int i=0; i < x.length; i++)
                if (getIndexNearestCentroid(x[i]) == indexCluster)
                    System.out.println(y[i]);

            System.out.println("Entropy: " + entropy[indexCluster]);
        }
    }
}
