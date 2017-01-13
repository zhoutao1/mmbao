package com.mmbao.session;

import com.mmbao.session.structure.ISessionStructure;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

/**
 * Created by gongbin on 2016/11/3.
 */
public class MmbaoHttpSession implements HttpSession{

    private final ServletContext servletContext;
    private ISessionStructure session;
    private boolean invalidated;
    private boolean old;

    public MmbaoHttpSession(ServletContext servletContext, ISessionStructure iSessionStructure) {
        this.servletContext = servletContext;
        this.session = iSessionStructure;
    }

    @Override
    public long getCreationTime() {
        return session.getCreateTime();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        new MmbaoSessionException("unSupported method!");
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return session.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(this.session.getKeys());
    }

    @Override
    public String[] getValueNames() {
        Set<String> attrs = this.session.getKeys();
        return attrs.toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.session.add(name,value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name,value);
    }

    @Override
    public void removeAttribute(String name) {
        this.session.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        this.invalidated = true;
    }

    public void setNew(boolean isNew) {
        this.old = !isNew;
    }

    @Override
    public boolean isNew() {
        return !this.old;
    }

}
