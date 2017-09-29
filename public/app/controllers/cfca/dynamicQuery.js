'use strict';

app.controller('dynamicQuery', [ '$rootScope', '$scope', '$modal', 'egfhttp',
		function($rootScope, $scope, $modal, egfhttp) {
			console.info('dynamicQuery');
			var dynamicQuery = "cfca/dynamicquery"
			var downView = "cfca/down/view"
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function() {
				var subParam = {}
				subParam.applicationID = $scope.obj.applicationid
				subParam.oriapplicationid = $scope.obj.oriapplicationid
				subParam.transtype = $scope.obj.transtype
				subParam.flag = $scope.obj.flag
				subParam.account = $scope.obj.account
				
				console.info(subParam)

				egfhttp.sendHttp(dynamicQuery, subParam, {
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
		} ]);