package com.example.todo_tracker.repository;

import com.example.todo_tracker.model.Task; 
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    long countByCompleted(boolean completed);
    List<Task> findAllByOrderByCompletedAscIdDesc();
}
