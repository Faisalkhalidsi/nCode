import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by riset on 16/08/14.
 */
public class LoggerSetup {

    public static final String LOG_DIR = "C:/Users/sourceMyLab/Documents/Tesis/nCode/tdbnDemo/log/";

    public static void setFileHandler(Logger logger, String fileName) {
        try {
            FileHandler fileHandler = new FileHandler(LOG_DIR + fileName + ".log", false);
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(true);
        } catch (IOException e) {
            logger.warning("Failed to initialize logger handler.");
        }
    }

    public static void setHandlerLevel(Logger logger, Level level) {
       Handler[] handlers =
                Logger.getLogger( "" ).getHandlers();
        for (Handler handler : handlers) handler.setLevel(level);
        logger.setLevel(level);
    }



}
