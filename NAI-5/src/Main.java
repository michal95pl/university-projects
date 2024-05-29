import java.io.FileNotFoundException;
import java.util.Scanner;

// https://medium.com/analytics-vidhya/use-naive-bayes-algorithm-for-categorical-and-numerical-data-classification-935d90ab273f

public class Main {
    public static void main(String[] args) {

        try {
            DatasheetsReader reader = new DatasheetsReader("datasheets/iris_training.txt");
            String[][] trainData = reader.getData();

            String[] y = DatasheetsReader.getDecisionAttributes(trainData);
            double[][] x = DatasheetsReader.getAttributes(trainData);


            NaiveBayes nb = new NaiveBayes(x, y);

            reader = new DatasheetsReader("datasheets/iris_test.txt");
            String[][] testData = reader.getData();
            y = DatasheetsReader.getDecisionAttributes(testData);
            x = DatasheetsReader.getAttributes(testData);

            String[] predicted = new String[x.length];
            for (int i = 0; i < x.length; i++) {
                predicted[i] = nb.predict(x[i]);
            }

            new Metrics(y, predicted).showMetrics();


            String cmd = new Scanner(System.in).nextLine();

            while(!cmd.equals("exit")) {

                try {
                    String[] strings = cmd.split(" ");
                    double[] attributes = new double[strings.length];

                    for (int i=0; i < strings.length; i++)
                        attributes[i] = Double.parseDouble(strings[i]);

                    System.out.println(nb.predict(attributes));
                } catch (Exception e) {
                    System.out.println("Invalid data");
                }

                cmd = new Scanner(System.in).nextLine();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}