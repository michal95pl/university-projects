import java.util.*;

public class KnnModel {


    private Double[][] x; // attributes
    private String[] y; // decision attributes
    private int k;

    KnnModel(Double[][] x, String[] y, int k) {
        this.x = x;
        this.y = y;
        this.k = k;
    }

    String predict(Double[] x) {

        PriorityQueue<ModelData> modelDataQueue = new PriorityQueue<>(Comparator.comparingDouble(o -> o.x));

        for (int i=0; i < this.x.length; i++) {

            double temp_x = 0;

            for (int j=0; j < this.x[i].length; j++) {
                temp_x += Math.pow((x[j] - this.x[i][j]), 2);
            }

            temp_x = Math.sqrt(temp_x);
            modelDataQueue.add(new ModelData(temp_x, this.y[i]));
        }


        Map<String, Integer> mapResults = new HashMap<>();

        for (int i=0; i < this.k && i < modelDataQueue.size(); i++) {

            ModelData temp = modelDataQueue.remove();
            mapResults.compute(temp.y, (key, val) -> (val == null)? 1 : val + 1);
        }

        return Collections.max(mapResults.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    double testPrecision(Double[][] testX, String[] testY) {

        int numberTests = testY.length;
        int success = 0;

        for (int i=0; i < numberTests; i++) {
            String s = this.predict(testX[i]);

            if (s.equals(testY[i]))
                success++;
        }
        return Math.round(success / (double)numberTests * 10000) / 100.;
    }

}
