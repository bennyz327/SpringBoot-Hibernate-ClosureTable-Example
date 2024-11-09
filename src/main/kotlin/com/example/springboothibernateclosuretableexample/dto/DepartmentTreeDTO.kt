package com.example.springboothibernateclosuretableexample.dto

data class DepartmentTreeDTO(
    val id: Long,
    val name: String,
    val children: List<DepartmentTreeDTO>
)
