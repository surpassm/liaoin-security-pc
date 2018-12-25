/**
 * 
 */
package com.liaoin.security.pc.config;

import com.liaoin.security.core.properties.SecurityProperties;
import com.liaoin.security.pc.logout.LiaoinLogoutSuccessHandler;
import com.liaoin.security.pc.session.LiaoinExpiredSessionStrategy;
import com.liaoin.security.pc.session.LiaoinInvalidSessionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * @author mc
 * @version 1.0v
 */
@Configuration
public class BrowserSecurityBeanConfig {

	@Autowired
	private SecurityProperties securityProperties;
	
	@Bean
	@ConditionalOnMissingBean(InvalidSessionStrategy.class)
	public InvalidSessionStrategy invalidSessionStrategy(){
		return new LiaoinInvalidSessionStrategy(securityProperties.getBrowser().getSession().getSessionInvalidUrl());
	}
	
	@Bean
	@ConditionalOnMissingBean(SessionInformationExpiredStrategy.class)
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy(){
		return new LiaoinExpiredSessionStrategy(securityProperties.getBrowser().getSession().getSessionInvalidUrl());
	}

	@Bean
	@ConditionalOnMissingBean(LiaoinLogoutSuccessHandler.class)
	public LogoutSuccessHandler logoutSuccessHandler(){
		return new LiaoinLogoutSuccessHandler(securityProperties);
	}
}
