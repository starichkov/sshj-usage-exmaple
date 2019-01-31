package org.starichkov.java.ssh;

import net.schmizz.sshj.xfer.InMemorySourceFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Vadim Starichkov
 * @since 31.01.2019 12:20
 */
public class ByteArraySourceFile extends InMemorySourceFile {
    private final String name;
    private final byte[] bytes;

    public ByteArraySourceFile(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes == null ? new byte[0] : bytes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getLength() {
        return bytes.length;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }
}
