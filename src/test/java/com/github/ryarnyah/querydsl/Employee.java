package com.github.ryarnyah.querydsl;

import jakarta.annotation.Generated;

/**
 * Employee is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Employee {

    private Long deptId;

    private Long id;

    private String name;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

