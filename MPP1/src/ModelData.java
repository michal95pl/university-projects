public class ModelData implements Comparable<ModelData> {

    public double x=0;
    public String y="";

    @Override
    public int compareTo(ModelData o) {
        return Double.compare(this.x, o.x);
    }

    @Override
    public String toString() {
        return x + " -> " + y;
    }
}
