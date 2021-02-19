package cn.sakuramiku.lightblog.shiro;


import cn.sakuramiku.lightblog.common.util.RedisUtil;
import cn.sakuramiku.lightblog.util.JwtUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

/**
 * 基于Redis的用于Shiro的缓存
 *
 * @author lyy
 */
@Component
@Lazy
public class RedisCache<K, V> implements Cache<K, V> {

    protected static RedisUtil redisUtil;

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil1) {
        redisUtil = redisUtil1;
    }

    protected static final String PREFIX_SHIRO_CACHE = "light_blog:shiro:cache:";

    /**
     * 根据Token生成key
     *
     * @param key
     * @return
     */
    protected String genKey(Object key) {
        return PREFIX_SHIRO_CACHE + JwtUtil.getUserName(key.toString());
    }

    @Override
    public Object get(Object s) throws CacheException {
        return redisUtil.get(genKey(s));
    }

    @Override
    public Object put(Object s, Object o) throws CacheException {
        return redisUtil.set(genKey(s), o);
    }

    @Override
    public Object remove(Object s) throws CacheException {
        return redisUtil.delete(genKey(s));
    }

    @Override
    public void clear() throws CacheException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set keys() {
        return redisUtil.getKeys(PREFIX_SHIRO_CACHE + "*");
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }
}
