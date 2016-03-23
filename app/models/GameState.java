package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "game_state")
@Entity
public class GameState extends Model {

	@MaxSize(6)
	public String name;
	@MaxSize(6)
	public String state;

	
	public String toString() {
		return name;
	}

}