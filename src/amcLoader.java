import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

class amcLoader implements Serializable {

    private static final String fileName = amcLoader.class.getName();
    private static final Logger logger = Logger.getLogger(fileName);

    public amcLoader() {
        LoggerSetup.setFileHandler(logger, fileName);
        LoggerSetup.setHandlerLevel(logger, Level.FINE);
    }// loads .amc file into a string array

    @NotNull
    String[][] loadAMC(@NotNull String path) throws IOException {

        logger.fine("path: " + path);
        BufferedReader amc = new BufferedReader(new FileReader(path));
        String line;
        String data[][] = new String[10000][100];
        int frame = -1;
        int boneCnt = 0;
        while (true) {
            line = amc.readLine();
            if (line == null) break;
            if (line.charAt(0) == '#') continue;
            if (line.charAt(0) == ':') continue;
            if (line.split(" ").length == 1) continue;
            if (line.startsWith("root")) {
                boneCnt = 0;
                frame++;
            }
            data[frame][boneCnt] = line;
            boneCnt++;
        }

        return data;
    }
}