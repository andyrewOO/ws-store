/* 雷达图
 * RadarOption chartData 格式:
{
	title:{text:'预算 vs 开销（Budget vs spending）',subtext:'纯属虚构'},
	indicatorData:[{ text: '销售（sales）', max: 6000},{ text: '管理（Administration）', max: 16000},{ text: '信息技术（Information Techology）', max: 30000},{ text: '客服（Customer Support）', max: 38000},{ text: '研发（Development）', max: 52000},{ text: '市场（Marketing）', max: 25000}],
	seriesData:[{value : [4300, 10000, 28000, 35000, 50000, 19000],name : '预算分配（Allocated Budget）'},{value : [5000, 14000, 28000, 31000, 42000, 21000],name : '实际开销（Actual Spending）'}]
}*/

app.directive('egfEchartsRadar',['chartWeightService',function(chartWeightService){
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
			    title : {},
			    tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			        orient : 'vertical',
			        x : 'right',
			        y : 'bottom',
			        data:[]
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: false},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    polar : [
			       {
			           indicator : [
			            ]
			        }
			    ],
			    calculable : true,
			    series : []
			};
		},
		link : function($scope,iElement,iAttrs,controller){
			$scope.updateCmd = undefined;
			$scope.optionTemp = controller.option;
			$scope.option = undefined;
			$scope.optionTemp.toolbox.show = iAttrs.toolbox == "false" ? false : true;
			// 初始化按钮
			chartWeightService.initTitleBar(iElement,$scope.button);
			$scope.$watch('chartData',function(newVal,oldVal){
				if(newVal != undefined && angular.isArray(newVal.seriesData)){
					$scope.optionTemp.legend.data = [];		//清除legend的数据信息
					$scope.optionTemp.series = [];			//清除series信息
					$scope.optionTemp.series.push({
						name:newVal.title.text ? newVal.title.text : '',
						type:'radar',
						data:newVal.seriesData
					});
					newVal.seriesData.forEach(function(item,index){
						var legendName = item.name ? item.name : '未知名称'+index;
						$scope.optionTemp.legend.data.push(legendName);
					});
					$scope.optionTemp.polar[0].indicator = angular.isArray(newVal.seriesData) && newVal.indicatorData ? newVal.indicatorData : [];
					$scope.optionTemp.title = newVal.title;
					$scope.option = $scope.optionTemp;
				}
			},true);
		}
	}
}]);

/* 地图
 * mapOption chartData 格式:
{
	mapType: 'china',
	title:{text: 'iphone销量',subtext: '纯属虚构'},
	dataRange:{min:0,max:2500,text:['高'，'低']},
	seriesData:[
        {
            name: 'iphone3',
            data:[
                {name: '北京',value: Math.round(Math.random()*1000)},
                {name: '香港',value: Math.round(Math.random()*1000)},
                {name: '澳门',value: Math.round(Math.random()*1000)}
            ]
        },
        {
            name: 'iphone4',
            data:[
                {name: '北京',value: Math.round(Math.random()*1000)},
                {name: '天津',value: Math.round(Math.random()*1000)},
                {name: '上海',value: Math.round(Math.random()*1000)},
                {name: '澳门',value: Math.round(Math.random()*1000)}
            ]
        },
        {
            name: 'iphone5',
            data:[
                {name: '北京',value: Math.round(Math.random()*1000)},
                {name: '香港',value: Math.round(Math.random()*1000)}
            ]
        }
    ]
}*/

