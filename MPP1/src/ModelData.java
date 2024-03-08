public class ModelData implements Comparable<ModelData> {

    public double x;
    public String y;

    ModelData(double x, String y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(ModelData o) {
        return Double.compare(this.x, o.x);
    }

    @Override
    public String toString() {
        return x + " -> " + y;
    }
}
