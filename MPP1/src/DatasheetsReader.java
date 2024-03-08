import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class DatasheetsReader {

    BufferedReader reader;
    DatasheetsReader(String path) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(path));
    }

    void getData() {
        try {

            Stream<String[]> dataStream =
                    this.reader.lines()
                            .map(s -> s.split("[ \\t]{2,}"))
                            .map(data1 -> {
                                for (int i = 0; i < data1.length; i++)
                                    data1[i] = data1[i].replaceAll(" ", "");
                                return data1;
                            });
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}