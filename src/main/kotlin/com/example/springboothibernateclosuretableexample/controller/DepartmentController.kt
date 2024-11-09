package com.example.springboothibernateclosuretableexample.controller

import com.example.springboothibernateclosuretableexample.dto.DeleteStrategy
import com.example.springboothibernateclosuretableexample.dto.DepartmentDTO
import com.example.springboothibernateclosuretableexample.service.DepartmentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/departments")
class DepartmentController(
    private val departmentService: DepartmentService,
) {

    @PostMapping
    fun createDepartment(
        @RequestParam name: String,
        @RequestParam(required = false) parentId: Long?,
    ): ResponseEntity<Any> {
        val department = departmentService.createDepartment(name, parentId)
        return ResponseEntity.ok(department)
    }

    @GetMapping("/{id}")
    fun getDepartment(@PathVariable id: Long): ResponseEntity<Any> {
        val department = departmentService.getDepartment(id)
            ?: return ResponseEntity.notFound().build()
        val departmentDTO = DepartmentDTO(
            id = department.id,
            name = department.name
        )
        return ResponseEntity.ok(departmentDTO)
    }

    @GetMapping("/{id}/tree")
    fun getDepartmentTree(@PathVariable id: Long): ResponseEntity<Any> {
        val tree = departmentService.getDepartmentTree(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(tree)
    }

    @GetMapping("/tree")
    fun getAllRootDepartments(): ResponseEntity<Any> {
        val roots = departmentService.getAllRootDepartments()
        return ResponseEntity.ok(roots)
    }

    @PutMapping("/{id}")
    fun updateDepartment(
        @PathVariable id: Long,
        @RequestParam name: String,
    ): ResponseEntity<Any> {
        departmentService.updateDepartment(id, name)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun deleteDepartment(
        @PathVariable id: Long,
        @RequestParam strategy: DeleteStrategy,
    ): ResponseEntity<Any> {
        departmentService.deleteDepartment(id, strategy)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{id}/move")
    fun moveDepartment(
        @PathVariable id: Long,
        @RequestParam(required = false) newParentId: Long?,
    ): ResponseEntity<Any> {
        try {
            departmentService.moveDepartment(id, newParentId)
            return ResponseEntity.ok().build()
        } catch (e: NoSuchElementException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }
}