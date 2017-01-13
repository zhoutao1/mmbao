package com.mmbao.session.storage;

import com.mmbao.session.structure.ISessionStructure;
import com.mmbao.session.structure.MapStructure;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis存储
 * Created by gongbin on 2016/11/4.
 */
public class RedisStorage implements ISessionStorage<Map> {

    private static Logger logger = Logger.getLogger(RedisStorage.class);

    private RedisTemplate template;

    public RedisTemplate getTemplate() {
        return template;
    }

    public void setTemplate(RedisTemplate template) {
        this.template = template;
    }

    private final static String SESSION_PREFIX = "mmbao_session:";

    private final static String EXPIRE_PREFIX = "mmbao_expire:";

    protected final static String CREATETIME = "CREATETIME";
    protected final static String LASTACCESSEDTIME = "LASTACCESSEDTIME";
    protected final static String INTERVAL = "INTERVAL";


    @Override
    public String save(final ISessionStructure<Map> structure) {
        if(StringUtils.isEmpty(structure.getId()))
        {
            String id = UUID.randomUUID().toString();
            structure.setId(id);
        }

        final Map<String,Object> tmp = new HashMap<String, Object>();
        tmp.putAll(structure.data());
        tmp.put(CREATETIME,structure.getCreateTime());
        tmp.put(LASTACCESSEDTIME,structure.getLastAccessedTime());
        tmp.put(INTERVAL,structure.getMaxInactiveInterval());

        template.execute(new RedisCallback<Map>() {
            public Map doInRedis(RedisConnection connection) throws DataAccessException
            {
                String key = SESSION_PREFIX + structure.getId();
                BoundHashOperations<String,Object, Object> boundHashOperations = template.boundHashOps(key);

                List<Object> keys4Delete = new ArrayList<Object>();
                for(Object _key : boundHashOperations.keys())
                {
                    if(!tmp.containsKey(String.valueOf(_key)))
                    {
                        keys4Delete.add(_key);
                    }
                }
                if(!CollectionUtils.isEmpty(keys4Delete))
                {
                    boundHashOperations.delete(keys4Delete.toArray());
                }
                boundHashOperations.putAll(tmp);
                return null;
            }
        });
        updateExpire(structure);

        logger.info("[mmbaosession - redis] : save session " + structure.getId());

        return  structure.getId();
    }

    @Override
    public ISessionStructure<Map> getSession(String id) {
        BoundHashOperations hashOperations = this.template.boundHashOps(SESSION_PREFIX + id);
        Map<Object, Object> entries = hashOperations.entries();
        if (entries.isEmpty()) {
            logger.info("[mmbaosession - redis] : get session, empty session, id:" + id);
            return null;
        }

        logger.info("[mmbaosession - redis] : get session, session size "+ entries.size() +", id:" + id);

        MapStructure structure = new MapStructure(id);
        for(Map.Entry<Object,Object> entry : entries.entrySet())
        {
            if(CREATETIME.equals(entry.getKey().toString()))
            {
                structure.setCreateTime(NumberUtils.toLong(String.valueOf(entry.getValue())));
            }
            else if(LASTACCESSEDTIME.equals(entry.getKey().toString()))
            {
                structure.setLastAccessedTime(NumberUtils.toLong(String.valueOf(entry.getValue())));
            }
            else if(INTERVAL.equals(entry.getKey().toString()))
            {
                structure.setMaxInactiveInterval(NumberUtils.toInt(String.valueOf(entry.getValue())));
            }
            else{
                structure.add(entry.getKey().toString(),entry.getValue());
            }
        }
        updateExpire(structure);
//        logger.info("[mmbaosession - redis] : get session " + id);
        return structure;
    }

    @Override
    public void delete(String id) {
        if(template.hasKey(SESSION_PREFIX + id))
        {
            ISessionStructure structure = getSession(id) ;
            if(structure != null)
            {
                structure.setMaxInactiveInterval(0);
                deleteExpire(structure);
                template.delete(SESSION_PREFIX + id);
                logger.info("[mmbaosession - redis] : delete session " + id);
            }
        }
    }

    private void deleteExpire(ISessionStructure structure)
    {
        long expireTime = sessionExpireTime(structure);
        String expireKey = EXPIRE_PREFIX + expireTime;
        template.boundSetOps(expireKey).remove(structure.getId());
    }

    private void updateExpire(ISessionStructure structure)
    {
        long oldTime = structure.getLastAccessedTime();
        //TODO；
        //刷新最后访问时间
        structure.setLastAccessedTime(System.currentTimeMillis());
        long newExpireTime = sessionExpireTime(structure);

        if(oldTime > 0L)
        {
            long oldExpireTime = roundDownMinute(oldTime);
            if(newExpireTime != oldExpireTime)
            {
                this.template.boundSetOps(EXPIRE_PREFIX + oldExpireTime).remove(structure.getId());
            }
        }

        //过期sessionId集合缓存，过期时间设为当前最新session过期时间的5分钟后
        BoundSetOperations expireOperations = this.template.boundSetOps(EXPIRE_PREFIX + newExpireTime);
        expireOperations.add(structure.getId()); //session Id放入集合
        expireOperations.expire(structure.getMaxInactiveInterval() + TimeUnit.MINUTES.toSeconds(5), TimeUnit.SECONDS);

        String sessionKey = SESSION_PREFIX + structure.getId();
        if (structure.getMaxInactiveInterval() == 0) {
            this.template.delete(sessionKey);
        }
        else {
            this.template.boundValueOps(sessionKey).expire(structure.getMaxInactiveInterval(), TimeUnit.SECONDS);
        }
    }

    //TODO: 定时器调用定时刷新缓存
    @Scheduled(cron = "0 * * * * *")
    public void cleanExpiredSessions() {
//        logger.info("[mmbaosession - redis - schedule] : start");

        long nowExpireTime = roundDownMinute(System.currentTimeMillis());
        String expirationKey = EXPIRE_PREFIX + nowExpireTime;
        Set<String> sessionIds = this.template.boundSetOps(expirationKey).members();
        this.template.delete(expirationKey);

        for (String sessionId : sessionIds) {
            logger.info("[mmbaosession - redis - schedule] : delete session " + sessionId);
            this.template.hasKey(SESSION_PREFIX + sessionId);
        }
//        logger.info("[mmbaosession - redis - schedule] : finish");
    }

    private long sessionExpireTime(ISessionStructure structure)
    {   int maxInactiveInSeconds = structure.getMaxInactiveInterval();
        long lastAccessedTimeInMillis = structure.getLastAccessedTime();
        return roundUpToNextMinute(lastAccessedTimeInMillis + TimeUnit.SECONDS.toMillis(maxInactiveInSeconds));
    }

    private long roundUpToNextMinute(long timeInMs) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMs);
        date.add(Calendar.MINUTE, 1);
        date.clear(Calendar.SECOND);
        date.clear(Calendar.MILLISECOND);
        return date.getTimeInMillis();
    }

    private long roundDownMinute(long timeInMs) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMs);
        date.clear(Calendar.SECOND);
        date.clear(Calendar.MILLISECOND);
        return date.getTimeInMillis();
    }
}
