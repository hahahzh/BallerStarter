package controllers;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.mail.internet.InternetAddress;

import models.AdminManagement;
import models.Blood;
import models.CheckDigit;
import models.Constellation;
import models.Job;
import models.Member;
import models.MemberPoint;
import models.Session;
import models.Team;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import play.Play;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.DB;
import play.db.Model;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;
import utils.Coder;
import utils.JSONUtil;
import utils.SendMail;
import utils.StringUtil;
import controllers.CRUD.ObjectType;

/**
 * Baller Starter主接口
 * 
 * @author hanzhao
 * 
 */
public class Master extends Controller {
	
	public static final SimpleDateFormat SDF_TO_S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDF_TO_DAY = new SimpleDateFormat("yyyy-MM-dd");

	// 定义返回Code
	public static final String SUCCESS = "1";//成功
	public static final String FAIL = "0"; // 失败
	
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	
	public static final int error_parameter_required = 1;// 缺少必须参数
	public static final int error_username_already_used = 2;// 已存在的用户名
	public static final int error_username_not_exist = 3;// 不存在用户名
	public static final int error_userid_not_exist = 4;// 用户ID不存在
	public static final int error_not_owner = 5;// 不是定位器的拥有者
	public static final int error_unknown = 6;// 未知错误
	public static final int error_rwatch_not_exist = 7;// 定位器不存在
	public static final int error_both_email_phonenumber_empty = 8;// 电话号码或Email为空
	public static final int error_username_or_password_not_match = 9;// 用户名或密码不匹配
	public static final int error_session_expired = 10;// 会话过期
	public static final int error_mail_resetpassword = 11;// 密码重置错误
	public static final int error_rwatch_bind_full = 12;// 不能绑定过多定位器
	public static final int error_rwatch_already_bind = 13;// 定位器已被绑定
	public static final int error_unknown_waring_format = 14;// 未知警报
	public static final int error_unknown_command = 15;// 未知命令
	public static final int error_rwatch_not_confirmed = 16;// 定位器未确认
	public static final int error_dateformat = 17;// 日期格式错误
	public static final int error_rwatch_max = 18;// 拥有定位器过多
	public static final int error_download = 19;// 下载错误
	public static final int error_send_mail_fail = 20;// 发送Email错误
	public static final int error_already_exists = 21;// 已存在
	public static final int error_parameter_formate = 22;// 格式错误

	// 存储Session副本
	public static ThreadLocal<Session> sessionCache = new ThreadLocal<Session>();
	
	/**
	 * 校验Session是否过期
	 * 
	 * @param sessionID
	 */
	@Before(unless={"checkDigit","register", "login", "sendResetPasswordMail", "download", 
			"getRWatchInfo", "syncTime", "receiver_new","receiverPhysiological"},priority=1)
	public static void validateSessionID(@Required String z) {
		
		Session s = Session.find("bySessionID",z).first();
		sessionCache.set(s);
		if (s == null) {
			renderFail("error_session_expired");
		}
	}
	
	/**
	 * 更新用户信息
	 * 
	 */
	public static void updateMemberInfo(String pwd, String name, String nickname, String birthday, 
			String gender, String nationality, String region, String height, String weight, Integer number,
			String team, Integer job1, Integer job2, String Specialty, Blob img_ch, Blob identification, String qq,
			String email, String phone, String weixin, Integer constellation, Integer blood, @Required String z) {

		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		Session s = sessionCache.get();
		Member m = s.member;
		
		if(!StringUtil.isEmpty(pwd)){
			m.pwd = pwd;
		}
		if(!StringUtil.isEmpty(name)){
			m.name = name;
		}
		if(!StringUtil.isEmpty(nickname)){
			m.nickname = nickname;
		}
		if(!StringUtil.isEmpty(birthday)){
			m.birthday = new Date(birthday);
		}
		if(!StringUtil.isEmpty(gender)){
			m.gender = gender;
		}
		if(!StringUtil.isEmpty(nationality)){
			m.nationality = nationality;
		}
		if(!StringUtil.isEmpty(region)){
			m.region = region;
		}
		if(!StringUtil.isEmpty(height)){
			m.height = height;
		}
		if(!StringUtil.isEmpty(weight)){
			m.weight = weight;
		}
		if(number != null){
			m.number = number;
		}
		if(!StringUtil.isEmpty(team)){
			m.team = team;
		}
		if(job1 != null){
			m.job1 = Job.findById(job1);
		}
		if(job2 != null){
			m.job2 = Job.findById(job2);
		}
		if(!StringUtil.isEmpty(Specialty)){
			m.Specialty = Specialty;
		}
		if(img_ch != null){
			if(m.img_ch.exists()){
				m.img_ch.getFile().delete();
			}
			m.img_ch = img_ch;
		}
		if(identification != null){
			if(m.identification.exists()){
				m.identification.getFile().delete();
			}
			m.identification = identification;
		}
		if(!StringUtil.isEmpty(qq)){
			m.qq = qq;
		}
		if(!StringUtil.isEmpty(email)){
			m.email = email;
		}
		if(!StringUtil.isEmpty(phone)){
			m.phone = phone;
		}
		if(!StringUtil.isEmpty(weixin)){
			m.weixin = weixin;
		}
		if(constellation != null){
			m.constellation = Constellation.findById(constellation);
		}
		if(blood != null){
			m.blood = Blood.findById(blood);
		}

		m.save();
		renderSuccess(initResultJSON());
	}

