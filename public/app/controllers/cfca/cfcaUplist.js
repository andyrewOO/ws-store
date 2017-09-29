'use strict';

app.controller('cfcaUpCtrl', [ '$rootScope', '$scope', '$modal', 'egfhttp', 
		function($rootScope, $scope, $modal, egfhttp) {
			console.info('updownLogCtrl');
			var updownLogUrl = "cfca/up"
			var upXmlView = "cfca/up/view"
			var upXmlResultView = "cfca/up/view/result"
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function() {
				var subParam = {}
				subParam.applicationID = $scope.obj.applicationID
				subParam.txcode = $scope.obj.txcode
				subParam.status = $scope.obj.status
				console.info(subParam)

				egfhttp.sendHttp(updownLogUrl, subParam, {
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

			// 查看详细
			$scope.detail = function(id) {
				var scope = $rootScope.$new();
				var subParam = {}
				subParam.id = id
				egfhttp.sendHttp(upXmlView, subParam, {
					"headers" : {
						"Content-Type" : "application/json"
					},
					"method" : "POST"
				}, function(data) {
					console.info(data);
					scope.content = data.data;
					console.info(scope.content);
				});
				var modalInstance = $modal.open({
					windowClass : "",
					templateUrl : 'views/cfca/xmlDetail.html',
					controller : 'detailCtrl',
					scope : scope,
					resolve : {}
				});
			}
			
			//查看反馈明细
			$scope.detailResult = function(id) {
				var scope = $rootScope.$new();
				var subParam = {}
				subParam.id = id
				egfhttp.sendHttp(upXmlResultView, subParam, {
					"headers" : {
						"Content-Type" : "application/json"
					},
					"method" : "POST"
				}, function(data) {
					console.info(data);
					scope.content = data.data;
				});
				var modalInstance = $modal.open({
					windowClass : "",
					templateUrl : 'views/cfca/xmlDetail.html',
					controller : 'detailCtrl',
					scope : scope,
					resolve : {}
				});
			}
		} ]);