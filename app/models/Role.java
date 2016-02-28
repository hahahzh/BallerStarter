package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "roles")
@Entity
public class Role extends Model {

	@Required
	@MaxSize(10)
	@MinSize(2)
	public String name;

	public String toString() {
		return name;
	}

}