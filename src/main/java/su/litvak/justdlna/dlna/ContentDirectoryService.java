package su.litvak.justdlna.dlna;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;
import su.litvak.justdlna.model.ContainerNode;
import su.litvak.justdlna.model.ContentNode;
import su.litvak.justdlna.model.ItemNode;
import su.litvak.justdlna.model.RootNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentDirectoryService extends AbstractContentDirectoryService {
    private static final Logger LOG = LoggerFactory.getLogger(ContentDirectoryService.class);
    Map<String, ContentNode> nodes = new HashMap<String, ContentNode>();

    public ContentDirectoryService (RootNode rootNode) {
        super();
        nodes.put(rootNode.getId(), rootNode);
    }

    @Override
    public BrowseResult browse (final String objectID, final BrowseFlag browseFlag, final String filter, final long firstResult, final long maxResults, final SortCriterion[] orderby) throws ContentDirectoryException {
        LOG.info("browse: {} ({}, {})", objectID, firstResult, maxResults);
        try {
            final DIDLContent didl = new DIDLContent();
            final ContentNode node = nodes.get(objectID);

            if (node == null) return new BrowseResult("", 0, 0);

            if (node instanceof ItemNode) {
                didl.addItem(((ItemNode) node).getItem());
                return new BrowseResult(new DIDLParser().generate(didl), 1, 1);
            }

            final ContainerNode containerNode = (ContainerNode) node;
            final Container container = containerNode.getContainer();

            if (browseFlag == BrowseFlag.METADATA) {
                didl.addContainer(container);
                return new BrowseResult(new DIDLParser().generate(didl), 1, 1);
            }

            List<? extends ContainerNode> containerNodes = containerNode.getContainers();
            List<? extends ItemNode> itemNodes = containerNode.getItems();

            if (containerNodes.size() > firstResult) {
                final int from = (int) firstResult;
                final int to = Math.min((int) (firstResult + maxResults), containerNodes.size());
                for (ContainerNode containerNodeX : containerNodes.subList(from, to)) {
                    nodes.put(node.getId(), node);
                    Container containerX = containerNodeX.getContainer();
                    container.addContainer(containerX);
                    didl.addContainer(containerX);
                }
            }
            if (didl.getContainers().size() < maxResults) {
                final int from = (int) Math.max(firstResult - container.getContainers().size(), 0);
                final int to = Math.min(itemNodes.size(), from + (int) (maxResults - didl.getContainers().size()));
                for (ItemNode itemNode : itemNodes.subList(from, to)) {
                    nodes.put(itemNode.getId(), itemNode);
                    Item item = itemNode.getItem();
                    container.addItem(item);
                    didl.addItem(item);
                }
            }

            return new BrowseResult(new DIDLParser().generate(didl),
                    didl.getContainers().size() + didl.getItems().size(),
                    container.getChildCount());
        }
        catch (final Exception e) {
            LOG.warn("Failed to generate directory listing.", e);
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, e.toString()); // NOSONAR
        }
    }

    @Override
    public BrowseResult search (final String containerId, final String searchCriteria,
                                final String filter, final long firstResult, final long maxResults,
                                final SortCriterion[] orderBy) throws ContentDirectoryException {
        // You can override this method to implement searching!
        return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
    }
}
