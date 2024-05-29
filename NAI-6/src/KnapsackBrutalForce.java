public class KnapsackBrutalForce {

    // 0 - sizes, 1 - values
    private final double[][] data;
    private double knapsackSize = 0;

    KnapsackBrutalForce(double knapsackSize , double[][] data) {
        this.data = data;
        this.knapsackSize = knapsackSize;

        if (data.length != 2 && data[0].length != data[1].length)
            throw new IllegalArgumentException("Data must have 2 rows and same length");

    }

    private double getSumSizes(long characteristicVector) {
        double sum = 0;

        for (int i=0; i < data[0].length; i++) {
            if ((characteristicVector & (1L << i)) == 1L << i)
                sum += data[0][i];
        }

        return sum;
    }

    private double getSumValues(long characteristicVector) {
        double sum = 0;

        for (int i=0; i < data[0].length; i++) {
            if ((characteristicVector & (1L << i)) == 1L << i)
                sum += data[1][i];
        }

        return sum;
    }

    private void showItems(long characteristicVector) {

        for (int i=0; i < data[0].length; i++) {
            if ((characteristicVector & (1L << i)) == 1L << i) {
                System.out.println("Item " + i + " size: " + data[0][i] + " value: " + data[1][i]);
            }

        }
    }

    public void solve() {

        double maxValue = -1;
        double maxSize = -1;
        long maxCharacteristicVector = 0;

        long startTimer = System.currentTimeMillis();
        int checked = 0;

        for (long i=0; i < Math.pow(2, data[0].length); i++) {
            double size = getSumSizes(i);

            if (size <= knapsackSize) {
                double sum = getSumValues(i);

                if (sum > maxValue) {
                    maxValue = sum;
                    maxCharacteristicVector = i;
                    maxSize = size;
                }
            }
            checked++;
        }

        showItems(maxCharacteristicVector);
        System.out.println("Sum value: " + maxValue);
        System.out.println("Sum size: " + maxSize);
        System.out.println("Time: " + (System.currentTimeMillis() - startTimer) + "ms");
        System.out.println("Checked: " + checked);

    }

}
