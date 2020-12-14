package teammates.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import javax.servlet.http.Part;

/**
 * Mocks {@link Part} for testing purpose.
 *
 * <p>Only important methods are modified here; everything else are auto-generated.
 */
public class MockPart implements Part {

    private final String contentType;
    private final InputStream inputStream;
    private final long size;
    private final String name;

    public MockPart(String filePath) throws IOException {
        File file = new File(filePath);
        this.contentType = URLConnection.guessContentTypeFromName(file.getName());
        this.inputStream = Files.newInputStream(Paths.get(filePath));
        this.size = file.length();
        this.name = file.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSubmittedFileName() {
        return this.name;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public void write(String fileName) throws IOException {
        // not used
    }

    @Override
    public void delete() throws IOException {
        // not used
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

}
