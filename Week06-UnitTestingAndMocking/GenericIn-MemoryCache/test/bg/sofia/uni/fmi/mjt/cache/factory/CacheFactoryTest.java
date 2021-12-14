package bg.sofia.uni.fmi.mjt.cache.factory;

import bg.sofia.uni.fmi.mjt.cache.Cache;
import bg.sofia.uni.fmi.mjt.cache.LeastFrequentlyUsedCache;
import bg.sofia.uni.fmi.mjt.cache.LeastRecentlyUsedCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheFactoryTest {

    @Test
    void testGetInstanceWithCapacityWithNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> CacheFactory.getInstance(null, -5, null), "the capacity must not be negative");
    }

    @Test
    void testGetInstanceWithCapacityWithZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> CacheFactory.getInstance(null, 0, null), "the capacity must not be zero");
    }

    @Test
    void testGetInstanceWithCapacityLeastFrequentlyUsedCache() {
        Cache<Object, Object> result = CacheFactory.getInstance(null, 5, EvictionPolicy.LEAST_FREQUENTLY_USED);
        assertTrue(result instanceof LeastFrequentlyUsedCache);
    }

    @Test
    void testGetInstanceWithCapacityLeastRecentlyUsedCache() {
        Cache<Object, Object> result = CacheFactory.getInstance(null, 5, EvictionPolicy.LEAST_RECENTLY_USED);
        assertTrue(result instanceof LeastRecentlyUsedCache);
    }

    @Test
    void testGetInstanceWithoutCapacityLeastFrequentlyUsedCache() {
        Cache<Object, Object> result = CacheFactory.getInstance(null, EvictionPolicy.LEAST_FREQUENTLY_USED);
        assertTrue(result instanceof LeastFrequentlyUsedCache);
    }

    @Test
    void testGetInstanceWithoutCapacityLeastRecentlyUsedCache() {
        Cache<Object, Object> result = CacheFactory.getInstance(null, EvictionPolicy.LEAST_RECENTLY_USED);
        assertTrue(result instanceof LeastRecentlyUsedCache);
    }
}