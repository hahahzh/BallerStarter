package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Table(name = "results")
@Entity
public class Result extends Model {
	
	public Game game;
	public Integer round;
	public String home_team;
	public String visiting_team;
	public Integer home_team_point;
	public Integer visiting_team_point;
	public Integer home_team_integral;
	public Integer visiting_team_integral;
	public Date date;
	public String toString() {
		return home_team + " VS " + visiting_team;
	}

}