package su.litvak.justdlna;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.litvak.justdlna.model.FolderNode;

import java.io.File;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Config {
    public static String APPNAME = "justDLNA";

    public static String METADATA_MANUFACTURER = "Litvak.SU";

    public static String METADATA_MODEL_NAME = "justDLNA";
    public static String METADATA_MODEL_DESCRIPTION = "justDLNA MediaServer";
    public static String METADATA_MODEL_NUMBER = "v1";

    private final static String CONFIG_FILE = System.getProperty("user.dir") + File.separatorChar + "config.json";
    private Logger logger = LoggerFactory.getLogger(Config.class);

    private final static Config INSTANCE = new Config();

    /**
     * Port for http server
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private int httpPort = 8192;
    /**
     * Re-indexing interval (in milliseconds)
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private int refreshInterval = 10 * 60 * 1000;

    private List<FolderNode> folders;

    public static Config get() {
        return INSTANCE;
    }

    private Config() {
        if (new File(CONFIG_FILE).exists()) {
            load();
        }
    }

    public void load() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readerForUpdating(this).readValue(new File(CONFIG_FILE));
        } catch (Exception ex) {
            logger.error("Error occurred while loading config file", ex);
        }
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public List<FolderNode> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderNode> folders) {
        this.folders = folders;
    }
}
