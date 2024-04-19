import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class DataService {

    private final List<String> decisionAttributes;
    private final List<double[]> attributes;

    private static int countChar(char c, String data) {
        return (int) data.chars().
                filter(ch -> ch == c).
                count();
    }

    private static double[] countCharacters(String data) {
        double[] dataLetter = new double[26];

        data = data.toLowerCase();

        for (char i = 'a'; i <= 'z'; i++)
            dataLetter[i - 97] = countChar(i, data);

        return dataLetter;
    }

    private static double[] getProportion(double[] data) {
        double count = 0;
        for (double x : data)
            count += x;

        double[] temp = new double[data.length];

        for (int i=0; i < temp.length; i++)
            temp[i] = data[i] / count;

        return temp;
    }

    public static double[] StringToAttribute(String data) {
        return getProportion(countCharacters(data));
    }

    DataService(String folderName) throws IOException {

        this.decisionAttributes = new ArrayList<>();
        this.attributes = new ArrayList<>();

        Files.walkFileTree(Path.of(folderName), new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                if (file.toString().endsWith(".txt")) {
                    try {
                        // count letters
                        FileInputStream stream = new FileInputStream(String.valueOf(file));

                        decisionAttributes.add(
                                file.getParent().getFileName().toString()
                        );

                        attributes.add(
                                countCharacters(new String(stream.readAllBytes()))
                        );

                        stream.close();

                    } catch (Exception e) {
                        System.out.println("Open file: " + file + " failed");
                    }

                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.out.println("No permission to open: " + file.getFileName());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        attributes.replaceAll(DataService::getProportion);

    }

    public double[][] getAttributes() {
        double[][] data = new double[attributes.size()][26];
        attributes.toArray(data);

        return data;
    }

    public String[] getDecisionAttributes() {
        String[] temp = new String[decisionAttributes.size()];
        decisionAttributes.toArray(temp);
        return temp;
    }

}
