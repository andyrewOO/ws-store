//序列化为JSON对象
$.fn.serializeObject = function(){
    var a,o,h,i,e;
    a=this.serializeArray();
    o={};
    h=o.hasOwnProperty;
    for(i=0;i<a.length;i++){
        e=a[i];
        if(!h.call(o,e.name)){
            o[e.name]=e.value;
        }
    }
    return o;
};

//根据form表单属性拼接get请求 链接参数 param=form.id
function serializeNotIncludeNull11(id){
	var flag=false;
	var parameReqUrl="";
    var a,o,h,i,e;
    a=$("#"+id).serializeArray();
    o={};
    h=o.hasOwnProperty;
    for(i=0;i<a.length;i++){
        e=a[i];
        if(!h.call(o,e.name)){
            if(e.value){
            	if(!flag){ //首个参数要拼一个"？"
            		parameReqUrl+="?"+e.name+"="+e.value;
            		flag=true;
            	}else{
            		parameReqUrl+="&"+e.name+"="+e.value;
            	}
            	
            }
        }
    }
    return parameReqUrl;
}