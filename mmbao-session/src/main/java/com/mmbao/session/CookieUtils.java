/*
 * Create Author  : xiaopengli
 * Create  Time   : 11/14/13 3:35 PM
 * Project        : API
 *
 * Copyright (c) 2010-2015 by Shanghai HanTao Information Co., Ltd.
 * All rights reserved.
 *
 */

package com.mmbao.session;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils
{
	private static final int SECONDS_OF_DAY = 86400; // 一天的总秒数

	private static final int EXPIRE_DATE = 31;// 保持登录的天数

	public static final String SESSION_ID = "mmbao_session_id";

	public static void addCookie(String key, String value,String domain, HttpServletResponse response)
	{
		Cookie cookie = new Cookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static void addCookie(String key, String value,int timelong ,HttpServletResponse response)
	{
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(timelong);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static String getCookieValue(String key, HttpServletRequest request)
	{
		String value = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			for (Cookie cookie : cookies)
			{
				if (cookie != null)
				{
					if (key.equals(cookie.getName()))
					{
						value = cookie.getValue();
						break;
					}
				}
			}
		}
		return value;
	}

	/**
	 * 自动域名
	 */
	public static String getSecondDomain(HttpServletRequest request)
	{
		String domain = request.getServerName();
		String[] domains = domain.split("\\.");
		if(domains.length > 1)
		{
			domain = String.format(".%s.%s",domains[domains.length - 2],domains[domains.length - 1]);
		}
		return domain;
	}
	
	public static void removeCookie(String key,HttpServletRequest request,HttpServletResponse response){
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(Cookie cookie:cookies){
				if(cookie.getName().equals(key)){
					Cookie newCookie=new Cookie(key,null); 
					newCookie.setMaxAge(0); 
					newCookie.setPath("/"); 
					response.addCookie(newCookie);; 
				}
				
			}
		}
		
	}
	
}
