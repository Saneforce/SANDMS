package com.example.sandms.Model;


import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "Secondary_Product")
public class SecondaryProduct implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String UID;
    private String PID;
    private String name;
    private String Pname;
    private String UOM;
    private String Product_Cat_Code;
    private String Product_Bar_Code;
    private String Product_Sale_Unit;
    private String Discount;
    private String Tax_Value;
    private String qty;
    private String Txtqty;
    private String Subtotal;
    private String Dis_amt;
    private String Tax_amt;
    private String Con_fac;


    private String selectedScheme = "";
    private String selectedDisValue = "";


    @TypeConverters({Converter.class})
    List<SecondaryProduct.SchemeProducts> schemeProducts;


    public static class Converter {

        @TypeConverter
        public  List<SecondaryProduct.SchemeProducts> restoreBankList(String listOfString) {
            return new Gson().fromJson(listOfString, new TypeToken<List<SecondaryProduct.SchemeProducts>>() {}.getType());
        }

        @TypeConverter
        public String saveListAsString(List<SecondaryProduct.SchemeProducts> listOfString) {
            return new Gson().toJson(listOfString);
        }

    }


    public SecondaryProduct(String UID, String PID, String name, String Pname, String Product_Bar_Code,
                            String UOM, String Product_Cat_Code, String Product_Sale_Unit, String Discount,
                            String Tax_Value, String qty, String Txtqty, String Subtotal, String Dis_amt,
                            String Tax_amt, String Con_fac, List<SecondaryProduct.SchemeProducts> schemeProducts) {
        this.UID = UID;
        this.PID = PID;
        this.name = name;
        this.Pname = Pname;
        this.UOM = UOM;
        this.Product_Cat_Code = Product_Cat_Code;
        this.Product_Bar_Code = Product_Bar_Code;
        this.Product_Sale_Unit = Product_Sale_Unit;
        this.Discount = Discount;
        this.Tax_Value = Tax_Value;
        this.qty = qty;
        this.Txtqty = Txtqty;
        this.Subtotal = Subtotal;
        this.Dis_amt = Dis_amt;
        this.Tax_amt = Tax_amt;
        this.Con_fac = Con_fac;
        this.schemeProducts = schemeProducts;
    }

    public String getSelectedScheme() {
        return selectedScheme;
    }

    public void setSelectedScheme(String selectedScheme) {
        this.selectedScheme = selectedScheme;
    }

    public String getSelectedDisValue() {
        return selectedDisValue;
    }

    public void setSelectedDisValue(String selectedDisValue) {
        this.selectedDisValue = selectedDisValue;
    }

    public void setSchemeProducts(List<SecondaryProduct.SchemeProducts> schemeProducts) {
        this.schemeProducts = schemeProducts;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDis_amt() {
        return Dis_amt;
    }

    public void setDis_amt(String dis_amt) {
        Dis_amt = dis_amt;
    }

    public String getTax_amt() {
        return Tax_amt;
    }

    public void setTax_amt(String tax_amt) {
        Tax_amt = tax_amt;
    }

    public String getCon_fac() {
        return Con_fac;
    }

    public void setCon_fac(String con_fac) {
        Con_fac = con_fac;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
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

    public String getProduct_Cat_Code() {
        return Product_Cat_Code;
    }

    public void setProduct_Cat_Code(String product_Cat_Code) {
        Product_Cat_Code = product_Cat_Code;
    }

    public String getProduct_Sale_Unit() {
        return Product_Sale_Unit;
    }

    public void setProduct_Sale_Unit(String product_Sale_Unit) {
        Product_Sale_Unit = product_Sale_Unit;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getTax_Value() {
        return Tax_Value;
    }

    public void setTax_Value(String tax_Value) {
        Tax_Value = tax_Value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTxtqty() {
        return Txtqty;
    }

    public void setTxtqty(String txtqty) {
        Txtqty = txtqty;
    }


    public String getProduct_Bar_Code() {
        return Product_Bar_Code;
    }

    public void setProduct_Bar_Code(String product_Bar_Code) {
        Product_Bar_Code = product_Bar_Code;
    }

    public String getSubtotal() {
        return Subtotal;
    }

    public void setSubtotal(String subtotal) {
        Subtotal = subtotal;
    }

    public List<SecondaryProduct.SchemeProducts> getSchemeProducts() {
        return schemeProducts;
    }

    public static class SchemeProducts{

        private String Scheme;
        private String Discountvalue;
        private String Scheme_Unit;
        private String Product_Name;
        private String Product_Code;
        private String Package;
        private String Free;
        private String Discount_Type;


        public SchemeProducts(String Scheme, String Discountvalue, String Scheme_Unit, String Product_Name, String Product_Code, String Package, String Free, String Discount_Type) {
            this.Scheme = Scheme;
            this.Discountvalue = Discountvalue;
            this.Scheme_Unit = Scheme_Unit;
            this.Product_Name = Product_Name;
            this.Product_Code = Product_Code;
            this.Package = Package;
            this.Free = Free;
            this.Discount_Type = Discount_Type;

        }

        public String getPackage() {
            return Package;
        }

        public void setPackage(String aPackage) {
            Package = aPackage;
        }

        public String getFree() {
            return Free;
        }

        public void setFree(String free) {
            Free = free;
        }

        public String getDiscount_Type() {
            return Discount_Type;
        }

        public void setDiscount_Type(String discount_Type) {
            Discount_Type = discount_Type;
        }

        public String getScheme() {
            return Scheme;
        }

        public void setScheme(String scheme) {
            Scheme = scheme;
        }

        public String getDiscountvalue() {
            return Discountvalue;
        }

        public void setDiscountvalue(String discountvalue) {
            Discountvalue = discountvalue;
        }

        public String getScheme_Unit() {
            return Scheme_Unit;
        }

        public void setScheme_Unit(String scheme_Unit) {
            Scheme_Unit = scheme_Unit;
        }

        public String getProduct_Name() {
            return Product_Name;
        }

        public void setProduct_Name(String product_Name) {
            Product_Name = product_Name;
        }

        public String getProduct_Code() {
            return Product_Code;
        }

        public void setProduct_Code(String product_Code) {
            Product_Code = product_Code;
        }
    }

}
