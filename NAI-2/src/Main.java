import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try {
            String[][] data = new DatasheetsReader("datasheets/iris_training.txt").getData();
            double[][] x_train = DatasheetsReader.getAttributes(data);
            int[] y_train = DatasheetsReader.integerToInt(
                    Arrays.stream(DatasheetsReader.getDecisionAttributes(data)).mapToInt(value -> value.equals("Iris-setosa")? 1 : 0).boxed().toArray(Integer[]::new)
            );

            data = new DatasheetsReader("datasheets/iris_test.txt").getData();
            double[][] x_test = DatasheetsReader.getAttributes(data);
            int[] y_test = DatasheetsReader.integerToInt(
                    Arrays.stream(DatasheetsReader.getDecisionAttributes(data)).mapToInt(value -> value.equals("Iris-setosa")? 1 : 0).boxed().toArray(Integer[]::new)
            );

            Perceptron perceptron = new Perceptron();
            perceptron.fit(x_train, y_train, 0.5);

            double success = 0;
            for (int i=0; i < x_test.length; i++) {
                if (perceptron.predict(x_test[i]) == y_test[i])
                    success++;
            }
            System.out.println(success);
            System.out.println(Math.round(success / x_test.length * 100) + "%");


            Scanner scanner = new Scanner(System.in);
            do {
                try {
                    String[] dataLines = scanner.nextLine().split(" ");
                    double[] attributes = new double[dataLines.length];

                    for (int i=0; i < attributes.length; i++)
                        attributes[i] = Double.parseDouble(dataLines[i]);

                    if (perceptron.predict(attributes) == 1)
                        System.out.println("Iris-setosa");
                    else
                        System.out.println("no Iris-setosa");

                } catch (Exception e) {
                    System.out.println("wrong vector");
                }
            } while (true);


        } catch (FileNotFoundException e) {
            System.out.print("File not found");
        }






    }
}