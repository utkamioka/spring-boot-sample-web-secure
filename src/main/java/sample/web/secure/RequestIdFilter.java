package sample.web.secure;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements Filter {
    public static final String HTTP_HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;

        String requestId = Optional.ofNullable(req.getHeader(HTTP_HEADER_REQUEST_ID)).orElse(UUID.randomUUID().toString());
        resp.setHeader(HTTP_HEADER_REQUEST_ID, requestId);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
