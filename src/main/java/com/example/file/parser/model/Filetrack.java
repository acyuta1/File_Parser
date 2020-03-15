package com.example.file.parser.model;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.file.parser.utilities.FileTrackStatusEnum;

//import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import javax.persistence.GeneratedValue
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="Filetrack")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Filetrack {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String filename ;
	private int checkpointLine;
	private int totalLinesPresent;
	private float percentComplete;
	@Enumerated
	private FileTrackStatusEnum status; 
	
	public Filetrack() {}
	
	public Filetrack(String filename, int checkpointLine, int totalLinesPresent, int percentInt, FileTrackStatusEnum status) {
		// TODO Auto-generated constructor stub
		this.setFilename(filename);
		this.setCheckpointLine(checkpointLine);
		this.setTotalLinesPresent(totalLinesPresent);
		this.setPercentComplete(percentInt);
		this.setStatus(status);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getCheckpointLine() {
		return checkpointLine;
	}

	public void setCheckpointLine(int checkpointLine) {
		this.checkpointLine = checkpointLine;
	}

	public FileTrackStatusEnum getStatus() {
		return status;
	}

	public void setStatus(FileTrackStatusEnum status) {
		this.status = status;
	}

	public float getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(float percentComplete) {
		this.percentComplete = percentComplete;
	}

	public int getTotalLinesPresent() {
		return totalLinesPresent;
	}

	public void setTotalLinesPresent(int totalLinesPresent) {
		this.totalLinesPresent = totalLinesPresent;
	}
}
