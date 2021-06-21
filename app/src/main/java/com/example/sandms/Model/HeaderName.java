package com.example.sandms.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class HeaderName {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("Division_Code")
    @Expose
    private Integer divisionCode;
    @SerializedName("Product_Cat_Code")
    @Expose
    private Integer productCatCode;
    @SerializedName("SampleQty")
    @Expose
    private Integer sampleQty;
    @SerializedName("Cat_Image")
    @Expose
    private String catImage;

    @SerializedName("SaleUnit")
    @Expose
    private String SaleUnit;
    @SerializedName("Product")
    @Expose
    private List<Product> product = null;


    private String adapterText;

    private ArrayList<String> mStringArrayList = null;

    public HeaderName() {

    }


    public HeaderName(String adapterText) {
        this.adapterText = adapterText;
    }

    public String getAdapterText() {
        return adapterText;
    }

    public void setAdapterText(String adapterText) {
        this.adapterText = adapterText;
    }

    public HeaderName(ArrayList<String> mStringArrayList) {

        this.mStringArrayList = mStringArrayList;
    }

    private Integer getSubTotal;

    public Integer getGetSubTotal() {
        return getSubTotal;
    }

    public void setGetSubTotal(Integer getSubTotal) {
        this.getSubTotal = getSubTotal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(Integer divisionCode) {
        this.divisionCode = divisionCode;
    }

    public Integer getProductCatCode() {
        return productCatCode;
    }

    public void setProductCatCode(Integer productCatCode) {
        this.productCatCode = productCatCode;
    }

    public Integer getSampleQty() {
        return sampleQty;
    }

    public void setSampleQty(Integer sampleQty) {
        this.sampleQty = sampleQty;
    }

    public String getCatImage() {
        return catImage;
    }

    public void setCatImage(String catImage) {
        this.catImage = catImage;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }

    public String getSaleUnit() {
        return SaleUnit;
    }

    public void setSaleUnit(String saleUnit) {
        SaleUnit = saleUnit;
    }
}
