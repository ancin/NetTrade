var Custom = function() {
	var a = function(b) {
		alert(b)
	};
	return {
		init : function() {
		},
		doSomeStuff : function() {
			a()
		}
	}
}();