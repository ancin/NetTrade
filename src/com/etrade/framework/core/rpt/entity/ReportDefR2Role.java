package com.etrade.framework.core.rpt.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.etrade.framework.core.annotation.MetaData;
import com.etrade.framework.core.auth.entity.Role;
import com.etrade.framework.core.entity.BaseUuidEntity;

@Entity
@Table(name = "tbl_RPT_REPORT_DEF_R2_ROLE", uniqueConstraints = @UniqueConstraint(columnNames = { "REPORT_DEF_ID", "ROLE_ID" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "�������ɫ����")
public class ReportDefR2Role extends BaseUuidEntity {

    /**  */
    private static final long serialVersionUID = 1L;

    @MetaData(value = "��������")
    private ReportDef reportDef;

    @MetaData(value = "������ɫ����")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "REPORT_DEF_ID", nullable = false)
    public ReportDef getReportDef() {
        return reportDef;
    }

    public void setReportDef(ReportDef reportDef) {
        this.reportDef = reportDef;
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
        return role.getDisplay() + "_" + reportDef.getDisplay();
    }
}
