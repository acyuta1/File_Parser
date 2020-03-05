package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
//import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import javax.persistence.GeneratedValue
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="File_Tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class File_Tracking {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String filename ;
	private int checkpointLine;
	private String status;
	
	public File_Tracking() {}
	
	public File_Tracking(String filename, int checkpointLine, String status) {
		// TODO Auto-generated constructor stub
		this.setFilename(filename);
		this.setCheckpointLine(checkpointLine);
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
