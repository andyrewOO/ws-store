'use strict';

app.service('egfhttp', function($rootScope, $http) {
	this.sendHttp = function(reqUrl, data, settings, success) {
		var method = "POST";
		var headers = {
			'Content-Type' : 'application/x-www-form-urlencoded'
		};

		if (settings) {
			if (null != settings.method) {
				method = settings.method;
			}
			if (null != settings.headers) {
				headers = settings.headers;
			}
		}

		// 加载数据等待效果
		var loadingScreen = $('<div style="position:fixed;top:0;left:0;right:0;bottom:0;z-index:10000;background-color:gray;background-color:rgba(70,70,70,0.2);"><img style="position:absolute;top:50%;left:50%;" width="54px" height="54px" alt="请稍等" src="assets/img/loading.gif" /></div>').appendTo($('body')).hide();
		loadingScreen.show();// 显示

		$http({
			url : reqUrl,
			method : method,
			data : data,
			headers : headers
		}).success(function(data) {
			loadingScreen.hide();// 隐藏
			loadingScreen.remove();// 删除加载数据等待效果元素
			if (-1 == data.flag && "会话超时请重新登录" == data.msg) {
				alert(data.msg);
				// window.location.href=routeForAces;
				$rootScope.$state.go('login');
			}
			success(data);
		}).error(function(e) {
			loadingScreen.hide();
			loadingScreen.remove();
			alert("出错：" + e);
		});
	}
});

// 获取字典数据
app.service('egfDict', [ '$rootScope', 'egfhttp', '$cookieStore', function($rootScope, egfhttp, $cookieStore) {
	this.getdictinfo = function() {
		var finddictAllUrl = "publicdict/queryAll";// 查询全部字典数据
		$rootScope.dictinfo = [];// 定义字典数据全局变量
		$rootScope.dictState = 0;
		// 创建字典数据
		egfhttp.sendHttp(finddictAllUrl, null, {
			method : "GET"
		}, function(dt) {
			if (dt.flag == "0") {
				// alert("返回2："+dt.flag);
				var oldid = "";
				var newid = "";
				var objdict = [];
				angular.forEach(dt.data, function(item) {
					if (item.dictId != oldid && oldid != "") {
						$rootScope.dictinfo.push({
							dictId : oldid,
							list : objdict
						});
						objdict = [];
					}
					oldid = item.dictId;
					objdict.push({
						dictId : item.dictId,
						dictName : item.dictName,
						dictDesc : item.dictDesc
					});
				});
				$rootScope.dictinfo.push({
					dictId : oldid,
					list : objdict
				});
			}
			$cookieStore.remove("dictinfo");
			$cookieStore.put("dictinfo", $rootScope.dictinfo);
			$rootScope.dictState = 1;
			// return $rootScope.dictinfo;
		});
		$rootScope.$watch('dictState', function(newValue, oldValue) {
			if (newValue != oldValue) {
				// alert("dictState:"+$rootScope.dictState);
				return $rootScope.dictinfo;
			}
		});
	}
	// 获取指定类型的字典数据
	this.getdicttypeinfo = function(type) {
		$rootScope.dicts = {};
		$rootScope.dictinfo = $cookieStore.get("dictinfo");
		angular.forEach($rootScope.dictinfo, function(item) {
			if (item.dictId == type) {
				$rootScope.dicts = item.list;
				$cookieStore.remove("dicts");
				$cookieStore.put("dicts", $rootScope.dicts);
				// alert("getdicttypeinfo返回值:"+$rootScope.dicts);
			}
		});
		return $rootScope.dicts;
	}
	// 获取指定的字典数据
	this.getdictindexinfo = function(type, id) {
		$rootScope.dicts = {};
		$rootScope.dictinfo = $cookieStore.get("dictinfo");
		angular.forEach($rootScope.dictinfo, function(item) {
			if (item.dictId == type) {
				angular.forEach(item.list, function(items) {
					if (items.dictName == id) {
						$rootScope.dicts = items;
						$cookieStore.remove("dicts");
						$cookieStore.put("dicts", $rootScope.dicts);
					}
				});
			}
		});
		return $rootScope.dicts;
	}

	this.getHttpdictInfoById = function(TypeName, success) {
		var findDictByIdUrl = "publicdict/queryByDictId";
		var dictId = 0;
		switch (TypeName.toUpperCase()) {
		case 'GATHER_TYPE':
			dictId = 1;
			break;
		case 'FILTER_TYPE':
			dictId = 2;
			break;
		case 'BUS_TYPE':
			dictId = 3;
			break;
		case 'RECEIVE_TYPE':
			dictId = 4;
			break;
		case 'PROCESS_TYPE':
			dictId = 5;
			break;
		case 'TOPIC_ATTR':
			dictId = 6;
			break;
		case 'FILTER_ATTR':
			dictId = 7;
			break;
		case 'SOURCE_ATTR':
			dictId = 8;
			break;
		case 'BUS_ATTR':
			dictId = 9;
			break;
		case '_':
			dictId = 10;
			break;
		case 'FILTER_ATTR':
			dictId = 11;
			break;
		case 'DB_TYPE':
			dictId = 12;
			break;
		}
		var reqUrl = findDictByIdUrl + "?dictid=" + dictId;
		egfhttp.sendHttp(reqUrl, null, {
			method : "GET"
		}, success);
	}

	// 根据字典ID和字典代码查询字典信息服务
	this.getHttpDictInfoByIdByCode = function(dictId, dictName, success) {
		var reqUrl = "publicdict/queryByDictIdByDictName" + "?dictid=" + dictId + "&dictname=" + dictName;
		egfhttp.sendHttp(reqUrl, null, {
			method : "GET"
		}, success);
	}
} ]);