app.directive('egfEchartsMap',['chartWeightService',function(chartWeightService){
	return {
		restrict : 'AE',
		scope : {
			chartName : "@",
			chartData : "=",
			chartEntry :"=?",
			theme: "@",
			button : "@",
			dropdown:"="
		},
		templateUrl : 'views/charts/echartsTemplate.html',
		transclude : true,
		controller : function($scope){
			this.option = {
			    title : {
			    	x:'center',
			    	y:'top'
			    },
			    tooltip : {
			        trigger: 'item'
			    },
			    legend: {
			        orient: 'vertical',
			        x:'left',
			        data:[]
			    },
			    dataRange: {
			        x: 'left',
			        y: 'bottom',
			        text:['高','低'],           // 文本，默认为数值文本
			        calculable : true
			    },
			    toolbox: {
			        show: true,
			        orient : 'vertical',
			        x: 'right',
			        y: 'center',
			        feature : {
			            mark : {show: true},
			            dataView : {show: true, readOnly: false},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    roamController: {
			        show: true,
			        x: 'right',
			        mapTypeControl: {
			            'china': true
			        }
			    },
			    series : []
			};
				                    
		},
		link : function($scope,iElement,iAttrs,controller){
			$scope.updateCmd = undefined;
			$scope.optionTemp = controller.option;
			$scope.option = undefined;
			$scope.optionTemp.toolbox.show = iAttrs.toolbox == "false" ? false : true;
			// 初始化按钮
			chartWeightService.initTitleBar(iElement,$scope.button);
			$scope.$watch('chartData',function(newVal,oldVal){
				if(newVal != undefined && angular.isArray(newVal.seriesData)){
					$scope.optionTemp.legend.data = [];		//清除legend的数据信息
					$scope.optionTemp.series = [];			//清除series信息
					newVal.seriesData.forEach(function(item,index){
						var legendName = item.name ? item.name : '未知名称'+index;
						var itemData = item.data ? item.data : [];
						$scope.optionTemp.legend.data.push(legendName);
						$scope.optionTemp.series.push({
							name:legendName,
							type:'map',
							mapType:newVal.mapType,
							selectedMode: 'single', 
							roam:false,
							itemStyle:{
				                normal:{label:{show:true}},
				                emphasis:{label:{show:true}}
				            },
							data:itemData
						});
					});
					angular.extend($scope.optionTemp.title, newVal.title);
					angular.extend($scope.optionTemp.dataRange, newVal.dataRange);
					$scope.option = $scope.optionTemp;
				}
			},true);
		}
	}
}]);

/* 仪表盘
 * GaugeOption chartData 格式:
{
	formatter: "{a} <br/>{b} : {c}%",
	seriesData:[{name:'业务指标',data:[{value: 50, name: '完成率'}]}]
}*/

app.directive('egfEchartsGauge',['chartWeightService',function(chartWeightService){
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
			    tooltip : {},
			    toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    series : []
			};
		},
		link : function($scope,iElement,iAttrs,controller){
			$scope.updateCmd = undefined;
			$scope.optionTemp = controller.option;
			$scope.option = undefined;
			$scope.optionTemp.toolbox.show = iAttrs.toolbox == "false" ? false : true;
			// 初始化按钮
			chartWeightService.initTitleBar(iElement,$scope.button);
			$scope.$watch('chartData',function(newVal,oldVal){
				if(newVal != undefined && angular.isArray(newVal.seriesData)){
					$scope.optionTemp.series = [];			//清除series信息
					newVal.seriesData.forEach(function(item,index){
						var legendName = item.name ? item.name : '未知名称'+index;
						var itemData = item.data ? item.data : [];
						$scope.optionTemp.series.push({
							name:legendName,
							type:'gauge',
							data:itemData
						});
					});
					$scope.option = $scope.optionTemp;
				}
			},true);
		}
	}
}]);

/* 力导向分布图----只有echarts2.0支持该图系（注释掉的代码为2.0的力导向分布图）
 * 关系图----echarts3.0支持图系
 * forceOption chartData 格式:
{
	tooltip : {
    	formatter: "{a}:{b}"
    },
	seriesData:[{
		name : "人物关系",
		categories : [
            {
                name: '人物'
            },
            {
                name: '家人'
            },
            {
                name:'朋友'
            }
        ],
		nodes:[
            {category:0, name: '乔布斯', value : 10, label: '乔布斯\n（主要）'},
            {category:1, name: '丽萨-乔布斯',value : 2},
            {category:1, name: '保罗-乔布斯',value : 3},
            {category:1, name: '克拉拉-乔布斯',value : 3},
            {category:1, name: '劳伦-鲍威尔',value : 7},
            {category:2, name: '史蒂夫-沃兹尼艾克',value : 5},
            {category:2, name: '奥巴马',value : 8},
            {category:2, name: '比尔-盖茨',value : 9},
            {category:2, name: '乔纳森-艾夫',value : 4},
            {category:2, name: '蒂姆-库克',value : 4},
            {category:2, name: '龙-韦恩',value : 1},
        ],
        links : [
            {source : '丽萨-乔布斯', target : '乔布斯', weight : 1, name: '女儿'},
            {source : '保罗-乔布斯', target : '乔布斯', weight : 2, name: '父亲'},
            {source : '克拉拉-乔布斯', target : '乔布斯', weight : 1, name: '母亲'},
            {source : '劳伦-鲍威尔', target : '乔布斯', weight : 2},
            {source : '史蒂夫-沃兹尼艾克', target : '乔布斯', weight : 3, name: '合伙人'},
            {source : '奥巴马', target : '乔布斯', weight : 1},
            {source : '比尔-盖茨', target : '乔布斯', weight : 6, name: '竞争对手'},
            {source : '乔纳森-艾夫', target : '乔布斯', weight : 1, name: '爱将'},
            {source : '蒂姆-库克', target : '乔布斯', weight : 1},
            {source : '龙-韦恩', target : '乔布斯', weight : 1},
            {source : '克拉拉-乔布斯', target : '保罗-乔布斯', weight : 1},
            {source : '奥巴马', target : '保罗-乔布斯', weight : 1},
            {source : '奥巴马', target : '克拉拉-乔布斯', weight : 1},
            {source : '奥巴马', target : '劳伦-鲍威尔', weight : 1},
            {source : '奥巴马', target : '史蒂夫-沃兹尼艾克', weight : 1},
            {source : '比尔-盖茨', target : '奥巴马', weight : 6},
            {source : '比尔-盖茨', target : '克拉拉-乔布斯', weight : 1},
            {source : '蒂姆-库克', target : '奥巴马', weight : 1}
        ]}]
}*/

