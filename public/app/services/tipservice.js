'use strict';
app.service('tip', function(toaster) {

	this.success = function(title, text) {
		toaster.pop("success", title, text);
	}

	this.info = function(title, text) {
		toaster.pop("info", title, text);
	}

	this.warning = function(title, text) {
		toaster.pop("warning", title, text);
	}

	this.error = function(title, text) {
		toaster.pop("error", title, text);
	}

});