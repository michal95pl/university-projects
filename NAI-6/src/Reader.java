import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Reader {

    private int dataLength;
    private final Map<Integer, double[][]> datasets = new HashMap<>();
    double rucksackCapacity;

    Reader (String path) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        try {
            String dataDescriptionLine = reader.readLine();
            String[] data = dataDescriptionLine.split("[ ,]");

            for (int i = 0; i < data.length-1; i++) {
                if (data[i].equals("capacity")) {
                    rucksackCapacity = Double.parseDouble(data[i + 1]);
                } else if (data[i].equals("length")) {
                    dataLength = Integer.parseInt(data[i+1]);
                }
            }

            // read datasets
            String line;
            String[] datasetLines = new String[3];
            int i=0;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    i = 0;
                    readDataSet(datasetLines);
                } else {
                    datasetLines[i] = line;
                    i++;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void readDataSet(String[] data) {
        if (data.length != 3)
            throw new IllegalArgumentException("Data must have 3 lines");

        int datasetNumber = -1;

        for (int i = 0; i < data.length; i++) {
            if (i == 0) {
                datasetNumber = Integer.parseInt(data[i].substring(8, data[i].length()-1));
                this.datasets.put(datasetNumber, new double[2][dataLength]);
            } else if (i == 1) {
                this.datasets.get(datasetNumber)[0] = getArray(data[i]);
            } else {
                this.datasets.get(datasetNumber)[1] = getArray(data[i]);
            }
        }
    }

    private double[] getArray(String line) {

        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '{')
                startIndex = i+1;
            if (line.charAt(i) == '}')
                endIndex = i;
        }


        double[] arrayData = new double[dataLength];
        {
            int i = 0;
            for (String data : line.substring(startIndex, endIndex).split("[, ]")) {
                if (!data.equals(" ") && !data.isEmpty()) {
                    arrayData[i] = Double.parseDouble(data);
                    i++;
                }
            }
        }

        return arrayData;
    }

    public double[][] getDataSet(int datasetNumber) {
        if (!this.datasets.containsKey(datasetNumber))
            throw new IllegalArgumentException("Dataset not found");

        return this.datasets.get(datasetNumber);
    }

    public double getRucksackCapacity() {
        return rucksackCapacity;
    }

}
