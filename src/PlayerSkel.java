/*
 * Class representing a skeleton for playing motion.
 */

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

class PlayerSkel {
    HashMap<String, PlayerBone> bones;
    float[][] data;
    String[] words;

    class rootDatum {
        Point3f tran, rot;
        rootDatum(Point3f dof, Point3f pos) {
            this.tran = new Point3f(dof);
            this.rot = new Point3f(pos);
        }
    }
    class twoDofDatum {
        Point2f dof;
        twoDofDatum(Point2f dof) {
            this.dof = new Point2f(dof);
        }
    }

    //add by milyun at 17 nov 2014
    class threeDofDatum {
        Point3f dof;
        threeDofDatum(Point3f dof) {
            this.dof = new Point3f(dof);
        }
    }


    ArrayList<rootDatum> rootData = new ArrayList<>();
    ArrayList<twoDofDatum> lhandData = new ArrayList<>();
    ArrayList<twoDofDatum> rhandData = new ArrayList<>();
    ArrayList<twoDofDatum> lfootData = new ArrayList<>();
    ArrayList<twoDofDatum> rfootData = new ArrayList<>();
    //add by milyun 17 nov 2014
    ArrayList<threeDofDatum> lhumerusData = new ArrayList<>();
    ArrayList<threeDofDatum> rhumerusData = new ArrayList<>();
    ArrayList<threeDofDatum> lfemurData = new ArrayList<>();
    ArrayList<threeDofDatum> rfemurData = new ArrayList<>();
    ArrayList<threeDofDatum> lowerbackData = new ArrayList<>();
    ArrayList<threeDofDatum> thoraxData = new ArrayList<>();
    ArrayList<threeDofDatum> lowerneckData = new ArrayList<>();
    ArrayList<threeDofDatum> upperneckData = new ArrayList<>();

    boolean DO_RECORD_ROOT = true;
    boolean DO_RECORD_LHAND = true;
    boolean DO_RECORD_RHAND = true;
    boolean DO_RECORD_LFOOT = true;
    boolean DO_RECORD_RFOOT = true;
    //add by milyu 17 nov 2014
    boolean DO_RECORD_LHUMERUS = true;
    boolean DO_RECORD_RHUMERUS = true;
    boolean DO_RECORD_LFEMUR = true;
    boolean DO_RECORD_RFEMUR = true;
    boolean DO_RECORD_LOWERBACK = true;
    boolean DO_RECORD_THORAX = true;
    boolean DO_RECORD_LOWERNECK = true;
    boolean DO_RECORD_UPPERNECK = true;

    ArrayList<Float> rootRXs = new ArrayList<>();
    ArrayList<Float> rootRYs = new ArrayList<>();
    ArrayList<Float> rootRZs = new ArrayList<>();
    ArrayList<Float> lhandRXs = new ArrayList<>();
    ArrayList<Float> lhandRZs = new ArrayList<>();
    ArrayList<Float> rhandRXs = new ArrayList<>();
    ArrayList<Float> rhandRZs = new ArrayList<>();
    ArrayList<Float> lfootRXs = new ArrayList<>();
    ArrayList<Float> lfootRZs = new ArrayList<>();
    ArrayList<Float> rfootRXs = new ArrayList<>();
    ArrayList<Float> rfootRZs = new ArrayList<>();
    // add by milyun at 17 nov 2014
    ArrayList<Float> rhumerusRXs = new ArrayList<>();
    ArrayList<Float> rhumerusRYs = new ArrayList<>();
    ArrayList<Float> rhumerusRZs = new ArrayList<>();
    ArrayList<Float> lhumerusRXs = new ArrayList<>();
    ArrayList<Float> lhumerusRYs = new ArrayList<>();
    ArrayList<Float> lhumerusRZs = new ArrayList<>();
    ArrayList<Float> rfemurRXs = new ArrayList<>();
    ArrayList<Float> rfemurRYs = new ArrayList<>();
    ArrayList<Float> rfemurRZs = new ArrayList<>();
    ArrayList<Float> lfemurRXs = new ArrayList<>();
    ArrayList<Float> lfemurRYs = new ArrayList<>();
    ArrayList<Float> lfemurRZs = new ArrayList<>();
    ArrayList<Float> lowerbackRXs = new ArrayList<>();
    ArrayList<Float> lowerbackRYs = new ArrayList<>();
    ArrayList<Float> lowerbackRZs = new ArrayList<>();
    ArrayList<Float> thoraxRXs = new ArrayList<>();
    ArrayList<Float> thoraxRYs = new ArrayList<>();
    ArrayList<Float> thoraxRZs = new ArrayList<>();
    ArrayList<Float> lowerneckRXs = new ArrayList<>();
    ArrayList<Float> lowerneckRYs = new ArrayList<>();
    ArrayList<Float> lowerneckRZs = new ArrayList<>();
    ArrayList<Float> upperneckRXs = new ArrayList<>();
    ArrayList<Float> upperneckRYs = new ArrayList<>();
    ArrayList<Float> upperneckRZs = new ArrayList<>();


