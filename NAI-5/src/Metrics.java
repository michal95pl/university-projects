import java.util.*;

public class Metrics {

    String[] real;
    String[] predicted;

    Metrics(String[] real, String[] predicted) {
        this.real = real;
        this.predicted = predicted;

        if (real.length != predicted.length)
            throw new IllegalArgumentException("Real and predicted arrays must have the same length");
    }

    public double accuracy() {
        int correct = 0;
        for (int i = 0; i < real.length; i++) {
            if (real[i].equals(predicted[i])) {
                correct++;
            }
        }
        return (double) correct / real.length;
    }

    private double getFalsePositive(String className) {
        double fp = 0;
        for (int i = 0; i < this.real.length; i++) {
            if (!this.real[i].equals(className) && this.predicted[i].equals(className)) {
                fp++;
            }
        }
        return fp;
    }

    private double getFalseNegative(String className) {
        double fn = 0;
        for (int i = 0; i < this.real.length; i++) {
            if (this.real[i].equals(className) && !this.predicted[i].equals(className)) {
                fn++;
            }
        }
        return fn;
    }

    public double getPrecisionByClass(String className) {
        double tp = countActualPredicted(className, className);
        double fp = getFalsePositive(className);
        return tp / (tp + fp);
    }

    public double getRecallByClass(String className) {
        double tp = countActualPredicted(className, className);
        double fn = getFalseNegative(className);
        return tp / (tp + fn);
    }


    private double countActualPredicted(String actual, String predicted) {
        int count = 0;
        for (int i = 0; i < this.real.length; i++) {
            if (this.real[i].equals(actual) && this.predicted[i].equals(predicted)) {
                count++;
            }
        }
        return count;
    }

    public double getFScoreByClass(String className) {
        double precision = getPrecisionByClass(className);
        double recall = getRecallByClass(className);
        return 2 * (precision * recall) / (precision + recall);
    }

    public void showConfusionMatrix() {
        // all classes
        Set<String> realClasses = new HashSet<>(Arrays.asList(this.real));
        Set<String> predictedClasses = new HashSet<>(Arrays.asList(this.predicted));

        System.out.print("real  ");
        for (String predictedClass : predictedClasses) {
            System.out.print(predictedClass + " ");
        }

        System.out.println();

        for (String realClass : realClasses) {
            System.out.print(realClass + " ");
            for (String predictedClass : predictedClasses) {
                System.out.print(countActualPredicted(realClass, predictedClass) + " ");
            }
            System.out.println();
        }

    }

    public void showMetrics() {
        Set<String> classes = new HashSet<>(Arrays.asList(this.real));
        System.out.println("Accuracy: " + accuracy());
        System.out.println();
        for (String className : classes) {
            System.out.println("Class: " + className);
            System.out.println("Precision: " + getPrecisionByClass(className));
            System.out.println("Recall: " + getRecallByClass(className));
            System.out.println("F-score: " + getFScoreByClass(className));
            System.out.println();
        }

        System.out.println("Confusion matrix:");
        showConfusionMatrix();
    }

}
