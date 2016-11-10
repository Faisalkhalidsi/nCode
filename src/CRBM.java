/*
 * Conditional RBM
 * It has additional connections between past visible and visible layers.
 */

public class CRBM extends RBM {
    layer conditionLayer;
    edgeLayer cvEdges;
    int degree;

    CRBM(int vNum, boolean vReal,
         int hNum, boolean hReal,
         int degree) {
        super(vNum, vReal, hNum, hReal);
        if (vReal) {
            conditionLayer = new relayer(degree, vNum);
        } else {
            conditionLayer = new bilayer(degree, vNum);
        }
        cvEdges = new edgeLayer(conditionLayer, visibleLayer);
        this.degree = degree;
    }

    double edgesCalc(double rate) {
        double error = super.edgesCalc(rate);
        cvEdges.calc((float) rate);
        return error;
    }

    void edgesUpdate(double momentum) {
        super.edgesUpdate(momentum);
        cvEdges.update((float) momentum);
    }

    double edgesEnergy() {
        return super.edgesEnergy();
    }

    void vUpdate(boolean sample) {
        cvEdges.sendUp();
        super.vUpdate(sample);
    }

    void passValues() {
        for (int i = 0; i < degree; i++) {
            for (int j = 0; j < visibleLayer.size(); j++)
                if (i < degree - 1)
                    conditionLayer.get(i, j).val = conditionLayer.get(i + 1, j).val;
                else {
                    conditionLayer.get(i, j).val = visibleLayer.get(j).val;
                    visibleLayer.get(j).val += rand.nextGaussian() * 0.01;
                }
        }
    }
}
