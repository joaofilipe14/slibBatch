package com.slib.rms.analytics.bd.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

public class DsSuperClientDTO {
    private BigDecimal dsId;
    private BigDecimal id;
    private String accessCode;
    private BigDecimal creditLine;
    private String name;
    private BigDecimal idOrigin;
    private BigDecimal version;
    private String referenceCurrencyRefId;
    private Timestamp auditEntryTs;
    private Timestamp auditEntryTsPrev;
    private Timestamp auditEntryTsLast;
    private Timestamp auditValidationTsLast;
    private String uniqueId;
    private String userIdLastChange;
    private boolean simulation;
    private LocalDate insertDate;
    private Timestamp insertDateTime;

    // Getters and Setters

    public BigDecimal getDsId() {
        return dsId;
    }

    public void setDsId(BigDecimal dsId) {
        this.dsId = dsId;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public BigDecimal getCreditLine() {
        return creditLine;
    }

    public void setCreditLine(BigDecimal creditLine) {
        this.creditLine = creditLine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getIdOrigin() {
        return idOrigin;
    }

    public void setIdOrigin(BigDecimal idOrigin) {
        this.idOrigin = idOrigin;
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public String getReferenceCurrencyRefId() {
        return referenceCurrencyRefId;
    }

    public void setReferenceCurrencyRefId(String referenceCurrencyRefId) {
        this.referenceCurrencyRefId = referenceCurrencyRefId;
    }

    public Timestamp getAuditEntryTs() {
        return auditEntryTs;
    }

    public void setAuditEntryTs(Timestamp auditEntryTs) {
        this.auditEntryTs = auditEntryTs;
    }

    public Timestamp getAuditEntryTsPrev() {
        return auditEntryTsPrev;
    }

    public void setAuditEntryTsPrev(Timestamp auditEntryTsPrev) {
        this.auditEntryTsPrev = auditEntryTsPrev;
    }

    public Timestamp getAuditEntryTsLast() {
        return auditEntryTsLast;
    }

    public void setAuditEntryTsLast(Timestamp auditEntryTsLast) {
        this.auditEntryTsLast = auditEntryTsLast;
    }

    public Timestamp getAuditValidationTsLast() {
        return auditValidationTsLast;
    }

    public void setAuditValidationTsLast(Timestamp auditValidationTsLast) {
        this.auditValidationTsLast = auditValidationTsLast;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUserIdLastChange() {
        return userIdLastChange;
    }

    public void setUserIdLastChange(String userIdLastChange) {
        this.userIdLastChange = userIdLastChange;
    }

    public boolean isSimulation() {
        return simulation;
    }

    public void setSimulation(boolean simulation) {
        this.simulation = simulation;
    }

    public LocalDate getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(LocalDate insertDate) {
        this.insertDate = insertDate;
    }

    public Timestamp getInsertDateTime() {
        return insertDateTime;
    }

    public void setInsertDateTime(Timestamp insertDateTime) {
        this.insertDateTime = insertDateTime;
    }
}
