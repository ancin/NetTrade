package com.etrade.framework.core.auth.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.entity.BaseUuidEntity;


@Entity
@Table(name = "tbl_AUTH_USER_R2_ROLE", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_ID", "ROLE_ID" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "��ɫ��Ȩ�޹���")
public class UserR2Role extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    /** �����û����� */
    private User user;

    /** ������ɫ���� */
    private Role role;
    
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    @Transient
    @Override
    public String getDisplay() {
        return user.getDisplay() + "_" + role.getDisplay();
    }
}
