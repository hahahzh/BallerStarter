package controllers;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.InternetAddress;

import controllers.CRUD.ObjectType;
import models.AdminManagement;
import models.Blood;
import models.CheckDigit;
import models.Constellation;
import models.Game;
import models.GameApproval;
import models.Job;
import models.Member;
import models.MemberPoint;
import models.Result;
import models.Session;
import models.Team;
import models.TempFile;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import play.Play;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.DB;
import play.db.Model;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Header;
import utils.DateUtil;
import utils.JSONUtil;
import utils.SendMail;
import utils.SendSMSMy;
import utils.StringUtil;

/**
 * Baller Starter主接口
 * 
 * @author hanzhao
 * 
 */
public class Master extends Controller {
	
	public static final SimpleDateFormat SDF_TO_S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDF_TO_DAY = new SimpleDateFormat("yyyy-MM-dd");
	public static final String TLOGO = "/public/images/tlogo.jpg";

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
	@Before(unless={"checkDigit","register", "login", "sendResetPasswordMail", "sendResetPasswordSMS",
			"download",	"getRWatchInfo", "syncTime", "receiver_new","receiverPhysiological", "getGameInfo"},priority=1)
	public static void validateSessionID(@Required String z) {
		
		Session s = Session.find("bySessionID",z).first();
		sessionCache.set(s);
		if (s == null) {
			renderFail("session_expired");
		}
	}
	
	public static void getBlood(){
		List<Blood> ls = Blood.findAll();
		JSONObject results = initResultJSON();
		JSONArray datalist = initResultJSONArray();
		for(Blood b:ls){
			JSONObject data = initResultJSON();
			data.put(b.id, b.name);
			datalist.add(data);
		}
		results.put("datalist", datalist);
		renderSuccess(results);
	}

	public static void getConstellation(){
		List<Constellation> ls = Constellation.findAll();
		JSONObject results = initResultJSON();
		JSONArray datalist = initResultJSONArray();
		for(Constellation c:ls){
			JSONObject data = initResultJSON();
			data.put(c.id, c.name);
			datalist.add(data);
		}
		results.put("datalist", datalist);
		renderSuccess(results);
	}

	public static void getJob(){
		List<Job> ls = Job.findAll();
		JSONObject results = initResultJSON();
		JSONArray datalist = initResultJSONArray();
		for(Job c:ls){
			JSONObject data = initResultJSON();
			data.put(c.id, c.full_name);
			datalist.add(data);
		}
		results.put("datalist", datalist);
		renderSuccess(results);
	}
	
	public static void getManageData(@Required String z){
		List<Blood> lbs = Blood.findAll();
		JSONObject results = initResultJSON();
		
		JSONArray bloodlist = initResultJSONArray();
		for(Blood b:lbs){
			JSONObject data = initResultJSON();
			data.put(b.id, b.name);
			bloodlist.add(data);
		}
		results.put("bloodlist", bloodlist);
		List<Constellation> lcs = Constellation.findAll();
		JSONArray constellationlist = initResultJSONArray();
		for(Constellation c:lcs){
			JSONObject data = initResultJSON();
			data.put(c.id, c.name);
			constellationlist.add(data);
		}
		results.put("constellationlist", constellationlist);
		List<Job> ljs = Job.findAll();
		JSONArray joblist = initResultJSONArray();
		for(Job c:ljs){
			JSONObject data = initResultJSON();
			data.put(c.id, c.full_name);
			joblist.add(data);
		}
		results.put("joblist", joblist);
		renderSuccess(results);
	}
	
