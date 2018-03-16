$.fn.fmatter.showlink = function(d, b, h) {
	var g = {
		baseLinkUrl : b.baseLinkUrl,
		showAction : b.showAction,
		addParam : b.addParam || "",
		target : b.target,
		idName : b.idName
	}, c = "", f;
	if (b.colModel !== undefined && b.colModel.formatoptions !== undefined) {
		g = $.extend({}, g, b.colModel.formatoptions)
	}
	if (g.target) {
		c = " target=" + g.target
	}
	if (g.title) {
		title = " title=" + g.title
	}
	var e = null;
	if (g.idValue == "id") {
		e = b.rowId
	} else {
		e = h[g.idValue];
		if ((e == undefined || e == "") && g.idValue.indexOf(".") > -1) {
			var a = h;
			$.each(g.idValue.split("."), function(l, m) {
				a = a[m]
			});
			e = a
		}
	}
	f = g.baseLinkUrl + g.showAction + "?" + g.idName + "=" + e + g.addParam;
	if ($.fmatter.isString(d) || $.fmatter.isNumber(d)) {
		return "<a " + c + title + ' href="' + f + '">' + d + "</a>"
	}
	return $.fn.fmatter.defaultFormat(d, b)
};
var Grid = function() {
	var a = false;
	return {
		initGridDefault : function() {
			$.extend($.ui.multiselect, {
				locale : {
					addAll : "全部添加",
					removeAll : "全部移除",
					itemsCount : "已选择项目列表"
				}
			});
			$.extend($.jgrid.ajaxOptions, {
				dataType : "json"
			});
			$.extend($.jgrid.defaults, {
				datatype : "json",
				loadui : false,
				loadonce : false,
				filterToolbar : {},
				ignoreCase : true,
				prmNames : {
					npage : "npage"
				},
				jsonReader : {
					repeatitems : false,
					root : "content",
					total : "totalPages",
					records : "totalElements"
				},
				treeReader : {
					level_field : "extraAttributes.level",
					parent_id_field : "extraAttributes.parent",
					leaf_field : "extraAttributes.isLeaf",
					expanded_field : "extraAttributes.expanded",
					loaded : "extraAttributes.loaded",
					icon_field : "extraAttributes.icon"
				},
				autowidth : true,
				rowNum : 15,
				page : 1,
				altclass : "ui-jqgrid-evennumber",
				height : "stretch",
				viewsortcols : [ true, "vertical", true ],
				mtype : "GET",
				viewrecords : true,
				rownumbers : true,
				toppager : true,
				recordpos : "left",
				gridview : true,
				altRows : true,
				sortable : false,
				multiboxonly : true,
				multiselect : true,
				multiSort : false,
				forceFit : false,
				shrinkToFit : true,
				sortorder : "desc",
				sortname : "createdDate",
				ajaxSelectOptions : {
					cache : true
				},
				loadError : function(d, e, b, c) {
					Global.notify("error", "表格数据加载处理失败,请尝试刷新或联系管理员!")
				},
				subGridOptions : {
					reloadOnExpand : false
				}
			});
			$.extend($.jgrid.search, {
				multipleSearch : true,
				multipleGroup : true,
				width : 700,
				jqModal : false,
				searchOperators : true,
				stringResult : true,
				searchOnEnter : true,
				defaultSearch : "bw",
				operandTitle : "点击选择查询方式",
				odata : [ {
					oper : "eq",
					text : "等于\u3000\u3000"
				}, {
					oper : "ne",
					text : "不等\u3000\u3000"
				}, {
					oper : "lt",
					text : "小于\u3000\u3000"
				}, {
					oper : "le",
					text : "小于等于"
				}, {
					oper : "gt",
					text : "大于\u3000\u3000"
				}, {
					oper : "ge",
					text : "大于等于"
				}, {
					oper : "bw",
					text : "开始于"
				}, {
					oper : "bn",
					text : "不开始于"
				}, {
					oper : "in",
					text : "属于\u3000\u3000"
				}, {
					oper : "ni",
					text : "不属于"
				}, {
					oper : "ew",
					text : "结束于"
				}, {
					oper : "en",
					text : "不结束于"
				}, {
					oper : "cn",
					text : "包含\u3000\u3000"
				}, {
					oper : "nc",
					text : "不包含"
				}, {
					oper : "nu",
					text : "不存在"
				}, {
					oper : "nn",
					text : "存在"
				}, {
					oper : "bt",
					text : "介于"
				} ],
				operands : {
					eq : "=",
					ne : "!",
					lt : "<",
					le : "<=",
					gt : ">",
					ge : ">=",
					bw : "^",
					bn : "!^",
					"in" : "=",
					ni : "!=",
					ew : "|",
					en : "!@",
					cn : "~",
					nc : "!~",
					nu : "#",
					nn : "!#",
					bt : "~~"
				}
			});
			$.extend($.jgrid.del, {
				serializeDelData : function(b) {
					b.ids = b.id;
					b.id = "";
					return b
				},
				errorTextFormat : function(c) {
					var b = jQuery.parseJSON(c.responseText);
					return b.message
				},
				afterComplete : function(d) {
					var c = new Array();
					var b = jQuery.parseJSON(d.responseText);
					if (b.type == "success") {
						top.$.publishMessage(b.message);
						c[0] = true
					} else {
						top.$.publishError(b.message);
						c[0] = false
					}
					return c
				},
				ajaxDelOptions : {
					dataType : "json"
				}
			});
			$.jgrid
					.extend({
						bindKeys : function(b) {
							var c = $.extend({
								upKey : true,
								downKey : true,
								onEnter : null,
								onSpace : null,
								onLeftKey : null,
								onRightKey : null,
								scrollingRows : true
							}, b || {});
							return this
									.each(function() {
										var d = this;
										if (!$("body").is("[role]")) {
											$("body").attr("role",
													"application")
										}
										d.p.scrollrows = c.scrollingRows;
										$(d)
												.keydown(
														function(h) {
															var l = $(d)
																	.find(
																			"tr[tabindex=0]")[0], n, g, m, f = d.p.treeReader.expanded_field;
															if (l) {
																m = d.p._index[$.jgrid
																		.stripPref(
																				d.p.idPrefix,
																				l.id)];
																if (h.keyCode === 37
																		|| h.keyCode === 38
																		|| h.keyCode === 39
																		|| h.keyCode === 40) {
																	if (h.keyCode === 38
																			&& c.upKey) {
																		g = l.previousSibling;
																		n = "";
																		if (g) {
																			if ($(
																					g)
																					.is(
																							":hidden")) {
																				while (g) {
																					g = g.previousSibling;
																					if (!$(
																							g)
																							.is(
																									":hidden")
																							&& $(
																									g)
																									.hasClass(
																											"jqgrow")) {
																						n = g.id;
																						break
																					}
																				}
																			} else {
																				n = g.id
																			}
																		}
																		$(d)
																				.jqGrid(
																						"setSelection",
																						n,
																						true,
																						h);
																		h
																				.preventDefault()
																	}
																	if (h.keyCode === 40
																			&& c.downKey) {
																		g = l.nextSibling;
																		n = "";
																		if (g) {
																			if ($(
																					g)
																					.is(
																							":hidden")) {
																				while (g) {
																					g = g.nextSibling;
																					if (!$(
																							g)
																							.is(
																									":hidden")
																							&& $(
																									g)
																									.hasClass(
																											"jqgrow")) {
																						n = g.id;
																						break
																					}
																				}
																			} else {
																				n = g.id
																			}
																		}
																		$(d)
																				.jqGrid(
																						"setSelection",
																						n,
																						true,
																						h);
																		h
																				.preventDefault()
																	}
																	if (h.keyCode === 37) {
																		if (d.p.treeGrid
																				&& d.p.data[m][f]) {
																			$(l)
																					.find(
																							"div.treeclick")
																					.trigger(
																							"click")
																		}
																		$(d)
																				.triggerHandler(
																						"jqGridKeyLeft",
																						[ d.p.selrow ]);
																		if ($
																				.isFunction(c.onLeftKey)) {
																			c.onLeftKey
																					.call(
																							d,
																							d.p.selrow)
																		}
																	}
																	if (h.keyCode === 39) {
																		if (d.p.treeGrid
																				&& !d.p.data[m][f]) {
																			$(l)
																					.find(
																							"div.treeclick")
																					.trigger(
																							"click")
																		}
																		$(d)
																				.triggerHandler(
																						"jqGridKeyRight",
																						[ d.p.selrow ]);
																		if ($
																				.isFunction(c.onRightKey)) {
																			c.onRightKey
																					.call(
																							d,
																							d.p.selrow)
																		}
																	}
																} else {
																	if (h.keyCode === 13) {
																		var e = h.target;
																		if (e.tagName === "TEXTAREA") {
																			return true
																		}
																		h
																				.stopPropagation();
																		$(d)
																				.triggerHandler(
																						"jqGridKeyEnter",
																						[ d.p.selrow ]);
																		if ($
																				.isFunction(c.onEnter)) {
																			c.onEnter
																					.call(
																							d,
																							d.p.selrow)
																		}
																	} else {
																		if (h.keyCode === 32) {
																			h
																					.stopPropagation();
																			$(d)
																					.triggerHandler(
																							"jqGridKeySpace",
																							[ d.p.selrow ]);
																			if ($
																					.isFunction(c.onSpace)) {
																				c.onSpace
																						.call(
																								d,
																								d.p.selrow)
																			}
																		} else {
																			if (h.keyCode === 27) {
																				h
																						.stopPropagation();
																				$(
																						d)
																						.jqGrid(
																								"restoreRow",
																								d.p.selrow,
																								c.afterrestorefunc);
																				$(
																						d)
																						.jqGrid(
																								"showAddEditButtons")
																			}
																		}
																	}
																}
															}
														})
									})
						},
						refresh : function() {
							this.each(function() {
								var b = this;
								if (!b.grid) {
									return
								}
								$(b).jqGrid("setGridParam", {
									datatype : "json"
								}).trigger("reloadGrid")
							})
						},
						search : function(b) {
							this.each(function() {
								var e = this;
								if (!e.grid) {
									return
								}
								var c = $(e).jqGrid("getGridParam", "url");
								for ( var d in b) {
									c = AddOrReplaceUrlParameter(c, d, b[d])
								}
								$(e).jqGrid("setGridParam", {
									url : c,
									page : 1
								}).trigger("reloadGrid")
							})
						},
						exportExcelLocal : function(b) {
							this
									.each(function() {
										var f = this;
										if (!f.grid) {
											return
										}
										if (!confirm("确认导出当前页面 " + f.p.caption
												+ " 数据为Excel下载文件？")) {
											return
										}
										var e = new Array();
										e = $(f).getDataIDs();
										var m = f.p.colModel;
										var p = f.p.colNames;
										var h = "";
										for (k = 0; k < p.length; k++) {
											var n = m[k];
											if (n.hidedlg || n.hidden
													|| n.disableExport) {
												continue
											}
											h = h + p[k] + "\t"
										}
										h = h + "\n";
										for (i = 0; i < e.length; i++) {
											data = $(f).getRowData(e[i]);
											for (j = 0; j < p.length; j++) {
												var n = m[j];
												if (n.hidedlg || n.hidden
														|| n.disableExport) {
													continue
												}
												var g = data[n.name];
												var l = null;
												if (n.searchoptions
														&& n.searchoptions.value) {
													l = n.searchoptions.value
												} else {
													if (n.editoptions
															&& n.editoptions.value) {
														l = n.editoptions.value
													}
												}
												if (l) {
													g = l[g]
												}
												if (g.indexOf("<") > -1
														&& g.indexOf(">") > -1) {
													g = $(g).text()
												}
												if (g == "") {
													g = data[n.name]
												}
												if (g == "null" || g == null) {
													g = ""
												}
												g = g.replace(/\&nbsp;/g, "");
												h = h + g + "\t"
											}
											h = h + "\n"
										}
										h = h + "\n";
										var c = $(
												'<form method="post" target = "_blank" action="'
														+ WEB_ROOT
														+ '/pub/grid!export"></form>')
												.appendTo($("body"));
										var o = $(
												'<input type="hidden" name="exportDatas"/>')
												.appendTo(c);
										var d = $(
												'<input type="hidden" name="fileName"/>')
												.appendTo(c);
										d.val("export-data.xls");
										o.val(h);
										c.submit();
										c.remove()
									})
						},
						refreshRowIndex : function() {
							var b = $(this);
							$
									.each(
											$(b).jqGrid("getDataIDs"),
											function(c, d) {
												$(b)
														.find("#" + d)
														.find("input,select")
														.each(
																function() {
																	var e = $(
																			this)
																			.attr(
																					"name");
																	$(this)
																			.attr(
																					"name",
																					e
																							.substring(
																									0,
																									e
																											.indexOf("[") + 1)
																							+ c
																							+ e
																									.substring(
																											e
																													.indexOf("]"),
																											e.length))
																})
											})
						},
						getAtLeastOneSelectedItem : function(g) {
							var f = $(this);
							var d = [];
							var e = jQuery(f).jqGrid("getGridParam",
									"selarrrow");
							if (e.length > 0) {
								for (var b = 0; b < e.length; b++) {
									var c = $(
											"#jqg_" + jQuery(f).attr("id")
													+ "_" + e[b]).is(
											":disabled");
									if (!c) {
										d.push(e[b])
									}
								}
							} else {
								var h = jQuery(f).jqGrid("getGridParam",
										"selrow");
								if (h) {
									d.push(h)
								}
							}
							if (g) {
								jQuery(f)
										.find("table.jqsubgrid")
										.each(
												function() {
													var n = $(this).jqGrid(
															"getGridParam",
															"selarrrow");
													for (var l = 0; l < n.length; l++) {
														var m = $(
																"#jqg_"
																		+ jQuery(
																				this)
																				.attr(
																						"id")
																		+ "_"
																		+ e[l])
																.is(":disabled");
														if (!m) {
															d.push(n[l])
														}
													}
												})
							}
							if (d.length == 0) {
								Global.notify("error", "请至少选择一条行项目！");
								return false
							} else {
								return d
							}
						},
						getOnlyOneSelectedItem : function(g) {
							var f = $(this);
							var d = [];
							var e = jQuery(f).jqGrid("getGridParam",
									"selarrrow");
							if (e.length > 0) {
								for (var b = 0; b < e.length; b++) {
									var c = $(
											"#jqg_" + jQuery(f).attr("id")
													+ "_" + e[b]).is(
											":disabled");
									if (!c) {
										d.push(e[b])
									}
								}
							} else {
								var h = jQuery(f).jqGrid("getGridParam",
										"selrow");
								if (h) {
									d.push(h)
								}
							}
							if (d.length == 0) {
								if (g) {
									Global.notify("error", "请选取操作项目")
								}
								return false
							} else {
								if (d.length > 1) {
									Global.notify("error", "只能选择一条操作项目");
									return false
								}
								return d[0]
							}
						},
						getSelectedItem : function() {
							var c = $(this);
							var b = jQuery(c).jqGrid("getGridParam",
									"selarrrow");
							return b.join()
						},
						getSelectedRowdatas : function() {
							var b = $(this);
							var c = [];
							var e = b.jqGrid("getGridParam", "selarrrow");
							if (e) {
								$.each(e, function(g, h) {
									var l = b.jqGrid("getRowData", h);
									l.id = h;
									c.push(l)
								})
							} else {
								var d = b.jqGrid("getGridParam", "selrow");
								if (d) {
									var f = b.jqGrid("getRowData", d);
									f.id = d;
									c.push(f)
								}
							}
							return c
						},
						getSelectedRowdata : function() {
							var b = $(this);
							var c = b.jqGrid("getGridParam", "selrow");
							if (c) {
								return b.jqGrid("getRowData", c)
							}
						},
						getDirtyRowdatas : function() {
							var l = $(this);
							var g = [];
							var f = l.jqGrid("getGridParam", "colModel");
							var d = [];
							$.each(f, function(o, n) {
								if (n.editable) {
									d.push(n.name)
								}
							});
							var b = l.jqGrid("getDataIDs");
							var h = 0;
							$.each(b, function(n, o) {
								if (!Util.startWith(o, "-") && o != "") {
									h++
								}
							});
							var e = BooleanUtil.toBoolean(l.attr("data-clone"));
							$
									.each(
											b,
											function(n, q) {
												var p = l.jqGrid("getRowData",
														q);
												if (BooleanUtil
														.toBoolean(p["extraAttributes.dirtyRow"])) {
													if (Util.startWith(q, "-")) {
														q = ""
													}
													var o = {
														id : q
													};
													$.each(d, function(s, r) {
														o[r] = p[r]
													});
													o._arrayIndex = p._arrayIndex;
													if (p["extraAttributes.operation"]) {
														o["extraAttributes.operation"] = p["extraAttributes.operation"]
													}
													g.push(o)
												}
											});
							var m = l.jqGrid("getGridParam",
									"batchEntitiesPrefix");
							if (m) {
								var c = {};
								$.each(g, function(n, p) {
									var o = p._arrayIndex;
									delete p._arrayIndex;
									if (o == undefined || o == "") {
										o = h;
										h++
									}
									$.each(p, function(r, q) {
										c[m + "[" + o + "]." + r] = q
									})
								});
								return c
							}
							return g
						},
						insertNewRowdata : function(f) {
							var b = $(this);
							var e = null;
							var d = b.jqGrid("getDataIDs");
							$
									.each(
											d,
											function(g, l) {
												var h = b.jqGrid("getRowData",
														l);
												if (!BooleanUtil
														.toBoolean(h["extraAttributes.dirtyRow"])) {
													e = l;
													return false
												}
											});
							var c = -Math.floor(new Date().getTime()
									+ Math.random() * 100 + 100);
							f["extraAttributes.dirtyRow"] = true;
							if (e) {
								b.jqGrid("addRowData", c, f, "before", e)
							} else {
								b.jqGrid("addRowData", c, f, "last")
							}
							return c
						},
						setEditingRowdata : function(l, c) {
							var b = $(this);
							var d = b.find("tbody");
							for ( var f in l) {
								var g = "input[name='" + f + "'],select[name='"
										+ f + "'],textarea[name='" + f + "']";
								var e = d.find(g);
								if (c == false) {
									if ($.trim(e.val()) != "") {
										continue
									}
								}
								var h = l[f];
								e.val(h).attr("title", h);
								if (e.is("select")) {
									e.select2({
										openOnEnter : false,
										placeholder : "请选择...",
										matcher : function(n, q) {
											var m = makePy(q) + "";
											var p = m.toUpperCase().indexOf(
													n.toUpperCase()) == 0;
											var o = q.toUpperCase().indexOf(
													n.toUpperCase()) == 0;
											return (p || o)
										}
									})
								}
							}
						},
						getEditingRowdata : function() {
							var b = $(this);
							var d = b.find("tbody");
							var e = {};
							var c = "input,select,textarea";
							d.find(c).each(function() {
								var f = $(this);
								e[f.attr("name")] = f.val()
							});
							return e
						},
						isEditingMode : function(d) {
							var b = $(this);
							var c = b.find('tr[editable="1"]');
							if (c.size() > 0) {
								if (d == undefined) {
									return true
								}
								if (d === true) {
									alert("请先保存或取消正在编辑的表格数据行项后再操作")
								} else {
									alert(d)
								}
								return true
							}
							return false
						},
						sumColumn : function(d, f) {
							var b = $(this);
							if (f == undefined) {
								f = 2
							}
							var e = b.jqGrid("getCol", d, false, "sum");
							var c = Math.pow(10, f);
							return Math.round(e * c) / c
						},
						getDataFromBindSeachForm : function(d) {
							var b = $(this);
							var c = b.jqGrid("getGridParam",
									"bindSearchFormData");
							var e = c[d];
							return e
						},
						inlineNav : function(b, c) {
							c = $.extend(true, {
								edit : true,
								editicon : "ui-icon-pencil",
								add : true,
								addicon : "ui-icon-plus",
								save : true,
								saveicon : "ui-icon-disk",
								cancel : true,
								cancelicon : "ui-icon-cancel",
								del : true,
								delicon : "ui-icon-trash",
								addParams : {
									addRowParams : {
										extraparam : {}
									}
								},
								editParams : {},
								restoreAfterSelect : true
							}, $.jgrid.nav, c || {});
							return this
									.each(function() {
										if (!this.grid) {
											return
										}
										var n = this, f, l = $.jgrid
												.jqID(n.p.id), e = $(
												n.p.toppager).attr("id");
										n.p._inlinenav = true;
										if (c.addParams.useFormatter === true) {
											var d = n.p.colModel, h;
											for (h = 0; h < d.length; h++) {
												if (d[h].formatter
														&& d[h].formatter === "actions") {
													if (d[h].formatoptions) {
														var m = {
															keys : false,
															onEdit : null,
															onSuccess : null,
															afterSave : null,
															onError : null,
															afterRestore : null,
															extraparam : {},
															url : null
														}, g = $
																.extend(
																		m,
																		d[h].formatoptions);
														c.addParams.addRowParams = {
															keys : g.keys,
															oneditfunc : g.onEdit,
															successfunc : g.onSuccess,
															url : g.url,
															extraparam : g.extraparam,
															aftersavefunc : g.afterSave,
															errorfunc : g.onError,
															afterrestorefunc : g.afterRestore
														}
													}
													break
												}
											}
										}
										$(n).jqGrid("navSeparatorAdd", b);
										if (n.p.toppager) {
											$(n).jqGrid("navSeparatorAdd",
													n.p.toppager)
										}
										if (c.add) {
											$(n)
													.jqGrid(
															"navButtonAdd",
															b,
															{
																caption : c.addtext,
																title : c.addtitle,
																buttonicon : c.addicon,
																id : n.p.id
																		+ "_iladd",
																onClickButton : function() {
																	var q = $(n)
																			.getOnlyOneSelectedItem(
																					false);
																	if (q) {
																		var r = $(
																				n)
																				.getRowData(
																						q);
																		c.addParams.initdata = r;
																		var o = n.p.colModel, p;
																		for (p = 0; p < o.length; p++) {
																			if (o[p].editcopy == false) {
																				delete c.addParams.initdata[o[p].name]
																			} else {
																				if (o[p].editcopy == "append") {
																					c.addParams.initdata[o[p].name] = r[o[p].name]
																							+ "_COPY"
																				}
																			}
																		}
																		$(n)
																				.jqGrid(
																						"resetSelection")
																	} else {
																		c.addParams.initdata = {
																			id : ""
																		}
																	}
																	c.addParams.rowID = -(new Date()
																			.getTime());
																	$(n)
																			.jqGrid(
																					"addRow",
																					c.addParams);
																	if (!c.addParams.useFormatter) {
																		$(
																				"#"
																						+ l
																						+ "_ilsave")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_ilcancel")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_iladd")
																				.addClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_iledit")
																				.addClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_ilsave")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_ilcancel")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_iladd")
																				.addClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_iledit")
																				.addClass(
																						"ui-state-disabled")
																	}
																}
															});
											if (n.p.toppager) {
												$(n)
														.jqGrid(
																"navButtonAdd",
																n.p.toppager,
																{
																	caption : c.addtext,
																	title : c.addtitle,
																	buttonicon : c.addicon,
																	id : e
																			+ "_iladd",
																	onClickButton : function() {
																		$(
																				".ui-icon-plus",
																				$(n.p.pager))
																				.click()
																	}
																})
											}
										}
										if (c.edit) {
											$(n)
													.jqGrid(
															"navButtonAdd",
															b,
															{
																caption : c.edittext,
																title : c.edittitle,
																buttonicon : c.editicon,
																id : n.p.id
																		+ "_iledit",
																onClickButton : function() {
																	var o = $(n)
																			.getOnlyOneSelectedItem();
																	if (o) {
																		if ($(
																				"#"
																						+ o,
																				$(n))
																				.hasClass(
																						"not-editable-row")) {
																			alert("提示：当前行项不可编辑");
																			return
																		}
																		$(n)
																				.jqGrid(
																						"editRow",
																						o,
																						c.editParams);
																		$(
																				"#"
																						+ l
																						+ "_ilsave")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_ilcancel")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_iladd")
																				.addClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_iledit")
																				.addClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_ilsave")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_ilcancel")
																				.removeClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_iladd")
																				.addClass(
																						"ui-state-disabled");
																		$(
																				"#"
																						+ l
																						+ "_toppager_iledit")
																				.addClass(
																						"ui-state-disabled")
																	}
																}
															});
											if (n.p.toppager) {
												$(n)
														.jqGrid(
																"navButtonAdd",
																n.p.toppager,
																{
																	caption : c.edittext,
																	title : c.edittitle,
																	buttonicon : c.editicon,
																	id : e
																			+ "_iledit",
																	onClickButton : function() {
																		$(
																				".ui-icon-pencil",
																				$(n.p.pager))
																				.click()
																	}
																})
											}
										}
										if (c.save) {
											$(n)
													.jqGrid(
															"navButtonAdd",
															b,
															{
																caption : c.savetext
																		|| "",
																title : c.savetitle
																		|| "保存编辑行项",
																buttonicon : c.saveicon,
																id : n.p.id
																		+ "_ilsave",
																onClickButton : function() {
																	var p = n.p.savedRow[0] ? n.p.savedRow[0].id
																			: false;
																	if (p) {
																		var o = n.p.prmNames, r = o.oper, q = {};
																		if ($(
																				"#"
																						+ $.jgrid
																								.jqID(p),
																				"#"
																						+ l)
																				.hasClass(
																						"jqgrid-new-row")) {
																			c.addParams.addRowParams.extraparam[r] = o.addoper;
																			q = c.addParams.addRowParams;
																			q.extraparam.id = ""
																		} else {
																			if (!c.editParams.extraparam) {
																				c.editParams.extraparam = {}
																			}
																			c.editParams.extraparam[r] = o.editoper;
																			q = c.editParams
																		}
																		q.extraparam["extraAttributes.dirtyRow"] = true;
																		if ($(n)
																				.jqGrid(
																						"saveRow",
																						p,
																						q)) {
																			$(n)
																					.jqGrid(
																							"showAddEditButtons")
																		}
																	}
																}
															});
											$("#" + l + "_ilsave").addClass(
													"ui-state-disabled");
											if (n.p.toppager) {
												$(n)
														.jqGrid(
																"navButtonAdd",
																n.p.toppager,
																{
																	caption : c.savetext
																			|| "",
																	title : c.savetitle
																			|| "保存编辑行项",
																	buttonicon : c.saveicon,
																	id : e
																			+ "_ilsave",
																	onClickButton : function() {
																		$(
																				".ui-icon-disk",
																				$(n.p.pager))
																				.click()
																	}
																});
												$("#" + l + "_toppager_ilsave")
														.addClass(
																"ui-state-disabled")
											}
										}
										if (c.cancel) {
											$(n)
													.jqGrid(
															"navButtonAdd",
															b,
															{
																caption : c.canceltext
																		|| "",
																title : c.canceltitle
																		|| "放弃正在编辑行项",
																buttonicon : c.cancelicon,
																id : n.p.id
																		+ "_ilcancel",
																onClickButton : function() {
																	var p = n.p.savedRow[0] ? n.p.savedRow[0].id
																			: false, o = {};
																	if (p) {
																		if ($(
																				"#"
																						+ $.jgrid
																								.jqID(p),
																				"#"
																						+ l)
																				.hasClass(
																						"jqgrid-new-row")) {
																			o = c.addParams.addRowParams
																		} else {
																			o = c.editParams
																		}
																		$(n)
																				.jqGrid(
																						"restoreRow",
																						p,
																						o)
																	}
																	$(n)
																			.jqGrid(
																					"resetSelection");
																	$(n)
																			.jqGrid(
																					"showAddEditButtons")
																}
															});
											if (n.p.toppager) {
												$(n)
														.jqGrid(
																"navButtonAdd",
																n.p.toppager,
																{
																	caption : c.canceltext
																			|| "",
																	title : c.canceltitle
																			|| "放弃正在编辑行项",
																	buttonicon : c.cancelicon,
																	id : e
																			+ "_ilcancel",
																	onClickButton : function() {
																		$(
																				".ui-icon-cancel",
																				$(n.p.pager))
																				.click()
																	}
																})
											}
										}
										if (c.del) {
											$(n).jqGrid("navSeparatorAdd", b);
											$(n)
													.jqGrid(
															"navButtonAdd",
															b,
															{
																caption : c.deltext
																		|| "",
																title : c.deltitle
																		|| "删除所选行项",
																buttonicon : c.delicon,
																id : n.p.id
																		+ "_ildel",
																onClickButton : function() {
																	if (!$(this)
																			.hasClass(
																					"ui-state-disabled")) {
																		var p = $(
																				n)
																				.getAtLeastOneSelectedItem();
																		if (p) {
																			$(n)
																					.jqGrid(
																							"restoreRow",
																							p);
																			if ($
																					.isFunction(c.delfunc)) {
																				c.delfunc
																						.call(
																								n,
																								p)
																			} else {
																				if (n.p.delurl == undefined
																						|| n.p.delurl == "clientArray") {
																					$
																							.each(
																									p,
																									function(
																											r,
																											u) {
																										if (Util
																												.startWith(
																														u,
																														"-")) {
																											$(
																													n)
																													.jqGrid(
																															"delRowData",
																															u)
																										} else {
																											var s = $(
																													n)
																													.find(
																															"#"
																																	+ u);
																											var t = $(
																													n)
																													.jqGrid(
																															"getRowData",
																															u);
																											for ( var q in t) {
																												if (q == "id"
																														|| Util
																																.endWith(
																																		q,
																																		".id")
																														|| q == "_arrayIndex") {
																												} else {
																													if (q == "extraAttributes.dirtyRow") {
																														t[q] = true
																													} else {
																														if (q == "extraAttributes.operation") {
																															t[q] = "remove"
																														} else {
																															t[q] = ""
																														}
																													}
																												}
																											}
																											$(
																													n)
																													.jqGrid(
																															"setRowData",
																															u,
																															t);
																											s
																													.hide()
																										}
																										if (n.p.afterInlineDeleteRow) {
																											n.p.afterInlineDeleteRow
																													.call(
																															$(n),
																															u)
																										}
																									})
																				} else {
																					var p = $(
																							n)
																							.getAtLeastOneSelectedItem();
																					if (p) {
																						var o = Util
																								.AddOrReplaceUrlParameter(
																										n.p.delurl,
																										"ids",
																										p
																												.join(","));
																						$(
																								n)
																								.ajaxPostURL(
																										{
																											url : o,
																											success : function(
																													q) {
																												$
																														.each(
																																p,
																																function(
																																		r,
																																		t) {
																																	var t = $
																																			.trim(t);
																																	if (q.userdata
																																			&& q.userdata[t]) {
																																		var s = $(
																																				n)
																																				.find(
																																						"tr.jqgrow[id='"
																																								+ t
																																								+ "']");
																																		var u = q.userdata[t];
																																		s
																																				.pulsate({
																																					color : "#bf1c56",
																																					repeat : 3
																																				})
																																	} else {
																																		$(
																																				n)
																																				.jqGrid(
																																						"delRowData",
																																						t)
																																	}
																																})
																											},
																											confirmMsg : "确认批量删除所选记录吗？"
																										})
																					}
																				}
																			}
																			$(n)
																					.jqGrid(
																							"showAddEditButtons")
																		} else {
																			$.jgrid
																					.viewModal(
																							"#"
																									+ alertIDs.themodal,
																							{
																								gbox : "#gbox_"
																										+ $.jgrid
																												.jqID(n.p.id),
																								jqm : true
																							});
																			$(
																					"#jqg_alrt")
																					.focus()
																		}
																	}
																	return false
																}
															});
											if (n.p.toppager) {
												$(n).jqGrid("navSeparatorAdd",
														n.p.toppager);
												$(n)
														.jqGrid(
																"navButtonAdd",
																n.p.toppager,
																{
																	caption : c.deltext
																			|| "",
																	title : c.deltitle
																			|| "删除所选行项",
																	buttonicon : c.delicon,
																	id : e
																			+ "_ildel",
																	onClickButton : function() {
																		$(
																				".ui-icon-trash",
																				$(n.p.pager))
																				.click()
																	}
																})
											}
										}
										if (c.restoreAfterSelect === true) {
											if ($
													.isFunction(n.p.beforeSelectRow)) {
												f = n.p.beforeSelectRow
											} else {
												f = false
											}
											n.p.beforeSelectRow = function(q, p) {
												var o = true;
												if (n.p.savedRow.length > 0
														&& n.p._inlinenav === true
														&& (q !== n.p.selrow && n.p.selrow !== null)) {
													if (n.p.selrow === c.addParams.rowID) {
														$(n).jqGrid(
																"delRowData",
																n.p.selrow)
													} else {
														$(n).jqGrid(
																"restoreRow",
																n.p.selrow,
																c.editParams)
													}
													$(n)
															.jqGrid(
																	"showAddEditButtons")
												}
												if (f) {
													o = f.call(n, q, p)
												}
												return o
											}
										}
										$(n).jqGrid("showAddEditButtons")
									})
						},
						showAddEditButtons : function() {
							return this.each(function() {
								if (!this.grid) {
									return
								}
								var b = $.jgrid.jqID(this.p.id);
								$("#" + b + "_ilsave").addClass(
										"ui-state-disabled");
								$("#" + b + "_ilcancel").addClass(
										"ui-state-disabled");
								$("#" + b + "_iladd").removeClass(
										"ui-state-disabled");
								$("#" + b + "_iledit").removeClass(
										"ui-state-disabled");
								$("#" + b + "_toppager_ilsave").addClass(
										"ui-state-disabled");
								$("#" + b + "_toppager_ilcancel").addClass(
										"ui-state-disabled");
								$("#" + b + "_toppager_iladd").removeClass(
										"ui-state-disabled");
								$("#" + b + "_toppager_iledit").removeClass(
										"ui-state-disabled")
							})
						}
					});
			a = true
		},
		initAjax : function(b) {
			if (b == undefined) {
				b = $("body")
			}
			$('table[data-grid="table"],table[data-grid="items"]', b).each(
					function() {
						Grid.initGrid($(this))
					})
		},
		initGrid : function(X, F) {
			if (!a) {
				Grid.initGridDefault()
			}
			var ak = $(X);
			if (ak.hasClass("ui-jqgrid-btable")) {
				return
			}
			if (ak.attr("id") == undefined) {
				ak.attr("id", "grid_" + new Date().getTime())
			}
			if (F == undefined && ak.data("gridOptions") == undefined) {
				alert("Grid options undefined: class=" + ak.attr("class"));
				return
			}
			var x = $.extend(true, {}, ak.data("gridOptions"), F);
			var d = ak.attr("data-grid");
			var I = null;
			var ac = ak.attr("id") + "-context-menu-container";
			var Y = null;
			var f = (d == "items" ? false : true);
			var J = (d == "items" ? false : true);
			var M = $
					.extend(
							true,
							{},
							$.jgrid.defaults,
							{
								formatter : {
									integer : {
										defaultValue : ""
									},
									number : {
										decimalSeparator : ".",
										thousandsSeparator : ",",
										decimalPlaces : 2,
										defaultValue : ""
									},
									currency : {
										decimalSeparator : ".",
										thousandsSeparator : ",",
										decimalPlaces : 2,
										defaultValue : ""
									}
								},
								cmTemplate : {
									sortable : d == "items" ? false : true
								},
								viewsortcols : d == "items" ? [ true,
										"vertical", false ] : [ true,
										"vertical", true ],
								altRows : d == "items" ? false : true,
								hoverrows : d == "items" ? false : true,
								pgbuttons : d == "items" ? false : true,
								pginput : d == "items" ? false : true,
								rowList : d == "items" ? [] : [ 10, 15, 20, 50,
										100, 200, 500, 1000, 2000 ],
								inlineNav : {
									add : x.editurl || d == "items" ? true
											: false,
									edit : x.editurl || d == "items" ? true
											: false,
									del : x.delurl || d == "items" ? true
											: false,
									restoreAfterSelect : d == "items" ? false
											: true,
									addParams : {
										addRowParams : {
											extraparam : {},
											restoreAfterError : false,
											beforeSaveRow : function(c) {
												if (M.beforeInlineSaveRow) {
													M.beforeInlineSaveRow.call(
															ak, c)
												}
											},
											aftersavefunc : function(ax, ay) {
												if (M.editurl == "clientArray") {
													ak.jqGrid("resetSelection");
													if (M.afterInlineSaveRow) {
														M.afterInlineSaveRow
																.call(ak, ax)
													}
													setTimeout(
															function() {
																$("#" + I)
																		.find(
																				".ui-pg-div span.ui-icon-plus")
																		.click()
															}, 200);
													return
												}
												var aw = jQuery
														.parseJSON(ay.responseText);
												if (aw.type == "success"
														|| aw.type == "warning") {
													Global.notify(aw.type,
															aw.message);
													var c = aw.userdata.id;
													ak.find("#" + ax).attr(
															"id", c);
													ak.jqGrid("resetSelection");
													ak
															.jqGrid(
																	"setSelection",
																	c);
													if (M.afterInlineSaveRow) {
														M.afterInlineSaveRow
																.call(ak, ax)
													}
													setTimeout(
															function() {
																$("#" + I)
																		.find(
																				".ui-pg-div span.ui-icon-plus")
																		.click()
															}, 200)
												} else {
													if (aw.type == "failure"
															|| aw.type == "error") {
														Global.notify("error",
																aw.message)
													} else {
														Global
																.notify(
																		"error",
																		"数据处理异常，请联系管理员")
													}
												}
											},
											errorfunc : function(aw, ax) {
												var c = jQuery
														.parseJSON(ax.responseText);
												Global.notify("error",
														c.message)
											}
										}
									},
									editParams : {
										restoreAfterError : false,
										beforeSaveRow : function(c) {
											if (M.beforeInlineSaveRow) {
												M.beforeInlineSaveRow.call(ak,
														c)
											}
										},
										oneditfunc : function(az) {
											var aw = ak.jqGrid("getGridParam",
													"iCol");
											var c = ak.jqGrid("getGridParam",
													"colModel")[aw];
											var ay = ak.find("tr#" + az);
											var aA = ay.find("> td:eq(" + aw
													+ ")");
											var ax = aA
													.find("input:visible:first");
											if (ax.size() > 0
													&& ax.attr("readonly") == undefined) {
												setTimeout(function() {
													ax.focus()
												}, 200)
											} else {
												ay
														.find(
																"input:visible:enabled:first")
														.focus()
											}
										},
										aftersavefunc : function(aw, az) {
											var ay = true;
											if (M.editurl != "clientArray") {
												var c = jQuery
														.parseJSON(az.responseText);
												if (c.type == "success"
														|| c.type == "warning") {
													Global.notify(c.type,
															c.message)
												} else {
													if (c.type == "failure"
															|| c.type == "error") {
														Global.notify("error",
																c.message);
														ay = false
													} else {
														Global
																.notify(
																		"error",
																		"数据处理异常，请联系管理员");
														ay = false
													}
												}
											}
											if (ay) {
												if (M.afterInlineSaveRow) {
													M.afterInlineSaveRow.call(
															ak, aw)
												}
												if (M.editurl != "clientArray") {
													var ax = ak
															.find(
																	"tr.jqgrow[id='"
																			+ aw
																			+ "']")
															.next("tr");
													if (ax.size() > 0) {
														var aA = ax.attr("id");
														ak
																.jqGrid("resetSelection");
														ak.jqGrid(
																"setSelection",
																aA);
														setTimeout(
																function() {
																	$("#" + I)
																			.find(
																					".ui-pg-div span.ui-icon-pencil")
																			.click()
																}, 200)
													}
												}
											}
										},
										errorfunc : function(aw, ax) {
											var c = jQuery
													.parseJSON(ax.responseText);
											Global.notify("error", c.message)
										}
									}
								},
								filterToolbar : J,
								multiselect : f,
								contextMenu : true,
								columnChooser : true,
								exportExcelLocal : true,
								loadBeforeSend : function() {
									App.blockUI(ak.closest(".ui-jqgrid"))
								},
								subGridBeforeExpand : function() {
									var c = ak.closest(".ui-jqgrid-bdiv");
									c.css({
										height : "auto"
									})
								},
								beforeProcessing : function(aw) {
									if (aw && aw.content) {
										var c = 1000;
										$
												.each(
														aw.content,
														function(ax, ay) {
															if (ay.extraAttributes
																	&& ay.extraAttributes.dirtyRow) {
																ay.id = -(c++)
															}
														});
										if (aw.totalElements >= (2147473647 - 10000)) {
											ak.jqGrid("setGridParam", {
												recordtext : "{0} - {1}\u3000"
											})
										}
									}
								},
								loadComplete : function(ax) {
									ak.jqGrid("showAddEditButtons");
									if (ax.total == undefined
											&& ax.totalElements == undefined) {
										alert("表格数据格式不正确");
										return
									}
									if (ax && ax.content) {
										$.each(ax.content, function(ay, az) {
											ak.setRowData(az.id, {
												_arrayIndex : ay
											})
										});
										if (ax.totalElements >= (2147473647 - 10000)) {
											ak
													.closest(".ui-jqgrid")
													.find(
															".ui-pg-table td[id^='last_']")
													.addClass(
															"ui-state-disabled");
											ak
													.closest(".ui-jqgrid")
													.find(
															".ui-pg-table .ui-pg-input")
													.each(
															function() {
																$(this)
																		.parent()
																		.html(
																				$(this))
															})
										}
									}
									if (d == "items"
											&& M.inlineNav.add != false) {
										for (var aw = 1; aw <= 3; aw++) {
											ak.addRowData(-aw, {})
										}
									}
									if (ab == "enable" && M.contextMenu
											&& Y.find("li").length > 0) {
										ak
												.find("tr.jqgrow")
												.each(
														function() {
															$(this)
																	.contextmenu(
																			{
																				target : "#"
																						+ ac,
																				onItem : function(
																						aA,
																						az) {
																					var ay = $(
																							az)
																							.attr(
																									"role-idx");
																					Y
																							.find(
																									'a[role-idx="'
																											+ ay
																											+ '"]')
																							.click();
																					return true
																				}
																			})
														})
									}
									if (M.footerLocalDataColumn) {
										$.each(M.footerLocalDataColumn,
												function(az, aB) {
													var aA = ak.jqGrid(
															"sumColumn", aB);
													var ay = [];
													ay[aB] = aA;
													ak.footerData("set", ay)
												})
									}
									if (ak.attr("data-selected")) {
										ak.jqGrid("setSelection", ak
												.attr("data-selected"), false)
									}
									var c = x.userLoadComplete;
									if (c) {
										c.call(ak, ax)
									}
									$('[data-hover="dropdown"]',
											ak.closest(".ui-jqgrid"))
											.dropdownHover();
									App.unblockUI(ak.closest(".ui-jqgrid"))
								},
								beforeSelectRow : function(ax) {
									if (M.inlineNav.restoreAfterSelect == false) {
										var aw = ak.jqGrid("getGridParam",
												"selrow");
										var c = ak.find("tr#" + aw).attr(
												"editable");
										if (aw && aw != ax && c == "1") {
											$("#" + I)
													.find(
															".ui-pg-div span.ui-icon-disk")
													.click();
											return false
										}
									}
									return true
								},
								onSelectRow : function(ax, c, aw) {
									ak.find("tr.jqgrow").attr("tabindex", -1);
									ak.find("tr.jqgrow[id='" + ax + "']").attr(
											"tabindex", 0);
									if (d == "items") {
										$("#" + I)
												.find(
														".ui-pg-div span.ui-icon-pencil")
												.click()
									}
								},
								onCellSelect : function(aw, c) {
									ak.jqGrid("setGridParam", {
										iCol : c
									})
								},
								ondblClickRow : function(ay, aA, aw, az) {
									var c = $("#" + I).find("i.fa-edit")
											.parent("a");
									if (c.size() > 0) {
										c.click()
									} else {
										if (d != "items") {
											var ax = $("#" + I)
													.find(
															".ui-pg-div span.ui-icon-pencil");
											if (ax.size() > 0) {
												ax.click()
											} else {
												$("#" + I).find(
														"i.fa-credit-card")
														.parent("a").click()
											}
										}
									}
									az.stopPropagation()
								}
							}, x);
			if ($.isFunction(M.url)) {
				M.url = M.url.call(ak)
			}
			if (M.url == undefined) {
				M.url = ak.attr("data-url")
			}
			if (M.url == undefined) {
				M.datatype = "local"
			}
			if (BooleanUtil.toBoolean(ak.attr("data-readonly"))) {
				M.inlineNav.add = false;
				M.inlineNav.edit = false;
				M.inlineNav.del = false
			}
			if (M.pager == undefined || M.pager) {
				I = ak.attr("id") + "_pager";
				$("<div id='" + I + "'/>").insertAfter(ak);
				M.pager = "#" + I
			} else {
				M.toppager = false
			}
			if (M.toppager) {
				M.toppager = "#" + ak.attr("id") + "_toppager"
			}
			if (M.treeGrid) {
				M.rownumbers = false
			}
			if (x.editurl == undefined && d == "items") {
				M.editurl = "clientArray"
			}
			if (x.delurl == undefined && d == "items") {
				M.delurl = "clientArray"
			}
			if (M.editurl == "clientArray") {
				M.cellsubmit = M.editurl
			} else {
				M.cellurl = M.editurl
			}
			var U = 0;
			var T = false;
			var S = false;
			var V = [];
			$
					.each(
							M.colModel,
							function(ay, c) {
								if (c.frozen) {
									T = true
								}
								c = $
										.extend(
												true,
												{
													editoptions : {
														rows : 1
													},
													searchoptions : {
														clearSearch : false,
														searchhidden : true,
														sopt : [ "cn", "bw",
																"bn", "eq",
																"ne", "nc",
																"ew", "en" ],
														defaultValue : "",
														buildSelect : function(
																aF) {
															var aE = jQuery
																	.parseJSON(aF);
															if (aE == null) {
																aE = aF
															}
															var aD = "<select>";
															aD += "<option value=''></option>";
															for ( var aC in aE) {
																aC = aC + "";
																aD += ("<option value='"
																		+ aC
																		+ "'>"
																		+ aE[aC] + "</option>")
															}
															aD += "</select>";
															return aD
														}
													}
												}, c);
								if (c.name == "id") {
									S = true
								}
								if (c.responsive) {
									if (c.hidden == undefined) {
										var ax = $(window).width();
										var aB = c.responsive;
										if (aB == "sm") {
											if (ax < 768) {
												c.hidden = true
											}
										} else {
											if (aB == "md") {
												if (ax < 992) {
													c.hidden = true
												}
											} else {
												if (aB == "lg") {
													if (ax < 1200) {
														c.hidden = true
													}
												}
											}
										}
									}
								}
								if (c.formatter == "currency") {
									c = $.extend({}, {
										width : 80,
										align : "right"
									}, c);
									c.formatoptions = $.extend({},
											c.formatoptions, {
												decimalSeparator : ".",
												thousandsSeparator : ",",
												decimalPlaces : 2,
												prefix : "",
												defaultValue : ""
											});
									c.searchoptions = $.extend({},
											c.searchoptions, {
												sopt : [ "eq", "ne", "ge",
														"le", "gt", "lt" ]
											})
								}
								if (c.formatter == "percentage") {
									c = $.extend(true, {
										width : 50,
										align : "right"
									}, c);
									c.formatter = function(aE, aC, aF, aD) {
										if (aE) {
											return Math.round(aE * 10000) / 100
													+ "%"
										} else {
											return aE
										}
									}
								}
								if (c.stype == "date" || c.sorttype == "date"
										|| c.formatter == "date"
										|| c.formatter == "timestamp") {
									if (c.formatter == "timestamp") {
										c = $.extend(true, {
											width : 150,
											fixed : true,
											align : "center",
											formatoptions : {
												srcformat : "Y-m-d H:i:s",
												newformat : "Y-m-d H:i:s"
											}
										}, c);
										c.formatter = "date"
									} else {
										c = $.extend(true, {
											width : 120,
											fixed : true,
											align : "center",
											formatoptions : {
												newformat : "Y-m-d"
											}
										}, c)
									}
									c.searchoptions = $
											.extend(
													{},
													c.searchoptions,
													{
														sopt : [ "bt", "eq",
																"ne", "ge",
																"le", "gt",
																"lt" ],
														dataInit : function(aD) {
															var aC = $(aD);
															$(aD)
																	.daterangepicker(
																			$
																					.extend(
																							true,
																							$.fn.daterangepicker.defaults,
																							c.searchoptions.daterangepicker),
																			function(
																					aF,
																					aE) {
																				$(
																						aD)
																						.focus()
																			});
															$(aD).off("focus")
														}
													});
									c.editoptions = $.extend(c.editoptions, {
										dataInit : function(aC) {
											if (c.editoptions.time) {
												$(aC).datetimepicker({
													language : "zh-CN",
													autoclose : true,
													todayBtn : true,
													minuteStep : 10,
													format : "yyyy-mm-dd hh:ii"
												})
											} else {
												$(aC).datepicker({
													language : "zh-CN",
													autoclose : true,
													todayBtn : true,
													format : "yyyy-mm-dd"
												})
											}
										}
									})
								}
								if (c.formatter == "showlink") {
									c = $.extend(true, {
										formatoptions : {
											idValue : "id",
											target : "modal-ajaxify"
										}
									}, c)
								}
								if (c.formatter == "integer") {
									c = $.extend(true, {
										width : 60,
										align : "center",
										formatoptions : {
											defaultValue : ""
										},
										searchoptions : {
											sopt : [ "eq", "ne", "ge", "le",
													"gt", "lt" ]
										}
									}, c)
								}
								if (c.sorttype == "number"
										|| c.edittype == "number"
										|| c.formatter == "number") {
									c = $.extend(true, {
										width : 60,
										align : "right",
										formatoptions : {
											defaultValue : ""
										},
										searchoptions : {
											sopt : [ "eq", "ne", "ge", "le",
													"gt", "lt" ]
										}
									}, c)
								}
								if (c.name == "id") {
									c = $
											.extend(
													true,
													{
														width : 80,
														align : "center",
														title : false,
														formatter : function(
																aF, aD, aH, aE) {
															if (aF
																	&& aF.length > 5) {
																var aC = aF.length;
																var aG = aF
																		.substring(
																				aC - 5,
																				aC);
																return "<span data='"
																		+ aF
																		+ "' onclick='$(this).html($(this).attr(\"data\"))'>..."
																		+ aG
																		+ "</span>"
															} else {
																return "<span>"
																		+ aF
																		+ "</span>"
															}
														},
														frozen : true
													}, c);
									c.searchoptions = $.extend(true,
											c.searchoptions, {
												sopt : [ "eq", "ne", "ge",
														"le", "gt", "lt" ]
											})
								}
								if (c.formatter == "checkbox"
										&& c.edittype == undefined) {
									c.edittype = "checkbox"
								}
								if (c.edittype == "checkbox"
										&& c.formatter == undefined) {
									c.formatter = "checkbox"
								}
								if (c.edittype == "checkbox") {
									c = $.extend(true, {
										width : 60,
										align : "center",
										formatter : "checkbox",
										stype : "select"
									}, c);
									c.searchoptions.value = {
										"" : "",
										"true" : "Y",
										"false" : "N"
									};
									c.editoptions.value = "true:false"
								}
								if (c.edittype == undefined
										|| c.edittype == "text"
										|| c.edittype == "select"
										|| c.edittype == "textarea") {
									var aA = c.editoptions.dataInit;
									c.editoptions = $
											.extend(
													c.editoptions,
													{
														dataInit : function(aD) {
															var aC = $(aD);
															aC
																	.removeClass(
																			"editable")
																	.addClass(
																			"form-control")
																	.attr(
																			"autocomplete",
																			"off")
																	.css(
																			{
																				width : "100%"
																			});
															if (aA) {
																aA.call(this,
																		aD)
															}
															if (c.editoptions.updatable == false) {
																var aE = ak
																		.jqGrid("getSelectedRowdata");
																if (aE && aE.id) {
																	aC
																			.attr(
																					"disabled",
																					true)
																} else {
																	if (!aC
																			.attr("placeholder")) {
																		aC
																				.attr(
																						"placeholder",
																						"创建后不可修改");
																		aC
																				.attr(
																						"title",
																						"创建后不可修改")
																	}
																}
															}
															if (aC
																	.is("input[type='text']")) {
																aC
																		.blur(function() {
																			aC
																					.val($
																							.trim(aC
																									.val()))
																		})
															}
															if (aC.is("select")) {
																aC
																		.select2({
																			openOnEnter : false,
																			placeholder : "请选择...",
																			matcher : function(
																					aG,
																					aJ) {
																				var aF = makePy(aJ)
																						+ "";
																				var aI = aF
																						.toUpperCase()
																						.indexOf(
																								aG
																										.toUpperCase()) == 0;
																				var aH = aJ
																						.toUpperCase()
																						.indexOf(
																								aG
																										.toUpperCase()) == 0;
																				return (aI || aH)
																			}
																		})
															}
															if (c.editoptions.spellto) {
																aC
																		.change(function() {
																			var aF = {};
																			aF[c.editoptions.spellto] = Pinyin
																					.getCamelChars($
																							.trim(aC
																									.val()));
																			ak
																					.jqGrid(
																							"setEditingRowdata",
																							aF)
																		})
															}
														}
													})
								}
								if (c.stype == "select"
										|| c.formatter == "select") {
									c.searchoptions.sopt = [ "eq", "ne" ];
									if (c.edittype == undefined) {
										c.edittype = "select"
									}
									if (c.stype == undefined) {
										c.stype = "select"
									}
									if (c.formatter == undefined) {
										c.formatter = "select"
									}
									c.editoptions = $
											.extend(
													true,
													{
														optionsurl : c.searchoptions.optionsurl,
														value : c.searchoptions.value
													}, c.editoptions)
								}
								if (c.editoptions.optionsurl) {
									c.editoptions.value = Util
											.getCacheSelectOptionDatas(
													c.editoptions.optionsurl,
													ak
															.closest(".panel-content"))
								}
								if (typeof c.editoptions.value === "function") {
									c.editoptions.value = c.editoptions.value
											.call(ak)
								}
								if (c.editoptions.value
										&& c.searchoptions.value == undefined) {
									c.searchoptions.value = c.editoptions.value
								}
								if (!c.hidden) {
									if (c.width) {
										U += c.width
									} else {
										U += 300
									}
								}
								if (c.hasOwnProperty("searchoptions")) {
									var aw = c.searchoptions;
									if (aw.hasOwnProperty("defaultValue")
											&& aw.defaultValue != "") {
										var az = c.index;
										if (az == undefined) {
											az = c.name
										}
										V[V.length++] = {
											field : az,
											op : c.searchoptions.sopt[0],
											data : aw.defaultValue
										}
									}
								}
								M.colModel[ay] = c
							});
			if (!S) {
				M.colModel.push({
					label : "流水号",
					name : "id",
					hidden : true
				});
				if (M.colNames) {
					M.colNames.push("流水号")
				}
			}
			if (d == "items") {
				M.colModel.push({
					name : "extraAttributes.dirtyRow",
					hidden : true,
					hidedlg : true
				});
				if (M.colNames) {
					M.colNames.push("extraAttributes.dirtyRow")
				}
				M.colModel.push({
					name : "_arrayIndex",
					hidedlg : true,
					hidden : true
				});
				if (M.colNames) {
					M.colNames.push("_arrayIndex")
				}
				M.colModel.push({
					name : "extraAttributes.operation",
					hidedlg : true,
					hidden : true
				});
				if (M.colNames) {
					M.colNames.push("extraAttributes.operation")
				}
			}
			var q = $(".theme-panel .grid-shrink-option").val();
			if (q == "true") {
				M.shrinkToFit = true
			} else {
				if (Number(U) > Number(ak.parent().width())) {
					$.each(M.colModel, function(aw, c) {
						if (!c.hidden) {
							if (c.width == undefined) {
								c.width = 300
							}
						}
					});
					M.shrinkToFit = false
				}
			}
			var aj = false;
			if (ak.closest(".ui-subgrid").size() == 0 && d != "items") {
				if (M.height == undefined || M.height == "stretch") {
					aj = true;
					M.height = 0
				}
			}
			if (M.filterToolbar) {
				if (M.postData == undefined) {
					M.postData = {}
				}
				var D = M.postData;
				var B = {};
				if (D.hasOwnProperty("filters")) {
					B = JSON.parse(D.filters)
				}
				var n = [];
				if (B.hasOwnProperty("rules")) {
					n = B.rules
				}
				$.each(V, function(aw, c) {
					var ax = false;
					$.each(n, function(ay, az) {
						if (c.field == az.field) {
							ax = true;
							return
						}
					});
					if (ax == false) {
						n.push(c)
					}
				});
				if (n.length > 0) {
					B.groupOp = "AND";
					B.rules = n;
					D._search = true;
					D.filters = JSON.stringify(B)
				}
			}
			if (M.jqPivot) {
				var R = M.jqPivot;
				delete M.jqPivot;
				var m = M.url;
				M = {
					multiselect : false,
					pager : M.pager,
					shrinkToFit : false
				};
				ak.jqGrid("jqPivot", m, R, M, {
					reader : "content"
				});
				return
			} else {
				ak.jqGrid(M)
			}
			if (M.filterToolbar) {
				ak.jqGrid("filterToolbar", M.filterToolbar);
				var E = $("#jqgh_" + ak.attr("id") + "_rn");
				var Q = '<a href="javascript:;" title="显示快速查询"><span class="ui-icon ui-icon-carat-1-s"></span></a>';
				var Z = '<a href="javascript:;" title="隐藏快速查询"><span class="ui-icon ui-icon-carat-1-n"></span></a>';
				if (ak.is(".ui-jqgrid-subgrid") || M.subGrid
						|| M.filterToolbar == "hidden") {
					E.html(Q);
					ak[0].toggleToolbar()
				} else {
					E.html(Z)
				}
				E.on("click", ".ui-icon-carat-1-s", function() {
					E.html(Z);
					ak[0].toggleToolbar()
				});
				E.on("click", ".ui-icon-carat-1-n", function() {
					E.html(Q);
					ak[0].toggleToolbar()
				})
			}
			if (M.setGroupHeaders) {
				ak.jqGrid("setGroupHeaders", $.extend(true, {
					useColSpanStyle : true
				}, M.setGroupHeaders))
			}
			ak.bindKeys({
				upKey : false,
				downKey : false,
				onEnter : function(aw) {
					if (aw == undefined) {
						return
					}
					ak.find("tr.jqgrow").attr("tabindex", -1);
					var c = ak.find("tr.jqgrow[id='" + aw + "']");
					c.attr("tabindex", 0);
					if (M.editurl) {
						if (c.attr("editable") == "1") {
							N.find(".ui-pg-div span.ui-icon-disk").click()
						} else {
							N.find(".ui-pg-div span.ui-icon-pencil").click()
						}
						return false
					}
				}
			});
			if (M.pager || M.toppager) {
				var N = $(M.pager);
				var au = Util.notSmallViewport();
				if (au) {
					au = (d == "items" ? false : true)
				}
				var am = Util.notSmallViewport();
				if (am) {
					am = (d == "items" ? false : true)
				}
				ak.jqGrid("navGrid", M.pager, {
					edit : false,
					add : false,
					del : false,
					refresh : au,
					search : au,
					position : "right",
					cloneToTop : true
				});
				if (M.columnChooser) {
					var e = {
						caption : "",
						buttonicon : "ui-icon-battery-2",
						position : "first",
						title : "设定显示列和顺序",
						onClickButton : function() {
							var c = ak.jqGrid("getGridParam", "width");
							ak.jqGrid("columnChooser", {
								width : 470,
								done : function(aw) {
									if (aw) {
										this.jqGrid("remapColumns", aw, true);
										ak.jqGrid("setGridWidth", c, false)
									} else {
									}
								}
							})
						}
					};
					if (M.pager) {
						ak.jqGrid("navButtonAdd", M.pager, e)
					}
					if (M.toppager) {
						ak.jqGrid("navButtonAdd", M.toppager, e)
					}
				}
				var at = Util.notSmallViewport();
				if (at) {
					at = (d == "items" ? false : true)
				}
				if (M.exportExcelLocal && at) {
					var y = {
						caption : "",
						buttonicon : "ui-icon-arrowthickstop-1-s",
						position : "first",
						title : "导出当前显示数据",
						onClickButton : function() {
							ak.jqGrid("exportExcelLocal", M.exportExcelLocal)
						}
					};
					if (M.pager) {
						ak.jqGrid("navButtonAdd", M.pager, y)
					}
					if (M.toppager) {
						ak.jqGrid("navButtonAdd", M.toppager, y)
					}
				}
				var O = {
					caption : "",
					buttonicon : "ui-icon-arrowstop-1-w",
					position : "first",
					title : "收缩显示模式",
					onClickButton : function() {
						var c = ak.jqGrid("getGridParam", "width");
						ak.jqGrid("destroyFrozenColumns");
						ak.jqGrid("setGridWidth", c, true)
					}
				};
				if (M.pager) {
					ak.jqGrid("navButtonAdd", M.pager, O)
				}
				if (M.toppager) {
					ak.jqGrid("navButtonAdd", M.toppager, O)
				}
				if (M.gridDnD) {
					var an = $.extend(true, {
						dropbyname : true,
						beforedrop : function(c, ax, aw) {
							aw.id = $(ax.draggable).attr("id");
							return aw
						},
						autoid : function(c) {
							return c.id
						},
						drop_opts : {
							activeClass : "ui-state-active",
							hoverClass : "ui-state-hover",
							greedy : true
						},
						ondrop : function(c, az, aC) {
							var aD = $("#" + this.id);
							var aE = aD.closest(".ui-subgrid");
							var ay = "";
							if (aE.size() > 0) {
								ay = aE.prev(".jqgrow").attr("id")
							}
							var aw = $(az.draggable).attr("id");
							var aA = {};
							var aB = aD.jqGrid("getGridParam", "parent");
							var ax = aD.jqGrid("getGridParam", "editurl");
							aA[aB] = ay;
							aA.id = aw;
							aD.ajaxPostURL({
								url : ax,
								success : function() {
									return true
								},
								confirmMsg : false,
								data : aA
							})
						}
					}, M.gridDnD);
					var u = {
						caption : "",
						buttonicon : "ui-icon-arrow-4",
						position : "first",
						title : "开启拖放移动模式",
						onClickButton : function() {
							var c = null;
							if (ak.closest(".ui-subgrid").size() > 0) {
								$topGrid = ak
										.parent()
										.closest(
												".ui-jqgrid-btable:not(.ui-jqgrid-subgrid)");
								c = $topGrid.parent().find(".ui-jqgrid-btable")
							} else {
								c = ak.parent().find(".ui-jqgrid-btable")
							}
							var ax = [];
							c.each(function(ay, az) {
								ax.push("#" + $(this).attr("id"))
							});
							var aw = ax.reverse();
							$.each(aw, function(az, aC) {
								var aB = $.map(ax, function(aD) {
									return aD != aC ? aD : null
								});
								var ay = $(aC);
								if (aB.length > 0) {
									var aA = $.extend({
										connectWith : aB.join(",")
									}, an);
									ay.jqGrid("gridDnD", aA);
									console.log(aC + "=>" + aA.connectWith)
								}
								if (!ay.hasClass("ui-jqgrid-dndtable")) {
									ay.addClass("ui-jqgrid-dndtable")
								}
							})
						}
					};
					if (M.pager) {
						ak.jqGrid("navButtonAdd", M.pager, u)
					}
					if (M.toppager) {
						ak.jqGrid("navButtonAdd", M.toppager, u)
					}
				}
				if (M.pager
						&& (M.inlineNav.add || M.inlineNav.edit || M.inlineNav.del)
						&& M.inlineNav != false) {
					ak.jqGrid("inlineNav", M.pager, M.inlineNav)
				}
				N.find(".navtable").css("float", "right");
				var w = N.find(" .navtable > tbody > tr");
				ak.jqGrid("navSeparatorAdd", M.pager, {
					position : "first"
				});
				var h = $("<td></td>").prependTo(w);
				var ag = $('<div class="btn-group dropup btn-group-contexts"><button data-close-others="true" data-delay="1000" data-toggle="dropdown" class="btn btn-xs yellow dropdown-toggle" type="button"><i class="fa fa-cog"></i>  <i class="fa fa-angle-down"></i></button></div>');
				h.append(ag);
				ag.wrap('<div class="clearfix jqgrid-options"></div>');
				Y = $('<ul role="menu" class="dropdown-menu"></ul>');
				Y.appendTo(ag);
				var P = [];
				var H = [];
				var A = [];
				if (M.viewurl) {
					var s = $('<li><a href="javascript:;"><i class="fa fa-credit-card"></i> 查看详情</a></li>');
					s.children("a").bind(
							"click",
							function(az) {
								Util.debug(az.target + ":" + az.type);
								az.preventDefault();
								var aB = ak.getOnlyOneSelectedItem();
								if (aB) {
									var ax = "TBD";
									var aA = ak.jqGrid("getRowData", aB);
									if (M.editcol) {
										ax = aA[M.editcol];
										if (ax.indexOf("<") > -1
												&& ax.indexOf(">") > -1) {
											ax = $(ax).text()
										}
									} else {
										ax = aA.id;
										if (ax.indexOf("<span") > -1) {
											ax = $(ax).text()
										}
									}
									var aw = ax.length;
									if (aw > 8) {
										ax = "..." + ax.substring(aw - 5, aw)
									}
									var ay = Util.AddOrReplaceUrlParameter(
											M.viewurl, "id", aB);
									var c = ak.closest(".tabbable").find(
											" > .nav");
									Global.addOrActiveTab(c, {
										title : "查看: " + ax,
										url : ay
									})
								}
							});
					H.push(s)
				}
				if (M.fullediturl) {
					if (M.addable == undefined || M.addable != false) {
						var ae = $(
								'<li><a href="javascript:;" data-toggle="dynamic-tab" data-url="'
										+ M.fullediturl
										+ '"><i class="fa fa-plus-square"></i> 新增数据</a></li>')
								.appendTo(Y);
						P.push(ae);
						var av = $('<li><a href="javascript:;"><i class="fa fa-copy"></i> 克隆复制</a></li>');
						av.children("a").bind(
								"click",
								function(ay) {
									Util.debug(ay.target + ":" + ay.type);
									ay.preventDefault();
									var az = ak.getOnlyOneSelectedItem();
									if (az) {
										var aw = M.cloneurl ? M.cloneurl
												: M.fullediturl;
										var ax = Util.AddOrReplaceUrlParameter(
												aw, "id", az);
										ax = ax + ("&clone=true");
										var c = ak.closest(".tabbable").find(
												" > .nav");
										Global.addOrActiveTab(c, {
											title : "克隆复制",
											url : ax
										})
									}
								});
						H.push(av)
					}
					var r = $('<li><a href="javascript:;"><i class="fa fa-edit"></i> 编辑数据 <span class="badge badge-info">双击</span></a></li>');
					r.children("a").bind(
							"click",
							function(az) {
								Util.debug(az.target + ":" + az.type);
								az.preventDefault();
								var aB = ak.getOnlyOneSelectedItem();
								if (aB) {
									var ax;
									var aA = ak.jqGrid("getRowData", aB);
									if (M.editcol) {
										ax = aA[M.editcol];
										if (ax && ax.indexOf("<") > -1
												&& ax.indexOf(">") > -1) {
											ax = $(ax).text()
										}
									} else {
										ax = aA.id;
										if (ax.indexOf("<span") > -1) {
											ax = $(ax).text()
										}
									}
									if (ax == undefined) {
										ax = "TBD"
									}
									var aw = ax.length;
									if (aw > 8) {
										ax = "..." + ax.substring(aw - 5, aw)
									}
									var ay = Util.AddOrReplaceUrlParameter(
											M.fullediturl, "id", aB);
									var c = ak.closest(".tabbable").find(
											" > .nav");
									Global.addOrActiveTab(c, {
										title : "编辑: " + ax,
										url : ay
									})
								}
							});
					H.push(r)
				}
				if (M.operations) {
					var p = [];
					M.operations.call(ak, p);
					$.each(p, function() {
						var c = $(this);
						var aw = c.attr("data-position");
						if (aw == "multi") {
							A.push(c)
						} else {
							if (aw == "single") {
								H.push(c)
							} else {
								P.push(c)
							}
						}
					})
				}
				if (P.length > 0) {
					$
							.each(
									P,
									function() {
										var az = $(this);
										var aw = az.children("a");
										az.appendTo(Y);
										if (Util.notSmallViewport()) {
											var ay = aw.children("i").attr(
													"class");
											var c = "";
											if (az.attr("data-text") == "show") {
												c = aw.text()
											}
											var ax = $(
													'<button type="button" class="btn btn-xs blue" style="margin-left:5px"><i class="'
															+ ay
															+ '"></i> '
															+ c + "</button>")
													.appendTo(ag.parent());
											ax.attr("title", az.text());
											ax.click(function() {
												aw.click()
											})
										}
									})
				}
				if (H.length > 0) {
					if (Y.find("li").size() > 0) {
						Y.append('<li class="divider"></li>')
					}
					$.each(H, function() {
						var ax = $(this);
						var c = ax.children("a");
						ax.appendTo(Y);
						if (Util.notSmallViewport()
								&& ax.attr("data-toolbar") == "show") {
							var c = ax.children("a");
							var aw = c.clone();
							aw.addClass("btn btn-xs blue");
							aw.css({
								"margin-left" : "5px"
							});
							aw.appendTo(ag.parent());
							aw.click(function(ay) {
								c.click();
								ay.preventDefault();
								return false
							})
						}
					})
				}
				if (A.length > 0) {
					if (Y.find("li").size() > 0) {
						Y.append('<li class="divider"></li>')
					}
					$.each(A, function() {
						var ax = $(this);
						ax.appendTo(Y);
						if (Util.notSmallViewport()
								&& ax.attr("data-toolbar") == "show") {
							var c = ax.children("a");
							var aw = c.clone();
							aw.addClass("btn btn-xs blue");
							aw.css({
								"margin-left" : "5px"
							});
							aw.appendTo(ag.parent());
							aw.click(function(ay) {
								c.click();
								ay.preventDefault();
								return false
							})
						}
					})
				}
				if (Y.find("li").length == 0) {
					ag.hide()
				} else {
					Y.find("li > a").each(function(c) {
						$(this).attr("role-idx", c)
					})
				}
				if (!Util.notSmallViewport()) {
					var o = N
							.find(" > .ui-pager-control > .ui-pg-table > tbody");
					var al = o.find(" > tr > td").eq(0);
					al.attr("align", "left");
					$("<tr/>").appendTo(o).append(al);
					var al = o.find(" > tr > td").eq(0);
					al.attr("align", "left");
					$("<tr/>").appendTo(o).append(al);
					var al = o.find(" > tr > td").eq(0);
					al.find("> .ui-pg-table").css("float", "left");
					N.height("75px")
				} else {
					N.find("#" + N.attr("id") + "_left").css({
						width : "150px"
					})
				}
				if (M.pager && M.toppager) {
					var t = ak.attr("id") + "_toppager";
					var L = $("#" + t);
					ak.jqGrid("navSeparatorAdd", "#" + t, {
						position : "first"
					});
					var b = $("div#" + t + " .ui-pg-table > tbody > tr");
					var G = b.find("#" + t + "_right");
					var ar = N.find(".jqgrid-options").parent("td").clone(true);
					ar.prependTo(G.find("> .ui-pg-table > tbody > tr"));
					ar.find(".btn-group").removeClass("dropup");
					G.prependTo(G.parent());
					var g = b.find("#" + t + "_left");
					g.css({
						width : "150px"
					});
					g.appendTo(g.parent());
					var W = g.find(".ui-paging-info");
					W.css("float", "right");
					L.width(N.width());
					if (!Util.notSmallViewport()) {
						L.hide()
					}
					if (ak.closest(".ui-subgrid").size() > 0) {
						$(M.pager).hide()
					}
				}
				var ab = $(".theme-panel .context-menu-option").val();
				if (ab == "enable" && M.contextMenu && Y.find("li").length > 0) {
					var ad = $('<div id="' + ac
							+ '" class="context-menu"></div>');
					Y.clone().appendTo(ad);
					$("body").append(ad);
					ak.unbind("contextmenu")
				}
			}
			var K = X.jqGrid("getGridParam", "colModel");
			for (var ah = 0; ah < K.length; ah++) {
				var aq = K[ah];
				if (aq.tooltips) {
					var ai = $('<span class="glyphicon glyphicon-exclamation-sign tooltipster"  title="'
							+ aq.tooltips + '"></span>');
					var v = aq.index ? aq.index : aq.name;
					var af = $(".ui-jqgrid-sortable[id*='" + v + "']", z);
					if (af.size() > 0) {
						af.prepend(ai);
						ai.tooltipster({
							contentAsHTML : true,
							offsetY : 5,
							theme : "tooltipster-punk"
						})
					}
				}
			}
			var ap = M.editrulesurl;
			if (ap == undefined && M.editurl && M.editurl != "clientArray") {
				ap = M.editurl.substring(0, M.editurl.indexOf("!"))
						+ "!buildValidateRules"
			}
			if (ap) {
				var z = $("#gbox_" + X.attr("id") + "  .ui-jqgrid-labels");
				z
						.ajaxJsonUrl(
								ap,
								function(az) {
									var aw = X.jqGrid("getGridParam",
											"colModel");
									for ( var ay in az) {
										for (var ax = 0; ax < aw.length; ax++) {
											var aC = aw[ax];
											if ((aC.index && aC.index == ay)
													|| (aC.name && aC.name == ay)) {
												aw[ax].editrules = $.extend(
														az[ay] || {},
														aw[ax].editrules || {});
												if (aw[ax].editrules.required == undefined) {
													aw[ax].editrules.required = false
												}
												delete aC.editrules.timestamp;
												if (aC.editrules.tooltips
														&& aC.tooltips == undefined) {
													var aB = $('<span class="glyphicon glyphicon-exclamation-sign tooltipster"  title="'
															+ aC.editrules.tooltips
															+ '"></span>');
													var aA = $(
															".ui-jqgrid-sortable[id*='"
																	+ ay + "']",
															z);
													if (aA.size() > 0) {
														aA.prepend(aB);
														aB
																.tooltipster({
																	contentAsHTML : true,
																	offsetY : 5,
																	theme : "tooltipster-punk"
																})
													}
													delete aC.editrules.tooltips
												}
												break
											}
										}
									}
								})
			}
			if (aj) {
				var C = $("#gbox_" + X.attr("id"));
				var ao = 0;
				var aa = "div.ui-jqgrid-titlebar,div.ui-jqgrid-hdiv,div.ui-jqgrid-pager,div.ui-jqgrid-toppager,div.ui-jqgrid-sdiv";
				C.find(aa).filter(":visible").each(function() {
					ao += $(this).outerHeight()
				});
				ao = ao + 4;
				var l = $(window).height()
						- ak.closest(".ui-jqgrid").offset().top - ao;
				if (l < 300) {
					l = 300
				}
				ak.setGridHeight(l, true)
			}
			Grid.refreshWidth();
			if (T) {
				ak.jqGrid("setFrozenColumns")
			}
			ak.jqGrid("gridResize", {
				minWidth : 500,
				minHeight : 100
			});
			ak.closest(".ui-jqgrid").find(".ui-resizable-s").dblclick(
					function() {
						var c = ak.jqGrid("getGridParam", "height");
						ak.jqGrid("setGridHeight", ak.height() + 17)
					}).attr("title", "鼠标双击可自动扩展显示区域")
		},
		refreshWidth : function() {
			$("table.ui-jqgrid-btable:visible").each(function() {
				var c = $(this);
				var d = c.jqGrid("getGridParam", "width");
				var b = c.closest("div.ui-jqgrid").parent("div").width();
				if (d != b) {
					c.jqGrid("setGridWidth", b);
					var e = $(this).jqGrid("getGridParam", "groupHeader");
					if (e) {
						c.jqGrid("destroyGroupHeader");
						c.jqGrid("setGroupHeaders", e)
					}
				}
			})
		},
		initRecursiveSubGrid : function(f, c, e, h) {
			var b = $("<table data-grid='table' class='ui-jqgrid-subgrid'/>")
					.appendTo($("#" + f));
			var d = b.closest("table.ui-jqgrid-btable").data("gridOptions");
			d.url = Util.AddOrReplaceUrlParameter(d.url, "search['EQ_" + e
					+ "']", c);
			d.inlineNav = $.extend(true, {
				addParams : {
					addRowParams : {
						extraparam : {}
					}
				}
			}, d.inlineNav);
			d.inlineNav.addParams.addRowParams.extraparam[e] = c;
			d.parent = e;
			if (h) {
				d.postData = {}
			}
			b.data("gridOptions", d);
			Grid.initGrid(b);
			var g = $("#" + f).parent().closest(
					".ui-jqgrid-btable:not(.ui-jqgrid-subgrid)");
			if (d.gridDnD && g.hasClass("ui-jqgrid-dndtable")) {
				$("#" + f).find(".ui-icon-arrow-4:first").click()
			}
		},
		initSubGrid : function(e, d, c) {
			var b = $("<table data-grid='table' class='ui-jqgrid-subgrid'/>")
					.appendTo($("#" + e));
			b.data("gridOptions", c);
			Grid.initGrid(b)
		}
	}
}();