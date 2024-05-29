import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            Reader reader = new Reader("plecak.txt");

            int numberDataSet = (int)(Math.random()*14 + 1);

            System.out.println("Data set: " + numberDataSet);
            new KnapsackBrutalForce(reader.getRucksackCapacity(), reader.getDataSet(numberDataSet)).solve();
            new KnapsackGreedy(reader.getRucksackCapacity(), reader.getDataSet(numberDataSet)).solve();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

    }
}