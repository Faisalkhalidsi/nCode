/*
 * Class for reading .amc motion files
 */

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class dataset {

    boneGroup bodyParts[]; // [bodyBones.length, i.e., 5]
    int modeNum;

    // we divided joint-angles into 5 parts, and trained 5 separate RBMs with them
    @NotNull
//    String bodyBones[][] = {
//            {"root"},
//            {"lowerback"},
//            {"upperback"},
//            {"thorax"},
//            {"lowerneck"},
//            {"upperneck"},
//            {"head"},
//            //////////////////
//            {"rclavicle"},
//            {"rhumerus"},
//            {"rradius"},
//            {"rwrist"},
//            {"rhand"},
//            {"rfingers"},
//            {"rthumb"},
//            {"lclavicle"},
//            {"lhumerus"},
//            {"lradius"},
//            {"lwrist"},
//            {"lhand"},
//            {"lfingers"},
//            {"lthumb"},
//            //////////////////////
//            {"rfemur"},
//            {"rtibia"},
//            {"rfoot"},
//            {"rtoes"},
//            {"lfemur"},
//            {"ltibia"},
//            {"lfoot"},
//            {"ltoes"}
//
//    };
//    String bodyBones[][] = {
//            {"root", "lowerback", "upperback", "thorax", "lowerneck", "upperneck", "head"},
//            {"rclavicle", "rhumerus", "rradius", "rwrist", "rhand", "rfingers", "rthumb",
//                    "lclavicle", "lhumerus", "lradius", "lwrist", "lhand", "lfingers", "lthumb"},
//            {"rfemur", "rtibia", "rfoot", "rtoes","lfemur", "ltibia", "lfoot", "ltoes"}
//
//    };
    String bodyBones[][] = {
            {"root", "lowerback", "upperback", "thorax", "lowerneck", "upperneck", "head"},
            {"rclavicle", "rhumerus", "rradius", "rwrist", "rhand", "rfingers", "rthumb"},
            {"lclavicle", "lhumerus", "lradius", "lwrist", "lhand", "lfingers", "lthumb"},
            {"rfemur", "rtibia", "rfoot", "rtoes"},
            {"lfemur", "ltibia", "lfoot", "ltoes"}
    };

    dataset(@NotNull ArrayList<URL> bases) throws IOException {
        BufferedReader amc;
        String line;
        String[][][] data; // [amcFile][line][token]

        modeNum = bases.size();
        data = new String[modeNum][][];

        for (int pi = 0; pi < modeNum; pi++) {
            int ln = 0;
            amc = new BufferedReader(new FileReader(bases.get(pi).getFile()));
            while (amc.readLine() != null) {
                ln++;
            }
            data[pi] = new String[ln][];
            amc = new BufferedReader(new FileReader(bases.get(pi).getFile()));
            ln = 0;
            while ((line = amc.readLine()) != null) {
                data[pi][ln] = line.split(" ");
                ln++;
            }
        }

        bodyParts = new boneGroup[bodyBones.length];
        for (int i = 0; i < bodyParts.length; i++) {
            bodyParts[i] = new boneGroup(bodyBones[i]);
        }

        for (boneGroup bodyPart : bodyParts) {
            bodyPart.readData(data);
        }
    }

    int getFrameLength(int mode) {
        return bodyParts[0].getFrameLength(mode);
    }

    int getTotalFrameLength() {
        return bodyParts[0].getTotalFrameLength();
    }

    int getDOF() {
        int DOF = 0;
        for (boneGroup bodyPart : bodyParts) {
            DOF += bodyPart.DOF;
        }
        return DOF;
    }

    /*
    double getOrigVal(int mode,int fr,int di){
        for (boneGroup bodyPart : bodyParts) {
            if (di < bodyPart.DOF) return bodyPart.getOrigVal(mode, fr, di);
            di -= bodyPart.DOF;
        }
		throw new IndexOutOfBoundsException();
	}
    */

    /*
    double getOrigVal(int part,int mode,int fr,int di){
		return bodyParts[part].getOrigVal(mode,fr, di);
	}
	*/

    /*
	double getNormVal(int mode,int fr,int di){
        for (boneGroup bodyPart : bodyParts) {
            if (di < bodyPart.DOF) return bodyPart.getNormVal(mode, fr, di);
            di -= bodyPart.DOF;
        }
		throw new IndexOutOfBoundsException();
	}
	*/

    double getNormVal(int part, int mode, int fr, int di) {
        return bodyParts[part].getNormVal(mode, fr, di);
    }
	
    /*
    double[][] getOrigData(int mode){
		double[][] body;
		double[][] part;
		int b = 0;
		body = new double[getFrameLength(mode)][getDOF()];
        for (boneGroup bodyPart : bodyParts) {
            part = bodyPart.getOrigData(mode);

            for (int d = 0; d < part.length; d++) {
                System.arraycopy(part[d], 0, body[d], b + 0, part[d].length);
            }
            b += bodyPart.DOF;
        }
		return body;
	}
	*/


    /*
	double convertToOrig(int mode,int di,double val){
		for(int i = 0; i < bodyParts.length; i++){
			if(di < bodyParts[i].DOF ) return bodyParts[i].convertToOrig(mode, di, val);
			di -= bodyParts[i].DOF;
		}
		throw new IndexOutOfBoundsException();
	}
	*/

    /*
	double convertToOrig(int part,int mode,int di,double val){
		return bodyParts[part].convertToOrig(mode, di, val);	
	}
	*/

    // prints data in .amc format
    void printAMC(@NotNull double data[][], @NotNull StringBuilder sb) {
        double d[];
        int b;
        for (int frame = 0; frame < data.length; frame++) {
            sb.append(frame + 1).append("\n");

            b = 0;
            for (boneGroup bodyPart : bodyParts) {
                d = new double[bodyPart.DOF];
                System.arraycopy(data[frame], b, d, 0, d.length);
                bodyPart.printAMC(d, 0, sb);
                b += bodyPart.DOF;
            }
        }
    }
}
