package com.example.File_Parser.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.File_Parser.model.FileTracking;

public interface FileTrackingRepository extends JpaRepository<FileTracking, Integer>{
	
	FileTracking findByFilename(String filename);
	FileTracking findById(int id);
//    File_Tracking findOneByDayRequestedAndDateRequested(LocalDateTime day, LocalDateTime localDateTime);
}
