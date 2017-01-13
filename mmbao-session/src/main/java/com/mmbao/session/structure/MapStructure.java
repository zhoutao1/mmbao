package com.mmbao.session.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by gongbin on 2016/11/4.
 */
public class MapStructure implements ISessionStructure<Map> {
    protected String id;
    protected Map<String,Object> map;
    protected long createTime;
    protected long lastAccessedTime;
    protected int interval;

    public MapStructure() {
        this.map = new HashMap<>();
    }

    public MapStructure(String id) {
        this.id = id;
        this.map = new HashMap<>();
    }

    public MapStructure init()
    {
        createTime = System.currentTimeMillis();
        lastAccessedTime = System.currentTimeMillis();
        interval = DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;
        return this;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void add(String key, Object value) {
        map.put(key,value);
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public Set<String> getKeys() {
        return map.keySet();
    }

    @Override
    public Map data() {
        return map;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public void setLastAccessedTime(long accessedTime) {
        this.lastAccessedTime = accessedTime;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return interval;
    }

}
