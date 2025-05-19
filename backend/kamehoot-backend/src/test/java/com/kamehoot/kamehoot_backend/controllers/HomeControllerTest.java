package com.kamehoot.kamehoot_backend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.kamehoot.kamehoot_backend.config.JWTConfig;
import com.kamehoot.kamehoot_backend.services.JwtService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({
                HomeController.class,
                AuthenticationController.class
})
@Import({ JWTConfig.class, JwtService.class })
public class HomeControllerTest {

        @Autowired
        MockMvc mvc;

        @Test
        void rootWhenUnauthenticatedThen401() throws Exception {
                this.mvc.perform(get("/"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void rootWhenAuthenticatedThenSaysHelloUser() throws Exception {
                MvcResult result = this.mvc.perform(post("/token")
                                .with(httpBasic("stef", "stef")))
                                .andExpect(status().isOk())
                                .andReturn();

                String token = result.getResponse().getContentAsString();

                this.mvc.perform(get("")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(content().string("Welcome stef"));
        }

        @Test
        @WithMockUser
        public void rootWithMockUserStatusIsOK() throws Exception {
                this.mvc.perform(get("/"))
                                .andExpect(status().isOk());
        }
}
