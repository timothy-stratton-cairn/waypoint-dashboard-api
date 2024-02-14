package com.cairn.waypoint.dashboard.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/foos")
public class FooController {

    @GetMapping(value = "/{id}")
    public Foo findOne(@PathVariable Long id) {
        return new Foo(id, "test" + id);
    }

    @GetMapping
    public List<Foo> findAll() {
        List<Foo> fooList = new ArrayList<>();
        fooList.add(new Foo(1, "test1"));
        fooList.add(new Foo(2, "test2"));
        fooList.add(new Foo(3, "test3"));
        return fooList;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void create(@RequestBody Foo newFoo) {
        log.info("Foo created");
    }
}