    ArrayList<Float> totalSmoothness = new ArrayList<>();

    int frame = 0;

    PrintWriter writer = null;

    PlayerSkel(@NotNull String asfPath) throws Exception {
        bones = new HashMap<>();
        BufferedReader asf = new BufferedReader(new FileReader(asfPath));
        parseSkel(asf);
        data = new float[35][6];
        setSmoothnessOutputFile();
    }

    // read skeleton parameters from an .asf file
    void parseSkel(@NotNull BufferedReader reader) throws Exception {
        // add root manually.
        bones.put("root",
                new PlayerBone("root", 0, 0,
                        0, 0, 0,
                        0, 0, 0,
                        1, 1, 1));
        bones.get("root").root = 1;

        // read parameters for other bones
        while (!reader.readLine().equals(":bonedata")) {
        }
        while (!reader.readLine().equals(":hierarchy")) {
            PlayerBone bone;
            bone = new PlayerBone(reader);
            bones.put(bone.name, bone);
        }

        // read hierarchical relations of bones
        String[] words;
        while (true) {
            words = reader.readLine().trim().split(" ");
            if (words[0].equals("begin")) continue;
            if (words[0].equals("end")) break;
            PlayerBone bone = bones.get(words[0]);
            bone.child = bones.get(words[1]);
            if (words.length > 2)
                bone.child2 = bones.get(words[2]);
            if (words.length > 3)
                bone.child3 = bones.get(words[3]);
        }
    }

