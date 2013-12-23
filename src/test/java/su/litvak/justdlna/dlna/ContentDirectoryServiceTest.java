package su.litvak.justdlna.dlna;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;
import static su.litvak.justdlna.util.FileHelper.*;

public class ContentDirectoryServiceTest {
    private final static String ROOT_ID = "0";
    @Rule public TemporaryFolder tmp = new TemporaryFolder();

    private ContentDirectoryService service;
    private DIDLParser parser;

	@Before
	public void initService () throws Exception {
		this.service = new ContentDirectoryService();
	}

    @Before
    public void initParser() throws Exception {
        this.parser = new DIDLParser();
    }

    @Before
    public void cleanConfig() throws Exception {
        Config.get().getFolders().clear();
    }

	@Test
	public void checkBrowsePaging() throws Exception {
        FolderNode<VideoFormat> folderNode = mockDir("Video", VideoFormat.class);
        NodesMap.put(ROOT_ID, new RootNode(Arrays.asList(folderNode)));
        mockFile("Sub file", VideoFormat.MKV, mockDir("Sub Dir 1", folderNode));
        mockFile("Sub file", VideoFormat.MPEG, mockDir("Sub Dir 2", folderNode));
        for (int i = 0; i < 49; i++) {
            mockFile("Test " + (i < 10 ? "0" : "") + i, VideoFormat.AVI, folderNode);
        }

        /**
         * Browse root to initialize sub-folders
         */
        service.browse(ROOT_ID, BrowseFlag.DIRECT_CHILDREN, null, 0, 1, null);
        /**
         * Browse sub folder
         */
		BrowseResult ret = service.browse(folderNode.getId(), BrowseFlag.DIRECT_CHILDREN, null, 49, 10, null);
        DIDLContent didl = parser.parse(ret.getResult());

        assertEquals(2, didl.getItems().size());
        assertEquals(0, didl.getContainers().size());
        assertEquals("Test 47.avi", didl.getItems().get(0).getTitle());
        assertEquals("Test 48.avi", didl.getItems().get(1).getTitle());
	}

    private <T extends Enum<T> & MediaFormat> FolderNode<T> mockDir(final String name, Class<T> formatClass) {
        return mockDir(name, formatClass, this.tmp.getRoot());
    }

    private <T extends Enum<T> & MediaFormat> FolderNode<T> mockDir(final String name, FolderNode<T> parent) {
        return mockDir(name, parent.getFormatClass(), parent.getFolder());
    }

    private <T extends Enum<T> & MediaFormat> FolderNode<T> mockDir(final String name, Class<T> formatClass, File parent) {
        File d = new File(parent, name);
        d.mkdirs();
        return new FolderNode<T>(d.getName(), d, formatClass);
    }

    private static File mockFile(final String name, MediaFormat format, final FolderNode<?> parent) throws IOException {
        File f = new File(parent.getFolder(), name + '.' + format.getExt());
        touch(f);
        return f;
    }
}
