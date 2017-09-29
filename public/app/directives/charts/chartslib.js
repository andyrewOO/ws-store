app.directive('egfEcharts',['$templateCache','$timeout', function($templateCache,$timeout) {
	return {
		restrict : 'AE',
		scope : {
			echartsOption : "=",
			echartsInstance:"=",
			theme:"="
		},
		template : '<div style="height:350px;"></div>',
		replace : true,
		link : function($scope, element, attrs) {
			var myChart,updateCharts,theme;
			theme = $scope.theme?$scope.theme:"default";
			/** 初始化eCharts**/
			/**
			 * 可用theme:dark,macarons,vintage
			 */
			myChart = echarts.init(element[0],theme);
			$scope.echartsInstance = myChart;
			myChart.showLoading({text:"正为您努力加载...",effect:'bubble'});

			updateCharts = function(){
				if($scope.echartsOption != null && $scope.echartsOption!=undefined){
					myChart.hideLoading();
					myChart.setOption($scope.echartsOption,false);
					myChart.resize();
				}
//				$(element.children()[0]).css('overflow','visible');
				 
			}
			$scope.$watch('echartsOption',function(newVal,oldVal){
				if(newVal){
					updateCharts();
				}
			},true);
		}
	}
}]);


/*lineOption chartData 格式:
{
	xAxisData :[],
	seriesData:[{name:'',data:[]},{}]
}*/

