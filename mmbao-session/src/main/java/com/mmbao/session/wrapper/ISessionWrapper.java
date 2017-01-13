package com.mmbao.session.wrapper;

import com.mmbao.session.structure.ISessionStructure;

/**
 * Created by gongbin on 2016/11/3.
 */
public interface ISessionWrapper<T> extends ISessionStructure<T> {

    ISessionWrapper<T> getSessionFromStorage(String id);

    ISessionStructure<T> createSession(String id);

    ISessionStructure<T> getSession();

    void updateAccessedTime();

    boolean isExpired();

    void kill();

    boolean isNew();

    void setNew(boolean isNew);

}
