package com.obby;

import com.obby.validation.Validator;
import com.obby.validation.rule.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class SimpleValidationController {

    @PostMapping("/language/switch")
    public ResponseEntity<String> switchLanguage(String language, HttpServletResponse response) {
        String lang = "en_US".equals(language) || "zh_CN".equals(language) ? language : "en_US";

        response.addCookie(new Cookie("language", lang));

        return ResponseEntity.ok(lang);
    }

    @GetMapping("/form")
    public String showForm() {
        return "form";
    }

    @PostMapping("/form")
    public ResponseEntity<String> validateForm(Map<String, String> form) {
        Validator.create()
                .fieldValidator(
                        Validator.FieldValidator.create()
                                .attribute("foo")
                                .customAttribute("Foo")
                                .rule(new Required())
                )
                .fieldValidator(
                        Validator.FieldValidator.create()
                                .attribute("bar")
                                .customAttribute("Bar")
                                .rule(new Required())
                )
                .abort(true)
                .validate(form);

        return ResponseEntity.ok("valid");
    }

}
