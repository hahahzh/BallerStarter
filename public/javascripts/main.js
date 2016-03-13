var successMsg;

function GetQueryString(name){
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}

var InterValObj; //timer变量，控制时间
var count = 60; //间隔函数，1秒执行
var curCount; //当前剩余秒数
function sendCode(){

	var p = $("#phone").val();
	if(p == null || p == ''){
		alert("手机号不能为空");
		return;
	}
	
	var data = {
            phone:p
        };
	$.ajax({
		url: "/c/cd",
        type: "post",
        data: data,
        dataType: "text",
        success:function(msg){
            if(msg=='OK'){
                alert("验证码已发出");

            	curCount = count;
            	$("#vcButton").attr("disabled", true);
            	$("#vcButton").val(curCount+"秒");
            	InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
            }else{
            	var obj = jQuery.parseJSON(msg); 
            	alert(obj.msg);
            }
        }
	});

}

//timer处理函数
function SetRemainTime() {
	if (curCount == 0) {                
		window.clearInterval(InterValObj);//停止计时器
		$("#vcButton").attr("disabled", false);
		$("#vcButton").text("重新发送");    
	} else {
		curCount--;
		$("#vcButton").text(curCount + "秒");
	}
}

function regSubmit() {
	
	var p = $("#phone").val();
	var vc = $("#vc").val();
	var pwd = $("#pwd").val();
	if(p == null || p == ''){
		alert("手机号不能为空");
		return;
	}
	if(vc == null || vc == ''){
		alert("验证码不能为空");
		return;
	}
	if(pwd == null || pwd == ''){
		alert("密码不能为空");
		return;
	}
	var data = {
            phone:p,
            vc:vc,
            pwd:pwd
        };
	
	sendRequest("/c/r", "post", data, "text", "/public/html5/login.html", 1);
}

function forgetPWDSubmit() {
	var n = $("#p").val();
	if(n == null || n == ''){
		alert("用户名不能为空");
		return;
	}
	
	var data = {
            p:n
        };
	sendRequest("/c/fps", "post", data, "text", "/public/html5/login.html", 2);
}

function loginSubmit() {
	var n = $("#name").val();
	var pwd = $("#pwd").val();
	if(n == null || n == ''){
		alert("用户名不能为空");
		return;
	}
	if(pwd == null || pwd == ''){
		alert("密码不能为空");
		return;
	}
	var data = {
            name:n,
            pwd:pwd
        };
	sendRequest("/c/l", "post", data, "text", "/public/html5/personal/info_view.html", 0);
}

function sendRequest(url, method, data, dataType, forword, successMsg){
	$.ajax({
        url: url,
        type: method,
        data: data,
        dataType: dataType,
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		if(obj.results.session != null && obj.results.session != ''){
        			sessionStorage.setItem("sessionID", obj.results.session);
        		}
        		if(forword != null && forword != '')window.location = forword+"?successMsg="+successMsg;
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			jumppage(11, 0);
        		}
        	}
        }
    });
}

var resultsData;
function getRequestData(url, method, data, dataType, forword){
	
}

function loadInitPersonalData(){
	
	var data = {
            z:sessionStorage.getItem("sessionID")
        };
	
	$.ajax({
        url: "/c/p/gmi",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#v_img_ch").attr("src", obj.results.img_ch);
        		$("#name").text(obj.results.name);
        		$("#nickname").text(obj.results.nickname);
        		$("#birthday").text(obj.results.birthday);
        		$("#gender").text(obj.results.gender);
        		$("#nationality").text(obj.results.nationality);
        		$("#region").text(obj.results.region);
        		$("#height").text(obj.results.height+" CM");
        		$("#weight").text(obj.results.weight+" KG");
        		$("#number").text(obj.results.number+" 号");
        		$("#team").text(obj.results.team);
        		$("#job1").text(obj.results.job1);
        		$("#job2").text(obj.results.job2);
        		$("#specialty").text(obj.results.specialty);
        		if(obj.results.auth==1){
        			$("#auth").text("是");
        		}else{
        			$("#auth").text("否");
        		}
        		$("#qq").text(obj.results.qq);
        		$("#email").text(obj.results.email);
        		$("#weixin").text(obj.results.weixin);
            	$("#phone").text(obj.results.phone);
            	$("#constellation").text(obj.results.constellation);
            	$("#blood").text(obj.results.blood);
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			jumppage(11, 0);
        		}
        	}	
        }
    });
}

