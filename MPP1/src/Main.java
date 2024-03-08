import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {

        try {
            DatasheetsReader reader = new DatasheetsReader("datasheets/iris_test.txt");
            String[][] data = reader.getData();

            String[] dat = DatasheetsReader.getDecisionAttributes(data);
            Double[][] at = DatasheetsReader.getAttributes(data);


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


    }
}