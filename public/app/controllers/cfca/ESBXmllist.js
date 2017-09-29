'use strict';

app.controller('esbCtrl', [ '$rootScope', '$scope', '$modal', 'egfhttp', 
		function($rootScope, $scope, $modal, egfhttp) {
			console.info('esbCtrl');
			var updownLogUrl = "esb/esbXml"
			var esbXmlView = "esb/esbXml/view"
			var esbXmlViewResult = "esb/esbXml/view/result"
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function() {
				var subParam = {}
				subParam.SEQNO = $scope.obj.SEQNO
				subParam.SVCID = $scope.obj.SVCID
				subParam.SVCSCNID = $scope.obj.SVCSCNID
				subParam.APPLICATIONID = $scope.obj.APPLICATIONID
				subParam.CREATETIME = $scope.obj.CREATETIME
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
				egfhttp.sendHttp(esbXmlView, subParam, {
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
				egfhttp.sendHttp(esbXmlViewResult, subParam, {
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
		} ]);