function loadEditPersonalData(){
	var blood, constellation, job1, job2, gender;
	var data = {
            z:sessionStorage.getItem("sessionID")
        };
	
	$.ajax({
        url: "/c/p/gmi",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#v_img_ch").attr("src", obj.results.img_ch);
        		$("#name").val(obj.results.name);
        		$("#nickname").val(obj.results.nickname);
        		$("#birthday").val(obj.results.birthday);
        		if('女'==obj.results.gender){
        			$("#gender").prepend("<option value='男'>男</option>");
        			$("#gender").prepend("<option value='女' selected>女</option>");
        		}else{
        			$("#gender").prepend("<option value='男' selected>男</option>");
        			$("#gender").prepend("<option value='女'>女</option>");
        		}
        		$("#nationality").val(obj.results.nationality);
        		$("#region").val(obj.results.region);
        		$("#height").val(obj.results.height);
        		$("#weight").val(obj.results.weight);
        		$("#number").val(obj.results.number);
        		$("#team").val(obj.results.team);
        		$("#job1").val(obj.results.job1);
        		$("#job2").val(obj.results.job2);
        		$("#specialty").val(obj.results.specialty);
        		$("#auth").val(obj.results.auth);
        		if(obj.results.auth == 0){
        			$("#r_identification").show();
        		}else{
        			$("#r_identification").hide();
        		}
        		$("#qq").val(obj.results.qq);
        		$("#email").val(obj.results.email);
        		$("#weixin").val(obj.results.weixin);
            	$("#phone").val(obj.results.phone);
            	$("#constellation").val(obj.results.constellation);
            	$("#blood").val(obj.results.blood);
            	blood = obj.results.blood;
            	constellation = obj.results.constellation;
            	job1 = obj.results.job1;
            	job2 = obj.results.job2;
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			jumppage(11, 0);
        		}
        	}	
        }
    });
	
	$.ajax({
        url: "/c/p/gmd",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$.each(obj.results.bloodlist, function(index, json) { 
        			for(var key in json){  
        				if(blood == json[key]){
        					$("#blood").prepend("<option value="+key+" selected>"+json[key]+"</option>");
        				}else{
        					$("#blood").prepend("<option value="+key+">"+json[key]+"</option>");
        				}
        			}
        		});
        		$.each(obj.results.constellationlist, function(index, json) { 
        			for(var key in json){  
        				if(constellation == json[key]){
        					$("#constellation").prepend("<option value="+key+" selected>"+json[key]+"</option>");
        				}else{
        					$("#constellation").prepend("<option value="+key+">"+json[key]+"</option>");
        				}
        			}
        		});
        		$.each(obj.results.joblist, function(index, json) { 
        			for(var key in json){  
        				if(job1 == json[key]){
        					$("#job1").prepend("<option value="+key+" selected>"+json[key]+"</option>");
        				}else{
        					$("#job1").prepend("<option value="+key+">"+json[key]+"</option>");
        				}
        			}
        		});
        		$.each(obj.results.joblist, function(index, json) { 
        			for(var key in json){  
        				if(job2 == json[key]){
        					$("#job2").prepend("<option value="+key+" selected>"+json[key]+"</option>");
        				}else{
        					$("#job2").prepend("<option value="+key+">"+json[key]+"</option>");
        				}
        			}
        		});
        	}else{
        		alert(obj.msg);
        	}	
        }
    });
}

function personalSubmit(){
//	$("#img_ch").attr();

	var data = {
			name: $("#name").val(),
			nickname: $("#nickname").val(),
			birthday: $("#birthday").val(),
			gender: $("#gender").val(),
			nationality: $("#nationality").val(),
			region: $("#region").val(),
			height: $("#height").val(),
			weight: $("#weight").val(),
			number: $("#number").val(),
			team: $("#team").val(),
			job1: $("#job1").val(),
			job2: $("#job2").val(),
			specialty: $("#specialty").val(),
			auth: $("#auth").val(),
			qq: $("#qq").val(),
			email: $("#email").val(),
			weixin: $("#weixin").val(),
			phone: $("#phone").val(),
			constellation: $("#constellation").val(),
			blood: $("#blood").val(),
			z:sessionStorage.getItem("sessionID")
        };
	
	sendRequest("/c/p/umi", "post", data, "text", "/public/html5/personal/info_view.html", 3);
}



