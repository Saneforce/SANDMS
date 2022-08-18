package com.saneforce.dms.model;

public class ReportNoOrderModel {
    int Trans_Detail_Slno;
    int Trans_Detail_Info_Type;
    int ModTime;

    public int getTrans_Detail_Slno() {
        return Trans_Detail_Slno;
    }

    public void setTrans_Detail_Slno(int Trans_Detail_Slno) {
        Trans_Detail_Slno = Trans_Detail_Slno;
    }

    public int getTrans_Detail_Info_Type() {
        return Trans_Detail_Info_Type;
    }

    public void setTrans_Detail_Info_Type(int trans_Detail_Info_Type) {
        Trans_Detail_Info_Type = trans_Detail_Info_Type;
    }

    public int getModTime() {
        return ModTime;
    }

    public void setModTime(int modTime) {
        ModTime = modTime;
    }

    public ReportNoOrderModel(int Trans_Detail_Slno, int trans_Detail_Info_Type, int modTime) {
        Trans_Detail_Slno = Trans_Detail_Slno;
        Trans_Detail_Info_Type = trans_Detail_Info_Type;
        ModTime = modTime;

    }
}
