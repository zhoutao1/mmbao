package com.mmbao.session.storage;

import com.mmbao.session.structure.ISessionStructure;

/**
 * Created by gongbin on 2016/11/3.
 */
public interface ISessionStorage<T>{

    String save(ISessionStructure<T> wrapper);

    ISessionStructure<T> getSession(String id);

    void delete(String id);

}
