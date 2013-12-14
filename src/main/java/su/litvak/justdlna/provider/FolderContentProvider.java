package su.litvak.justdlna.provider;

/**
 * TODO
 *
 * 1) Move listing logic to the container. Remove ContentProvider interface. Store ContainerNode's in config file
 *
 * 2) Do not store ROOT as singleton, let it be a reference in the content directory service
 *
 * 3) Store map of id-contentnode (NodeMap) in the content directory service
 *
 * 4) Find out when and how directory listing is invoked. Maybe will need to use proxy here.
 *
 * 5) Do not store containers and items. Create them lazily. This will help reflect directory changes.
 */
public class FolderContentProvider {
}