import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.stream.Stream;

public class DatasheetsReader {

    BufferedReader reader;
    DatasheetsReader(String path) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(path));
    }

    String[][] getData() {
        try {

            Stream<String[]> dataStream =
                    this.reader.lines()
                            .map(s -> s.split("[ \\t]{2,}"))
                            .map(data1 -> {
                                for (int i = 0; i < data1.length; i++)
                                    data1[i] = data1[i].replaceAll(" ", "");
                                return data1;
                            });

            return dataStream.toArray(String[][]::new);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Double[][] getAttributes(String[][] data) {
        return Arrays.stream(data).map(strings -> {
                Double[] data1 = new Double[strings.length-1];

                for (int i=0; i < strings.length-1; i++) {
                    strings[i] = strings[i].replaceAll(",", ".");
                    data1[i] = Double.parseDouble(strings[i]);
                }
                return data1;
        }).toArray(Double[][]::new);
    }

    static String[] getDecisionAttributes(String[][] data) {
        return Arrays.stream(data).map(strings -> strings[strings.length-1]).toArray(String[]::new);
    }

}