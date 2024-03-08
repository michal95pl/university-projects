import java.util.Arrays;

public class KnnModel {


    private double[][] x;
    private String[] y;
    private int k;

    KnnModel(double[][] x, String[] y, int k) {
        this.x = x;
        this.y = y;
        this.k = k;
    }

    void predict(double[] x) {

        ModelData[] modelData = new ModelData[x.length];

        for (int i=0; i < modelData.length; i++) {

            for (int j=0; j < this.x[i].length; j++)
                modelData[i].x += Math.pow((x[j] - this.x[i][j]), 2);

            modelData[i].x = Math.sqrt(modelData[i].x);

            Arrays.sort(modelData);

            for (ModelData data : modelData)
                System.out.println(data);
        }



    }

}
