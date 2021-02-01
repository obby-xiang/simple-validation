package com.obby;

import com.obby.validation.Validator;
import com.obby.validation.rule.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class SimpleValidationController {

    @GetMapping("/")
    public String showForm() {
        return "form";
    }

    @PostMapping("/")
    public ResponseEntity<String> validateForm(@RequestBody Map<String, String> form) {
        Validator.make()
                .fieldValidator(
                        Validator.FieldValidator.make()
                                .attribute("foo")
                                .rule(Required.make())
                )
                .fieldValidator(
                        Validator.FieldValidator.make()
                                .attribute("bar")
                                .rule(Required.make())
                )
                .abort(true)
                .validate(form);

        return ResponseEntity.ok("The given data is valid.");
    }

}
