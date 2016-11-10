import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class CustomFormatter extends Formatter {

    @NotNull
    @Override
    public String format(@NotNull LogRecord record) {
        StringBuilder sb = new StringBuilder();
        Date date = new Date(record.getMillis());
        sb.append(date.toString()).append(" : ");
        sb.append(record.getLevel()).append(" : ");
        sb.append(record.getSourceClassName()).append(" -:- ");
        sb.append(record.getSourceMethodName()).append(" -:- ");
        sb.append(record.getMessage()).append("\n");

        Throwable throwable = record.getThrown();
        if (throwable != null)
        {
            StringWriter sink = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sink, true));
            sb.append(sink.toString());
        }

        return sb.toString();
    }

}