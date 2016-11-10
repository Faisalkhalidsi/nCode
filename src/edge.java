import org.jetbrains.annotations.NotNull;

import java.util.Random;

class edge {
    @NotNull
    static Random rand = new Random();
    //	int wind;

    double w;   // weight
    double dw;  // detla weight
    double v;
    node n1;
    node n2;

    // weight di inisiasu dengan nilai random
    public edge() {
        w = rand.nextGaussian() * 0.01;
    }
}

class edgeLayer {
    edge[] edges;
    int count;
    layer l1, l2;

    public edgeLayer(@NotNull layer l1, @NotNull layer l2) {
        this.l1 = l1;
        this.l2 = l2;
        count = l1.size() * l2.size();
        edges = new edge[count];
        int c = 0;
        for (int i1 = 0; i1 < l1.size(); i1++) {
            for (int i2 = 0; i2 < l2.size(); i2++) {
                edges[c] = new edge();
                edges[c].n1 = l1.get(i1);
                edges[c].n2 = l2.get(i2);
                c++;
            }
        }
    }

    public double calc(double rate) {
        double error = 0;
        for (int i = 0; i < count; i++) {
            double deltaW = edges[i].n1.val * edges[i].n2.val * rate;
            edges[i].dw += deltaW;
            error += deltaW;
        }
        return error;
    }

    public void sendUp() {
        for (int i = 0; i < count; i++)
            edges[i].n2.buff += edges[i].w * edges[i].n1.val;
    }

    public void sendDown() {
        for (int i = 0; i < count; i++)
            edges[i].n1.buff += edges[i].w * edges[i].n2.val;
    }

    public void update(double mom) {
        double accum = 0;
        if (demo.WITH_SPARSITY) {
            for (int i2 = 0; i2 < l2.size(); i2++) {
                double zi = l2.get(i2).val;
                double lzi = 1. / (1. + Math.exp(-zi));
                double oneP = Math.log(1 + lzi * lzi);
                accum += oneP;
            }
        }

        for (int i = 0; i < count; i++) {
            edges[i].v = mom * edges[i].v + edges[i].dw;
            edges[i].dw = 0;
            edges[i].w += edges[i].v;
            edges[i].w -= edges[i].w * 0.000002;
            if (demo.WITH_REGULARIZATION) // regularization term
                edges[i].w += (edges[i].w * edges[i].w) * demo.REGULARIZATION_CONSTANT;
            if (demo.WITH_SPARSITY) // sparsity term
                edges[i].w += accum * demo.SPARSITY_CONSTANT;
        }
    }

    public double getVHWTerm(){
		double e = 0;
		for(int i = 0; i < count; i++) {
            e -= edges[i].w * edges[i].n1.val * edges[i].n2.val;
        }
		return e;
	}

    public double getReconError() {
        double e = 0;
        for (int i1 = 0; i1 < l1.size(); i1++) {
            double diff = l1.get(i1).buff - l1.get(i1).val;
            double diff2 = diff * diff;
            e -= diff2;
        }
        return e;
    }

    public double getBiasTerm() {
        double sum = 0;
        for(int i = 0; i < count; i++)
            sum += edges[i].w * edges[i].n2.val;
        return sum;
    }

    //add 14 dec 2014
    public double getVisible(){
        double e = 0;
        for(int i = 0; i < count; i++)
            e += Math.pow((edges[i].n1.val),2);
        return e;
    }

    public double getSquareTerm() {
        double sum = 0;
        for (int i = 0; i < count; i++)
            sum += edges[i].n1.val * edges[i].n1.val;
        return sum;
    }


}