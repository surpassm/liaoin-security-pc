package com.liaoin.security.pc.validate;

import com.liaoin.security.core.properties.SecurityProperties;
import com.liaoin.security.core.validate.bean.ValidateCode;
import com.liaoin.security.core.validate.enums.ValidateCodeType;
import com.liaoin.security.core.validate.service.ValidateCodeRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author mc
 * @version 1.0v
 *  PC端验证码实现类
 */
@Component
public class SessionValidateCodeRepository implements ValidateCodeRepository {

    @Autowired
    private SecurityProperties securityProperties;
    /**
     * 操作session的工具类
     */
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
    @Override
    public void save(ServletWebRequest request, ValidateCode code, ValidateCodeType validateCodeType) {
        //由于需要放入redis不需要把图片放入session中。
        sessionStrategy.setAttribute(request, getSessionKey(request,validateCodeType), code);

    }

    @Override
    public ValidateCode get(ServletWebRequest request, ValidateCodeType validateCodeType) {
        return (ValidateCode) sessionStrategy.getAttribute(request,getSessionKey(request,validateCodeType));
    }

    @Override
    public void remove(ServletWebRequest request, ValidateCodeType validateCodeType) {
        sessionStrategy.removeAttribute(request, getSessionKey(request,validateCodeType));
    }

	/**
	 * 构建验证码放入session时的key
	 * @param request request
	 * @param validateCodeType validateCodeType
	 * @return String
	 */
    private String getSessionKey(ServletWebRequest request, ValidateCodeType validateCodeType) {
        return securityProperties.getCode().getSessionKeyPrefix() + validateCodeType.toString().toUpperCase();
    }

	/**
	 * 根据请求的url获取校验码的类型
	 * @param request request
	 * @return ValidateCodeType
	 */
	private ValidateCodeType getValidateCodeType(ServletWebRequest request) {
        String type = StringUtils.substringBefore(getClass().getSimpleName(), "CodeProcessor");
        return ValidateCodeType.valueOf(type.toUpperCase());
    }
}
