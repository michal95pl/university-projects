public class ModelData {

    public double x;
    public String y;

    ModelData(double x, String y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + " -> " + y;
    }
}
