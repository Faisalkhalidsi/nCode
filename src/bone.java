/*
 * Class for bones
 */

import org.jetbrains.annotations.NotNull;

public class bone {
    double[] SD;
    double[] mean;
    int DOF;
    String name;

    double[][][] dataOrig; // [amcFile][line][token]
    double[][][] dataNorm;

    bone(String name) {
        this.name = name;
    }

    public void readData(@NotNull String[][][] data) {
        //count frames
        dataOrig = new double[data.length][][];
        for (int mi = 0; mi < data.length; mi++) {
            int fn = 0;
            for (int li = 0; li < data[mi].length; li++) {
                if (data[mi][li][0].equals(name)) {
                    fn++;
                    DOF = data[mi][li].length - 1;
                    if (name.equals("root")) DOF = 3;
                }
            }
            readValues(data[mi], mi, fn);
        }

        //calc mean
        mean = new double[DOF];
        for (double[][] aDataOrig : dataOrig) {
            for (double[] anADataOrig : aDataOrig) {
                for (int i = 0; i < DOF; i++)
                    mean[i] += anADataOrig[i];
            }
        }

        for (int i = 0; i < DOF; i++)
            mean[i] /= getTotalFrameLength();

        //calc standard deviation
        SD = new double[DOF];
        for (double[][] aDataOrig : dataOrig) {
            for (double[] anADataOrig : aDataOrig) {
                for (int i = 0; i < DOF; i++)
                    SD[i] += Math.pow(anADataOrig[i] - mean[i], 2);
            }
        }
        for (int i = 0; i < DOF; i++)
            SD[i] = Math.sqrt(SD[i] / (getTotalFrameLength() - 1));

        //calc norm data
        dataNorm = new double[dataOrig.length][][];
        for (int i = 0; i < dataNorm.length; i++)
            dataNorm[i] = new double[dataOrig[i].length][dataOrig[i][0].length];

        for (int mi = 0; mi < dataOrig.length; mi++) {
            for (int fi = 0; fi < dataOrig[mi].length; fi++) {
                for (int i = 0; i < DOF; i++)
                    dataNorm[mi][fi][i] = (dataOrig[mi][fi][i] - mean[i]) / SD[i];
            }
        }
    }

    private void readValues(@NotNull String[][] strings, int mi, int fn) {
        //read values
        dataOrig[mi] = new double[fn][DOF];
        fn = 0;
        for (String[] string : strings) {
            if (string[0].equals(name)) {
                for (int i = 0; i < DOF; i++) {
                    dataOrig[mi][fn][i] = Double.parseDouble(string[i + 1]);
                }
                if (name.equals("root")) {
                    for (int i = 0; i < DOF; i++) {
                        dataOrig[mi][fn][i] = Double.parseDouble(string[i + 1 + 3]);
                    }
                }
                fn++;
            }
        }
    }

    int getModeNum() {
        return dataOrig.length;
    }

    int getFrameLength(int mode) {
        return dataOrig[mode].length;
    }

    int getTotalFrameLength() {
        int totfr = 0;
        for (int mode = 0; mode < getModeNum(); mode++) {
            totfr += getFrameLength(mode);
        }
        return totfr;
    }

    void printAMC(double data[], int offset, @NotNull StringBuilder sb) {
        sb.append(name);
        if (name.equals("root"))
            sb.append(" 0 0 0");
        for (int i = 0; i < DOF; i++)
            sb.append(" ").append(data[offset + i] * SD[i] + mean[i]);
        sb.append("\n");
    }
}
