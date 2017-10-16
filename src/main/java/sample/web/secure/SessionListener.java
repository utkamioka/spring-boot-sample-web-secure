package sample.web.secure;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

// see)
// https://moelholm.com/2016/08/22/spring-boot-sessions-actuator-endpoint/

// spring-bootのform認証でのセッション生成では呼ばれない。（原因不明）

@Component
public class SessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        System.out.println(">>> SessionListener.sessionCreated()");
        System.out.println("sessionEvent.getSession().getId() = " + sessionEvent.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        System.out.println(">>> SessionListener.sessionDestroyed()");
        System.out.println("sessionEvent.getSession().getId() = " + sessionEvent.getSession().getId());
    }
}
