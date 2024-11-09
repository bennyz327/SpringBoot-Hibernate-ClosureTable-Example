package com.example.springboothibernateclosuretableexample.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class DepartmentClosureId(
    @Column(name = "ancestor")
    val ancestorId: Long,

    @Column(name = "descendant")
    val descendantId: Long,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DepartmentClosureId

        if (ancestorId != other.ancestorId) return false
        if (descendantId != other.descendantId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ancestorId.hashCode()
        result = 31*result + descendantId.hashCode()
        return result
    }
}