package com.mycompany.api.student;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.api.student.repository.Student;
import com.mycompany.api.student.repository.StudentRepository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/students")
@Validated
@EnableMethodSecurity
public class StudentApiController {
	
	@Autowired
	StudentRepository repo;

	@GetMapping
	public ResponseEntity<?> list(@RequestParam("pageSize") 
			@Min(value = 10, message = "Page Size minimum is 10") 
				@Max(value = 50, message = "Page Size maximum is 50") Integer pageSize, 
					@RequestParam("pageNum")
					@Positive(message = "Page Number must be greater than 0") Integer pageNum){
		
		System.out.println("Page size = " + pageSize);
		System.out.println("Page num = " + pageNum);
		
		List<Student> listStudents = repo.findAll();
		
		if(listStudents.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return new ResponseEntity<>(listStudents, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Student> add(@RequestBody Student student){
		repo.save(student);
		
		return new ResponseEntity<>(student, HttpStatus.CREATED);
	}
	
	@PutMapping
	public ResponseEntity<?> replace(@RequestBody Student student){
		if(repo.existsById(student.getId())) {
			repo.save(student);
			
			return new ResponseEntity<>(student, HttpStatus.OK);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('write')")
	public ResponseEntity<?> delete(@PathVariable(name = "id") 
				@Positive(message = "Student ID must be greater than zero") Integer id){
		Student student = new Student(id);
		
		if(repo.existsById(student.getId())) {
			repo.deleteById(student.getId());
			
			return ResponseEntity.noContent().build();
		}else {
			return ResponseEntity.notFound().build();
		}
		
	}
}






















