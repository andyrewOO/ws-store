app.controller('ForceViewCtrl', [ '$rootScope', '$scope', '$modal',
		'$stateParams', 'egfhttp',
		function($rootScope, $scope, $modal, $stateParams, egfhttp) {
			console.info($stateParams)
			var updownLogUrl = "cfca/force2"
			$scope.obj = {}
			$rootScope.search = {}

			var text = $stateParams.pkid;
			$scope.obj = {}
			$scope.obj.applicationID = text.substr(0, text.indexOf('_'));
			$scope.obj.text = text.substr(text.indexOf('_') + 1);

			var subParam = {}
			subParam.applicationID = $scope.obj.applicationID

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

			// 查看详细
			$scope.detail = function(obj) {
				var scope = $rootScope.$new();
				scope.obj = obj
				var modalInstance = $modal.open({
					windowClass : "",
					templateUrl : 'views/cfca/Fileview.html',
					controller : 'detailCtrl',
					scope : scope,
					resolve : {}
				});
			}
			$scope.gotoback = function() {
				$rootScope.$state.go('app.menus', {
					title : "强制措施查询",
					desc : "强制措施查询",
					url : "views/cfca/Forcelist.html",
					files : "app/controllers/cfca/Forcelist.js",
					pkid : {}
				});
			}
		} ]);
