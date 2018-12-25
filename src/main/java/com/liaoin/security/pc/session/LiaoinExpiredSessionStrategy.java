/**
 * 
 */
package com.liaoin.security.pc.session;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * @author mc
 * @version 1.0v
 * 自定义超时策略
 */
public class LiaoinExpiredSessionStrategy extends AbstractSessionStrategy implements SessionInformationExpiredStrategy {

	public LiaoinExpiredSessionStrategy(String invalidSessionUrl) {
		super(invalidSessionUrl);
	}

	/**
	 * 接口可以取得超时事件或自定义并发登陆处理
	 */
	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		onSessionInvalid(event.getRequest(), event.getResponse());
	}
	
	/* (non-Javadoc)
	 * @see com.imooc.security.browser.session.AbstractSessionStrategy#isConcurrency()
	 */
	@Override
	protected boolean isConcurrency() {
		return true;
	}

}
