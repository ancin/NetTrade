package com.etrade.framework.core.cons;

import com.etrade.framework.core.annotation.MetaData;

public class TreeNodeConstant {

    public static enum TreeNodeDragType {

        @MetaData("��Ϊ�ӽڵ�")
        inner,

        @MetaData("��Ϊͬ��ǰһ���ڵ�")
        prev,

        @MetaData("��Ϊͬ����һ���ڵ�")
        next;

    }
}
