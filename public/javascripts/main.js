function regSubmit(frm, fn) {
	var dataPara = getFormJson(frm);
    $.ajax({
        url: frm.action,
        type: frm.method,
        data: dataPara,
        success: fn
    });
}

function getFormJson(frm) {
    var o = {};
    var a = $(frm).serializeArray();
    alert(a);
    $.each(a, function () {
    	alert(this.phone);
        if (o[this.name] !== undefined) {

            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });

    return o;
}

function loadInitData(url, method, flag){
	xmlhttp = null;
	if (window.XMLHttpRequest){
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp=new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	}

	xmlhttp.onreadystatechange=handlestatechange;
	xmlhttp.open(method,url,flag);
	xmlhttp.send();
	return xmlhttp;
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