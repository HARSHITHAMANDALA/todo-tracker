package com.example.todo_tracker.controller;

import com.example.todo_tracker.model.Task;
import com.example.todo_tracker.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    private final TaskService service;
    private final String appTitle;
    private final String appVersion;

    public TaskController(TaskService service,
                          @Value("${app.title:Task Tracker}") String appTitle,
                          @Value("${app.version:dev}") String appVersion) {
        this.service = service;
        this.appTitle = appTitle;
        this.appVersion = appVersion;
    }

    @GetMapping("/")
    public String index(Model model) {
        populate(model);
        model.addAttribute("task", new Task());
        return "index";
    }

    @PostMapping("/tasks")
    public String add(@Valid @ModelAttribute("task") Task task, BindingResult result, Model model) {
        if (result.hasErrors()) {
            populate(model);
            return "index";
        }
        service.create(task);
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        service.toggle(id);
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/";
    }

    private void populate(Model model) {
        model.addAttribute("tasks", service.findAll());
        model.addAttribute("stats", service.stats());
        model.addAttribute("appTitle", appTitle);
        model.addAttribute("appVersion", appVersion);
    }
}
