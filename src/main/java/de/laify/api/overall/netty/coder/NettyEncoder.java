package de.laify.api.overall.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class NettyEncoder extends MessageToByteEncoder<Serializable> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable obj, ByteBuf out) {
        byte[] data = SerializationUtils.serialize(obj);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

}
