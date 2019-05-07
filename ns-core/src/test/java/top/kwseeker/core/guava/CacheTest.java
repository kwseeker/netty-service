package top.kwseeker.core.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.TIMEOUT;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 缓存
 */
public class CacheTest {

    private Cache<Object, Object> cache;
    private Cache<Object, Object> cache1;

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
                .build(new CacheLoader<Object, Object>() {
                    public Object load(Object key) throws Exception {   //如果缓存加载器抛出一个未经检查的异常，get（K）将抛出一个包含它的UncheckedExecutionException。
                        return
                    }

                    public Graph load(Key key) { // no checked exception
                        return createExpensiveGraph(key);
                    }
                });

        cache1 = CacheBuilder.newBuilder().recordStats().build();
    }

    @Test
    public void testPut() {
        //直接插入数据
        cache.put(key, value)

        cache1.put("one", 1);
    }

    @Test
    public void testGet() {
        try {
            return cache.get(key);

            graphs.getUnchecked(key);

            cache.get(key, new Callable<Value>() {
                @Override
                public Value call() throws AnyException {
                    return doThingsTheHardWay(key);
                }
            });

        } catch (ExecutionException e) {
            throw new OtherException(e.getCause());
        }
    }


}
