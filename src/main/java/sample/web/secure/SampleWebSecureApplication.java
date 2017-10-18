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

import java.util.Date;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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
                    .formLogin();
//                    // XXX: loginPage()を指定しなければ、Spring-bootのデフォルトログインページになる
//                    .loginPage("/login").failureUrl("/login?error").permitAll()
//                    .and()
//                    .logout().permitAll();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("admin").password("admin").roles("ADMIN", "USER")
                    .and()
                    .withUser("user").password("user").roles("USER");
        }

        // XXX: #configure(AuthenticationManagerBuilder)で作成されるAuthenticationManagerが取得できるか？
        @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
        @Override
        protected AuthenticationManager authenticationManager() throws Exception {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ApplicationSecurity.authenticationManager()");
            return super.authenticationManager();
        }
    }
}
