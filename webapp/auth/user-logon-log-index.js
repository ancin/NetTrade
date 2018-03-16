$(function() {
    $(".grid-auth-user-logon-log-index").data("gridOptions", {
        url : WEB_ROOT + "/auth/user-logon-log!findByPage",
        colModel : [ {
            label : 'ʧ������',
            name : 'authenticationFailure',
            width : 50,
            edittype : "checkbox"
        }, {
            label : '��¼�˺�',
            name : 'username',
            width : 100,
            align : 'center'
        }, {
            label : '�˻����',
            name : 'userid',
            width : 100,
            hidden : true,
            align : 'left'
        }, {
            label : '��¼ʱ��',
            name : 'logonTime',
            sorttype : 'date'
        }, {
            label : '�ǳ�ʱ��',
            name : 'logoutTime',
            sorttype : 'date'
        }, {
            label : '��¼ʱ��',
            name : 'logonTimeLengthFriendly',
            index : 'logonTimeLength',
            width : 100,
            fixed : true,
            align : 'center'
        }, {
            label : '��¼����',
            name : 'logonTimes',
            width : 60,
            sorttype : 'number',
            align : 'center'
        }, {
            name : 'userAgent',
            hidden : true,
            align : 'left'
        }, {
            name : 'xforwardFor',
            width : 100,
            align : 'left'
        }, {
            name : 'localAddr',
            hidden : true,
            align : 'left'
        }, {
            name : 'localName',
            hidden : true,
            align : 'left'
        }, {
            name : 'localPort',
            width : 60,
            fixed : true,
            hidden : true,
            align : 'right'
        }, {
            name : 'remoteAddr',
            hidden : true,
            align : 'left'
        }, {
            name : 'remoteHost',
            hidden : true,
            align : 'left'
        }, {
            name : 'remotePort',
            width : 60,
            fixed : true,
            hidden : true,
            align : 'right'
        }, {
            name : 'serverIP',
            hidden : true,
            align : 'left'
        }, {
            name : 'httpSessionId',
            hidden : true,
            align : 'left'
        } ],
        multiselect : false,
        sortorder : "desc",
        sortname : "logonTime"
    });
});