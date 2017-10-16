package sample.web.secure.pages;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Secured({"ROLE_ADMIN", "ROLE_USER"})
@RestController
public class User {
    @RequestMapping(path = "/pages/user", method = RequestMethod.GET)
    public String get() {
        return "user";
    }
}
