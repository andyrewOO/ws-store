app.filter('paging', function() {
	return function(items, index, pageSize) {
		if (!items)
			return [];
		var offset = (index - 1) * pageSize;
		return items.slice(offset, offset + pageSize);
	}
}).filter('size', function() {
	return function(items) {
		if (!items)
			return 0;
		return items.length || 0;
	}
}).filter('flags', function() {
	return function(items) {
		if (!items)
			return [];
		if (items == 1)
			return "无效";
		if (items == 0)
			return "有效";
	}
}).filter('dict_type',['egfDict', function(egfDict) {
	return function(items,type) {
		var dict = egfDict.getdictindexinfo(type,items);
		return dict.dictDesc;
	}
}]);