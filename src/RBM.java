/*
 * Class representing a Restricted Boltzmann Machine
 */
import java.util.Random;
import java.util.logging.Logger;


public class RBM {
	Random rand;
	layer dummyLayer; // dummy layer with one unit with fixed value
	layer visibleLayer;
	layer hiddenLayer;
	edgeLayer vhEdges;
	edgeLayer vBias;
	edgeLayer hBias;
	double lrate = 0.001; // learning rate
	double mom = 0; // momentum
	int batchsz = demo.BATCH_SIZE; // batch size
	int batchcnt = 0;

    private static final String fileName = RBM.class.getName();
    private static final Logger logger = Logger.getLogger(fileName);

	RBM(int vNum,boolean vReal, 
			int hNum,boolean hReal){

        LoggerSetup.setFileHandler(logger, fileName);
//        LoggerSetup.setHandlerLevel(logger, Level.FINE); // may be conflict with TDBN lo level

        logger.info("vNum: " + vNum + ", vReal: " + vReal + ", hNum: " + hNum + ", hReal: " + hReal);

		if(vReal){
			visibleLayer = new relayer(vNum); // Gaussian units
		}else{
			visibleLayer = new bilayer(vNum); // binary units
		}
		if(hReal){
			hiddenLayer = new relayer(hNum);
		}else{
			hiddenLayer = new bilayer(hNum);
		}

		// dummy layer isi nya hanya sebuah unit
        // dimana nilainya satu.
		dummyLayer = new bilayer(1);
		dummyLayer.get(0).val = 1;

		vhEdges = new edgeLayer(visibleLayer, hiddenLayer);
		vBias = new edgeLayer(dummyLayer, visibleLayer);
        // hBias berupa satu buah dummy layer dan hidden layer
        // dimana jumlah hBias sama dengan jumlah unit pada hidden layer
		hBias = new edgeLayer(dummyLayer, hiddenLayer);
		
		rand = new Random();
	}

	// Pada proses training RBM yang pertama
    // yang dilakukan adalah
    // 1. Mengupdate weight dan unit baik visible serta hidden
    // 2. menyimpan free energy dan recontruction error

	double[] train(){
        double posError;
        double negError;
		hUpdate(true);
		posError = edgesCalc(lrate / batchsz); // positive phases
		gibbsSampling(1, true, false);
		negError = edgesCalc(- lrate / batchsz); // negative phase
		double[] returnee = update(); // masukkan regularisasi dan sparsity penalty
        logger.fine("posError: "+ posError);
        logger.fine("negError: "+ negError);
        logger.fine("energy: "+ returnee[0]);
        logger.fine("reconError: "+ returnee[1]);
        return returnee;
	}
	
	synchronized double[] update(){
	    double energy = 0;
        double reconError = 0;
        batchcnt++;
		if(batchcnt == batchsz){
			edgesUpdate(mom);
            energy += edgesEnergy();
            reconError += edgesReconError();
			batchcnt = 0;
		}
        double[] returnee = new double[2];
        returnee[0] = energy;
        returnee[1] = reconError;
        return returnee;
	}
	
	void gibbsSampling(int sample, boolean lastDeterministic,boolean updateHiddenFirst){
		for(int i = 0; i < sample; i++){
			if(updateHiddenFirst){
				hUpdate(true);
				vUpdate(true);
			}else{
				vUpdate(true);
				hUpdate(true);
			}
		}
		if(lastDeterministic){
			if(updateHiddenFirst){
				hUpdate(false);
				vUpdate(false);
			}else{
				vUpdate(false);
				hUpdate(false);
			}
		}
	}
	
	double edgesCalc(double rate){
        double error = vhEdges.calc(rate);
		vBias.calc(rate);
		hBias.calc(rate);
        return error;
	}
	
	void edgesUpdate(double momentum){
		vhEdges.update(momentum);
		vBias.update(momentum);
		hBias.update(momentum);
	}
    double edgesEnergy() {
        double vb = vBias.getBiasTerm();
        double hc = hBias.getBiasTerm();
        return vhEdges.getVHWTerm() - vb - hc;
//        double v2 = vhEdges.getSquareTerm();
//        return 0.5*v2 - vhEdges.getVHWTerm() - vb - hc;
//        return energy;
    }
    double edgesReconError() {
        return vhEdges.getReconError();
    }
	void hUpdate(boolean sample){
        // send up and send down adalah proses dot product
        // dot product adalah sigma hasil perkalian node dengan weightnya
        // hasil dot product disimpan dalam buff pada node target
		hBias.sendUp();
		vhEdges.sendUp();

        // update terbagi dua
        // 1. untuk yang bertipe binary akan di kenakan aktivation function eq: sigmoid
        //    setelah di kenakan acatovation fuction, niali tersebut di simpan dengan nama val.
        // 2. untuk yang bertipe gaussian, tidak dikenakan activation function melain langsung digunakan
		hiddenLayer.update();

        // proses sample (update value pada hidden layer) di lakukan juga terbagi dua
        // 1. jika bertipe binary, diambil suatu nilai random kemudian bandingkan dengan nilai pada val.
        //    apabila random value kecil dari val, maka val akan bernilai 1, jika tidak akan bernilai 0
        // 2. jika bertipe gaussian, maka nilai val akan di jumlahkan dengan suatu nilai random.
		if(sample) hiddenLayer.sample();
	}
	void vUpdate(boolean sample){
		vBias.sendUp();
		vhEdges.sendDown();
		visibleLayer.update();
		if(sample) visibleLayer.sample();
	}
}
