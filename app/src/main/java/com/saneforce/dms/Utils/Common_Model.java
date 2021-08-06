package com.saneforce.dms.Utils;

public class Common_Model {
    private String name;
    private String id;
    private String flag;
    private String address;
    private String phone;
    String checkouttime;
    boolean isSelected;
    private Integer Pho;



    public Common_Model(String name, String id, String flag, String address, String phone) {
        this.name = name;
        this.id = id;
        this.flag = flag;
        this.address = address;
        this.phone = phone;
    }

    public Common_Model(String name, String id, String flag, String address, Integer phone) {
        this.name = name;
        this.id = id;
        this.flag = flag;
        this.address = address;
        this.Pho = phone;
    }

    public Common_Model(String id, String name, String flag, String checkouttime) {
        this.id = id;
        this.name = name;
        this.flag = flag;
        this.checkouttime = checkouttime;
    }

    public Common_Model(String name, String id, boolean isSelected) {
        this.name = name;
        this.id = id;
        this.isSelected = isSelected;
    }

    public String getCheckouttime() {
        return checkouttime;
    }

    public void setCheckouttime(String checkouttime) {
        this.checkouttime = checkouttime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Common_Model(String id, String name, String flag) {
        this.id = id;
        this.name = name;
        this.flag = flag;
    }
//    public Common_Model(String name, String productcode,String conqty, String flag) {
//        this.id = name;
//        this.name = productcode;
//        this.
//        this.flag = flag;
//    }
    public Common_Model(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public Integer getPho() {
        return Pho;
    }

    public void setPho(Integer pho) {
        Pho = pho;
    }
}