	/**
	 * 更新用户信息
	 * 
	 */
	public static void updateMemberInfo(String pwd, String name, String nickname, String birthday, 
			String gender, String nationality, String region, String height, String weight, Integer number,
			String team, Long job1, Long job2, String specialty, Blob img_ch, Blob identification, String qq,
			String email, String phone, String weixin, Long constellation, Long blood, @Required String z) {

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
			m.birthday = DateUtil.reverse2Date(birthday);
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
			m.team = Team.find("byName", team).first();
		}
		if(job1 != null){
			m.job1 = Job.findById(job1);
		}
		if(job2 != null){
			m.job2 = Job.findById(job2);
		}
		if(!StringUtil.isEmpty(specialty)){
			m.specialty = specialty;
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

	public static void getMemberImg(@Required String z) {
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
		Member m = s.member;
		JSONObject results = initResultJSON();
	
		if(m.img_ch != null && m.img_ch.exists()){
			results.put("img_ch", "/c/download?id=" + m.id + "&fileID=img_ch&entity=" + m.getClass().getName() + "&z=" + z);
		}else{
			if(m.gender == null){
				results.put("img_ch", "/public/images/boy.jpg");
			}else{
				results.put("img_ch", "/public/images/girl.jpg");
			}
		}

		renderSuccess(results);
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
		results.put("birthday", SDF_TO_DAY.format(m.birthday));
		results.put("gender", m.gender);
		results.put("nationality", m.nationality);
		results.put("region", m.region);
		results.put("height", m.height);
		results.put("weight", m.weight);
		results.put("number", m.number);
		results.put("team", m.team);
		results.put("job1", m.job1.full_name);
		results.put("job2", m.job2.full_name);
		results.put("specialty", m.specialty);
		if(m.img_ch != null && m.img_ch.exists()){
			results.put("img_ch", "/c/download?id=" + m.id + "&fileID=img_ch&entity=" + m.getClass().getName() + "&z=" + z);
		}else{
			if(m.gender == null){
				results.put("img_ch", "/public/images/boy.jpg");
			}else{
				results.put("img_ch", "/public/images/girl.jpg");
			}
		}
		results.put("auth", m.isAuth==1?"是":"否");
		results.put("qq", m.qq);
		results.put("email", m.email);
		results.put("phone", m.phone);
		results.put("weixin", m.weixin);
		results.put("constellation", m.constellation==null?"":m.constellation.name);
		results.put("blood", m.blood==null?"":m.blood.name);
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
		if (!mps.isEmpty()) {
			JSONArray datalist = initResultJSONArray();
			JSONObject data = initResultJSON();
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
		
		Team t = Team.find("byCoach", s.member).first();
		if(t == null)Team.find("byCaptain", s.member).first();
		if(t == null){
			t = new Team();
			t.updated_at_ch = new Date();
		}
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
				t.coach = Member.find("byName", coach).first();
			}
			if(captain_img != null){
				if(t.captain_img.exists()){
					t.captain_img.getFile().delete();
				}
				t.captain_img = coach_img;
			}
			if(!StringUtil.isEmpty(captain)){
				t.captain = Member.find("byName", captain).first();
			}
			if(!StringUtil.isEmpty(contact)){
				t.contact = contact;
			}
			members = "12,13,14,15,16,17,";
			if(!StringUtil.isEmpty(members)){
				String[] ma = members.split(",");
				int count = 0;
				for(String ms:ma){
					if(count > 11)break;
					Member m = Member.findById(Long.parseLong(ms));
					if(m != null)t.members.add(m);
					count++;
				}
				t.members = StringUtil.removalDup(t.members);
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
				
		Team t = Team.find("byCoach", s.member).first();
		if(t == null)Team.find("byCaptain", s.member).first();
				
		JSONObject results = initResultJSON();
	
		if(t.logo != null && t.logo.exists()){
			results.put("logo", "/c/download?id=" + t.id + "&fileID=logo&entity=" + t.getClass().getName() + "&z=" + z);
		}else{
			results.put("logo", TLOGO);
		}
		results.put("name", t.name);
		if(t.coach_img != null && t.coach_img.exists()){
			results.put("coach_img", "/c/download?id=" + t.id + "&fileID=coach_img&entity=" + t.getClass().getName() + "&z=" + z);
		}
		results.put("coach", t.coach==null?"":t.coach.name);
		if(t.captain_img != null && t.captain_img.exists()){
			results.put("captain_img", "/c/download?id=" + t.id + "&fileID=captain_img&entity=" + t.getClass().getName() + "&z=" + z);
		}
		results.put("captain", t.captain==null?"":t.captain.name);
		results.put("contact", t.contact);
		JSONArray datalist = initResultJSONArray();
		if(t.members.size() >0){
			JSONObject data = initResultJSON();
			for(Member m:t.members){
				data.put("name", m.name);
				data.put("number", m.number);
				data.put("job1", m.job1==null?"":m.job1.full_name);
				data.put("job2", m.job2==null?"":m.job2.full_name);
				data.put("height", m.height);
				data.put("weight", m.weight);
				if(m.img_ch != null && m.img_ch.exists()){
					data.put("img_ch", "/c/download?id=" + m.id + "&fileID=img_ch&entity=" + m.getClass().getName() + "&z=" + z);
				}else{
					data.put("img_ch", "/public/images/boy.jpg");
				}
				datalist.add(data);
			}
		}
		results.put("members", datalist);
		results.put("updated_at_ch", SDF_TO_DAY.format(t.updated_at_ch));
		renderSuccess(results);
	}
	
	/**
	 * 获取球队信息2
	 * 
	 * @param z
	 */
	public static void getAllTeamList(@Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
				
		List<Team> ts = Team.findAll();
		
		JSONObject results = initResultJSON();
		JSONArray datalist = initResultJSONArray();
		if(ts.isEmpty() && ts.size() > 0){
			JSONObject data = initResultJSON();
			for(Team t:ts){
				data.put("id", t.id);
				if(t.logo != null && t.logo.exists()){
					data.put("logo", "/c/download?id=" + t.id + "&fileID=logo&entity=" + t.getClass().getName() + "&z=" + z);
				}else{
					data.put("logo", TLOGO);
				}
				data.put("name", t.name);
				datalist.add(data);
			}
			results.put("list", datalist);
		}
		renderSuccess(results);
	}
	
	/**
	 * 获取球队积分
	 * 
	 * @param z
	 */
	public static void getTeamPoint(String teamId, @Required String z) {
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
				
		List<Result> ts = Result.find("home_team_id", teamId).first();
		List<Result> ts2 = Result.find("visiting_team_id", teamId).first();
		ts.addAll(ts2);
		
		JSONObject results = initResultJSON();
		JSONArray datalist = initResultJSONArray();
		if(ts.isEmpty() && ts.size() > 0){
			JSONObject data = initResultJSON();
			for(Result t:ts){
				data.put("game", t.game);
				data.put("round", t.round);
				data.put("home_team", t.home_team);
				data.put("visiting_team", t.visiting_team);
				data.put("home_team_point", t.home_team_point);
				data.put("visiting_team_point", t.visiting_team_point);
				data.put("home_team_integral", t.home_team_integral);
				data.put("visiting_team_integral", t.visiting_team_integral);
				data.put("date", t.date);
				datalist.add(data);
			}
			results.put("list", datalist);
		}
		renderSuccess(results);
	}
	
	/**
	 * 获取新闻
	 * 
	 * @param z
	 */
	public static void getNewsList(@Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
				
		List<Constellation> cs = Constellation.findAll();
		
		JSONObject results = initResultJSON();
		JSONArray datalist = initResultJSONArray();
		if(cs.isEmpty() && cs.size() > 0){
			JSONObject data = initResultJSON();
			for(Constellation c:cs){
				data.put("id", c.id);
				data.put("name", c.name);
				datalist.add(data);
			}
			results.put("list", datalist);
		}
		renderSuccess(results);
	}
	
	/**
	 * 获取赛事
	 * 
	 * @param z
	 * @throws ParseException
	 */
	public static void getGameInfo() throws ParseException {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
						
		Game g = Game.find("isShow =1 order by id desc").first();
		
		JSONObject results = initResultJSON();
		
		if(g != null){	
			results.put("id", g.id);
			results.put("name", g.name);
			results.put("logo", "/c/download?id=" + g.id + "&fileID=logo&entity=" + g.getClass().getName() + "&z=" + 1);
			results.put("prize", g.prize);
			results.put("schedule", g.schedule);
			results.put("startDate", g.startDate==null?"":SDF_TO_DAY.format(g.startDate));
			results.put("endDate", g.endDate==null?"":SDF_TO_DAY.format(g.endDate));
			results.put("startSignUp", g.startSignUp==null?"":SDF_TO_DAY.format(g.startSignUp));
			results.put("endSignUp", g.endSignUp==null?"":SDF_TO_DAY.format(g.endSignUp));
			results.put("describtion", g.describtion);
			if(DateUtil.intervalOfHour(new Date(), g.endSignUp) > 0){
				results.put("isSignUp", 1);
			}else{
				results.put("isSignUp", 0);
			}
						
			JSONArray teamlist = initResultJSONArray();
			List<GameApproval> gas = GameApproval.find("games_id=? and isAppr=1", g.id).fetch();
			if(gas != null && gas.size() > 0){
				JSONObject data = initResultJSON();
				for(GameApproval ga:gas){
					data.put("name", ga.teams.name);
					data.put("coach", ga.teams.coach==null?"":ga.teams.coach.name);
					data.put("captain", ga.teams.captain==null?"":ga.teams.captain.name);
					if(ga.teams.logo != null && ga.teams.logo.exists()){
						data.put("logo", "/c/download?id=" + ga.teams.id + "&fileID=logo&entity=" + ga.teams.getClass().getName() + "&z=" + 1);
					}else{
						data.put("logo", TLOGO);
					}
					teamlist.add(data);
				}
			}
			results.put("teamlist", teamlist);
			
//			List<Result> rs = Result.find("byGame", g).fetch();
//			JSONArray resultlist = initResultJSONArray();
//			if(rs.size() > 0){
//				JSONObject data = initResultJSON();
//				for(Result r:rs){
//					data.put("round", r.round);
//					data.put("home_team", r.home_team);
//					data.put("visiting_team", r.visiting_team);
//					data.put("home_team_point", r.home_team_point);
//					data.put("visiting_team_point", r.visiting_team_point);
//					data.put("home_team_integral", r.home_team_integral);
//					data.put("visiting_team_integral", r.visiting_team_integral);
//					data.put("date", r.date);
//					resultlist.add(data);
//				}
//			}
//			results.put("resultlist", resultlist);
		}
		renderSuccess(results);
	}
	
	/**
	 * 赛事报名
	 * 
	 * @param z
	 */
	public static void signUp(@Required Long gId, @Required String z) {
		
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		
		Session s = sessionCache.get();
		if(s == null){
			renderFail("error_session_expired");
		}
				
		Game g = Game.findById(gId);
		Team t = Team.find("coach_id=? or captain_id=?",s.member.id, s.member.id).first();
		GameApproval ga = GameApproval.find("games_id=? and teams_id=?", g.id,t.id).first();
		if(ga == null){
			ga = new GameApproval();
			ga.games = g;
			ga.teams = t;
			ga.isAppr = false;
			ga._save();
		}else{
			renderFail("error_team_su_exist");
		}
		renderSuccess(initResultJSON());
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
	
	public static void saveTempFile(@Required Blob f, @Required String z){
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		TempFile tf = new TempFile();
		tf.tempFile = f;
		tf.save();
		JSONObject results = initResultJSON();
		results.put("tf", "/c/download?id=" + tf.id + "&fileID=tempFile&entity=" + tf.getClass().getName() + "&z=" + z);
		renderSuccess(results);
	}
	
	/**
	 * 发送验证码到手机
	 * 
	 * @param m_number
	 */
	public static void checkDigit(@Required String phone) {
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}
		if(!Validation.phone(SUCCESS, phone).ok || phone.length() != 11){
			renderFail("error_parameter_required");
		}
		String n = String.valueOf(Math.random()).substring(2, 6);
		
		try {
			boolean s = SendSMSMy.sendMsg(phone, n, "5");
			if(!s)renderFail("error_unknown");
			
			CheckDigit cd = CheckDigit.find("m=?", phone).first();
			if(cd == null)cd = new CheckDigit();
			cd.d = n;
			cd.updatetime = new Date().getTime();
			cd.m = phone;
			cd._save();
		} catch (Exception e) {
			play.Logger.error("checkDigit: PNumber="+phone+" digit="+n);
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
	public static void register(@Required String phone, @Required String pwd, @Required String vc) {
		if (Validation.hasErrors()) {
			renderFail("error_parameter_required");
		}

		try {			
			
		
			CheckDigit c = CheckDigit.find("d=?", vc).first();
			if(c == null){
				renderFail("error_checkdigit");
			}
			if(!c.m.equals(phone)){
				renderFail("error_checkdigit");
			}
			if(new Date().getTime() - c.updatetime > 1800000){
				c.delete();
				renderFail("error_checkdigit");
			}

			Member m = Member.find("byPhone", phone.trim()).first();
			if(m != null){
				play.Logger.info("register:error_username_already_used");
				renderFail("error_username_already_used");
			}
			
			m = new Member();
			m.phone = phone;
			m.pwd = pwd;
			m.updated_at_ch = new Date();
			m.save();
			//c.delete();
			
			Session s = new Session();
			s.member = m;
			s.date = new Date();
			s.sessionID = UUID.randomUUID().toString();
			s.save();
			
			JSONObject results = initResultJSON();
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

		Member m = Member.find("byPhone", name).first();
		
		if(m == null){
			m = Member.find("byEmail", name).first();
			if(m == null)m = Member.find("byOpenID", name).first();
			if(m == null)m = Member.find("byWeixin", name).first();
		}
		
		if(m == null || !m.pwd.equals(pwd)){
			renderFail("error_username_or_password_not_match");
		}
		
		Session s = Session.find("member_openid=?", m.id).first();
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

            Member m = Member.find("byPhone", p).first();
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
            mail.setBodyAsText("password:"+m.pwd);

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
	 * 重置密码
	 * 
	 * @param m
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("deprecation")
	public static void sendResetPasswordSMS(@Required String p)
			throws UnsupportedEncodingException {

            if (Validation.hasErrors()) {
                    renderFail("error_parameter_required");
            }

            Member m = Member.find("byPhone", p).first();
            if (m == null) {
                m = Member.find("byWeixin", m).first();
                if(m == null){
                	List<Member> tmp = Member.find("byEmail", m).fetch();
                	if(tmp.size() == 0)renderFail("error_username_not_exist");
                	if(StringUtil.isEmpty(tmp.get(0).email))renderFail("error_email_empty"); 	
                }
            }

            if(m == null)renderFail("error_username_not_exist");
            boolean flag = SendSMSMy.sendMsg(m.phone, m.pwd, "5");
            if(!flag)renderFail("error_unknown");
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
