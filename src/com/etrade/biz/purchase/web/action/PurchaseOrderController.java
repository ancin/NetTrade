package com.etrade.biz.purchase.web.action;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.etrade.biz.core.constant.VoucherTypeEnum;
import com.etrade.biz.core.service.VoucherNumGenerateService;
import com.etrade.biz.finance.service.BizTradeUnitService;
import com.etrade.biz.md.entity.Commodity;
import com.etrade.biz.md.service.CommodityService;
import com.etrade.biz.purchase.entity.PurchaseOrder;
import com.etrade.biz.purchase.entity.PurchaseOrderDetail;
import com.etrade.biz.purchase.service.PurchaseOrderService;
import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Department;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.security.AuthUserHolder;
import com.etrade.framework.core.auth.service.DepartmentService;
import com.etrade.framework.core.auth.service.UserService;
import com.etrade.framework.core.pagination.GroupPropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter;
import com.etrade.framework.core.pagination.PropertyFilter.MatchType;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.view.OperationResult;
import com.google.common.collect.Maps;

@MetaData("�ɹ�����")
public class PurchaseOrderController extends BaseController<PurchaseOrder, Long> {

    private final Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private BizTradeUnitService bizTradeUnitService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;

    @Autowired
    private VoucherNumGenerateService voucherNumGenerateService;

    @Override
    protected BaseService<PurchaseOrder, Long> getEntityService() {
        return purchaseOrderService;
    }

    @Override
    protected void checkEntityAclPermission(PurchaseOrder entity) {
        // TODO Add acl check code logic
    }

    @Override
    protected void setupDetachedBindingEntity(Long id) {
        bindingEntity = getEntityService().findDetachedOne(id, "purchaseOrderDetails");
    }

    @Override
    @MetaData("��ѯ")
    public HttpHeaders findByPage() {
        return super.findByPage();
    }

    public Map<String, Object> getTaskVariables() {
        Map<String, Object> variables = taskService.getVariables(this.getRequiredParameter("taskId"));
        if (logger.isDebugEnabled()) {
            for (Map.Entry<String, Object> me : variables.entrySet()) {
                logger.debug("{} - {}", me.getKey(), me.getValue());
            }
        }
        return variables;
    }

    public Map<String, String> getDepartmentsMap() {
        Map<String, String> departmentsMap = new LinkedHashMap<String, String>();
        GroupPropertyFilter groupPropertyFilter = GroupPropertyFilter.buildDefaultAndGroupFilter();
        Iterable<Department> departments = departmentService.findByFilters(groupPropertyFilter);
        Iterator<Department> it = departments.iterator();
        while (it.hasNext()) {
            Department department = it.next();
            departmentsMap.put(department.getId(), department.getDisplay());
        }
        return departmentsMap;
    }

