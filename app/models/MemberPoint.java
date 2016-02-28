package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Table(name = "memberpoint")
@Entity
public class MemberPoint extends Model {
	@OneToOne
	public Member member;
	public String match;
	public Integer point;
	
	public String toString() {
		return point+"";
	}

}