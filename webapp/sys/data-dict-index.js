$(function() {
    $(".grid-sys-data-dict-index").data("gridOptions", {
        url : WEB_ROOT + "/sys/data-dict!findByPage",
        colModel : [ {
            label : '��Ҫ����',
            name : 'primaryValue',
            editable : true,
            editoptions : {
                spellto : 'primaryKey'
            },
            width : 150
        }, {
            label : '����ʶ',
            name : 'primaryKey',
            editable : true,
            width : 100
        }, {
            label : '�α�ʶ',
            name : 'secondaryKey',
            hidden : true,
            editable : true,
            width : 50
        }, {
            label : '��Ҫ����',
            name : 'secondaryValue',
            hidden : true,
            editable : true,
            width : 50
        }, {
            label : '����',
            name : 'disabled',
            editable : true,
            edittype : "checkbox"
        }, {
            label : '�����',
            name : 'orderRank',
            width : 60,
            editable : true,
            editoptions : {
                defaultValue : 100
            },
            sorttype : 'number'
        }, {
            label : '���ı�����',
            name : 'richTextValue',
            width : 200,
            hidden : true,
            editable : true,
            edittype : 'textarea'
        } ],
        sortorder : "desc",
        sortname : 'orderRank',
        multiselect : false,
        subGrid : true,
        gridDnD : true,
        subGridRowExpanded : function(subgrid_id, row_id) {
            Grid.initRecursiveSubGrid(subgrid_id, row_id, "parent.id");
        },
        editurl : WEB_ROOT + "/sys/data-dict!doSave",
        delurl : WEB_ROOT + "/sys/data-dict!doDelete",
        editcol : 'primaryKey',
        inlineNav : {
            add : true
        }
    });
});