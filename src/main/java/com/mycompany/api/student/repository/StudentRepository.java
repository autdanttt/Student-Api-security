package com.mycompany.api.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer>{
	
}
