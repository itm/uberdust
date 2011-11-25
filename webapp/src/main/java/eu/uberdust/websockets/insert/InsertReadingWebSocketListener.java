package eu.uberdust.websockets.insert;

import com.caucho.websocket.AbstractWebSocketListener;
import com.caucho.websocket.WebSocketContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/25/11
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class InsertReadingWebSocketListener extends AbstractWebSocketListener {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocketListener.class);

    public InsertReadingWebSocketListener() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onStart(WebSocketContext context) throws IOException {
        super.onStart(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onReadBinary(WebSocketContext context, InputStream is) throws IOException {
        super.onReadBinary(context, is);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onReadText(WebSocketContext context, Reader is) throws IOException {
        super.onReadText(context, is);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onClose(WebSocketContext context) throws IOException {
        super.onClose(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDisconnect(WebSocketContext context) throws IOException {
        super.onDisconnect(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onTimeout(WebSocketContext context) throws IOException {
        super.onTimeout(context);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
