import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try {
            DataService data_learn = new DataService("datasheets");

            String[] y_learn = data_learn.getDecisionAttributes();
            double[][] x_learn = data_learn.getAttributes();

            Layer layer = new Layer(x_learn, y_learn, 0.1);
            layer.fit();


            //test
            DataService data_test = new DataService("test-datasheet");
            String[] y_test = data_test.getDecisionAttributes();
            double[][] x_test = data_test.getAttributes();

            for (int i=0; i < y_test.length; i++)
                System.out.println(y_test[i] + " -> " + layer.predict(x_test[i]));

            System.out.println(layer.checkAccuracy(x_test, y_test) + "%");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                StringBuilder builder = new StringBuilder();

                String data = scanner.nextLine();
                if (data.equals("exit"))
                    break;

                while (!data.equals("end data")) {
                    builder.append(data);
                    data = scanner.nextLine();
                }
                System.out.println(layer.predict(DataService.StringToAttribute(builder.toString())));
            }


        } catch (IOException e) {
            System.out.println("File not found");
        }

    }
}