$(function() {
    $(".grid-schedule-job-bean-cfg-index").data("gridOptions", {
        url : WEB_ROOT + '/schedule/job-bean-cfg!findByPage',
        colModel : [ {
            label : '������ȫ��',
            name : 'jobClass',
            width : 200,
            editable : true,
            editoptions : {
                title : '����Ӽƻ����񲻻����̰�����ҵ,��Ҫ����Ӧ�÷�����������Ч'
            }
        }, {
            label : 'CRON���ʽ',
            name : 'cronExpression',
            width : 100,
            editable : true,
            align : 'right'
        }, {
            label : '�Զ���ʼ����',
            name : 'autoStartup',
            formatter : 'checkbox',
            editable : true,
            editoptions : {
                title : '�Ƿ���Ӧ������֮���Զ������������񣬽��ú���Ҫ�ֹ�������������'
            }
        }, {
            label : '������ʷ��¼',
            name : 'logRunHist',
            formatter : 'checkbox',
            editable : true,
            editoptions : {
                title : '�ؼ�������������ʷ��¼���Էǹؼ�����������Ƶ�ʽϸ߿����������ؽ���ر�'
            }
        }, {
            label : '��Ⱥ����ģʽ',
            name : 'runWithinCluster',
            formatter : 'checkbox',
            editable : true,
            editoptions : {
                title : '�������ģʽ���Ƴ���ǰ�ƻ�����,����Ҫ����Ӧ�÷�����������Ч'
            }
        }, {
            label : '��������',
            name : 'description',
            editable : true,
            edittype : 'textarea',
            width : 100,
            align : 'left'
        } ],
        editcol : 'code',
        editurl : WEB_ROOT + "/schedule/job-bean-cfg!doSave",
        delurl : WEB_ROOT + "/schedule/job-bean-cfg!doDelete"
    });
});