function jumppage(page, smsg){

	var successMsg="";
	if(smsg != 0){
		successMsg = "?successMsg="+smsg;
	}
	var loginpage = "/public/html5/login.html"+successMsg;;
	var personalviewpage = "/public/html5/personal/info_view.html"+successMsg;
	var personaleditpage = "/public/html5/personal/info_edit.html"+successMsg;
	var personalportraitpage = "/public/html5/personal/info_edit_portrait.html"+successMsg;
	var teamviewpage = "/public/html5/team/info_view.html"+successMsg;
	var teameditpage = "/public/html5/team/info_edit.html"+successMsg;
	var teamlogopage = "/public/html5/team/info_edit_logo.html"+successMsg;
	var teamcoachpage = "/public/html5/team/info_edit_coach.html"+successMsg;
	var teamcaptainpage = "/public/html5/team/info_edit_captain.html"+successMsg;
	var gameviewpage = "/public/html5/game/info_view.html"+successMsg;
	switch(page){
	case 11:
		window.location = loginpage;
		break;
	case 110:
		window.location = personalviewpage;
		break;
	case 111:
		window.location = personaleditpage;
		break;
	case 112:
		window.location = personalportraitpage;
		break;
	case 113:
		window.location = personaleditpage;
		break;
	case 120:
		window.location = teamviewpage;
		break;
	case 121:
		window.location = teameditpage;
		break;
	case 122:
		window.location = teamlogopage;
		break;
	case 123:
		window.location = teamcoachpage;
		break;
	case 124:
		window.location = teamcaptainpage;
		break;
	case 130:
		window.location = gameviewpage;
		break;
	default:
	}
}

var msgCount=3;
function SetSMsg(code){
	curCount = msgCount;
	if('1' == code){
		$("#gmsg").text("注册成功！");
	}else if('2' == code){
		$("#gmsg").text("请到注册邮箱查收新密码!");
	}else if('3' == code){
		$("#gmsg").text("修改成功!");
	}else if('4' == code){
		$("#gmsg").text("请先登录");
	}else if('5' == code){
		$("#gmsg").text("报名成功,请等待审核！");
	}
	$("#gmsg").show();
	InterValObj = window.setInterval(SetSMsgTime, 1000); //启动计时器，1秒执行一次
}
//timer处理函数
function SetSMsgTime() {
	if (curCount == 0) {                
		window.clearInterval(InterValObj);//停止计时器
		$("#gmsg").hide();    
	} else {
		curCount--;
	}
}

function loadInitTeamData(){
	
	var data = {
            z:sessionStorage.getItem("sessionID")
        };
	$.ajax({
        url: "/c/t/gti",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#v_img_t_logo").attr("src", obj.results.logo);
        		$("#name").text(obj.results.name);
        		$("#v_coach_img").attr("src", obj.results.coach_img);
        		$("#coach").text(obj.results.coach);
          		$("#v_captain_img").attr("src", obj.results.captain_img);
        		$("#captain").text(obj.results.captain);
        		$("#contact").text(obj.results.contact);
        		$("#updated_at_ch").text(obj.results.updated_at_ch);
        		str = "";
        		$.each(obj.results.members, function(index, json) {
        			str+="<tr><td>";
        			str+="姓名: "+json.name;
        			str+="</br>";
        			str+="场上位置: "+json.job1;
        			str+="</br>";
        			str+="号码: "+json.number+" 号";
        			str+="</br>";
        			str+="身高: "+json.height+" CM";
        			str+="</br>";
        			str+="体重: "+json.weight+" KG";
        			str+="</td>";
        			str+="<td>";
        			str+="<img src='"+json.img_ch+"'>";
                	str+="</td></tr>";
        		});
        		$("#t_v_members").append(str);
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			jumppage(11, 0);
        		}
        	}	
        }
    });
}