app.directive('egfEchartsForce',['chartWeightService',function(chartWeightService){
	return {
		restrict : 'AE',
		scope : {
			chartName : "@",
			chartData : "=",
			theme: "@",
			button : "@"
		},
		templateUrl : 'views/charts/echartsTemplate.html',
		transclude : true,
		controller : function($scope){
			this.option = {
			    title : {
			        x:'right',
			        y:'bottom'
			    },
			    tooltip : {
			        trigger: 'item'
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            restore : {show: true},
			            magicType: {show: true, type: ['force', 'chord']},
			            saveAsImage : {show: true}
			        }
			    },
			    legend: {
			        x: 'left',
			        data:[]
			    },
			    series : [
			        
			    ]
			};
		},
		link : function($scope,iElement,iAttrs,controller){
			$scope.updateCmd = undefined;
			$scope.optionTemp = controller.option;
			$scope.option = undefined;
			$scope.optionTemp.toolbox.show = iAttrs.toolbox == "false" ? false : true;
			// 初始化按钮
			chartWeightService.initTitleBar(iElement,$scope.button);
			$scope.$watch('chartData',function(newVal,oldVal){
				if(newVal != undefined && angular.isArray(newVal.seriesData)){
					$scope.optionTemp.series = [];			//清除series信息
					newVal.seriesData.forEach(function(item,index){
						var legendName = item.name ? item.name : '未知名称'+index;
						var nodes = item.nodes ? item.nodes : [];
						var links = item.links ? item.links : [];
						$scope.optionTemp.title.text = legendName;
						
						var categories = item.categories ;
						if(categories && angular.isArray(categories)){
							categories.forEach(function(categorie,index){
								$scope.optionTemp.legend.data.push(categorie.name);
							})
						}
						$scope.optionTemp.series.push({
//							type:'force',
//				            name : legendName,
//				            ribbonType: false,
//				            categories:categories,
//				            itemStyle: {
//				                normal: {
//				                    label: {
//				                        show: true,
//				                        textStyle: {
//				                            color: '#333'
//				                        }
//				                    },
//				                    nodeStyle : {
//				                        brushType : 'both',
//				                        borderColor : 'rgba(255,215,0,0.4)',
//				                        borderWidth : 1
//				                    },
//				                    linkStyle: {
//				                        type: 'curve'
//				                    }
//				                },
//				                emphasis: {
//				                    label: {
//				                        show: false
//				                        // textStyle: null      // 默认使用全局文本样式，详见TEXTSTYLE
//				                    },
//				                    nodeStyle : {
//				                        //r: 30
//				                    },
//				                    linkStyle : {}
//				                }
//				            },
//				            useWorker: false,
//				            minRadius : 15,
//				            maxRadius : 25,
//				            gravity: 1.1,
//				            scaling: 1.1,
//				            roam: 'move',
//				            nodes:nodes,
//				            links:links
							name: legendName,
			                type: 'graph',
			                layout: 'force',
			                data: nodes,
			                links: links,
			                categories: categories,
			                roam: true,
			                label: {
			                    normal: {
			                        position: 'right'
			                    }
			                },
			                force: {
			                    repulsion: 1500
			                }
						});
					});
					angular.extend($scope.optionTemp.tooltip, newVal.tooltip);
					console.log($scope.optionTemp);
					$scope.option = $scope.optionTemp;
				}
			},true);
		}
	}
}]);
