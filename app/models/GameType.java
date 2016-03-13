package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "game_type")
@Entity
public class GameType extends Model {

	@Required
	@MaxSize(4)
	public String name;

	
	public String toString() {
		return name;
	}

}