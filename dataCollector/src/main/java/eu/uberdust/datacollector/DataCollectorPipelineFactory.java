package eu.uberdust.datacollector;

import de.uniluebeck.itm.gtr.messaging.Messages;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.Map;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Custom
 */
public class DataCollectorPipelineFactory implements ChannelPipelineFactory {

    /**
     * Chanel handler that receives the messages and Generates parser threads.
     */
    private final transient DataCollectorChannelUpstreamHandler upstreamHandler;
    private DataCollector dataCollector;

    public DataCollectorPipelineFactory(DataCollector dataCollector) {
        this.dataCollector = dataCollector;
        upstreamHandler = new DataCollectorChannelUpstreamHandler(dataCollector);
    }

    public void setSensors(final Map<String, String> sensors) {
        upstreamHandler.setSensors(sensors);
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
    public ChannelPipeline getPipeline() {

        final ChannelPipeline channelPipeline = pipeline();

        channelPipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_LEN, 0, FLD_LEN, 0, STRIP));
        channelPipeline.addLast("pbfEnvelopeMessageDec", new ProtobufDecoder(Messages.Msg.getDefaultInstance()));

        channelPipeline.addLast("frameEncoder", new LengthFieldPrepender(FLD_LEN));
        channelPipeline.addLast("protobufEncoder", new ProtobufEncoder());


        channelPipeline.addLast("handler", upstreamHandler);

        return channelPipeline;

    }


}
