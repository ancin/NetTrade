$(function() {
    $(".grid-auth-signup-user-index").data("gridOptions", {
        url : WEB_ROOT + "/auth/signup-user!findByPage",
        colModel : [ {
            label : '��¼�˺�',
            name : 'signinid',
            width : 120
        }, {
            label : '��������',
            name : 'aclCode',
            width : 120
        }, {
            label : '�ǳ�',
            name : 'nick',
            width : 120
        }, {
            label : '�����ʼ�',
            name : 'email',
            width : 200
        }, {
            label : 'ע��ʱ��',
            name : 'signupTime',
            sorttype : 'date'
        }, {
            label : '���ʱ��',
            name : 'auditTime',
            sorttype : 'date'
        } ],
        editcol : 'signinid',
        delurl : WEB_ROOT + "/auth/signup-user!doDelete",
        fullediturl : WEB_ROOT + "/auth/signup-user!edit"
    });
});