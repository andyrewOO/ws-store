'use strict';

app.controller('errorCtrl', [ '$rootScope', '$scope', '$modal', 'egfhttp', 
		function($rootScope, $scope, $modal, egfhttp) {
			console.info('errorCtrl');
			var updownLogUrl = "error/errorXml"
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function() {
				var subParam = {}
				subParam.APPLICATIONID = $scope.obj.applicationID
				subParam.TRADETYPE = $scope.obj.txcode
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
			$scope.detail = function(obj) {
				var scope = $rootScope.$new();
				scope.content = obj.INFO;
				var modalInstance = $modal.open({
					windowClass : "",
					templateUrl : 'views/cfca/xmlDetail.html',
					controller : 'detailCtrl',
					scope : scope,
					resolve : {}
				});
			}
			
			//查看反馈明细
//			$scope.detailResult = function(obj) {
//				var scope = $rootScope.$new();
//				scope.content = obj.RESPONSE;
//				var modalInstance = $modal.open({
//					windowClass : "",
//					templateUrl : 'views/cfca/xmlDetail.html',
//					controller : 'detailCtrl',
//					scope : scope,
//					resolve : {}
//				});
//			}
		} ]);