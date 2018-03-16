var Util = function() {
	return {
		traverseTreeToKeyValue : function(b, a) {
			if (a == undefined) {
				a = {}
			}
			$.each(b, function(c, d) {
				a[d.id] = d.name;
				if (typeof (d.children) === "object") {
					Util.traverseTreeToKeyValue(d.children, a)
				}
			});
			return a
		},
		getCacheDatas : function(b, d, a) {
			if (d == undefined || d == null) {
				d = $("body")
			}
			if (d.data("CacheUrlDatas") == undefined) {
				d.data("CacheUrlDatas", {})
			}
			var c = d.data("CacheUrlDatas")[b];
			if (c == undefined) {
				$.ajax($.extend({
					async : false,
					type : "GET",
					url : b,
					dataType : "json",
					success : function(e) {
						c = e;
						d.data("CacheUrlDatas")[b] = c
					}
				}, a || {}))
			}
			return c
		},
		getCacheSelectOptionDatas : function(a, c) {
			if (c == undefined) {
				c = $("body")
			}
			if (c.data("CacheSelectOptionDatas") == undefined) {
				c.data("CacheSelectOptionDatas", {})
			}
			var b = c.data("CacheSelectOptionDatas")[a];
			if (b == undefined) {
				$.ajax({
					async : false,
					type : "GET",
					url : a,
					dataType : "json",
					success : function(e) {
						var d = e;
						if (e.content) {
							d = e.content
						}
						b = {
							"" : ""
						};
						$.each(d, function(f, g) {
							b[g.id] = g.display
						});
						c.data("CacheSelectOptionDatas")[a] = b
					}
				})
			}
			return b
		},
		getCacheEnumsByType : function(a, c) {
			if (c == undefined) {
				c = $("body")
			}
			if (c.data("CacheEnumDatas") == undefined) {
				$.ajax({
					async : false,
					type : "GET",
					url : WEB_ROOT + "/pub/data!enums.json",
					dataType : "json",
					success : function(h) {
						for ( var g in h) {
							var e = h[g];
							var d = {
								"" : ""
							};
							for ( var f in e) {
								d[f] = e[f]
							}
							h[g] = d
						}
						c.data("CacheEnumDatas", h)
					}
				})
			}
			var b = c.data("CacheEnumDatas")[a];
			if (b == undefined) {
				alert("错误的枚举数据类型：" + a);
				b = {}
			}
			return b
		},
		getCacheDictDatasByType : function(b, e) {
			if (e == undefined) {
				e = $("body")
			}
			var f = e.data("CacheDictDatas");
			if (f == undefined) {
				$.ajax({
					async : false,
					type : "GET",
					url : WEB_ROOT + "/pub/data!dictDatas.json",
					dataType : "json",
					success : function(g) {
						f = g;
						e.data("CacheDictDatas", f)
					}
				})
			}
			var c = e.data("CacheDictDatas")[b];
			if (c == undefined) {
				var a = {};
				var d = true;
				$.each(f, function(g, h) {
					if (h.parentPrimaryKey == b) {
						d = false;
						a[h.primaryKey] = h.primaryValue
					}
				});
				c = a;
				e.data("CacheDictDatas")[b] = c;
				if (d) {
					alert("错误的数据字典类型：" + b)
				}
			}
			return c
		},
		assert : function(b, a) {
			if (!b) {
				alert(a)
			}
		},
		assertNotBlank : function(b, a) {
			if (b == undefined || $.trim(b) == "") {
				Util.assert(false, a);
				return
			}
		},
		debug : function(a) {
			if (window.console) {
				console.debug(a)
			} else {
				alert(a)
			}
		},
		hashCode : function(c) {
			var b = 0;
			if (c.length == 0) {
				return b
			}
			for (i = 0; i < c.length; i++) {
				var a = c.charCodeAt(i);
				b = ((b << 5) - b) + a;
				b = b & b
			}
			if (b < 0) {
				b = -b
			}
			return b
		},
		AddOrReplaceUrlParameter : function(f, a, e) {
			var d = f.indexOf("?");
			if (d == -1) {
				f = f + "?" + a + "=" + e
			} else {
				var g = f.split("?");
				var h = g[1].split("&");
				var c = "";
				var b = false;
				for (i = 0; i < h.length; i++) {
					c = h[i].split("=")[0];
					if (c == a) {
						h[i] = a + "=" + e;
						b = true;
						break
					}
				}
				if (!b) {
					f = f + "&" + a + "=" + e
				} else {
					f = g[0] + "?";
					for (i = 0; i < h.length; i++) {
						if (i > 0) {
							f = f + "&"
						}
						f = f + h[i]
					}
				}
			}
			return f
		},
		subStringBetween : function(d, f, b) {
			var e = new RegExp(f + ".*?" + b, "img");
			var c = new RegExp(f, "g");
			var a = new RegExp(b, "g");
			return d.match(e).join("=").replace(c, "").replace(a, "")
					.split("=")
		},
		split : function(a) {
			return a.split(",")
		},
		isArrayContainElement : function(c, b) {
			var a = c.length;
			while (a--) {
				if (c[a] === b) {
					return true
				}
			}
			return false
		},
		getTextWithoutChildren : function(a) {
			return $(a)[0].childNodes[0].nodeValue.trim()
		},
		findClosestFormInputByName : function(b, a) {
			return $(b).closest("form").find("[name='" + a + "']")
		},
		setInputValIfBlank : function(a, b) {
			if ($.trim($(a).val()) == "") {
				$(a).val(b)
			}
		},
		unEditable : function(b) {
			var a = $(b);
			return a.attr("readonly") || a.attr("disabled")
		},
		startWith : function(b, c) {
			var a = new RegExp("^" + c);
			return a.test(b)
		},
		endWith : function(c, a) {
			var b = new RegExp(a + "$");
			return b.test(c)
		},
		objectToString : function(a) {
			if (a == undefined) {
				return "undefined"
			}
			var b = "";
			$.each(a, function(d, c) {
				b += (d + ":" + c + ";\n")
			});
			return b
		},
		parseFloatValDefaultZero : function(b) {
			if ($.trim($(b).val()) == "") {
				return 0
			} else {
				var a = parseFloat($.trim($(b).val()));
				if (isNaN(a)) {
					return 0
				} else {
					return a
				}
			}
		},
		notSmallViewport : function() {
			var a = $(window).width();
			return a >= 768
		},
		init : function() {
			$.fn.cacheData = function(c, b) {
				var d = $(this);
				var a = $("body");
				if (a.data("CacheUrlDatas") == undefined) {
					a.data("CacheUrlDatas", {})
				}
				var e = a.data("CacheUrlDatas")[c];
				if (e == undefined) {
					var f = d.closest("div");
					$.ajax($.extend({
						async : false,
						type : "GET",
						url : c,
						dataType : "json",
						success : function(g) {
							e = g;
							a.data("CacheUrlDatas")[c] = e
						}
					}, b || {}))
				}
				return e
			};
					$.fn.plot = function(e) {
						var d = $(this);
						if (d.attr("chart-plot-done")) {
							return
						}
						d.attr("chart-plot-done", true);
						d.css("min-height", "100px");
						var a = $.extend({}, d.data("plotOptions") || {}, e
								|| {});
						var b = a.data;
						var c = a.options;
						$.each(b, function(g, h) {
							if (typeof h.data === "function") {
								h.data = h.data.call(d)
							}
						});
						c = $.extend(true, {
							pointhover : true,
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
								timezone : "browser",
								monthNames : [ "1月", "2月", "3月", "4月", "5月",
										"6月", "7月", "8月", "9月", "10月", "11月",
										"12月" ]
							}
						}, c);
						$.plot(d, b, c);
						if (a.pointhover) {
							var f = $("#plothoverTooltip");
							if (f.size() == 0) {
								f = $("<div id='plothoverTooltip'></div>").css(
										{
											position : "absolute",
											display : "none",
											border : "1px solid #333",
											padding : "4px",
											color : "#fff",
											"border-radius" : "3px",
											"background-color" : "#333",
											opacity : 0.8,
											"min-width" : "50px",
											"text-align" : "center"
										}).appendTo("body")
							}
							d.bind("plothover", function(h, k, g) {
								if (g) {
									var j = g.datapoint[1];
									f.html(j).css({
										top : g.pageY,
										left : g.pageX + 15
									}).fadeIn(200)
								} else {
									f.hide()
								}
							})
						}
					},
					$.fn.barcodeScanSupport = function(b) {
						var a = $(this);
						if (a.attr("barcode-scan-support-done")) {
							return this
						}
						a.attr("barcode-scan-support-done", true);
						var c = a.attr("id");
						if (c == undefined) {
							c = "barcode_" + new Date().getTime();
							a.attr("id", c)
						}
						if (a.attr("placeholder") == undefined) {
							a.attr("placeholder", "支持条码扫描输入;可手工输入按回车键模拟")
						}
						if (a.attr("title") == undefined) {
							a.attr("title", a.attr("placeholder"))
						}
						a.focus(function(d) {
							a.select()
						}).click(function(d) {
							if (window.wst) {
								window.wst.startupBarcodeScan(c)
							}
						}).keydown(function(d) {
							if (b && b.onEnter) {
								if (d.keyCode === 13) {
									b.onEnter.call(a)
								}
							}
						}).bind("barcode", function(d, f) {
							a.val(f);
							var g = jQuery.Event("keydown");
							g.keyCode = 13;
							a.trigger(g);
							a.select()
						})
					},
					$.fn.treeselect = function(b) {
						var g = $(this);
						if (g.attr("treeselect-done")) {
							return this
						}
						g.attr("treeselect-done", true);
						if (Util.unEditable(g)) {
							return this
						}
						b = $.extend({
							url : g.attr("data-url"),
							position : g.attr("data-position")
						}, g.data("treeOptions") || {}, b);
						var f = "treeselect_" + new Date().getTime();
						g.attr("id", f);
						var e = g.closest(".panel-content");
						var c = $(
								'<i class="fa fa-angle-double-down btn-toggle"></i>')
								.insertBefore(g);
						var a = g.parent().children();
						a.wrapAll('<div class="input-icon right"></div>');
						var h = $('<div style="z-index: 990; display: none; position: absolute; background-color: #FFFFFF; border: 1px solid #DDDDDD"></div>');
						h.appendTo(e);
						var m = [];
						m
								.push('<div role="navigation" class="navbar navbar-default" style="border: 0px; margin:0px">');
						m
								.push('<div class="collapse navbar-collapse navbar-ex1-collapse" style="padding: 0">');
						m
								.push('<form role="search" class="navbar-form navbar-left">');
						m
								.push('<div class="form-group" style="border-bottom: 0px">');
						m
								.push('<input type="text" name="keyword" class="form-control input-small">');
						m.push("</div>");
						m
								.push('<button class="btn blue" type="submit">查询</button>');
						m.push("</form>");
						m.push('<ul class="nav navbar-nav navbar-right">');
						m
								.push('<li><a href="javascript:;" class="btn-open-all" style="padding-left: 0">展开</li>');
						m
								.push('<li><a href="javascript:;" class="btn-close-all" style="padding-left: 0">收拢</a></li>');
						m
								.push('<li><a href="javascript:;" class="btn-clear" style="padding-left: 0;padding-right: 20px">清除</a></li>');
						m.push("</ul>");
						m.push("</div>");
						m.push("</div>");
						var j = $(m.join("")).appendTo(h);
						var l = $(
								'<div style="max-height: 300px;overflow: auto"></div>')
								.appendTo(h);
						var k = $('<ul class="ztree"></ul>').appendTo(l);
						k.attr("id", "ztree_" + f);
						k.attr("id-for", f);
						k.attr("data-url", b.url);
						var d = function(r) {
							var n = g.attr("name");
							var v = n.replace(".display", ".id");
							var u = {};
							if (r) {
								u[n] = Util.startWith(r.id, "-") ? "" : r.name;
								u[v] = Util.startWith(r.id, "-") ? "" : r.id
							} else {
								u[n] = "";
								u[v] = ""
							}
							var t = g.closest(".ui-jqgrid-btable");
							if (t.size() > 0) {
								var o = false;
								var s = t.jqGrid("getGridParam", "colModel");
								for (var p = 0; p < s.length; p++) {
									var q = s[p];
									if (q.name == v || q.index == v) {
										o = true;
										break
									}
								}
								if (!o) {
									alert("页面配置错误： " + n + " 对应的id属性 " + v
											+ " 未定义");
									return
								}
								t.jqGrid("setEditingRowdata", u)
							} else {
								if (g.closest(".form-group").size() > 0) {
									var w = g.closest("form");
									w.setFormDatas(u, true)
								}
							}
							g.focus()
						};
						g
								.click(
										function() {
											var s = g
													.attr("treeselect-cached-done");
											if (s == undefined) {
												g
														.attr(
																"treeselect-cached-done",
																true);
												g.attr("disabled", true);
												g.addClass("spinner");
												var u = g.cacheData(b.url);
												$.fn.zTree
														.init(
																k,
																{
																	callback : {
																		onClick : function(
																				w,
																				y,
																				x) {
																			if (b.callback
																					&& b.callback.onSingleClick) {
																				var v = b.callback.onSingleClick
																						.call(
																								this,
																								w,
																								y,
																								x);
																				if (v == undefined
																						|| v == true) {
																					h
																							.hide();
																					c
																							.removeClass("fa-angle-double-up");
																					c
																							.addClass("fa-angle-double-down")
																				}
																			} else {
																				d(x);
																				h
																						.hide();
																				c
																						.removeClass("fa-angle-double-up");
																				c
																						.addClass("fa-angle-double-down")
																			}
																			g
																					.trigger(
																							"treeselect.nodeSelect",
																							[ x ]);
																			w
																					.stopPropagation();
																			w
																					.preventDefault();
																			return false
																		}
																	}
																}, u);
												g.removeAttr("disabled");
												g.removeClass("spinner")
											}
											var p = $.fn.zTree.getZTreeObj(k
													.attr("id"));
											p.cancelSelectedNode();
											if ($.trim(g.val()) != "") {
												var o = p.getNodesByParamFuzzy(
														"name", g.val());
												for (var q = 0, n = o.length; q < n; q++) {
													var t = o[q];
													p.selectNode(t)
												}
											}
											h.children(".ztree").hide();
											k.show();
											var r = g.outerWidth();
											if (r < 330) {
												r = 330
											}
											h.css({
												width : r + "px"
											}).slideDown("fast");
											h.position($.extend(true, {
												my : "right top",
												at : "right bottom",
												of : g.parent("div")
											}, b.position));
											c
													.removeClass("fa-angle-double-down");
											c.addClass("fa-angle-double-up")
										}).keydown(function(n) {
									if (n.keyCode === 13) {
										return true
									}
									return false
								});
						c.click(function(n) {
							if ($(this).hasClass("fa-angle-double-down")) {
								g.click()
							} else {
								c.removeClass("fa-angle-double-up");
								c.addClass("fa-angle-double-down");
								h.hide()
							}
							n.stopPropagation();
							n.preventDefault()
						});
						j.find("form").submit(function(s) {
							var t = j.find("input[name='keyword']").val();
							var p = $.fn.zTree.getZTreeObj(k.attr("id"));
							p.cancelSelectedNode();
							var o = p.getNodesByParamFuzzy("name", t);
							for (var q = 0, n = o.length; q < n; q++) {
								var r = o[q];
								p.selectNode(r, true)
							}
							s.stopPropagation();
							s.preventDefault();
							return false
						});
						j.find(".btn-open-all").click(function(o) {
							var n = $.fn.zTree.getZTreeObj(k.attr("id"));
							n.expandAll(true);
							o.stopPropagation();
							o.preventDefault();
							return false
						});
						j.find(".btn-close-all").click(function(o) {
							var n = $.fn.zTree.getZTreeObj(k.attr("id"));
							n.expandAll(false);
							o.stopPropagation();
							o.preventDefault();
							return false
						});
						j.find(".btn-clear").click(function(n) {
							if (b.callback && b.callback.onClear) {
								b.callback.onClear.call(this, n)
							} else {
								d()
							}
							h.hide();
							c.removeClass("fa-angle-double-up");
							c.addClass("fa-angle-double-down");
							n.stopPropagation();
							n.preventDefault();
							return false
						});
						$(document).on(
								"mousedown",
								function(p) {
									var q = h;
									var o = g;
									var n = p.target.tagName;
									if (n == "HTML") {
										return
									}
									if (!(o.is(p.target)
											|| o.find(p.target).length
											|| q.is(p.target) || q
											.find(p.target).length)) {
										q.hide()
									}
								})
					}, $.fn.ajaxGetUrl = function(b, d, c) {
						Util.assertNotBlank(b, "ajaxGetUrl调用的url参数不能为空");
						$("#btn-profile-param").hide();
						var a = $(this);
						a.addClass("ajax-get-container");
						a.attr("data-url", b);
						a.css("min-height", "100px");
						App.blockUI(a);
						$.ajax({
							type : "GET",
							cache : false,
							url : b,
							data : c,
							dataType : "html",
							success : function(f) {
								a.empty();
								var e = $("<div class='ajax-page-inner'/>")
										.appendTo(a);
								e.hide();
								e.html(f);
								if (d) {
									d.call(a, f)
								}
								Page.initAjaxBeforeShow(e);
								e.show();
								FormValidation.initAjax(e);
								Page.initAjaxAfterShow(e);
								Grid.initAjax(e);
								App.unblockUI(a)
							},
							error : function(g, e, f) {
								a.html("<h4>页面内容加载失败</h4>" + g.responseText);
								App.unblockUI(a)
							},
							statusCode : {
								403 : function() {
									Global
											.notify("error", "URL: " + b,
													"未授权访问")
								},
								404 : function() {
									Global.notify("error", "页面未找到：" + b
											+ "，请联系管理员", "请求资源未找到")
								}
							}
						});
						return a
					};
			$.fn.ajaxJsonUrl = function(b, d, c) {
				Util.assertNotBlank(b, "ajaxJsonUrl调用的url参数不能为空");
				var a = $(this);
				App.blockUI(a);
				$.ajax({
					traditional : true,
					type : "GET",
					cache : false,
					url : b,
					dataType : "json",
					data : c,
					success : function(e) {
						if (e.type == "error" || e.type == "warning"
								|| e.type == "failure") {
							Global.notify("error", e.message)
						} else {
							if (d) {
								d.call(a, e)
							}
							json = e
						}
						App.unblockUI(a)
					},
					error : function(g, e, f) {
						Global.notify("error", "数据请求异常，请联系管理员", "系统错误");
						App.unblockUI(a)
					},
					statusCode : {
						403 : function() {
							Global.notify("error", "URL: " + b, "未授权访问")
						},
						404 : function() {
							Global.notify("error", "请尝试刷新页面试试，如果问题依然请联系管理员",
									"请求资源未找到")
						}
					}
				})
			};
			$.fn.ajaxJsonSync = function(b, d, e) {
				Util.assertNotBlank(b, "ajaxJsonSync 调用的url参数不能为空");
				var a = $(this);
				App.blockUI(a);
				var c = null;
				$.ajax({
					traditional : true,
					type : "GET",
					cache : false,
					async : false,
					url : b,
					data : d,
					contentType : "application/json",
					dataType : "json",
					success : function(f) {
						if (f.type == "error" || f.type == "warning"
								|| f.type == "failure") {
							Global.notify("error", f.message)
						} else {
							if (e) {
								e.call(a, f)
							}
							c = f
						}
						App.unblockUI(a)
					},
					error : function(h, f, g) {
						Global.notify("error", "数据请求异常，请联系管理员", "系统错误");
						App.unblockUI(a)
					},
					statusCode : {
						403 : function() {
							Global.notify("error", "URL: " + b, "未授权访问")
						},
						404 : function() {
							Global.notify("error", "请尝试刷新页面试试，如果问题依然请联系管理员",
									"请求资源未找到")
						}
					}
				});
				return c
			};
			$.fn.ajaxPostURL = function(b) {
				var a = b.url;
				Util.assertNotBlank(a);
				var e = b.success;
				var d = b.confirmMsg;
				if (d == undefined) {
					d = "确认提交数据？"
				}
				if (d) {
					if (!confirm(d)) {
						return false
					}
				}
				var b = $.extend({
					data : {}
				}, b);
				var c = $(this);
				App.blockUI(c);
				$.post(encodeURI(a), b.data, function(f, j) {
					App.unblockUI(c);
					if (!f.type) {
						Global.notify("error", f, "系统处理异常");
						return
					}
					if (f.type == "success" || f.type == "warning") {
						Global.notify(f.type, f.message);
						if (e) {
							e.call(c, f)
						}
					} else {
						if (f.userdata) {
							var h = [];
							for ( var g in f.userdata) {
								h.push(f.userdata[g])
							}
							Global.notify("error", h.join("<br>"), f.message)
						} else {
							Global.notify("error", f.message)
						}
						if (b.failure) {
							b.failure.call(c, f)
						}
					}
				}, "json")
			};
			$.fn.ajaxPostForm = function(b) {
				var e = b.success;
				var a = b.failure;
				var d = b.confirmMsg;
				if (d) {
					if (!confirm(d)) {
						return false
					}
				}
				var b = $.extend({
					data : {}
				}, b);
				var c = $(this);
				App.blockUI(c);
				c.ajaxSubmit({
					dataType : "json",
					method : "post",
					success : function(f) {
						App.unblockUI(c);
						if (f.type == "success") {
							if (e) {
								e.call(c, f)
							}
						} else {
							if (f.type == "failure" || f.type == "error") {
								Global.notify(f.type, f.message);
								if (a) {
									a.call(c, f)
								}
							} else {
								Global.notify("error", f, "表单处理异常，请联系管理员");
								if (a) {
									a.call(c, f)
								}
							}
						}
					},
					error : function(j, h, f) {
						App.unblockUI(c);
						var g = jQuery.parseJSON(j.responseText);
						if (g.type == "error") {
							bootbox.alert(g.message)
						} else {
							Global.notify("error", g, "表单处理异常，请联系管理员")
						}
						if (a) {
							a.call(c, g)
						}
					}
				})
			};
			$.fn.popupDialog = function(j) {
				var d = $(this);
				var a = d.attr("href");
				if (a == undefined) {
					a = d.attr("data-url")
				}
				var e = d.attr("title");
				if (e == undefined) {
					e = "对话框"
				}
				var h = d.attr("modal-size");
				if (h == undefined) {
					h = "modal-full"
				} else {
					if (h == "auto") {
						h = ""
					} else {
						h = "modal-" + h
					}
				}
				var j = $.extend({
					url : a,
					postData : {},
					title : e,
					size : h
				}, j);
				Util.assertNotBlank(j.url);
				var g = "dialog_level_" + $("modal:visible").size();
				var b = $("#" + g);
				if (b.length == 0) {
					var c = [];
					c
							.push('<div id="'
									+ g
									+ '" class="modal fade" tabindex="-1" role="basic" aria-hidden="true" >');
					c.push('<div class="modal-dialog ' + j.size + '">');
					c.push('<div class="modal-content">');
					c.push('<div class="modal-header">');
					c
							.push('<button type="button" class="close"  data-dismiss="modal" aria-hidden="true"></button>');
					c
							.push('<button type="button" class="close btn-reload" style="margin-left:10px;margin-right:10px;margin-top:-3px!important;height:16px;width:13px;background-image: url(\''
									+ WEB_ROOT
									+ "/assets/img/portlet-reload-icon.png')!important;\"></button>");
					c.push('<h4 class="modal-title">' + j.title + "</h4>");
					c.push("</div>");
					c.push('<div class="modal-body">');
					c.push("</div>");
					c.push('<div class="modal-footer hide">');
					c
							.push('<button type="button" class="btn default" data-dismiss="modal">关闭窗口</button>');
					c.push("</div>");
					c.push("</div>");
					c.push("</div>");
					c.push("</div>");
					var f = d.closest(".panel-content");
					if (f == undefined) {
						f = $(".page-container:first")
					}
					var b = $(c.join("")).appendTo($("body"));
					b.find(" > .modal-dialog > .modal-content > .modal-body")
							.ajaxGetUrl(j.url, false, j.postData);
					b.modal();
					b
							.find(
									" > .modal-dialog > .modal-content > .modal-header > .btn-reload")
							.click(
									function() {
										b
												.find(
														" > .modal-dialog > .modal-content > .modal-body")
												.ajaxGetUrl(j.url, false,
														j.postData)
									})
				} else {
					b.find(" > .modal-dialog > .modal-content > .modal-body")
							.ajaxGetUrl(j.url, false, j.postData);
					b.modal("show")
				}
				if (j.callback) {
					b.data("callback", j.callback)
				}
			}
		}
	}
}();
var BooleanUtil = function() {
	return {
		toBoolean : function(b) {
			if (b) {
				var a = $.type(b);
				if (a === "string"
						&& (b == "true" || b == "1" || b == "y" || b == "yes"
								|| b == "readonly" || b == "checked"
								|| b == "enabled" || b == "enable" || b == "selected")) {
					return true
				} else {
					if (a === "number" && (b == 1)) {
						return true
					}
				}
			}
			return false
		}
	}
}();
var MathUtil = function() {
	return {
		mul : function(arg1, arg2) {
			if (arg1 == undefined) {
				arg1 = 0
			}
			var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
			try {
				m += s1.split(".")[1].length
			} catch (e) {
			}
			try {
				m += s2.split(".")[1].length
			} catch (e) {
			}
			return Number(s1.replace(".", "")) * Number(s2.replace(".", ""))
					/ Math.pow(10, m)
		},
		div : function(arg1, arg2, fix) {
			if (fix == undefined) {
				fix = 2
			}
			var t1 = 0, t2 = 0, r1, r2;
			try {
				t1 = arg1.toString().split(".")[1].length
			} catch (e) {
			}
			try {
				t2 = arg2.toString().split(".")[1].length
			} catch (e) {
			}
			with (Math) {
				r1 = Number(arg1.toString().replace(".", ""));
				r2 = Number(arg2.toString().replace(".", ""));
				return MathUtil.mul((r1 / r2), pow(10, t2 - t1)).toFixed(fix)
			}
		},
		add : function(arg1, arg2) {
			if (arg1 == undefined) {
				arg1 = 0
			}
			if (arg2 == undefined) {
				arg2 = 0
			}
			var r1, r2, m, c;
			try {
				r1 = arg1.toString().split(".")[1].length
			} catch (e) {
				r1 = 0
			}
			try {
				r2 = arg2.toString().split(".")[1].length
			} catch (e) {
				r2 = 0
			}
			c = Math.abs(r1 - r2);
			m = Math.pow(10, Math.max(r1, r2));
			if (c > 0) {
				var cm = Math.pow(10, c);
				if (r1 > r2) {
					arg1 = Number(arg1.toString().replace(".", ""));
					arg2 = Number(arg2.toString().replace(".", "")) * cm
				} else {
					arg1 = Number(arg1.toString().replace(".", "")) * cm;
					arg2 = Number(arg2.toString().replace(".", ""))
				}
			} else {
				arg1 = Number(arg1.toString().replace(".", ""));
				arg2 = Number(arg2.toString().replace(".", ""))
			}
			return MathUtil.div((arg1 + arg2), m)
		},
		sub : function(arg1, arg2) {
			return MathUtil.add(arg1, -Number(arg2))
		}
	}
}();
function scanBarcodeCallback(b, a) {
	$("#" + b).trigger("barcode", [ a ])
};