	/**
	 * 获取用户信息
	 * 
	 * @param z
	 */
	public static void getMemberInfo(@Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
		
		Member m = s.member;
		JSONObject results = initResultJSON();
	
		results.put("name", m.name);
		results.put("nickname", m.nickname);
		results.put("birthday", m.birthday);
		results.put("gender", m.gender);
		results.put("nationality", m.nationality);
		results.put("region", m.region);
		results.put("height", m.height);
		results.put("weight", m.weight);
		results.put("number", m.number);
		results.put("team", m.team);
		results.put("job1", m.job1);
		results.put("job2", m.job2);
		results.put("Specialty", m.Specialty);
		if(m.img_ch != null && m.img_ch.exists()){
			results.put("img_ch", "/c/download?id=" + m.id + "&fileID=img_ch&entity=" + m.getClass().getName() + "&z=" + z);
		}else{
			if(m.gender == null){
				results.put("img_ch", "/public/images/boy.jpg");
			}else{
				results.put("img_ch", "/public/images/girl.jpg");
			}
		}
		results.put("qq", m.qq);
		results.put("email", m.email);
		results.put("phone", m.phone);
		results.put("weixin", m.weixin);
		results.put("constellation", m.constellation);
		results.put("blood", m.blood);
		results.put("updated_at_ch", m.updated_at_ch);

		renderSuccess(results);
	}

	/**
	 * 获取用户积分
	 * 
	 * @param z
	 */
	public static void getMemberPoint(@Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
		
		Member m = s.member;
		List<MemberPoint> mps = MemberPoint.find("byMember", m).fetch();
		JSONObject results = initResultJSON();
		JSONObject data = initResultJSON();
		if (!mps.isEmpty()) {
			JSONArray datalist = initResultJSONArray();
			for(MemberPoint mp:mps){
				data.put("match", mp.match);
				data.put("score", mp.score);
				data.put("point", mp.point);
				datalist.add(data);
			}
			results.put("list", datalist);
		}
		renderSuccess(results);
	}
	
	/**
	 * 更新球队信息
	 * 
	 */
	public static void updateTeamInfo(Blob logo, String name, Blob coach_img, String coach, 
			Blob captain_img, String captain, String contact, String members, @Required String z) {

		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		Session s = sessionCache.get();
		
		Team t = Team.find("byCoach", s.member.name).first();
		if(t == null)Team.find("byCaptain", s.member.name).first();
		if(t != null){
			if(logo != null){
				if(t.logo.exists()){
					t.logo.getFile().delete();
				}
				t.logo = logo;
			}
			if(!StringUtil.isEmpty(name)){
				t.name = name;
			}
			if(coach_img != null){
				if(t.coach_img.exists()){
					t.coach_img.getFile().delete();
				}
				t.coach_img = coach_img;
			}
			if(!StringUtil.isEmpty(coach)){
				t.coach = coach;
			}
			if(captain_img != null){
				if(t.captain_img.exists()){
					t.captain_img.getFile().delete();
				}
				t.captain_img = coach_img;
			}
			if(!StringUtil.isEmpty(captain)){
				t.captain = captain;
			}
			if(!StringUtil.isEmpty(contact)){
				t.contact = contact;
			}
			if(!StringUtil.isEmpty(members)){
				String[] ma = members.split(",");
				List<Member> ls = new ArrayList<Member>();
				for(String ms:ma){
					Member m = Member.findById(ms);
					if(m != null)ls.add(m);
				}
				t.members = ls;
			}
			
			t.save();
		}
		
		renderSuccess(initResultJSON());
	}
	
