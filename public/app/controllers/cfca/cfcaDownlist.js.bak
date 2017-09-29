'use strict';

app.controller('cfcaDownCtrl', [ '$rootScope', '$scope', '$modal', 'egfhttp',
		function($rootScope, $scope, $modal, egfhttp) {
			console.info('updownLogCtrl');
			var updownLogUrl = "cfca/down"
			var downView = "cfca/down/view"
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function() {
				var subParam = {}
				subParam.transSerialNumber = $scope.obj.transSerialNumber
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
			$scope.detail = function(objid) {
				var scope = $rootScope.$new();
				var subParam = {}
				subParam.id = objid;
				egfhttp.sendHttp(downView, subParam, {
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