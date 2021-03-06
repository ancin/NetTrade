var Charts = function() {
	return {
		init : function() {
			App.addResponsiveHandler(function() {
				Charts.initPieCharts()
			})
		},
		initCharts : function() {
			if (!jQuery.plot) {
				return
			}
			var f = [];
			var h = 250;
			function g() {
				if (f.length > 0) {
					f = f.slice(1)
				}
				while (f.length < h) {
					var l = f.length > 0 ? f[f.length - 1] : 50;
					var m = l + Math.random() * 10 - 5;
					if (m < 0) {
						m = 0
					}
					if (m > 100) {
						m = 100
					}
					f.push(m)
				}
				var k = [];
				for (var j = 0; j < f.length; ++j) {
					k.push([ j, f[j] ])
				}
				return k
			}
			function e() {
				var m = [];
				for (var j = 0; j < Math.PI * 2; j += 0.25) {
					m.push([ j, Math.sin(j) ])
				}
				var l = [];
				for (var j = 0; j < Math.PI * 2; j += 0.25) {
					l.push([ j, Math.cos(j) ])
				}
				var k = [];
				for (var j = 0; j < Math.PI * 2; j += 0.1) {
					k.push([ j, Math.tan(j) ])
				}
				$.plot($("#chart_1"), [ {
					label : "sin(x)",
					data : m
				}, {
					label : "cos(x)",
					data : l
				}, {
					label : "tan(x)",
					data : k
				} ], {
					series : {
						lines : {
							show : true
						},
						points : {
							show : true
						}
					},
					xaxis : {
						ticks : [ 0, [ Math.PI / 2, "\u03c0/2" ],
								[ Math.PI, "\u03c0" ],
								[ Math.PI * 3 / 2, "3\u03c0/2" ],
								[ Math.PI * 2, "2\u03c0" ] ]
					},
					yaxis : {
						ticks : 10,
						min : -2,
						max : 2
					},
					grid : {
						backgroundColor : {
							colors : [ "#fff", "#eee" ]
						}
					}
				})
			}
			function d() {
				function k() {
					return (Math.floor(Math.random() * (1 + 40 - 20))) + 20
				}
				var l = [ [ 1, k() ], [ 2, k() ], [ 3, 2 + k() ],
						[ 4, 3 + k() ], [ 5, 5 + k() ], [ 6, 10 + k() ],
						[ 7, 15 + k() ], [ 8, 20 + k() ], [ 9, 25 + k() ],
						[ 10, 30 + k() ], [ 11, 35 + k() ], [ 12, 25 + k() ],
						[ 13, 15 + k() ], [ 14, 20 + k() ], [ 15, 45 + k() ],
						[ 16, 50 + k() ], [ 17, 65 + k() ], [ 18, 70 + k() ],
						[ 19, 85 + k() ], [ 20, 80 + k() ], [ 21, 75 + k() ],
						[ 22, 80 + k() ], [ 23, 75 + k() ], [ 24, 70 + k() ],
						[ 25, 65 + k() ], [ 26, 75 + k() ], [ 27, 80 + k() ],
						[ 28, 85 + k() ], [ 29, 90 + k() ], [ 30, 95 + k() ] ];
				var j = [ [ 1, k() - 5 ], [ 2, k() - 5 ], [ 3, k() - 5 ],
						[ 4, 6 + k() ], [ 5, 5 + k() ], [ 6, 20 + k() ],
						[ 7, 25 + k() ], [ 8, 36 + k() ], [ 9, 26 + k() ],
						[ 10, 38 + k() ], [ 11, 39 + k() ], [ 12, 50 + k() ],
						[ 13, 51 + k() ], [ 14, 12 + k() ], [ 15, 13 + k() ],
						[ 16, 14 + k() ], [ 17, 15 + k() ], [ 18, 15 + k() ],
						[ 19, 16 + k() ], [ 20, 17 + k() ], [ 21, 18 + k() ],
						[ 22, 19 + k() ], [ 23, 20 + k() ], [ 24, 21 + k() ],
						[ 25, 14 + k() ], [ 26, 24 + k() ], [ 27, 25 + k() ],
						[ 28, 26 + k() ], [ 29, 27 + k() ], [ 30, 31 + k() ] ];
				var n = $.plot($("#chart_2"), [ {
					data : l,
					label : "Unique Visits"
				}, {
					data : j,
					label : "Page Views"
				} ], {
					series : {
						lines : {
							show : true,
							lineWidth : 2,
							fill : true,
							fillColor : {
								colors : [ {
									opacity : 0.05
								}, {
									opacity : 0.01
								} ]
							}
						},
						points : {
							show : true
						},
						shadowSize : 2
					},
					grid : {
						hoverable : true,
						clickable : true,
						tickColor : "#eee",
						borderWidth : 0
					},
					colors : [ "#d12610", "#37b7f3", "#52e136" ],
					xaxis : {
						ticks : 11,
						tickDecimals : 0
					},
					yaxis : {
						ticks : 11,
						tickDecimals : 0
					}
				});
				function o(p, r, q) {
					$('<div id="tooltip">' + q + "</div>").css({
						position : "absolute",
						display : "none",
						top : r + 5,
						left : p + 15,
						border : "1px solid #333",
						padding : "4px",
						color : "#fff",
						"border-radius" : "3px",
						"background-color" : "#333",
						opacity : 0.8
					}).appendTo("body").fadeIn(200)
				}
				var m = null;
				$("#chart_2")
						.bind(
								"plothover",
								function(r, t, q) {
									$("#x").text(t.x.toFixed(2));
									$("#y").text(t.y.toFixed(2));
									if (q) {
										if (m != q.dataIndex) {
											m = q.dataIndex;
											$("#tooltip").remove();
											var p = q.datapoint[0].toFixed(2), s = q.datapoint[1]
													.toFixed(2);
											o(q.pageX, q.pageY, q.series.label
													+ " of " + p + " = " + s)
										}
									} else {
										$("#tooltip").remove();
										m = null
									}
								})
			}
			function c() {
				var m = [], p = [];
				for (var o = 0; o < 14; o += 0.1) {
					m.push([ o, Math.sin(o) ]);
					p.push([ o, Math.cos(o) ])
				}
				plot = $.plot($("#chart_3"), [ {
					data : m,
					label : "sin(x) = -0.00"
				}, {
					data : p,
					label : "cos(x) = -0.00"
				} ], {
					series : {
						lines : {
							show : true
						}
					},
					crosshair : {
						mode : "x"
					},
					grid : {
						hoverable : true,
						autoHighlight : false
					},
					yaxis : {
						min : -1.2,
						max : 1.2
					}
				});
				var j = $("#chart_3 .legendLabel");
				j.each(function() {
					$(this).css("width", $(this).width())
				});
				var l = null;
				var k = null;
				function n() {
					l = null;
					var v = k;
					var u = plot.getAxes();
					if (v.x < u.xaxis.min || v.x > u.xaxis.max
							|| v.y < u.yaxis.min || v.y > u.yaxis.max) {
						return
					}
					var s, r, q = plot.getData();
					for (s = 0; s < q.length; ++s) {
						var t = q[s];
						for (r = 0; r < t.data.length; ++r) {
							if (t.data[r][0] > v.x) {
								break
							}
						}
						var w, z = t.data[r - 1], x = t.data[r];
						if (z == null) {
							w = x[1]
						} else {
							if (x == null) {
								w = z[1]
							} else {
								w = z[1] + (x[1] - z[1]) * (v.x - z[0])
										/ (x[0] - z[0])
							}
						}
						j.eq(s).text(
								t.label.replace(/=.*/, "= " + w.toFixed(2)))
					}
				}
				$("#chart_3").bind("plothover", function(r, s, q) {
					k = s;
					if (!l) {
						l = setTimeout(n, 50)
					}
				})
			}
			function b() {
				var j = {
					series : {
						shadowSize : 1
					},
					lines : {
						show : true,
						lineWidth : 0.5,
						fill : true,
						fillColor : {
							colors : [ {
								opacity : 0.1
							}, {
								opacity : 1
							} ]
						}
					},
					yaxis : {
						min : 0,
						max : 100,
						tickFormatter : function(n) {
							return n + "%"
						}
					},
					xaxis : {
						show : false
					},
					colors : [ "#6ef146" ],
					grid : {
						tickColor : "#a8a3a3",
						borderWidth : 0
					}
				};
				var l = 30;
				var k = $.plot($("#chart_4"), [ g() ], j);
				function m() {
					k.setData([ g() ]);
					k.draw();
					setTimeout(m, l)
				}
				m()
			}
			function a() {
				var k = [];
				for (var m = 0; m <= 10; m += 1) {
					k.push([ m, parseInt(Math.random() * 30) ])
				}
				var j = [];
				for (var m = 0; m <= 10; m += 1) {
					j.push([ m, parseInt(Math.random() * 30) ])
				}
				var q = [];
				for (var m = 0; m <= 10; m += 1) {
					q.push([ m, parseInt(Math.random() * 30) ])
				}
				var o = 0, p = true, r = false, n = false;
				function l() {
					$.plot($("#chart_5"), [ k, j, q ], {
						series : {
							stack : o,
							lines : {
								show : r,
								fill : true,
								steps : n
							},
							bars : {
								show : p,
								barWidth : 0.6
							}
						}
					})
				}
				$(".stackControls input").click(function(s) {
					s.preventDefault();
					o = $(this).val() == "With stacking" ? true : null;
					l()
				});
				$(".graphControls input").click(function(s) {
					s.preventDefault();
					p = $(this).val().indexOf("Bars") != -1;
					r = $(this).val().indexOf("Lines") != -1;
					n = $(this).val().indexOf("steps") != -1;
					l()
				});
				l()
			}
			e();
			d();
			c();
			b();
			a()
		},
		initBarCharts : function() {
			var a = b(0);
			function b(f) {
				var g = [];
				var j = 100 + f;
				var e = 200 + f;
				for (i = 1; i <= 20; i++) {
					var h = Math.floor(Math.random() * (e - j + 1) + j);
					g.push([ i, h ]);
					j++;
					e++
				}
				return g
			}
			var c = {
				series : {
					bars : {
						show : true
					}
				},
				bars : {
					barWidth : 0.8
				},
				grid : {
					backgroundColor : {
						colors : [ "#fafafa", "#35aa47" ]
					}
				}
			};
			$.plot($("#chart_1_1"), [ a ], c);
			var a = [ [ 10, 10 ], [ 20, 20 ], [ 30, 30 ], [ 40, 40 ],
					[ 50, 50 ] ];
			var c = {
				series : {
					bars : {
						show : true
					}
				},
				bars : {
					horizontal : true,
					barWidth : 6
				},
				grid : {
					backgroundColor : {
						colors : [ "#fafafa", "#4b8df8" ]
					}
				}
			};
			$.plot($("#chart_1_2"), [ a ], c)
		},
		initPieCharts : function() {
			var e = [];
			var d = Math.floor(Math.random() * 10) + 1;
			d = d < 5 ? 5 : d;
			for (var c = 0; c < d; c++) {
				e[c] = {
					label : "Series" + (c + 1),
					data : Math.floor(Math.random() * 100) + 1
				}
			}
			$.plot($("#pie_chart"), e, {
				series : {
					pie : {
						show : true
					}
				}
			});
			$.plot($("#pie_chart_1"), e, {
				series : {
					pie : {
						show : true
					}
				},
				legend : {
					show : false
				}
			});
			$
					.plot(
							$("#pie_chart_2"),
							e,
							{
								series : {
									pie : {
										show : true,
										radius : 1,
										label : {
											show : true,
											radius : 1,
											formatter : function(f, g) {
												return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
														+ f
														+ "<br/>"
														+ Math.round(g.percent)
														+ "%</div>"
											},
											background : {
												opacity : 0.8
											}
										}
									}
								},
								legend : {
									show : false
								}
							});
			$
					.plot(
							$("#pie_chart_3"),
							e,
							{
								series : {
									pie : {
										show : true,
										radius : 1,
										label : {
											show : true,
											radius : 3 / 4,
											formatter : function(f, g) {
												return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
														+ f
														+ "<br/>"
														+ Math.round(g.percent)
														+ "%</div>"
											},
											background : {
												opacity : 0.5
											}
										}
									}
								},
								legend : {
									show : false
								}
							});
			$
					.plot(
							$("#pie_chart_4"),
							e,
							{
								series : {
									pie : {
										show : true,
										radius : 1,
										label : {
											show : true,
											radius : 3 / 4,
											formatter : function(f, g) {
												return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
														+ f
														+ "<br/>"
														+ Math.round(g.percent)
														+ "%</div>"
											},
											background : {
												opacity : 0.5,
												color : "#000"
											}
										}
									}
								},
								legend : {
									show : false
								}
							});
			$
					.plot(
							$("#pie_chart_5"),
							e,
							{
								series : {
									pie : {
										show : true,
										radius : 3 / 4,
										label : {
											show : true,
											radius : 3 / 4,
											formatter : function(f, g) {
												return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
														+ f
														+ "<br/>"
														+ Math.round(g.percent)
														+ "%</div>"
											},
											background : {
												opacity : 0.5,
												color : "#000"
											}
										}
									}
								},
								legend : {
									show : false
								}
							});
			$
					.plot(
							$("#pie_chart_6"),
							e,
							{
								series : {
									pie : {
										show : true,
										radius : 1,
										label : {
											show : true,
											radius : 2 / 3,
											formatter : function(f, g) {
												return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
														+ f
														+ "<br/>"
														+ Math.round(g.percent)
														+ "%</div>"
											},
											threshold : 0.1
										}
									}
								},
								legend : {
									show : false
								}
							});
			$.plot($("#pie_chart_7"), e, {
				series : {
					pie : {
						show : true,
						combine : {
							color : "#999",
							threshold : 0.1
						}
					}
				},
				legend : {
					show : false
				}
			});
			$
					.plot(
							$("#pie_chart_8"),
							e,
							{
								series : {
									pie : {
										show : true,
										radius : 300,
										label : {
											show : true,
											formatter : function(f, g) {
												return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
														+ f
														+ "<br/>"
														+ Math.round(g.percent)
														+ "%</div>"
											},
											threshold : 0.1
										}
									}
								},
								legend : {
									show : false
								}
							});
			$
					.plot(
							$("#pie_chart_9"),
							e,
							{
								series : {
									pie : {
										show : true,
										radius : 1,
										tilt : 0.5,
										label : {
											show : true,
											radius : 1,
											formatter : function(f, g) {
												return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
														+ f
														+ "<br/>"
														+ Math.round(g.percent)
														+ "%</div>"
											},
											background : {
												opacity : 0.8
											}
										},
										combine : {
											color : "#999",
											threshold : 0.1
										}
									}
								},
								legend : {
									show : false
								}
							});
			$.plot($("#donut"), e, {
				series : {
					pie : {
						innerRadius : 0.5,
						show : true
					}
				}
			});
			$.plot($("#interactive"), e, {
				series : {
					pie : {
						show : true
					}
				},
				grid : {
					hoverable : true,
					clickable : true
				}
			});
			$("#interactive").bind("plothover", a);
			$("#interactive").bind("plotclick", b);
			function a(f, h, g) {
				if (!g) {
					return
				}
				percent = parseFloat(g.series.percent).toFixed(2);
				$("#hover").html(
						'<span style="font-weight: bold; color: '
								+ g.series.color + '">' + g.series.label + " ("
								+ percent + "%)</span>")
			}
			function b(f, h, g) {
				if (!g) {
					return
				}
				percent = parseFloat(g.series.percent).toFixed(2);
				alert("" + g.series.label + ": " + percent + "%")
			}
		}
	}
}();