package teammates.common.datatransfer.logs;

import java.util.Objects;

/**
 * Represents a location of source code that produces a log line.
 */
public class SourceLocation {
    private final String file;
    private final Long line;
    private final String function;

    public SourceLocation(String file, Long line, String function) {
        this.file = file;
        this.line = line;
        this.function = function;
    }

    public String getFile() {
        return file;
    }

    public Long getLine() {
        return line;
    }

    public String getFunction() {
        return function;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SourceLocation) {
            SourceLocation other = (SourceLocation) obj;
            return file.equals(other.getFile())
                    && line.equals(other.getLine())
                    && function.equals(other.getFunction());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, line, function);
    }
}
