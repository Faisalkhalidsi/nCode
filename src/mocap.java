import processing.core.PApplet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class mocap {

    private boolean isDrawSkel = false;
    private ChildApplet child;
    private EmbeddedSketch eSketch;
    private PlayerSkel skel;
    private TDBN net;

    private int control = 1; // 1 for run, 0 for walk

    public mocap() {
        child = new ChildApplet();
        eSketch = new EmbeddedSketch(child);
    }

    public void resetChildApplet() {
        child.reset();
    }

    public String saveScreenshot() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String currTime = sdf.format(cal.getTime());
        String filename = demo.PLOT_DIR + "mocap_" + currTime + ".png";
        child.saveFrame(filename);
        return filename;
    }


    public void setDrawSkel() {
        isDrawSkel = true;
    }

    public void setSkel(PlayerSkel skel) {
        this.skel = skel;
    }

    public void setNet(TDBN net) {
        this.net = net;
    }

    public void setControl(int control) {
        this.control = control;
    }

    private static final int FRAME_WIDTH = 1500;//1500
    private static final int FRAME_HEIGHT = 250;
    private static final int START_OFFSET = 1400;//1400
    private static final int FRAME_STEP = 8;

    //The JFrame which will contain the child applet
    private class EmbeddedSketch extends JFrame {
        PApplet sketch;

        public EmbeddedSketch(PApplet p) {

            setTitle("Mocap Frame");
            setBounds(100, 100, FRAME_WIDTH, FRAME_HEIGHT);
            add(p);
            p.init();
            sketch = p;
            setLocation(500, 200);
            //Program exits
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }
    }

    private class ChildApplet extends PApplet {

        double rotX = 0.5;
        double rotY = 0.5;

        private int frame = 0;
        Robot robot = null;

        public void mouseDragged() {
            rotY = 1.0 * mouseX / width;
            rotX = 1.0 * mouseY / width;
        }

        public void reset() {
            background(255);
            frame = 0;
        }

        public void setup() {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }

            background(255);
            size(FRAME_WIDTH, FRAME_HEIGHT, P3D);
            noSmooth();
        }

        public void draw() {
//            background(255);

            float fov = PApplet.PI / 3;
            float cameraZ = (height / 2.0f) / PApplet.tan(fov / 2.0f);
            float cameraNear = cameraZ / 2.0f;
            float cameraFar = cameraZ * 2.0f;
            ortho(-width / 2, width / 2, -height / 2, height / 2, cameraNear, cameraFar);

            if (isDrawSkel) {
//                println("mocap frameRate: "+frameRate);

                // draw body
                stroke(100, 0.5f);
                strokeWeight(3);
                translate(width / 2, 3 * height / 4, 0);

                rotateX((float) (PI / 2.0));
                rotateY((float) (PI / 2.0));
                rotateZ((float) (PI / 2.0));

                scale(0.5f);
                translate(0, 100, FRAME_STEP * frame - START_OFFSET);

                // change the view-angle by mouse drag
                rotateY((float) ((rotY - 0.5) * PI * 1.5));
                rotateZ((float) ((rotX - 0.5) * PI * 1.5));

                fill(255, 0, 0);

//                System.out.println("frame: " + frame);
                if (frame == 100) {
                    robot.keyPress(KeyEvent.VK_W);
                    robot.keyRelease(KeyEvent.VK_W);
                }
                if (frame == 200) {
                    robot.keyPress(KeyEvent.VK_R);
                    robot.keyRelease(KeyEvent.VK_R);
                }
                if (frame == demo.END_OFFSET + 1) {
                    robot.keyPress(KeyEvent.VK_S);
                    robot.keyRelease(KeyEvent.VK_S);
                    robot.keyPress(KeyEvent.VK_P);
                    robot.keyRelease(KeyEvent.VK_P);
                }


                // generate each testModeFrame by TDBN
                if (frame % FRAME_STEP == 0 && frame < demo.END_OFFSET)
                    skel.draw(this, net.stepRealtime(control));

                frame++;
            }
        }

    }


}
