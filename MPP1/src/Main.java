import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            DatasheetsReader reader = new DatasheetsReader("datasheets/iris_training.txt");
            reader.getData();


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


    }
}