$(function() {
    $(".grid-sys-config-property-index").data("gridOptions", {
        url : WEB_ROOT + '/sys/config-property!findByPage',
        colModel : [{
            label : '����',
            name : 'propKey',
            width : 120,
            editable: true,
            align : 'left'
        }, {
            label : '����',
            name : 'propName',
            width : 100,
            editable: true,
            align : 'left'
        }, {
            label : '������ֵ',
            name : 'simpleValue',
            width : 80,
            editable: true,
            align : 'left'
        }, {
            label : 'HTML����ֵ',
            name : 'htmlValue',
            width : 200,
            editable: true,
            align : 'left'
        }, {
            label : '���������÷�˵��',
            name : 'propDescn',
            width : 100,
            editable: true,
            align : 'left'
        } ],
        editurl : WEB_ROOT + '/sys/config-property!doSave',
        delurl : WEB_ROOT + '/sys/config-property!doDelete'
    });
});
