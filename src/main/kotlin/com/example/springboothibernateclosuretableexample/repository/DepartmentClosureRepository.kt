package com.example.springboothibernateclosuretableexample.repository

import com.example.springboothibernateclosuretableexample.entity.DepartmentClosure
import com.example.springboothibernateclosuretableexample.entity.DepartmentClosureId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DepartmentClosureRepository : JpaRepository<DepartmentClosure, DepartmentClosureId> {


    /**
     * 查找特定部門的直接子部門
     */
    @Query(
        """
        SELECT dc 
        FROM DepartmentClosure dc 
        WHERE dc.id.ancestorId = :ancestorId AND dc.depth = 1
        """
    )
    fun findDirectChildren(@Param("ancestorId") ancestorId: Long): List<DepartmentClosure>


    /**
     * 查找特定部門的所有子孫部門
     */
    @Query(
        """
            SELECT dc
            FROM DepartmentClosure dc
            WHERE dc.id.ancestorId = :ancestorId AND dc.depth > 0
            ORDER BY dc.depth, dc.id.descendantId
            """
    )
    fun findDescendants(@Param("ancestorId") ancestorId: Long): List<DepartmentClosure>


    /**
     * 查找特定部門的直接父部門
     */
    @Query(
        """
        SELECT dc 
        FROM DepartmentClosure dc 
        WHERE dc.id.descendantId = :descendantId AND dc.depth = 1
        """
    )
    fun findDirectParent(@Param("descendantId") descendantId: Long): Optional<DepartmentClosure>


    /**
     * 查找特定部門的所有祖先部門
     */
    fun findAllByIdDescendantId(descendantId: Long): List<DepartmentClosure>


    /**
     * 刪除特定部門的閉包紀錄
     */
    @Modifying
    @Query("DELETE FROM DepartmentClosure dc WHERE dc.id.descendantId IN :descendantIds")
    fun deleteClosuresByDescendantIds(@Param("descendantIds") descendantIds: List<Long>)


    /**
     * 刪除子部門指向被刪除部門及其祖先的閉包紀錄
     */
    @Modifying
    @Query(
        """
        DELETE FROM DepartmentClosure dc
        WHERE dc.id.descendantId = :descendantId
        AND dc.id.ancestorId IN (
            SELECT dc2.id.ancestorId FROM DepartmentClosure dc2 WHERE dc2.id.descendantId = :ancestorId
        )
    """
    )
    fun deleteClosuresFromAncestor(@Param("descendantId") descendantId: Long, @Param("ancestorId") ancestorId: Long)


    /**
     * 刪除即將被移動部門的閉包紀錄
     */
    @Modifying
    @Query(
        """
            DELETE 
            FROM DepartmentClosure dc 
            WHERE 
                dc.id.descendantId IN (
                    SELECT d.id 
                    FROM Department d 
                    WHERE d.id = :departmentId
                ) 
                AND 
                dc.id.ancestorId IN (
                    SELECT dc2.id.ancestorId 
                    FROM DepartmentClosure dc2 
                    WHERE dc2.id.descendantId = :departmentId AND dc2.id.ancestorId != :departmentId
                )
                """
    )
    fun deleteClosuresForMove(@Param("departmentId") departmentId: Long)
}