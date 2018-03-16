$(function() {
    $(".grid-schedule-job-bean-cfg-triggers").data("gridOptions", {
        url : WEB_ROOT + '/schedule/job-bean-cfg!triggers',
        colNames : [ '��������', 'CRON���ʽ', '��ǰ״̬', '�ϴδ���ʱ��', '�´δ���ʱ��', '��Ⱥ����ģʽ' ],
        colModel : [ {
            name : 'jobName',
            width : 240,
            align : 'left'
        }, {
            name : 'cronExpression',
            width : 100,
            align : 'right'
        }, {
            name : 'stateLabel',
            width : 60,
            align : 'center'
        }, {
            name : 'previousFireTime',
            sorttype : 'date',
            align : 'center'
        }, {
            name : 'nextFireTime',
            sorttype : 'date',
            align : 'center'
        }, {
            name : 'runWithinCluster',
            formatter : 'checkbox'
        } ],
        rowNum : -1,
        loadonce : true,
        addable : false,
        loadonce : true,
        operations : function(items) {
            var $grid = $(this);

            var $resume = $('<li data-position="multi" data-toolbar="show" data-text="show"><a><i class="fa fa-play"></i> ����</a></li>');
            $resume.children("a").bind("click", function(e) {
                e.preventDefault();
                var ids = $grid.getAtLeastOneSelectedItem();
                if (ids) {
                    $grid.ajaxPostURL({
                        url : WEB_ROOT + '/schedule/job-bean-cfg!doStateTrigger',
                        success : function() {
                            $grid.refresh();
                        },
                        confirmMsg : "ȷ�� ���� ��ѡ����",
                        data : {
                            ids : ids.join(","),
                            state : 'resume'
                        }
                    })
                }
            });
            items.push($resume);

            var $pause = $('<li data-position="multi" data-toolbar="show" data-text="show"><a><i class="fa fa-pause"></i> ��ͣ</a></li>');
            $pause.children("a").bind("click", function(e) {
                e.preventDefault();
                var ids = $grid.getAtLeastOneSelectedItem();
                if (ids) {
                    $grid.ajaxPostURL({
                        url : WEB_ROOT + '/schedule/job-bean-cfg!doStateTrigger',
                        success : function() {
                            $grid.refresh();
                        },
                        confirmMsg : "ȷ��  ��ͣ  ��ѡ����",
                        data : {
                            ids : ids.join(","),
                            state : 'pause'
                        }
                    })
                }
            });
            items.push($pause);

            var $run = $('<li data-position="multi" data-toolbar="show" data-text="show"><a><i class="fa fa-bolt"></i> ����ִ��</a></li>');
            $run.children("a").bind("click", function(e) {
                e.preventDefault();
                var ids = $grid.getAtLeastOneSelectedItem();
                if (ids) {
                    $grid.ajaxPostURL({
                        url : WEB_ROOT + '/schedule/job-bean-cfg!doRunTrigger',
                        success : function() {
                            $grid.refresh();
                        },
                        confirmMsg : "ȷ��  ����ִ��  ��ѡ����",
                        data : {
                            ids : ids.join(",")
                        }
                    })
                }
            });
            items.push($run);
        }
    });
});