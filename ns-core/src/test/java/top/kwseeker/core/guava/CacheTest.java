package top.kwseeker.core.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * LoadingCache API
 *
 * 1
 * V apply(K key)
 *
 * Deprecated. Provided to satisfy the Function interface; use get(K) or getUnchecked(K) instead.
 *
 * 2
 * ConcurrentMap<K,V> asMap()
 *
 * Returns a view of the entries stored in this cache as a thread-safe map.
 *
 * 3
 * V get(K key)
 *
 * Returns the value associated with key in this cache, first loading that value if necessary.
 *
 * 4
 * ImmutableMap<K,V> getAll(Iterable<? extends K> keys)
 *
 * Returns a map of the values associated with keys, creating or retrieving those values if necessary.
 *
 * 5
 * V getUnchecked(K key)
 *
 * Returns the value associated with key in this cache, first loading that value if necessary.
 *
 * 6
 * void refresh(K key)
 *
 * Loads a new value for key, possibly asynchronously.
 */
public class CacheTest {

    private LoadingCache<Key, Value> cache;
    private Cache<Key, Value> cache1;

    @Before
    public void init() {
        cache = CacheBuilder.newBuilder()
                .initialCapacity(10)        //初始容量
                .maximumSize(1000)          //最大容量
                .concurrencyLevel(5)        //同一时间最多只能有5个线程往cache执行写操作
                .expireAfterWrite(10, TimeUnit.SECONDS) //数据写入后的存活时间
                //.expireAfterAccess(10, TimeUnit.SECONDS)
                //.removalListener()
                //.recordStats()
                //.refreshAfterWrite()
                .build(new CacheLoader<Key, Value>() {
                    @Override
                    public Value load(@Nonnull Key key) throws Exception {   //如果缓存加载器抛出一个未经检查的异常，get（K）将抛出一个包含它的UncheckedExecutionException。
                        return getValueByKey(key);
                    }
                    //public Graph load(Key key) { // no checked exception
                    //    return createExpensiveGraph(key);
                    //}
                });

        cache1 = CacheBuilder.newBuilder().recordStats().build();
    }

    @Test
    public void testPut() {
        //直接插入数据
        //cache.put(key, value)
        //
        //cache1.put("one", 1);
    }

    @Test
    public void testGet() {
        try {
            Key key1 = new Key("1");
            Key key2 = new Key("2");
            long beginTime = System.currentTimeMillis();
            //第一次通过load获取
            System.out.println(cache.get(key1));
            System.out.println(cache.get(key2));
            System.out.println(System.currentTimeMillis() - beginTime);
            //第二次从缓存中直接获取
            System.out.println(cache.get(key1));
            System.out.println(cache.get(key2));
            System.out.println(System.currentTimeMillis() - beginTime);

            //graphs.getUnchecked(key);
            //
            //cache.get(key, new Callable<Value>() {
            //    @Override
            //    public Value call() throws AnyException {
            //        return doThingsTheHardWay(key);
            //    }
            //});

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //实际应用中常常是从数据库或文件中读取
    private static Value getValueByKey(Key key) {
        try {
            Thread.sleep(50);   //模拟缓存之前的耗时的读取过程
            return new Value(Integer.valueOf(key.getKey()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    //实际应用中键和值数据结构会比较复杂
    public static class Key {
        String key;

        Key(String key) {
            this.key = key;
        }

        String getKey() {
            return key;
        }
    }

    public static class Value {
        Integer value;

        Value(Integer value) {
            this.value = value;
        }

        Integer getValue() {
            return value;
        }
    }
}
