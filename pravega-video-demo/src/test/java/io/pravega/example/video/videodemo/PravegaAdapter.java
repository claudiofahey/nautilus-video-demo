package io.pravega.example.video.videodemo;

import io.pravega.client.byteStream.ByteStreamReader;
import org.jcodec.common.io.SeekableByteChannel;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PravegaAdapter implements SeekableByteChannel {
    private ByteStreamReader reader;
    private long size;
    private long offset;

    public PravegaAdapter(ByteStreamReader reader, long size) {
        this.reader = reader;
        this.size = size;
        this.offset = reader.getOffset();
    }

    @Override
    public long position() throws IOException {
        return reader.getOffset() - offset;
    }

    @Override
    public SeekableByteChannel setPosition(long newPosition) throws IOException {
        reader.seekToOffset(newPosition + offset);
        return this;
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return reader.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        return reader.isOpen();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
