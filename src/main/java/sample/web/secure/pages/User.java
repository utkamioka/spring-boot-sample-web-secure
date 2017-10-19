package sample.web.secure.pages;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Secured({"ROLE_ADMIN", "ROLE_USER"})
@RestController
public class User {
    @RequestMapping(path = "/pages/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> get(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("getAuthorities() = " + userDetails.getAuthorities());
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("password", userDetails.getPassword());
        response.put("page", "user");
        response.put("authorities", userDetails.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        return response;
    }
}
