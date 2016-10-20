package me.zuichu.mp4coder.boxes.sampleentry;

import me.zuichu.mp4coder.BoxParser;
import me.zuichu.mp4coder.tools.IsoTypeReader;
import me.zuichu.mp4coder.tools.IsoTypeWriter;
import me.zuichu.mp4coder.tools.CastUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * <h1>4cc = "{@value #TYPE}"</h1>
 */
public class Ovc1VisualSampleEntryImpl extends AbstractSampleEntry {
    public static final String TYPE = "ovc1";
    private byte[] vc1Content = new byte[0];

    public Ovc1VisualSampleEntryImpl() {
        super(TYPE);
    }

    public byte[] getVc1Content() {
        return vc1Content;
    }

    public void setVc1Content(byte[] vc1Content) {
        this.vc1Content = vc1Content;
    }

    @Override
    public void parse(ReadableByteChannel dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(CastUtils.l2i(contentSize));
        dataSource.read(byteBuffer);
        byteBuffer.position(6);
        dataReferenceIndex = IsoTypeReader.readUInt16(byteBuffer);
        vc1Content = new byte[byteBuffer.remaining()];
        byteBuffer.get(vc1Content);
    }


    @Override
    public void getBox(WritableByteChannel writableByteChannel) throws IOException {
        writableByteChannel.write(getHeader());
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.position(6);
        IsoTypeWriter.writeUInt16(byteBuffer, dataReferenceIndex);
        writableByteChannel.write((ByteBuffer) byteBuffer.rewind());
        writableByteChannel.write(ByteBuffer.wrap(vc1Content));
    }

    @Override
    public long getSize() {
        long header = (largeBox || (vc1Content.length + 16) >= (1L << 32)) ? 16 : 8;
        return header + vc1Content.length + 8;
    }

}