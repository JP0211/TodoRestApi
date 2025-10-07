package com.example.todo_rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
@RequestMapping("/api/todos")
public class TodoRestApiApplication {

    private final Map<Long, Todo> todos = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public static void main(String[] args) {
        SpringApplication.run(TodoRestApiApplication.class, args);
    }

    public static class Todo {
        public Long id;
        public String title;
        public boolean completed;

        public Todo(Long id, String title, boolean completed) {
            this.id = id;
            this.title = title;
            this.completed = completed;
        }
    }

    @GetMapping
    public Collection<Todo> list() {
        return todos.values();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> get(@PathVariable Long id) {
        Todo todo = todos.get(id);
        return (todo != null) ? ResponseEntity.ok(todo) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Todo create(@RequestBody Map<String, String> body) {
        Long id = counter.getAndIncrement();
        String title = body.get("title");
        Todo todo = new Todo(id, title, false);
        todos.put(id, todo);
        return todo;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Todo todo = todos.get(id);
        if (todo == null) return ResponseEntity.notFound().build();

        if (body.containsKey("title")) {
            todo.title = body.get("title").toString();
        }

        if (body.containsKey("completed")) {
            todo.completed = Boolean.parseBoolean(body.get("completed").toString());
        }

        return ResponseEntity.ok(todo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!todos.containsKey(id)) return ResponseEntity.notFound().build();
        todos.remove(id);
        return ResponseEntity.noContent().build();
    }
}
