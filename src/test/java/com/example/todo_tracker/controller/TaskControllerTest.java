package com.example.todo_tracker.controller;

import com.example.todo_tracker.model.Task;
import com.example.todo_tracker.model.TaskStats;
import com.example.todo_tracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TaskService service;

    @BeforeEach
    void setUp() {
        when(service.stats()).thenReturn(new TaskStats(0, 0, 0));
    }

    @Test
    void indexRendersPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void addValidTaskRedirects() throws Exception {
        mockMvc.perform(post("/tasks").param("title", "Deploy").param("priority", "HIGH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(service).create(any(Task.class));
    }

    @Test
    void addBlankTitleShowsError() throws Exception {
        mockMvc.perform(post("/tasks").param("title", " ").param("priority", "LOW"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("task", "title"));
        verify(service, never()).create(any(Task.class));
    }
}