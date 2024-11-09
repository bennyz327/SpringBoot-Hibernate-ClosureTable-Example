package com.example.springboothibernateclosuretableexample.service

import com.example.springboothibernateclosuretableexample.dto.DeleteStrategy
import com.example.springboothibernateclosuretableexample.dto.DepartmentTreeDTO
import com.example.springboothibernateclosuretableexample.entity.Department
import com.example.springboothibernateclosuretableexample.entity.DepartmentClosure
import com.example.springboothibernateclosuretableexample.entity.DepartmentClosureId
import com.example.springboothibernateclosuretableexample.repository.DepartmentClosureRepository
import com.example.springboothibernateclosuretableexample.repository.DepartmentRepository
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository,
    private val departmentClosureRepository: DepartmentClosureRepository,
    private val entityManager: EntityManager,
) {

    @Transactional
    fun createDepartment(name: String, parentId: Long?): Department {
        val department = Department(name = name)
        val savedDepartment = departmentRepository.save(department)
        val closures = mutableListOf<DepartmentClosure>()
        closures.add(savedDepartment.createSelfClosure())
        if (parentId != null) {
            val parentClosures = departmentClosureRepository.findAllByIdDescendantId(parentId)
            parentClosures.forEach { parentClosure ->
                closures.add(
                    DepartmentClosure(
                        id = DepartmentClosureId(
                            ancestorId = parentClosure.id.ancestorId,
                            descendantId = savedDepartment.id
                        ),
                        depth = parentClosure.depth + 1,
                        department = savedDepartment
                    )
                )
            }
        }
        departmentClosureRepository.saveAll(closures)
        return savedDepartment
    }


    fun getDepartment(id: Long): Department? {
        return departmentRepository.findById(id).orElse(null)
    }


    fun getDepartmentTree(id: Long): DepartmentTreeDTO? {
        val department = departmentRepository.findById(id).orElse(null) ?: return null
        return buildDepartmentTree(department)
    }


    fun getAllRootDepartments(): List<DepartmentTreeDTO> {
        val rootDepartments = departmentRepository.findRootDepartments()
        return rootDepartments.map { buildDepartmentTree(it) }
    }


    @Transactional
    fun updateDepartment(id: Long, name: String) {
        val department = departmentRepository.findById(id)
            .orElseThrow { NoSuchElementException("Department not found") }
        department.name = name
        departmentRepository.save(department)
    }


    @Transactional
    fun deleteDepartment(id: Long, strategy: DeleteStrategy) {
        departmentRepository.findById(id)
            .orElseThrow { NoSuchElementException("Department with id $id not found") }
        departmentClosureRepository.deleteClosuresByDescendantIds(listOf(id))
        departmentRepository.deleteById(id)
        when (strategy) {
            DeleteStrategy.CASCADE -> {
                cascadeDelete(id)
            }
        }
    }


    @Transactional
    fun moveDepartment(departmentId: Long, newParentId: Long?) {
        // 移除舊的閉包關係
        val department = departmentRepository
            .findById(departmentId)
            .orElseThrow { NoSuchElementException("Target Department not found") }
        if (newParentId != null) {
            departmentRepository
                .findById(newParentId)
                .orElseThrow { NoSuchElementException("New Parent Department not found") }
        }
        departmentClosureRepository.deleteClosuresForMove(departmentId)
        // 創建新的閉包關係
        val closures = mutableListOf<DepartmentClosure>()
        closures.add(department.createSelfClosure())
        if (newParentId != null) {
            val parentClosures = departmentClosureRepository.findAllByIdDescendantId(newParentId)
            parentClosures.forEach { parentClosure ->
                closures.add(
                    DepartmentClosure(
                        id = DepartmentClosureId(
                            ancestorId = parentClosure.id.ancestorId,
                            descendantId = departmentId,
                        ),
                        depth = parentClosure.depth + 1,
                        department = departmentRepository.getReferenceById(departmentId)
                    )
                )
            }
        }
        departmentClosureRepository.saveAll(closures)
    }


    private fun Department.createSelfClosure() = DepartmentClosure(
        id = DepartmentClosureId(
            ancestorId = this.id,
            descendantId = this.id
        ),
        depth = 0,
        department = this
    )


    // TODO 閉包表會有殘留的閉包關係，研究中
    private fun reattachChildrenToNewParent(departmentId: Long, newParent: Department) {
        val directChildrenClosures = departmentClosureRepository.findDirectChildren(departmentId)
        directChildrenClosures.forEach { closure ->
            // 移除舊的閉包關係
            val childId = closure.id.descendantId
            entityManager.flush()
            entityManager.clear()
            println("有成功? descendantId=${childId} ancestorId=${departmentId}")
            departmentClosureRepository.deleteClosuresFromAncestor(childId, departmentId)
            // 創建新的閉包關係
            val newParentClosures = departmentClosureRepository.findAllByIdDescendantId(newParent.id)
            val newClosures = newParentClosures.map { parentClosure ->
                DepartmentClosure(
                    id = DepartmentClosureId(
                        ancestorId = parentClosure.id.ancestorId,
                        descendantId = childId
                    ),
                    depth = parentClosure.depth + 1,
                    department = departmentRepository.findById(childId).get()
                )
            }
            departmentClosureRepository.saveAll(newClosures)
        }
    }


    fun cascadeDelete(departmentId: Long) {
        val descendantIds = departmentClosureRepository.findDescendants(departmentId)
            .map { it.id.descendantId }
            .distinct()
        departmentClosureRepository.deleteClosuresByDescendantIds(descendantIds)
        entityManager.clear() // 清除 EntityManager 中的所有緩存，否則下一行的刪除操作會被 JPA 忽略
        departmentRepository.deleteAllById(descendantIds)
    }


    private fun buildDepartmentTree(department: Department): DepartmentTreeDTO {
        val children = departmentClosureRepository.findDirectChildren(department.id)
            .mapNotNull { closure ->
                departmentRepository.findById(closure.id.descendantId).orElse(null)
            }
            .map { childDepartment ->
                buildDepartmentTree(childDepartment)
            }
        return DepartmentTreeDTO(
            id = department.id,
            name = department.name,
            children = children
        )
    }
}

