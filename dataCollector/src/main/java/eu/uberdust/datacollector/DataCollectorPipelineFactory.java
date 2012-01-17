package eu.uberdust.datacollector;

import de.uniluebeck.itm.gtr.messaging.Messages;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/1/11
 * Time: 5:14 PM
 */
public class DataCollectorPipelineFactory implements ChannelPipelineFactory {

    /**
     * Chanel handler that receives the messages and Generates parser threads.
     */
    private final transient DataCollectorChannelUpstreamHandler upstreamHandler;
    private String testbedPrefix;
    private int testbedId;

    /**
     * @param dataCollector a datacollector object
     */
    public DataCollectorPipelineFactory(final DataCollector dataCollector) {
        upstreamHandler = new DataCollectorChannelUpstreamHandler(dataCollector);
    }

    /**
     * a decoder size limit.
     */
    public static final int MAX_LEN = 1048576;
    /**
     * constant parameter.
     */
    public static final int FLD_LEN = 4;
    /**
     * constant parameter.
     */
    public static final int STRIP = 4;

    @Override
    public final ChannelPipeline getPipeline() {

        final ChannelPipeline channelPipeline = pipeline();

        channelPipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_LEN, 0, FLD_LEN, 0, STRIP));
        channelPipeline.addLast("pbfEnvelopeMessageDec", new ProtobufDecoder(Messages.Msg.getDefaultInstance()));

        channelPipeline.addLast("frameEncoder", new LengthFieldPrepender(FLD_LEN));
        channelPipeline.addLast("protobufEncoder", new ProtobufEncoder());


        channelPipeline.addLast("handler", upstreamHandler);

        return channelPipeline;

    }

}
