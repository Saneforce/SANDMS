package com.example.sandms.Model;

public class Product_Array {
    String productcode;
    String productname;
    Float productqty;
    Float sampleqty;
    Float productRate;
    String catImage;
    String catName;
    String UkeyId;
    String ProductUnit;
    String ProductName;
    String ProductUOM;
    String ConFac;
    String Discount;
    String TaxValue;
    Float DisAmt;
    Float TaxAmt;
    Float ProductActualTotal;


    public Product_Array(String productcode, String productname, Float productqty, Float sampleqty, Float productRate, String catImage, String catName) {
        this.productcode = productcode;
        this.productname = productname;
        this.productqty = productqty;
        this.sampleqty = sampleqty;
        this.productRate = productRate;
        this.catImage = catImage;
        this.catName = catName;

    }

    public Product_Array(String productcode, String productname, Float productqty, Float sampleqty, Float productRate, String catImage, String catName, String ProductName, String ProductUOM, String ConFac, String Discount,
                         String TaxValue,
                         Float DisAmt,
                         Float TaxAmt, Float ProductActualTotal) {
        this.productcode = productcode;
        this.productname = productname;
        this.productqty = productqty;
        this.sampleqty = sampleqty;
        this.productRate = productRate;
        this.catImage = catImage;
        this.catName = catName;
        this.ProductName = ProductName;
        this.ProductUOM = ProductUOM;
        this.ConFac = ConFac;
        this.Discount = Discount;
        this.TaxValue = TaxValue;
        this.DisAmt = DisAmt;
        this.TaxAmt = TaxAmt;
        this.ProductActualTotal = ProductActualTotal;
    }

    public Product_Array(String productcode, String productname, Float productqty, Float sampleqty, Float productRate, String catImage, String catName, String ProductUnit) {
        this.productcode = productcode;
        this.productname = productname;
        this.productqty = productqty;
        this.sampleqty = sampleqty;
        this.productRate = productRate;
        this.catImage = catImage;
        this.catName = catName;
        this.ProductUnit = ProductUnit;
    }


    public Product_Array(String productcode, String productname, Float productqty, Float sampleqty, Float productRate) {
        this.productcode = productcode;
        this.productname = productname;
        this.productqty = productqty;
        this.sampleqty = sampleqty;
        this.productRate = productRate;
    }

    public Float getProductActualTotal() {
        return ProductActualTotal;
    }

    public void setProductActualTotal(Float productActualTotal) {
        ProductActualTotal = productActualTotal;
    }

    public String getConFac() {
        return ConFac;
    }

    public void setConFac(String conFac) {
        ConFac = conFac;
    }

    public String getUkeyId() {
        return UkeyId;
    }

    public void setUkeyId(String ukeyId) {
        UkeyId = ukeyId;
    }

    public String getProductUnit() {
        return ProductUnit;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getTaxValue() {
        return TaxValue;
    }

    public void setTaxValue(String taxValue) {
        TaxValue = taxValue;
    }

    public Float getDisAmt() {
        return DisAmt;
    }

    public void setDisAmt(Float disAmt) {
        DisAmt = disAmt;
    }

    public Float getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(Float taxAmt) {
        TaxAmt = taxAmt;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductUOM() {
        return ProductUOM;
    }

    public void setProductUOM(String productUOM) {
        ProductUOM = productUOM;
    }

    public void setProductUnit(String productUnit) {
        ProductUnit = productUnit;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatImage() {
        return catImage;
    }

    public void setCatImage(String catImage) {
        this.catImage = catImage;
    }

    public Float getProductRate() {
        return productRate;
    }

    public void setProductRate(Float productRate) {
        this.productRate = productRate;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public Float getProductqty() {
        return productqty;
    }

    public void setProductqty(Float productqty) {
        this.productqty = productqty;
    }

    public Float getSampleqty() {
        return sampleqty;
    }

    public void setSampleqty(Float sampleqty) {
        this.sampleqty = sampleqty;
    }
}
