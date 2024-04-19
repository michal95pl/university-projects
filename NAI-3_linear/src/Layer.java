import java.util.HashMap;
import java.util.Map;

public class Layer {

    private final Map<String, Perceptron> perceptrons;

    private int[] stringToClassificationArray(String[] y, String key) {

        int[] temp = new int[y.length];

        for (int i=0; i < temp.length; i++)
            temp[i] = (y[i].equals(key))? 1 : -1;

        return temp;
    }

    private double[][] x;
    private String [] y;

    Layer(double[][] x, String[] y, double learningRate) {

        this.x = x;
        this.y = y;

        perceptrons = new HashMap<>();

        for (String s : y)
            if (!perceptrons.containsKey(s))
                perceptrons.put(s, new Perceptron(x, stringToClassificationArray(y, s), learningRate));
    }

    public void fit(double maxError) {
        int counter = 0;
        boolean errorFlag = true;

        while(checkAccuracy(x, y) != 100 || errorFlag) {

            errorFlag = false;
            for (int i=0; i < y.length; i++) {

                double error = 0;
                for (String key : perceptrons.keySet()) {
                    error += perceptrons.get(key).fit(i);
                }

                error /= 2;

                if (error > maxError)
                    errorFlag = true;
            }

            counter++;
        }
        System.out.println("epochs: " + counter);
    }

    public String predict(double[] x) {

        if (perceptrons.isEmpty())
            throw new RuntimeException("no perceptrons");

        String maxKey = null;
        Double maxVal = null;

        for (String key : perceptrons.keySet()) {
            double result = perceptrons.get(key).predict(x);

            if (maxVal == null || result > maxVal) {
                maxVal = result;
                maxKey = key;
            }
        }

        return maxKey;
    }

    public int checkAccuracy(double[][] x, String[] y) {

        int score = 0;
        for (int i=0; i < y.length; i++)
            if (y[i].equals(predict(x[i])))
                score++;

        return (int) ((score / (double) y.length) * 100);
    }

}
