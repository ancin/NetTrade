package com.etrade.framework.core.rpt.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.etrade.framework.core.auth.dao.RoleDao;
import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.rpt.dao.ReportDefDao;
import com.etrade.framework.core.rpt.dao.ReportDefR2RoleDao;
import com.etrade.framework.core.rpt.entity.ReportDef;
import com.etrade.framework.core.rpt.entity.ReportDefR2Role;
import com.etrade.framework.core.security.AuthContextHolder;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.AttachmentFileDao;
import com.etrade.framework.core.sys.entity.AttachmentFile;

@Service
@Transactional
public class ReportDefService extends BaseService<ReportDef, String> {

    @Autowired
    private ReportDefDao reportDefDao;

    @Autowired
    private ReportDefR2RoleDao reportDefR2RoleDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AttachmentFileDao attachmentFileDao;

    @Override
    protected BaseDao<ReportDef, String> getEntityDao() {
        return reportDefDao;
    }

    @Transactional(readOnly = true)
    public ReportDef findByCode(String code) {
        return reportDefDao.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<String> findCategories() {
        return reportDefDao.findCategories();
    }

    @Transactional(readOnly = true)
    public List<ReportDefR2Role> findRelatedRoleR2s(String id) {
        return reportDefR2RoleDao.findByReportDef_Id(id);
    }

    @CacheEvict(value = "SpringSecurityCache", allEntries = true)
    public void updateRelatedRoleR2s(String id, String[] roleIds) {
        updateRelatedR2s(id, roleIds, "reportDefR2Roles", "role");
    }

    @Transactional(readOnly = true)
    public AttachmentFile findRelatedAttachmentFile(String id) {
        List<AttachmentFile> attachmentFiles = attachmentFileDao.findByEntityClassNameAndEntityId(
                ReportDef.class.getName(), id);
        if (CollectionUtils.isEmpty(attachmentFiles)) {
            return null;
        } else {
            Assert.isTrue(attachmentFiles.size() == 1);
            return attachmentFiles.get(0);
        }
    }

    @Override
    public ReportDef save(ReportDef entity) {
        AttachmentFile attachmentFile = entity.getTemplateFile();
        if (attachmentFile != null) {
            attachmentFile.setLastTouchTime(new Date());
            attachmentFile.setLastTouchBy(AuthContextHolder.getAuthUserPin());
            attachmentFileDao.save(attachmentFile);
        }
        return super.save(entity);
    }
}
