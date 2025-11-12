package com.slib.rms.analytics.dbt;

public class DbtDTO {
    private String subcommand = "run";
    private String select;
    private DbtVarsDTO vars;

    public DbtDTO(String select){
        this.select = select;
        this.vars = new DbtVarsDTO();
    }

    public DbtDTO(String subcommand, String select){
        this.subcommand = subcommand;
        this.select = select;
        this.vars = new DbtVarsDTO();
    }

    @Override
    public String toString() {
        return "DbtDTO{" +
                "subcommand='" + subcommand + '\'' +
                ", select='" + select + '\'' +
                ", vars=" + vars.toString() +
                '}';
    }

    public String getSubcommand() {
        return subcommand;
    }

    public void setSubcommand(String subcommand) {
        this.subcommand = subcommand;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public DbtVarsDTO getVars() {
        return vars;
    }

    public void setVars(DbtVarsDTO vars) {
        this.vars = vars;
    }
}
