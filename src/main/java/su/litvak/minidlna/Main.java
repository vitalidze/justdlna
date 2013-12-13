package su.litvak.minidlna;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import su.litvak.minidlna.dlna.MediaServer;
import su.litvak.minidlna.model.NodeMap;
import su.litvak.minidlna.model.RootNode;
import su.litvak.minidlna.provider.FolderContentProvider;
import su.litvak.minidlna.provider.VideoFormat;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.LogManager;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main () {
        throw new AssertionError();
    }

    public static void main(String[] args) throws Exception {
        bridgeJul();

        LOG.info("Reading config file...");
        Config.get();

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
