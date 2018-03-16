$(function() {
    $(".grid-sys-pub-post-index").data("gridOptions", {
        url : WEB_ROOT + "/sys/pub-post!findByPage",
        colNames : [ '�Ķ�����', '����', '�ⲿ����', 'ǰ����ʾ', '�����ʾ', '�����', '��Чʱ��', '����ʱ��' ],
        colModel : [ {
            name : 'readUserCount',
            width : 60,
            fixed : true,
            align : 'center'
        }, {
            name : 'htmlTitle',
            editable : true,
            align : 'left'
        }, {
            name : 'externalLink',
            editable : true,
            align : 'left'
        }, {
            name : 'frontendShow',
            width : 60,
            editable : true,
            edittype : 'checkbox'
        }, {
            name : 'backendShow',
            width : 60,
            editable : true,
            edittype : 'checkbox'
        }, {
            name : 'orderRank',
            width : 60,
            sorttype : 'number',
            editable : true
        }, {
            name : 'publishTime',
            formatter : 'timestamp',
            editable : true,
            editoptions : {
                time : true
            },
            align : 'center'
        }, {
            name : 'expireTime',
            formatter : 'timestamp',
            editable : true,
            editoptions : {
                time : true
            },
            align : 'center'
        } ],
        editcol : 'htmlTitle',
        editurl : WEB_ROOT + "/sys/pub-post!doSave",
        delurl : WEB_ROOT + "/sys/pub-post!doDelete",
        fullediturl : WEB_ROOT + "/sys/pub-post!edit",
        subGrid : true,
        subGridRowExpanded : function(subgrid_id, row_id) {
            Grid.initSubGrid(subgrid_id, row_id, {
                url : WEB_ROOT + "/sys/pub-post-read!findByPage?search['EQ_pubPost.id']=" + row_id,
                colNames : [ '�Ķ��û�', '�״��Ķ�ʱ��', '����Ķ�ʱ��', '�ܼ��Ķ�����' ],
                colModel : [ {
                    name : 'readUserLabel',
                    width : 150
                }, {
                    name : 'firstReadTime',
                    formatter : 'timestamp'
                }, {
                    name : 'lastReadTime',
                    formatter : 'timestamp'
                }, {
                    name : 'readTotalCount',
                    formatter : 'number'
                } ]
            });
        }
    });
});