package com.example.springboothibernateclosuretableexample.entity

import jakarta.persistence.*

@Entity
@Table(name = "department_closure")
class DepartmentClosure(
    @EmbeddedId
    var id: DepartmentClosureId,

    @Column(nullable = false)
    var depth: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("descendantId")
    @JoinColumn(name = "descendant")
    var department: Department
)