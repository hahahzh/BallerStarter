package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Table(name = "teams")
@Entity
public class Team extends Model {
	
	
	public Blob logo;
	@Required
	@MaxSize(20)
	public String name;
	public Blob coach_img;
	public String coach;
	public Blob captain_img;
	public String captain;
	@MaxSize(50)
	public String contact;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
    public List<Member> members;	
	public Date updated_at_ch;
	public String toString() {
		return name;
	}

}