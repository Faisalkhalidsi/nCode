import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

class LinesReader {
    private double[] x;
    private double[] y;

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }

    public LinesReader invoke(ArrayList<String> lines) {
        // add a line plot to the PlotPanel
        ArrayList<Double> xs = new ArrayList<>();
        ArrayList<Double> ys = new ArrayList<>();

        if (!lines.contains("#")) {
            for (String line : lines){
                String delim = " ";
                StringTokenizer st = new StringTokenizer(line, delim);
                xs.add(Double.parseDouble((String) st.nextElement()));
                ys.add(Double.parseDouble((String) st.nextElement()));
            }
        }
        Double[] d = new Double[xs.size()];
        xs.toArray(d);
        x = ArrayUtils.toPrimitive(d);
        ys.toArray(d);
        y = ArrayUtils.toPrimitive(d);
        return this;
    }
}
