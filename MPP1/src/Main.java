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

            knnModel.predict(DatasheetsReader.getAttributes(data)[0]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


    }
}