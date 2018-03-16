$(function() {
    $(".grid-auth-user-index").data("gridOptions", {
        url : WEB_ROOT + "/auth/user!findByPage",
        colModel : [ {
            label : '��¼�˺�',
            name : 'signinid',
            editable : true,
            editoptions : {
                updatable : false
            },
            width : 120
        }, {
            label : '��������',
            name : 'aclCode',
            editable : true,
            width : 120
        }, {
            label : '�ǳ�',
            name : 'nick',
            editable : true,
            width : 120
        }, {
            label : '�����ʼ�',
            name : 'email',
            editable : true,
            width : 200
        }, {
            label : '�ƶ��绰',
            name : 'mobilePhone',
            editable : true,
            width : 100
        }, {
            label : '����',
            name : 'enabled',
            editable : true,
            edittype : "checkbox"
        }, {
            label : '�˺�ʧЧ����',
            name : 'accountExpireTime',
            editable : true,
            sorttype : 'date'
        }, {
            label : '��������',
            name : 'department.id',
            editable : true,
            stype : 'select',
            editoptions : {
                value : Util.getCacheSelectOptionDatas(WEB_ROOT + "/auth/department!findByPage?rows=-1")
            },
            width : 150
        }, {
            label : 'ע��ʱ��',
            name : 'signupTime',
            sorttype : 'date'
        } ],
        editcol : 'signinid',
        inlineNav : {
            add : false
        },
        editurl : WEB_ROOT + "/auth/user!doSave",
        delurl : WEB_ROOT + "/auth/user!doDelete",
        fullediturl : WEB_ROOT + "/auth/user!inputTabs"
    });
});