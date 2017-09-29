'use strict'
angular.module('app').directive("commonDiv", function($templateCache){
	return{
		restrict : 'AE',
		scope : {
			inputItems : "=",
			formChild : "="
		},
		templateUrl: 'common-div.htm', 
		transclude:true,
		replace:true,
		link: function ( $scope, element, attrs ){
		}
	}
}).run(['$templateCache',function($templateCache){

}]);