function loadEditTeamData(){
	var data = {
            z:sessionStorage.getItem("sessionID")
        };
	$.ajax({
        url: "/c/t/gti",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#v_img_t_logo").attr("src", obj.results.logo);
        		$("#name").val(obj.results.name);
        		$("#v_coach_img").val(obj.results.coach_img);
        		$("#coach").val(obj.results.coach);
          		$("#v_captain_img").val(obj.results.captain_img);
        		$("#captain").val(obj.results.captain);
        		$("#contact").val(obj.results.contact);
        		str = "";
        		$.each(obj.results.members, function(index, json) {
        			str+="<tr><td>";
        			str+="姓名: "+json.name;
        			str+="</br>";
        			str+="场上位置: "+json.job1;
        			str+="</br>";
        			str+="号码: "+json.number+" 号";
        			str+="</br>";
        			str+="身高: "+json.height+" CM";
        			str+="</br>";
        			str+="体重: "+json.weight+" KG";
        			str+="</td>";
        			str+="<td>";
        			str+="<img src='"+json.img_ch+"'>";
                	str+="</td></tr>";
        		});
        		$("#t_e_members").append(str);
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			jumppage(11, 0);
        		}
        	}	
        }
    });
}

function teamSubmit(){
	var data = {
			name: $("#name").val(),
			coach: $("#coach").val(),
			captain: $("#captain").val(),
			contact: $("#contact").val(),
			z:sessionStorage.getItem("sessionID")
        };
	
	sendRequest("/c/t/uti", "post", data, "text", "/public/html5/team/info_view.html", 3);
}

function loadInitGameData(){
	var data = {
        };
	$.ajax({
        url: "/c/g/ggi",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#gId").val(obj.results.id);
        		$("#v_img_g_logo").attr("src", obj.results.logo);
        		$("#name").text(obj.results.name);
        		$("#describtion").text(obj.results.describtion);
        		//$("#schedule").text(obj.results.schedule);
        		$("#a_g_schedule").attr("href", obj.results.schedule);
        		$("#startSignUp").text(obj.results.startSignUp);
        		$("#endSignUp").text(obj.results.endSignUp);
        		$("#startDate").text(obj.results.startDate);
        		$("#endDate").text(obj.results.endDate);
        		if(obj.results.isSignUp == 0){
        			$("#b_g_signup").attr("disabled", true);
        			$("#b_g_signup").text("报名结束");
        		}else{
        			$("#b_g_signup").attr("disabled", false);
        			$("#b_g_signup").val("我要报名");
        		}
        		$("#a_g_standings").attr("href", "/public/html5/game/standings.html?gid="+obj.results.id);
        		$("#a_g_results").attr("href", "/public/html5/game/results.html?gid="+obj.results.id);
        		str = "";
        		$.each(obj.results.teamlist, function(index, json) {
        			str+="<tr><td>";
        			str+="球队名称: "+json.name;
        			str+="</br>";
        			str+="教练: "+json.coach;
        			str+="</br>";
        			str+="队长: "+json.captain;
        			str+="</td>";
        			str+="<td>";
        			str+="<img src='"+json.logo+"'>";
                	str+="</td></tr>";
        		});
        		$("#g_v_teams").append(str);
        	}else{
        		alert(obj.msg);
        	}	
        }
    });
}

function signup(){
	if(sessionStorage.getItem("sessionID")==null||sessionStorage.getItem("sessionID")==''){
		jumppage(11, 4);
		return;
	}
	if(confirm("确认要报名吗?")){
		var id = $("#gId").val();
		var data = {
	            gId:id,
	            z:sessionStorage.getItem("sessionID")
	        };
		sendRequest("/c/g/su", "get", data, "text", "/public/html5/game/info_view.html", 5);
    }else{
        return;
    }
}

