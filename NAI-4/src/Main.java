import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            DatasheetsReader irisTraining = new DatasheetsReader("datasheets/iris_training_3.txt");

            String[][] data = irisTraining.getData();

            double[][] x = DatasheetsReader.getAttributes(data);
            String[] y = DatasheetsReader.getDecisionAttributes(data);

            KMeans kMeans = new KMeans(3, x);
            kMeans.fit();

            DatasheetsReader irisTest = new DatasheetsReader("datasheets/iris_test_3.txt");

            data = irisTest.getData();

            x = DatasheetsReader.getAttributes(data);
            y = DatasheetsReader.getDecisionAttributes(data);

            kMeans.getEntropy(x, y);
            kMeans.showClusters(x, y);


        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        }

    }
}