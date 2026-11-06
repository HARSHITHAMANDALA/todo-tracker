package com.example.todo_tracker.service;

import com.example.todo_tracker.model.Task;
import com.example.todo_tracker.model.TaskStats;
import com.example.todo_tracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() {
        return repository.findAllByOrderByCompletedAscIdDesc();
    }

    public Task create(Task task) {
        task.setCompleted(false);
        return repository.save(task);
    }

    public Task toggle(Long id) {
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setCompleted(!task.isCompleted());
        return repository.save(task);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public TaskStats stats() {
        long total = repository.count();
        long completed = repository.countByCompleted(true);
        return new TaskStats(total, completed, total - completed);
    }
}
