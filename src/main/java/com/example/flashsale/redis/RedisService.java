package com.example.flashsale.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * Get an Object
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String actualKey = prefix.getPrefix() + key;
            String str = jedis.get(actualKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * Set an Object
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0){
                return false;
            }
            String actualKey = prefix.getPrefix() + key;
            int expireSec = prefix.getExpireSeconds();
            if (expireSec <= 0){
                jedis.set(actualKey,str);
            } else {
                jedis.setex(actualKey, (long)expireSec, str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * Determine if the key exists
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String actualKey = prefix.getPrefix() + key;
            return jedis.exists(actualKey);
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * Delete with prefix and key
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String actualKey = prefix.getPrefix() + key;
            long res = jedis.del(actualKey);
            return res > 0;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * Delete keys by pattern
     *
     * @param pattern
     * @return
     */
    public boolean delete(String pattern){
        Set<String> matchingKeys = new HashSet<>();
        ScanParams params = new ScanParams();
        params.match(pattern);

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String nextCursor = "0";

            do {
                ScanResult<String> scanResult = jedis.scan(nextCursor, params);
                List<String> keys = scanResult.getResult();
                nextCursor = scanResult.getCursor();

                matchingKeys.addAll(keys);

            } while(!nextCursor.equals("0"));

            if (matchingKeys.size() == 0) {
                return false;
            }

            jedis.del(matchingKeys.toArray(new String[matchingKeys.size()]));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }


    /**
     * Increase value by one
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String actualKey = prefix.getPrefix() + key;
            return jedis.incr(actualKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * Decrease value by one
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String actualKey = prefix.getPrefix() + key;
            return jedis.decr(actualKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     *  Bean to String convertor
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value) {
        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class){
            return "" + value;
        } else  if (clazz == String.class){
            return  (String)value;
        } else if (clazz == long.class || clazz == Long.class){
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * String to Bean convertor
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if (clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        } else  if (clazz == String.class){
            return (T)str;
        } else if (clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    // Return jedis to jedis pool
    private void returnToPool(Jedis jedis) {
        if (jedis != null){
            jedis.close();
        }
    }



}
