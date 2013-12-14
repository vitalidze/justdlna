package su.litvak.justdlna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import su.litvak.justdlna.dlna.MediaServer;
import su.litvak.justdlna.model.RootNode;

import java.net.InetAddress;
import java.util.logging.LogManager;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main () {
        throw new AssertionError();
    }

    public static void main(String[] args) throws Exception {
        bridgeJul();

        LOG.info("Initializing root node...");
        RootNode.get();

        final String hostName = InetAddress.getLocalHost().getHostName();

        LOG.info("hostName: {}", hostName);
        final UpnpService upnpService = new UpnpServiceImpl();
        upnpService.getRegistry().addDevice(new MediaServer(hostName).getDevice());
    }

    public static void bridgeJul() {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
