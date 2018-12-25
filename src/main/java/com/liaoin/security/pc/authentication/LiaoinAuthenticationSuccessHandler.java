package com.liaoin.security.pc.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liaoin.security.core.enums.LoginResponseType;
import com.liaoin.security.core.properties.SecurityProperties;
import com.liaoin.security.core.support.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义登陆成功配置
 * 继承AuthenticationSuccessHandler
 * 默认成功处理器：SavedRequestAwareAuthenticationSuccessHandler
 * @author mc
 */
@Component("liaoinAuthenticationSuccessHandler")
public class LiaoinAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * jackson json转换
     */
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

	/**
	 * 该方法在登陆成功以后会被调用
	 * @param request request
	 * @param response response
	 * @param authentication authentication
	 * @throws IOException IOException
	 * @throws ServletException ServletException
	 */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("登录成功");
        if (LoginResponseType.JSON.equals(securityProperties.getBrowser().getLoginType())) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(HttpStatus.OK.value(),"",authentication.getPrincipal())));
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
