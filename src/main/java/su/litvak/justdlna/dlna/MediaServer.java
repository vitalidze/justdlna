package su.litvak.justdlna.dlna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import su.litvak.justdlna.Config;

/**
 * Based on a class from WireMe and used under Apache 2 License.
 * See https://code.google.com/p/wireme/ for more details.
 */
public class MediaServer {

	private static final String DEVICE_TYPE = "MediaServer";
	private static final int VERSION = 1;
	private static final Logger LOG = LoggerFactory.getLogger(MediaServer.class);

	private final LocalDevice localDevice;

	public MediaServer(final String hostName) throws ValidationException {
		final DeviceType type = new UDADeviceType(DEVICE_TYPE, VERSION);
		final DeviceDetails details = new DeviceDetails(Config.METADATA_MODEL_NAME + " (" + hostName + ")",
				new ManufacturerDetails(Config.METADATA_MANUFACTURER),
				new ModelDetails(Config.METADATA_MODEL_NAME, Config.METADATA_MODEL_DESCRIPTION, Config.METADATA_MODEL_NUMBER));

		final LocalService<ContentDirectoryService> contDirSrv = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
		contDirSrv.setManager(new DefaultServiceManager<ContentDirectoryService>(contDirSrv, ContentDirectoryService.class) {
			@Override
			protected ContentDirectoryService createServiceInstance () {
				return new ContentDirectoryService();
			}
		});

		final LocalService<ConnectionManagerService> connManSrv = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);
		connManSrv.setManager(new DefaultServiceManager<ConnectionManagerService>(connManSrv, ConnectionManagerService.class));

		final UDN usi = UDN.uniqueSystemIdentifier("justDLNA-MediaServer");
		LOG.info("uniqueSystemIdentifier: {}", usi);
		this.localDevice = new LocalDevice(new DeviceIdentity(usi), type, details, new LocalService[] { contDirSrv, connManSrv });
	}

	public LocalDevice getDevice () {
		return this.localDevice;
	}
}
