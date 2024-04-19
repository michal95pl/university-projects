import java.util.HashMap;
import java.util.Map;

public class Layer {

    private final Map<String, Perceptron> perceptrons;

    private int[] stringToClassificationArray(String[] y, String key) {

        int[] temp = new int[y.length];

        for (int i=0; i < temp.length; i++)
            temp[i] = (y[i].equals(key))? 1 : 0;

        return temp;
    }

    Layer(double[][] x, String[] y, double learningRate) {

        perceptrons = new HashMap<>();

        for (String s : y)
            if (!perceptrons.containsKey(s))
                perceptrons.put(s, new Perceptron(x, stringToClassificationArray(y, s), learningRate));
    }

    public void fit() {
        for (String key : perceptrons.keySet())
            perceptrons.get(key).fit();
    }

    public String predict(double[] x) {

        if (perceptrons.isEmpty())
            throw new RuntimeException("no perceptrons");

        for (String key : perceptrons.keySet()) {
            double result = perceptrons.get(key).predict(x);

            if (result == 1) {
                return key;
            }
        }

        return "none";
    }

    public int checkAccuracy(double[][] x, String[] y) {

        int score = 0;
        for (int i=0; i < y.length; i++)
            if (y[i].equals(predict(x[i])))
                score++;

        return (int) ((score / (double) y.length) * 100);
    }

}
