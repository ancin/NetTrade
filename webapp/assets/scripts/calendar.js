var Calendar = function() {
	return {
		init : function() {
			Calendar.initCalendar()
		},
		initCalendar : function() {
			if (!jQuery().fullCalendar) {
				return
			}
			var b = new Date();
			var g = b.getDate();
			var a = b.getMonth();
			var i = b.getFullYear();
			var c = {};
			if (App.isRTL()) {
				if ($("#calendar").parents(".portlet").width() <= 720) {
					$("#calendar").addClass("mobile");
					c = {
						right : "title, prev, next",
						center : "",
						right : "agendaDay, agendaWeek, month, today"
					}
				} else {
					$("#calendar").removeClass("mobile");
					c = {
						right : "title",
						center : "",
						left : "agendaDay, agendaWeek, month, today, prev,next"
					}
				}
			} else {
				if ($("#calendar").parents(".portlet").width() <= 720) {
					$("#calendar").addClass("mobile");
					c = {
						left : "title, prev, next",
						center : "",
						right : "today,month,agendaWeek,agendaDay"
					}
				} else {
					$("#calendar").removeClass("mobile");
					c = {
						left : "title",
						center : "",
						right : "prev,next,today,month,agendaWeek,agendaDay"
					}
				}
			}
			var f = function(h) {
				var d = {
					title : $.trim(h.text())
				};
				h.data("eventObject", d);
				h.draggable({
					zIndex : 999,
					revert : true,
					revertDuration : 0
				})
			};
			var e = function(h) {
				h = h.length == 0 ? "Untitled Event" : h;
				var d = $('<div class="external-event label label-default">'
						+ h + "</div>");
				jQuery("#event_box").append(d);
				f(d)
			};
			$("#external-events div.external-event").each(function() {
				f($(this))
			});
			$("#event_add").unbind("click").click(function() {
				var d = $("#event_title").val();
				e(d)
			});
			$("#event_box").html("");
			e("My Event 1");
			e("My Event 2");
			e("My Event 3");
			e("My Event 4");
			e("My Event 5");
			e("My Event 6");
			$("#calendar").fullCalendar("destroy");
			$("#calendar").fullCalendar({
				header : c,
				slotMinutes : 15,
				editable : true,
				droppable : true,
				drop : function(j, k) {
					var h = $(this).data("eventObject");
					var d = $.extend({}, h);
					d.start = j;
					d.allDay = k;
					d.className = $(this).attr("data-class");
					$("#calendar").fullCalendar("renderEvent", d, true);
					if ($("#drop-remove").is(":checked")) {
						$(this).remove()
					}
				},
				events : [ {
					title : "All Day Event",
					start : new Date(i, a, 1),
					backgroundColor : App.getLayoutColorCode("yellow")
				}, {
					title : "Long Event",
					start : new Date(i, a, g - 5),
					end : new Date(i, a, g - 2),
					backgroundColor : App.getLayoutColorCode("green")
				}, {
					title : "Repeating Event",
					start : new Date(i, a, g - 3, 16, 0),
					allDay : false,
					backgroundColor : App.getLayoutColorCode("red")
				}, {
					title : "Repeating Event",
					start : new Date(i, a, g + 4, 16, 0),
					allDay : false,
					backgroundColor : App.getLayoutColorCode("green")
				}, {
					title : "Meeting",
					start : new Date(i, a, g, 10, 30),
					allDay : false,
				}, {
					title : "Lunch",
					start : new Date(i, a, g, 12, 0),
					end : new Date(i, a, g, 14, 0),
					backgroundColor : App.getLayoutColorCode("grey"),
					allDay : false,
				}, {
					title : "Birthday Party",
					start : new Date(i, a, g + 1, 19, 0),
					end : new Date(i, a, g + 1, 22, 30),
					backgroundColor : App.getLayoutColorCode("purple"),
					allDay : false,
				}, {
					title : "Click for Google",
					start : new Date(i, a, 28),
					end : new Date(i, a, 29),
					backgroundColor : App.getLayoutColorCode("yellow"),
					url : "http://google.com/",
				} ]
			})
		}
	}
}();