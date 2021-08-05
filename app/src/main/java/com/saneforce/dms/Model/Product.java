package com.saneforce.dms.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("Product_Cat_Code")
    @Expose
    private Float productCatCode;

    @SerializedName("Sale_Unit")
    @Expose
    private String productUnit;

    @SerializedName("Discount")
    @Expose
    private String Discount;
    @SerializedName("Scheme")
    @Expose
    private String Scheme;
    @SerializedName("Tax_value")
    @Expose
    private String Tax_value;

    @SerializedName("Product_Sale_Unit")
    @Expose
    private String ProductSaleUnit;

    @SerializedName("PID")
    @Expose
    private String PID;

    @SerializedName("Pname")
    @Expose
    private String Pname;

    @SerializedName("UOM")
    @Expose
    private String UOM;

    @SerializedName("Conv_Fac")
    @Expose
    private String Conv_Fac;

    @SerializedName("Product_Brd_Code")
    @Expose
    private String productBrdCode;

    public Product(String id, String name, Float productCatCode, String productUnit, String discount, String scheme, String tax_value, String productSaleUnit, String PID, String pname, String UOM, int mQuantity, String Conv_Fac) {
        this.id = id;
        this.name = name;
        this.productCatCode = productCatCode;
        this.productUnit = productUnit;
        Discount = discount;
        Scheme = scheme;
        Tax_value = tax_value;
        ProductSaleUnit = productSaleUnit;
        this.PID = PID;
        Pname = pname;
        this.UOM = UOM;
        this.mQuantity = mQuantity;
        this.Conv_Fac = Conv_Fac;
    }


    public String getProductBrdCode() {
        return productBrdCode;
    }

    public void setProductBrdCode(String productBrdCode) {
        this.productBrdCode = productBrdCode;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getProductSaleUnit() {
        return ProductSaleUnit;
    }

    public void setProductSaleUnit(String productSaleUnit) {
        ProductSaleUnit = productSaleUnit;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    private int mQuantity;

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getScheme() {
        return Scheme;
    }

    public void setScheme(String scheme) {
        Scheme = scheme;
    }

    public String getTax_value() {
        return Tax_value;
    }

    public void setTax_value(String tax_value) {
        Tax_value = tax_value;
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }


    public void addToQuantity() {
        this.mQuantity += 1;
    }

    public void removeFromQuantity() {
        if (this.mQuantity > 0) {
            this.mQuantity -= 1;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getProductCatCode() {
        return productCatCode;
    }

    public void setProductCatCode(Float productCatCode) {
        this.productCatCode = productCatCode;
    }


    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getConv_Fac() {
        return Conv_Fac;
    }

    public void setConv_Fac(String conv_Fac) {
        Conv_Fac = conv_Fac;
    }
}
