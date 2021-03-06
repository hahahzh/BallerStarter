package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "bloods")
@Entity
public class Blood extends Model {

	@Required
	@MaxSize(6)
	public String name;

	
	public String toString() {
		return name;
	}

}