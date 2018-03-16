$(function() {
    $(".grid-profile-profile-param-def-index").data("gridOptions", {
        url : WEB_ROOT + '/profile/profile-param-def!findByPage',
        colModel : [ {
            label : '����',
            name : 'code',
            width : 200,
            editable : true,
            align : 'left'
        }, {
            label : '����',
            name : 'title',
            width : 200,
            editable : true,
            align : 'left'
        }, {
            label : '���ñ�ʶ',
            name : 'disabled',
            formatter : 'checkbox',
            editable : true
        }, {
            label : '��������',
            name : 'type',
            formatter : 'select',
            searchoptions : {
                value : Util.getCacheEnumsByType('dynamicParameterTypeEnum')
            },
            width : 120,
            editable : true,
            align : 'center'
        }, {
            label : 'ȱʡ����ֵ',
            name : 'defaultValue',
            width : 150,
            editable : true,
            align : 'left'
        }, {
            label : '�����',
            name : 'orderRank',
            width : 60,
            editable : true,
            align : 'right'
        }, {
            label : 'ǰ��UIУ�����',
            name : 'validateRules',
            width : 200,
            editable : true,
            align : 'left'
        }, {
            label : '��������Դ ',
            name : 'listDataSource',
            width : 200,
            editable : true,
            align : 'left'
        }, {
            label : '�Ƿ������ѡ ',
            name : 'multiSelectFlag',
            formatter : 'checkbox',
            editable : true
        } ],
        editurl : WEB_ROOT + '/profile/profile-param-def!doSave',
        delurl : WEB_ROOT + '/profile/profile-param-def!doDelete'
    });
});
