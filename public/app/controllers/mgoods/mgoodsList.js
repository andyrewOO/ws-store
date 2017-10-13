'use strict';

app.controller('mgoodsListCtrl', [ '$rootScope', '$scope', '$modal', 'egfhttp', 
		function($rootScope, $scope, $modal, egfhttp) {
			console.info('mgoodsListCtrl')
			// 列表获取url
			var mgoodsListUrl = "search/mgoods"
			// 商品详情url
			var mgoodsDetailUrl = "cfca/up/view"
				
			$scope.obj = {}
			$rootScope.search = {}

			$scope.submitSearchParam = function(index, pageSize) {
				var mgoods = {
						kind:$scope.obj.kind,
						place:$scope.obj.place,
						description:$scope.obj.description,
						status:$scope.obj.status
				}
				var page = {
						start:index,
		        		size:pageSize
				}
				var subParam = {}
				subParam.mgoods = mgoods
				subParam.page = page
				console.info(subParam)

				egfhttp.sendHttp(mgoodsListUrl, subParam, {
					"headers" : {"Content-Type" : "application/json"},
					"method" : "POST"
				}, function(data) {
					console.info(data);
					$scope.objs = {};
					if(data.total>0){
						$scope.objs.total = data.total
						$scope.objs.colume = data.data.result
					}
					$scope.objs.page = {
						size : data.data.size,
						index : data.data.start
					};
					$scope.objs.total = data.data.sum
				});
			}
			
			//分页
			$scope.showPage = (function() {
				var index = $scope.objs.page.index
				var size = $scope.objs.page.size
				$scope.submitSearchParam(index, size)
			})

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
		} ]);