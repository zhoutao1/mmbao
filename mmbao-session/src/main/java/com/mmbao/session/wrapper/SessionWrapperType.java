package com.mmbao.session.wrapper;

/**
 * Created by gongbin on 2016/11/4.
 */
public enum SessionWrapperType {
    RedisSessionWrapper(new WrapperHandle() {
        @Override
        public ISessionWrapper instance() {
            return new RedisSessionWrapper();
        }
    });
    private WrapperHandle handle;

    SessionWrapperType(WrapperHandle handle) {
        this.handle = handle;
    }

    public ISessionWrapper instance()
    {
        return handle.instance();
    }

    interface WrapperHandle { ISessionWrapper instance();}
}
