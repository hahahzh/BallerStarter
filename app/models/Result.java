package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;

@Table(name = "results")
@Entity
public class Result extends Model {
	
	@Required
	@ManyToOne(fetch=FetchType.EAGER,cascade=javax.persistence.CascadeType.REFRESH)
	public Game game;
	public Integer round;
	@ManyToOne(fetch=FetchType.EAGER,cascade=javax.persistence.CascadeType.REFRESH)
	@Required
	public Team home_team;
	@Required
	@ManyToOne(fetch=FetchType.EAGER,cascade=javax.persistence.CascadeType.REFRESH)
	public Team visiting_team;
	@Required
	public Integer home_team_point;
	@Required
	public Integer visiting_team_point;
	public Integer home_team_integral;
	public Integer visiting_team_integral;
	@Required
	@ManyToOne(fetch=FetchType.EAGER,cascade=javax.persistence.CascadeType.REFRESH)
	public GameType gameType;
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;
	
	public String toString() {
		return home_team + " VS " + visiting_team;
	}
}