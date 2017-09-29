$scope.userlist = function(obj) {
	skylineService.gotoPage("views/sys/rolechoose.html",obj);
}
$scope.roleauth = function(obj) {
	skylineService.gotoPage("views/sys/menuAuth.html",obj);
}