    public Map<Long, String> getUsersMap() {
        Map<Long, String> usersMap = new LinkedHashMap<Long, String>();
        GroupPropertyFilter groupPropertyFilter = GroupPropertyFilter.buildDefaultAndGroupFilter();
        groupPropertyFilter.append(new PropertyFilter(MatchType.EQ, "enabled", Boolean.TRUE));
        Iterable<User> users = userService.findByFilters(groupPropertyFilter);
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            User user = it.next();
            usersMap.put(user.getId(), user.getDisplay());
        }
        return usersMap;
    }

    @Override
    @MetaData("����")
    public HttpHeaders doSave() {
        if (bindingEntity.isNew()) {
            List<PurchaseOrderDetail> purchaseOrderDetails = bindingEntity.getPurchaseOrderDetails();
            for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
                purchaseOrderDetail.setPurchaseOrder(bindingEntity);
            }
        }
        return super.doSave();
    }

    public void prepareBpmNew() {
        String clone = this.getParameter("clone");
        if (BooleanUtils.toBoolean(clone)) {
            bindingEntity.resetCommonProperties();
        } else {
            bindingEntity = new PurchaseOrder();
        }
        bindingEntity.setVoucher(voucherNumGenerateService.getVoucherNumByType(VoucherTypeEnum.JHD));
        bindingEntity.setVoucherDate(new Date());
        bindingEntity.setVoucherUser(AuthUserHolder.getLogonUser());
        bindingEntity.setVoucherDepartment(AuthUserHolder.getLogonUserDepartment());

    }

    @MetaData("�ɹ�����������ʼ��")
    public HttpHeaders bpmNew() {
        return buildDefaultHttpHeaders("bpmInput");
    }

    @MetaData("�ɹ���������")
    public HttpHeaders bpmSave() {

        Map<String, Object> variables = Maps.newHashMap();
        String submitToAudit = this.getParameter("submitToAudit");
        if (BooleanUtils.toBoolean(submitToAudit)) {
            bindingEntity.setLastOperationSummary(bindingEntity.buildLastOperationSummary("�ύ"));
            bindingEntity.setSubmitDate(new Date());
        }

        List<PurchaseOrderDetail> purchaseOrderDetails = bindingEntity.getPurchaseOrderDetails();
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
            purchaseOrderDetail.setPurchaseOrder(bindingEntity);
        }
        if (StringUtils.isBlank(bindingEntity.getTitle())) {
            Commodity commodity = commodityService.findOne(purchaseOrderDetails.get(0).getCommodity().getId());
            String commodityTitle = commodity.getTitle();
            commodityTitle = StringUtils.substring(commodityTitle, 0, 30);
            bindingEntity.setTitle(commodityTitle + "...��" + purchaseOrderDetails.size() + "����Ʒ");
        }

        if (bindingEntity.isNew()) {
            purchaseOrderService.bpmCreate(bindingEntity, variables);
            setModel(OperationResult.buildSuccessResult("�ɹ�����������ɣ���ͬ��������������", bindingEntity));
        } else {
            purchaseOrderService.bpmUpdate(bindingEntity, this.getRequiredParameter("taskId"), variables);
            setModel(OperationResult.buildSuccessResult("�ɹ����������ύ���", bindingEntity));
        }
        return buildDefaultHttpHeaders();
    }

    @MetaData("�ɹ���������")
    public HttpHeaders bpmPrice() {
        Map<String, Object> variables = Maps.newHashMap();
        List<PurchaseOrderDetail> purchaseOrderDetails = bindingEntity.getPurchaseOrderDetails();
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
            purchaseOrderDetail.setPurchaseOrder(bindingEntity);
        }
        purchaseOrderService.bpmUpdate(bindingEntity, this.getRequiredParameter("taskId"), variables);
        setModel(OperationResult.buildSuccessResult("�ɹ������������", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    @MetaData("һ�����")
    public HttpHeaders bpmLevel1Audit() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("auditLevel1Time", new Date());
        variables.put("auditLevel1User", AuthContextHolder.getAuthUserPin());
        Boolean auditLevel1Pass = new Boolean(getRequiredParameter("auditLevel1Pass"));
        variables.put("auditLevel1Pass", auditLevel1Pass);
        variables.put("auditLevel1Explain", getParameter("auditLevel1Explain"));
        bindingEntity.setLastOperationSummary(bindingEntity.buildLastOperationSummary("���"));
        if (!auditLevel1Pass) {
            bindingEntity.setSubmitDate(null);
        }
        purchaseOrderService.bpmUpdate(bindingEntity, this.getRequiredParameter("taskId"), variables);
        setModel(OperationResult.buildSuccessResult("�ɹ�����һ��������", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    @MetaData("�������")
    public HttpHeaders bpmLevel2Audit() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("auditLevel2Time", new Date());
        variables.put("auditLevel2User", AuthContextHolder.getAuthUserPin());
        Boolean auditLevel2Pass = new Boolean(getRequiredParameter("auditLevel2Pass"));
        variables.put("auditLevel2Pass", auditLevel2Pass);
        variables.put("auditLevel2Explain", getParameter("auditLevel2Explain"));
        if (!auditLevel2Pass) {
            bindingEntity.setSubmitDate(null);
        }
        purchaseOrderService.bpmUpdate(bindingEntity, this.getRequiredParameter("taskId"), variables);
        setModel(OperationResult.buildSuccessResult("�ɹ���������������", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    @MetaData("�ɹ��������������ʾ")
    public HttpHeaders bpmPayInput() {
        return buildDefaultHttpHeaders("bpmPay");
    }

    @MetaData("(Ԥ)��������")
    public HttpHeaders bpmPay() {
        purchaseOrderService.bpmPay(bindingEntity, this.getRequiredParameter("taskId"));
        setModel(OperationResult.buildSuccessResult("�ɹ�(Ԥ)�������������", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    @MetaData("�������ҷ�����Ϣ")
    public HttpHeaders bpmDelivery() {
        bindingEntity.setDeliveryTime(new Date());
        purchaseOrderService.bpmDelivery(bindingEntity, this.getRequiredParameter("taskId"));
        setModel(OperationResult.buildSuccessResult("¼�����ҷ�����Ϣ���", bindingEntity));
        return buildDefaultHttpHeaders();
    }

    @MetaData("��������")
    public HttpHeaders purchaseOrderDetails() {
        List<PurchaseOrderDetail> purchaseOrderDetails = bindingEntity.getPurchaseOrderDetails();
        if (BooleanUtils.toBoolean(getParameter("clone"))) {
            for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
                purchaseOrderDetail.resetCommonProperties();
            }
        }
        setModel(buildPageResultFromList(purchaseOrderDetails));
        return buildDefaultHttpHeaders();
    }

    @MetaData("���ݺ��")
    public HttpHeaders doRedword() {
        Assert.isTrue(bindingEntity.getRedwordDate() == null);
        purchaseOrderService.redword(bindingEntity);
        setModel(OperationResult.buildSuccessResult("������"));
        return buildDefaultHttpHeaders();
    }

    @Override
    public HttpHeaders revisionList() {
        return super.revisionList();
    }

    @Override
    public HttpHeaders revisionCompare() {
        return super.revisionCompare();
    }
}