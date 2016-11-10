/*
 * This represents a group of bones
 */

import org.jetbrains.annotations.NotNull;

public class boneGroup {
    bone bones[];
    int DOF;

    boneGroup(@NotNull String boneNames[]) {
        bones = new bone[boneNames.length];
        for (int i = 0; i < bones.length; i++) {
            bones[i] = new bone(boneNames[i]);
        }
    }

    void readData(@NotNull String[][][] data) {
        for (bone bone : bones) bone.readData(data);
        for (bone bone : bones) DOF += bone.DOF;
    }

    /*
    int getModeNum(){
		return bones[0].getModeNum();
	}
	*/

    int getFrameLength(int mode) {
        return bones[0].getFrameLength(mode);
    }

    int getTotalFrameLength() {
        return bones[0].getTotalFrameLength();
    }
	
    /*
    double getOrigVal(int mode,int fr,int di){
        for (bone bone : bones) {
            if (di < bone.DOF) {
                return bone.dataOrig[mode][fr][di];
            }
            di -= bone.DOF;
        }
		throw new IndexOutOfBoundsException();	
	}
	*/

    double getNormVal(int mode, int fr, int di) {
        for (bone bone : bones) {
            if (di < bone.DOF) {
                return bone.dataNorm[mode][fr][di];
            }
            di -= bone.DOF;
        }
        throw new IndexOutOfBoundsException();
    }
	
    /*
    double[][] getOrigData(int mode){
		double data[][] = new double[getFrameLength(mode)][DOF];
		int d = 0;
        for (bone bone : bones) {
            for (int fr = 0; fr < data.length; fr++) {
                System.arraycopy(bone.dataOrig[mode][fr], 0, data[fr], d, bone.DOF);
            }
            d += bone.DOF;
        }
		return data;
	}
	*/
	
	/*double convertToOrig(int mode,int di,double val){
        for (bone bone : bones) {
            if (di < bone.DOF) {
                return bone.SD[di] * val + bone.mean[di];
            }
            di -= bone.DOF;
        }
		throw new IndexOutOfBoundsException();
	}
	*/

    void printAMC(double data[], int offset, @NotNull StringBuilder sb) {
        for (bone bone : bones) {
            bone.printAMC(data, offset, sb);
            offset += bone.DOF;
        }
    }
}
