package com.etrade.framework.core.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.dao.AttachmentFileDao;
import com.etrade.framework.core.sys.entity.AttachmentFile;
import com.google.common.collect.Lists;

@Service
@Transactional
public class AttachmentFileService extends BaseService<AttachmentFile, String> {

    @Autowired
    private AttachmentFileDao attachmentFileDao;

    @Override
    protected BaseDao<AttachmentFile, String> getEntityDao() {
        return attachmentFileDao;
    }

    public List<AttachmentFile> findBy(String entityClassName, String entityId, String entityFileCategory) {
        return attachmentFileDao.findByEntityClassNameAndEntityIdAndEntityFileCategory(entityClassName, entityId,
                entityFileCategory);
    }

    public void attachmentBind(String[] attachmentIds, Persistable<?> bindingEntity, String entityFileCategory) {
        List<AttachmentFile> tobeRemoves = Lists.newArrayList();
        List<AttachmentFile> tobeAdds = Lists.newArrayList();
        List<AttachmentFile> r2List = attachmentFileDao.findByEntityClassNameAndEntityId(bindingEntity.getClass()
                .getName(), String.valueOf(bindingEntity.getId()));
        if ((r2List == null || r2List.size() == 0)
                && (attachmentIds == null || attachmentIds.length == 0 || attachmentIds[0].trim().length() == 0)) {
            return;
        }
        if (r2List == null || r2List.size() == 0) {
            for (String attachmentId : attachmentIds) {
                Assert.isTrue(attachmentId.trim().length() >= 32, "Invalid attachment parameter length: "
                        + attachmentId);
                tobeAdds.add(attachmentFileDao.findOne(attachmentId.trim()));
            }
        } else {
            if (attachmentIds == null || attachmentIds.length == 0 || attachmentIds[0].trim().length() == 0) {
                tobeRemoves.addAll(r2List);
            } else {
                for (AttachmentFile r2 : r2List) {
                    boolean tobeRemove = true;
                    for (String attachmentId : attachmentIds) {
                        if (r2.getId().equals(attachmentId.trim())) {
                            tobeRemove = false;
                            break;
                        }
                    }
                    if (tobeRemove) {
                        tobeRemoves.add(r2);
                    }
                }
                for (String attachmentId : attachmentIds) {
                    boolean tobeAdd = true;
                    for (AttachmentFile r2 : r2List) {
                        if (r2.getId().equals(attachmentId)) {
                            tobeAdd = false;
                            break;
                        }
                    }
                    if (tobeAdd) {
                        tobeAdds.add(attachmentFileDao.findOne(attachmentId.trim()));
                    }
                }
            }
        }
        for (AttachmentFile attachmentFile : tobeAdds) {
            if (attachmentFile.getEntityId() == null) {
                attachmentFile.setEntityClassName(bindingEntity.getClass().getName());
                attachmentFile.setEntityId(String.valueOf(bindingEntity.getId()));
                attachmentFile.setEntityFileCategory(entityFileCategory);
                attachmentFileDao.save(attachmentFile);
            }
        }
        //���������ÿգ��ɶ�ʱ��������
        for (AttachmentFile attachmentFile : tobeRemoves) {
            attachmentFile.setEntityClassName(null);
            attachmentFile.setEntityId(null);
            attachmentFile.setEntityFileCategory(null);
            attachmentFileDao.save(attachmentFile);
        }
    }

    /**
     * ��ʱ�û��ϴ���һЩ�������ǿ���û�б�����ҵ����󣬵��²���һЩ���¶���������¼���ļ�
     * ���ö�ʱ����������һ��ʱ��û�б�����ʹ�õĸ������ݼ�¼�ʹ����ļ�
     */
    public void timelyClearUnusedFiles() {

    }
}
