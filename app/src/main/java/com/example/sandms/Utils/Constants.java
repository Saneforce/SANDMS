package com.example.sandms.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Model.PrimaryProduct;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Constants {
	public static final String PARAMETER_SEP = "&";
	public static final String PARAMETER_EQUALS = "=";
	public static final String TRANS_URL = "https://secure.ccavenue.com/transaction/initTrans";

	public static boolean isInternetAvailable(Context context){
		ConnectivityManager cm =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();

	}

	// This method  converts String to RequestBody
	public static RequestBody toRequestBody (JSONArray value) {
		RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value.toString());
		return body ;
	}

	// This method  converts String to RequestBody
	public static RequestBody toRequestBody (JSONObject value) {
		RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value.toString());
		return body ;
	}

	public static boolean isNetworkAvailable(Context context) {
		boolean isConnected = false;
		if(context!=null) {
			final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
			if(connectivityManager.getActiveNetworkInfo()!=null) {
				isConnected = connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
			}
		}else{
			isConnected = true;
		}

		try {
			Log.d("Constant", "isNetworkAvailable: isConnected => "+ isConnected );
//            if(!isConnected)
//                Constant.showToast(context, "No Internet Connection Available");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isConnected;
	}

	public static String roundTwoDecimals(double d) {
		try {
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return twoDForm.format(d);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return String.valueOf(d);
		}
	}


/*
	private void updateSchemeData(List<PrimaryProduct.SchemeProducts> schemeProducts, int qty, PrimaryProduct mContact, RecyclerView.ViewHolder holder, int position, PrimaryProduct contact) {
		int product_Sale_Unit_Cn_Qty = 1;
		if(mContact.getProduct_Sale_Unit_Cn_Qty()!=0)
			product_Sale_Unit_Cn_Qty= mContact.getProduct_Sale_Unit_Cn_Qty();
*/
/*
        double value=
        subTotal = Double.parseDouble(mContact.getProduct_Cat_Code()) * mContact.getProduct_Sale_Unit_Cn_Qty();

        tax = Float.valueOf(mContact.getTax_Value());

        mContact.getTxtqty()
        valueTotal = subTotal*tax/100;
        subTotal = (taxAmt* subTotal) / 100;
        subTotal = subTotal * mContact.getProduct_Sale_Unit_Cn_Qty();


*//*



		PrimaryProduct.SchemeProducts selectedScheme = null;
		int previousSchemeCount = 0;
		for(PrimaryProduct.SchemeProducts scheme : schemeProducts){
			if(!scheme.getScheme().equals("")) {
				int currentSchemeCount = Integer.parseInt(scheme.getScheme());
				if(previousSchemeCount <= currentSchemeCount &&  currentSchemeCount <= qty){
					previousSchemeCount =currentSchemeCount;
					selectedScheme = scheme;
				}
			}
		}

		String discountType = "";

		double discountValue = 0;
		double productAmt = 0;
		double schemeDisc = 0;
		if(selectedScheme != null){
//			workinglist.get(position).setSelectedScheme(selectedScheme.getScheme());
			contact.setSelectedScheme(selectedScheme.getScheme());

//			workinglist.get(position).setSelectedDisValue(selectedScheme.getDiscountvalue());
			contact.setSelectedDisValue(selectedScheme.getDiscountvalue());

//			workinglist.get(position).setOff_Pro_code(selectedScheme.getProduct_Code());
			contact.setOff_Pro_code(selectedScheme.getProduct_Code());

//			workinglist.get(position).setOff_Pro_name(selectedScheme.getProduct_Name());
			contact.setOff_Pro_name(selectedScheme.getProduct_Name());

//			workinglist.get(position).setOff_Pro_Unit(selectedScheme.getScheme_Unit());
			contact.setOff_Pro_Unit(selectedScheme.getScheme_Unit());
			discountType= selectedScheme.getDiscount_Type();


			if(discountType.equals("Rs"))
				holder.ll_disc.setVisibility(View.GONE);
			else
				holder.ll_disc.setVisibility(View.VISIBLE);

			String packageType = selectedScheme.getPackage();

			double freeQty = 0;
			double packageCalc = 0;
			switch (packageType){
				case "N":
					packageCalc = (int)(qty/Integer.parseInt(selectedScheme.getScheme()));
					break;
				case "Y":
					packageCalc = (double) (qty/Integer.parseInt(selectedScheme.getScheme()));
					break;
//                default:

			}
			if(!selectedScheme.getFree().equals(""))
				freeQty = packageCalc * Integer.parseInt(selectedScheme.getFree());

			workinglist.get(position).setSelectedFree(String.valueOf(freeQty));
			contact.setSelectedFree(String.valueOf(freeQty));

			holder.tv_free_qty.setText(String.valueOf(freeQty));



			if(mContact.getProduct_Cat_Code()!=null && !mContact.getProduct_Cat_Code().equals(""))
				productAmt = Double.parseDouble(mContact.getProduct_Cat_Code());

			if(selectedScheme.getDiscountvalue()!=null && !selectedScheme.getDiscountvalue().equals(""))
				schemeDisc = Double.parseDouble(selectedScheme.getDiscountvalue());

			switch (discountType){
				case "%":
					discountValue = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) * (schemeDisc/100);
					holder.ll_disc.setVisibility(View.VISIBLE);
					holder.ProductDis.setText(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
					holder.ProductDisAmt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
					break;
				case "Rs":
					discountValue = ((double) qty/Integer.parseInt(selectedScheme.getScheme())) * schemeDisc;
					holder.ProductDisAmt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
					holder.ll_disc.setVisibility(View.GONE);
					break;
//                default:
			}


			if(discountValue>0){
				workinglist.get(position).setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));
				contact.setDiscount(String.valueOf(Constants.roundTwoDecimals(schemeDisc)));

				workinglist.get(position).setDis_amt(Constants.roundTwoDecimals(discountValue));
				contact.setDis_amt(Constants.roundTwoDecimals(discountValue));
*/
/*
                totalAmt = (productAmt * (qty * product_Sale_Unit_Cn_Qty)) -discountValue;
                holder.ll_disc_reduction.setVisibility(View.VISIBLE);
                holder.tv_disc_amt.setText(String.valueOf(Constants.roundTwoDecimals(discountValue)));
                holder.tv_disc_amt_total.setText(String.valueOf(Constants.roundTwoDecimals(totalAmt)));*//*


			}

		}else {
			holder.ll_disc.setVisibility(View.VISIBLE);
			holder.tv_free_qty.setText("0");
		}
		double totalAmt = 0;
		double taxPercent = 0;
		double taxAmt = 0;

		try {
			totalAmt = Double.parseDouble(mContact.getProduct_Cat_Code()) * (qty *product_Sale_Unit_Cn_Qty);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		try {
			taxPercent = Double.parseDouble(mContact.getTax_Value());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		holder.subProdcutChildRate.setText("Rs:" + Constants.roundTwoDecimals(Double.parseDouble(mContact.getProduct_Cat_Code())));
		holder.productItem.setText(String.valueOf(qty *product_Sale_Unit_Cn_Qty));
		holder.productItemTotal.setText(Constants.roundTwoDecimals(totalAmt));


		holder.ProductTax.setText(String.valueOf(taxPercent));

		try {
			taxAmt =  (totalAmt- discountValue) * (taxPercent/100);

		} catch (Exception e) {
			e.printStackTrace();
		}

		holder.ProductTaxAmt.setText(Constants.roundTwoDecimals(taxAmt));
		holder.tv_final_total_amt.setText(Constants.roundTwoDecimals(((totalAmt - discountValue) + taxAmt)));

	}
*/

	public static void hideKeyboard(Activity activity) {
		try {

			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			//Find the currently focused view, so we can grab the correct window token from it.
			View view = activity.getCurrentFocus();
			//If no view currently has focus, create a new one, just so we can grab a window token from it
			if (view == null) {
				view = new View(activity);
			}
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}catch (Exception e){
			e.printStackTrace();
		}

	}



}