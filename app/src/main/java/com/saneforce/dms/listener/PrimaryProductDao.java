package com.saneforce.dms.listener;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.saneforce.dms.model.PrimaryProduct;

import java.util.List;

@Dao
public interface PrimaryProductDao {

    @Insert
    void insert(PrimaryProduct contact);

    @Delete
    void delete(List<PrimaryProduct> contact);

    @Query("SELECT * FROM primary_product")
    LiveData<List<PrimaryProduct>> getAllContact();

    @Query("SELECT * FROM  primary_product  WHERE Product_Bar_Code = :category")
    List<PrimaryProduct> fetchTodoListByCategory(String category);

    @Query("SELECT * FROM primary_product Where Txtqty!=0")
    LiveData<List<PrimaryProduct>> getFilterData();

    @Query("UPDATE primary_product SET qty = :sQty,Txtqty = :sTxtQty,Subtotal = :sSubTotal,Tax_Value = :sTax_Value,Discount =:sDiscount,Tax_amt = :sTax_amt,Dis_amt =:sDis_amt,selectedScheme =:selectedScheme,selectedDisValue =:selectedDisValue,selectedFree =:selectedFree,Off_Pro_code =:Off_Pro_code,Off_Pro_name =:Off_Pro_name,Off_Pro_Unit =:Off_Pro_Unit,Off_disc_type =:Off_disc_type,Off_free_unit =:Off_free_unit WHERE PID= :sID")
    void update(String sID,String sQty,String sTxtQty,String sSubTotal,String sTax_Value,String sDiscount,
                String sTax_amt,String sDis_amt,String selectedScheme,String selectedDisValue,String selectedFree,String Off_Pro_code,
                String Off_Pro_name,String Off_Pro_Unit,String Off_disc_type,String Off_free_unit);

    @Query("UPDATE primary_product SET qty = :sQty,Txtqty = :sTxtQty WHERE PID= :sID")
    void update(String sID,String sQty,String sTxtQty);

    @Query("UPDATE primary_product SET Product_Sale_Unit = :sPdtQty,Product_Sale_Unit_Cn_Qty = :Product_Sale_Unit_Cn_Qty,Subtotal = :sSubTotal WHERE PID= :sID")
    void updateDATA(String sID,String sPdtQty,int Product_Sale_Unit_Cn_Qty,String sSubTotal);

    @Query("UPDATE primary_product SET isEdited = :isEdited,editedDiscount = :editedDiscount ,editedPrice = :editedPrice WHERE PID= :sID")
    void updateEditDiscount(String sID,boolean isEdited, String editedDiscount, String editedPrice);


    @Query("DELETE FROM Primary_Product WHERE PID = :Pid")
    void deleteById(String Pid);

}
