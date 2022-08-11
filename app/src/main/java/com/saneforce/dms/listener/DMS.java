package com.saneforce.dms.listener;

import android.content.Context;
import android.content.DialogInterface;

import com.saneforce.dms.utils.Common_Model;

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
        void reportCliick(String productId, String orderDate,String OrderValue,String orderType,String editOrder,int Paymentflag,int Dispatch_Flag, String dispatch_date, String payment_type, String payment_option, String check_utr_no, String attachment);

        void reportClick(String productId, String orderDate, String OrderValue, String orderType, String editOrder, int Paymentflag, int Dispatch_Flag, String dispatch_date, String payment_type, String payment_option, String check_utr_no, String attachment);
    }

    interface On_ItemCLick_Listner {
        void onIntentClick(int Name);
    }

    interface PaymentResponse {
        void onResponse(String response);
    }


    interface PaymentResponseBilldesk {
        void onResponse(Context context, String response);
    }


}
