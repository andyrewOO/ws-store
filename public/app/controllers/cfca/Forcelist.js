'use strict';

app.controller('allLogCtrl', [ '$rootScope', '$scope','egfhttp',
		function($rootScope, $scope, egfhttp) {
			console.info('updownLogCtrl');
			var allLogUrl = "cfca/force"
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function() {
				var subParam = {}
				subParam.applicationID = $scope.obj.applicationID
				subParam.txcode = $scope.obj.txcode
				subParam.status = $scope.obj.status
				console.info(subParam)

				egfhttp.sendHttp(allLogUrl, subParam, {
					"headers" : {
						"Content-Type" : "application/json"
					},
					"method" : "POST"
				}, function(data) {
					console.info(data);
					$scope.objs = {};
					$scope.objs.colume = data.data;
					$scope.objs.page = {
						size : 5,
						index : 1
					};
				});
			}

			// 配置转向查看页面的资源对象
			$scope.viewres = {
				label : '查看文件',
				templateUrl : 'views/cfca/Forceview.html',
				loadfiles : 'app/controllers/cfca/Forceview.js',
			}
		} ]);