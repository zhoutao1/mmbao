package com.mmbao.session;

import com.mmbao.session.wrapper.ISessionWrapper;
import com.mmbao.session.wrapper.SessionWrapperType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by gongbin on 2016/11/7.
 */
public class MmbaoSessionFilter implements Filter {
    private static Logger logger = Logger.getLogger(MmbaoSessionFilter.class);

    private SessionWrapperType sessionWrapperType;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String session_type = filterConfig.getInitParameter("session_type");
        if(StringUtils.isNotBlank(session_type))
        {
            logger.info("[mmbaosession] : session type " + session_type  );
            sessionWrapperType = SessionWrapperType.valueOf(session_type);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(sessionWrapperType != null)
        {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String requestedSessionId = CookieUtils.getCookieValue(CookieUtils.SESSION_ID,httpRequest);

            ISessionWrapper wrapper = sessionWrapperType.instance().getSessionFromStorage(requestedSessionId);
            if(wrapper.getSession() == null)
            {
                wrapper.createSession(requestedSessionId);
                CookieUtils.addCookie(CookieUtils.SESSION_ID,wrapper.getId(), CookieUtils.getSecondDomain(httpRequest),httpResponse); //TODO：.mmbao.com
                logger.info("[mmbaosession] : create new session " + wrapper.getId());
            }
            MmbaoHttpSession httpSession = new MmbaoHttpSession(httpRequest.getSession().getServletContext(),wrapper);
            chain.doFilter(new MmbaoRequestWrapper(httpRequest,httpSession), httpResponse);
        }
        else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
