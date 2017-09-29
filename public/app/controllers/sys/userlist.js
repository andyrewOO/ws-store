'use strict';

app.controller('UsersboxesCtrl', [ '$rootScope', '$scope', '$http', 'egfhttp', 'skyline', function($rootScope, $scope, $http, egfhttp,skyline) {
	var finduserUrl = "sysUser"; // 查询所有用户
	var adduserUrl = "user/add1"; // 添加一个用户
	var updateuserUrl = "user/edit1"; // 修改用户信息
	var deleteuserUrl = "user/del1"; // 删除一个用户
	var finduserByIdUrl = "user/queryById1";// 根据id查找用户
	// view加载完成后，执行一次QueryAll操作
	egfhttp.sendHttp(finduserUrl, null, {
		method : "GET"
	}, function(data) {
		$scope.objs = {};
		if (0 == data.flag) {
			$scope.objs.colume = data.data;
			$scope.objs.page = {
				size : 5,
				index : 1
			};
		}
	});

	// 新增按钮
	$scope.userAdd = function() {
		alert("ss");
		// $rootScope.$state.go('app.usersboxesAdd');
		skyline.addNumber(123, 123, function(rdata) {
			alert(rdata.retVals.retval);
		});
	};

	// 修改按钮
	$scope.edit = function(id) {
		$rootScope.$state.go('app.usersboxesEdit', {
			id : id
		});
	};

	// 删除按钮
	$scope.del = function(id) {
		if (confirm("确认删除？") == false) {
			return;
		}
		var form = "id=" + id + "&_method=delete";
		// var form = $('form').serialize()+"&id="+id;
		// alert(form);
		egfhttp.sendHttp(deleteuserUrl, form, null, function(data) {
			if (0 == data.flag) {
				alert("删除成功!");
				egfhttp.sendHttp(finduserUrl, null, {
					method : "GET"
				}, function(data) {
					$scope.objs = {};
					if (0 == data.flag) {
						$scope.objs.colume = data.data;
						$scope.objs.page = {
							size : 5,
							index : 1
						};
					}
				});
			} else {
				alert("删除失败!");
			}
		});
	}

	// 刷新按钮，即，查询所有用户
	$scope.processForm = function() {
		var form = $('form').serialize(); // 获取form表单参数，并拼接url
		// alert(form);
		egfhttp.sendHttp(finduserUrl, form, {
			method : "GET"
		}, function(data) {
			$scope.objs = {};
			if (0 == data.flag) {
				$scope.objs.colume = data.data;
				$scope.objs.page = {
					size : 5,
					index : 1
				};
			}
			checkid = -1;
		});
	};

} ]);
