package com.example.sandms.Interface;
//
//
//
import com.example.sandms.Model.HeaderCat;
import com.example.sandms.Model.ReportDataList;
import com.google.gson.JsonObject;







import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;





import com.example.sandms.Model.HeaderCat;
import com.example.sandms.Model.ReportDataList;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
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

    /*Gmail*/
    @GET("db_v14.php?axn=get/StockistGLogin")
    Call<JsonObject> Glogin(@Query("Email") String email);

    @GET("db_v14.php?axn=get/Templates")
    Call<JsonObject> getTemplates(@Query("SfCode") String sfcode);

    /*UserName and Password*/
    @GET("db_v14.php?axn=get/GoogleUserLogin")
    Call<JsonObject> userLogin(@Query("Username") String sfcode, @Query("Password") String pass);

    /*SUbcategory*/
    @FormUrlEncoded
    @POST("db_v14.php?axn=table/list")
    Call<HeaderCat> SubCategory(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);

    @FormUrlEncoded
    @POST("db_v14.php?axn=table/list")
    Call<Object> SubCategoryLog(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);

    /*submitValue*/
    @FormUrlEncoded
    @POST("db_v14.php?")
    Call<JsonObject> submitValue(@Query("axn") String Axn, @Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Field("data") String data);

    /*ReportView*/
    @POST("db_v14.php?")
    Call<ReportDataList> reportValues(@Query("axn") String Axn,@Query("Sf_code") String sFCode, @Query("fromdate") String fromdate, @Query("todate") String todate);

    /*DateReportView*/
    @POST("db_v14.php?")
    Call<JsonObject> dateReport(@Query("axn") String Axn,@Query("Order_Id") String rsfCode, @Query("Sf_code") String sFCode);


    /*Add New Retailer*/
    @FormUrlEncoded
    @POST("db_v14.php?axn=table/list")
    Call<JsonObject> retailerClass(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);

    /*Get Retailer Name*/
    @FormUrlEncoded
    @POST("db_v14.php?axn=table/list")
    Call<JsonObject> getRetName(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);

    /*Save New Retailer*/
    @FormUrlEncoded
    @POST("db_v14.php?axn=dcr/save")
    Call<JsonObject> addNewRetailer(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("State_Code") String StateCode, @Query("desig") String desig, @Field("data") String data);

    /*retailer details*/
    @POST("db_v14.php?axn=get/precall")
    Call<JsonObject> retailerViewDetails(@Query("Msl_No") String retailerID, @Query("divisionCode") String divisionCode, @Query("sfCode") String sfCode);

    /*Save Retailer*/
    @FormUrlEncoded
    @POST("db_v14.php?")
    Call<JsonObject> getDetails(@Query("axn") String Axn, @Field("data") String data);

    @POST("db_v14.php?")
    Call<JsonObject> getOfflineMode(@Query("axn") String Axn, @Query("divisionCode") String divisionCode);

    /*CHECKING*/
    @FormUrlEncoded
    @POST("db_v14.php?axn=table/list")
    Call<JsonObject> Category(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);


    @POST("db_v14.php?axn=get/getpripaymentverfication")
    Call<JsonObject> getPrimaryVerification(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);

    @POST("db_v14.php?axn=get/getpripaymentverfied")
    Call<JsonObject> getPaymentVerifed(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);

    @POST("db_v14.php?axn=get/getdispatchedpripaymentverfied")
    Call<JsonObject> getDisaptchCreated(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);


    @POST("db_v14.php?axn=get/getpripaymentverfiedlg")
    Call<JsonObject> getPendingVer(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);

    @POST("db_v14.php?axn=save/pripaymentverify")
    Call<JsonObject> getPayVerification(@Query("Order_Id") String disvisonCode, @Query("Sf_code") String sFCode);



    @Multipart
    @POST("db_v14.php?")
    Call<JsonObject> offlineImage(@Query("axn") String AXN,@Part MultipartBody.Part file);


    @GET("db_v14.php?axn=get/getprilgorderdets")
    Call<JsonObject> LogistData(@Query("OrderID") String OrderID);

    @FormUrlEncoded
    //For dispatch save url commented below 2 lines

    @POST("db_v14.php?axn=save/editpriorderlg")
    Call<JsonObject> Dispatch(@Query("sfCode") String OrderID, @Field("data") String data);



}


