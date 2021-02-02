package com.obby.validation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "logging.level.org.springframework.web=DEBUG")
class SimpleValidationApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Test
    public void checkFormWhenFooMissingThenFailure() throws Exception {
        RequestBuilder request = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.gson.toJson(ImmutableMap.of("bar", "Bar")));

        this.mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("errors.foo").isArray())
                .andExpect(jsonPath("errors.bar").doesNotExist());
    }

    @Test
    public void checkFormWhenFooEmptyThenFailure() throws Exception {
        RequestBuilder request = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.gson.toJson(ImmutableMap.builder().put("foo", "").put("bar", "Bar").build()));

        this.mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("errors.foo").isArray())
                .andExpect(jsonPath("errors.bar").doesNotExist());
    }

    @Test
    public void checkFormWhenBarMissingThenFailure() throws Exception {
        RequestBuilder request = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.gson.toJson(ImmutableMap.of("foo", "Foo")));

        this.mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("errors.foo").doesNotExist())
                .andExpect(jsonPath("errors.bar").isArray());
    }

    @Test
    public void checkFormWhenBarEmptyThenFailure() throws Exception {
        RequestBuilder request = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.gson.toJson(ImmutableMap.builder().put("foo", "Foo").put("bar", "").build()));

        this.mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("errors.foo").doesNotExist())
                .andExpect(jsonPath("errors.bar").isArray());
    }

    @Test
    public void checkFormWhenValidRequestThenSuccess() throws Exception {
        RequestBuilder request = post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.gson.toJson(ImmutableMap.builder().put("foo", "Foo").put("bar", "Bar").build()));

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("errors").doesNotExist());
    }

}
