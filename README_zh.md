# 關係閉包表在 Spring Data JPA 中的實現

[English](README.md) | [中文](README_zh.md)

這是一個在 Spring Data JPA 中實現閉包表模式的簡單示例。閉包表模式是一種在關係型資料庫中儲存階層式資料的方法。這是一種簡單且高效的方式來儲存和查詢階層式資料。

## 目錄

- [技術棧](#技術棧)
- [快速開始](#快速開始)
    - [資料庫設定](#資料庫設定)
- [API 說明](#api-說明)
    - [建立](#建立)
        - [根部門](#根部門)
        - [具有父部門的部門](#具有父部門的部門)
    - [查詢](#查詢)
        - [完整的部門樹](#完整的部門樹)
        - [按 ID 查詢部門樹](#按-id-查詢部門樹)
    - [移動](#移動)
        - [移動部門](#移動部門)
    - [刪除](#刪除)
        - [刪除部門及所有子部門](#刪除部門及所有子部門)

## 技術棧

- Java 17
- Kotlin 1.9.25
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- PostgreSQL 16.4

## 快速開始

### 資料庫設定

```shell
docker run \
-e POSTGRES_DB=test \
-e POSTGRES_PASSWORD=dev \
-e POSTGRES_USER=dev \
-p 5432:5432 \
postgres:16.4 -d
```

## API 說明

### 建立

#### 根部門

```shell
curl -X POST "http://localhost:8080/departments?name=dep1"
```

#### 具有父部門的部門

```shell
curl -X POST "http://localhost:8080/departments?name=dep2&parentId=1"
```

### 查詢

#### 完整的部門樹

```shell
curl -X GET "http://localhost:8080/departments/tree"
```

#### 按 ID 查詢部門樹

```shell
curl -X GET "http://localhost:8080/departments/1/tree"
```

### 移動

#### 移動部門

```shell
curl -X POST "http://localhost:8080/departments/2/move?newParentId=3"
```

### 刪除

#### 刪除部門及所有子部門

```shell
curl -X DELETE "http://localhost:8080/departments/2?strategy=CASCADE"
```