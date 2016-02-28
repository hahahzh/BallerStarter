package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import controllers.CRUD.Hidden;
import play.db.jpa.Model;

@Table(name = "consultations")
@Entity
public class Consultation extends Model {
	
	public String name;
	@Hidden
	public Date release_date;
	
	public String toString() {
		return name+"";
	}

}