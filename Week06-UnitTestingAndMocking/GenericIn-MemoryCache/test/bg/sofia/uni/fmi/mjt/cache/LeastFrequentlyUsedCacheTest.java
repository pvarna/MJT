package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.cache.exception.ItemNotFound;
import bg.sofia.uni.fmi.mjt.cache.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeastFrequentlyUsedCacheTest {
    @Mock
    private Storage<Integer, String> storageMock;

    @InjectMocks
    private CacheBase<Integer, String> cache = new LeastFrequentlyUsedCache<>(storageMock, 5);

    @Test
    void testPutContainsGetFromCacheWithOneElement() {
        cache.put(1, "a");

        assertTrue(cache.containsKey(1));
        assertEquals(1, cache.size());
        assertEquals("a", cache.getFromCache(1));
        assertNull(cache.getFromCache(2));
    }

    @Test
    void testPutContainsGetFromCacheWithThreeElements() {
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");

        assertTrue(cache.containsKey(1));
        assertTrue(cache.containsKey(2));
        assertTrue(cache.containsKey(3));
        assertFalse(cache.containsKey(0));
        assertEquals(3, cache.size());
        assertEquals("a", cache.getFromCache(1));
        assertEquals("b", cache.getFromCache(2));
        assertEquals("c", cache.getFromCache(3));
        assertNull(cache.getFromCache(4));
    }

    @Test
    void testValuesWithThreeElements() {
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");

        Collection<String> values = cache.values();

        assertTrue(values.getClass().getName().contains("UnmodifiableCollection"));
    }

    @Test
    void testValuesWithEmptyCache() {
        Collection<String> values = cache.values();

        assertTrue(values.isEmpty());
        assertTrue(values.getClass().getName().contains("UnmodifiableCollection"));
    }

    @Test
    void testEvictFromCacheWithThreeElements() throws ItemNotFound {
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");

        cache.get(1);
        cache.get(1);
        cache.get(1);
        cache.get(2);
        cache.get(2);

        cache.evictFromCache();

        assertEquals(2, cache.size());
        assertFalse(cache.containsKey(3));
    }

    @Test
    void testEvictFromEmptyCache() {
        cache.evictFromCache();

        assertEquals(0, cache.size());
    }

    @Test
    void testAddToCacheWithThreeElements() {
        cache.addToCache(1, "a");
        cache.addToCache(2, "a");
        cache.addToCache(3, "a");

        assertEquals(3, cache.size());
    }

    @Test
    void testAddToCacheWithSevenElementsWhileCapacityIsFive() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");
        cache.addToCache(4, "d");
        cache.addToCache(5, "e");
        cache.addToCache(6, "f");
        cache.addToCache(7, "g");

        assertEquals(5, cache.size());
    }

    @Test
    void testGetWithNull() {
        assertThrows(IllegalArgumentException.class, () -> cache.get(null));
    }

    @Test
    void testGetWithExistingElement() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        assertEquals("a", cache.get(1));
        assertEquals("b", cache.get(2));
        assertEquals("c", cache.get(3));
    }

    @Test
    void testGetWithElementNotInCacheButInStorage() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        when(storageMock.retrieve(4)).thenReturn("d");

        assertEquals("d", cache.get(4));
        assertTrue(cache.containsKey(4));
    }

    @Test
    void testGetWithElementNotInCacheAndNotInStorage() {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        when(storageMock.retrieve(4)).thenReturn(null);

        assertThrows(ItemNotFound.class, () -> cache.get(4));
    }

    @Test
    void testHitRateWithAllSuccessfulGets() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        cache.get(1);
        cache.get(2);
        cache.get(3);

        assertEquals(1.0, cache.getHitRate(), 0.001);
    }

    @Test
    void testHitRateWithAllUnsuccessfulGets() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        when(storageMock.retrieve(4)).thenReturn("d");
        when(storageMock.retrieve(5)).thenReturn("e");
        when(storageMock.retrieve(6)).thenReturn("f");

        cache.get(4);
        cache.get(5);
        cache.get(6);

        assertEquals(0.0, cache.getHitRate(), 0.001);
    }

    @Test
    void testHitRateWithOneSuccessfulAndTwoUnsuccessfulGets() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        when(storageMock.retrieve(4)).thenReturn("d");
        when(storageMock.retrieve(5)).thenReturn("e");

        cache.get(1);
        cache.get(4);
        cache.get(5);

        assertEquals(1.0/3, cache.getHitRate(), 0.001);
    }

    @Test
    void testHitRateWithOneSuccessfulAndOneUnsuccessfulGets() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        when(storageMock.retrieve(4)).thenReturn("d");

        cache.get(1);
        cache.get(4);

        assertEquals(1.0/2, cache.getHitRate(), 0.001);
    }

    @Test
    void testClear() {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");

        cache.clear();
        assertEquals(0.0, cache.getHitRate(), 0.001);
        assertEquals(0, cache.size());
    }

    @Test
    void testAddToCacheWhenCacheFull() throws ItemNotFound {
        cache.addToCache(1, "a");
        cache.addToCache(2, "b");
        cache.addToCache(3, "c");
        cache.addToCache(4, "d");
        cache.addToCache(5, "e");

        for (int i = 0; i < 2; ++i) {
            cache.getFromCache(2);
        }
        for (int i = 0; i < 3; ++i) {
            cache.getFromCache(3);
        }
        for (int i = 0; i < 4; ++i) {
            cache.getFromCache(4);
        }

        cache.addToCache(6, "f");
        cache.addToCache(7, "g");

        assertTrue(cache.containsKey(2));
        assertTrue(cache.containsKey(3));
        assertTrue(cache.containsKey(4));
        assertTrue(cache.containsKey(6));
        assertTrue(cache.containsKey(7));

        assertFalse(cache.containsKey(1));
        assertFalse(cache.containsKey(5));
    }
}