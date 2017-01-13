package com.mmbao.session.structure;

import java.util.Set;

/**
 * Created by gongbin on 2016/11/3.
 */
public interface ISessionStructure<T> {
    int DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS = 1800; //TODO:1800

    void setId(String id);

    String getId();

    void add(String key, Object value);

    Object get(String key);

    void remove(String key);

    Set<String> getKeys();

    T data();

    long getCreateTime();

    void setCreateTime(long createTime);

    long getLastAccessedTime();

    void setLastAccessedTime(long accessedTime);

    void setMaxInactiveInterval(int interval);

    int getMaxInactiveInterval();
}
