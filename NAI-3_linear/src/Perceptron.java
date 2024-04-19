public class Perceptron {

    private double[] weights;

    private double func(double x) {
        return x;
    }

    private final double[][] x;
    private final int[] y;
    private final double learningRate;

    Perceptron(double[][] x, int[] y, double learningRate) {
        // add -1 to X
        this.x = new double[x.length][x.length+1];
        for (int i=0; i < x.length; i++)
            this.x[i] = Vector.normalizeVector(Vector.expandVector(x[i], -1));

        // generate weights
        this.weights = new double[x[0].length+1];
        for (int i=0; i < weights.length; i++)
            this.weights[i] = (Math.random())+0.1;
        this.weights = Vector.normalizeVector(weights);

        this.y = y;
        this.learningRate = learningRate;
    }


    public double fit(int i) {

        double outPerception = func(Vector.dotProduct(x[i], this.weights));

        this.weights = Vector.sumVector(
                this.weights,
                Vector.multiplyVector(x[i], learningRate * (y[i] - outPerception))
        );

        // error d - (W^T * X)
        return Math.pow(y[i] - func(Vector.dotProduct(x[i], this.weights)), 2);
    }

    public void normalizeWeights() {
        this.weights = Vector.normalizeVector(this.weights);
    }

    public double predict(double[] x) throws IllegalArgumentException {
        if (1+x.length != weights.length)
            throw new IllegalArgumentException();

        x = Vector.normalizeVector(Vector.expandVector(x, -1));

        return func(Vector.dotProduct(x, this.weights));
    }

}