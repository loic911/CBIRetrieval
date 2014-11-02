package retrieval.storage;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import retrieval.storage.exception.TooMuchIndexRequestException;

/**
 * This class in a index thread which will receive picture from indexed and put them on a waiting queue.
 * Until the queue is not empty, it will index picture on server
 * @author lrollus
 */
final class StorageIndexThread extends Thread {

    /**
     * Server which carry index request
     */
    private Storage server;
    /**
     * Queue for Path indexer (heavy indexer)
     */
    private BlockingQueue<PictureInfo> indexQueuePicture;
    /**
     * Max Size of picture queue
     */
    private int sizeOfPictureQueue;
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger(StorageIndexThread.class);

    /**
     * Constructor (private) for a index thread.
     * The only way to get a index thread is to call static methode
     * getThreadIndex which check if Thread index will be the only one
     * @param server Server which carry index request
     * @param sizeOfPictureQueue Size of Picture Queue (0 for no limit)
     */
    private StorageIndexThread(Storage server, int sizeOfPictureQueue) {
        logger.info("IndexThread sizeOfPictureQueue="+sizeOfPictureQueue);
        this.server = server;
        this.sizeOfPictureQueue = sizeOfPictureQueue;
        //for light indexer: put a limit
        if(sizeOfPictureQueue==0) {
            this.indexQueuePicture = new LinkedBlockingQueue<PictureInfo>();
        }
        else {
            this.indexQueuePicture = new LinkedBlockingQueue<PictureInfo>(sizeOfPictureQueue);
        }
        logger.info("IndexThread init");
    }

    /**
     * Only way to get a Thread index (which is a singleton).
     * Thread index must be the one to index pictures.
     * @param server Server which carry index request
     * @param sizeOfPictureQueue Size of Picture Queue (Path queue has no limit)
     * @return IndexThread (singleton)
     */
    public synchronized static StorageIndexThread getThreadIndex(Storage server, int sizeOfPictureQueue) {
        return new StorageIndexThread(server, sizeOfPictureQueue);
    }

    /**
     * Check if index queue is empty
     * @return true if index queue is empty
     */
    public boolean isIndexQueueEmpty() {
       return indexQueuePicture.isEmpty();
    }
    
    /**
     * Get the size of index queue
     * @return Index queue size
     */
    public int getIndexQueueSize() {
       return indexQueuePicture.size();
    }
    
    /**
     * Check if picture is in index queue
     * @param path Picture path
     * @return TRue if picture is in index queue
     */
    public boolean isInIndexQueue(Long id) {
        return indexQueuePicture.contains(new PictureInfo(null,id,null));
    }

    /**
     * Add picture in index picture queue
     * @param path Picture path
     * @param authorization Authorization to access image
     * @throws TooMuchIndexRequestException Too much
     */
    public synchronized void addInIndexPicture(BufferedImage image, Long id, Map<String,String> properties) throws TooMuchIndexRequestException {
        //A LinkedBlockingQueue is conccurrent collection,
        if (indexQueuePicture.size() >= (sizeOfPictureQueue - 1)) {
            throw new TooMuchIndexRequestException();
        }
        indexQueuePicture.add(new PictureInfo(image,id,properties));
    }
    
    /**
     * Run the indexer Thread.
     * Take a picture path from queue 1 or queue 2 and indexed it
     */
    @Override
    public void run() {

        //Thread safe: only one thread execute this code, no remove function!
        while (true) {
            try {
                if (indexQueuePicture.size() > 0) {
                    PictureInfo info = indexQueuePicture.take();
                    server.setCurrentIndexedPicture(info.id);
                    server.indexPicture(info.image,info.id,info.properties);
                }

                //If two queue are empty, wait some times
                if (indexQueuePicture.isEmpty()) {
                    Thread.sleep(100);
                }
                
            } catch (Exception e) {
                logger.error(e);
            }

        }
    }
}

/**
 * This class define a entry path, authorization
 * @author lrollus
 */
class PictureInfo {

    public Long id;
    public BufferedImage image;
    public Map<String,String> properties;

    public PictureInfo(BufferedImage image, Long id, Map<String,String> properties) {
        this.image = image;
        this.id = id;
        this.properties = properties;
    }
    
    @Override
    public boolean equals(Object o) {
        PictureInfo info = (PictureInfo)o;
        return (id.equals(info.id));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