	/**
	 * 获取球队信息1
	 * 
	 * @param z
	 */
	public static void getPTeamInfo(@Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
				
		Team t = Team.find("byCoach", s.member.name).first();
		if(t == null)Team.find("byCaptain", s.member.name).first();
		if(t == null)Team.find("member_id", z).first();
		
		JSONObject results = initResultJSON();
	
		if(t.logo != null && t.logo.exists()){
			results.put("logo", "/c/download?id=" + t.id + "&fileID=logo&entity=" + t.getClass().getName() + "&z=" + z);
		}
		results.put("name", t.name);
		if(t.logo != null && t.logo.exists()){
			results.put("logo", "/c/download?id=" + t.id + "&fileID=logo&entity=" + t.getClass().getName() + "&z=" + z);
		}
		results.put("nickname", t.nickname);
		results.put("birthday", t.birthday);
		results.put("gender", t.gender);
		results.put("nationality", t.nationality);
		results.put("region", t.region);
		results.put("height", t.height);
		results.put("weight", t.weight);
		results.put("number", t.number);
		results.put("team", t.team);
		results.put("job1", t.job1);
		results.put("job2", t.job2);
		results.put("Specialty", t.Specialty);
		if(t.img_ch != null && t.img_ch.exists()){
			results.put("img_ch", "/c/download?id=" + t.id + "&fileID=img_ch&entity=" + t.getClass().getName() + "&z=" + z);
		}else{
			if(t.gender == null){
				results.put("img_ch", "/public/images/boy.jpg");
			}else{
				results.put("img_ch", "/public/images/girl.jpg");
			}
		}
		results.put("qq", t.qq);
		results.put("email", t.email);
		results.put("phone", t.phone);
		results.put("weixin", t.weixin);
		results.put("constellation", t.constellation);
		results.put("blood", t.blood);
		results.put("updated_at_ch", m.updated_at_ch);

		renderSuccess(results);
	}
	
	/**
	 * 头像
	 * 
	 * @param sn
	 * @param rId
	 * @param z
	 */
	public static void getMemberPortrait(@Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Member m = null;
		JSONObject results = initResultJSON();
		if(m.img_ch != null && m.img_ch.exists()){
			if(StringUtil.isEmpty(z))z = ((Session)Session.find("byC", m.img_ch).first()).sessionID;
			results.put("portrait", "/c/download?id=" + m.id + "&fileID=portrait&entity=" + m.getClass().getName() + "&z=" + z);
		}else{
			results.put("portrait", "/public/images/boy.jpg");
		}
		renderSuccess(results);
	}
	
	
	

	
	


	
	
	public static void insertSN(@Required String username, @Required String pwd, @Required String from,@Required String to) {
		if (Validation.hasErrors()) {
			renderText("error");
		}
		
		AdminManagement user = AdminManagement.find("byName", username).first();
		if(user == null)renderText("error username");
		if(!user.pwd.equals(pwd))renderText("error password");
		
		Integer snf = Integer.parseInt(from.substring(from.length()-5,from.length()));
		Integer snt = Integer.parseInt(to.substring(to.length()-5,to.length()));
		String str = from.substring(0,from.length()-5);
		
		Connection con = DB.getConnection();
		int count=0;
		try {
			con.setAutoCommit(false);
			con.setTransactionIsolation(con.TRANSACTION_READ_UNCOMMITTED);
			PreparedStatement pst = (PreparedStatement) con.prepareStatement("insert into serial_number(sn) values (?)");
			String tmp = "";
			for(long i=snf;i<=snt;i++){
				if(i<10)tmp="0000"+i;
				else if(i<100)tmp="000"+i;
				else if(i<1000)tmp="00"+i;
				else if(i<10000)tmp="0"+i;
				else if(i<100000)tmp=""+i;
				pst.setString(1, str+tmp);
				pst.addBatch();
				count++;
			}
			pst.executeBatch();
			con.commit();
			renderText(count);
		}catch(Exception e){
			play.Logger.error("insertSN:"+e.getMessage());
			renderText("error2");
		}finally {
			DB.close();
		}
	}
	
