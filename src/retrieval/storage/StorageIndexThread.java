/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private final Storage storage;
    /**
     * Queue for Path indexer (heavy indexer)
     */
    private final BlockingQueue<PictureInfo> indexQueuePicture;
    /**
     * Max Size of picture queue
     */
    private final int sizeOfPictureQueue;
    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(StorageIndexThread.class);

    /**
     * Constructor (private) for a index thread.
     * The only way to get a index thread is to call static methode
     * getThreadIndex which check if Thread index will be the only one
     * @param server Server which carry index request
     * @param sizeOfPictureQueue Size of Picture Queue (0 for no limit)
     */
    private StorageIndexThread(Storage server, int sizeOfPictureQueue) {
        logger.info("IndexThread sizeOfPictureQueue="+sizeOfPictureQueue);
        this.storage = server;
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
                    storage.setCurrentIndexedPicture(info.id);
                    storage.indexPicture(info.image,info.id,info.properties);
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
 * This class define image data to index
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