    // draws skeleton
    void draw(@NotNull PApplet applet, @NotNull String[] amcFormatFrame) {
        for (String anAmcFormatFrame : amcFormatFrame) {
            if (anAmcFormatFrame == null) break;
            words = anAmcFormatFrame.trim().split("\\s+");
            if (words.length == 1) continue;

            PlayerBone bone = bones.get(words[0]);
            float[] arr = bone.readData(data, words);

            if (frame < demo.END_OFFSET) { // save only up to END_OFFSET frames

                if (DO_RECORD_ROOT && words[0].equals("root")) {
                    float TX = arr[3];
                    float TY = arr[4];
                    float TZ = arr[5];
                    Point3f tran = new Point3f(TX, TY, TZ);
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f rot = new Point3f(RX, RY, RZ);
                    rootData.add(new rootDatum(tran, rot));
                    rootRXs.add(RX);
                    rootRYs.add(RY);
                    rootRZs.add(RZ);
                }
                if (DO_RECORD_LHAND && words[0].equals("lhand")) {
                    float RX = arr[0];
                    float RZ = arr[2];
                    Point2f dof = new Point2f(RX, RZ);
                    lhandData.add(new twoDofDatum(dof));
                    lhandRXs.add(RX);
                    lhandRZs.add(RZ);
                }
                if (DO_RECORD_RHAND && words[0].equals("rhand")) {
                    float RX = arr[0];
                    float RZ = arr[2];
                    Point2f dof = new Point2f(RX, RZ);
                    rhandData.add(new twoDofDatum(dof));
                    rhandRXs.add(RX);
                    rhandRZs.add(RZ);
                }
                if (DO_RECORD_LFOOT && words[0].equals("lfoot")) {
                    float RX = arr[0];
                    float RZ = arr[2];
                    Point2f dof = new Point2f(RX, RZ);
                    lfootData.add(new twoDofDatum(dof));
                    lfootRXs.add(RX);
                    lfootRZs.add(RZ);
                }
                if (DO_RECORD_RFOOT && words[0].equals("rfoot")) {
                    float RX = arr[0];
                    float RZ = arr[2];
                    Point2f dof = new Point2f(RX, RZ);
                    rfootData.add(new twoDofDatum(dof));
                    rfootRXs.add(RX);
                    rfootRZs.add(RZ);
                }
                //add by milyun 17 nov 2014
                if (DO_RECORD_LHUMERUS && words[0].equals("lhumerus")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    lhumerusData.add(new threeDofDatum(dof));
                    lhumerusRXs.add(RX);
                    lhumerusRYs.add(RY);
                    lhumerusRZs.add(RZ);
                }
                if (DO_RECORD_RHUMERUS && words[0].equals("rhumerus")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    rhumerusData.add(new threeDofDatum(dof));
                    rhumerusRXs.add(RX);
                    rhumerusRYs.add(RY);
                    rhumerusRZs.add(RZ);
                }
                if (DO_RECORD_LFEMUR && words[0].equals("lfemur")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    lfemurData.add(new threeDofDatum(dof));
                    lfemurRXs.add(RX);
                    lfemurRYs.add(RY);
                    lfemurRZs.add(RZ);
                }
                if (DO_RECORD_RFEMUR && words[0].equals("rfemur")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    rfemurData.add(new threeDofDatum(dof));
                    rfemurRXs.add(RX);
                    rfemurRYs.add(RY);
                    rfemurRZs.add(RZ);
                }
                if (DO_RECORD_LOWERBACK && words[0].equals("lowerback")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    lowerbackData.add(new threeDofDatum(dof));
                    lowerbackRXs.add(RX);
                    lowerbackRYs.add(RY);
                    lowerbackRZs.add(RZ);
                }
                if (DO_RECORD_THORAX && words[0].equals("thorax")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    thoraxData.add(new threeDofDatum(dof));
                    thoraxRXs.add(RX);
                    thoraxRYs.add(RY);
                    thoraxRZs.add(RZ);
                }
                if (DO_RECORD_LOWERNECK && words[0].equals("lowerneck")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    lowerneckData.add(new threeDofDatum(dof));
                    lowerneckRXs.add(RX);
                    lowerneckRYs.add(RY);
                    lowerneckRZs.add(RZ);
                }
                if (DO_RECORD_UPPERNECK && words[0].equals("upperneck")) {
                    float RX = arr[0];
                    float RY = arr[1];
                    float RZ = arr[2];
                    Point3f dof = new Point3f(RX, RY, RZ);
                    upperneckData.add(new threeDofDatum(dof));
                    upperneckRXs.add(RX);
                    upperneckRYs.add(RY);
                    upperneckRZs.add(RZ);
                }
            }
        }

        bones.get("root").draw(applet, data, null);

        frame++;

    }

    private void setSmoothnessOutputFile() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
            String currTime = sdf.format(cal.getTime());
            writer = new PrintWriter(demo.PLOT_DIR + "smoothness_" +
                    currTime + ".out", "UTF-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("writer is a null");
        }