	/**
	 * 发送验证码到手机
	 * 
	 * @param m_number
	 */
	public static void checkDigit(@Required String m_number) {
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		if(!Validation.phone(SUCCESS, m_number).ok){
			renderFail("error_parameter_required");
		}
		Random r = new Random();
		int n = Math.abs(r.nextInt())/10000;
		
		try {
			String s = "1110";// SDKTestSendTemplateSMS.sendMsg(m_number, play.i18n.Messages.get("verification_msg",n));
			if(!s.startsWith("result=0")){
				play.Logger.error("checkDigit: result="+s+" PNumber="+m_number+" digit="+n);
				renderText(play.i18n.Messages.get("error_verification_code"));
			}
			
			CheckDigit cd = CheckDigit.find("m=?", m_number).first();
			if(cd == null)cd = new CheckDigit();
			cd.d = n;
			cd.updatetime = new Date().getTime();
			cd.m = m_number;
			cd._save();
		} catch (Exception e) {
			play.Logger.error("checkDigit: PNumber="+m_number+" digit="+n);
			play.Logger.error(e.getMessage());
			renderText(play.i18n.Messages.get("error_verification_code_sys"));
		}
		renderText("OK");
	}
	
	
	/**
	 * 用户注册
	 * 
	 * @param z
	 */
	public static void register(@Required String z) {
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		try {			
			byte[] b = Coder.decryptBASE64(z);
			String src = new String(b);
			String[] arr = src.split("\\|");
		
			int i = Integer.parseInt(arr[7]);
			CheckDigit c = CheckDigit.find("d=?", i).first();
			if(c == null){
				renderFail("error_checkdigit");
			}
			if(!c.m.equals(arr[6])){
				renderFail("error_checkdigit");
			}
			if(new Date().getTime() - c.updatetime > 1800000){
				c.delete();
				renderFail("error_checkdigit");
			}

			Member m = Member.find("byM_number", arr[6].trim()).first();
			if(m != null){
				play.Logger.info("register:error_username_already_used");
				renderFail("error_username_already_used");
			}
//			if(arr[9].contains("@")){
//				Customer m2 = Customer.find("byEmail", arr[9].trim()).first();
//				if(m2 != null){
//					play.Logger.info("register:error_email_already_used");
//					renderFail("error_email_already_used");
//				}
//			}
			
			m = new Member();
			m.phone = arr[0];
			m.pwd = arr[1];
			m.updated_at_ch = new Date();
			m.save();			
			c.delete();
			
			Session s = new Session();
			s.member = m;
			s.date = new Date();
			s.sessionID = UUID.randomUUID().toString();
			s.save();
			
			JSONObject results = initResultJSON();
			results.put("uid", m.getId());
			results.put("phone", m.phone);
			results.put("session", s.sessionID);
			play.Logger.info("register:OK "+m.phone);
			renderSuccess(results);
		} catch (Exception e) {
			play.Logger.info("register:error "+e.getMessage());
			renderFail("error_unknown");
		}
		
	}
		
	/**
	 * 登录
	 * 
	 * @param phone
	 * @param pwd
	 * @param os
	 * @param serialNumber
	 * @param ip
	 * @param imei
	 * @param mac
	 * @param imsi
	 */
	public static void login(@Required String name, @Required String pwd) {
		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		Member m = Member.find("byM_number", name).first();
		
		if(m == null){
			m = Member.find("byEmail", name).first();
			if(m == null)m = Member.find("byOpenID", name).first();
		}
		
		if(m == null || !m.pwd.equals(pwd)){
			renderFail("error_username_or_password_not_match");
		}
		
		Session s = Session.find("byC", m).first();
		if(s == null){
			s = new Session();
			s.member = m;
			s.sessionID = UUID.randomUUID().toString();
		}
		s.date = new Date();
		s._save();
		
		m.updated_at_ch = new Date();
		m._save();
		sessionCache.set(s);
		
		JSONObject results = initResultJSON();
		results.put("uid", m.getId());
		results.put("phone", m.phone);
		results.put("name", m.nickname);
		results.put("session", s.sessionID);
		play.Logger.info("login:OK "+m.phone);
				
		renderSuccess(results);
	}
	
	
	
	/**
	 * 登出
	 * 
	 * @param z
	 */
	public static void logout(@Required String z) {
		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		Session s = sessionCache.get();
		if(s != null && s.id != 1 && s.id != 2){
			s.delete();
//			s.sessionID = "";
//			s.save();
		}
		renderSuccess(initResultJSON());
	}

