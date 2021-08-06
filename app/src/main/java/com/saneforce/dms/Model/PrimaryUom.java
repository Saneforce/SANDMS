package com.saneforce.dms.Model;

public class PrimaryUom {

    private String name, productCode, conQty;

    public String getName() {
        return name;
    }

    public PrimaryUom(String name, String productCode, String conQty) {
        this.name = name;
        this.productCode = productCode;
        this.conQty = conQty;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getConQty() {
        return conQty;
    }

    public void setConQty(String conQty) {
        this.conQty = conQty;
    }
}
