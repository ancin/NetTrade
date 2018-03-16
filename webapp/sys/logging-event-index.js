$(function() {
    $(".grid-sys-logging-event-index").data("gridOptions", {
        url : WEB_ROOT + '/sys/logging-event!findByPage',
        colNames : [ '��ˮ��', '��־ʱ��', '������Ϣ', '��־����', '��־����', '����״̬' ],
        colModel : [ {
            name : 'id',
            align : 'center',
            width : 50
        }, {
            name : 'timestampDate',
            index : 'timestmp',
            align : 'center',
            search : false,
            width : 150,
            fixed : true
        }, {
            name : 'formattedMessage',
            sortable : false
        }, {
            name : 'levelString',
            align : 'center',
            width : 50
        }, {
            name : 'loggerName',
            align : 'left',
            width : 250
        }, {
            name : 'state',
            align : 'center',
            width : 80,
            stype : 'select',
            searchoptions : {
                value : Util.getCacheEnumsByType('loggingHandleStateEnum')
            }

        } ],
        sortname : 'timestmp',
        delurl : WEB_ROOT + '/sys/logging-event!doDelete',
        fullediturl : WEB_ROOT + '/sys/logging-event!edit',
        addable : false
    });
});