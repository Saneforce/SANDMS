package com.saneforce.dms.model;

import java.util.List;

/**
 * Designed and Developed by Mohammad suhail ahmed on 24/02/2020
 */
public class ParentMyOrderModel {
    private String subCategoryName;

    private List<ChildMyOrderModel> childMyOrderModels;

    public List<ChildMyOrderModel> getChildMyOrderModels() {
        return childMyOrderModels;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public void setChildMyOrderModels(List<ChildMyOrderModel> childMyOrderModels) {
        this.childMyOrderModels = childMyOrderModels;
    }
    public ParentMyOrderModel(String subCategoryName, List<ChildMyOrderModel> childMyOrderModels)
    {
        this.subCategoryName = subCategoryName;
        this.childMyOrderModels = childMyOrderModels;
    }
}