//
//public interface
//ApiInterface {
//
//    /*Gmail*/
//    @GET("db_v14.php?axn=get/StockistGLogin")
//    Call<JsonObject> Glogin(@Query("Email") String email);
//
//    @GET("db_v14.php?axn=get/Templates")
//    Call<JsonObject> getTemplates(@Query("SfCode") String sfcode);
//
//    /*UserName and Password*/
//    @GET("db_v14.php?axn=get/GoogleUserLogin")
//    Call<JsonObject> userLogin(@Query("Username") String sfcode, @Query("Password") String pass);
//
//    /*SUbcategory*/
//    @FormUrlEncoded
//    @POST("db_v14.php?axn=table/list")
//    Call<HeaderCat> SubCategory(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    @FormUrlEncoded
//    @POST("db_v14.php?axn=table/list")
//    Call<Object> SubCategoryLog(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    /*submitValue*/
//    @FormUrlEncoded
//    @POST("db_v14.php?")
//    Call<JsonObject> submitValue(@Query("axn") String Axn, @Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Field("data") String data);
//
//    /*ReportView*/
//    @POST("db_v14.php?")
//    Call<ReportDataList> reportValues(@Query("axn") String Axn,@Query("Sf_code") String sFCode, @Query("fromdate") String fromdate, @Query("todate") String todate);
//
//    /*DateReportView*/
//    @POST("db_v14.php?")
//    Call<JsonObject> dateReport(@Query("axn") String Axn,@Query("Order_Id") String rsfCode, @Query("Sf_code") String sFCode);
//
//
//    /*Add New Retailer*/
//    @FormUrlEncoded
//    @POST("db_v14.php?axn=table/list")
//    Call<JsonObject> retailerClass(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    /*Get Retailer Name*/
//    @FormUrlEncoded
//    @POST("db_v14.php?axn=table/list")
//    Call<JsonObject> getRetName(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//    /*Save New Retailer*/
//    @FormUrlEncoded
//    @POST("db_v14.php?axn=dcr/save")
//    Call<JsonObject> addNewRetailer(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("State_Code") String StateCode, @Query("desig") String desig, @Field("data") String data);
//
//    /*retailer details*/
//    @POST("db_v14.php?axn=get/precall")
//    Call<JsonObject> retailerViewDetails(@Query("Msl_No") String retailerID, @Query("divisionCode") String divisionCode, @Query("sfCode") String sfCode);
//
//    /*Save Retailer*/
//    @FormUrlEncoded
//    @POST("db_v14.php?")
//    Call<JsonObject> getDetails(@Query("axn") String Axn, @Field("data") String data);
//
//    @POST("db_v14.php?")
//    Call<JsonObject> getOfflineMode(@Query("axn") String Axn, @Query("divisionCode") String divisionCode);
//
//    /*CHECKING*/
//    @FormUrlEncoded
//    @POST("db_v14.php?axn=table/list")
//    Call<JsonObject> Category(@Query("divisionCode") String disvisonCode, @Query("sfCode") String sFCode, @Query("rSF") String rSF, @Query("State_Code") String StateCode, @Field("data") String data);
//
//
//    @POST("db_v14.php?axn=get/getpripaymentverfication")
//    Call<JsonObject> getPrimaryVerification(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//    @POST("db_v14.php?axn=get/getpripaymentverfied")
//    Call<JsonObject> getPaymentVerifed(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//    @POST("db_v14.php?axn=get/getdispatchedpripaymentverfied")
//    Call<JsonObject> getDisaptchCreated(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//
//    @POST("db_v14.php?axn=get/getpripaymentverfiedlg")
//    Call<JsonObject> getPendingVer(@Query("DivCode") String disvisonCode, @Query("Sf_code") String sFCode);
//
//    @POST("db_v14.php?axn=save/pripaymentverify")
//    Call<JsonObject> getPayVerification(@Query("Order_Id") String disvisonCode, @Query("Sf_code") String sFCode);
//
//
//
//    @Multipart
//    @POST("db_v14.php?")
//    Call<JsonObject> offlineImage(@Query("axn") String AXN,@Part MultipartBody.Part file);
//
//
//    @GET("db_v14.php?axn=get/getprilgorderdets")
//    Call<JsonObject> LogistData(@Query("OrderID") String OrderID);
//
//    @FormUrlEncoded
//    //For dispatch save url commented below 2 lines
//
//    @POST("db_v14.php?axn=save/editpriorderlg")
//    Call<JsonObject> Dispatch(@Query("sfCode") String OrderID, @Field("data") String data);
//
//
//
//}
//
