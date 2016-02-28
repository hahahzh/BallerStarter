package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "constellations")
@Entity
public class Constellation extends Model {

	@Required
	@MaxSize(20)
	public String name;

	public String toString() {
		return name;
	}

}