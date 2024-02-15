package com.cairn.waypoint.dashboard.endpoints;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Foo {
    private long id;
    private String name;

    // constructor, getters and setters
}
