import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            DatasheetsReader reader = new DatasheetsReader("datasheets/iris_training.txt");
            List<String[]> data = reader.getData();

            String test[] = data.get(0);
            for (String x : test)
                System.out.println(x);

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }


    }
}