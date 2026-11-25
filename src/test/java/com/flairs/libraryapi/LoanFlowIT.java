package com.flairs.libraryapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// The whole app on a real database, called like a client would. Named *IT: runs in "mvn verify".
// The tests share one database, so each one leaves the data as it found it (or uses data nobody else uses).
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class LoanFlowIT {

    @Autowired MockMvc mvc;

    // TODO 8
    @Test
    void aMemberBorrowsAndReturnsABook() throws Exception {
        String token = login("sara@example.com");
        // Borrow book 1 as sara
        mvc.perform(post("/api/loans").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\": 1}"))
            .andExpect(status().isCreated());
        // Read the id with JsonPath
        String body = mvc.perform(patch("/api/loans/1/return").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Long loanId = Long.valueOf(JsonPath.read(body, "$.id").toString());
        // Second return should fail with 409
        mvc.perform(patch("/api/loans/" + loanId + "/return").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    // TODO 9
    @Test
    void theLastCopyIsAlreadyOut() throws Exception {
        String token = login("sara@example.com");
        // Sara already borrowed Animal Farm (book 2), which has only 1 copy
        mvc.perform(post("/api/loans").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\": 2}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.detail").value("No copies of 'Animal Farm' are available"));
    }

    @Test
    void aMemberCannotBorrowForSomeoneElse() throws Exception {
        mvc.perform(post("/api/loans").header("Authorization", "Bearer " + login("sara@example.com"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"bookId\": 3, \"memberId\": 2}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void aMemberCannotListMembersButALibrarianCan() throws Exception {
        mvc.perform(get("/api/members").header("Authorization", "Bearer " + login("sara@example.com")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/members").header("Authorization", "Bearer " + login("librarian@example.com")))
                .andExpect(status().isOk());
    }

    private String login(String email) throws Exception {
        String json = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.accessToken");
    }
}
