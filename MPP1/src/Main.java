import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {

        try {
            DatasheetsReader reader = new DatasheetsReader("datasheets/iris_training.txt");
            String[][] data = reader.getData();

            Double[][] x = DatasheetsReader.getAttributes(data);
            String[] y = DatasheetsReader.getDecisionAttributes(data);

            KnnModel knnModel = new KnnModel(x, y, 3);

            DatasheetsReader reader1 = new DatasheetsReader("datasheets/iris_test.txt");
            data = reader1.getData();

            double precision = knnModel.testPrecision(DatasheetsReader.getAttributes(data), DatasheetsReader.getDecisionAttributes(data));
            System.out.print(precision);



        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


    }
}