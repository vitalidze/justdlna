package su.litvak.justdlna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import su.litvak.justdlna.dlna.MediaServer;
import su.litvak.justdlna.http.Server;
import su.litvak.justdlna.model.ContainerNode;
import su.litvak.justdlna.model.NodesMap;
import su.litvak.justdlna.model.ViewLog;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.LogManager;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main () {
        throw new AssertionError();
    }

    public static void main(String[] args) throws Exception {
        bridgeJul();

        /**
         * Set up IP address
         */
        if (Config.get().getIpAddress() == null) {
            List<InetAddress> ipAddresses = getIpAddresses();
            Config.get().setIpAddress(ipAddresses.get(0).getHostAddress());
        }

        /**
         * Will bind to a single IP address
         */
        System.setProperty("org.teleal.cling.network.useAddresses", Config.get().getIpAddress());

        /**
         * Create view log database
         */
        LOG.info("Initializing view log...");
        ViewLog.init();

        /**
         * Initialize root node
         */
        LOG.info("Initializing root node...");
        ContainerNode rootNode = Config.get().getContent();
        rootNode.setId("0");
        NodesMap.put(rootNode.getId(), rootNode);

        /**
         * Start up UPNP service
         */
        final String hostName = InetAddress.getLocalHost().getHostName();
        LOG.info("hostName: {}", hostName);

        final UpnpService upnpService = new UpnpServiceImpl();
        upnpService.getRegistry().addDevice(new MediaServer(hostName).getDevice());

        /**
         * Start up content serving service
         */
        LOG.info("Starting HTTP server...");
        final Server server = new Server();
        server.start();
        LOG.info("HTTP server started");

        /**
         * Register shutdown hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Shutting down " + Config.APPNAME);

                LOG.info("Shutting down Cling UPNP service");
                upnpService.shutdown();
                LOG.info("Shutting down HTTP server");
                try {
                    server.stop();
                } catch (Exception ex) {
                    LOG.error("Error occurred during HTTP server shutdown", ex);
                }
            }
        });
    }

    private static void bridgeJul() {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static List<InetAddress> getIpAddresses () throws SocketException {
        final List<InetAddress> addresses = new ArrayList<InetAddress>();
        for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
            final NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback()) continue;
            for (final InterfaceAddress ifaceAddr : iface.getInterfaceAddresses()) {
                final InetAddress inetAddr = ifaceAddr.getAddress();
                if (!(inetAddr instanceof Inet4Address)) continue;
                addresses.add(inetAddr);
            }
        }
        return addresses;
    }
}
