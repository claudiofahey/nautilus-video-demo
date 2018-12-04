package io.pravega.example.video.videodemo;

import io.pravega.client.ClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.byteStream.ByteStreamClient;
import io.pravega.client.byteStream.ByteStreamReader;
import io.pravega.client.byteStream.ByteStreamWriter;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import lombok.Cleanup;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class VideoTest {
    private static final Logger log = LoggerFactory.getLogger(VideoTest.class);

    /**
    This writes a video of any length to a Pravega stream.
    First it writes a long representing the length of the video file contents.
    Then it writes the video file contents.
    This function can be run multiple times to append additional video files.
    */
    @Ignore()
    @Test
    public void testWriteVideoToPravega() throws Exception {
        URI controllerURI = URI.create("tcp://localhost:9090");
        StreamManager streamManager = StreamManager.create(controllerURI);
        String scope = "video5";
        streamManager.createScope(scope);
        String streamName = "stream1";
        StreamConfiguration streamConfig = StreamConfiguration.builder()
                .scalingPolicy(ScalingPolicy.fixed(1))
                .build();
        streamManager.createStream(scope, streamName, streamConfig);
        @Cleanup
        ClientFactory clientFactory = ClientFactory.withScope(scope, controllerURI);
        ByteStreamClient byteStreamClient = clientFactory.createByteStreamClient();
        @Cleanup
        ByteStreamWriter writer = byteStreamClient.createByteStreamWriter(streamName);
//        File file = new File("../Wildlife.mp4");   // https://archive.org/download/WildlifeSampleVideo/Wildlife.mp4
        File file = new File("../bike.mp4");
        long fileLength = file.length();
        DataOutputStream dos = new DataOutputStream(writer);
        dos.writeLong(fileLength);
        long bytesWritten = Files.copy(file.toPath(), dos);
        assertEquals(bytesWritten, fileLength);
        dos.close();
        log.info("Wrote {} bytes from {}", bytesWritten, file);
    }

    /**
     This continually reads and decodes video files from a Pravega stream.
     It must be in the format written by testWriteVideoToPravega.
     It will block when it reaches the end of the stream.
     */
    @Ignore()
    @Test
    public void testReadVideoFromPravega() throws Exception {
        URI controllerURI = URI.create("tcp://localhost:9090");
        StreamManager streamManager = StreamManager.create(controllerURI);
        String scope = "video5";
        streamManager.createScope(scope);
        String streamName = "stream1";
        StreamConfiguration streamConfig = StreamConfiguration.builder()
                .scalingPolicy(ScalingPolicy.fixed(1))
                .build();
        streamManager.createStream(scope, streamName, streamConfig);
        @Cleanup
        ClientFactory clientFactory = ClientFactory.withScope(scope, controllerURI);
        ByteStreamClient byteStreamClient = clientFactory.createByteStreamClient();
        @Cleanup
        ByteStreamReader reader = byteStreamClient.createByteStreamReader(streamName);
        @Cleanup
        DataInputStream dis = new DataInputStream(reader);

        for (;;) {
            long offset = reader.getOffset();
            // This will block until the length of the next chunk has been written to Pravega.
            long dataLength = dis.readLong();
            log.info("offset={}, dataLength={}", offset, dataLength);
            PravegaAdapter adapter = new PravegaAdapter(reader, dataLength);
            FrameGrab grab = FrameGrab.createFrameGrab(adapter);
            Picture picture;
            long frameNumber = 0;
            while (null != (picture = grab.getNativeFrame())) {
                frameNumber++;
                log.info("frame {} {}x{} {}", frameNumber, picture.getWidth(), picture.getHeight(), picture.getColor());
                // TODO: process the video frame here
            }
            log.info("Finished reading chunk");
            // We need to reposition the Pravega reader because FrameGrab may not finish at the end of the chunk.
            reader.seekToOffset(offset + dataLength);
        }
    }

}
