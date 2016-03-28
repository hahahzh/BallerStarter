package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
//	public String schedule;
	public Blob schedule;
	public Date startDate;
	public Date endDate;
	public Date startSignUp;
	public Date endSignUp;
	public Integer tCount;
	public Boolean isShow;
	public String organizers;
	public String sponsor;
	public String place;
	@Required
	@ManyToOne(fetch=FetchType.EAGER,cascade=javax.persistence.CascadeType.REFRESH)
	public GameState state;
	@MaxSize(1000)
	public String prize;
	public String toString() {
		return name;
	}

}