/*
 * Class representing a bone in motion player
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import processing.core.PApplet;

import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;

class PlayerBone {
    String name;
    int type = 0;
    int ind;
    int root;
    PlayerBone child;
    PlayerBone child2;
    PlayerBone child3;

    float axisX;
    float axisY;
    float axisZ;
    float dirX;
    float dirY;
    float dirZ;
    int dofX;
    int dofY;
    int dofZ;
    float len;

    private static final String fileName = PlayerBone.class.getName();
    private static final Logger logger = Logger.getLogger(PlayerBone.class.getName());

    // read parameters from a .asf format string
    PlayerBone(@NotNull BufferedReader reader) throws Exception {

        LoggerSetup.setFileHandler(logger, fileName);
        LoggerSetup.setHandlerLevel(logger, Level.INFO);

        dofX = 0;
        dofY = 0;
        dofZ = 0;

        String[] words;
        do {
            words = reader.readLine().trim().split("\\s+");
            switch (words[0]) {
                case "id":
                    ind = Integer.parseInt(words[1]);
                    break;
                case "name":
                    name = words[1];
                    break;
                case "direction":
                    dirX = Float.parseFloat(words[1]);
                    dirY = Float.parseFloat(words[2]);
                    dirZ = Float.parseFloat(words[3]);
                    break;
                case "length":
                    len = (float) (Float.parseFloat(words[1]) * 10.0);
                    break;
                case "axis":
                    axisX = PApplet.radians(Float.parseFloat(words[1]));
                    axisY = PApplet.radians(Float.parseFloat(words[2]));
                    axisZ = PApplet.radians(Float.parseFloat(words[3]));
                    if (!words[4].equals("XYZ"))
                        throw new Exception("Error in parsing axis from asf file");
                    break;
                case "dof":
                    for (int i = 1; i < words.length; i++) {
                        switch (words[i]) {
                            case "rx":
                                dofX = 1;
                                break;
                            case "ry":
                                dofY = 1;
                                break;
                            case "rz":
                                dofZ = 1;
                                break;
                        }
                    }
                    break;
            }
        } while (!words[0].equals("end"));
    }

    PlayerBone(String name,
               int _ind, float _len,
               float _dirX, float _dirY, float _dirZ,
               float _axisX, float _axisY, float _axisZ,
               int _dofX, int _dofY, int _dofZ) {
        this.name = name;
        ind = _ind;
        len = _len;
        dirX = _dirX;
        dirY = _dirY;
        dirZ = _dirZ;
        axisX = PApplet.radians(_axisX);
        axisY = PApplet.radians(_axisY);
        axisZ = PApplet.radians(_axisZ);
        dofX = _dofX;
        dofY = _dofY;
        dofZ = _dofZ;
    }

    // draws the bone and its child bones recursively
    void draw(@NotNull PApplet applet, float data[][], @Nullable PlayerBone parent) {

        applet.pushMatrix();

        /*
        if(root == 1) {
			// we ignore position information
			//translate(data[ind][3],data[ind][4],data[ind][5]);
		}
		*/

        if (parent != null) {
            if (parent.axisX != 0) applet.rotateX(-parent.axisX);
            if (parent.axisY != 0) applet.rotateY(-parent.axisY);
            if (parent.axisZ != 0) applet.rotateZ(-parent.axisZ);
        }

        if (axisZ != 0) applet.rotateZ(axisZ);
        if (axisY != 0) applet.rotateY(axisY);
        if (axisX != 0) applet.rotateX(axisX);

        if (data[ind][2] != 0) applet.rotateZ(data[ind][2]);
        if (data[ind][1] != 0) applet.rotateY(data[ind][1]);
        if (data[ind][0] != 0) applet.rotateX(data[ind][0]);


        if (this.type == 1) applet.stroke(255, 0, 0);

        if (len != 0) {
            if (axisX != 0) applet.rotateX(-axisX);
            if (axisY != 0) applet.rotateY(-axisY);
            if (axisZ != 0) applet.rotateZ(-axisZ);

            applet.line(0, 0, 0, len * dirX, len * dirY, len * dirZ);
            applet.translate(len * dirX, len * dirY, len * dirZ);
            applet.noStroke();
            if (this.name.equals("head")) {
                //fill(100);
                applet.sphere(16);
            } else {
                //fill(0,255,0);
                applet.box(8);
            }
            applet.stroke(100);

            if (axisZ != 0) applet.rotateZ(axisZ);
            if (axisY != 0) applet.rotateY(axisY);
            if (axisX != 0) applet.rotateX(axisX);
        }


        applet.sphere(1);

        if (child != null) child.draw(applet, data, this);
        if (child2 != null) child2.draw(applet, data, this);
        if (child3 != null) child3.draw(applet, data, this);

        if (this.type == 1) applet.stroke(0);

        applet.popMatrix();
    }

    // read one testModeFrame data from .amc format string
    float[] readData(float data[][], @NotNull String words[]) {
        int c = 1;
        try {
            if (root == 1) {
                assert words.length > 1;
                data[ind][3] = (float) (Float.parseFloat(words[c]) * 10.0);
                c++;
                assert words.length > 1;
                data[ind][4] = (float) (Float.parseFloat(words[c]) * 10.0);
                c++;
                assert words.length > 1;
                data[ind][5] = (float) (Float.parseFloat(words[c]) * 10.0);
                c++;
            }

            if (dofX == 1) {
                assert words.length > 1;
                String s = words[c];
                float angle = Float.parseFloat(s);
                data[ind][0] = PApplet.radians(angle);
                c++;
            } else {
                data[ind][0] = 0;
            }
            if (dofY == 1) {
                assert words.length > 1;
                String s = words[c];
                float angle = Float.parseFloat(s);
                data[ind][1] = PApplet.radians(angle);
                c++;
            } else {
                data[ind][1] = 0;
            }
            if (dofZ == 1) {
                assert words.length > 1;
                String s = words[c];
                float angle = Float.parseFloat(s);
                data[ind][2] = PApplet.radians(angle);
                c++;
            } else {
                data[ind][2] = 0;
            }
            assert c == words.length;
        }
        catch(IndexOutOfBoundsException e) {
            logger.warning("e.msg: "+e.getMessage());
            logger.warning("e.cause: "+e.getCause());
        }
        return data[ind];
    }

}

