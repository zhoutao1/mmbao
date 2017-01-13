package com.mmbao.session.wrapper;

import com.mmbao.session.SpringBeanUtils;
import com.mmbao.session.storage.RedisStorage;
import com.mmbao.session.structure.ISessionStructure;
import com.mmbao.session.structure.MapStructure;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by gongbin on 2016/11/4.
 */
public class RedisSessionWrapper implements ISessionWrapper<Map> {
    private RedisStorage redisStorage;
    private ISessionStructure<Map> structure;
    private boolean isNew = false;

    public RedisSessionWrapper()
    {
        redisStorage = SpringBeanUtils.getBean(RedisStorage.class); //TODO:
    }

    private void update()
    {
        redisStorage.save(this.structure);
    }

    @Override
    public ISessionWrapper<Map> getSessionFromStorage(String id) {
        if(StringUtils.isNotBlank(id)){
            structure = redisStorage.getSession(id);
        }
        return this;
    }

    @Override
    public ISessionStructure<Map> createSession(String id) {
        ISessionStructure<Map> structure = new MapStructure(id).init();
        structure.setId(redisStorage.save(structure));
        this.isNew = true;
        this.structure = structure;
        return structure;
    }

    @Override
    public ISessionStructure<Map> getSession() {
        return structure;
    }

    @Override
    public void updateAccessedTime() {
        structure.setLastAccessedTime(System.currentTimeMillis());
    }

    @Override
    public boolean isExpired() {
        if (structure.getMaxInactiveInterval() < 0) {
            return false;
        }
        return System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(structure.getMaxInactiveInterval()) >= structure.getLastAccessedTime();
    }

    @Override
    public void kill() {
        structure.setMaxInactiveInterval(0);
        structure.data().clear();
        redisStorage.delete(structure.getId());
    }

    @Override
    public void setId(String id) {
        structure.setId(id);
    }

    @Override
    public String getId() {
        return structure.getId();
    }

    @Override
    public void add(String key, Object value) {
        structure.add(key,value);
        update();
    }

    @Override
    public Object get(String key) {
        return structure.get(key);
    }

    @Override
    public void remove(String key) {
        structure.remove(key);
        update();
    }

    @Override
    public Set<String> getKeys() {
        return structure.getKeys();
    }

    @Override
    public Map data() {
        return structure.data();
    }

    @Override
    public long getCreateTime() {
        return structure.getCreateTime();
    }

    @Override
    public void setCreateTime(long createTime) {
        structure.setCreateTime(createTime);
    }

    @Override
    public long getLastAccessedTime() {
        return structure.getLastAccessedTime();
    }

    @Override
    public void setLastAccessedTime(long accessedTime) {
        structure.setLastAccessedTime(accessedTime);
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        structure.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return structure.getMaxInactiveInterval();
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
