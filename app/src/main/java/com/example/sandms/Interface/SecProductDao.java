package com.example.sandms.Interface;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.sandms.Model.PrimaryProduct;
import com.example.sandms.Model.SecondaryProduct;

import java.util.List;

@Dao
public interface SecProductDao {

    @Insert
    void insert(SecondaryProduct contact);

    @Delete
    void delete(List<SecondaryProduct> contact);

    @Query("SELECT * FROM Secondary_Product")
    LiveData<List<SecondaryProduct>> getAllContact();


    @Query("SELECT * FROM  Secondary_Product  WHERE Product_Bar_Code = :category")
    List<SecondaryProduct> fetchTodoListByCategory(String category);

    @Query("SELECT * FROM Secondary_Product Where Txtqty!=0")
    LiveData<List<SecondaryProduct>> getFilterData();

    @Query("UPDATE Secondary_Product SET qty = :sQty,Txtqty = :sTxtQty,Subtotal = :sSubTotal,Tax_Value = :sTax_Value,Discount =:sDiscount,Tax_amt = :sTax_amt,Dis_amt =:sDis_amt,selectedScheme =:selectedScheme,selectedDisValue =:selectedDisValue,selectedFree =:selectedFree,Off_Pro_code =:Off_Pro_code,Off_Pro_name =:Off_Pro_name,Off_Pro_Unit =:Off_Pro_Unit WHERE PID= :sID")
    void update(String sID,String sQty,String sTxtQty,String sSubTotal,String sTax_Value,String sDiscount,String sTax_amt,String sDis_amt,String selectedScheme,String selectedDisValue,String selectedFree,String Off_Pro_code,
                String Off_Pro_name,String Off_Pro_Unit);


}
