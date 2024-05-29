public class KnapsackGreedy {

    private final double knapsackSize;

    // 0 - sizes, 1 - values
    private final double[][] data;
    private final double[] ratio;

    KnapsackGreedy(double knapsackSize, double[][] data) {
        this.knapsackSize = knapsackSize;
        this.data = data;

        if (data.length != 2 && data[0].length != data[1].length)
            throw new IllegalArgumentException("Data must have 2 rows and same length");

        this.ratio = getRatio();
    }


    private double[] getRatio() {
        double[] ratio = new double[data[0].length];

        for (int i=0; i < data[0].length; i++)
            ratio[i] = data[1][i] / data[0][i];

        return ratio;
    }

    public void solve() {
        long characteristicVector = 0; // contains items in knapsack

        double sumSize = 0;
        double sumValue = 0;

        long runTime = System.currentTimeMillis();

        while (true) {
            int bestItemIndex = -1;

            // find first available item
            for (int i=0; i < data[0].length; i++) {
                if ((characteristicVector & (1L << i)) == 0) {
                    bestItemIndex = i;
                }
            }

            // added all items
            if (bestItemIndex == -1) {
                break;
            }

            // find best item
            for (int i=0; i < data[0].length; i++) {

                if ((characteristicVector & (1L << i)) == 0 && sumSize + data[0][i] <= knapsackSize) {
                    if (ratio[i] > ratio[bestItemIndex]) {
                        bestItemIndex = i;
                    }
                    else if (ratio[i] == ratio[bestItemIndex]) {
                        if (data[1][i] > data[1][bestItemIndex]) {
                            bestItemIndex = i;
                        }
                    }
                }

            }

            if (sumSize + data[0][bestItemIndex] > knapsackSize) {
                break;
            }

            System.out.println("Item " + bestItemIndex + " size: " + data[0][bestItemIndex] + " value: " + data[1][bestItemIndex] + " ratio: " + ratio[bestItemIndex]);

            sumSize += data[0][bestItemIndex];
            sumValue += data[1][bestItemIndex];

            characteristicVector |= (1L << bestItemIndex);
        }

        System.out.println("Sum size: " + sumSize + " Sum value: " + sumValue);
        System.out.println("Run time: " + (System.currentTimeMillis() - runTime) + "ms");
    }

}