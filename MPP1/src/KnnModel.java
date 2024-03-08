public class KnnModel {

    private class ModelData
        implements Comparable<ModelData> {

        private double x;
        private String y;

        ModelData(double x, String y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(ModelData o) {
            return Double.compare(this.x, o.x);
        }
    }

    private double[][] x;
    private String[] y;
    private int k;

    KnnModel(double[][] x, String[] y, int k) {
        this.x = x;
        this.y = y;
        this.k = k;
    }

    void predict(double[][] x) {


//        for (int i=0; i < distance.length; i++) {
//
//            for (int j=0; j < this.x[0].length; j++)
//                distance[i] += Math.pow((x[i][j] - this.x[i][j]), 2);
//            distance[i] = Math.sqrt(distance[i]);
//        }



    }

}
