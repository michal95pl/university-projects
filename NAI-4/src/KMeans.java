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
                double coordinate = Math.random() * 6;
                coordinate = Math.round(coordinate * 6.0) / 6.0;
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

    public void getEntropy(double[][] x, String[] y) {
        Map<String, List<Integer>> group = new HashMap<>();

        // predict group for decision attribute
        for (int i=0; i < x.length; i++) {
            if (!group.containsKey(y[i]))
                group.put(y[i], new ArrayList<>());

            group.get(y[i]).add(predictGroup(x[i]));
        }

        for (String key : group.keySet()) {
            List<Integer> temp = group.get(key);
            double[] m = new double[temp.size()];

            for (int i=0; i < temp.size(); i++) {
                m[i] = temp.get(i);
            }

            System.out.println(key + " " + Vector.entropy(m));
        }
    }

    public void showClusters(double[][] x, String[] y) {

        for (int indexCluster = 0; indexCluster < k; indexCluster++) {
            System.out.println("\nCluster: " + indexCluster);

            for (int i=0; i < x.length; i++)
                if (getIndexNearestCentroid(x[i]) == indexCluster)
                    System.out.println(y[i]);
        }
    }

    public int predictGroup(double[] x) {
        return getIndexNearestCentroid(x);
    }

}