	/**
	 * 重置密码
	 * 
	 * @param m
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("deprecation")
	public static void sendResetPasswordMail(@Required String p)
			throws UnsupportedEncodingException {

            if (Validation.hasErrors()) {
                    renderFail("error_parameter_required");
            }

            Member m = Member.find("byM_number", p).first();
            if (m == null) {
                m = Member.find("byWeixin", m).first();
                if(m == null){
                	List<Member> tmp = Member.find("byEmail", m).fetch();
                	if(tmp.size() == 0)renderFail("error_username_not_exist");
                	if(StringUtil.isEmpty(tmp.get(0).email))renderFail("error_email_empty");
                	SendMail mail = new SendMail(
                            Play.configuration.getProperty("mail.smtp.host"),
                            Play.configuration.getProperty("mail.smtp.user"),
                            Play.configuration.getProperty("mail.smtp.pass"));

                	mail.setSubject(Messages.get("mail_resetpassword_title"));
                	String text = "";
                	for(Member tmpC : tmp){
                		text += "phone:"+tmpC.phone+"password:"+tmpC.pwd+"\n";
                	}
                	mail.setBodyAsText(text);

	            String nick = Messages.get("mail_show_name");
	            try {
	                    nick = javax.mail.internet.MimeUtility.encodeText(nick);
	                    mail.setFrom(new InternetAddress(nick + " <"
	                                    + Play.configuration.getProperty("mail.smtp.from") + ">")
	                                    .toString());
	                    mail.setTo(tmp.get(0).email);
	                    mail.send();
	            } catch (Exception e) {
	                    renderFail("error_mail_resetpassword");
	            }
	            renderSuccess(initResultJSON());
                	
                }
            }

            if(StringUtil.isEmpty(m.email))renderFail("error_email_empty");
            SendMail mail = new SendMail(
                            Play.configuration.getProperty("mail.smtp.host"),
                            Play.configuration.getProperty("mail.smtp.user"),
                            Play.configuration.getProperty("mail.smtp.pass"));

            mail.setSubject(Messages.get("mail_resetpassword_title"));
            mail.setBodyAsText("phone:"+m.phone+"password:"+m.pwd);

            String nick = Messages.get("mail_show_name");
            try {
                    nick = javax.mail.internet.MimeUtility.encodeText(nick);
                    mail.setFrom(new InternetAddress(nick + " <"
                                    + Play.configuration.getProperty("mail.smtp.from") + ">")
                                    .toString());
                    mail.setTo(m.email);
                    mail.send();
            } catch (Exception e) {
                    renderFail("error_mail_resetpassword");
            }
            renderSuccess(initResultJSON());
    }
	
	/**
	 * 手表同步时间(定位器)
	 * 
	 * @param sn
	 */
	public static void syncTime(String sn) {		
		renderText(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
	}
	
	/**
	 * 重置密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param z
	 */
	public static void changePassword(@Required String oldPassword, @Required String newPassword, @Required String z) {
		JSONObject results = initResultJSON();
		// ....
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		Session s = Session.find("bySessionID",z).first();
		if(s.member.pwd.equals(oldPassword) && !StringUtil.isEmpty(newPassword)){
			s.member.pwd = newPassword;
			s.member._save();
			renderSuccess(results);
		}else{
			renderFail("error_old_password_not_match");
		}
	}
	
	/**
	 * 下载
	 * 
	 * @param id
	 * @param fileID
	 * @param entity
	 */
	public static void download(@Required String id, @Required String fileID, @Required String entity) {

		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		ObjectType type;
		try {
			type = new ObjectType(entity);
			notFoundIfNull(type);

			Model object = type.findById(id);
			notFoundIfNull(object);
			Object att = object.getClass().getField(fileID).get(object);
			if (att instanceof Model.BinaryField) {
				Model.BinaryField attachment = (Model.BinaryField) att;
				if (attachment == null || !attachment.exists()) {
					renderFail("error_download");
				}
				long p = 0;
				Header h = request.headers.get("Range");
				play.Logger.info("download header:", h);
				if(h != null){
					p = Long.parseLong(h.value().replaceAll("bytes=", "").replaceAll("-", ""));
				}
				play.Logger.info("download header:", p);
				response.contentType = attachment.type();
				if(p > 0){
					renderBinary(attachment.get(), attachment.get().skip(p));
				}else{
					renderBinary(attachment.get(), attachment.length());
				}
				
			}
		} catch (Exception e) {
			renderText("Download failed");
		}
		renderFail("error_download");
	}

	protected static JSONObject initResultJSON() {
		return JSONUtil.getNewJSON();
	}
	
	protected static JSONArray initResultJSONArray() {
		return JSONUtil.getNewJSONArray();
	}

	protected static void renderSuccess(JSONObject results) {
		JSONObject jsonDoc = new JSONObject();
		jsonDoc.put("state", SUCCESS);
		jsonDoc.put("results",results);
		renderJSON(jsonDoc.toString());
	}

	protected static void renderFail(String key, Object... objects) {
		JSONObject jsonDoc = new JSONObject();
		jsonDoc.put("state", FAIL);
		jsonDoc.put("msg", Messages.get(key));
		renderJSON(jsonDoc.toString());
	}

}
