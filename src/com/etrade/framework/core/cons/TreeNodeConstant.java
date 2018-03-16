package com.etrade.framework.core.cons;

import com.etrade.framework.core.annotation.MetaData;

public class TreeNodeConstant {

    public static enum TreeNodeDragType {

        @MetaData("成为子节点")
        inner,

        @MetaData("成为同级前一个节点")
        prev,

        @MetaData("成为同级后一个节点")
        next;

    }
}