app.directive('egfEchartsLineZoom',['chartWeightService','timeFmt',function(chartWeightService,timeFmt){
return {
	restrict : 'AE',
	scope : {
		chartName : "@",
		chartData : "=",
		theme: "@",
		button : "@",
		dropdown:"="
	},
	templateUrl : 'views/charts/echartsTemplate.html',
	transclude : true,
	controller : function($scope){
		this.option = {
			    title : {show:false,text: '默认名称'},
			    tooltip : {
			    	trigger: 'axis',
			    	axisPointer: {
		            animation: false
			    	}
		        },
			    legend: {x:'center',data:[]},
			    grid: {
			    	x:35,
			    	y:40,
			    	x2:35,
			    	y2:20,
			        bottom: 80
			    },
			    dataZoom: [
			        {
			           show: true,
			           realtime: true,
			           start: 80,
			           end: 100
			        }
			      ],
			    calculable : true,
			    xAxis : [
			        {
			            type : 'category',
			            boundaryGap : false,
			            data : []
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        
			    ]
			};
	},
	link : function($scope,iElement,iAttrs,controller){
		$scope.updateCmd = undefined;
		$scope.optionTemp = controller.option;
		$scope.option = undefined;
		// 初始化按钮
		chartWeightService.initTitleBar(iElement,$scope.button);
		$scope.$watch('chartData',function(newVal,oldVal){
			if(newVal != undefined && angular.isArray(newVal.seriesData)){
				$scope.optionTemp.legend.data = [];		//清除legend的数据信息
				$scope.optionTemp.series = [];			//清除series信息
				newVal.seriesData.forEach(function(item,index){
					var legendName = item.name ? item.name : '未知名称'+index;
					var seriesData = item.data ? item.data : [];
					$scope.optionTemp.legend.data.push(legendName);
					$scope.optionTemp.series.push({
						name:legendName,
						type:'line',
						data:seriesData,
						hoverAnimation: false,
			            areaStyle: {
			                normal: {
			                  
			                }
			            },
			            lineStyle: {
			                normal: {
			                    width: 1
			                }
			            }
					});
				});
				$scope.optionTemp.xAxis[0].data = newVal.xAxisData ? newVal.xAxisData : [];
				$scope.option = $scope.optionTemp;
			}
		},true);
	}
}
}]);


/*lineOption chartData 格式:
	{
		xAxisData :[],
		seriesData:[{name:'',data:[]},{}]
	}*/

app.directive('egfEchartsLine',['chartWeightService','timeFmt',function(chartWeightService,timeFmt){
	return {
		restrict : 'AE',
		scope : {
			chartName : "@",
			chartData : "=",
			theme: "@",
			button : "@",
			dropdown:"=",
			customButtons :"="
		},
		templateUrl : 'views/charts/echartsTemplate.html',
		transclude : true,
		controller : function($scope){
			this.option = {
				    title : {show:false,text: '默认名称'},
				    tooltip : {trigger: 'axis'},
				    legend: {x:'center',y:0,data:[]},
				    grid: {x:35,y:40,x2:35,y2:20},
				    toolbox: {
				        show : true,
				        x: 'left',
				        feature : {
				            mark : {show: true},
				            dataView : {show: true, readOnly: false},
				            magicType : {show: false, type: ['line', 'bar']},
				            restore : {show: true},
				            saveAsImage : {show: true}
				        }
				    },
				    calculable : true,
				    xAxis : [
				        {
				            type : 'category',
				            boundaryGap : false,
				            data : []
				        }
				    ],
				    yAxis : [
				        {
				            type : 'value'
				        }
				    ],
				    series : [
				        {
				            name:'访问量',
				            type:'line',
				            data:[]
				        }
				    ]
				};
		},
		link : function($scope,iElement,iAttrs,controller){
			$scope.updateCmd = undefined;
			$scope.optionTemp = controller.option;
			$scope.option = undefined;
			// 初始化按钮
			chartWeightService.initTitleBar(iElement,$scope.button);
			$scope.$watch('chartData',function(newVal,oldVal){
				if(newVal != undefined && angular.isArray(newVal.seriesData)){
					$scope.optionTemp.legend.data = [];		//清除legend的数据信息
					$scope.optionTemp.series = [];			//清除series信息
					newVal.seriesData.forEach(function(item,index){
						var legendName = item.name ? item.name : '未知名称'+index;
						var seriesData = item.data ? item.data : [];
						$scope.optionTemp.legend.data.push(legendName);
						$scope.optionTemp.series.push({
							name:legendName,
							type:'line',
							data:seriesData
						});
					});
					$scope.optionTemp.xAxis[0].data = newVal.xAxisData ? newVal.xAxisData : [];
					$scope.option = $scope.optionTemp;
				}
			},true);
			
			$scope.dropdownClick = function(item){
				if(item != $scope.dropdown.selected){
					$scope.dropdown.selected = item;
					if($scope.dropdown.onChange!=undefined){
						$scope.dropdown.onChange(item);
					}
				}
			}
			// 时间控件
			$scope.today = timeFmt.getDate(new Date(),'YYYY-MM-DD',0,true);
			$scope.dt = {};
			$scope.dt.start = undefined;
			$scope.dt.end = undefined;
			
		    $scope.dateOptions = {
		            formatYear : 'yy',
		            startingDay : 1
		        }
		    $scope.opened = {};
		    $scope.opened.start = false;
		    $scope.opened.end = false;
		    $scope.open = {};
		    
	        $scope.formats = ['dd-MMMM-yyyy','yyy/MM/dd','dd.MM.yyyy','shortDate','yyyy-MM-dd']; //日期显示格式 
	        $scope.format = $scope.formats[4];  // 将formats的第0项设为默认显示格式 
	        $scope.clear = function(){ 
	            $scope.dt = null;
	        }
	        $scope.open.start = function($event){  
	            $event.preventDefault();
	            $event.stopPropagation();
	            $scope.opened.start = true;
	        }
	        $scope.open.end = function($event){  
	        	$event.preventDefault();
	        	$event.stopPropagation();
	        	$scope.opened.end = true;
	        }
	        $scope.confirm = function(start,end){
	        	var startDate = timeFmt.getDate(start,'YYYY-MM-DD',0,true);
	        	var endDate = timeFmt.getDate(end,'YYYY-MM-DD',0,true);
	        	var startStr = timeFmt.getDate(start,'YY/MM/DD',0,true);
	        	var endStr = timeFmt.getDate(end,'YY/MM/DD',0,true);
	        	$scope.dropdown.selected = startStr + '-' + endStr;
	        	if($scope.dropdown.inquiry.confirm){
	        		$scope.dropdown.inquiry.confirm(startDate,endDate);
	        	}
	        }
		}
	}
}]);

/** 饼状图数据格式 **
{
	pieName:'访问来源',
	pieData:[{name:'直接访问',value:'355'},{name:'邮件营销',value:'310'},{name:'联盟广告',value:'234'},{name:'视频广告',value:'135'},{name:'搜索引擎',value:'1548'}]
}
**/
app.directive('egfEchartsPie',['chartWeightService','timeFmt',function(chartWeightService,timeFmt){
	return {
		restrict : 'AE',
		scope : {
			chartName : "@",
			chartData : "=",
			theme: "@",
			button : "@",
			dropdown:"="
		},
		templateUrl : 'views/charts/echartsTemplate.html',
		transclude : true,
		controller : function($scope){
			this.option = {
				    title : {
				    	show:false,
				        text: '',
				        subtext: '',
				        x:'center'
				    },
				    tooltip : {
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				    },
				    legend: {
				        orient : 'vertical',
				        x : 'right',
				        data:[]
				    },
				    toolbox: {
				        show : false,
				        x: 'left',
				        feature : {
				            mark : {show: true},
				            dataView : {show: true, readOnly: false},
				            magicType : {
				                show: false, 
				                type: ['pie', 'bar']
				            },
				            restore : {show: true},
				            saveAsImage : {show: true}
				        }
				    },
				    calculable : true,
				    series : []
				};
		},
		link : function($scope,iElement,iAttrs,controller){
			$scope.updateCmd = undefined;
			$scope.optionTemp = controller.option;
			$scope.option = undefined;
			// 初始化按钮
			chartWeightService.initTitleBar(iElement,$scope.button);
			$scope.$watch('chartData',function(newVal,oldVal){
//				if(oldVal === newVal) return;
				if(newVal != undefined && angular.isArray(newVal.pieData)){
					$scope.optionTemp.legend.data = [];		//清除legend的数据信息
					$scope.optionTemp.series = [];			//清除series信息
					$scope.optionTemp.series.push({
				            name:newVal.pieName,
				            type:'pie',
				            radius : '70%',
				            center: ['50%', '50%'],
				            data:newVal.pieData
					});
					newVal.pieData.forEach(function(item,index){
						var legendName = item.name ? item.name : '未知名称'+index;
						$scope.optionTemp.legend.data.push(legendName);
					});
					$scope.option = $scope.optionTemp;
				}
			},true);
			$scope.dropdownClick = function(item){
				if(item != $scope.dropdown.selected){
					$scope.dropdown.selected = item;
					if($scope.dropdown.onChange!=undefined){
						$scope.dropdown.onChange(item);
					}
				}
			}
			// 时间控件
			$scope.today = timeFmt.getDate(new Date(),'YYYY-MM-DD',0,true);
			$scope.dt = {};
			$scope.dt.start = undefined;
			$scope.dt.end = undefined;
			
		    $scope.dateOptions = {
		            formatYear : 'yy',
		            startingDay : 1
		        }
		    $scope.opened = {};
		    $scope.opened.start = false;
		    $scope.opened.end = false;
		    $scope.open = {};
		    
	        $scope.formats = ['dd-MMMM-yyyy','yyy/MM/dd','dd.MM.yyyy','shortDate','yyyy-MM-dd']; //日期显示格式 
	        $scope.format = $scope.formats[4];  // 将formats的第0项设为默认显示格式 
	        $scope.clear = function(){ 
	            $scope.dt = null;
	        }
	        $scope.open.start = function($event){  
	            $event.preventDefault();
	            $event.stopPropagation();
	            $scope.opened.start = true;
	        }
	        $scope.open.end = function($event){  
	        	$event.preventDefault();
	        	$event.stopPropagation();
	        	$scope.opened.end = true;
	        }
	        $scope.confirm = function(start,end){
	        	var startDate = timeFmt.getDate(start,'YYYY-MM-DD',0,true);
	        	var endDate = timeFmt.getDate(end,'YYYY-MM-DD',0,true);
	        	var startStr = timeFmt.getDate(start,'YY/MM/DD',0,true);
	        	var endStr = timeFmt.getDate(end,'YY/MM/DD',0,true);
	        	$scope.dropdown.selected = startStr + '-' + endStr;
	        	if($scope.dropdown.inquiry.confirm){
	        		$scope.dropdown.inquiry.confirm(startDate,endDate);
	        	}
	        }
		}
	}
}]);

/* 柱状图 */
/*BarOption chartData 格式:
{
	xAxisData :['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
	seriesData:[{name:'蒸发量',data:[2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3]},{name:'降水量',data:[2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3]}]
}*/
app.directive('egfEchartsBar',['chartWeightService','timeFmt',function(chartWeightService,timeFmt){
return {
	restrict : 'AE',
	scope : {
		chartName : "@",
		chartData : "=",
		theme: "@",
		button : "@",
		dropdown:"=",
		customButtons :"="
	},
	templateUrl : 'views/charts/echartsTemplate.html',
	transclude : true,
	controller : function($scope){
		this.option = {
			    title : {
			    	show:false,
			        text: '',
			        subtext: ''
			    },
			    tooltip : {
			        trigger: 'axis',
		            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		            }
			    },
			    legend: {
			        data:[]
			    },
			    grid: {left: '3%',
			        right: '4%',
			        bottom: '3%',
			        containLabel: true},
			    toolbox: {
			        show : true,
			        x: 'left',
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: true},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    calculable : true,
			    xAxis : [
			        {
			            type : 'category',
			            data : []
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : []
			};
	},
	link : function($scope,iElement,iAttrs,controller){
		$scope.updateCmd = undefined;
		$scope.optionTemp = controller.option;
		$scope.option = undefined;
		// 初始化按钮
		chartWeightService.initTitleBar(iElement,$scope.button);
		$scope.$watch('chartData',function(newVal,oldVal){
			if(newVal != undefined && angular.isArray(newVal.seriesData)){
				$scope.optionTemp.legend.data = [];		//清除legend的数据信息
				$scope.optionTemp.series = [];			//清除series信息
				newVal.seriesData.forEach(function(item,index){
					var legendName = item.name ? item.name : '未知名称'+index;
					var seriesData = item.data ? item.data : [];
					var stack = item.stack ? item.stack : item.name ;
					$scope.optionTemp.legend.data.push(legendName);
					$scope.optionTemp.series.push({
						name:legendName,
						type:'bar',
						stack:stack,
						data:seriesData
					});
				});
				$scope.optionTemp.xAxis[0].data = newVal.xAxisData ? newVal.xAxisData : [];
				$scope.option = $scope.optionTemp;
			}
		},true);
		$scope.dropdownClick = function(item){
			if(item != $scope.dropdown.selected){
				$scope.dropdown.selected = item;
				if($scope.dropdown.onChange!=undefined){
					$scope.dropdown.onChange(item);
				}
			}
		}
		// 时间控件
		$scope.today = timeFmt.getDate(new Date(),'YYYY-MM-DD',0,true);
		$scope.dt = {};
		$scope.dt.start = undefined;
		$scope.dt.end = undefined;
		
	    $scope.dateOptions = {
	            formatYear : 'yy',
	            startingDay : 1
	        }
	    $scope.opened = {};
	    $scope.opened.start = false;
	    $scope.opened.end = false;
	    $scope.open = {};
	    
        $scope.formats = ['dd-MMMM-yyyy','yyy/MM/dd','dd.MM.yyyy','shortDate','yyyy-MM-dd']; //日期显示格式 
        $scope.format = $scope.formats[4];  // 将formats的第0项设为默认显示格式 
        $scope.clear = function(){ 
            $scope.dt = null;
        }
        $scope.open.start = function($event){  
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened.start = true;
        }
        $scope.open.end = function($event){  
        	$event.preventDefault();
        	$event.stopPropagation();
        	$scope.opened.end = true;
        }
        $scope.confirm = function(start,end){
        	var startDate = timeFmt.getDate(start,'YYYY-MM-DD',0,true);
        	var endDate = timeFmt.getDate(end,'YYYY-MM-DD',0,true);
        	var startStr = timeFmt.getDate(start,'YY/MM/DD',0,true);
        	var endStr = timeFmt.getDate(end,'YY/MM/DD',0,true);
        	$scope.dropdown.selected = startStr + '-' + endStr;
        	if($scope.dropdown.inquiry.confirm){
        		$scope.dropdown.inquiry.confirm(startDate,endDate);
        	}
        }
	}
}
}]);

/* 条形图  */
/*LbarOption chartData 格式:
{
	xAxisData :['巴西','印尼','美国','印度','中国','世界人口(万)'],
	seriesData:[{name:'蒸发量',data:[18203, 23489, 29034, 104970, 131744, 630230]},{name:'降水量',data:[19325, 23438, 31000, 121594, 134141, 681807]}]
}*/

app.directive('egfEchartsLbar',['chartWeightService','timeFmt',function(chartWeightService,timeFmt){
return {
	restrict : 'AE',
	scope : {
		chartName : "@",
		chartData : "=",
		theme: "@",
		button : "@",
		dropdown:"="
	},
	templateUrl : 'views/charts/echartsTemplate.html',
	transclude : true,
	controller : function($scope){
		this.option = {
			    title : {
			    	show:false,
			        text: '',
			        subtext: ''
			    },
			    tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			        data:[]
			    },
			    grid: {x:80,y:40,x2:80,y2:20},
			    toolbox: {
			        show : true,
			        x: 'left',
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: false},
			            magicType : {show: false, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    calculable : true,
			    xAxis : [
		             {
		                 type : 'value',
		                 boundaryGap : [0, 0.01]
		             }
			    ],
			    yAxis : [
		             {
		                 type : 'category',
		                 data : []
		             }
			    ],
			    series : []
			};
	},
	link : function($scope,iElement,iAttrs,controller){
		$scope.updateCmd = undefined;
		$scope.optionTemp = controller.option;
		$scope.option = undefined;
		// 初始化按钮
		chartWeightService.initTitleBar(iElement,$scope.button);
		var xIncreaseData = [];		// 排序后的数据数组
		$scope.$watch('chartData',function(newVal,oldVal){
			if(newVal != undefined && angular.isArray(newVal.seriesData)){
				$scope.optionTemp.legend.data = [];		//清除legend的数据信息
				$scope.optionTemp.series = [];			//清除series信息
				
				
				newVal.seriesData.forEach(function(item,index){
					var legendName = item.name ? item.name : '未知名称'+index;
					var seriesData = item.data ? item.data.slice(0) : [];
					var map = {};
					/*构造数据为Key，名称为value的对象*/
					seriesData.forEach(function(d,index){
						map[d] = newVal.xAxisData[index];
					});
					/*升序排序*/
					seriesData = seriesData.sort(function(a,b){
						return a-b;
					});
					seriesData.forEach(function(s,index){
						xIncreaseData[index] = map[s];
					});
					$scope.optionTemp.legend.data.push(legendName);
					$scope.optionTemp.series.push({
						name:legendName,
						type:'bar',
						data:seriesData
					});
				});
				$scope.optionTemp.yAxis[0].data = xIncreaseData ? xIncreaseData : [];
				$scope.option = $scope.optionTemp;
			}
		},true);
		$scope.dropdownClick = function(item){
			if(item != $scope.dropdown.selected){
				$scope.dropdown.selected = item;
				if($scope.dropdown.onChange!=undefined){
					$scope.dropdown.onChange(item);
				}
			}
		}
		// 时间控件
		$scope.today = timeFmt.getDate(new Date(),'YYYY-MM-DD',0,true);
		$scope.dt = {};
		$scope.dt.start = undefined;
		$scope.dt.end = undefined;
		
	    $scope.dateOptions = {
	            formatYear : 'yy',
	            startingDay : 1
	        }
	    $scope.opened = {};
	    $scope.opened.start = false;
	    $scope.opened.end = false;
	    $scope.open = {};
	    
        $scope.formats = ['dd-MMMM-yyyy','yyy/MM/dd','dd.MM.yyyy','shortDate','yyyy-MM-dd']; //日期显示格式 
        $scope.format = $scope.formats[4];  // 将formats的第0项设为默认显示格式 
        $scope.clear = function(){ 
            $scope.dt = null;
        }
        $scope.open.start = function($event){  
            $event.preventDefault();
            $event.stopPropagation();
            $scope.opened.start = true;
        }
        $scope.open.end = function($event){  
        	$event.preventDefault();
        	$event.stopPropagation();
        	$scope.opened.end = true;
        }
        $scope.confirm = function(start,end){
        	var startDate = timeFmt.getDate(start,'YYYY-MM-DD',0,true);
        	var endDate = timeFmt.getDate(end,'YYYY-MM-DD',0,true);
        	var startStr = timeFmt.getDate(start,'YY/MM/DD',0,true);
        	var endStr = timeFmt.getDate(end,'YY/MM/DD',0,true);
        	$scope.dropdown.selected = startStr + '-' + endStr;
        	if($scope.dropdown.inquiry.confirm){
        		$scope.dropdown.inquiry.confirm(startDate,endDate);
        	}
        }
	}
}
}]);
