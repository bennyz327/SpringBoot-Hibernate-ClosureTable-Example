package com.example.springboothibernateclosuretableexample.repository

import com.example.springboothibernateclosuretableexample.entity.Department
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DepartmentRepository : JpaRepository<Department, Long> {

    @Query(
        """
        SELECT d FROM Department d
        WHERE NOT EXISTS (
            SELECT 1 FROM DepartmentClosure dc
            WHERE dc.id.descendantId = d.id AND dc.depth = 1
        )
    """
    )
    fun findRootDepartments(): List<Department>

}