package com.etrade.biz.core.web;

import com.etrade.biz.core.entity.BaseBizEntity;
import com.etrade.framework.core.web.BaseController;

public abstract class BaseBizController<T extends BaseBizEntity> extends BaseController<T, Long> {

}
