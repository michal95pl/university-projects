import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Main {
    public static void main(String[] args) {

        System.out.print("Put k: ");
        int k = new Scanner(System.in).nextInt();

        try {
            DatasheetsReader reader = new DatasheetsReader("datasheets/iris_training.txt");
            String[][] data = reader.getData();

            Double[][] x = DatasheetsReader.getAttributes(data);
            String[] y = DatasheetsReader.getDecisionAttributes(data);

            KnnModel knnModel = new KnnModel(x, y, k);

            // test
            DatasheetsReader reader1 = new DatasheetsReader("datasheets/iris_test.txt");
            data = reader1.getData();

            x = DatasheetsReader.getAttributes(data);
            y = DatasheetsReader.getDecisionAttributes(data);

            int numberPassTest = knnModel.testPrecision(x, y);
            System.out.println("Passed: ");
            System.out.println(numberPassTest);
            System.out.println((Math.round(numberPassTest / (double)data.length * 10000) / 100.) + "%");


            String cmd = new Scanner(System.in).nextLine();

            while(!cmd.equals("exit")) {

                String[] strings = cmd.split(" ");
                Double[] attributes = new Double[strings.length];

                for (int i=0; i < strings.length; i++)
                    attributes[i] = Double.parseDouble(strings[i]);

                try {
                    System.out.println(knnModel.predict(attributes));
                } catch (DataFormatException e) {
                    System.out.println(e.getMessage());
                }

                cmd = new Scanner(System.in).nextLine();
            }


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (DataFormatException e) {
            System.out.print("File format error");
        }


    }
}