package models;

import javax.persistence.Entity;
import javax.persistence.Table;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Table(name = "temp_file")
@Entity
public class TempFile extends Model {

	public Blob tempFile;

	
	public String toString() {
		return tempFile.getFile().getName();
	}
}