package com.etrade.framework.core.profile.web.action;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.User;
import com.etrade.framework.core.auth.security.AuthUserHolder;
import com.etrade.framework.core.service.BaseService;
import com.etrade.framework.core.sys.entity.PubPost;
import com.etrade.framework.core.sys.entity.PubPostRead;
import com.etrade.framework.core.sys.service.PubPostReadService;
import com.etrade.framework.core.sys.service.PubPostService;
import com.etrade.framework.core.web.BaseController;
import com.etrade.framework.core.web.annotation.SecurityControlIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@MetaData(value = "公告消息显示")
public class PubPostController extends BaseController<PubPost, String> {

    private final static String READED_PUB_POST_IDS = "READED_PUB_POST_IDS";

    @Autowired
    private PubPostService pubPostService;

    @Autowired
    private PubPostReadService pubPostReadService;

    @Override
    protected BaseService<PubPost, String> getEntityService() {
        return pubPostService;
    }

    @Override
    protected void checkEntityAclPermission(PubPost entity) {
        // Nothing to do
    }

    @MetaData(value = "公告列表")
    public HttpHeaders list() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<Serializable, Boolean> idMaps = (Map<Serializable, Boolean>) session.getAttribute(READED_PUB_POST_IDS);
        if (idMaps == null) {
            idMaps = Maps.newHashMap();
            session.setAttribute(READED_PUB_POST_IDS, idMaps);
        }
        
        List<PubPost> pubPosts = pubPostService.findPublished();
        if (CollectionUtils.isNotEmpty(pubPosts)) {
            User user = AuthUserHolder.getLogonUser();
            List<PubPostRead> pubPostReads = pubPostReadService.findReaded(user, pubPosts);
            for (PubPost pubPost : pubPosts) {
                pubPost.addExtraAttribute("readed", false);
                for (PubPostRead pubPostRead : pubPostReads) {
                    if (pubPostRead.getPubPost().equals(pubPost)) {
                        pubPost.addExtraAttribute("readed", true);
                        break;
                    }
                }
            }
            setModel(pubPosts);
        }
        return buildDefaultHttpHeaders("list");
    }

    @MetaData("用户公告消息列表")
    @SecurityControlIgnore
    public HttpHeaders messages() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();

        //进行Session数据缓存优化处理，避免每次查询数据库
        @SuppressWarnings("unchecked")
        Map<Serializable, Boolean> idMaps = (Map<Serializable, Boolean>) session.getAttribute(READED_PUB_POST_IDS);
        if (idMaps == null) {
            idMaps = Maps.newHashMap();
            session.setAttribute(READED_PUB_POST_IDS, idMaps);
        }
        List<PubPost> pubPosts = pubPostService.findPublished();
        List<PubPost> notifyList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(pubPosts)) {

            boolean needRetriveReads = false;
            for (PubPost pubPost : pubPosts) {
                if (!idMaps.containsKey(pubPost.getId())) {
                    needRetriveReads = true;
                    break;
                }
            }

            if (needRetriveReads) {
                User user = AuthUserHolder.getLogonUser();
                List<PubPostRead> pubPostReads = pubPostReadService.findReaded(user, pubPosts);
                for (PubPost pubPost : pubPosts) {
                    idMaps.put(pubPost.getId(), Boolean.FALSE);
                    for (PubPostRead pubPostRead : pubPostReads) {
                        if (pubPostRead.getPubPost().equals(pubPost)) {
                            idMaps.put(pubPost.getId(), Boolean.TRUE);
                            break;
                        }
                    }
                }
            }

            for (PubPost pubPost : pubPosts) {
                if (Boolean.FALSE.equals(idMaps.get(pubPost.getId()))) {
                    notifyList.add(pubPost);
                }
            }
        }
        setModel(notifyList);
        return buildDefaultHttpHeaders("list");
    }

    @Override
    @MetaData(value = "查看")
    public HttpHeaders view() {
        User user = AuthUserHolder.getLogonUser();
        PubPostRead pubPostRead = pubPostReadService.findReaded(user, bindingEntity);
        if (pubPostRead == null) {
            pubPostRead = new PubPostRead();
            pubPostRead.setFirstReadTime(new Date());
            pubPostRead.setReadTotalCount(1);
            pubPostRead.setReadUser(user);
            pubPostRead.setPubPost(bindingEntity);

            if (bindingEntity.getReadUserCount() == null) {
                bindingEntity.setReadUserCount(1);
            } else {
                bindingEntity.setReadUserCount(bindingEntity.getReadUserCount() + 1);
            }
        } else {
            pubPostRead.setLastReadTime(new Date());
            pubPostRead.setReadTotalCount(pubPostRead.getReadTotalCount() + 1);
        }
        pubPostReadService.save(pubPostRead);
        pubPostService.save(bindingEntity);
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        Map<Serializable, Boolean> idMaps = (Map<Serializable, Boolean>) session.getAttribute(READED_PUB_POST_IDS);
        idMaps.put(bindingEntity.getId(), Boolean.TRUE);
        return buildDefaultHttpHeaders("view");
    }
}
