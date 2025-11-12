package com.slib.rms.analytics.dbt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DbtVarsDTO {
    private String loadFromDate;

    public DbtVarsDTO(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        loadFromDate = now.format(formatter);
    }

    @Override
    public String toString() {
        return "DbtVarsDTO{" +
                "loadFromDate='" + loadFromDate + '\'' +
                '}';
    }

    public String getLoadFromDate() {
        return loadFromDate;
    }

    public void setLoadFromDate(String loadFromDate) {
        this.loadFromDate = loadFromDate;
    }
}
