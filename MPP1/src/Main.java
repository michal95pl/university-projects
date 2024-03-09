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

            int countTest = data.length;
            int success = 0;

            for (int i=0; i < countTest; i++) {
                String s = knnModel.predict(DatasheetsReader.getAttributes(data)[i]);

                if (s.equals(DatasheetsReader.getDecisionAttributes(data)[i]))
                    success++;
            }

            System.out.print((int)(success / (double)countTest * 100) + "%");


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


    }
}