# Closure Table Implementation For Spring Data JPA

[English](README.md) | [中文](README_zh.md)

This is a simple implementation of the closure table pattern for Spring Data JPA. The closure table pattern is a way to store hierarchical data in a relational database. It is a simple and efficient way to store and query hierarchical data.

## Table of Contents
  - [Tech Stack](#tech-stack)
  - [Getting Started](#getting-started)
    - [Database Setup](#database-setup)
  - [API Description](#api-description)
    - [Create](#create)
      - [root department](#root-department)
      - [department with parent](#department-with-parent)
    - [Find](#find)
      - [full departments tree](#full-departments-tree)
      - [department tree by id](#department-tree-by-id)
    - [Move](#move)
      - [move department](#move-department)
    - [Delete](#delete)
      - [delete department and all children](#delete-department-and-all-children)

## Tech Stack
- Java 17
- Kotlin 1.9.25
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- PostgreSQL 16.4

## Getting Started

### database setup
```shell
docker run \
-e POSTGRES_DB=test \
-e POSTGRES_PASSWORD=dev \
-e POSTGRES_USER=dev \
-p 5432:5432 \
postgres:16.4 -d
```

## API Description

### Create

#### root department
```shell
curl -X POST "http://localhost:8080/departments?name=dep1"
```

#### department with parent
```shell
curl -X POST "http://localhost:8080/departments?name=dep2&parentId=1"
```

### Find

#### full departments tree
```shell
curl -X GET "http://localhost:8080/departments/tree"
```

#### department tree by id
```shell
curl -X GET "http://localhost:8080/departments/1/tree"
```

### Move

#### move department
```shell
curl -X POST "http://localhost:8080/departments/2/move?newParentId=3"
```

### Delete

#### delete department and all children
```shell
curl -X DELETE "http://localhost:8080/departments/2?strategy=CASCADE"
```
