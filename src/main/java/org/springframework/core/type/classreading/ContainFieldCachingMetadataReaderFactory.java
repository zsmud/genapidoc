package org.springframework.core.type.classreading;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wot_zhengshenming on 2021/2/19.
 */
public class ContainFieldCachingMetadataReaderFactory extends ContainFieldSimpleMetadataReaderFactory {
    /** Default maximum number of entries for the MetadataReader cache: 256 */
    public static final int DEFAULT_CACHE_LIMIT = 256;


    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    @SuppressWarnings("serial")
    private final Map<Resource, MetadataReader> metadataReaderCache =
            new LinkedHashMap<Resource, MetadataReader>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest) {
                    return size() > getCacheLimit();
                }
            };


    /**
     * Create a new CachingMetadataReaderFactory for the default class loader.
     */
    public ContainFieldCachingMetadataReaderFactory() {
        super();
    }

    /**
     * Create a new CachingMetadataReaderFactory for the given resource loader.
     * @param resourceLoader the Spring ResourceLoader to use
     * (also determines the ClassLoader to use)
     */
    public ContainFieldCachingMetadataReaderFactory(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    /**
     * Create a new CachingMetadataReaderFactory for the given class loader.
     * @param classLoader the ClassLoader to use
     */
    public ContainFieldCachingMetadataReaderFactory(ClassLoader classLoader) {
        super(classLoader);
    }


    /**
     * Specify the maximum number of entries for the MetadataReader cache.
     * <p>Default is 256.
     */
    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    /**
     * Return the maximum number of entries for the MetadataReader cache.
     */
    public int getCacheLimit() {
        return this.cacheLimit;
    }


    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        if (getCacheLimit() <= 0) {
            return super.getMetadataReader(resource);
        }
        synchronized (this.metadataReaderCache) {
            MetadataReader metadataReader = this.metadataReaderCache.get(resource);
            if (metadataReader == null) {
                metadataReader = super.getMetadataReader(resource);
                this.metadataReaderCache.put(resource, metadataReader);
            }
            return metadataReader;
        }
    }

    /**
     * Clear the entire MetadataReader cache, removing all cached class metadata.
     */
    public void clearCache() {
        synchronized (this.metadataReaderCache) {
            this.metadataReaderCache.clear();
        }
    }
}
