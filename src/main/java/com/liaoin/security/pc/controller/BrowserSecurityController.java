package com.liaoin.security.pc.controller;

import com.liaoin.security.core.constants.SecurityConstants;
import com.liaoin.security.core.properties.SecurityProperties;
import com.liaoin.security.core.support.SimpleResponse;
import com.liaoin.security.core.support.SocialUserInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * @author mc
 * @version 1.0v
 */
@RestController
public class BrowserSecurityController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 从请求的缓存中取出跳转的请求
     * http请求在访问BrowserSecurityConfig之前，把请求缓存到HttpSessionRequestCache中
     */
    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ProviderSignInUtils providerSignInUtils;

	/**
	 * 当需要身份认证时，跳转到这里
	 * @param request request
	 * @param response response
	 * @return SimpleResponse
	 * @throws IOException IOException
	 */
    @RequestMapping(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response)throws IOException {
        //取出引发跳转的请求
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            logger.info("引发跳转的请求是:" + targetUrl);
            //判断这个url是否为.html结尾
            if (StringUtils.endsWithIgnoreCase(targetUrl, ".html")) {
                //使用RedirectStrategy接口跳转到配置登陆的url页面，自定义登陆页面
                redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getLoginPage());
            }
        }
        return new SimpleResponse(400,"访问的服务需要身份认证，请引导用户到登录页","");
    }

	/**
	 *
	 * @param request request
	 * @return SocialUserInfo
	 */
    @GetMapping("/social/user")
    public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        return SocialUserInfo.builder()
                .providerId(connection.getKey().getProviderId())
                .headimg(connection.getImageUrl())
                .nickname(connection.getDisplayName())
                .providerUserId(connection.getKey().getProviderUserId())
                .build();
    }
}
