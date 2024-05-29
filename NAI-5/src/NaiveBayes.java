import java.util.*;

class NumericToNominal {

    private final Map<String, List<Double>> map;
    NumericToNominal(double[] x, String[] y) {
        Map<String, List<Double>> map = new HashMap<>();

        for (int i = 0; i < x.length; i++) {

            if (!map.containsKey(y[i]))
                map.put(y[i], new ArrayList<>());

            map.get(y[i]).add(x[i]);
        }

        this.map = map;
    }

    public double getMeanByDecisionAttribute(String attribute) {
        if (map.containsKey(attribute)) {
            List<Double> list = map.get(attribute);

            double sum = 0;
            for (double d : list)
                sum += d;
            return sum / list.size();
        }

        throw new IllegalArgumentException("Attribute not found");
    }

    public double getStandardDeviationByDecisionAttribute(String attribute) {
        if (map.containsKey(attribute)) {
            List<Double> list = map.get(attribute);

            double mean = getMeanByDecisionAttribute(attribute);

            double sum = 0;
            for (double d : list)
                sum += Math.pow(d - mean, 2);

            return Math.sqrt(sum / (list.size()-1));
        }

        throw new IllegalArgumentException("Attribute not found");
    }
}

public class NaiveBayes {

    private final double[][] x;
    private final String[] y;


    private static double normalDistribution(double x, double mean, double std) {
        return (1 / (std * Math.sqrt(2 * Math.PI))) * Math.exp(-Math.pow(x - mean, 2) / (2 * Math.pow(std, 2)));
    }

    // conatins mean and standard deviation for each attribute
    private final NumericToNominal[] numericToNominals;


    private final Set<String> decisionAttributes;

    NaiveBayes(double[][] x, String[] y){
        this.x = x;
        this.y = y;

        decisionAttributes = new HashSet<>(Arrays.asList(y));

        // for each desicion attribute calculate mean and standard deviation
        numericToNominals = new NumericToNominal[x[0].length];
        for (int i = 0; i < x[0].length; i++)
            numericToNominals[i] = new NumericToNominal(DatasheetsReader.getColumnByIndex(x, i), y);

    }


    // P(X1 ^ X2 ^ X3 | y) * p(Y=y) = P(X1 | y) * P(X2 | y) * P(X3 | y) * P(Y = y)
    private double getProbability(double[] x, String y){
        double probability = 1;

        for (int i = 0; i < x.length; i++) {
            double mean = numericToNominals[i].getMeanByDecisionAttribute(y);
            double std = numericToNominals[i].getStandardDeviationByDecisionAttribute(y);

            double temp = normalDistribution(x[i], mean, std); // P(X | y)

            //System.out.print(temp + " ");

            //temp += 1;

            //temp /= this.x.length;
            //temp /= Integer.MAX_VALUE;

            System.out.println(temp);

            probability *= temp;
        }

        return probability * getProbability(y, this.y);
    }

    // P(Y=y)
    private double getProbability(String y, String[] data){
        int count = 0;

        for (String s : data) {
            if (s.equals(y))
                count++;
        }

        return (double) count / data.length;
    }

    public String predict(double[] x) {

        if (x.length != this.x[0].length)
            throw new IllegalArgumentException("Invalid vector size");

        double maxProbability = -1;
        String maxCategory = null;

        for (String category : decisionAttributes) {
            double probability = getProbability(x, category);
            if (probability > maxProbability) {
                maxProbability = probability;
                maxCategory = category;
            }
        }

        return maxCategory;
    }
}