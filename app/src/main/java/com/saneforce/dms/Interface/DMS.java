package com.saneforce.dms.Interface;

import android.content.DialogInterface;

import com.saneforce.dms.Utils.Common_Model;

import java.util.List;

public interface DMS {

    interface ParentListInterface {
        void onClickParentInter(String value, int totalValue, String itemID, Integer positionValue, String productName, Float productCode, Float productQuantiy, String catImage, String catName, Integer Value, String UkeyPID, String productunit, String Pname, String Uom);

        void ProductImage(String ImageUrl);
    }

    interface DisptachEditing{
        void onClickParentInter(String position,String Slno,String PCode,String OrderId, String ProductName,
                                String OldCQty,
                                String Newvalue,String Oldvalue, String Rate,
                                String Cl_bal, String Unit, String newCQty);

    }

    interface CheckingInterface{
        void ProdcutDetails(String id,String name,String Image);
    }

    interface CheckingInterface1{
        void ProdcutDetails(int position, String id,String name,String Image);
    }

    interface ChildListInterface {
        void onClickInterface(String value, float totalValue, String itemID, Integer positionValue, String productName, Float productCode, Float productQuantiy, Integer Value, String UkeyPID, String productunit, String Pname, String Uom, String conFac, String Discont, String tax, float DiscontAmt, float taxAmt);
    }

    interface UpdateUi {
        void update(int value, int pos);
    }

    interface AlertBox {
        void PositiveMethod(DialogInterface dialog, int id);

        void NegativeMethod(DialogInterface dialog, int id);
    }

    interface Master_Interface {
        void OnclickMasterType(List<Common_Model> myDataset, int position, int type);
    }

    interface viewProduct {
        void onViewItemClick(String itemID, String productName, String catName, String catImg, Float productQty, Float productRate);
    }

    interface ViewReport {
        void reportCliick(String productId, String orderDate,String OrderValue);
    }

    interface On_ItemCLick_Listner {
        void onIntentClick(int Name);
    }


}
