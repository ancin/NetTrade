$(function() {
    $(".grid-auth-privilege-urls").data(
            "gridOptions",
            {
                url : WEB_ROOT + '/auth/privilege!urls',
                colModel : [ {
                    label : 'ģ�����',
                    name : 'namespace',
                    align : 'left',
                    width : 80,
                    hidden : true,
                    fixed : true
                }, {
                    label : 'ģ��',
                    name : 'namespaceLabel',
                    align : 'left',
                    width : 80,
                    fixed : true
                }, {
                    label : 'ҵ�����',
                    name : 'actionName',
                    align : 'left',
                    width : 80,
                    hidden : true,
                    fixed : true
                }, {
                    label : 'ҵ��',
                    name : 'actionNameLabel',
                    align : 'left',
                    width : 100
                }, {
                    label : '��������',
                    name : 'methodName',
                    align : 'left',
                    hidden : true,
                    width : 80
                }, {
                    label : '����',
                    name : 'methodNameLabel',
                    align : 'left',
                    width : 150
                }, {
                    label : 'URL',
                    name : 'url',
                    width : 250,
                    align : 'left'
                }, {
                    label : '�ܿ�',
                    name : 'controlled',
                    formatter : 'checkbox'
                }, {
                    label : '����Ȩ��',
                    name : 'controllPrivilegesJoin',
                    width : 250,
                    align : 'left'
                } ],
                editcol : 'code',
                loadonce : true,
                operations : function(items) {
                    var $grid = $(this);
                    var $copy = $('<li><a href="javascript:;" data-toggle="dynamic-tab"><i class="fa fa-copy"></i> ���ƴ���</a></li>');
                    $copy.children("a").bind(
                            "click",
                            function(e) {
                                var rowdata = $grid.getSelectedRowdata();
                                var url = WEB_ROOT + "/auth/privilege!edit?url=" + encodeURI(rowdata.url) + "&category=" + encodeURI(rowdata.namespaceLabel) + "&title="
                                        + encodeURI(rowdata.actionNameLabel + rowdata.methodNameLabel);
                                $(this).attr("data-url", url);
                            });
                    items.push($copy);
                }
            });
});