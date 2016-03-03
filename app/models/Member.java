package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import controllers.CRUD.Hidden;

@Table(name = "members")
@Entity
public class Member extends Model {

	@Hidden
	public Date updated_at_ch;
	@Hidden
	public String openID;
	public String pwd;
	@Required
	@MaxSize(20)
	public String name;
	@MaxSize(20)
	public String nickname; // 常用名
	public Date birthday; // 常用名
	public String gender;
	@MaxSize(30)
	public String nationality;
	@MaxSize(20)
	public String region;
	@MaxSize(10)
	public String height;
	@MaxSize(10)
	public String weight;
	public Integer number;
	public String team;
	@MaxSize(10)
	public Job job1;
	@MaxSize(10)
	public Job job2;
	@MaxSize(1000)
	public String Specialty;
	public Blob img_ch;
	public Blob identification;
	public byte isAuth;
	@MaxSize(20)
	public String qq;
	@MaxSize(20)
	public String email;
	@MaxSize(20)
	public String phone;
	@MaxSize(20)
	public String weixin;
	public Constellation constellation;
	public Blood blood;
	
	public String toString() {
		return name;
	}

}