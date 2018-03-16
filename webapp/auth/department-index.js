$(function() {
    $(".grid-auth-department-index").data("gridOptions", {
        url : WEB_ROOT + '/auth/department!findByPage',
        colModel : [ {
            label : '����',
            name : 'title',
            width : 100,
            editable : true,
            editoptions : {
                spellto : 'code'
            },
            align : 'left'
        }, {
            label : '����',
            name : 'code',
            width : 100,
            editable : true,
            align : 'left'
        }, {
            label : '��ϵ�绰',
            name : 'contactTel',
            width : 100,
            editable : true,
            align : 'left'
        } ],
        postData : {
            "search['FETCH_manager']" : "LEFT"
        },
        editcol : 'code',
        editurl : WEB_ROOT + "/auth/department!doSave",
        delurl : WEB_ROOT + "/auth/department!doDelete",
        fullediturl : WEB_ROOT + "/auth/department!inputTabs"
    });
});