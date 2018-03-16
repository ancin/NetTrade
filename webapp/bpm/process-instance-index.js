$(function() {
    $(".grid-bpm-process-instance-index").data("gridOptions", {
        url : WEB_ROOT + '/bpm/process-instance!findByPageRunning',
        colModel : [ {
            label : '����ʵ�����',
            name : 'executionEntityId',
            align : 'center',
            width : 80
        }, {
            label : '���̷�����',
            name : 'startUserId',
            align : 'center',
            width : 80
        }, {
            label : 'ҵ�����',
            name : 'businessKey',
            width : 150
        }, {
            label : '��������',
            name : 'processDefinitionName',
            width : 150
        }, {
            label : '��ǰ�',
            name : 'activityNames',
            width : 150
        } ],
        filterToolbar : false,
        cmTemplate : {
            sortable : false
        },
        operations : function(items) {
            var $grid = $(this);
            var $select = $('<li data-position="multi" data-toolbar="show"><a href="javascript:;"><i class="fa fa-trash-o"></i> ǿ�ƽ�������ʵ��</a></li>');
            $select.children("a").bind("click", function(e) {
                var ids = $grid.getAtLeastOneSelectedItem();
                if (ids) {
                    $grid.ajaxPostURL({
                        url : WEB_ROOT + "/bpm/process-instance!forceTerminal",
                        success : function(response) {
                            $.each(ids, function(i, item) {
                                var item = $.trim(item);
                                var $tr = $grid.find("tr.jqgrow[id='" + item + "']");
                                if (response.userdata && response.userdata[item]) {
                                    var msg = response.userdata[item];
                                    $tr.pulsate({
                                        color : "#bf1c56",
                                        repeat : 3
                                    });
                                } else {
                                    $grid.jqGrid("delRowData", item);
                                }
                            });
                        },
                        confirmMsg : "ȷ��ǿ�ƽ�������ʵ����",
                        data : {
                            ids : ids.join(",")
                        }
                    })
                }
            });
            items.push($select);
        },
        delurl : WEB_ROOT + '/bpm/process-instance!doDelete'
    });
});