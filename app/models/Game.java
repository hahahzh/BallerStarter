package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Table(name = "games")
@Entity
public class Game extends Model {
	
	public Blob logo;
	@Required
	@MaxSize(20)
	public String name;
	@MaxSize(2000)
	public String describtion;
	public String schedule;
	public Date startDate;
	public Date endDate;
	public Date startSignUp;
	public Date endSignUp;
	@MaxSize(1000)
	public String prize;
	@OneToMany(fetch=FetchType.LAZY)
	public List<Team> teams;
	public String toString() {
		return name;
	}

}