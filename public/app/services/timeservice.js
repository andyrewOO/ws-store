'use strict';
app.service('timeFmt', function() {
	this.getDate = function(oDate,sFormat,offset,Bzero){
		// 获取oDate 偏置offset天数之后的日期
		oDate.setDate(oDate.getDate() + offset);
		// 根据格式，拼装
		sFormat = sFormat.replace('YYYY',oDate.getFullYear());
		sFormat = sFormat.replace("YY", String(oDate.getFullYear()).substr(2));
		sFormat = sFormat.replace("MM", oDate.getMonth()+1);
		sFormat = sFormat.replace("DD", oDate.getDate());
		sFormat = sFormat.replace("hh", oDate.getHours());
		sFormat = sFormat.replace("mm", oDate.getMinutes());
		sFormat = sFormat.replace("ss", oDate.getSeconds());
		// 判断是否补零
		if(Bzero){
			sFormat = sFormat.replace(/\b(\d)\b/g,'0$1');
		}
		return sFormat;
	}
	this.getDateHour = function(oDate,sFormat,offsetHour,Bzero){
		// 获取oDate 偏置offset天数之后的日期
		oDate.setHours(oDate.getHours() + offsetHour);
		// 根据格式，拼装
		sFormat = sFormat.replace('YYYY',oDate.getFullYear());
		sFormat = sFormat.replace("YY", String(oDate.getFullYear()).substr(2));
		sFormat = sFormat.replace("MM", oDate.getMonth()+1);
		sFormat = sFormat.replace("DD", oDate.getDate());
		sFormat = sFormat.replace("hh", oDate.getHours());
		sFormat = sFormat.replace("mm", oDate.getMinutes());
		sFormat = sFormat.replace("ss", oDate.getSeconds());
		// 判断是否补零
		if(Bzero){
			sFormat = sFormat.replace(/\b(\d)\b/g,'0$1');
		}
		return sFormat;
	}
	//获取当前日期在当前年第几周函数封装，例如2013-08-15 是当前年的第32周
	this.getWeekIndex = function(oDate,offset){
		var now = oDate;	// 传进来的参数必须是Date对象
		now.setDate(now.getDate() + offset)
		var year = now.getFullYear();
		var firstDay = new Date(year,0,1);
		var d = Math.round((now.valueOf() - firstDay.valueOf()) / 86400000); 
		return {
			year:year,
			weekIndex:Math.ceil((d + ((firstDay.getDay() + 1) - 1)) / 7 )
		} 
	}
	
});