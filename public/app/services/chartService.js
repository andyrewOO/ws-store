'use strict';
app.service('chartWeightService', function($rootScope, $timeout) {
	this.initTitleBar = function(e,button){
		var button = button?button:"collapse",
			_button = {
				TOGGLE_CLOSE:'dispose',
				TOGGLE_COLLAPSE:'collapse',
				TOGGLE_FULLSCREEN:'maximize',
				CLASS_CLOSE:'times',
				CLASS_COLLAPSE:'minus',
				CLASS_FULLSCREEN:'expand'
			}
			//设置按钮
			var buttonArr = button.split(";");
			Array.prototype.in_array = function(e){  
			    for(i=0;i<this.length && this[i]!=e;i++);  
			    return !(i==this.length);  
			}
			
//			var weightBtn = e.children().eq(0).children().eq(0).children().eq(1).children();
			var weightBtn = e.find('.widget-buttons').children();
			if(!buttonArr.in_array("close")){
				weightBtn.remove("a[data-toggle=dispose]");
			}
			if(!buttonArr.in_array("collapse")){
				weightBtn.remove("a[data-toggle=collapse]");
			}
			if(!buttonArr.in_array("fullScreen")){
				weightBtn.remove("a[data-toggle=maximize]");
			}
	}
})