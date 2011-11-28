package eu.uberdust.websockets.insert;

import com.caucho.websocket.AbstractWebSocketListener;
import com.caucho.websocket.WebSocketContext;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

/**
 * Insert Reading Web Socket Listener.
 */
public final class InsertReadingWebSocketListener extends AbstractWebSocketListener {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocketListener.class);

    /**
     * Constructor.
     */
    public InsertReadingWebSocketListener() {
        super();
    }

    /**
     * On start of connection.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onStart(final WebSocketContext context) throws IOException {
        super.onStart(context);
        LOGGER.info("onStart()");
    }

    /**
     * On read binary.
     *
     * @param context WebSocketContext instance.
     * @param is InputStream instance.
     * @throws IOException IOException exception.
     */
    public void onReadBinary(final WebSocketContext context, final  InputStream is) throws IOException {
        super.onReadBinary(context, is);
        LOGGER.info("onReadBinary()");
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        LOGGER.info("onReadBinary(): " + writer.toString());
    }

    /**
     * On read text.
     *
     * @param context WebSocketContext instance.
     * @param is InputStream instance.
     * @throws IOException IOException exception.
     */
    public void onReadText(final WebSocketContext context, final Reader is) throws IOException {
        super.onReadText(context, is);
        LOGGER.info("onReadText()");
        StringBuilder stringBuilder = new StringBuilder();
        char[] buf = new char[1024];
        int numRead;
        while((numRead=is.read(buf)) != -1) {
            stringBuilder.append(buf, 0, numRead);
        }
        String data = stringBuilder.toString();
        LOGGER.info("onReadText() : " + data);
    }

    /**
     * On close.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onClose(final WebSocketContext context) throws IOException {
        super.onClose(context);
        LOGGER.info("onClose()");
    }

    /**
     * On disconnect.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onDisconnect(final WebSocketContext context) throws IOException {
        super.onDisconnect(context);
        LOGGER.info("onDisconnect()");

    }

    /**
     * On timeout.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onTimeout(final WebSocketContext context) throws IOException {
        super.onTimeout(context);
        LOGGER.info("onTimeout()");
    }
}
