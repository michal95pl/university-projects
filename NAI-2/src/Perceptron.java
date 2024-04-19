public class Perceptron {

    private double[] weights;

    private int func(double x) {
        return x >= 0? 1 : 0;
    }

    public void fit(double[][] x, int[] y, double learningRate) {
        this.weights = new double[x[0].length+1];

        for (int i=0; i < weights.length; i++)
            this.weights[i] = (Math.random())+0.1;


        ///////////////////////
        double[][] temp_x = new double[x.length][x.length+1];
        for (int i=0; i < x.length; i++)
            temp_x[i] = Vector.expandVector(x[i], -1);
        ///////////////////////

        boolean run = true;
        while(run) {
            run = false;

            for (int i=0; i < x.length; i++) {

                double[] tempWeights;

                do {
                    int outPerception = func(Vector.dotProduct(temp_x[i], this.weights));

                    if (outPerception != y[i])
                        run = true;

                    tempWeights = weights;
                    this.weights = Vector.sumVector(
                            this.weights,
                            Vector.multiplyVector(temp_x[i], learningRate * (y[i] - outPerception))
                    );
                } while (!Vector.compareVector(weights, tempWeights));
            }

        }

    }

    public int predict(double[] x) throws IllegalArgumentException {
        if (1+x.length != weights.length)
            throw new IllegalArgumentException();

        x = Vector.expandVector(x, -1);

        return func(Vector.dotProduct(x, this.weights));
    }

}