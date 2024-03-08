import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasheetsReader {

    BufferedReader reader;
    DatasheetsReader(String path) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(path));
    }

    List<String[]> getData() {
        try {
            List<String[]> data = new ArrayList<>();

            while (this.reader.ready()) {
                String s = this.reader.readLine();
                String[] temp = s.split("[ \\t]{2,}"); // cut

                for (int i=0; i < temp.length; i++)
                    temp[i] = temp[i].replaceAll(" ", ""); // delete space

                data.add(temp);
            }

            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}