package sample.web.secure.pages;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Secured("ROLE_ADMIN")
@RestController
public class Admin {
    @Autowired
    AuthenticationManager authenticationManager;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "固定文字列を返します。"),
            @ApiResponse(code = 403, message = "権限がありません。")
    })
    @RequestMapping(path = "/pages/admin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> get(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("getAuthorities() = " + userDetails.getAuthorities());
        System.out.println("Authentication = " + SecurityContextHolder.getContext().getAuthentication());
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("getPrincipal() = " + a.getPrincipal());
        System.out.println("getCredentials() = " + a.getCredentials());
        System.out.println("AuthenticationManager = " + authenticationManager);
        if (authenticationManager != null) {
            for (AuthenticationProvider p : ((ProviderManager) authenticationManager).getProviders()) {
                System.out.println("\tAuthenticationProvider = " + p);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("password", userDetails.getPassword());
        response.put("page", "admin");
        response.put("authorities", userDetails.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        return response;
    }
}
