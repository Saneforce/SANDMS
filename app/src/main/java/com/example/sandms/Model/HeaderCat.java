package com.example.sandms.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeaderCat {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("Data")
    @Expose
    private List<HeaderName> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<HeaderName> getData() {
        return data;
    }

    public void setData(List<HeaderName> data) {
        this.data = data;
    }

}
