	app.controller('FileViewCtrl', [ '$rootScope', '$scope','$modal', 'egfhttp', '$stateParams',function($rootScope, $scope, $modal, egfhttp, $stateParams) {
		console.info($stateParams)
		var updownLogUrl = "cfca/file"
		$scope.obj = {}
		$rootScope.search = {}
		
		var text = $stateParams.pkid;
		$scope.obj = {}
		$scope.obj.FILENAME =  text.substr(0,text.indexOf('_/'));
		$scope.obj.FILECONTENT =  text.substr(text.indexOf('/'));
		$scope.obj.applicationID =  text.substr(text.indexOf('_')+1);
		
		$scope.submitSearchParam = function() {
			var subParam = {}
			subParam.FILENAME = $scope.obj.FILENAME
//			subParam.FILECONTENT = $scope.obj.FILECONTENT
			
//			window.open(updownLogUrl+"?FILENAME="+$scope.obj.FILENAME)
			egfhttp.sendHttp(updownLogUrl, subParam, {
				"headers" : {
					"Content-Type" : "application/json"
				},
				"method" : "POST"
			}, function(data) {
				console.info(data);
				$scope.objs = {};
				$scope.objs.data = data;
				$scope.objs.colume = data.data;
				$scope.objs.page = {
					size : 5,
					index : 1
				};
			});
		}
		
		
		$scope.gotoback = function() {
			$rootScope.$state.go('app.menus', {title:"文件内容", desc:"文件内容", url:"views/cfca/Forcelist.html", files:"app/controllers/cfca/Forcelist.js", pkid:{}});
			}
	} ]);
