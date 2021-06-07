package teammates.common.util;

import java.io.OutputStream;
import java.util.logging.ConsoleHandler;

/**
 * {@link ConsoleHandler} implementation that uses {@link System#out} instead of {@link System#err}.
 */
public class StdOutConsoleHandler extends ConsoleHandler {

    @Override
    protected void setOutputStream(OutputStream out) {
        super.setOutputStream(System.out);
    }

}
