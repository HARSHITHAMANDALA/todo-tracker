package com.example.todo_tracker.service;


import com.example.todo_tracker.model.Task;
import com.example.todo_tracker.model.Task.Priority;
import com.example.todo_tracker.model.TaskStats;
import com.example.todo_tracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository repository;
    @InjectMocks private TaskService service;

    @Test
    void createSavesTaskAsNotCompleted() {
        Task task = new Task("Write report", "CIA 2", Priority.HIGH);
        task.setCompleted(true);
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        assertFalse(service.create(task).isCompleted());
    }

    @Test
    void toggleFlipsCompletedFlag() {
        Task task = new Task("Deploy", null, Priority.MEDIUM);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);

        assertTrue(service.toggle(1L).isCompleted());
        assertFalse(service.toggle(1L).isCompleted());
    }

    @Test
    void toggleThrowsWhenTaskMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> service.toggle(99L));
    }

    @Test
    void deleteThrowsWhenTaskMissing() {
        when(repository.existsById(5L)).thenReturn(false);
        assertThrows(TaskNotFoundException.class, () -> service.delete(5L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void statsComputesPending() {
        when(repository.count()).thenReturn(5L);
        when(repository.countByCompleted(true)).thenReturn(2L);

        TaskStats stats = service.stats();
        assertEquals(3L, stats.pending());
    }
}
