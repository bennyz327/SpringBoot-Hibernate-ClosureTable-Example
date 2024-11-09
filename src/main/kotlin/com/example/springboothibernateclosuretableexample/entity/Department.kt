package com.example.springboothibernateclosuretableexample.entity

import jakarta.persistence.*

@Entity
@Table(name = "departments")
class Department(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @OneToMany(mappedBy = "department", cascade = [CascadeType.ALL], orphanRemoval = true)
    var closures: MutableList<DepartmentClosure> = mutableListOf()
)