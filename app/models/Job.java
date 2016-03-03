package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "jobs")
@Entity
public class Job extends Model {

	@Required
	@MaxSize(4)
	public String full_name;

	@Required
	@MaxSize(4)
	public String abb_name;
	
	public String toString() {
		return abb_name+"-"+full_name;
	}

}