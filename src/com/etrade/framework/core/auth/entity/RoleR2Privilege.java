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
@Table(name = "tbl_AUTH_ROLE_R2_PRIVILEGE", uniqueConstraints = @UniqueConstraint(columnNames = { "PRIVILEGE_ID",
        "ROLE_ID" }))
@MetaData(value = "��ɫ��Ȩ�޹���")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleR2Privilege extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    /** ����Ȩ�޶��� */
    private Privilege privilege;

    /** ������ɫ���� */
    private Role role;
    
    @ManyToOne
    @JoinColumn(name = "PRIVILEGE_ID", nullable = false)
    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
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
        return privilege.getDisplay() + "_" + role.getDisplay();
    }
}
