package com.example.demo.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.File_Tracking;;

public interface FileTrackingRepository extends JpaRepository<File_Tracking, Integer>{
	
	File_Tracking findByFilename(String filename);
	File_Tracking findById(int id);
//    File_Tracking findOneByDayRequestedAndDateRequested(LocalDateTime day, LocalDateTime localDateTime);
}
