package com.saneforce.dms.Interface;

import com.saneforce.dms.Model.HeaderCat;
import com.saneforce.dms.Model.ReportDataList;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
//    dms_db_V1
//    db_v14
    /*Gmail*/
    @GET("dms_db_V1.php?axn=get/StockistGLogin")
    Call<JsonObject> Glogin(@Query("Email") String email);

    @GET("dms_db_V1.php?axn=get/Templates")
    Call<JsonObject> getTemplates(@Query("SfCode") String sfcode);

    /*UserName and Password*/
    @GET("dms_db_V1.php?axn=get/GoogleUserLogin")
    Call<JsonObject> userLogin(@Query("Username") String sfcode, @Query("Password") String pass);

    /*SUbcategory*/
    @FormUrlEncoded
    @POST("dms_db_V1.php?axn=table/list")
    Call<HeaderCat> SubCategory(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);

    @FormUrlEncoded
    @POST("dms_db_V1.php?axn=table/list")
    Call<Object> SubCategoryLog(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);



    /*submitValue*/
    @FormUrlEncoded
    @POST("dms_db_V1.php?")
    Call<JsonObject> submitValue(@Query("axn") String Axn, @Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Field("data") String data);

    /*submitValue*/
    @Multipart
    @POST("dms_db_V1.php?")
    Call<ResponseBody> submitValue(@Query("axn") String Axn, @Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Part("data") RequestBody data, @Query("State_Code") String StateCode, @Query("desig") String desig);

    /*ReportView*/
    @POST("dms_db_V1.php?")
    Call<ReportDataList> reportValues(@Query("axn") String Axn,@Query("Sf_code") String sFCode, @Query("fromdate") String fromdate, @Query("todate") String todate);

    /*DateReportView*/
    @POST("dms_db_V1.php?")
    Call<JsonObject> dateReport(@Query("axn") String Axn,@Query("Order_Id") String rsfCode, @Query("Sf_code") String sFCode);


    /*Add New Retailer*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?axn=table/list")
//    Call<JsonObject> retailerClass(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
    @FormUrlEncoded
    @POST("dms_db_V1.php?axn=table/list")
    Call<JsonObject> retailerClass(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);

    /*Get Retailer Name*/
    @FormUrlEncoded
    @POST("dms_db_V1.php?axn=table/list")
    Call<JsonObject> getRetName(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);

    /*Save New Retailer*/
    @FormUrlEncoded
    @POST("dms_db_V1.php?axn=dcr/save")
    Call<JsonObject> addNewRetailer(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("State_Code") String StateCode, @Query("desig") String desig, @Field("data") String data);

    /*retailer details*/
    @POST("dms_db_V1.php?axn=get/precall")
    Call<JsonObject> retailerViewDetails(@Query("Msl_No") String retailerID, @Query("divisionCode") String divisionCode, @Query("sfCode") String sfCode);

    /*Save Retailer*/
    @FormUrlEncoded
    @POST("dms_db_V1.php?")
    Call<JsonObject> getDetails(@Query("axn") String Axn,  @Query("sfCode") String sFCode, @Field("data") String data);

    @POST("dms_db_V1.php?")
    Call<JsonObject> getOfflineMode(@Query("axn") String Axn, @Query("divisionCode") String divisionCode);

    /*CHECKING*/
    @FormUrlEncoded
    @POST("dms_db_V1.php?axn=table/list")
    Call<JsonObject> Category(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);


    @POST("dms_db_V1.php?axn=get/getpripaymentverfication")
    Call<JsonObject> getPrimaryVerification(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);

    @POST("dms_db_V1.php?axn=get/getpripaymentverfied")
    Call<JsonObject> getPaymentVerifed(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);

    @POST("dms_db_V1.php?axn=get/getdispatchedpripaymentverfied")
    Call<JsonObject> getDisaptchCreated(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);


    @POST("dms_db_V1.php?axn=get/getpripaymentverfiedlg")
    Call<JsonObject> getPendingVer(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);

    @POST("dms_db_V1.php?axn=save/pripaymentverify")
    Call<JsonObject> getPayVerification(@Query("Order_Id") String disvisonCode, @Query("Sf_code") String sFCode);



    @Multipart
    @POST("dms_db_V1.php?")
    Call<JsonObject> offlineImage(@Query("axn") String AXN,@Part MultipartBody.Part file);


    @GET("dms_db_V1.php?axn=get/getprilgorderdets")
    Call<JsonObject> LogistData(@Query("OrderID") String OrderID);

    @FormUrlEncoded
    //For dispatch save url commented below 2 lines

    @POST("dms_db_V1.php?axn=save/editpriorderlg")
    Call<JsonObject> Dispatch(@Query("sfCode") String OrderID, @Field("data") String data);
