angular.module('app').directive("flotRealtimeChart", function(){
    	return{
            restrict: 'AE',
            scope : 
            {
            	flotTitle : '=',
            	nodeModel01 : '=',
            	nodeModel02 : '=',
            	nodeModel03 : '=',
            	axisTick: '=',
            	markingsPosition: '=',
            	totalPoints : '=',
            	refreshTwink   : '='
            },
    		link : function($scope, ele){
    			var options;
                var getSeriesObj = function(){
                    return [
                        {
                            data: getRunningData($scope.nodeModel01),
                            label: "输入流量",
                            lines: {
                                show: true,
                                lineWidth: 1,
                                fill: false,
                                fillColor: {
                                    colors: [
                                        {
                                            opacity: 0
                                        }, {
                                            opacity: 1
                                        }
                                    ]
                                },
                                steps: false
                            },
                            shadowSize: 0
                        }, {
                            data: getRunningData($scope.nodeModel02),
                            label: "过滤流量",
                            lines: {
                            	show: true,
                                lineWidth: 1,
                                fill: true,
                                fillColor: {
                                    colors: [
                                        {
                                            opacity: 0
                                        }, {
                                            opacity: 1
                                        }
                                    ]
                                },
                                steps: false
                            },
                            shadowSize: 0
                        }, {
                            data: getRunningData($scope.nodeModel03),
                            label: "分发流量",
                            lines: {
                            	show: true,
                                lineWidth: 1,
                                fill: true,
                                fillColor: {
                                    colors: [
                                        {
                                            opacity: 0
                                        }, {
                                            opacity: 1
                                        }
                                    ]
                                },
                                steps: false
                            },
                            shadowSize: 0
                        }
                    ];
                };
                
                var getRunningData = function(dataArray){
                    var res = [];
                    var zeroCounts = $scope.totalPoints - dataArray.length;
                    for (var i = 0; i < $scope.totalPoints; ++i) {
                    	if(i < zeroCounts){
                    		res.push([i, 0]);
                    	}else{
                    		res.push([i, dataArray[i-zeroCounts]]);
                    	}
                    }
                    return res;
                };
                
                var updateOptions = function(){
                	return {
                		legend: {
            				show: true,
            				noColumns: 3
            			},
                        yaxis: {
                            color: '#f3f3f3',
                            min: 0,
                            ticks : [0,10,20,30,40,50,60,70,80,90,100],
                            tickFormatter: function(axis) {
                                return axis.toString();
                            }
                        },
                        xaxis: {
                            color: '#f3f3f3',
                            min: 0,
                            ticks : [0,60,120,180,239],
                            tickFormatter: function(axis) {
                            	var i = ((axis)/60);
                            	if(axis == 239) i=4;
                            	return $scope.axisTick[i];
                            }
                        },
                        grid: {
                            hoverable: true,
                            clickable: false,
                            aboveData: false,
            				backgroundColor: { colors: [ "#fff", "#f5f5f5" ] },
            				borderWidth: {
            					top: 1,
            					right: 1,
            					bottom: 2,
            					left: 2
            				},
            				markings: [{ color: "#000", lineWidth: 1, xaxis: { from: $scope.markingsPosition, to: $scope.markingsPosition } }]
                        },
                    };
                }
                
                var plot = $.plot(ele[0], getSeriesObj(), updateOptions());
                
        		$("<div id='tooltip'></div>").css({
        			position: "absolute",
        			display: "none",
        			border: "1px solid #eee",
        			padding: "2px",
        			"border-radius":"5px",
        			"background-color": "#eff",
        			opacity: 0.80
        		}).appendTo("body");
                
        		$(ele[0]).bind("plothover", function (event, pos, item) {
    				if (item) {
    					var x = item.datapoint[0].toFixed(2),
    						y = item.datapoint[1].toFixed(2);

    					$("#tooltip").html(item.series.label + " of " + x + " = " + y)
    						.css({top: item.pageY+5, left: item.pageX+5})
    						.fadeIn(200);
    				} else {
    					$("#tooltip").hide();
    				}
        		});
                
                var update = function() {
                	options = plot.getOptions();
                    plot.setData(getSeriesObj());
                    plot.draw();
                };
                $scope.$watch( 'refreshTwink' , function( newVal ) {  
                        update();                                                                                                                
                }); 
                $scope.$watch( 'markingsPosition' , function( newVal ) {  
                	plot = $.plot(ele[0], getSeriesObj(), updateOptions());                                                                                                               
                }); 
    		}
    	};
});