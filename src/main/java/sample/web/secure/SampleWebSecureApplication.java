/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.web.secure;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
@Controller
public class SampleWebSecureApplication extends WebMvcConfigurerAdapter {

    @GetMapping(value = {"/", "/home"})
    public String home(Map<String, Object> model) {
        model.put("message", "Hello World");
        model.put("title", "Hello Home");
        model.put("date", new Date());
        return "home";
    }

    @RequestMapping("/foo")
    public String foo() {
        throw new RuntimeException("Expected exception in controller");
    }

//    // XXX: #configure(HttpSecurity)で、loginPage()を指定しない場合（Spring-bootのデフォルトログインページを使う場合）は不要
//    @Override
//    public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
//        registry.addViewController("/login").setViewName("login");
//    }

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(SampleWebSecureApplication.class).run(args);
    }

    public static class FilterBefore1 extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FilterBefore1.doFilter()");
            chain.doFilter(request, response);
        }
    }

    public static class FilterBefore2 extends AbstractAuthenticationProcessingFilter {
        protected FilterBefore2(RequestMatcher requiresAuthenticationRequestMatcher, AuthenticationManager am) {
            super(requiresAuthenticationRequestMatcher);
            setAuthenticationManager(am);
        }
        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FilterBefore2.attemptAuthentication()");
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("admin", "admin"));
        }
        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            System.out.println(">>>>>>>>>>>>>>>>> FilterBefore2.successfulAuthentication()");
            System.out.println("authResult = " + authResult);
            super.successfulAuthentication(request, response, chain, authResult);
        }
        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
            System.out.println(">>>>>>>>>>>>>>>>> FilterBefore2.unsuccessfulAuthentication()");
            System.out.println("failed = " + failed);
            super.unsuccessfulAuthentication(request, response, failed);
        }
    }

    public static class FilterBefore3 extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FilterBefore3.doFilter()");
            HttpServletRequest req = (HttpServletRequest)request;
            System.out.println(req.getParameter("user"));
            System.out.println(req.getParameter("pass"));

            List<GrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//            authorities.add(new SimpleGrantedAuthority("ROLE_XXX"));

            Authentication a = new UsernamePasswordAuthenticationToken(req.getParameter("user"), null, authorities);
            System.out.println(a);
            SecurityContextHolder.getContext().setAuthentication(a);
            chain.doFilter(request, response);
        }
    }

    public static class FilterAfter1 extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FilterAfter1.doFilter()");
            chain.doFilter(request, response);
        }
    }

    @Configuration
    // ACCESS_OVERRIDE_ORDERは(BASIC_AUTH_ORDER-2)なので、BASIC認証より先に処理される
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            // swaggerのページは認証外とする
            web.ignoring().mvcMatchers("/v2/api-docs", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // CSRFをdisableにしないと、 POST /logoutが上手くいかない（対策不明）
            http.csrf().disable()
                    .authorizeRequests()
                    .anyRequest().fullyAuthenticated()
                    .and()
                    .formLogin()
                    .and()
                    .addFilterBefore(new FilterBefore1(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new FilterBefore2(new AntPathRequestMatcher("/force-login"), authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new FilterBefore3(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterAfter(new FilterAfter1(), UsernamePasswordAuthenticationFilter.class);
//                    // XXX: loginPage()を指定しなければ、Spring-bootのデフォルトログインページになる
//                    .loginPage("/login").failureUrl("/login?error").permitAll()
//                    .and()
//                    .logout().permitAll();
        }

        private @Autowired
        HttpServletRequest request;
        private @Autowired
        HttpServletResponse response;

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationEventPublisher(new AuthenticationEventPublisher() {
                @Override
                public void publishAuthenticationSuccess(Authentication authentication) {
                    System.out.println("ApplicationSecurity.publishAuthenticationSuccess()");
                    System.out.println("authentication = " + authentication);
                    System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
                    System.out.println("authentication.getCredentials() = " + authentication.getCredentials());
                    System.out.println("authentication.getAuthorities() = " + authentication.getAuthorities());

                    System.out.println("request = " + request);
                    System.out.println("response = " + response);

                    HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                    System.out.println("request = " + req);
                    HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
                    System.out.println("response = " + resp);

                    System.out.println(RequestIdFilter.HTTP_HEADER_REQUEST_ID + " = " + response.getHeader(RequestIdFilter.HTTP_HEADER_REQUEST_ID));
                    System.out.println(RequestIdFilter.HTTP_HEADER_REQUEST_ID + " = " + resp.getHeader(RequestIdFilter.HTTP_HEADER_REQUEST_ID));
                }

                @Override
                public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
                    System.out.println("ApplicationSecurity.publishAuthenticationFailure()");
                    System.out.println("exception = " + exception);
                    System.out.println("authentication = " + authentication);
                    System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
                    System.out.println("authentication.getCredentials() = " + authentication.getCredentials());
                    System.out.println("authentication.getAuthorities() = " + authentication.getAuthorities());
                    System.out.println("request = " + request);
                    System.out.println("response = " + response);
                    System.out.println(RequestIdFilter.HTTP_HEADER_REQUEST_ID + " = " + response.getHeader(RequestIdFilter.HTTP_HEADER_REQUEST_ID));
                }
            });
            auth.inMemoryAuthentication()
                    .withUser("admin").password("admin").roles("ADMIN", "USER")
                    .and()
                    .withUser("user").password("user").roles("USER")
                    .and()
                    .withUser("guest").password("guest").roles();
        }
    }
}
