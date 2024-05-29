import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

            String[][] temp = dataStream.toArray(String[][]::new);
            reader.close();
            return temp;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static double[][] toPrimitiveDouble(Double[][] data) {
        double[][] temp = new double[data.length][data[0].length];

        for (int i=0; i < data.length; i++)
            for (int j=0; j < data[0].length; j++)
                temp[i][j] = data[i][j];

        return temp;
    }

    static double[][] getAttributes(String[][] data) {
        return toPrimitiveDouble(Arrays.stream(data).map(strings -> {
                Double[] data1 = new Double[strings.length-1];

                for (int i=0; i < strings.length-1; i++) {
                    strings[i] = strings[i].replaceAll(",", ".");
                    data1[i] = Double.parseDouble(strings[i]);
                }
                return data1;
        }).toArray(Double[][]::new));
    }

    static String[] getDecisionAttributes(String[][] data) {

        return Arrays.stream(data).map(strings -> strings[strings.length-1]).toArray(String[]::new);
    }

    static double[] getColumnByIndex(double[][] data, int index) {
        List<Double> list = new ArrayList<>();

        for (int i=0; i < data.length; i++)
            list.add(data[i][index]);

        double[] temp = new double[list.size()];
        for (int i=0; i < list.size(); i++)
            temp[i] = list.get(i);

        return temp;
    }

}