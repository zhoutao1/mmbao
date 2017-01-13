package com.mmbao.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * Created by gongbin on 2016/11/3.
 */
public class MmbaoRequestWrapper extends HttpServletRequestWrapper{

    private MmbaoHttpSession httpSession;

    public MmbaoRequestWrapper(HttpServletRequest request) {
        super(request);
    }


    public MmbaoRequestWrapper(HttpServletRequest request,MmbaoHttpSession httpSession) {
        super(request);
        this.httpSession = httpSession;
    }

    @Override
    public String getRequestedSessionId() {
        return httpSession.getId();
    }

    @Override
    public HttpSession getSession() {
        return httpSession;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return httpSession;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return super.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return super.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return super.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return super.isRequestedSessionIdFromUrl();
    }
}