function loadGameStandingsData(){

	var gId = GetQueryString("gid");
	var data = {
            gId:gId
        };
	$.ajax({
        url: "/c/g/ggsd",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#v_s_t_name").text(obj.results.game);
        		str = "";
        		$.each(obj.results.datalist, function(index, json) {
        			str+="<tr><td>";
        			str+=json.standing;
        			str+="</td>";
        			str+="<td>";
        			str+=json.name;
        			str+="</td>";
        			str+="<td>";
        			str+=json.round;
        			str+="</td>";
        			str+="<td>";
        			str+=json.win;
        			str+="</td>";
        			str+="<td>";
        			str+=json.lose;
        			str+="</td>";
        			str+="<td>";
        			str+=json.rate;
        			str+="</td>";
                	str+="</td></tr>";
        		});
        		$("#g_v_standings").append(str);
        	}else{
        		alert(obj.msg);
        	}	
        }
    });
}

function loadGameResultsData(){
	
	var gId = GetQueryString("gid");
	var data = {
            gId:gId
        };
	$.ajax({
        url: "/c/g/ggrd",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#v_r_t_name").text(obj.results.game);
        		str = "";
        		$.each(obj.results.datalist, function(index, json) {
        			str+="<tr><td>";
        			str+=json.ruond;
        			str+="</td>";
        			str+="<td>";
        			str+=json.name;
        			str+="</td>";
        			str+="<td>";
        			str+=json.point;
        			str+="</td>";
        			str+="<td>";
        			str+=json.type;
        			str+="</td>";
        			str+="<td>";
        			str+=json.date;
                	str+="</td></tr>";
        		});
        		$("#g_v_results").append(str);
        	}else{
        		alert(obj.msg);
        	}	
        }
    });
}

function setPPFileUpload(){
	$('#f_edit_img').click();
}

function ajaxFileUpload() {
	var rs = GetQueryString("rs");
	var tfId = $("#h_s_tfId").val()
	var data = {
			rs:rs,
			sType:'0',
			tfId:tfId,
            z:sessionStorage.getItem("sessionID")
        };
	
    $.ajaxFileUpload({
            url: '/c/stf',
            secureuri: false,
            fileElementId: 'f_edit_img',
            data: data,
            dataType: 'text',
            success: function (data, status) {
            	var reg = /<pre.+?>(.+)<\/pre>/g;  
            	var result = data.match(reg);
            	data = RegExp.$1;
            	var obj = jQuery.parseJSON(data);
            	if(obj.state==1){
            		var t = obj.results.tf.replace(/&amp;/g,'&');
            		$("#h_s_tfId").val(obj.results.tfId);
            		$("#c_edit_img").attr("src", t);
            	}else{
            		alert(obj.msg);
            	}
            }
        });
    return false;
}

function setImg(resource){
	var saveimgpage = "/public/html5/save_img.html?rs="+resource;
	window.location = saveimgpage;
}

function saveImg(){
	
	var rs = Number(GetQueryString("rs"));
	var tfId = $("#h_s_tfId").val()
	var data = {
			rs:rs,
			sType:'1',
			tfId:tfId,
            z:sessionStorage.getItem("sessionID")
        };
	
    $.ajaxFileUpload({
            url: '/c/stf',
            secureuri: false,
            fileElementId: 'f_edit_img',
            data: data,
            dataType: 'text',
            success: function (data, status) {
            	var reg = /<pre.+?>(.+)<\/pre>/g;  
            	var result = data.match(reg);
            	data = RegExp.$1;
            	var obj = jQuery.parseJSON(data);
            	if(obj.state==1){
            		jumppage(rs, 0);
            	}else{
            		alert(obj.msg);
            	}
            }
        });
    return false;
}

function setPData(xmlhttp){
	if (xmlhttp.readyState==4 && xmlhttp.status==200){
		var jsonData = eval("(" + xmlhttp.responseText + ")");

		if(jsonData.state == 1){
			var jsonObject = jsonData.results;
			var jsonArr = jsonObject.list;
			
			for(var i=0;i<jsonArr.length;i++){
				if(i==0){
					var opp=new Option("Global","0");
				}else{
					var opp=new Option(jsonArr[i].name+"  "+jsonArr[i].abbr,jsonArr[i].code);
				}
				
				if(mcc == jsonArr[i].code+'')sel.options[i].selected=true;
			 }
			alert(jsonObject);
		}else{
			alert(jsonData.msg);
		}
	}
}

