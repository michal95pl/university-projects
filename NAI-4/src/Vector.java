import java.util.HashMap;
import java.util.Map;

public class Vector {

    public static double dotProduct(double[] m1, double[] m2) {
        if (m1.length != m2.length)
            throw new IllegalArgumentException("Arrays must have the same length");

        double result = 0;
        for (int i = 0; i < m1.length; i++)
            result += (m1[i] * m2[i]);

        return result;
    }

    public static double[] sumVector(double[] m1, double[] m2) {
        if (m1.length != m2.length)
            throw new IllegalArgumentException("Arrays must have the same length");

        double[] temp = new double[m2.length];

        for (int i=0; i < m1.length; i++)
            temp[i] = m1[i] + m2[i];

        return temp;
    }

    public static double[] sumVector(double[] m1, double m2) {
        double[] temp = new double[m1.length];

        for (int i=0; i < m1.length; i++)
            temp[i] = m1[i] + m2;

        return temp;
    }

    public static double[] multiplyVector(double[] m1, double val) {
        double[] temp = new double[m1.length];

        for (int i = 0; i < m1.length; i++)
            temp[i] = m1[i] * val;

        return temp;
    }

    public static double[] expandVector(double[] m1, double val) {
        double[] temp = new double[m1.length+1];

        System.arraycopy(m1, 0, temp, 0, m1.length);
        temp[temp.length-1] = val;

        return temp;
    }

    /**
     *
     * @param m1
     * @param m2
     * @return true if m1 and m2 are equal
     */
    public static boolean compareVector(double[] m1, double[] m2) {
        if (m1.length != m2.length)
            return false;

        for (int i=0; i < m1.length; i++)
            if (m1[i] != m2[i])
                return false;

        return true;
    }

    public static double[] normalizeVector(double[] m1) {
        double r = 0;

        for (double x : m1)
            r += (x * x);

        r = Math.sqrt(r);

        double[] data = new double[m1.length];

        for (int i=0; i < m1.length; i++)
            data[i] = m1[i] / r;

        return data;
    }

    public static double euclideanDistance(double[] m1, double[] m2) {

        if (m1.length != m2.length)
            throw new IllegalArgumentException();

        double x = 0;

        for (int i=0; i < m1.length; i++)
            x += Math.pow(m1[i] - m2[i], 2);

        return Math.sqrt(x);
    }

    static double entropy(double[] m) {

        Map<Double, Integer> classCounter = new HashMap<>();

        for (double data : m) {
            if (classCounter.containsKey(data))
                classCounter.put(data, classCounter.get(data) + 1);
            else
                classCounter.put(data, 1);
        }

        double entropy = 0;

        for (Double key : classCounter.keySet()) {
            double pi = (double) classCounter.get(key) / m.length;
            entropy += pi * Math.log(pi) / Math.log(2);
        }

        entropy *= -1;

        return entropy;

    }

}