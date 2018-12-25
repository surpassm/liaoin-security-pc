package com.liaoin.security.pc.config;

import com.liaoin.security.core.authentication.AbstractChannelSecurityConfig;
import com.liaoin.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.liaoin.security.core.authorize.AuthorizeCofigManager;
import com.liaoin.security.core.properties.SecurityProperties;
import com.liaoin.security.core.validate.config.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;
/**
 * @author mc
 * @version 1.0v
 */
@Configuration
public class BrowserSecurityConfig extends AbstractChannelSecurityConfig {
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 数据源信息
     */
    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;

    @Autowired
    private SpringSocialConfigurer liaoinSocialSecurityConfig;

    @Autowired
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    @Autowired
    private InvalidSessionStrategy invalidSessionStrategy;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
	private AuthorizeCofigManager authorizeCofigManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		//密码登陆相关配置
		applyPasswordAuthenticationConfig(http);
		//效验码相关配置
		http
//				.apply(validateCodeSecurityConfig)
//				.and()
				//短信登陆相关配置
				.apply(smsCodeAuthenticationSecurityConfig)
				.and()
				//第三方登陆相关配置
				.apply(liaoinSocialSecurityConfig)
				.and()
				//浏览器特有相关配置
				.rememberMe()
					.tokenRepository(persistentTokenRepository())
					.tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
					.userDetailsService(userDetailsService)
					.and()
				.sessionManagement()
					//session失效需要处理跳转的地址
					.invalidSessionStrategy(invalidSessionStrategy)
					//同一个用户在系统中的最大session数，默认1
					.maximumSessions(securityProperties.getBrowser().getSession().getMaximumSessions())
					//达到最大session时是否阻止新的登录请求，默认为false，不阻止，新的登录会将老的登录失效掉
					.maxSessionsPreventsLogin(securityProperties.getBrowser().getSession().isMaxSessionsPreventsLogin())
					//踢掉前一个session用户，超时策略设置
					.expiredSessionStrategy(sessionInformationExpiredStrategy)
					.and()
					.and()
				.logout()
                    .logoutUrl("/signOut")
//                    .logoutSuccessUrl("/")
                    //自定义退出处理器
                    .logoutSuccessHandler(logoutSuccessHandler)
                    //删除指定的cookies
                    .deleteCookies("SESSION_ID")
				.and()
				/*//授权配置
				.authorizeRequests()
				//在授权配置后面加入匹配器，不需要权限验证的配置，浏览器授权特有相关配置
				.antMatchers(
						//当请求需要身份认证时，默认跳转到controller层的url：/authentication/require
						SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
						SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,
						securityProperties.getBrowser().getLoginPage(),
						SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*",
						//跳转到用户配置的登陆页
						securityProperties.getBrowser().getSignUpUrl(),
						securityProperties.getBrowser().getSession().getSessionInvalidUrl()+".json",
						securityProperties.getBrowser().getSession().getSessionInvalidUrl()+".html",
						"/user/regist")
				.permitAll()
				//所有请求
				.anyRequest()
				//需要认证
				.authenticated()
				.and()*/
				//关闭跨战请求防护
				.cors()
				.and()
				.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.disable();
		authorizeCofigManager.config(http.authorizeRequests());

    }


    /**
     * 记住我数据库查询
     * @return PersistentTokenRepository
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //在系统启动的时候自动创建表
		tokenRepository.setCreateTableOnStartup(false);
        return tokenRepository;
    }
}
