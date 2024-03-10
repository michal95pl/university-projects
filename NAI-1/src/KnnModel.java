import java.util.*;
import java.util.zip.DataFormatException;

public class KnnModel {


    private final Double[][] x; // attributes
    private final String[] y; // decision attributes
    private int k;

    KnnModel(Double[][] x, String[] y, int k) throws DataFormatException {

        if (!validateArray(x))
            throw new DataFormatException("x array has different row length");

        this.x = x;
        this.y = y;
        this.k = k;
    }

    // all rows have the same length
    private <T> boolean validateArray(T[][] array) {
        for (T[] temp : array)
            if (temp.length != array[0].length)
                return false;
        return true;
    }

    public String predict(Double[] x) throws DataFormatException {

        if (x.length != this.x[0].length)
            throw new DataFormatException("predict data has different row number in order to learn data");

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

    public int testPrecision(Double[][] testX, String[] testY) throws DataFormatException {

        int success = 0;

        for (int i=0; i < testY.length; i++) {
            String s = this.predict(testX[i]);

            if (s.equals(testY[i]))
                success++;
        }

        return success;
    }

}
