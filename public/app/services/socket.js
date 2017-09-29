app.service('swSocketJs',function(){
	var self = this;
	
	// 根据url新建一个socketJs连接,并返回
	this.newSocketConnection = function(url){
		var socketUrl = url;
		if(socketUrl == null) return null;
		var sConn = new SockJS(socketUrl);
		
		// 使用时必须至少复写onmessage方法,处理消息
		sConn.onopen = function () {};  
        sConn.onmessage = function (event) {};  
        sConn.onclose = function (event) {}; 
        
        return sConn;
	}
	
	// 关闭socketJs连接
	this.closeSocketConnection = function(sConn){
		if(sConn == null) return;
		sConn.close();
	}
	
	// 发送数据
	this.send = function(sConn,msg){
		if(sConn != null){
			sConn.send(msg);
		}
	}
	
});