package com.example.sandms.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportDataList {
    @SerializedName("Data")
    @Expose
    private List<ReportModel> data = null;

    public List<ReportModel> getData() {
        return data;
    }

    public void setData(List<ReportModel> data) {
        this.data = data;
    }
}