//profile Activity

     @GET("dms_db_V1.php?axn=get/ProfileMas_Stockist")
     Call<JsonObject> getProfile(@Query("Stockist_Code") String Stockist_Code);

     @FormUrlEncoded
     @POST("dms_db_V1.php?axn=save/profile")
     Call<JsonObject> updateProfile(@Query("Stockist_Code") String Stockist_Code, @Field("data") String data);

    @POST("dms_db_V1.php?axn=save/editpriorderlg")
    Call<JsonObject> GetOrderId(String data);

    @GET("dms_db_V1.php?axn=get/paymentkeys")
    Call<JsonObject> getPaymentKey(@Query("Stockist_Code") String Stockist_Code,@Query("DivCode") String disvisonCode);
    @GET("dms_db_V1.php?axn=get/productuom")
    Call<JsonObject> getProductuom(@Query("divisionCode") String disvisonCode);

}


//
//public interface
//ApiInterface {
//
//    /*Gmail*/
//    @GET("dms_db_V1.php?axn=get/StockistGLogin")
//    Call<JsonObject> Glogin(@Query("Email") String email);
//
//    @GET("dms_db_V1.php?axn=get/Templates")
//    Call<JsonObject> getTemplates(@Query("SfCode") String sfcode);
//
//    /*UserName and Password*/
//    @GET("dms_db_V1.php?axn=get/GoogleUserLogin")
//    Call<JsonObject> userLogin(@Query("Username") String sfcode, @Query("Password") String pass);
//
//    /*SUbcategory*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?axn=table/list")
//    Call<HeaderCat> SubCategory(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?axn=table/list")
//    Call<Object> SubCategoryLog(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    /*submitValue*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?")
//    Call<JsonObject> submitValue(@Query("axn") String Axn, @Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Field("data") String data);
//
//    /*ReportView*/
//    @POST("dms_db_V1.php?")
//    Call<ReportDataList> reportValues(@Query("axn") String Axn,@Query("Sf_code") String sFCode, @Query("fromdate") String fromdate, @Query("todate") String todate);
//
//    /*DateReportView*/
//    @POST("dms_db_V1.php?")
//    Call<JsonObject> dateReport(@Query("axn") String Axn,@Query("Order_Id") String rsfCode, @Query("Sf_code") String sFCode);
//
//
//    /*Add New Retailer*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?axn=table/list")
//    Call<JsonObject> retailerClass(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    /*Get Retailer Name*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?axn=table/list")
//    Call<JsonObject> getRetName(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    /*Save New Retailer*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?axn=dcr/save")
//    Call<JsonObject> addNewRetailer(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("State_Code") String StateCode, @Query("desig") String desig, @Field("data") String data);
//
//    /*retailer details*/
//    @POST("dms_db_V1.php?axn=get/precall")
//    Call<JsonObject> retailerViewDetails(@Query("Msl_No") String retailerID, @Query("divisionCode") String divisionCode, @Query("sfCode") String sfCode);
//
//    /*Save Retailer*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?")
//    Call<JsonObject> getDetails(@Query("axn") String Axn, @Field("data") String data);
//
//    @POST("dms_db_V1.php?")
//    Call<JsonObject> getOfflineMode(@Query("axn") String Axn, @Query("divisionCode") String divisionCode);
//
//    /*CHECKING*/
//    @FormUrlEncoded
//    @POST("dms_db_V1.php?axn=table/list")
//    Call<JsonObject> Category(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//
//    @POST("dms_db_V1.php?axn=get/getpripaymentverfication")
//    Call<JsonObject> getPrimaryVerification(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//    @POST("dms_db_V1.php?axn=get/getpripaymentverfied")
//    Call<JsonObject> getPaymentVerifed(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//    @POST("dms_db_V1.php?axn=get/getdispatchedpripaymentverfied")
//    Call<JsonObject> getDisaptchCreated(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//
//    @POST("dms_db_V1.php?axn=get/getpripaymentverfiedlg")
//    Call<JsonObject> getPendingVer(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//    @POST("dms_db_V1.php?axn=save/pripaymentverify")
//    Call<JsonObject> getPayVerification(@Query("Order_Id") String disvisonCode, @Query("Sf_code") String sFCode);
//
//
//
//    @Multipart
//    @POST("dms_db_V1.php?")
//    Call<JsonObject> offlineImage(@Query("axn") String AXN,@Part MultipartBody.Part file);
//
//
//    @GET("dms_db_V1.php?axn=get/getprilgorderdets")
//    Call<JsonObject> LogistData(@Query("OrderID") String OrderID);
//
//    @FormUrlEncoded
//    //For dispatch save url commented below 2 lines
//
//    @POST("dms_db_V1.php?axn=save/editpriorderlg")
//    Call<JsonObject> Dispatch(@Query("sfCode") String OrderID, @Field("data") String data);
//
//
//
//}
//
