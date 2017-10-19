package sample.web.secure.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@EnableGlobalMethodSecurity(securedEnabled = true)
@RestController
@RequestMapping(path = "/api/books")
public class Books {

    public static class Book {
        public String id;
        public String title;
        public Book() {}
        public Book(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    @Secured("ROLE_USER")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> get() {
        return ImmutableList.of(new Book("1", "alpha"), new Book("2", "bravo"), new Book("3", "charlie"));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> post() {
        return ImmutableMap.<String, Object>builder().put("status", true).build();
    }
}
