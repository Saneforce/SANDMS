package com.saneforce.dms.Model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Primary_Product")
public class PrimaryProduct implements Serializable {

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
//    private String Con_fac;
    private int Product_Sale_Unit_Cn_Qty;

    private String selectedScheme = "";
    private String selectedDisValue = "";

    private String selectedFree = "";
    private String Off_Pro_code = "";
    private String Off_Pro_name = "";
    private String Off_Pro_Unit = "";
    private String Off_disc_type = "";
    private String Off_free_unit = "";
    private String golden_scheme = "";


    @TypeConverters({SchemeConverter.class})
    List<SchemeProducts> schemeProducts;

    @TypeConverters({UOMConverter.class})
    ArrayList<UOMlist> UOMList;

    public PrimaryProduct() {

    }

    public String getOff_free_unit() {
        return Off_free_unit;
    }

    public void setOff_free_unit(String off_free_unit) {
        Off_free_unit = off_free_unit;
    }

    public String getOff_disc_type() {
        return Off_disc_type;
    }

    public void setOff_disc_type(String off_disc_type) {
        Off_disc_type = off_disc_type;
    }

    public ArrayList<UOMlist> getUOMList() {
        return UOMList;
    }

    public void setUOMList(ArrayList<UOMlist> UOMList) {
        this.UOMList = UOMList;
    }

    public String getSelectedFree() {
        return selectedFree;
    }

    public void setSelectedFree(String selectedFree) {
        this.selectedFree = selectedFree;
    }

    public String getOff_Pro_code() {
        return Off_Pro_code;
    }

    public void setOff_Pro_code(String off_Pro_code) {
        Off_Pro_code = off_Pro_code;
    }

    public String getOff_Pro_name() {
        return Off_Pro_name;
    }

    public void setOff_Pro_name(String off_Pro_name) {
        Off_Pro_name = off_Pro_name;
    }

    public String getOff_Pro_Unit() {
        return Off_Pro_Unit;
    }

    public void setOff_Pro_Unit(String off_Pro_Unit) {
        Off_Pro_Unit = off_Pro_Unit;
    }

    public PrimaryProduct(String UID, String PID, String name, String Pname, String Product_Bar_Code,
                          String UOM, String Product_Cat_Code, String Product_Sale_Unit, String Discount,
                          String Tax_Value, String qty, String Txtqty, String Subtotal, String Dis_amt,
                          String Tax_amt, List<SchemeProducts> schemeProducts,
                          int Product_Sale_Unit_Cn_Qty, ArrayList<UOMlist> UOMList, String goldenScheme) {
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
//        this.Con_fac = Con_fac;
        this.schemeProducts = schemeProducts;
        this.Product_Sale_Unit_Cn_Qty=Product_Sale_Unit_Cn_Qty;
        this.UOMList=UOMList;
        this.golden_scheme= goldenScheme;
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

    public int getProduct_Sale_Unit_Cn_Qty() {
        return Product_Sale_Unit_Cn_Qty;
    }

    public void setProduct_Sale_Unit_Cn_Qty(int product_Sale_Unit_Cn_Qty) {
        Product_Sale_Unit_Cn_Qty = product_Sale_Unit_Cn_Qty;
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

/*    public String getCon_fac() {
        return Con_fac;
    }

    public void setCon_fac(String con_fac) {
        Con_fac = con_fac;
    }*/

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

    public List<SchemeProducts> getSchemeProducts() {
        return schemeProducts;
    }

    public void setSchemeProducts(List<SchemeProducts>  schemeProducts) {
        this.schemeProducts = schemeProducts;
    }

    public String getGolden_scheme() {
        return golden_scheme;
    }

    public void setGolden_scheme(String golden_scheme) {
        this.golden_scheme = golden_scheme;
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
        private String Free_Unit;

        public String getFree() {
            return Free;
        }

        public SchemeProducts(String Scheme, String Discountvalue, String Scheme_Unit, String Product_Name, String Product_Code, String Package, String Free, String Discount_Type, String Free_Unit) {
            this.Scheme = Scheme;
            this.Discountvalue = Discountvalue;
            this.Scheme_Unit = Scheme_Unit;
            this.Product_Name = Product_Name;
            this.Product_Code = Product_Code;
            this.Package = Package;
            this.Free = Free;
            this.Discount_Type = Discount_Type;
            this.Free_Unit = Free_Unit;
        }

        public String getFree_Unit() {
            return Free_Unit;
        }

        public void setFree_Unit(String free_Unit) {
            Free_Unit = free_Unit;
        }

        public String getDiscount_Type() {
            return Discount_Type;
        }

        public void setDiscount_Type(String discount_Type) {
            Discount_Type = discount_Type;
        }

        public void setFree(String free) {
            Free = free;
        }

        public String getPackage() {
            return Package;
        }

        public void setPackage(String aPackage) {
            Package = aPackage;
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

    public static class UOMlist implements Serializable {

        private String id;
        private String Product_Code;
        private String name;
        private String ConQty;

        public UOMlist(String id, String product_Code, String name, String conQty) {
            this.id = id;
            Product_Code = product_Code;
            this.name = name;
            ConQty = conQty;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProduct_Code() {
            return Product_Code;
        }

        public void setProduct_Code(String product_Code) {
            Product_Code = product_Code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getConQty() {
            return ConQty;
        }

        public void setConQty(String conQty) {
            ConQty = conQty;
        }

        @Override
        public String toString() {
            return "UOMlist{" +
                    "id='" + id + '\'' +
                    ", Product_Code='" + Product_Code + '\'' +
                    ", name='" + name + '\'' +
                    ", ConQty='" + ConQty + '\'' +
                    '}';
        }
    }


    public static class UOMConverter {

        @TypeConverter
        public  ArrayList<UOMlist> restoreBankList(String listOfString) {
            return new Gson().fromJson(listOfString, new TypeToken<ArrayList<UOMlist>>() {}.getType());
        }

        @TypeConverter
        public String saveListAsString(ArrayList<UOMlist> listOfString) {
            return new Gson().toJson(listOfString);
        }

    }

    public static class SchemeConverter {

        @TypeConverter
        public  List<SchemeProducts> restoreBankList(String listOfString) {
            return new Gson().fromJson(listOfString, new TypeToken<List<SchemeProducts>>() {}.getType());
        }

        @TypeConverter
        public String saveListAsString(List<SchemeProducts> listOfString) {
            return new Gson().toJson(listOfString);
        }

    }



}




