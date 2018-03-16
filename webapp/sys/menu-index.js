$(function() {
    $(".grid-sys-menu-index").data("gridOptions", {
        url : WEB_ROOT + "/sys/menu!findByPage",
        colModel : [ {
            label : '����',
            name : 'title',
            width : 150,
            editable : true,
            editoptions : {
                spellto : 'code'
            },
            align : 'left'
        }, {
            label : '����',
            name : 'code',
            align : 'center',
            editable : true,
            width : 100
        }, {
            label : 'ͼ��',
            name : 'style',
            editable : true,
            width : 80,
            align : 'center',
            formatter : function(cellValue, options, rowdata, action) {
                if (cellValue) {
                    return '<i class="fa ' + cellValue + '" icon="' + cellValue + '"></i>';
                } else {
                    return ''
                }
            },
            unformat : function(cellValue, options, cell) {
                return $('i', cell).attr('icon');
            }
        }, {
            label : '�˵�URL',
            name : 'url',
            editable : true,
            width : 200,
            align : 'left'
        }, {
            label : '����',
            name : 'type',
            width : 80,
            editable : true,
            align : 'center',
            stype : 'select',
            editoptions : {
                defaultValue : 'RELC'
            },
            searchoptions : {
                value : Util.getCacheEnumsByType('menuTypeEnum')
            }
        }, {
            label : 'չ��',
            name : 'initOpen',
            editable : true,
            edittype : "checkbox"
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
                defaultValue : 1000
            },
            sorttype : 'number'
        }, {
            label : '��ע˵��',
            name : 'description',
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
        editurl : WEB_ROOT + "/sys/menu!doSave",
        delurl : WEB_ROOT + "/sys/menu!doDelete",
        editcol : 'title',
        inlineNav : {
            add : true
        }
    });
});