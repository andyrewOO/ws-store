'use strict';
// Profile Controller
app.controller('LoginCtrl', [ '$rootScope', '$scope', '$http', '$cookieStore', 'skylineService', function($rootScope, $scope, $http, $cookieStore, skylineService) {
	// 回车登入
	$(document).ready(function() {
		$("#password").keydown(function(e) {
			var curKey = e.which;
			if (curKey == 13) {
				$("#submit").click();
				return false;
			}
		});
	});

	$cookieStore.remove("userInfo");// 清除用户信息

	$scope.login = function() {
		var loginname = $("#loginName").val();
		var password = $("#password").val();
		if (loginname == "" || password == "") {
			$scope.showError = false;
			$scope.showWarning = true;
			return;
		}

		skylineService.invokeAsync("sysLogin", {
			pbean : {
				userid : loginname,
				password : password
			}
		}).then(function(rdata) {
			console.info(rdata);
			if (rdata.isSucceeded) {
				if (rdata.retVals.rst) {
					$cookieStore.put("userInfo", rdata.retVals.rst);// cookie保存用户信息
					$rootScope.$state.go('app.blank');
				} else {
					$scope.showError = true;
					$scope.showWarning = false;
				}
			} else {
				alert("获取数据记录失败=>\n" + rdata.message);
			}
		});
	}
	$scope.hide = function() {
		$scope.showError = false;
		$scope.showWarning = false;
	}
} ]);