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

var msgCount=3;
function SetSMsg(code){
	curCount = msgCount;
	$("#gmsg").show();
	if('1' == code){
		$("#gmsg").text("注册成功！");
	}else if('2' == code){
		$("#gmsg").text("请到注册邮箱查收新密码!");
	}else if('3' == code){
		$("#gmsg").text("修改成功!");
	}
	
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
        		window.location = forword+"?successMsg="+successMsg;
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			window.location = "/public/html5/login.html";
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
        		$("#auth").text(obj.results.auth);
        		$("#qq").text(obj.results.qq);
        		$("#email").text(obj.results.email);
        		$("#weixin").text(obj.results.weixin);
            	$("#phone").text(obj.results.phone);
            	$("#constellation").text(obj.results.constellation);
            	$("#blood").text(obj.results.blood);
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			window.location = "/public/html5/login.html";
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
        			window.location = "/public/html5/login.html";
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

function loadEditPersonalImg(){
	var data = {
            z:sessionStorage.getItem("sessionID")
        };
	$.ajax({
        url: "/c/p/gmii",
        type: "get",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$("#v_img_ch").attr("src", obj.results.img_ch);
        	}else{
        		alert(obj.msg);
        		if(obj.msg == 'session_expired'){
        			window.location = "/public/html5/login.html";
        		}
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

function setPPFileUpload(){
	$('#img_ch').click();
	var data = {
			z:sessionStorage.getItem("sessionID")
	};
	$.ajax({
        url: "/c/p/gmd",
        type: "post",
        data: data,
        dataType: "text",
        success:function(msg){
        	var obj = jQuery.parseJSON(msg);
        	if(obj.state==1){
        		$('#v_img_ch').attr("src", obj.results.tf);
        	}
        }
    });
}

function personalEdit(){
	window.location = "/public/html5/personal/info_edit.html";
}
function jumppage(page){
	switch(page){
	case 110:
		window.location = "/public/html5/personal/info_view.html";
		break;
	case 111:
		window.location = "/public/html5/personal/info_edit.html";
		break;
	case 112:
		window.location = "/public/html5/personal/info_edit_portrait.html";
		break;
	default:
	}
	
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

