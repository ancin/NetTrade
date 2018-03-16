var TableAdvanced = function() {
	var b = function() {
		function c(g, j) {
			var h = g.fnGetData(j);
			var i = "<table>";
			i += "<tr><td>Platform(s):</td><td>" + h[2] + "</td></tr>";
			i += "<tr><td>Engine version:</td><td>" + h[3] + "</td></tr>";
			i += "<tr><td>CSS grade:</td><td>" + h[4] + "</td></tr>";
			i += "<tr><td>Others:</td><td>Could provide a link here</td></tr>";
			i += "</table>";
			return i
		}
		var e = document.createElement("th");
		var f = document.createElement("td");
		f.innerHTML = '<span class="row-details row-details-close"></span>';
		$("#sample_1 thead tr").each(function() {
			this.insertBefore(e, this.childNodes[0])
		});
		$("#sample_1 tbody tr").each(function() {
			this.insertBefore(f.cloneNode(true), this.childNodes[0])
		});
		var d = $("#sample_1").dataTable({
			aoColumnDefs : [ {
				bSortable : false,
				aTargets : [ 0 ]
			} ],
			aaSorting : [ [ 1, "asc" ] ],
			aLengthMenu : [ [ 5, 15, 20, -1 ], [ 5, 15, 20, "All" ] ],
			iDisplayLength : 10,
		});
		jQuery("#sample_1_wrapper .dataTables_filter input").addClass(
				"form-control input-small");
		jQuery("#sample_1_wrapper .dataTables_length select").addClass(
				"form-control input-small");
		jQuery("#sample_1_wrapper .dataTables_length select").select2();
		$("#sample_1").on(
				"click",
				" tbody td .row-details",
				function() {
					var g = $(this).parents("tr")[0];
					if (d.fnIsOpen(g)) {
						$(this).addClass("row-details-close").removeClass(
								"row-details-open");
						d.fnClose(g)
					} else {
						$(this).addClass("row-details-open").removeClass(
								"row-details-close");
						d.fnOpen(g, c(d, g), "details")
					}
				})
	};
	var a = function() {
		var c = $("#sample_2").dataTable({
			aoColumnDefs : [ {
				aTargets : [ 0 ]
			} ],
			aaSorting : [ [ 1, "asc" ] ],
			aLengthMenu : [ [ 5, 15, 20, -1 ], [ 5, 15, 20, "All" ] ],
			iDisplayLength : 10,
		});
		jQuery("#sample_2_wrapper .dataTables_filter input").addClass(
				"form-control input-small");
		jQuery("#sample_2_wrapper .dataTables_length select").addClass(
				"form-control input-small");
		jQuery("#sample_2_wrapper .dataTables_length select").select2();
		$('#sample_2_column_toggler input[type="checkbox"]').change(function() {
			var d = parseInt($(this).attr("data-column"));
			var e = c.fnSettings().aoColumns[d].bVisible;
			c.fnSetColumnVis(d, (e ? false : true))
		})
	};
	return {
		init : function() {
			if (!jQuery().dataTable) {
				return
			}
			b();
			a()
		}
	}
}();