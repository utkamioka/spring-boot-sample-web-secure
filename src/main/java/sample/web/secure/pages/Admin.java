package sample.web.secure.pages;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Secured("ROLE_ADMIN")
@RestController
public class Admin {
    @RequestMapping(path = "/pages/admin", method = RequestMethod.GET)
    public String get(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("getAuthorities() = " + userDetails.getAuthorities());
        return "admin";
    }
}
