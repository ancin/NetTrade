package com.etrade.framework.core.sys.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.etrade.framework.core.dao.BaseDao;
import com.etrade.framework.core.sys.entity.AttachmentFile;

@Repository
public interface AttachmentFileDao extends BaseDao<AttachmentFile, String> {
    List<AttachmentFile> findByEntityClassNameAndEntityIdAndEntityFileCategory(String entityClassName, String entityId,
            String entityFileCategory);

    List<AttachmentFile> findByEntityClassNameAndEntityId(String entityClassName, String entityId);
   
}
