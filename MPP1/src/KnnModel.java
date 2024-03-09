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

        ModelData[] modelData = new ModelData[this.x.length];

        for (int i=0; i < modelData.length; i++)
            modelData[i] = new ModelData();

        // for all learning data
        for (int i=0; i < modelData.length; i++) {

            for (int j=0; j < this.x[i].length; j++) {
                modelData[i].x += Math.pow((x[j] - this.x[i][j]), 2);
            }


            modelData[i].x = Math.sqrt(modelData[i].x);
            modelData[i].y = this.y[i];

            //Arrays.sort(modelData);

        }

        // todo: sortowanie
        for (int k = 0; k < modelData.length; k++) {
            for (int n = k + 1; n < modelData.length; n++) {
                if (modelData[k].x > modelData[n].x) {
                    ModelData temp = modelData[n];
                    modelData[n] = modelData[k];
                    modelData[k] = temp;
                }
            }
        }


//        for (int i=0; i < this.x.length; i++) {
//            System.out.print(Arrays.stream(this.x[i]).toList());
//            System.out.print(" ");
//            System.out.print(this.y[i]);
//            System.out.print("     ");
//            System.out.println(modelData[i]);
//        }

        Map<String, Integer> mapResults = new HashMap<>();

        for (int i=0 ; i < this.k; i++) {
            if (!mapResults.containsKey(modelData[i].y)) {
                mapResults.put(modelData[i].y, 0);
            }
            mapResults.put(modelData[i].y, mapResults.get(modelData[i].y) + 1);
        }

        Map.Entry<String, Integer> max = Collections.max(mapResults.entrySet(), Comparator.comparingInt(Map.Entry::getValue));

        return max.getKey();
    }

}
