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
function SetSMsg(){
	curCount = msgCount;
	$("#gmsg").show();
	$("#gmsg").text(successMsg);
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
	
	sendRequest("/c/r", "post", data, "text", "/public/html5/login.html", "注册成功!");
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
	sendRequest("/c/fps", "post", data, "text", "/public/html5/login.html", "请到注册邮箱查收新密码!");
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
	sendRequest("/c/l", "post", data, "text", "/public/html5/personal/info_edit.html", "");
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
        		sessionStorage.setItem("sessionID", obj.results.session);
        		window.location = forword+"?successMsg="+successMsg;
        	}else{
        		alert(obj.msg);
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
        		$("#img_ch").attr("src", obj.results.img_ch);
        		$("#name").val(obj.results.name);
        		$("#nickname").val(obj.results.nickname);
        		$("#birthday").val(obj.results.birthday);
        		$("#gender").val(obj.results.gender);
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
        	}else{
        		alert(obj.msg);
        	}	
        }
    });
}

function handlestatechange(){
	if (xmlhttp.readyState==4 && xmlhttp.status==200){
		return xmlhttp;
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

var xmlhttp;
function InitPipeLineDetails(url) {
	xmlhttp = null;
	if (window.XMLHttpRequest) {// code for IE7, Firefox, Opera, etc.
		xmlhttp = new XMLHttpRequest();
	} else if (window.ActiveXObject) {// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	if (xmlhttp != null) {
		xmlhttp.open("GET", url, false);
		xmlhttp.send(null);
		return xmlhttp;
	} else {
		alert("Your browser does not support XMLHTTP.");
	}
}