app.service('htmlTemplate', function($templateCache) {
	this.refreshUpdateHtmlTemplate = function(templateUrl) {
		var template = '';
		template += '<div class="row" ng-repeat="inputItem in inputItems">' + '<div class="col-sm-12">' + '<div class="form-group">' + '<label>{{inputItem.dictName}}<font color="green">({{inputItem.dictDesc}})</font></label>' + '<span class="input-icon icon-right">'
				+ '<input type="text" class="form-control" name="{{inputItem.dictName}}" id="{{inputItem.dictName}}"  ng-model="formChild[inputItem.dictName]" >' + '</span>' + '</div>' + '</div>' + '</div>';
		template = '<form role="form" name="formchild" id="formchild">' + template + '</form>';
		$templateCache.put(templateUrl, template);
	};
	this.refreshAddHtmlTemplate = function(templateUrl) {
		var template = '';
		template += '<div class="row" ng-repeat="inputItem in inputItems">' + '<div class="col-sm-12">' + '<div class="form-group">' + '<label>{{inputItem.dictName}}<font color="green">({{inputItem.dictDesc}})</font></label>' + '<span class="input-icon icon-right">'
				+ '<input type="text" class="form-control" name="{{inputItem.dictName}}" id="{{inputItem.dictName}}"  ng-model="formChild[inputItem.dictName]" ng-init="formChild[inputItem.dictName]=inputItem.dictDefaultValue" >' + '</span>' + '</div>' + '</div>' + '</div>';
		template = '<form role="form" name="formchild" id="formchild">' + template + '</form>';
		$templateCache.put(templateUrl, template);
	};
});

	/*
	 * 创建动态菜单
	 */
	app.controller('MenuTreeShowCtrl', ['$http', '$scope', 'egfhttp', function($http, $scope, egfhttp) {
		var findmenutreeAllUrl = "menutree/menutreeAll";	// 查询所有菜单

		egfhttp.sendHttp(findmenutreeAllUrl,null,{method:"GET"},function(dt){
			console.info(dt)
			$scope.objs = {};
			$scope.objs.colume = dt.data;
		});
	}]);

// 弹窗处理
app.controller('detailCtrl', function($scope, $modalInstance) {
	$scope.ok = function() {
		$modalInstance.close($scope);
	};

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};
});