        // write metadata
        assert writer != null;
        writer.println("# MetaData");
        writer.println("# DO_RECORD_ROOT: " + DO_RECORD_ROOT);
        writer.println("# DO_RECORD_LHAND: " + DO_RECORD_LHAND);
        writer.println("# DO_RECORD_RHAND: " + DO_RECORD_RHAND);
        writer.println("# DO_RECORD_LFOOT: " + DO_RECORD_LFOOT);
        writer.println("# DO_RECORD_RFOOT: " + DO_RECORD_RFOOT);
        writer.println("# DO_RECORD_LHUMERUS: " + DO_RECORD_LHUMERUS);
        writer.println("# DO_RECORD_RHUMERUS: " + DO_RECORD_RHUMERUS);
        writer.println("# DO_RECORD_LFEMUR: " + DO_RECORD_LFEMUR);
        writer.println("# DO_RECORD_RFEMUR: " + DO_RECORD_RFEMUR);
        //add by milyun at 21 nov 2014
        writer.println("# DO_RECORD_LOWERBACK: " + DO_RECORD_LOWERBACK);
        writer.println("# DO_RECORD_THORAX: " + DO_RECORD_THORAX);
        writer.println("# DO_RECORD_LOWERNECK: " + DO_RECORD_LOWERNECK);
        writer.println("# DO_RECORD_UPPERNECK: " + DO_RECORD_UPPERNECK);
        writer.println("# ");

    }

    void showRootData() {

        if (DO_RECORD_ROOT && rootData.size() > 0) {
            ArrayList<String> rotXs = new ArrayList<>();
            ArrayList<String> rotYs = new ArrayList<>();
            ArrayList<String> rotZs = new ArrayList<>();
            int frame = 0;
            for (rootDatum rd : rootData) {
                String rotx = frame + " " + rd.rot.x;
                String roty = frame + " " + rd.rot.y;
                String rotz = frame + " " + rd.rot.z;
                rotXs.add(rotx);
                rotYs.add(roty);
                rotZs.add(rotz);
                frame++;
            }

            LinePlotter xPlotter = new LinePlotter("root RX");
            LinePlotter yPlotter = new LinePlotter("root RY");
            LinePlotter zPlotter = new LinePlotter("root RZ");

            LinesReader xReader = new LinesReader().invoke(rotXs);
            LinesReader yReader = new LinesReader().invoke(rotYs);
            LinesReader zReader = new LinesReader().invoke(rotZs);

            xPlotter.plotLine("root RX", xReader.getX(), xReader.getY());
            yPlotter.plotLine("root RY", yReader.getX(), yReader.getY());
            zPlotter.plotLine("root RZ", zReader.getX(), zReader.getY());

            xPlotter.setAxisLabel("frame", "angle(rad)");
            yPlotter.setAxisLabel("frame", "angle(rad)");
            zPlotter.setAxisLabel("frame", "angle(rad)");

            xPlotter.savePlot("root_rx");
            yPlotter.savePlot("root_ry");
            zPlotter.savePlot("root_rz");

            float rotXs_std = computeSmoothness(rootRXs);
            float rotYs_std = computeSmoothness(rootRYs);
            float rotZs_std = computeSmoothness(rootRZs);

            assert writer != null;
            writer.println("# rotXs std: "+ rotXs_std);
            writer.println("# rotYs std: "+ rotYs_std);
            writer.println("# rotZs std: "+ rotZs_std);

            totalSmoothness.add(rotXs_std);
            totalSmoothness.add(rotYs_std);
            totalSmoothness.add(rotZs_std);

        }
    }

    private float computeSmoothness(ArrayList<Float> list) {

        // Get a DescriptiveStatistics instance
        DescriptiveStatistics stats = new DescriptiveStatistics();

        // measure smoothness
        for (int i = 1; i < list.size(); i++) {
            float prev = list.get(i-1);
            float curr = list.get(i);
            float diff = curr - prev;
            stats.addValue(diff);
        }

        // Compute some statistics
        return (float) stats.getStandardDeviation();

    }

    void showLhandData(String bone) {
        if (DO_RECORD_LHAND && lhandData.size() > 0) {
            plotTwoDofData(lhandData, bone, "frame", "angle(rad)");
            float lhandRX_std = computeSmoothness(lhandRXs);
            float lhandRZ_std = computeSmoothness(lhandRZs);
            assert writer != null;
            writer.println("# lhandRX_std: "+ lhandRX_std);
            writer.println("# lhandRZ_std: "+ lhandRZ_std);
            totalSmoothness.add(lhandRX_std);
            totalSmoothness.add(lhandRZ_std);
        }
    }

    void showRhandData(String bone) {
        if (DO_RECORD_LHAND && rhandData.size() > 0) {
            plotTwoDofData(rhandData, bone, "frame", "angle(rad)" );
            float rhandRX_std = computeSmoothness(rhandRXs);
            float rhandRZ_std = computeSmoothness(rhandRZs);
            assert writer != null;
            writer.println("# lhandRX_std: "+ rhandRX_std);
            writer.println("# lhandRZ_std: "+ rhandRZ_std);
            totalSmoothness.add(rhandRX_std);
            totalSmoothness.add(rhandRZ_std);
        }
    }

    void showLFootData(String bone) {
        if (DO_RECORD_LFOOT && lfootData.size() > 0) {
            plotTwoDofData(lfootData, bone, "frame", "angle(rad)");
            float lfootRX_std = computeSmoothness(lfootRXs);
            float lfootRZ_std = computeSmoothness(lfootRZs);
            assert writer != null;
            writer.println("# lfootRX_std: " + lfootRX_std);
            writer.println("# lfootRZ_std: " + lfootRZ_std);
            totalSmoothness.add(lfootRX_std);
            totalSmoothness.add(lfootRZ_std);
        }
    }

    void showRFootData(String bone) {
        if (DO_RECORD_RFOOT && rfootData.size() > 0) {
            plotTwoDofData(rfootData, bone, "frame", "angle(rad)");
            float rfootRX_std = computeSmoothness(rfootRXs);
            float rfootRZ_std = computeSmoothness(rfootRZs);
            assert writer != null;
            writer.println("# rfootRX_std: " + rfootRX_std);
            writer.println("# rfootRZ_std: " + rfootRZ_std);
            totalSmoothness.add(rfootRX_std);
            totalSmoothness.add(rfootRZ_std);
        }
    }

    //add by milyun at 17 nov 2014
    void showLHumerusData(String bone) {
        if (DO_RECORD_LHUMERUS && lhumerusData.size() > 0) {
            plotThreeDofData(lhumerusData, bone, "frame", "angle(rad)");
            float lhumerusRX_std = computeSmoothness(lhumerusRXs);
            float lhumerusRY_std = computeSmoothness(lhumerusRYs);
            float lhumerusRZ_std = computeSmoothness(lhumerusRZs);
            assert writer != null;
            writer.println("# lhumerusRX_std: " + lhumerusRX_std);
            writer.println("# lhumerusRY_std: " + lhumerusRY_std);
            writer.println("# lhumerusRZ_std: " + lhumerusRZ_std);
            totalSmoothness.add(lhumerusRX_std);
            totalSmoothness.add(lhumerusRY_std);
            totalSmoothness.add(lhumerusRZ_std);
        }
    }

    void showRHumerusData(String bone) {
        if (DO_RECORD_RHUMERUS && rhumerusData.size() > 0) {
            plotThreeDofData(rhumerusData, bone, "frame", "angle(rad)");
            float rhumerusRX_std = computeSmoothness(rhumerusRXs);
            float rhumerusRY_std = computeSmoothness(rhumerusRYs);
            float rhumerusRZ_std = computeSmoothness(rhumerusRZs);
            assert writer != null;
            writer.println("# rhumerusRX_std: " + rhumerusRX_std);
            writer.println("# rhumerusRY_std: " + rhumerusRY_std);
            writer.println("# rhumerusRZ_std: " + rhumerusRZ_std);
            totalSmoothness.add(rhumerusRX_std);
            totalSmoothness.add(rhumerusRY_std);
            totalSmoothness.add(rhumerusRZ_std);
        }
    }
    void showLFemurData(String bone) {
        if (DO_RECORD_LFEMUR && lfemurData.size() > 0) {
            plotThreeDofData(lfemurData, bone, "frame", "angle(rad)");
            float lfemurRX_std = computeSmoothness(lfemurRXs);
            float lfemurRY_std = computeSmoothness(lfemurRYs);
            float lfemurRZ_std = computeSmoothness(lfemurRZs);
            assert writer != null;
            writer.println("# lhumerusRX_std: " + lfemurRX_std);
            writer.println("# lhumerusRY_std: " + lfemurRY_std);
            writer.println("# lhumerusRZ_std: " + lfemurRZ_std);
            totalSmoothness.add(lfemurRX_std);
            totalSmoothness.add(lfemurRY_std);
            totalSmoothness.add(lfemurRZ_std);
        }
    }

    void showRFemurData(String bone) {
        if (DO_RECORD_RFEMUR && rfemurData.size() > 0) {
            plotThreeDofData(rfemurData, bone, "frame", "angle(rad)");
            float rfemurRX_std = computeSmoothness(rfemurRXs);
            float rfemurRY_std = computeSmoothness(rfemurRYs);
            float rfemurRZ_std = computeSmoothness(rfemurRZs);
            assert writer != null;
            writer.println("# rfemurRX_std: " + rfemurRX_std);
            writer.println("# rfemurRY_std: " + rfemurRY_std);
            writer.println("# rfemurRZ_std: " + rfemurRZ_std);
            totalSmoothness.add(rfemurRX_std);
            totalSmoothness.add(rfemurRY_std);
            totalSmoothness.add(rfemurRZ_std);
        }
    }

    //add by milyun at 21 nov 2014
    void showLowerbackData(String bone) {
        if (DO_RECORD_LOWERBACK && lowerbackData.size() > 0) {
            plotThreeDofData(lowerbackData, bone, "frame", "angle(rad)");
            float lowerbackRX_std = computeSmoothness(lowerbackRXs);
            float lowerbackRY_std = computeSmoothness(lowerbackRYs);
            float lowerbackRZ_std = computeSmoothness(lowerbackRZs);
            assert writer != null;
            writer.println("# lowerbackRX_std: " + lowerbackRX_std);
            writer.println("# lowerbackRY_std: " + lowerbackRY_std);
            writer.println("# lowerbackRZ_std: " + lowerbackRZ_std);
            totalSmoothness.add(lowerbackRX_std);
            totalSmoothness.add(lowerbackRY_std);
            totalSmoothness.add(lowerbackRZ_std);
        }
    }

    void showThoraxData(String bone) {
        if (DO_RECORD_THORAX && thoraxData.size() > 0) {
            plotThreeDofData(thoraxData, bone, "frame", "angle(rad)");
            float thoraxRX_std = computeSmoothness(thoraxRXs);
            float thoraxRY_std = computeSmoothness(thoraxRYs);
            float thoraxRZ_std = computeSmoothness(thoraxRZs);
            assert writer != null;
            writer.println("# thoraxRX_std: " + thoraxRX_std);
            writer.println("# thoraxRY_std: " + thoraxRY_std);
            writer.println("# thoraxRZ_std: " + thoraxRZ_std);
            totalSmoothness.add(thoraxRX_std);
            totalSmoothness.add(thoraxRY_std);
            totalSmoothness.add(thoraxRZ_std);
        }
    }

    void showLowerneckData(String bone) {
        if (DO_RECORD_LOWERNECK && lowerneckData.size() > 0) {
            plotThreeDofData(lowerneckData, bone, "frame", "angle(rad)");
            float lowerneckRX_std = computeSmoothness(thoraxRXs);
            float lowerneckRY_std = computeSmoothness(thoraxRYs);
            float lowerneckRZ_std = computeSmoothness(thoraxRZs);
            assert writer != null;
            writer.println("# lowerneckRX_std: " + lowerneckRX_std);
            writer.println("# lowerneckRY_std: " + lowerneckRY_std);
            writer.println("# lowerneckRZ_std: " + lowerneckRZ_std);
            totalSmoothness.add(lowerneckRX_std);
            totalSmoothness.add(lowerneckRY_std);
            totalSmoothness.add(lowerneckRZ_std);
        }
    }

    void showUpperneckData(String bone) {
        if (DO_RECORD_UPPERNECK && lowerneckData.size() > 0) {
            plotThreeDofData(upperneckData, bone, "frame", "angle(rad)");
            float upperneckRX_std = computeSmoothness(upperneckRXs);
            float upperneckRY_std = computeSmoothness(upperneckRYs);
            float upperneckRZ_std = computeSmoothness(upperneckRZs);
            assert writer != null;
            writer.println("# upperneckRX_std: " + upperneckRX_std);
            writer.println("# upperneckRY_std: " + upperneckRY_std);
            writer.println("# upperneckRZ_std: " + upperneckRZ_std);
            totalSmoothness.add(upperneckRX_std);
            totalSmoothness.add(upperneckRY_std);
            totalSmoothness.add(upperneckRZ_std);
        }
    }

    private void plotTwoDofData(ArrayList<twoDofDatum> twoDofData,
                                String bone,
                                String lx, String ly) {
        ArrayList<String> rotXs = new ArrayList<>();
        ArrayList<String> rotZs = new ArrayList<>();
        int frame = 0;
        for (twoDofDatum rd : twoDofData) {
            String rotX = frame + " " + rd.dof.x;
            String rotZ = frame + " " + rd.dof.y;
            rotXs.add(rotX);
            rotZs.add(rotZ);
            frame++;
        }

        LinePlotter linePlotter = new LinePlotter(bone);


        LinesReader xReader = new LinesReader().invoke(rotXs);
        double[] Xx = xReader.getX();
        double[] Xy = xReader.getY();
        linePlotter.plotLine("RX", Xx, Xy);

        LinesReader zReader = new LinesReader().invoke(rotZs);
        double[] Zx = zReader.getX();
        double[] Zy = zReader.getY();
        linePlotter.plotLine("RZ", Zx, Zy);

        linePlotter.setAxisLabel(lx, ly);
        linePlotter.savePlot(bone);

//        rxPlotter.plotLine(t1, xReader.getX(), xReader.getY());
//        rzPlotter.plotLine(t2, zReader.getX(), zReader.getY());
//
//        rxPlotter.setAxisLabel(lx, ly);
//        rzPlotter.setAxisLabel(lx, ly);
//
//        rxPlotter.savePlot(t1);
//        rzPlotter.savePlot(t2);

    }

    //add by milyun 17 nov 2014
    private void plotThreeDofData(ArrayList<threeDofDatum> threeDofData,
                                String bone,
                                String lx, String ly) {
        ArrayList<String> rotXs = new ArrayList<>();
        ArrayList<String> rotYs = new ArrayList<>();
        ArrayList<String> rotZs = new ArrayList<>();
        int frame = 0;
        for (threeDofDatum rd : threeDofData) {
            String rotX = frame + " " + rd.dof.x;
            String rotY = frame + " " + rd.dof.y;
            String rotZ = frame + " " + rd.dof.z;
            rotXs.add(rotX);
            rotYs.add(rotY);
            rotZs.add(rotZ);
            frame++;
        }

        LinePlotter linePlotter = new LinePlotter(bone);
//        LinePlotter ryPlotter = new LinePlotter(t2);
//        LinePlotter rzPlotter = new LinePlotter(t3);

        LinesReader xReader = new LinesReader().invoke(rotXs);
        double[] Xx = xReader.getX();
        double[] Xy = xReader.getY();
        linePlotter.plotLine("RX", Xx, Xy);

        LinesReader yReader = new LinesReader().invoke(rotYs);
        double[] Yx = yReader.getX();
        double[] Yy = yReader.getY();
        linePlotter.plotLine("RY", Yx, Yy);

        LinesReader zReader = new LinesReader().invoke(rotZs);
        double[] Zx = zReader.getX();
        double[] Zy = zReader.getY();
        linePlotter.plotLine("RZ", Zx, Zy);

        linePlotter.setAxisLabel(lx, ly);
        linePlotter.savePlot(bone);
//        rxPlotter.plotLine(t1, xReader.getX(), xReader.getY());
//        ryPlotter.plotLine(t2, yReader.getX(), yReader.getY());
//        rzPlotter.plotLine(t3, zReader.getX(), zReader.getY());
//
//        rxPlotter.setAxisLabel(lx, ly);
//        ryPlotter.setAxisLabel(lx, ly);
//        rzPlotter.setAxisLabel(lx, ly);
//
//        rxPlotter.savePlot(t1);
//        ryPlotter.savePlot(t2);
//        rzPlotter.savePlot(t3);

    }

    void measureTotalSmoothness() {
        // Get a DescriptiveStatistics instance
        DescriptiveStatistics stats = new DescriptiveStatistics();

        // measure smoothness
        for (int i = 1; i < totalSmoothness.size(); i++) {
            stats.addValue(totalSmoothness.get(i));
        }

        assert writer != null;
        writer.println("# total_smooth_mean: "+stats.getMean());
        writer.println("# total_smooth_sd: "+stats.getStandardDeviation());

    }

    void closeWriter() {
        assert writer != null;
        writer.close();
    }

}
