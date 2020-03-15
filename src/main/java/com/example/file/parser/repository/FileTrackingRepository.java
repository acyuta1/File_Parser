package com.example.file.parser.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.file.parser.model.Filetrack;

public interface FileTrackingRepository extends JpaRepository<Filetrack, Integer>{
	
	Filetrack findByFilename(String filename);
	Filetrack findById(int id);
//    File_Tracking findOneByDayRequestedAndDateRequested(LocalDateTime day, LocalDateTime localDateTime);
}
