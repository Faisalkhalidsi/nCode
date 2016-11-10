import org.math.plot.Plot2DPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LinePlotter {
    private Plot2DPanel plot;
    private JFrame frame;

    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 600;

    public LinePlotter(String title) {
        // create your PlotPanel (you can use it as a JPanel)
        this.plot = new Plot2DPanel();
        // define the legend position
        this.plot.addLegend("SOUTH");
        // put the PlotPanel in a JFrame like a JPanel
        frame = new JFrame(title);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setContentPane(plot);
        frame.setVisible(true);
    }

    void plotLine(String title, double[] x, double[] y) {
        assert (x.length > 0 && y.length > 0);
        this.plot.addLinePlot(title, x, y);
    }

    void setAxisLabel(String xLabel, String yLabel) {
        this.plot.setAxisLabel(0, xLabel);
        this.plot.setAxisLabel(1, yLabel);
    }

    void savePlot(String title) {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String currTime = sdf.format(cal.getTime());
        String file = demo.PLOT_DIR + title + "_" + currTime + ".png";
        BufferedImage image = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        frame.paint(graphics2D);
        try {
            ImageIO.write(image, "jpeg", new File(file));
        } catch (IOException e) {
            System.out.println("e.message: "+e.getMessage());
        }
    }
}