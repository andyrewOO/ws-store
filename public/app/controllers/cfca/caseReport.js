'use strict';

app.controller('caseReport', [ '$rootScope', '$scope', '$modal', 'egfhttp',
		function($rootScope, $scope, $modal, egfhttp) {
			console.info('caseReport');
			var caseReportURL = "cfca/caseReport"
			var caseReportView = "cfca/caseReport/view"
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function() {
				var subParam = {}
				subParam.applicationid = $scope.obj.applicationid
				subParam.transserialnumber = $scope.obj.transserialnumber
				subParam.result = $scope.obj.result
				
				console.info(subParam)

				egfhttp.sendHttp(caseReportURL, subParam, {
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
				egfhttp.sendHttp(caseReportView, subParam, {
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