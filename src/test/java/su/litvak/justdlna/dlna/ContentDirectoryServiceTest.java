package su.litvak.justdlna.dlna;

import org.junit.Before;
import org.junit.Test;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.model.FolderNode;
import su.litvak.justdlna.model.NodesMap;
import su.litvak.justdlna.model.RootNode;
import su.litvak.justdlna.model.VideoFormat;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ContentDirectoryServiceTest extends AbstractTest {
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
}
