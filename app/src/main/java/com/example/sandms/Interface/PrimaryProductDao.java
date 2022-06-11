package com.example.sandms.Interface;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.example.sandms.Model.PrimaryProduct;

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

    @Query("UPDATE primary_product SET qty = :sQty,Txtqty = :sTxtQty,Subtotal = :sSubTotal,Tax_Value = :sTax_Value,Discount =:sDiscount,Tax_amt = :sTax_amt,Dis_amt =:sDis_amt WHERE PID= :sID")
    void update(String sID,String sQty,String sTxtQty,String sSubTotal,String sTax_Value,String sDiscount,String sTax_amt,String sDis_amt);
}