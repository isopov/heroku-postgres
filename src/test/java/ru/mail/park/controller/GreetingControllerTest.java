package ru.mail.park.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class GreetingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testGreet() throws Exception {
    mockMvc.perform(post("/greet")
        .param("name", "foo")
        .param("greeting", "bar"))
        .andExpect(status().isOk());
  }

  @Test
  public void testList() throws Exception {
    testGreet();
    testGreet();
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)));
  }

}
