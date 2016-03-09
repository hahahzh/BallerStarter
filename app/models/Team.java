package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Table(name = "teams")
@Entity
public class Team extends Model {
	
	
	public Blob logo;
	@Required
	@MaxSize(20)
	@Unique
	public String name;
	public Blob coach_img;
	@OneToOne(optional = false, cascade = { CascadeType.REFRESH},fetch=FetchType.EAGER)
	public Member coach;
	public Blob captain_img;
	@OneToOne(optional = false, cascade = { CascadeType.REFRESH},fetch=FetchType.EAGER)
	public Member captain;
	@MaxSize(50)
	public String contact;
	@OneToMany(fetch=FetchType.LAZY)
    public List<Member> members;
	@Hidden
	@Temporal(TemporalType.TIMESTAMP)
	public Date updated_at_ch;
	public String toString() {
		return name;
	}

}