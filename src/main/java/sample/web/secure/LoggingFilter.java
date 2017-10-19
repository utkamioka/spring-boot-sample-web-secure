package sample.web.secure;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class LoggingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    // spring-bootのlogin(POST)は呼ばれない
    // たぶん、spring-bootのloginの方がプライオリティが高く、かつchain.doFilter()してないから。。。（対策不明）
    // -> @Orderで最高レベルのプライオリティを指定すれば、呼び出される。

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        System.out.println(">>> " + req.getMethod() + " " + req.getRequestURI());
        if (!isIgnore(req.getRequestURI())) {
            for (String n : Collections.list(req.getHeaderNames())) {
                System.out.println("\t> " + n + " = " + req.getHeader(n));
            }
            for (String n : resp.getHeaderNames()) {
                System.out.println("\t< " + n + " = " + resp.getHeader(n));
            }
        }

        chain.doFilter(request, response);

        int status = ((HttpServletResponse)response).getStatus();
        System.out.println(">>> status = " + status);
    }

    private boolean isIgnore(String uri) {
        if (uri.startsWith("/css/")) return true;
        if (uri.equals("/favicon.ico")) return true;
        return false;
    }

    @Override
    public void destroy() {

    }
}
