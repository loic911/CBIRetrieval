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
package retrieval.client;

import retrieval.dist.ResultsSimilarities;
import retrieval.exception.CBIRException;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RetrievalClient interface
 * @author lrollus
 */
public interface RetrievalClientInterface {
     /**
     * Search max k similar pictures as img
     * @param img Image request
     * @param k Max result
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    ResultsSimilarities search(BufferedImage img, int k) throws CBIRException;
    
     /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param k Max result
     * @param storages Storages limitation (empty = all storages)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    ResultsSimilarities search(BufferedImage img, int k,String[] storages) throws CBIRException;
    
     /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param k Max result
     * @param storages Storages limitation (empty = all storages)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */
    ResultsSimilarities search(BufferedImage img, int k,List<String> storages) throws CBIRException;
    
    /**
     * Search max k similar pictures as img, search only on servers in servers array
     * @param img Image request
     * @param N Number of patches to build
     * @param k Max result
     * @param storages Storages limitation (empty = all storages)
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */    
    ResultsSimilarities search(BufferedImage img, int N, int k,String[] storages) throws CBIRException;
    
    /**
     * Search similar pictures thanks to visualWords
     * @param visualWords Visual word for request
     * @param N Number of patches to build
     * @param k Max result
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */   
    ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k) throws CBIRException;
    
    /**
     * Search similar pictures thanks to visualWords, search only on servers in servers array
     * @param visualWords Visual word for request
     * @param N Number of patches to build
     * @param k Max result
     * @param storages Storage filters
     * @return Similar pictures and server state
     * @throws CBIRException Error during search
     */    
    ResultsSimilarities search(List<ConcurrentHashMap<String, Long>> visualWords, int N, int k, String[] storages) throws CBIRException;
}
