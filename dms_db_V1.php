<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET, OPTIONS');
session_start();
$URL_BASE = "/";
date_default_timezone_set("Asia/Kolkata");

include "dbConn.php";
include "utils.php";
//GOVIND.php
function sendFCM_notify($registrationIds,$message,$title,$msgId,$event_id){
	// API access key from Google API's Console
	define('FIREBASE_API_KEY', 'AAAAEDz9WFs:APA91bEKbRUwSNA4kU8_q6B6TZqU95zIU8eYCcVAa-ioUt1L9o8hO_VHHOwi8gxaa89HeswDEcldNfNRExY-SfJRpesZ2FUXgFf3QsgCOtsBC-0fE0ZOFhHfSBUpubD1HAq3a_oMTUSP');
	//define('FIREBASE_API_KEY', 'AAAAFZqP7mw:APA91bG9q_tSrr3sjaHiMo8fqBI18z8z3KWxsmFeL3L_9AMuLnUoT3kRsmTH2DmvRHZrqSJn9nfVqQZGPmYvG4Z6skH304xWp2Wa7kz_jTVIsGI63t4PwZxtHjbeH-sqXMESogFb5QJ_');

	$msg = array
	(
		'text' 	=> $message,
		'title'		=> $title,
		'subtitle'	=> 'Request Alert!',
		'tickerText'	=> 'Request Alert!',
		'vibrate'	=> 1,
		'sound'		=> 1,
		'largeIcon'	=> 'large_icon',
		'smallIcon'	=> 'small_icon',
		'id'		=> $msgId,
		'event_id'	=> $event_id
	);

	$fields = array
	(
		'registration_ids' 	=> array($registrationIds),
		'data'			=> $msg
	);
	
	$headers = array
	(
		'Authorization: key=' . FIREBASE_API_KEY,
		'Content-Type: application/json'
	);
	 
	$ch = curl_init();
	curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
	curl_setopt( $ch,CURLOPT_POST, true );
	curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
	curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
	curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
	curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
	$result = curl_exec($ch );
	
	curl_close( $ch );	
}
function send_gcm_notify($reg_id, $message,$title) {
    define("GOOGLE_API_KEY", "AIzaSyAqfZ3MIMN418_WkoRMpAHz-399EfsW_XQ");
    define("GOOGLE_GCM_URL", "https://android.googleapis.com/gcm/send");

    $fields = array(
        'registration_ids' => array($reg_id),
        'data' => array("message" => $message, "title" => $title),
    );

    $headers = array(
        'Authorization: key=' . GOOGLE_API_KEY,
        'Content-Type: application/json'
    );

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, GOOGLE_GCM_URL);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

    $result = curl_exec($ch);
    if ($result === FALSE) {
        die('Problem occurred: ' . curl_error($ch));
    }
    curl_close($ch);
    //echo $result;
}
function FilterArry($array,$key,$val){
    $fRes = array_values(array_filter($array, function ($item) use ($key,$val) {
		if ($item[$key]==$val) {
		//if (stripos($item[$key], "0") !== false) {	
			return true;
		}
		return false;
	}));
	return $fRes;
}
function actionLogin() {
    global $URL_BASE;
    $data = json_decode($_POST['data'], true);
    $username = (string) $data['name'];
    $password = (string) $data['password'];
    $DeviceRegId = (string) $data['DeviceRegId'];
    
      
    $query = "exec LoginAPP '$username','$password'";
    global $conn;
    $arr;
    $res = sqlsrv_query($conn, $query);
    if ($res) {
        $result = array();
        while ($row = sqlsrv_fetch_array($res, SQLSRV_FETCH_ASSOC)) {
            $result[] = $row;
        }
        $arr = $result;
    }
    sqlsrv_close();
    $respon = array();
    $count = count($arr);
    $DIVV=str_replace(",", "", $arr[0]['Division_Code']);
    $query = "insert into Login_Time_Table(Sf_Code,Division_Code,Start_Time,End_Time,Start_Lat,Start_Long,End_Lat,End_Long,login_date) VALUES ('" . $arr[0]['SF_Code'] . "', '" .  $DIVV . "', '".date("h:i:sa")."','','','','','','".date('Y-m-d')."')";
    performQuery($query);
        
    if ($count == 1) {
        $respon['success'] = true;
        $respon['sfCode'] = $arr[0]['SF_Code'];
        $sfName = utf8_encode(trim(preg_replace("/[\r\n]+/", " ", $arr[0]['SF_Name'])));
        $respon['sfName'] = $sfName;
        $respon['divisionCode'] = $arr[0]['Division_Code'];
        $respon['call_report'] = $arr[0]['call_report'];
        $respon['desigCode'] = $arr[0]['desig_Code'];
        $respon['HlfNeed'] = $arr[0]['MGRHlfDy'];
        if ($DeviceRegId!=null&&!empty($DeviceRegId)) {
            $sql = "update Access_Table set  DeviceRegId='$DeviceRegId' where sf_code='" . $arr[0]['SF_Code'] . "'";
            performQuery($sql);
        }
        if ($arr[0]['desig_Code'] == "MR") {
            $query = "Select count(SF_Code) Cnt from Salesforce_Master where sf_Tp_Reporting='" . $arr[0]['SF_Code'] . "'";
            $dsgc = performQuery($query);
            if ($dsgc[0]['Cnt'] > 0)
                $respon['desigCode'] = "AM";
            else
                $respon['HlfNeed'] = $arr[0]['MRHlfDy'];
        }
        $query = "select * from dcrmain_trans where sf_code='" . $arr[0]['SF_Code'] . "' and Activity_Date=cast(GETDATE() as date)";
        $dcrtoday = performQuery($query);
        $respon['dcrtoday']=count($dcrtoday);
        $respon['edit_sumry'] = $arr[0]['edit_sumry'];
        $respon['prod'] = $arr[0]['prod'];
        $respon['JWNeed'] = $arr[0]['JWNeed'];
        
        $dat=date('Y-m-d');
        $sql="select * from TP_Attendance_App where Sf_Code='" . $arr[0]['SF_Code'] . "' and DATEADD(dd, 0, DATEDIFF(dd,0,Start_Time))='$dat'";
        $attendance=performQuery($sql);
        if(count($attendance)==0)
            $respon['attendanceView'] = 0;
        else
            $respon['attendanceView'] = 1;

        $respon['NetweightVal'] = $arr[0]['NetweightVal'];
        $respon['OrderVal'] = $arr[0]['OrderVal'];
        $respon['VisitDist'] = $arr[0]['VisitDist'];
        $respon['template'] = $arr[0]['template'];
        $respon['AppTyp'] = 1;
        $respon['TBase'] = $arr[0]['TBase'];
        $respon['GeoChk'] = $arr[0]['GeoNeed'];
        $respon['ChmNeed'] = $arr[0]['ChmNeed'];
        $respon['StkNeed'] = $arr[0]['StkNeed'];
        $respon['CollectedAmount'] = $arr[0]['CollectedAmount'];
        $respon['jointwork'] = $arr[0]['jointwork'];
        $respon['UNLNeed'] = $arr[0]['UNLNeed'];
        $respon['DPNeed'] = $arr[0]['DPNeed'];
        $respon['DINeed'] = $arr[0]['DINeed'];
        $respon['CPNeed'] = $arr[0]['CPNeed'];
        $respon['CINeed'] = $arr[0]['CINeed'];
        $respon['SPNeed'] = $arr[0]['SPNeed'];
        $respon['SINeed'] = $arr[0]['SINeed'];
        $respon['NPNeed'] = $arr[0]['NPNeed'];
        $respon['NINeed'] = $arr[0]['NINeed'];
        $respon['DrCap'] = $arr[0]['DrCap'];
        $respon['ChmCap'] = $arr[0]['ChmCap'];
        $respon['StkCap'] = $arr[0]['StkCap'];
        $respon['NLCap'] = $arr[0]['NLCap'];
        $respon['DrSmpQ'] = $arr[0]['DrSmpQ'];
        $respon['DRxCap'] = $arr[0]['DrRxQCap'];
        $respon['DSmpCap'] = $arr[0]['DrSmpQCap'];
        $respon['CQCap'] = $arr[0]['ChmQCap'];
        $respon['SQCap'] = $arr[0]['StkQCap'];
        $respon['NRxCap'] = $arr[0]['NLRxQCap'];
        $respon['NSmpCap'] = $arr[0]['NLSmpQCap'];
        $respon['SFStat'] = $arr[0]['SFStat'];
        $respon['days'] = $arr[0]['days'];
        $respon['State_Code'] = $arr[0]['State_Code'];
        $respon['CusOrder'] = $arr[0]['CusOrder'];
        $respon['SF_type'] = $arr[0]['SF_type'];
        $respon['SFTPDate'] = $arr[0]['sf_TP_Active_Dt'];
        $respon['closing'] = $arr[0]['closing'];
        $respon['recv'] = $arr[0]['recv'];
  $respon['Selfie'] = $arr[0]['Selfie'];
        return outputJSON($respon);
    } else {
        $respon['success'] = false;
        $respon['msg'] = "Check User and Password";
        return outputJSON($respon);
    }
}
function cancelPrimaryOrder(){
	$data = json_decode($_POST['data'], true);
	$OrderID=$data["OrderID"];
	$StkCode=$data["Stkcode"];
	$ADt=date("Y-m-d H:m:s");
	$sql="{call CancelPrimaryOrder(?,?,?)}";
	$params1 = array(
						array($OrderID, SQLSRV_PARAM_IN),
						array($StkCode, SQLSRV_PARAM_IN),
						array($ADt, SQLSRV_PARAM_IN)
					);
	$res["success"]=performQueryWP($sql, $params1);
	$res["Msg"]="Submitted Successfully";
	return $res;
}
function cancelSecOrder(){
	$data = json_decode($_POST['data'], true);
	$OrderID=$data["OrderID"];
	$Retcode=$data["Retcode"];
	$stkcode=$data["SfCode"];					  
	$ADt=date("Y-m-d H:m:s");
	$sql="{call CancelSecondaryOrder(?,?,?,?)}";
	$params1 = array(
						array($OrderID, SQLSRV_PARAM_IN),
						array($Retcode, SQLSRV_PARAM_IN),
						array($ADt, SQLSRV_PARAM_IN),
						array($stkcode, SQLSRV_PARAM_IN)									  
					);
	$res["success"]=performQueryWP($sql, $params1);
	$res["Msg"]="Submitted Successfully";
	return $res;
}
function updateDispatch(){
	$data = json_decode($_POST['data'], true);
	$OrderID=$data["OrderID"];
	$Retcode=$data["Retcode"];
	$sql="{call UpdateSecondaryDispatch(?,?)}";
	$params1 = array(
						array($OrderID, SQLSRV_PARAM_IN),
						array($Retcode, SQLSRV_PARAM_IN)
					);
	$res["success"]=performQueryWP($sql, $params1);
	$res["Msg"]="Submitted Successfully";
	return $res;
}
function svLogisticsEditOrder(){
	$data = json_decode($_POST['data'], true);
	$sfcode = $_GET['sfCode'];
	$prod = $data;
	$OrderID="";
	$smsg=false;
	if(count($prod)>0){
		$OrderID = (string) $prod[0]["OrderID"];
	}
	$pxml="<ROOT>";
	for($i=0;$i<count($prod);$i++){
		if($prod[$i]["newCQty"]>0){
			$pxml = $pxml . "<Prod slno=\"".$prod[$i]["Slno"]."\" PCode=\"".$prod[$i]["PCode"]."\" PName=\"".$prod[$i]["Product_Name"]."\" ocqty=\"".$prod[$i]["OldCQty"]."\" ocval=\"".$prod[$i]["Oldvalue"]."\" NCQty=\"".$prod[$i]["newCQty"]."\" NVal=\"".$prod[$i]["Newvalue"]."\" Rate=\"".$prod[$i]["Rate"]."\" clbl=\"".$prod[$i]["Cl_bal"]."\" Unit=\"".$prod[$i]["Unit"]."\"  postponed=\"".$prod[$i]["postponed"]."\"  />";
		}
	}
	$pxml= $pxml . "</ROOT>";
	$sql="{call editPriOrderLG(?,?,?)}";
	$params1 = array(
						array($sfcode, SQLSRV_PARAM_IN),
						array($OrderID, SQLSRV_PARAM_IN),
						array($pxml, SQLSRV_PARAM_IN)
					);
	$smsg=performQueryWP($sql, $params1);
    if($smsg==true){
		$res["success"]=true;
        $res["msg"]="Dispatched Item Successfully";
    }
    else{
		$res["success"]=false;
        $res["msg"]="Dispatch Failed";
    }
	return $res;
}
function PrimaryPaymentSave(){
	$data = json_decode($_POST['data'], true);
	$OrderID = (string) $data["OrderID"];
	$StockistCode = (string) $data["StockistCode"];
	$divisionCode = (string) $data["divisionCode"];
	$PaymentMode = (string) $data["PaymentMode"];
	$PaymentTypeName = (string) $data["PaymentTypeName"];
	$PaymentTypeCode = (string) $data["PaymentTypeCode"];
	$Amount =(string) $data["Amount"];
	$Attachement =(string) $data["Attachement"];
	$UTRNumber =(string) $data["UTRNumber"];
	$PaymentID =(string) $data["PaymentID"];
	$RazorOrderID =(string) $data["RazorOrderID"];
	$SignatureID =(string) $data["SignatureID"];
// 	$chequeDate =(string) $data["cheque_date"];
//     $chequeAmt =(string) $data["cheque_amount"];

											 
	$sql="{call savePrimaryOrderPayment(?,?,?,?,?,?,?,?,?,?,?,?)}";
	$params1 = array(
						array($divisionCode, SQLSRV_PARAM_IN),
						array($OrderID, SQLSRV_PARAM_IN),
						array($StockistCode, SQLSRV_PARAM_IN),
						array($PaymentMode, SQLSRV_PARAM_IN),
						array($PaymentTypeName, SQLSRV_PARAM_IN),
						array($PaymentTypeCode, SQLSRV_PARAM_IN),
						array($Amount, SQLSRV_PARAM_IN),
						array($Attachement, SQLSRV_PARAM_IN),
						array($UTRNumber, SQLSRV_PARAM_IN),
						array($RazorOrderID, SQLSRV_PARAM_IN),
						array($PaymentID, SQLSRV_PARAM_IN),
						array($SignatureID, SQLSRV_PARAM_IN)
							/* ,array($chequeAmt, SQLSRV_PARAM_IN),
                                                     array($chequeDate, SQLSRV_PARAM_IN) */

					);
	$res["success"]=performQueryWP($sql, $params1);
	$res["Msg"]="Submitted Successfully";
	return $res;
}

function DispatchPaymentSave(){
	$data = json_decode($_POST['data'], true);
	$OrderID = (string) $data["OrderID"];
	$StockistCode = (string) $data["Stkcode"];
	$divisionCode = (string) $data["divisionCode"];
	$PaymentTypeName = (string) $data["PaymentTypeName"];
	$PaymentTypeCode = (string) $data["PaymentTypeCode"];
	$Amount =(string) $data["Amount"];
	$Attachement =(string) $data["Attachement"];
	$UTRNumber =(string) $data["UTRNumber"];


	$sql="{call savePrimaryOrderPayment(?,?,?,?,?,?,?,?,?,?,?,?)}";
	$params1 = array(
						array($divisionCode, SQLSRV_PARAM_IN),
						array($OrderID, SQLSRV_PARAM_IN),
						array($StockistCode, SQLSRV_PARAM_IN),
						array($PaymentTypeName, SQLSRV_PARAM_IN),
						array($PaymentTypeCode, SQLSRV_PARAM_IN),
						array($Amount, SQLSRV_PARAM_IN),
						array($Attachement, SQLSRV_PARAM_IN),
						array($UTRNumber, SQLSRV_PARAM_IN)

										 
					);
	$res["success"]=performQueryWP($sql, $params1);
	$res["Msg"]="Submitted Successfully";
	return $res;
}



function PrimaryPaymentImgUpload(){
	global $data;
	global $URL_BASE;
	
	$target_dir = "..\photos\PrimaryOrderPayment/";
	$target_file_name = $target_dir .basename($_FILES["file"]["name"]);
	$target_file = basename($_FILES["file"]["name"]);
	$response = array();
	$upload_url;
	if (isset($_FILES["file"])) 
	{
		if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file_name)) 
		{
			$success = true;
			$upload_url = $target_file;
			$message = "Image Uploaded";
		}
		else 
		{
			$success = false;
			$message = "Error while uploading";
		}
	}
	else 
	{
		$message =  "No File Found for Upload";
		$success = false;
	}
	$response["success"] = $success;
	$response["message"] = $message;
	$response["url"] = $upload_url;
	return $response;
}
function svDMSVisit(){
	$data = json_decode($_POST['data'], true);
	$Retcode=$data["Retcode"];
	$Retname=$data["Retname"];
	$StkCode=$data["Stkcode"];
	$Stkname=$data["Stkname"];
	$Divcode=$data["Divcode"];	
	$sfCode=$data['sfCode'];	

    $divs = explode(",", $Divcode . ",");
    $Owndiv = (string) $divs[0];
	$Remark=$data["Remark"];
	$sql="select tc.Territory_Code,tc.Territory_Name from Mas_ListedDr dr inner join Mas_Territory_Creation tc on tc.Territory_Code=dr.Territory_Code where ListedDrCode='".$Retcode."'";
	$arr=performQuery($sql);
	$RouteCode='';
	$RouteName='';
	$Dt=date("Y-m-d");
	$ADt=date("Y-m-d H:m:s");
	if(count($arr)>0){
		$RtCode=$arr[0]["Territory_Code"];
		$RouteName=$arr[0]["Territory_Name"];
	}
	$sql="exec svDCRMain_App_1 '".$sfCode."','".$Dt."',61,'".$RtCode."',".$Owndiv.",'','',''";
	performQuery($sql);
	$sql="select Trans_SlNo from DCRMain_Trans where sf_code='".$sfCode."' and convert(date,Activity_Date)=convert(date,'".$Dt."')";
	$transsl=performQuery($sql);
	$mtranssl=$transsl[0]["Trans_SlNo"];
	$sql="{call svDCRLstDet_App(?,0,?,1,?,?,?,0,'admin','','','','','','','','','',?,?,?,0,?,'','','admin','NA','0','0',?,?,'','','')}";
	$params1 = array(
						array($mtranssl, SQLSRV_PARAM_IN),
						array($sfCode, SQLSRV_PARAM_IN),
						array($Retcode, SQLSRV_PARAM_IN),
						array($Retname, SQLSRV_PARAM_IN),
						array($ADt, SQLSRV_PARAM_IN),
						array($RtCode, SQLSRV_PARAM_IN),
						array($Remark, SQLSRV_PARAM_IN),
						array($Owndiv, SQLSRV_PARAM_IN),
						array($ADt, SQLSRV_PARAM_IN),
						array($StkCode, SQLSRV_PARAM_IN),
						array($StkName, SQLSRV_PARAM_IN)
					);
	$res["success"]=performQueryWP($sql, $params1);
	$res["Msg"]="Submitted Successfully";
	return $res;
}
function svDmsSecondaryOrder(){
	$data = json_decode($_POST['data'], true);	
	$prod = $data[0]['Activity_Stk_POB_Report'];
	$head = $data[1]['Json_Head'];
    $temp = $data[2]['Json_proceed'];
	$Retcode=$temp["Retcode"];
	$Retname=$temp["Retname"];
	$Remark=$temp["Remark"];
	$StkCode=$temp["Stkcode"];
	$Stkname=$temp["Stkname"];
	$Divcode=$temp["Divcode"];	
	$orderNo=$temp["orderNo"];
	$sfCode=$temp['sfCode'];	

	$PhoneOrderTypes=$temp["PhoneOrderTypes"];
	$existingOrder=$temp["isExistingOrder"];
	$OrderDate=$head[0]["order_date"];
	$OrderVal=$head[0]["order_value"];
	$Subtot=$head[0]["sub_tot"];
	$Distot=$head[0]["dis_tot"];
	$Taxtot=$head[0]["tax_tot"];
    $divs = explode(",", $Divcode . ",");
    $Owndiv = (string) $divs[0];
	$sql="select Territory_Code from Mas_ListedDr where ListedDrCode='".$Retcode."'";
	$arr=performQuery($sql);
	$RtCode='';
	$Dt=date("Y-m-d");
	if(count($arr)>0){
		$RtCode=$arr[0]["Territory_Code"];
	}
	$sql="select Trans_SlNo from DCRMain_Trans where sf_code='".$sfCode."' and convert(date,Activity_Date)=convert(date,'".$Dt."')";
	$transsl=performQuery($sql);
	$res["Trans_SlNo"]=$sql;
	$mtranssl=$transsl[0]["Trans_SlNo"];
	$sql="select Trans_Detail_Slno from DCRDetail_Lst_Trans where Trans_SlNo='".$mtranssl."' and Trans_Detail_Info_Code='".$Retcode."'";
	$dtranssl=performQuery($sql);
	$dcrcode=$dtranssl[0]["Trans_Detail_Slno"];
	if (count($prod)>0){
		$PPXML = "<ROOT>";
		for ($j = 0; $j < count($prod); $j++) {
			$pqty=(intval($prod[$j]["Qty"])*intval($prod[$j]["Con_Fac"]));
			$prod[$j]["Productname"]=str_replace("&","and",$prod[$j]["Productname"]);
			if($prod[$j]["tax_value"]==null ||$prod[$j]["tax_value"]=="")
			{
				$prod[$j]["tax_value"]=0;
			}
			if($prod[$j]["dis_value"]==null || $prod[$j]["dis_value"]=="")
			{
				$prod[$j]["dis_value"]=0;
			}
			if($prod[$j]["dis"]==null || $prod[$j]["dis"]=="")
			{
				$prod[$j]["dis"]=0;
			}
			$PPXML = $PPXML . "<Prod PCode=\"".$prod[$j]["ProductCode"]."\" PName=\"".$prod[$j]["Productname"]."\" Qty=\"".$pqty."\" Val=\"".$prod[$j]["Val"]."\" Rate=\"".$prod[$j]["ProductRate"]."\" FQty=\"".$prod[$j]["free"]."\" Dval=\"".$prod[$j]["dis_value"]."\" DAmt=\"".$prod[$j]["dis"]."\" Md=\"free\" Mfg=\"\" Cl=\"\" Offer_ProductCd=\"".$prod[$j]["Off_Pro_code"]."\" Offer_ProductNm=\"".$prod[$j]["Off_Pro_name"]."\" of_Pro_Unit=\"".$prod[$j]["Off_Pro_Unit"]."\" Unit=\"".$prod[$j]["Productunit"]."\" tax=\"".$prod[$j]["tax_value"]."\" con_fac=\"".$prod[$j]["Con_Fac"]."\" Umo_Code=\"".$prod[$j]["UOM"]."\" Sl_No=\"".$j."\"  Tax_value=\"".$prod[$j]["tax_value"]."\" Qty_C=\"".$prod[$j]["Qty"]."\"  />";
			$SPProds = $SPProds . $prod[$j]["ProductCode"] . "~" . $pqty . "#";
			$nSPProds = $nSPProds . $prod[$j]["Productname"] . "~" . $pqty . "#";
		}
		$PPXML = $PPXML . "</ROOT>";
	}
	if($existingOrder=="true"){
		$sql="exec svAppSecOrder '".$sfCode."','".$Retcode."','".$StkCode."','".$RtCode."',0,'".$OrderDate."',".$OrderVal.",0,'0','',0,0,'','".$dcrcode."',".$Owndiv.",'".$PPXML."','".$PhoneOrderTypes."','','".$Subtot."','".$Distot."','".$Taxtot."',1,'".$orderNo."'";
	}
	else{
		$sql="exec svAppSecOrder '".$sfCode."','".$Retcode."','".$StkCode."','".$RtCode."',0,'".$OrderDate."',".$OrderVal.",0,'0','".$Remark."',0,0,'','".$dcrcode."',".$Owndiv.",'".$PPXML."','".$PhoneOrderTypes."','','".$Subtot."','".$Distot."','".$Taxtot."',0,''";
	}
	$suctxt=performQuery($sql);
	if(is_null($suctxt)==1){
  
	$res["success"]=true;
	$res["Msg"]="Submitted Successfully";
	}
	else{
		$res["success"]=false;
		$res["Msg"]="Failed";
	}
	$res["secorder"]=$dcrcode;
						  
	$sql="update DCRDetail_Lst_Trans set POB_Value=(POB_Value+cast('".$OrderVal."' as float)),Additional_Prod_Code=Additional_Prod_Code+'".$SPProds."',Additional_Prod_Dtls=Additional_Prod_Dtls+'".$nSPProds."' where Trans_SlNo='".$mtranssl."' and Trans_Detail_Slno='".$dcrcode."'";
	performQuery($sql);
	return $res;
}
function stkSecOrderProd(){	
		$sfCode = $_GET['sfCode'];
		$div = $_GET['divisionCode'];
		$query = "exec GetStockistProdBrand_App '" . $sfCode . "'";
		$results["Brand"] = performQuery($query);
		$sql = "exec getSecCustomerwiseProdts '" . $sfCode . "'";
		$Product=performQuery($sql);
		$DT=date("Y-m-d");
		$sql = "exec get_secondary_scheme '" . $sfCode . "','".$div."','".$DT."'";
		$Scheme=performQuery($sql);
		for($i=0;$i<count($Product);$i++){
			$pid=$Product[$i]['id'];
			$Qry = "select  UOM_Id id,UOM_Nm name,CnvQty ConQty  from vwMas_MUnitDets where PCode='".$pid."' and Division_Code='" . $div . "'";
			$Uoms=performQuery($Qry);
			$Product[$i]["UOMList"]=$Uoms;
			$new_array = array_values(array_filter($Scheme, function($obj)  use ($pid){
							if(($obj["Product_Code"] === $pid)) return true;
						}));
			if(count($new_array)>0){
				$Product[$i]['SchemeArr']=$new_array;
			}
			else{
				$new_array[]=array('Product_Code' => '','Product_Name' => '','Scheme' => '','Free' => '','Discount' => '','Package' => '','Division_Code' => '','Effective_From' => '',
								'State_Code' => '','ID' => '','Stockist_Code' => '','Effective_To' => '','Scheme_Name' => '','Against' => '','Offer_Product' => '','actFlg' => '',
								'Offer_Product_Name' => '','Scheme_Unit' => '','Free_Unit' => '','offer_product_unit' => '','Sale_Erp_Code' => '','Discount_Type' => '');
				$Product[$i]['SchemeArr']=$new_array;
			}
		}
		$results["Products"]=$Product;
		/*for ($i = 0;$i < count($brdarr);$i++) {
			$cat_Id=$brdarr[$i]['id'];
			$Prod=FilterArry($Product,"Product_Brd_Code",$cat_Id);
			$brdarr[$i]['Product']=$Prod; 
			$brdarr[$i]['SaleUnit']=$Prod[0]["Sale_Unit"];
		}*/
		return $results;
}
function getProducts($desig) {
    $sfCode = $_GET['sfCode'];
    $DivisionCode = $_GET['divisionCode'];
    if ($desig == "superstock" || $desig == "stockist" ){
	$div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
if($desig == "superstock")
   $query="select Sub_Division_Code from Supplier_Master  where SL_MaxNo='" . $sfCode . "'";
else
$query="select subdivision_code from mas_stockist where Stockist_Code='" . $sfCode . "' ";

   $subdivision=performQuery($query);
   $query = "exec getAppProdStockist    '".$Owndiv."','".$subdivision[0]['Sub_Division_Code']."'   "; 


		}
    else{
        $query = "exec getAppProd '" . $sfCode . "'"; //,'".$DivisionCode."'";
     
	}
 return performQuery($query);
}


function getAPPSetups() {
    $rqSF = $_GET['rSF'];
    $userType = $_GET['userType'];
    $query = "exec getAPPSetups '" . $rqSF . "'";
    return performQuery($query);
}
function getTrackSetUP(){
$data = json_decode($_POST['data'], true);
$sfCode = (string) $data['SF'];
$div = (string) $data['div'];
$query = "exec getTrackSetup '".$sfCode."','".$div."'";
return performQuery($query);
}
function getSchemeDets() {
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    
    $query = "exec getProdScheme '" . $Owndiv . "'";
    return performQuery($query);
}
function getBrandLittersWiseProd($desig){
    $date = $_GET['rptDt'];
    $sfCode = $_GET['sfCode'];
 $rptSF = $_GET['rptSF'];
    if ($desig == 'stockist')
        $stockistCode = $sfCode;
    else
        $stockistCode = $_GET['stockistCode'];
    if ($desig == 'mr')
        $str = "sf_code='$sfCode'";
    else
        $str = "Stockist_Code='$stockistCode'";
if($_GET['stockistCode']==-1)
 $str = "sf_code='$sfCode'";

if($desig == 'mgr' && $_GET['stockistCode']==-1)
$str= "sf_code='$rptSF'";

    $query = "select BrandName,sum(quantity) OQty,sum(ltrs) OVal from vwBrandWiseOrder where orderDate='$date' and $str group by BrandName";
    $daywise = performQuery($query);

    $query = "select BrandName,sum(quantity) TOQty,sum(ltrs) TOVal from vwBrandWiseOrder where MONTH(orderDate) = MONTH('$date') and YEAR(orderDate) = YEAR('$date') and $str group by BrandName";
    $monthwise = performQuery($query);
    $det = array();
    for ($i = 0; $i < count($monthwise); $i++) {
        $OQty = 0;
        $OVal = 0;
        for ($j = 0; $j < count($daywise); $j++) {
            if ($monthwise[$i]['BrandName'] == $daywise[$j]['BrandName']) {
                $OQty = $daywise[$j]['OQty'];
                $OVal = $daywise[$j]['OVal'];
                array_splice($daywise, $j, 1);
            }
        }
        $det[$i]['Brand'] = $monthwise[$i]['BrandName'];
        $det[$i]['OQty'] = $OQty;
        $det[$i]['OVal'] = $OVal;
        $det[$i]['TOQty'] = $monthwise[$i]['TOQty'];
        $det[$i]['TOVal'] = $monthwise[$i]['TOVal'];
    }
    return $det;
}

function getBrandWiseProd($desig) {
    $date = $_GET['rptDt'];
    $sfCode = $_GET['sfCode'];
    if ($desig == 'stockist')
        $stockistCode = $sfCode;
    else
        $stockistCode = $_GET['stockistCode'];
    if ($desig == 'mr')
        $str = "sf_code='$sfCode'";
    else
        $str = "Stockist_Code='$stockistCode'";
if($_GET['stockistCode']==-1)
 $str = "sf_code='$sfCode'";

if($desig == 'mgr' && $_GET['stockistCode']==-1)
$str= "sf_code='$rptSF'";

if($desig == 'mgr' && $_GET['stockistCode']==-1){


    $query = "select BrandName,quantity OQty,orderValue OVal from vwBrandWiseOrder where orderDate='$date' and sf_code='$sfCode'";

    $queryy = "select BrandName,sum(quantity) TOQty,sum(orderValue) TOVal from vwBrandWiseOrder where MONTH(orderDate) = MONTH('$date') and YEAR(orderDate) = YEAR('$date') and sf_code='$sfCode' group by BrandName";
   
}
else{

 $query = "select BrandName,quantity OQty,orderValue OVal from vwBrandWiseOrder where orderDate='$date' and sf_code='$sfCode' and Stockist_Code='$stockistCode'";

 $queryy = "select BrandName,sum(quantity) TOQty,sum(orderValue) TOVal from vwBrandWiseOrder where MONTH(orderDate) = MONTH('$date') and YEAR(orderDate) = YEAR('$date') and sf_code='$sfCode' and Stockist_Code='$stockistCode' group by BrandName";
}


    $daywise = performQuery($query);

    
 $monthwise = performQuery($queryy);


    $det = array();
    for ($i = 0; $i < count($monthwise); $i++) {
        $OQty = 0;
        $OVal = 0;
        for ($j = 0; $j < count($daywise); $j++) {
            if ($monthwise[$i]['BrandName'] == $daywise[$j]['BrandName']) {
                $OQty = $daywise[$j]['OQty'];
                $OVal = $daywise[$j]['OVal'];
                array_splice($daywise, $j, 1);
            }
        }
        $det[$i]['Brand'] = $monthwise[$i]['BrandName'];
        $det[$i]['OQty'] = $OQty;
        $det[$i]['OVal'] = $OVal;
        $det[$i]['TOQty'] = $monthwise[$i]['TOQty'];
        $det[$i]['TOVal'] = $monthwise[$i]['TOVal'];
    }
    return $det;
}

function getSubordinateMgr() {
    $sfCode = $_GET['rSF'];
    $param = array($sfCode);
    $query = "exec getHyrSF_APP '" . $sfCode . "'";
    return performQuery($query);
}

function getSubordinate() {
    $sfCode = $_GET['rSF'];
    $param = array($sfCode);
    $query = "exec getBaseLvlSFs_APP '" . $sfCode . "'";
    return performQuery($query);
}

function getJointWork() {
    $sfCode = $_GET['sfCode'];
    $rqSF = $_GET['rSF'];
    $query = "exec getJointWork_App '" . $sfCode . "','" . $rqSF . "'";
    return performQuery($query);
}

function GetDailyInv() {
    $sfCode = $_GET['rptSF'];
    $Md = $_GET['Mod'];
    if($Md==1 || $Md==3  ){
        if($Md==3) $Md=0;
	
        $query = "exec getDailyBeginInv '" . $sfCode . "','".$Md."'";
    }
    else
        $query = "exec getDailyEndInv '" . $sfCode . "'";
    return performQuery($query);
}

function getDtTPview() {
    $data = json_decode($_POST['data'], true);
    $sfCode = (string) $data['sfCode'];
    $t = strtotime(str_replace("Z", "", str_replace("T", " ", $data['tpDate'])));
    $TpDt = date('Y-m-d 00:00:00', $t);
    $Qry = "exec spTPViewDtws '$sfCode','$TpDt'";
    $respon = performQuery($Qry);
    return outputJSON($respon);
}

function getTPview() {
    $data = json_decode($_POST['data'], true);
    $sfCode = (string) $data['sfCode'];
    $t = strtotime(str_replace("Z", "", str_replace("T", " ", $data['mnthYr'])));
    $TpDt = date('Y-m-d 00:00:00', $t);
    $Qry = "SELECT convert(varchar,Tour_Date,103) [date],Worktype_Name_B wtype,replace(isnull(Territory_Code1,''),'0','') towns,replace(isnull(Tour_Schedule1,''),'0','') townsId,replace(isnull(Tour_Schedule1,''),'0','') PlnNo,replace(isnull(Worked_With_SF_Name,''),'0','') distributor,Confirmed,SF_Code sf_code from vwTrans_TP_View T where sf_code='$sfCode' and Tour_Month=month('$TpDt') and Tour_year=year('$TpDt') order by Tour_Date";
    $respon = performQuery($Qry);
    return outputJSON($respon);
}

function getMonthSummary() {
    $sfCode = $_GET['rptSF'];
    $dyDt = $_GET['rptDt'];
    $query = "exec getMonthSummaryApp '" . $sfCode . "','" . $dyDt . "'";
    return performQuery($query);
}
function getDaycallSummary() {
    $sfCode = $_GET['HQ'];
    $dyDt = $_GET['rptDt'];
    
   $div = $_GET['divisionCode'];
   $divisionCode = explode(",", $div);
    $date = date('Y-m-d');
    $query = "exec GET_Date_Call_Rep '" . $divisionCode[0] . "','" . $sfCode . "' ,'".$date."'";
    return  performQuery($query);
}
function getDayPlan(){
    $sfCode = $_GET['rptSF'];
    $dyDt = $_GET['rptDt'];
    $query = "exec getDayPlan '" . $sfCode . "','" . $dyDt . "'";

    return performQuery($query);
}

function getDailySalesSummry() {
    $sfCode = $_GET['sfCode'];
    $dyDt = $_GET['rptDt'];

    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];

    $query = "exec DaySaleSummary  '" . $sfCode . "',".$Owndiv.",'"  .  date('m', strtotime($dyDt )) . "','"  .  date('Y', strtotime($dyDt )) . "'";
    return performQuery($query);
}

function getDayReport() {
    $sfCode = $_GET['sfCode'];
    $dyDt = $_GET['rptDt'];
    $query = "exec getDayReportApp '" . $sfCode . "','" . $dyDt . "'";
    return performQuery($query);
}

function DayReport() {
    $sfCode = $_GET['sfCode'];
    $dyDt = $_GET['rptDt'];
 $today = date('Y-m-d 00:00:00');
    $query = "exec getDayReportApp '" . $sfCode . "','" . $dyDt . "'";
    $dayrep= performQuery($query);
    $query = "exec getDayBrandWiseReportApp '" . $sfCode . "','" . $dyDt . "'";
    $brndwise= performQuery($query);

    $query = "exec getDayDCR_TLSD_ReportApp '" . $sfCode . "','" . $dyDt . "'";
    $DCR_TLSD= performQuery($query);
    $query = "exec getDayDCR_LPC_ReportApp '" . $sfCode . "','" . $dyDt . "'";
    $DCR_LPC= performQuery($query);

    $query = "exec getDaySummaryReportApp '" . $sfCode . "','" . $dyDt . "'";
    $summary= performQuery($query);
    $query = "Select *  from Trans_Inshop_Activity where sfCode='" . $sfCode . "' and InsertDate='" . $dyDt . "' ";
    $InshopCount= performQuery($query);
    $query = "Select * from Trans_Product_Display where sfCode='" . $sfCode . "' and Current_Date_And_Time='" . $dyDt . "' ";
    $ProductCount= performQuery($query);
    
    $query = "Select * from Trans_Door_To_Door where sfCode='" . $sfCode . "' and Current_Date_And_Time='" . $dyDt . "' ";
    $Door_To_Door= performQuery($query);
    $query = "select Count(Trans_Detail_Info_Code) SuperStokit from vwActivity_SuperCSH_Detail D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime) and Trans_Detail_Info_Type=8";
    $temp = performQuery($query);
    $result=array();


    $result['dayrep']=$dayrep;
    $result['brndwise']=$brndwise;
    $result['DCR_TLSD']=$DCR_TLSD;
    $result['DCR_LPC']=$DCR_LPC;
    $result['summary']=$summary;
    $result['Inshop']=$InshopCount;
    $result['ProductDiisplay']=$ProductCount;
    $result['DoorToDoor']=$Door_To_Door;
     $result['SS']=$temp[0]['SuperStokit'];
    return $result;
}


function  ProductRemarksRpt(){

    $sfCode = $_GET['SF_Code_Type'];
    $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    
    $query = "exec SP_ProductRemarksView '" . $sfCode . "','" . $Owndiv . "','".$fromdate."'";


    $result['RemarksReport']=performQuery($query);
    return $result;


}

function  ProductRCPARpt(){

    $sfCode = $_GET['SF_Code_Type'];
    $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    
    $query = "exec SP_ProductRCPAView '" . $sfCode . "','" . $Owndiv . "','".$fromdate."'";


    $result['RemarksReport']=performQuery($query);
    return $result;


}


function  ProductRemarksView(){

    $sfCode = $_GET['sfCode'];
    $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Pcode = $_GET['Pcode'];
$Remarks_Id = $_GET['Remark_Id'];
    $fromdate = $_GET['fromdate'];
    
    $query = "exec SP_View_ProductRemarksView '" . $sfCode . "','" . $Owndiv . "','".$fromdate."' ,'" . $Pcode . "','".$Remarks_Id."'  ";

    $result['GetRemarksView']=performQuery($query);
    return $result;


}

function  ProductRCPAView(){

    $sfCode = $_GET['sfCode'];
    $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Pcode = $_GET['Pcode'];
$Remarks_Id = $_GET['Remark_Id'];
    $fromdate = $_GET['fromdate'];
    
    $query = "exec SP_View_ProductRCPAView '" . $sfCode . "','" . $Owndiv . "','".$fromdate."' ,'" . $Pcode . "','".$Remarks_Id."'  ";

    $result['GetRCPAView']=performQuery($query);
    return $result;


}



function  PrimarySalesFpWise(){

$sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
    $Mr_SFCode = $_GET['Mr_SFCode'];
    $Stockist_Code=$_GET['Stockist_Code'];

if($Type==2 ){
$fromdate=date('m', strtotime($_GET['fromdate'] ));
$todate=date('Y', strtotime($_GET['todate'] ));
}
if($Type==3 ){
$fromdate = $_GET['fromdate'];
}

    $query = "exec getPrimarySalesFPWise '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
//

$result['primaryfpwise']=performQuery($query);
return $result;


}
function  PrimarySalesStateWise(){

   $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
    $Mr_SFCode = $_GET['Mr_SFCode'];
    $Stockist_Code=$_GET['Stockist_Code'];
if($Type==2 ){
$fromdate=date('m', strtotime($_GET['fromdate'] ));
$todate=date('Y', strtotime($_GET['todate'] ));
}
if($Type==3 ){
$fromdate = $_GET['fromdate'];
}
    $query = "exec getPrimaryStateWise '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'";
    $result['primarystatewise']=performQuery($query);
	
	


    return $result;
}

function  PrimarytargetDistiWise(){
    $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
    $Mr_SFCode = $_GET['Mr_SFCode'];
    $Stockist_Code=$_GET['Stockist_Code'];

if($Type==2 ){
$fromdate=date('m', strtotime($_GET['fromdate'] ));
$todate=date('Y', strtotime($_GET['todate'] ));
}
  if($Type==3 ){
$fromdate=date(('Y-n-d'),  strtotime($fromdate));
$todate=date(('Y-n-d'),  strtotime($todate));
}  

    $query = "exec getPrimaryDistiWise '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
   
 $result['distributortarget']=performQuery($query);
    return $result;
    
}






function  OrderAnalysisView(){
    $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
    $Mr_SFCode = $_GET['Mr_SFCode'];
    $Stockist_Code=$_GET['Stockist_Code'];

if($Type==2 ){
$fromdate=date('m', strtotime($today ));
$todate=date('Y', strtotime($today ));
}
/* if($Type==3 ){
$fromdate=date('Y-m',  strtotime($fromdate));
$todate=date('Y-m',  strtotime($todate));
} */

    $query = "exec getOrderAnalysisView '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
   
 $result['OrderAnalysis']=performQuery($query);
    return $result;
    
}

function  AttendanceAnalysisView(){
    $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
    $Mr_SFCode = $_GET['Mr_SFCode'];
    $Stockist_Code=$_GET['Stockist_Code'];

if($Type==2 ){
$fromdate=date('m', strtotime($today ));
$todate=date('Y', strtotime($today ));
}
/* if($Type==3 ){
$fromdate=date('Y-m',  strtotime($fromdate));
$todate=date('Y-m',  strtotime($todate));
} */

    $query = "exec getAttendanceAnalysisView '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
   
 $result['OrderVisitAnalysis']=performQuery($query);
    return $result;
    
}



function  VisitAnalysisView(){
    $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
    $Mr_SFCode = $_GET['Mr_SFCode'];
    $Stockist_Code=$_GET['Stockist_Code'];

if($Type==2 ){
$fromdate=date('m', strtotime($today ));
$todate=date('Y', strtotime($today ));
}
/* if($Type==3 ){
$fromdate=date('Y-m',  strtotime($fromdate));
$todate=date('Y-m',  strtotime($todate));
} */

    $query = "exec getVisitAnalysisView '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
   
 $result['OrderVisitAnalysis']=performQuery($query);
    return $result;
    
}




function  getOrderFFView(){
	
    $sfCode = $_GET['FSf_Code'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
   

if($Type==2 ){
$fromdate=date('m', strtotime($today ));
$todate=date('Y', strtotime($today ));
}


    $query = "exec getOrderFFView '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
   
   
 
    return performQuery($query);
    
}



function  getVisitFFView(){
	
    $sfCode = $_GET['FSf_Code'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
   

if($Type==2 ){
$fromdate=date('m', strtotime($today ));
$todate=date('Y', strtotime($today ));
}


    $query = "exec getVisitFFView '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
   
   
 
    return performQuery($query);;
    
}


function  getVisitAttendanceView(){
	
    $sfCode = $_GET['FSf_Code'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
   

if($Type==2 ){
$fromdate=date('m', strtotime($today ));
$todate=date('Y', strtotime($today ));
}


    $query = "exec getAttendanceView '" . $sfCode . "','" . $Owndiv . "', '" .$Type. "','".$fromdate."','".$todate."'  ";
   
   
 
    return performQuery($query);;
    
}




function  getSalesforceTargetVsTotal(){
    $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
  
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
  


$fromdate=date('m', strtotime($fromdate));
$todate=date('Y', strtotime( $todate ));



    $query = "exec getSalesforceTargetVsTotal '" . $sfCode . "','" . $Owndiv . "','".$fromdate."','".$todate."'  ";
   
   
  
   
 $result['distributortarget']=performQuery($query);
    return $result;
    
}
function  GetvanLedger(){
    $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
  
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
  






    $query = "exec van_stock_ledger '" . $sfCode . "','" . $Owndiv . "','".$fromdate."','".$todate."','0'  ";
   
   
  
   
 $result['VanLedger']=performQuery($query);
    return $result;
    
}

function  GetvanLedgerView(){
    $sfCode = $_GET['Rsf_code'];
	
	
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
  
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
	
  
$query = "exec trans_van_stock_bal '" . $sfCode . "','" . $Owndiv . "','".$fromdate."','".$todate."','".$_GET['ProductCode']."'";


$result['TotalVanProduct']=performQuery($query);


    $query = "exec trans_van_stock '" . $sfCode . "','" . $Owndiv . "','".$fromdate."','".$todate."','".$_GET['ProductCode']."'";
   
   
  
   
 $result['CreditDebit']=performQuery($query);
    return $result;
    
}


function  PrimaryProductWise(){

   $sfCode = $_GET['sfCode'];
      $today = date('Y-m-d');
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $Type = $_GET['Type'];
    $fromdate = $_GET['fromdate'];
    $todate = $_GET['todate'];
    $Mr_SFCode = $_GET['Mr_SFCode'];
    $Stockist_Code=$_GET['Stockist_Code'];

if($Type==2 ){
$fromdate=date('m', strtotime($_GET['fromdate'] ));
$todate=date('Y', strtotime($_GET['todate'] ));
}
if($Type==3 ){
$fromdate = $_GET['fromdate'];
}


    $query = "exec getPrimaryproductwise '" . $sfCode . "','" . $Owndiv . "' , '" .$Type. "','".$fromdate."','".$todate."' ";
   
  $result['productwisesales']=performQuery($query);
    return $result;
}



function DayPragnancyReport() {
    $sfCode = $_GET['sfCode'];
    $dyDt = $_GET['rptDt'];
 $today = date('Y-m-d 00:00:00');
  
    
    $query = "select *  from vwActivity_Report where SF_Code='" .$sfCode . "' and   DATEDIFF(day,Activity_Date,'" . $dyDt . "')=0";
    $temp = performQuery($query);
    $result=array();



     $result['TodayPragnancy']=$temp[0];
    return $result;
}

function TpRouteNotify() {

  $sfCode = $_GET['sfCode'];
  $today = date('Y-m-d H:i:s');
    $divCode = $_GET['divisionCode'];
    $divisionCode = explode(",", $divCode);
	
	
  
    $sql = "SELECT Sf_Name,Reporting_To_SF,sf_type FROM Mas_Salesforce where Sf_Code='$sfCode'";

   $sfType = performQuery($sql);
   $sql = "SELECT  CONCAT('N',MAX(CONVERT(INT, RIGHT(NotifyID, LEN(NotifyID)-1)))+1) as id FROM Mas_AppNotifyList";
   $Maxid = performQuery($sql);
  $NtfyMsg=  $sfType[0]['Sf_Name'].'has changed route instad of Tour plan route';
   
   
   
   $sql = "insert into Mas_AppNotifyList(SF_Code,NotifyID,Subject,NtfyMsg,CrDt,DivCode,typ) values( '".$sfType[0]['Reporting_To_SF'] . "','" . $Maxid[0]['id'] . "','Wrong! Route','$NtfyMsg','".$today."'," . $divisionCode[0] . "," . $sfType[0]['sf_type'] . ")";
     performQuery($sql); 
	
$sql = "SELECT DeviceRegId FROM Access_Table where sf_code=(select Reporting_To_SF from mas_salesforce where Sf_Code='$sfCode')";
            $device = performQuery($sql);

            $reg_id = $device[0]['DeviceRegId'];
            if (!empty($reg_id)) {
                
                    
                send_gcm_notify($reg_id, $NtfyMsg,'Wrong! Route');
            }
				

}

function DayUpdatePragnancyReport() {
    $sfCode = $_GET['sfCode'];
    $OrderID = $_GET['OrderID'];
  $Confirm = $_GET['Confirm'];
    $Flag = $_GET['Flag'];
    

     $sql = "update Trans_Pragnancy__Head set Order_Flag=1 ,Message='".$Confirm."',Order_No='".$Flag."'  where sf_Code='" . $sfCode . "' and  OrderID='".$OrderID."' ";
   
$result = performQuery($sql);
    $result=array();



     $result['TodayPragnancy']=true;
    return $result;
}





function getsummary() {
    $sfCode = $_GET['sfCode'];
    $date=date('Y-m-d');
    $query = "select * from dcr_summary where sf_code='$sfCode' and cast(submission_date as date)='$date'";
    return performQuery($query);
}

function getDaySummary() {
    $sfCode = $_GET['sfCode'];
    $date=date('Y-m-d');

    $query = "select * from dcr_summary where sf_code='$sfCode' and cast(submission_date as date)='$date'";
    $dcrSummary=performQuery($query);
    $result=array();
    $result['dcrSummary']=$dcrSummary;
    if($dcrSummary[0]['editFlag']==0){
        $query="exec getDaySummaryDCR_TLSD_ReportApp '$sfCode'";
        $DCR_TLSD=performQuery($query);
        $query="exec getDaySummaryDCR_LPC_ReportApp '$sfCode'";
        $DCR_LPC=performQuery($query);
        $query="exec getDaySummaryBrandWiseReportApp '$sfCode'";
        $brandwise=performQuery($query);

        $result['DCR_TLSD']=$DCR_TLSD;
        $result['DCR_LPC']=$DCR_LPC;
        $result['brandwise']=$brandwise;
    }
    return $result;
}

function getDailyAlloww() {
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $query = "select ID,Allowance_Name Name from Mas_Allowance_Type where Type=1 and Division_Code=". $Owndiv ." and user_enter=1 and active_flag=0";
    return performQuery($query);
}
function getDailyAllow() {
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
	 $sfCode = $_GET['sfCode'];
	$date=date('Y-m-d');
    $query = "select ID,Allowance_Name Name from Mas_Allowance_Type where Type=1 and Division_Code=". $Owndiv ." and user_enter=1 and active_flag=0";
	$ExpenseWeb=performQuery($query);
	$query = "select * from Expense_Start_Activity where Sf_code='" . $sfCode . "'  and convert(date,Date_Time)='".$date."'";
	$TodayExpense=performQuery($query);
	
	 $result['ExpenseWeb']=$ExpenseWeb;
        $result['TodayExpense']=$TodayExpense;
    return $result;
}
function getPendingBills(){
    $sfCode = $_GET['rptSF'];
    $query = "exec getPendingBills '" . $sfCode . "'";
    return performQuery($query);

}

function getInvFlags(){
    $sfCode = $_GET['rSF'];
    $eDt = $_GET['edts'];
    $Flags=array();
    $query = "exec getInvFlags '" . $sfCode . "','" . $eDt . "'";
    $res=performQuery($query);
    $Flags["BInvFlg"]=$res[0]["BInvFlg"];
    $Flags["EInvFlg"]=$res[0]["EInvFlg"];
    $Flags["AInvFlg"]=$res[0]["AInvFlg"];
    return $Flags;
}

function getVstDets() {
    $ACd = $_GET['ACd'];
    $typ = $_GET['typ'];
    $query = "exec spGetVstDetApp '" . $ACd . "','" . $typ . "'";
    return performQuery($query);
}


function getVstPragnancyDets() {
    $ACd = $_GET['ACd'];
    $typ = $_GET['typ'];




       $query = "select  convert(varchar, convert(date,TP.Order_Date),5) as Activity_Date,TP.*,dr.ListedDr_Name as name  from Trans_Pragnancy__Head   TP inner join Mas_ListedDr dr  on  TP.Trans_Sl_No ='".$ACd."' and    Tp.Order_Flag is null and dr.ListedDrCode=Tp.Cust_Code";
    return performQuery($query);
}

function getVstPragnancyDetReport() {
    $ACd = $_GET['ACd'];
    $typ = $_GET['typ'];

       $query = "select  convert(varchar, convert(date,TP.Order_Date),5) as Activity_Date,TP.*,dr.ListedDr_Name as name  from Trans_Pragnancy__Head   TP inner join Mas_ListedDr dr  on  TP.Trans_Sl_No ='".$ACd."'  and    Tp.Order_Flag IS NOT NULL and    dr.ListedDrCode=Tp.Cust_Code";
    return performQuery($query);
}

function getVstPendingPragnancy() {
    
$sfCode = $_GET['sfCode'];
    $query = "select  TP.Order_Date as Activity_Date, TP.Remarks as remarks,TP.*,dr.ListedDr_Name as name  from Trans_Pragnancy__Head   TP inner join Mas_ListedDr dr  on      Tp.Order_Flag is null and dr.ListedDrCode=Tp.Cust_Code and  TP.Sf_Code='".$sfCode."' and convert(date,Order_Date)!='".date('Y-m-d')."'";
    


return performQuery($query);
}



function getVwOrderDet() {
    $dcrCode = $_GET['DCR_Code'];
    $query = "select * from vwOrderDetails where Dcr_Code='".$dcrCode."'";
    return performQuery($query);
}

function getCurrentStockDet() {
    if ($_GET['scode'] == "0")
        $SF = $_GET['sfCode'];
    else
        $SF = $_GET['scode'];

    $CLDT = $_GET['cldt'];

    if($CLDT=='') $CLDT=date('Y-m-d 00:00:00');
    $query = "exec getClStkVw '$SF','$CLDT'";
    $transHead = performQuery($query);
    return outputJSON($transHead);
}
function getCurrentSSStockDet() {
    if ($_GET['scode'] == "0")
        $SF = $_GET['sfCode'];
    else
        $SF = $_GET['scode'];

    $CLDT = $_GET['cldt'];

    if($CLDT=='') $CLDT=date('Y-m-d 00:00:00');
    $query = "exec getSSClStkVw '$SF','$CLDT'";
    $transHead = performQuery($query);
    return outputJSON($transHead);
}
function getPreCallDet() {
    $SF = $_GET['sfCode'];
    $MSL = $_GET['Msl_No'];

    $result = array();
   $query = "select SLVNo SVL,Doc_Class_ShortName DrCat,Doc_Spec_ShortName DrSpl,isnull(stuff((select ', '+Doc_SubCatName from Mas_Doc_SubCategory S where CHARINDEX(cast(Doc_SubCatCode as varchar),D.Doc_SubCatCode)>0 for XML Path('')),1,2,''),'') DrCamp,isnull(stuff((select ', '+Product_Detail_Name from Map_LstDrs_Product M  inner join Mas_Product_Detail P on M.Product_Code=P.Product_Detail_Code and P.Division_Code=M.Division_Code where Listeddr_Code=D.ListedDrCode for XML Path('')),1,2,''),'') DrProd,Case WHEN ListedDr_Address1!=''  and ListedDr_Address1 IS NOT NULL THEN ListedDr_Address1 WHEN ListedDr_Address2!=''  and ListedDr_Address2 IS NOT NULL THEN ListedDr_Address2 ELSE ListedDr_Address3 END as Address  from mas_listeddr  D where ListedDrCode='" . $MSL . "'";
  $as = performQuery($query);


   $query3 = "select top 1 Order_Value from Trans_Order_Head where Cust_Code = '" . $MSL . "' order by order_date desc";
  $loa = performQuery($query3);


   $query5 = "SELECT isNull(Slan_Name,'-') Slan_Name FROM Mas_ListedDr WHERE ListedDrCode='". $MSL ."'";
  $scheme = performQuery($query5);


   $query4 = "select top 1 convert(varchar,dmt.Activity_Date,103) OrderDate  from DCRMain_Trans dmt inner join DCRDetail_Lst_Trans dlt 
on dmt.Trans_SlNo=dlt.Trans_SlNo and dlt.Trans_Detail_Info_Code = '". $MSL ."' 
WHERE dmt.Sf_Code = '". $SF ."' order by dmt.Activity_Date desc";
  $lvd = performQuery($query4);
  
    if (count($as) > 0) {
        $result['SVL'] = (string) $as[0]['SVL'];
        $result['DrCat'] = (string) $as[0]['DrCat'];
        $result['DrSpl'] = (string) $as[0]['DrSpl'];
        $result['DrCamp'] = (string) $as[0]['DrCamp'];
        $result['DrProd'] = (string) $as[0]['DrProd'];
        $result['Address'] = (string) $as[0]['Address'];
		$result['Cat_Img']="";			
		$result['last_visit_date']=(string) $lvd[0]['OrderDate'];
		$result['Slan_Name']=(string) $scheme[0]['Slan_Name'];
		$result['LastOrderAmt']=(string) $loa[0]['Order_Value'];
		
         $dateYeabbr=date('m');
$now = new DateTime();
$year = $now->format("Y");
$Month=$now->format("M");
        $result['success'] = true;
        $query = "select SUM(Order_value)  sumoforder,stockist_code from vwTransOrderHead where id<7 and Cust_Code=$MSL group by stockist_code order by stockist_code"; // and Sf_Code='$SF'
        $transHead = performQuery($query);

        $query = "select Stockist_Code,Last_Order_Amt,Out_standing_Amt from Order_Collection_Details where Cust_Code=$MSL order by Stockist_Code";  // and Sf_Code='$SF' 
        $collectionOrder = performQuery($query);
        $query = "select * from vwOffTakeQtys where cust_Code='".$MSL."' ";
        $OpeningStock = performQuery($query);
        $query = "select * from Trans_MOQ where Cust_Code='".$MSL."' ";


        $MOQ = performQuery($query);
        
        $queryy="select SUM (Order_Value) AS MorderSum from Trans_Order_Head  where Cust_Code='".$MSL."' and MONTH(Order_Date) = '".$dateYeabbr."' AND YEAR(Order_Date) = '".$year."'";
        $MOV = performQuery($queryy);
$query = "select *,case when (isnull(contactperson,'') +','+ isnull(contactperson2,'')+','+ isnull(contactperson3,'')=',,') then (' ') else isnull(contactperson,'') +','+ isnull(contactperson2,'')+','+ isnull(contactperson3,'') end  as ContactPersionn  from Mas_ListedDr where ListedDrCode='".$MSL."' ";
        $POT = performQuery($query);
        if (count($collectionOrder) > 0) {
            for ($i = 0; $i < count($transHead); $i++) {
                $query = "select Product_Code,Product_Name,Quantity,value from ( select ROW_NUMBER() OVER(PARTITION BY Cust_Code,Stockist_Code order by Cust_Code,Stockist_Code,Order_Date desc) RW,Trans_Sl_No,sf_code,Cust_Code,Stockist_Code,Order_Date,route from Trans_Order_Head where Cust_Code='".$MSL."' and Stockist_Code='".$transHead[$i]['stockist_code']."') as H inner join Trans_Order_details D on H.Trans_Sl_No=D.Trans_Sl_No and RW=1";
                $RES = performQuery($query);
                $stockist[] = array(
                    "stockist_code" => $transHead[$i]['stockist_code'],
                    "Sum" => $transHead[$i]['sumoforder'],
                    "Avg" => floor($transHead[$i]['sumoforder'] / 6),
                    "outStandingAmt" => $collectionOrder[$i]['Out_standing_Amt'],
                    "OrderDetails" => $RES
                );                  
            }
        }
        $result['StockistDetails'] = $stockist;
        $result['OpeningStock'] =  $OpeningStock;
        $result['MOQ'] = $MOQ;
        $result['POTENTIAL'] = $POT;
        $result['MOV'] = $MOV;
        $query = "select Trans_SlNo,Trans_Detail_Slno,convert(varchar,Time,0) Adate,convert(varchar,cast(convert(varchar,Activity_Date,101)  as datetime),20) as DtTm,(Select content from vwFeedTemplate where ID=Rx) CalFed,Activity_Remarks,products,gifts from vwLastVstDet where rw=1 and Trans_Detail_Info_Code='" . $MSL . "'"; //and SF_Code='" . $SF . "'
        $as = performQuery($query);
        if (count($as) > 0) {
            $result['LVDt'] = $as[0]['Adate'];
            $Prods = (string) $as[0]['products'];
            $sProds = explode("#", $Prods . '#');
            $sSmp = '';
            $sProm = '';
            for ($il = 0; $il < count($sProds); $il++) {
                if ($sProds[$il] != '') {
                    $spr = explode("~", $sProds[$il]);
                    $Qty = 0;
                    if (count($spr) > 0) {
                        $QVls = explode("$", $spr[1]);
                        $Qty = $QVls[0];
                        $Vals = $QVls[1];
                    }
                    if ($Qty > 0)
                        $sSmp = $sSmp . $spr[0] . " ( " . $Qty . " )" . (($Vals > 0) ? " ( " . $Vals . " )" : "");
                    else
                        $sProm = $sProm . $spr[0] . ", ";
                }
            }

            $result['CallFd'] = (string) $as[0]['CalFed'];
            $result['Rmks'] = (string) $as[0]['Activity_Remarks'];
            $result['ProdSmp'] = $sSmp;
            $result['Prodgvn'] = $sProm;
            $result['DrGft'] = (string) $as[0]['gifts'];
        }else {
            $result['success'] = false;
        }
    } else {
        $result['success'] = false;
		$result['data'] = "";
    }
    return outputJSON($result);
}

function getEntryCount() {
    $sfCode = $_GET['sfCode'];
    $today = date('Y-m-d 00:00:00');
    $results = array();
    $query = "select Count(Trans_Detail_Info_Code) doctor_count from vwActivity_MSL_Details D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(H.activity_date as datetime)=cast('$today' as datetime)";
 
    $temp = performQuery($query);
    $results[] = $temp[0];
    $query = "select Count(Trans_Detail_Info_Code) chemist_count from vwActivity_CSH_Detail D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime) and Trans_Detail_Info_Type=2";
    $temp = performQuery($query);
    $results[] = $temp[0];
    $query = "select Count(Trans_Detail_Info_Code) stockist_count from vwActivity_CSH_Detail D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime) and Trans_Detail_Info_Type=3";
    $temp = performQuery($query);
    $results[] = $temp[0];
    $query = "select Count(Trans_Detail_Info_Code) uldoctor_count from vwActivity_Unlst_Detail D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime) and Trans_Detail_Info_Type=4";
    $temp = performQuery($query);
    $results[] = $temp[0];
    $query = "select isnull((SELECT top 1 isnull(remarks,'') from vwActivity_Report where sf_code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime)),'') as remarks";
    $temp = performQuery($query);
    $results[] = $temp[0];
    $query = "select isnull((SELECT top 1 Half_Day_FW from vwActivity_Report where sf_code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime)),'') as halfdaywrk";
    $temp = performQuery($query);
    $results[] = $temp[0];
    $query = "select Count(Trans_Detail_Info_Code) hospital_count from vwActivity_CSH_Detail D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime) and Trans_Detail_Info_Type=5";
    $temp = performQuery($query);
    $results[] = $temp[0];


$query = "select Count(Trans_Detail_Info_Code) SuperStokit from vwActivity_SuperCSH_Detail D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime) and Trans_Detail_Info_Type=8";
    $temp = performQuery($query);
    $results[] = $temp[0];
    return $results;
}

function getaddress($lat, $lng) {
    $url = 'http://maps.googleapis.com/maps/api/geocode/json?latlng=' . trim($lat) . ',' . trim($lng) . '&sensor=false';
    $json = @file_get_contents($url);
    $data = json_decode($json);
    $status = $data->status;
    if ($status == "OK") {
        return $data->results[0]->formatted_address;
    } else {
        return false;
    }
}

function updEntry() {
    $today = date('Y-m-d 00:00:00');
    $data = json_decode($_POST['data'], true);
    $SFCode = (string) $data[0]['Activity_Report']['SF_code'];
    $sql = "select SF_Code from vwActivity_report where sf_Code=" . $SFCode . " and cast(activity_date as datetime)=cast('$today' as datetime)";
    $result = performQuery($sql);
    if (count($result) < 1) {
        $result = array();
        $result['success'] = false;
        $result['type'] = 2;
        $result['msg'] = 'No Call Report Submited...';
        outputJSON($result);
        die;
    }

    $Remarks = (string) $data[0]['Activity_Report']['remarks'];
    $HalfDy = (string) $data[0]['Activity_Report']['HalfDay_FW_Type'];

    $sql = "update DCRMain_Temp set Remarks=$Remarks,Half_Day_FW=$HalfDy where sf_Code=" . $SFCode . " and cast(activity_date as datetime)=cast('$today' as datetime)";
    $result = performQuery($sql);
    $sql = "update DCRMain_Trans set Remarks=$Remarks,Half_Day_FW=$HalfDy where sf_Code=" . $SFCode . " and cast(activity_date as datetime)=cast('$today' as datetime)";
    $result = performQuery($sql);
    $resp["success"] = true;
    echo json_encode($resp);
}

function GetNotifyLists(){
    $sfCode = $_GET['sfCode'];
    $query = "exec getAppNotifyLists '" . $sfCode . "'";
    return performQuery($query);
}

function GetTPNotifyLists(){

  $sfCode = $_GET['sfCode'];
$sql = "SELECT  MAX(CONVERT(INT, RIGHT(NotifyID, LEN(NotifyID)-1))) as id FROM Mas_AppNotifyList";
   $Maxid = performQuery($sql);
$Nid=  'N'.$Maxid[0]['id'];
$sql = "select top 1 * from Mas_AppNotifyList where SF_Code='".$sfCode."'  ORDER BY CrDt DESC";
   
    return performQuery($sql);
}



function getFromTableWR($tableName, $coloumns, $divisionCode, $sfCode = null, $orderBy = null, $where = null, $join = null, $today = null, $wt = null) {

    $query = "SELECT " . join(",", $coloumns) . " FROM $tableName as tab";
    if (!is_null($join)) {
        $query.=" join " . join(" join ", $join);
    }
    $query.= " WHERE tab.Division_Code=" . $divisionCode;

    if (!is_null($where)) {
        $query.=" and " . join(" or ", $where);
    }

    if (!is_null($today)) {
        $today = date('Y-m-d 00:00:00');
        $query.="and cast(tab.activity_date as datetime)=cast('$today' as datetime)";
    }

    if (!is_null($orderBy)) {
        $query .=" ORDER BY " . join(", ", $orderBy);
    }

    return performQuery($query);
}


function getFromTable($tableName, $coloumns, $divisionCode, $sfCode = null, $orderBy = null, $where = null, $join = null, $today = null, $wt = null, $desig) {
	$resp=array();
    $query = "SELECT " . join(",", $coloumns) . " FROM $tableName as tab";
    if (!is_null($join)) {
        $query.=" join " . join(" join ", $join);
    }
    if (!is_null($sfCode)) {
        if ($tableName == 'vwTown_Master_APP' )                                   //if ($desig == "mgr" && ($tableName == 'vwTown_Master_APP' || $tableName == "vwDoctor_Master_APP"))
            $query .=" WHERE charindex(',$sfCode,',','+tab.Stockist_Code+',')>0";
        else if ($tableName == "vwDoctor_Master_APP")
            $query .=" WHERE charindex(',$sfCode,',','+tab.Distributor_Code+',')>0";
        else {
            $query .=" WHERE tab.SF_Code='$sfCode'";
        }
    } else {
        $query.= " WHERE tab.Division_Code=" . $divisionCode;
    }
    if (!is_null($where)) {
        $query.=" and " . join(" and ", $where);
    }
    if (!is_null($today)) {
        $today = date('Y-m-d 00:00:00');
        $query.=" and cast(tab.activity_date as datetime)=cast('$today' as datetime)";
    }
    if (!is_null($orderBy)) {
        $query .=" ORDER BY " . join(",", $orderBy);
    }

return  performQuery($query);
     

}

function updateEntry($sfCode) {
    $dt = date('Y-m-d');
    $sql = "select SUM(cast(Amount as int)) amt from Trans_Additional_Exp where CAST(Created_Date as date)='$dt' and Cal_Type=0 and Sf_Code='$sfCode'";
    $positiveVal = performQuery($sql);
    $sql = "select SUM(cast(Amount as int)) amt from Trans_Additional_Exp where CAST(Created_Date as date)='$dt' and Cal_Type=1 and Sf_Code='$sfCode'";
    $negativeVal = performQuery($sql);
    $updateAdditionalAmt = $positiveVal[0]['amt'] - $negativeVal[0]['amt'];
    $sql = "select Expense_Allowance,Expense_Distance,Expense_Fare,Expense_Total from  Trans_FM_Expense_Detail where CAST(Created_Date as date)='$dt' and Sf_Code='$sfCode'";
    $expenseDetail = performQuery($sql);
    $query = "delete from Trans_Additional_Exp where CAST(Created_Date as date)='$dt' and Sf_Code='$sfCode'";
    performQuery($query);
    $query = "delete from Trans_FM_Expense_Detail where CAST(Created_Date as date)='$dt' and Sf_Code='$sfCode'";
    performQuery($query);
    $query = "delete from Trans_FM_Expense_Head where CAST(snd_dt as date)='$dt' and Sf_Code='$sfCode'";
    performQuery($query);
    $sql = "select Total_Allowance,Total_Distance,Total_Fare,Total_Expense,Total_Additional_Amt,Grand_Total from Trans_Expense_Amount_Detail where Month=MONTH('$dt') and Year=YEAR('$dt') and Sf_Code='$sfCode'";
    $amountDetail = performQuery($sql);
    $Total_Allowance = $amountDetail[0]['Total_Allowance'] - $expenseDetail[0]['Expense_Allowance'];
    $Total_Distance = $amountDetail[0]['Total_Distance'] - $expenseDetail[0]['Expense_Distance'];
    $Total_Fare = $amountDetail[0]['Total_Fare'] - $expenseDetail[0]['Expense_Fare'];
    $Total_Expense = $amountDetail[0]['Total_Expense'] - $expenseDetail[0]['Expense_Total'];
    $Total_Additional_Amt = $amountDetail[0]['Total_Additional_Amt'] - $updateAdditionalAmt;
    $Grand_Total = $Total_Expense - $Total_Additional_Amt;
    $sql = "update Trans_Expense_Amount_Detail set Total_Allowance=$Total_Allowance,Total_Distance=$Total_Distance,Total_Fare=$Total_Fare,Total_Expense=$Total_Expense,Total_Additional_Amt=$Total_Additional_Amt,Grand_Total=$Grand_Total where Month=MONTH('$dt') and Year=YEAR('$dt') and Sf_Code='$sfCode'";
    performQuery($sql);
}

function orderDetailsDelete($sfCode, $custCode,$arc, $amc) {
    $date = date('Y-m-d');
    $sql = "select Trans_Sl_No,Order_Value,Collected_Amount from Trans_Order_Head where dcr_code='$amc'";
    $trDet = performQuery($sql);

    for ($t = 0; $t < count($trDet); $t++) {
        $Trans_Sl_No = $trDet[$t]['Trans_Sl_No'];
    
        $orderValue =$trDet[$t]['Order_Value'];
        $collectedAmount = $trDet[$t]['Collected_Amount'];
        $sql = "select Total_Order_Amount,Out_standing_Amt,Amt_Collect,Last_order_Amt from Order_Collection_Details where Sf_Code='$sfCode' and Cust_Code=$custCode";
        $tr = performQuery($sql);
        $total = $tr[0]['Total_Order_Amount'] - $orderValue;
    
        $collectAmount = $tr[0]['Amt_Collect'] - $collectedAmount;
        $outstand = $total - $collectAmount;
        $sql = "update Order_Collection_Details set Total_Order_Amount=$total,Out_standing_Amt=$outstand,Amt_Collect=$collectAmount,Last_Order_Amt=0 where Sf_Code='$sfCode' and Cust_Code=$custCode";
        $tr = performQuery($sql);
        $query = "delete from Trans_Order_Head WHERE Trans_Sl_No ='" . $Trans_Sl_No . "'";
        performQuery($query);
        $sql = "select Trans_Order_No from Trans_Order_Details where Trans_Sl_No='$Trans_Sl_No'";
        $tr = performQuery($sql);
        for ($i = 0; $i < count($tr); $i++) {
            $Trans_Order_No = $tr[$i]['Trans_Order_No'];
            $sql = "delete from Trans_Order_Details WHERE Trans_Order_No='$Trans_Order_No'";
            performQuery($sql);
        }
    }
}

function orderDetailsDeleteOtherWorkType($sfCode) {
    $date = date('Y-m-d');
    $sql = "select Cust_Code from Trans_Order_Head where Sf_Code='$sfCode' and cast(convert(varchar,Order_Date,101) as datetime)=cast('$date' as datetime)";
    $custCodes = performQuery($sql);

    for ($c = 0; $c < count($custCodes); $c++) {
        $custCode = $custCodes[$c]['Cust_Code'];
        $sql = "select Trans_Sl_No,Order_Value,Collected_Amount from Trans_Order_Head where Sf_Code='$sfCode' and Cust_Code=$custCode and cast(convert(varchar,Order_Date,101) as datetime)=cast('$date' as datetime)";
       
        $tr = performQuery($sql);
        $Trans_Sl_No = $tr[0]['Trans_Sl_No'];
        $orderValue = $tr[0]['Order_Value'];
        $collectedAmount = $tr[0]['Collected_Amount'];
        $sql = "select Total_Order_Amount,Out_standing_Amt,Amt_Collect,Last_order_Amt from Order_Collection_Details where Sf_Code='$sfCode' and Cust_Code=$custCode";
        $tr = performQuery($sql);
        $total = $tr[0]['Total_Order_Amount'] - $orderValue;

        $collectAmount = $tr[0]['Amt_Collect'] - $collectedAmount;
        $outstand = $total - $collectAmount;
        $sql = "update Order_Collection_Details set Total_Order_Amount=$total,Out_standing_Amt=$outstand,Amt_Collect=$collectAmount,Last_Order_Amt=0 where Sf_Code='$sfCode' and Cust_Code=$custCode";
        $tr = performQuery($sql);
        $query = "delete from Trans_Order_Head WHERE Trans_Sl_No ='" . $Trans_Sl_No . "'";
        performQuery($query);
        
        $sql = "delete from Trans_Order_Details where Trans_Sl_No='$Trans_Sl_No'";;
        performQuery($sql);
    }
}

function deleteEntry($arc, $amc) {
    if (!is_null($amc)) {
        $sql = "DELETE FROM DCRDetail_Lst_Temp where Trans_Detail_Slno='" . $amc . "'";
        performQuery($sql);
        $sql = "DELETE FROM DCRDetail_Lst_Trans where Trans_Detail_Slno='" . $amc . "'";
        performQuery($sql);

        $sql = "DELETE FROM DCRDetail_CSH_Temp where Trans_Detail_Slno='" . $amc . "'";
        performQuery($sql);
        $sql = "DELETE FROM DCRDetail_CSH_Trans where Trans_Detail_Slno='" . $amc . "'";
        performQuery($sql);

        $sql = "DELETE FROM DCRDetail_Unlst_Temp where Trans_Detail_Slno='" . $amc . "'";
        performQuery($sql);
        $sql = "DELETE FROM DCRDetail_Unlst_Trans where Trans_Detail_Slno='" . $amc . "'";
        performQuery($sql);

        $sql = "DELETE FROM Trans_Pragnancy__Head where Trans_Sl_No='" . $arc . "'";
        performQuery($sql);
    }
}

function getDistReport() {
    $filter = json_decode($_GET['filter'], true);
    $sfCode = $filter['sfCode'];
    $stockist = $filter['stockist'];
    $dyDt = $filter['date'];
    if ($filter['checkStock'] == 1)
        $sql = "select * from vwDistReport where cast(convert(varchar,Order_Date,101) as datetime)='$dyDt' and Stockist_Code=$stockist";
    if ($filter['checkStock'] != 1)
        $sql = "select * from vwDistReport where Sf_Code='$sfCode' and cast(convert(varchar,Order_Date,101) as datetime)='$dyDt' and Stockist_Code=$stockist";
    $res = performQuery($sql);
    return outputJSON($res);
}

function delAREntry($SF, $WT, $Dt) {
    $sqlH = "SELECT Trans_SlNo FROM vwActivity_Report where SF_Code='" . $SF . "' and lower(Work_Type) <> lower(" . $WT . ") and cast(activity_date as datetime)=cast('$Dt' as datetime)";

    $sql = "DELETE FROM DCRDetail_Lst_Temp where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);
    $sql = "DELETE FROM DCRDetail_Lst_Trans where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);

    $sql = "DELETE FROM DCRDetail_CSH_Temp where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);
    $sql = "DELETE FROM DCRDetail_CSH_Trans where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);

    $sql = "DELETE FROM DCRDetail_Unlst_Temp where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);
    $sql = "DELETE FROM DCRDetail_Unlst_Trans where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);

    $sql = "DELETE FROM DCREvent_Captures where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);

    $sql = "DELETE FROM DCRMain_Temp where SF_Code='" . $SF . "' and lower(Work_Type) <> lower(" . $WT . ") and cast(activity_date as datetime)=cast('$Dt' as datetime)";
    performQuery($sql);
    $sql = "DELETE FROM DCRMain_Trans where SF_Code='" . $SF . "' and lower(Work_Type) <> lower(" . $WT . ") and cast(activity_date as datetime)=cast('$Dt' as datetime)";
    performQuery($sql);
    $sql = "DELETE FROM DCRDetail_Distributors_Hunting where Trans_SlNo in (" . $sqlH . ")";
    performQuery($sql);
}


function svTrackDetail() {
    global $data;

    $sfCode = (string) $data['SF'];
    $device = (string) $data['device'];
    $gps = (string) $data['gps'];
    $time = (string) $data['time'];
    $net = (string) $data['net'];
    $battery = (string) $data['battery'];
    $query = "exec Map_SvTrackDetail '".$sfCode."','".$device."','".$gps."','".$time."','".$net."','".$battery."'";
    performQuery($query);
    $respon['msg'] = "success";
    return $respon;
}
function ViewGeoTag(){

global $data;
    $data = json_decode($_POST['data'], true);
$SF=(string) $data['SF'];
$cust=(string)$data['cust'];

$query="exec getViewTag '".$SF."','".$cust."'";
return performQuery($query);;
}


function addEntry() {
    $sfCode = $_GET['sfCode'];
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $data = json_decode($_POST['data'], true);
    $today = date('Y-m-d 00:00:00');
    $temp = array_keys($data[0]);
    $temp1 = array_keys($data[4]);
    $vals = $data[0][$temp[0]];
    $SFState='0';
    $sql = "SELECT Employee_Id,case sf_type when 4 then 'MR' else 'MGR' End SF_Type,State_Code FROM vwUserDetails where SF_code='" . $sfCode . "'";
    $as = performQuery($sql);
    $IdNo = (string) $as[0]['Employee_Id'];
    $SFTyp = (string) $as[0]['SF_Type'];
    $SFState=(string) $as[0]['State_Code'];
    switch ($temp[0]) {
        case "stockUpdation":
            $stateCode = $_GET['State_Code'];
            $editable = $_GET['editable'];
            $date = date('Y-m-d H:i:s');
            if ($_GET['sCode'] != "0") {
                $staffCode = $sfCode;
                $sfCode = $_GET['sCode'];
            } else
                $staffCode = null;

            if($sfCode!="undefined")
            {
                if ($editable == 1) {
                    $sql = "SELECT * FROM Trans_Current_Stock_details where Stockist_Code='" . $sfCode . "'";
                    $tRw = performQuery($sql);
                    for ($i = 0; $i < count($tRw); $i++) {
                        $updateDat = $tRw[$i]['Last_Updation_Date'];
                        $updateDate = $updateDat->format('Y-m-d H:i:s');
                        $productCode = $tRw[$i]['Product_Code'];
                        $sql = "delete from Trans_Stock_Updation_Details where Stockist_code='$sfCode' and Product_Code='$productCode' and Purchase_Date='$updateDate'";
                        performQuery($sql);
                        $sql = "delete from Trans_Secondary_Sales_Details where Stockist_Code='$sfCode' and Product_Code='$productCode' and date='$updateDate'";
                        performQuery($sql);
                    }
                }
                $sql = "delete from Trans_Current_Stock_details where Stockist_Code='$sfCode'";
                performQuery($sql);
                for ($i = 0; $i < count($vals); $i++) 
                {
                    $sql = "SELECT isNull(max(Tran_Slno),0)+1 as RwID FROM Trans_Stock_Updation_Details";
                    $tRw = performQuery($sql);
                    $pk = (int) $tRw[0]['RwID'];
                    $productCode = $vals[$i]['product'];
                    $productName = $vals[$i]['product_Nm'];
                    $cbQty = !empty($vals[$i]['cb_qty']) ? $vals[$i]['cb_qty'] : 0;
                    $pieces = !empty($vals[$i]['pieces']) ? $vals[$i]['pieces'] : 0;
                   $Mfg=($vals[$i]['Mgf_date']==null? '':$vals[$i]['Mgf_date']);
                   
                     if(!empty($vals[$i]['conversionQty']))
                    {
                        $sampleErpCode = $vals[$i]['conversionQty'];
                    }else{
                        $sql = "SELECT Sample_Erp_Code FROM Mas_Product_Detail where Product_Detail_Code='$productCode'";
                        $sampleCode = performQuery($sql);
                        $sampleErpCode = $sampleCode[0]['Sample_Erp_Code'];
                    }
                    $recvQty = $vals[$i]['recv_qty'];

                    $sql = "select isnull(cast(DistCasePrice as float),0) Distributor_Price,isnull(cast(RetailCasePrice as float),0) Retailor_Price from vwProductStateRates where State_Code=$stateCode and Product_Detail_Code='" . $productCode . "'";
                    $tr = performQuery($sql);
                    if (empty($tr)) {
                            $distPrice = 0;
                            $retPrice = 0;
                    }
                    else
                    {
                        $distPrice = $tr[0]['Distributor_Price'];
                        $retPrice = $tr[0]['Retailor_Price'];
                    }
        
                    $sql = "SELECT TOP(1)Cl_Qty,pieces FROM Trans_Secondary_Sales_Details where Stockist_Code='$sfCode' and Product_Code='$productCode' order by date desc";
                    $tRw = performQuery($sql);
                    $Op_Qty = 0;$OpP_Qty =0;
                    if (empty($tRw))
                    {
                            $Op_Qty = 0;$OpP_Qty =0;
                    }
                    else
                    {
                        $Op_Qty = $tRw[0]['Cl_Qty'];
                        $OpP_Qty = $tRw[0]['pieces'];
                    }

                    $sql = "select count(Sale_Code) Trans_Secondary_Sales_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and date='" . $date . "'";
                    $updFlag=false;
                    if((int)$tRw[0]['RwID']>0){
                        $updFlag=true;
                        $sql = "select Rec_Qty,Cl_Qty,pieces,Op_Qty,Sale_Qty,OP_Pieces Trans_Secondary_Sales_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and date='" . $date . "'";
                        $tRw = performQuery($sql);
                        if($recvQty<=0 && $tRw[0]['Rec_Qty']>0) $recvQty=$tRw[0]['Rec_Qty'];
                    }

                    $sql = "delete Trans_Stock_Updation_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and Purchase_Date='" . $date . "'";
                    performQuery($sql);

                    $sql = "insert into Trans_Stock_Updation_Details(Tran_Slno,Stockist_Code,Product_Code,Product_Name,Rec_Qty,Cb_Qty,pieces,Division_Code,Distributer_Rate,Retailor_Rate,Purchase_Date,Conversion_Qty,SfCode,Mgf_date) select '" . $pk . "','" . $sfCode . "','" . $productCode . "','" . $productName . "',$recvQty,$cbQty,$pieces," . $Owndiv . ",$distPrice,$retPrice,'" . $date . "',$sampleErpCode,'$staffCode','$Mfg'";
                    performQuery($sql);
                
                    $sql = "SELECT Stockist_Code FROM Trans_Current_Stock_details where Stockist_Code='$sfCode' and Product_Code='$productCode'";
                    $tRw = performQuery($sql);
                    if (!empty($tRw)) {
                        $sql = "DELETE FROM Trans_Current_Stock_details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "'";
                        performQuery($sql);
                    }


                   $sql = "select Distributor_Price,MRP_Price from mas_product_detail PMD inner join Mas_Product_State_Rates MPS  on PMD.Product_Detail_Code=MPS.Product_Detail_Code   where PMD.Product_Detail_Code='" . $productCode . "' and MPS.State_Code='$stateCode' ";
 
                   $CandPrate=performQuery($sql);
                   $Prate= $CandPrate[0]['MRP_Price'];
                   $Crate= $CandPrate[0]['Distributor_Price'];


                    $sql = "insert into Trans_Current_Stock_details(Stockist_Code,Product_Code,Product_Name,Rec_Qty,Cb_Qty,pieces,Division_Code,Last_Updation_Date,Conversion_Qty,SfCode,Mgf_date,Crate,Prate) select '" . $sfCode . "','" . $productCode . "','" . $productName . "',$recvQty,$cbQty,$pieces," . $Owndiv . ",'" . $date . "',$sampleErpCode,'$staffCode','$Mfg','$Crate','$Prate'";
                    performQuery($sql);

                    $sql = "SELECT isNull(max(Sale_Code),0)+1 as RwID FROM Trans_Secondary_Sales_Details";
                    $tRw = performQuery($sql);
                    $pk = (int) $tRw[0]['RwID'];
    
                    if (($pieces-$OpP_Qty) > 0) {
                        $saleQty = (($recvQty + $Op_Qty) - ($cbQty + 1));
                        $salepieces = $sampleErpCode - ($pieces-$OpP_Qty);
                    } else {
                        $saleQty = ($recvQty + $Op_Qty) - ($cbQty);
                        $salepieces = 0 - ($pieces-$OpP_Qty);
                    }
                    if($saleQty<0){$saleQty = 0;$salepieces=0;}
                    if($salepieces<0){$salepieces=0;}
                    $sql = "delete Trans_Secondary_Sales_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and date='" . $date . "'";
                    performQuery($sql);
                    $sql = "insert into Trans_Secondary_Sales_Details(Sale_Code,Stockist_Code,Product_Code,Product_Name,Rec_Qty,Cl_Qty,pieces,Distributer_Rate,Retailor_Rate,Op_Qty,Sale_Qty,OP_Pieces,date,Conversion_Qty,sale_pieces,SfCode,RwFlg,Rec_Pieces) select $pk,'" . $sfCode . "','" . $productCode . "','" . $productName . "',$recvQty,$cbQty,$pieces,$distPrice,$retPrice,$Op_Qty,$saleQty,$OpP_Qty,'" . $date . "',$sampleErpCode,$salepieces,'$staffCode',1,0";
                    performQuery($sql);
                }
                $resp["success"] = true;
            }else{
                $resp["success"] = false;
            }
            echo json_encode($resp);
            die;
            break;


case "SSstockUpdation":
            $stateCode = $_GET['State_Code'];
            $editable = $_GET['editable'];
            $date = date('Y-m-d H:i:s');
            if ($_GET['sCode'] != "0") {
                $staffCode = $sfCode;
                $sfCode = $_GET['sCode'];
            } else
                $staffCode = null;

            if($sfCode!="undefined")
            {
                if ($editable == 1) {
                    $sql = "SELECT * FROM Trans_Current_SSStock_details where Stockist_Code='" . $sfCode . "'";
                    $tRw = performQuery($sql);
                    for ($i = 0; $i < count($tRw); $i++) {
                        $updateDat = $tRw[$i]['Last_Updation_Date'];
                        $updateDate = $updateDat->format('Y-m-d H:i:s');
                        $productCode = $tRw[$i]['Product_Code'];
                        $sql = "delete from Trans_SS_Stock_Updation_Details where Stockist_code='$sfCode' and Product_Code='$productCode' and Purchase_Date='$updateDate'";
                        performQuery($sql);
                        $sql = "delete from Trans_Secondary_SSSales_Details where Stockist_Code='$sfCode' and Product_Code='$productCode' and date='$updateDate'";
                        performQuery($sql);
                    }
                }
                $sql = "delete from Trans_Current_SSStock_details where Stockist_Code='$sfCode'";
                performQuery($sql);
                for ($i = 0; $i < count($vals); $i++) 
                {
                    $sql = "SELECT isNull(max(Tran_Slno),0)+1 as RwID FROM Trans_SS_Stock_Updation_Details";
                    $tRw = performQuery($sql);
                    $pk = (int) $tRw[0]['RwID'];
                    $productCode = $vals[$i]['product'];
                    $productName = $vals[$i]['product_Nm'];
                    $cbQty = !empty($vals[$i]['cb_qty']) ? $vals[$i]['cb_qty'] : 0;
                    $pieces = !empty($vals[$i]['pieces']) ? $vals[$i]['pieces'] : 0;
                    
                    if(!empty($vals[$i]['conversionQty']))
                    {
                        $sampleErpCode = $vals[$i]['conversionQty'];
                    }else{
                        $sql = "SELECT Sample_Erp_Code FROM Mas_Product_Detail where Product_Detail_Code='$productCode'";
                        $sampleCode = performQuery($sql);
                        $sampleErpCode = $sampleCode[0]['Sample_Erp_Code'];
                    }
                    $recvQty = $vals[$i]['recv_qty'];

                    $sql = "select isnull(cast(DistCasePrice as float),0) Distributor_Price,isnull(cast(RetailCasePrice as float),0) Retailor_Price from vwProductStateRates where State_Code=$stateCode and Product_Detail_Code='" . $productCode . "'";
                    $tr = performQuery($sql);
                    if (empty($tr)) {
                            $distPrice = 0;
                            $retPrice = 0;
                    }
                    else
                    {
                        $distPrice = $tr[0]['Distributor_Price'];
                        $retPrice = $tr[0]['Retailor_Price'];
                    }
        
                    $sql = "SELECT TOP(1)Cl_Qty,pieces FROM Trans_Secondary_SSSales_Details where Stockist_Code='$sfCode' and Product_Code='$productCode' order by date desc";
                    $tRw = performQuery($sql);
                    $Op_Qty = 0;$OpP_Qty =0;
                    if (empty($tRw))
                    {
                            $Op_Qty = 0;$OpP_Qty =0;
                    }
                    else
                    {
                        $Op_Qty = $tRw[0]['Cl_Qty'];
                        $OpP_Qty = $tRw[0]['pieces'];
                    }

                    $sql = "select count(Sale_Code) Trans_Secondary_SSSales_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and date='" . $date . "'";
                    $updFlag=false;
                    if((int)$tRw[0]['RwID']>0){
                        $updFlag=true;
                        $sql = "select Rec_Qty,Cl_Qty,pieces,Op_Qty,Sale_Qty,OP_Pieces Trans_Secondary_SSSales_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and date='" . $date . "'";
                        $tRw = performQuery($sql);
                        if($recvQty<=0 && $tRw[0]['Rec_Qty']>0) $recvQty=$tRw[0]['Rec_Qty'];
                    }

                    $sql = "delete Trans_SS_Stock_Updation_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and Purchase_Date='" . $date . "'";
                    performQuery($sql);

                    $sql = "insert into Trans_SS_Stock_Updation_Details(Tran_Slno,Stockist_Code,Product_Code,Product_Name,Rec_Qty,Cb_Qty,pieces,Division_Code,Distributer_Rate,Retailor_Rate,Purchase_Date,Conversion_Qty,SfCode) select '" . $pk . "','" . $sfCode . "','" . $productCode . "','" . $productName . "',$recvQty,$cbQty,$pieces," . $Owndiv . ",$distPrice,$retPrice,'" . $date . "',$sampleErpCode,'$staffCode'";
                    performQuery($sql);



                
                    $sql = "SELECT Stockist_Code FROM Trans_Current_SSStock_details where Stockist_Code='$sfCode' and Product_Code='$productCode'";
                    $tRw = performQuery($sql);
                    if (!empty($tRw)) {
                        $sql = "DELETE FROM Trans_Current_SSStock_details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "'";
                        performQuery($sql);
                    }
                    $sql = "insert into Trans_Current_SSStock_details(Stockist_Code,Product_Code,Product_Name,Rec_Qty,Cb_Qty,pieces,Division_Code,Last_Updation_Date,Conversion_Qty,SfCode) select '" . $sfCode . "','" . $productCode . "','" . $productName . "',$recvQty,$cbQty,$pieces," . $Owndiv . ",'" . $date . "',$sampleErpCode,'$staffCode'";
                    performQuery($sql);
                    $sql = "SELECT isNull(max(Sale_Code),0)+1 as RwID FROM Trans_Secondary_SSSales_Details";
                    $tRw = performQuery($sql);
                    $pk = (int) $tRw[0]['RwID'];
    
                    if (($pieces-$OpP_Qty) > 0) {
                        $saleQty = (($recvQty + $Op_Qty) - ($cbQty + 1));
                        $salepieces = $sampleErpCode - ($pieces-$OpP_Qty);
                    } else {
                        $saleQty = ($recvQty + $Op_Qty) - ($cbQty);
                        $salepieces = 0 - ($pieces-$OpP_Qty);
                    }
                    if($saleQty<0){$saleQty = 0;$salepieces=0;}
                    if($salepieces<0){$salepieces=0;}
                    $sql = "delete Trans_Secondary_SSSales_Details where Stockist_Code='" . $sfCode . "' and Product_Code='" . $productCode . "' and date='" . $date . "'";
                    performQuery($sql);
                    $sql = "insert into Trans_Secondary_SSSales_Details(Sale_Code,Stockist_Code,Product_Code,Product_Name,Rec_Qty,Cl_Qty,pieces,Distributer_Rate,Retailor_Rate,Op_Qty,Sale_Qty,OP_Pieces,date,Conversion_Qty,sale_pieces,SfCode,RwFlg,Rec_Pieces) select $pk,'" . $sfCode . "','" . $productCode . "','" . $productName . "',$recvQty,$cbQty,$pieces,$distPrice,$retPrice,$Op_Qty,$saleQty,$OpP_Qty,'" . $date . "',$sampleErpCode,$salepieces,'$staffCode',1,0";
                    performQuery($sql);
                }
                $resp["success"] = true;
            }else{
                $resp["success"] = false;
            }
            echo json_encode($resp);
            die;
            break;



        case "tbMyDayPlan":

            //$sql="delete from tbmydayplan where sf_code='$sfCode' and cast(pln_date as date)=cast(GETDATE() as date)";
            // performQuery($sql);

            $ww=$vals["worked_with"];
            $datesumm=date('Y-m-d');
            $today=date('Y-m-d H:i:s');

            if($vals["dcr_activity_date"]!=null && $vals["dcr_activity_date"]!=''){
                    $today=str_replace("'", "", $vals["dcr_activity_date"]);
                    $datesumm=date('Y-m-d ',strtotime($today));
            }
 if( $vals['profilepic']!=null && $vals['profilepic']!="undefined")
            {
                                  
                   
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,DCRDetNo,imgurl,Division_Code,Identification,Insert_Date_Time) values( '" . $sfCode . "','" . $State_Code . "','" . $vals['profilepic']['imgurl'] . "','" . $Owndiv . "','Profile Pic','".$today."')";
                    performQuery($sql); 
     
            }

            if (str_replace("'", "", $vals["FWFlg"]) != "F") {
                    $sql="select count(*) count from dcr_summary where sf_code='$sfCode' and cast(submission_date as date)='$datesumm' and editFlag=1";
                    $cunSumm = performQuery($sql);
                    $cunSumm=$cunSumm[0]['count'];
                    if($cunSumm>0){
                            $result['msg'] = "DCR Summary Already Updated";
                            outputJSON($result);
                            die;
                    }
            }
            if($vals["dcrtype"]==null)
                    $dcrtype="";
            else
                    $dcrtype=$vals["dcrtype"];
				
				if($vals["OtherWors"]==null)
                    $OtherWors="";
            else
                    $OtherWors=$vals["OtherWors"];
				
				

            if($vals["location"]==null)
                    $location="";
            else
                    $location=$vals["location"];


 $sql = "insert into tbMyDayPlan(sf_code,sf_member_code,Pln_Date,cluster,remarks,Division_Code,wtype,FWFlg,ClstrName,stockist,worked_with_code,worked_with_name,dcrtype,location,CustId,CustName,StkName,Sprstk,Other_Works) select '" . $sfCode . "'," . $vals["sf_member_code"] . ",'".$today."'," . $vals["cluster"] . "," . $vals["remarks"] . ",'" . $Owndiv . "'," . $vals["wtype"] . "," . $vals["FWFlg"] . "," . $vals["ClstrName"] . "," . $vals["stockist"].",$ww,(Select SF_Name+', ' from vwUserDetails where charindex('$$'+sf_Code+'$$','$$'+$ww+'$$')>0 for XML path('')),'$dcrtype','$location','".$vals["custid"]."','".$vals["custName"]."','".$vals["stkName"]."' ,'".$vals["superstockistid"]."' ,'".$OtherWors."'";  
performQuery($sql);

$missedquery="exec svPostMissAndAuto  '$sfCode','".$today."','" . $Owndiv . "' ";

performQuery($missedquery);

            if (str_replace("'", "", $vals["FWFlg"]) != "F") {
                $sql = "SELECT * FROM vwActivity_Report where SF_Code='" . $sfCode . "'  and cast(activity_date as datetime)=cast('$today' as datetime)";
                $result1 = performQuery($sql);

                if (count($result1) > 0) {
                        if ($result1[0]['FWFlg'] == 'L' && $result1[0]['Confirmed'] != 2 && $result1[0]['Confirmed'] != 3) {
                            $result['msg'] = "Leave Post Already Updated";
                            outputJSON($result);
                            die;
                        } else {
                            orderDetailsDeleteOtherWorkType($sfCode);
                            delAREntry($sfCode, $vals["wtype"], $today);
                            $sql="delete from dcr_summary where sf_code='$sfCode' and cast(submission_date as date)='$datesumm'";
                            performQuery($sql);
                            $ARCd = "";
                            $sql = "{call  svDCRMain_App(?,?," . $vals["wtype"] . ",'" . str_replace("'", "", $vals["cluster"]) . "',?,'" . str_replace("'", "", $vals["remarks"]) . "',?,'" . str_replace("'", "", $vals["superstockistid"]) . "')}";
                            $params = array(array($sfCode, SQLSRV_PARAM_IN),
                                array($today, SQLSRV_PARAM_IN),
                                array($Owndiv, SQLSRV_PARAM_IN),
                                array(&$ARCd, SQLSRV_PARAM_INOUT, SQLSRV_PHPTYPE_STRING(SQLSRV_ENC_CHAR), SQLSRV_SQLTYPE_VARCHAR(50)));
                            performQueryWP($sql, $params);
                        }
                    } else {
                        delAREntry($sfCode, $vals["wtype"], $today);

                        $ARCd = "";
                        $sql = "{call  svDCRMain_App(?,?," . $vals["wtype"] . ",'" . str_replace("'", "", $vals["cluster"]) . "',?,'" . str_replace("'", "", $vals["remarks"]) . "',?,'" . str_replace("'", "", $vals["superstockistid"]) . "')}";
                        $params = array(array($sfCode, SQLSRV_PARAM_IN),
                            array($today, SQLSRV_PARAM_IN),
                            array($Owndiv, SQLSRV_PARAM_IN),
                            array(&$ARCd, SQLSRV_PARAM_INOUT, SQLSRV_PHPTYPE_STRING(SQLSRV_ENC_CHAR), SQLSRV_SQLTYPE_VARCHAR(50)));
                        performQueryWP($sql, $params);
                    }
            }
            break;
        case "chemists_master":
            $sql = "SELECT isNull(max(Chemists_Code),0)+1 as RwID FROM Mas_Chemists";
            $tRw = performQuery($sql);
            $pk = (int) $tRw[0]['RwID'];

            $sql = "insert into Mas_Chemists(Chemists_Code,Chemists_Name,Chemists_Address1,Territory_Code,Chemists_Phone,Chemists_Contact,Division_Code,Cat_Code,Chemists_Active_Flag,Sf_Code,Created_Date,Created_By) select '" . $pk . "'," . $vals["chemists_name"] . "," . $vals["Chemists_Address1"] . "," . $vals["town_code"] . "," . $vals["Chemists_Phone"] . ",'','" . $Owndiv . "','',0,'" . $sfCode . "','" . date('Y-m-d H:i:s') . "','Apps'";
            performQuery($sql);
            break;
        case "DCRDetail_Distributors_Hunting":
            if($vals['slno']!=null ){
                $sql = "update DCRDetail_Distributors_Hunting set Shop_Name=" . $vals['name'] . ",Contact_Person=" . $vals['contact'] . ",Phone_Number=" . $vals['phone'] . ",area=" . $vals["area"] . ",remarks=" . $vals["remarks"] . " where slno=".$vals['slno']."";
                performQuery($sql);
            }
            else{
                $sql = "{call  svNewDistirbutorsHunting(" . $vals['name'] . "," . $vals['contact'] . "," . $vals['phone'] . "," . $vals["area"] . "," . $vals["remarks"] . ",'" . date('Y-m-d H:i:s') . "','$sfCode')}";
                performQueryWP($sql,[]);
            }
            break;
        case "unlisted_doctor_master":
            if($vals["unlisted_cat_code"]==null)$vals["unlisted_cat_code"]=0;
            $Ukey=$vals['unlisted_doctor_name'].date('Y-m-d H:i:s');
            $divCode = $_GET['divisionCode'];
            $divisionCode = explode(",", $divCode);
            $sqlrnd="SELECT Retailer_add_ND from Access_Master where division_code='".$divisionCode[0]."'";
            $flagofretailerND= performQuery($sqlrnd)[0]['Retailer_add_ND'];  
 	$today=date('Y-m-d H:i:s');
   

       if($vals['lat']==null || $vals['lat']=="undefined"){
          $Lat='';
           $Long='';
       }else{
          $Lat=$vals["lat"];
          $Long=$vals["long"];
        }

   $visitpath='';

if($vals['Retailerphoto']!=null && $vals['Retailerphoto']!="undefined")
            {
                      
        
                   $visitpath=$sfCode.'_'.''.$vals['Retailerphoto']['imgurl'];
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,DCRDetNo,imgurl,Division_Code,Identification,Insert_Date_Time) values( '$sfCode','" . $State_Code . "','" . $vals['Retailerphoto']['imgurl'] . "','" . $Owndiv . "','Retailer Pic','".$today."')";
                    performQuery($sql); 
     
            }


         $ListedDr_Active_Flag=0;
         if($flagofretailerND==1){
          $ListedDr_Active_Flag=3;
        }
            $sql = "{call  svListedDR_APP_Distributor('$sfCode'," . $vals['unlisted_doctor_name'] . "," . $vals['unlisted_doctor_address'] . "," . $vals['unlisted_doctor_phone'] . ",'" . str_replace("'","",$vals['unlisted_doctor_cityname']) . "','" . str_replace("'","",$vals['unlisted_doctor_areaname']) . "','" . str_replace("'","",$vals['unlisted_doctor_contactperson']) . "', ' " . str_replace("'","",$vals['unlisted_doctor_designation']) . "','" . str_replace("'","",$vals['unlisted_doctor_gst']) . "','" . str_replace("'","",$vals['unlisted_doctor_pincode']) . "'," . $vals["unlisted_specialty_code"] . "," . $vals["unlisted_cat_code"] . "," . $vals["town_code"] . ",$ListedDr_Active_Flag,'" . date('Y-m-d H:i:s') . "',$Owndiv," . $vals["unlisted_class"] . ",0," . $vals['wlkg_sequence'] . ",'',''," . $vals['DrKeyId'] . ",'" . str_replace("'","",$vals['unlisted_doctor_phone2']) . "','" . str_replace("'","",$vals['unlisted_doctor_contactperson2']) . "','" . str_replace("'","",$vals['unlisted_doctor_designation2']) . "',
            '" .str_replace("'","",$vals['unlisted_doctor_landmark']) . "','" . str_replace("'","",$Ukey) . "','','','','" . $visitpath . "',".$Lat.",".$Long.",'" . str_replace("'","",$vals['unlisted_doctor_contactperson2']) . "','" . str_replace("'","",$vals['unlisted_doctor_phone3']) . "','" . str_replace("'","",$vals['unlisted_doctor_email']) . "','" . str_replace("'","",$vals['unlisted_doctor_mobile']) . "' )}";
           performQueryWP($sql,[]);

$resp["success"] = true;
 $resp["Query"] = $sql;
    echo json_encode($resp);
 die;

        break;

case "unlisted_Expense_start":
   $Datetime=date('Y-m-d H:i:s');
  $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
   $divisionCode = explode(",", $divCode);
 

    $sql = "insert into Expense_Start_Activity select '$sfCode',".$Owndiv.",'".$Datetime."',".$vals['Km'].",'".$vals['Retailerphoto']['imgurl']."' ,".$vals['Remarks'].",".$vals['MOT']."";
		      performQuery($sql);
 
 $resp["success"] = true;
 $resp["Query"] = $sql;
    echo json_encode($resp);
 die;
 break;




	case "ChangePassword":
		
		  $newpassword=$vals['newpassword'];
		  
		    $divCode = $_GET['divisionCode'];
		   

		    $sql = "update Mas_salesforce set Sf_Password='" . $newpassword . "'  where Sf_Code= '$sfCode' and Division_Code='$divCode'";
		      performQuery($sql);
				 
               $resp["success"] = true;
                echo json_encode($resp);
                die;
		
		break;

	case "ProductAvailablity":
		
		
		  $worktype=$vals['worktype'];
		  $doctorid=$vals['doctorid'];
		  $stockist=$vals['stockist'];
		  $cluster=$vals['cluster'];
		  $entryDate=$vals['entryDate'];
		  $divCode = $_GET['divisionCode'];   
          $ProductList=$vals['ProductList'];
          $divCode = $_GET['divisionCode'];
          $divisionCode = explode(",", $divCode);  
		   $Remarks = $vals['Remarks'];
		   
		   
		    if($vals['Remarks']==null || $vals['Remarks']=="undefined"){
          $Remarks="Product Availability";
       }else{
          $Remarks=$vals['Remarks'];
        }
		   
		   
          $today=date('Y-m-d H:i');
          $todayDCR=date('Y-m-d 00:00:00.000');
                
      $ARCd="";
      $sql = "{call  svDCRMain_App(?,?,'" . $worktype . "','" . $cluster  . "',?,'Product Availability',?,'0')}";
            $params = array(array($sfCode, SQLSRV_PARAM_IN),
                array($todayDCR, SQLSRV_PARAM_IN),
                array($divisionCode[0], SQLSRV_PARAM_IN),
                array(&$ARCd, SQLSRV_PARAM_INOUT, SQLSRV_PHPTYPE_STRING(SQLSRV_ENC_CHAR), SQLSRV_SQLTYPE_NVARCHAR(50)));

            $tr = performQueryWP($sql, $params);
                if (sqlsrv_errors() != null) {
                    echo($sql . "<br>");
                    outputJSON($params );
                    echo( "<br>");
                    outputJSON(sqlsrv_errors());
                    die;
                }  

          $sql="exec ProductAvail_Visit  '" .  $sfCode  . "','".$divisionCode[0]."' ,'".$today." ' , '" . $cluster  . "', '" . $stockist  . "' ,'" . $worktype . "','" . $doctorid . "', '" .  $ARCd  . "','" . $Remarks. "' ";
                   performQuery($sql); 
			
						
        for ($i = 0; $i < count($ProductList); $i++) {
		
       if($ProductList[$i]['NewCustomer']==null ||$ProductList[$i]['NewCustomer']=="undefined"){
	   $Newcustomer=0;
          }else{
	     $Newcustomer=$ProductList[$i]['NewCustomer'];
           }


       $sql = "SELECT isNull(max(Sl_No),0)+1 as Sl_No FROM Trans_Product_Availability";
            $topr = performQuery($sql);
            $pk = (int) $topr[0]['Sl_No'];
          $sql="insert into Trans_Product_Availability select '" .  $sfCode  . "','".$divisionCode[0]."'  ,'" . $doctorid . "', '" . $cluster . "' ,'" . $stockist . "', '" . $ProductList[$i]['id'] . "','" . $ProductList[$i]['name'] . "','" . $worktype . "', '".date('Y-m-d H:i')." ','" . $ProductList[$i]['Available'] . "','" . $ProductList[$i]['Visibility'] . "','" . $Newcustomer . "','" . $pk . "' ";
	
		      performQuery($sql);
          }
				 
               $resp["success"] = true;
                echo json_encode($resp);
                die;
		
		break;
		case "PrimaryClosing":
		
		 $worktype=$vals['worktype'];
		  $Ekey=$vals['Ekey'];
		  $stockist=$vals['stockist'];
		  $cluster=$vals['cluster'];
		  $entryDate=$vals['entryDate'];
		  $divCode = $_GET['divisionCode'];   
          $ProductList=$vals['ProductList'];
          $divCode = $_GET['divisionCode'];
          $divisionCode = explode(",", $divCode);  
		   $today=date('Y-m-d H:i');
          $todayDCR=date('Y-m-d 00:00:00.000');
		   $Remarks = $vals['Remarks'];
		   $ARCd="";
      $sql = "{call  svDCRMain_App(?,?,'" . $worktype . "','" . $cluster  . "',?,'PrimaryCLosing',?,'0')}";
            $params = array(array($sfCode, SQLSRV_PARAM_IN),
                array($todayDCR, SQLSRV_PARAM_IN),
                array($divisionCode[0], SQLSRV_PARAM_IN),
                array(&$ARCd, SQLSRV_PARAM_INOUT, SQLSRV_PHPTYPE_STRING(SQLSRV_ENC_CHAR), SQLSRV_SQLTYPE_NVARCHAR(50)));

            $tr = performQueryWP($sql, $params);
                if (sqlsrv_errors() != null) {
                    echo($sql . "<br>");
                    outputJSON($params );
                    echo( "<br>");
                    outputJSON(sqlsrv_errors());
                    die;
                }  
		  
		  $sql="exec SV_primary_Closing  '" .  $sfCode  . "','".$divisionCode[0]."' ,'".$entryDate." ' , '" . $cluster  . "', '" . $stockist  . "' ,'" . $worktype . "', '" .  $ARCd  . "','" . $Remarks. "' ";
                   performQuery($sql); 
				   
				
				  
		  
		   
		
		for ($i = 0; $i < count($ProductList); $i++) {
		
           $sql = "SELECT isNull(max(Sl_No),0)+1 as Sl_No FROM Trans_Primary_Closing";
            $topr = performQuery($sql);
            $pk = (int) $topr[0]['Sl_No'];
          $sql="insert into Trans_Primary_Closing select '" . $pk . "','" .  $sfCode  . "','".$divisionCode[0]."'  , '" . $cluster . "' ,'" . $stockist . "', '" . $ProductList[$i]['id'] . "','" . $ProductList[$i]['name'] . "','" . $worktype . "', '".date('Y-m-d H:i')." ','" . $ProductList[$i]['Closing']. "','" . $Ekey . "' ,'" . $Remarks. "' ";
	
		      performQuery($sql);
          }
			$resp["success"] = true;
        echo json_encode($resp);
         die;
		break;
		
		
		
		
        case "TP_Attendance":
            $dateTime=date('Y-m-d H:i');
            $date=date('Y-m-d');
            $login_date=date('Y-m-d');
            $lat=$vals['lat'];
            $long=$vals['long'];
            $update=$_GET['update'];
            if($update==0){
                $sql ="exec Attendance_entry '$sfCode','$Owndiv','$dateTime',$lat,$long,'$login_date'";
                $result=performQuery($sql);     
            }
            else{
                $sql="select id from TP_Attendance_App where Sf_Code='$sfCode' and DATEADD(dd, 0, DATEDIFF(dd,0,Start_Time))='$date' order by id desc";
                $tr=performQuery($sql);
                $id=$tr[0]['id'];
            
                $sql="update TP_Attendance_App set End_Lat=$lat,End_Long=$long,End_Time='$dateTime' where id=$id";
                performQuery($sql);
            
                $sql1="select ID from Attendance_history where Sf_Code='$sfCode' and DATEADD(dd, 0, DATEDIFF(dd,0,Start_Time))='$date' order by id desc";
                $tr1=performQuery($sql1);
                $id1=$tr1[0]['ID'];
                $sql1="update Attendance_history set End_Lat=$lat,End_Long=$long,End_Time='$dateTime' where ID=$id1";
                performQuery($sql1);
                $result=[];
                $result["msg"]="1";
            }

            outputJSON($result);
            die;
            break;

        case "Map_GEO_Customers":
            $vals["addrs"]="'".getaddress(str_replace("'","",$vals["lat"]),str_replace("'","",$vals["long"]))."'";
            $sql = "SELECT isNull(max(MapId),0)+1 as MapId FROM Map_GEO_Customers";
            $topr = performQuery($sql);
            $pk = (int) $topr[0]['MapId'];

            $tableData[$tableName]["MapId"] = $pk ;
            $TABLE_KEYS_MAPPING["'" . $tableName . "'"] = $tableData[$tableName]["MapId"];
			
			 $sql ="select count(Cust_Code) AS [Count] from Map_GEO_Customers   where Cust_Code=" . $vals["Cust_Code"] . "";
			 $tr1=performQuery($sql);
			if(!($tr1[0]['Count']>0)){
				$sql = "insert into Map_GEO_Customers(MapId,Cust_Code,lat,long,addrs,StatFlag,Division_code) select " . $pk . "," . $vals["Cust_Code"] . "," . $vals["lat"]  . "," . $vals["long"] . "," . $vals["addrs"] . ",0,'" . $Owndiv . "'" ;
            performQueryWP($sql,[]);
			}
			
            
            break;

case "Map_GEO_Distributor":
            $vals["addrs"]="'".getaddress(str_replace("'","",$vals["lat"]),str_replace("'","",$vals["long"]))."'";
            $sql = "SELECT isNull(max(MapId),0)+1 as MapId FROM Map_GEO_Distributors";
            $topr = performQuery($sql);
            $pk = (int) $topr[0]['MapId'];

            $tableData[$tableName]["MapId"] = $pk ;
            $TABLE_KEYS_MAPPING["'" . $tableName . "'"] = $tableData[$tableName]["MapId"];
            $sql = "insert into Map_GEO_Distributors(MapId,Cust_Code,lat,long,addrs,StatFlag,Division_code) select " . $pk . "," . $vals["Cust_Code"] . "," . $vals["lat"]  . "," . $vals["long"] . "," . $vals["addrs"] . ",0,'" . $Owndiv . "'" ;
            performQueryWP($sql,[]);
            break;
        case "dailyExpense": 
 $dateTime=date('Y-m-d H:i');
$tempp = array_keys($data[1]);
$va =$data[1][$tempp[0]];
            for ($i = 0; $i < count($vals); $i++) 
            {
                    $sql = "insert into Trans_Daily_User_Expense(SF_Code,eDate,expCode,expName,Amt,Division_Code,LUpdtDate) select '" . $sfCode . "',cast(convert(varchar,getdate(),101) as datetime),'" . $vals[$i]["ID"] . "','" . $vals[$i]["Name"]  . "','" . $vals[$i]["amt"] . "','" . $Owndiv . "',getDate()" ;
                    performQueryWP($sql,[]);
            }
          

  $sql1="insert into  Trans_Expense_Traveldetails(sf_code,Division_code,MOC,Remarks,FromPlace,FromDeparture,ToPlace,ToArrival,Date_Time,Fare)values('".$sfCode."','".$Owndiv ."','" . $va["MOT"] . "'
            ,'".$va["remarks"]."' ,'".$va["Start_image"]."','".$va["Start_Km"]."','".$va["Stop_image"]."','".$va["Stop_km"]."', '$dateTime' ,'".$va["BusFare"]."')";
        performQuery($sql1);


 if( $data[2]['ActivityCaptures']!=null && $data[2]['ActivityCaptures']!="undefined")
            {
                $Event_Captures = $data[2]['ActivityCaptures'];
                for ($j = 0; $j < count($Event_Captures); $j++) {                       
                    $ev_imgurl=str_replace("'", "",$Event_Captures[$j]["imgurl"]);
                    $ev_title=str_replace("'", "",$Event_Captures[$j]["title"]);
                    $ev_remarks=str_replace("'", "",$Event_Captures[$j]["remarks"]);
                    $visitpath=$sfCode.'_'.''.str_replace("'", "",$Event_Captures[$j]["imgurl"]);
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,imgurl,title,remarks,Division_Code,Identification,Insert_Date_Time) values( '".$sfCode."','" . $visitpath . "','" . $ev_title . "','" . $ev_remarks . "','".$Owndiv."','Expense','".$dateTime."')";
                    performQuery($sql); 
                }
            }



            break;

        

	

        case "tbTrackLoction":
            $sql = "select sf_emp_id,Employee_Id from Mas_Salesforce where Sf_Code='$sfCode'";
            $sf = performQuery($sql);
            $empid = $sf[0]['sf_emp_id'];
            $employeeid = $sf[0]['Employee_Id'];
            $lng = $vals['Lon'];
            $lat = $vals['Lat'];
            $address = getaddress($lat, $lng);
            $sql = "insert into tbTrackLoction(SF_code,Emp_Id,Employee_Id,DtTm,Lat,Lon,Addr,Auc,deg,DvcID) select '$sfCode','$empid','$employeeid'," . $vals['DtTm'] . ",$lat,$lng,'$address','" . str_replace("'", "", $vals['Auc']) . "','" . str_replace("'", "", $vals['deg']) . "','" . str_replace("'", "", $vals['DvcID']) . "'";
            $resp["qry"] =$sql;
            performQuery($sql);
            break;
        case "expense":
            $res = $data[0]['expense'];
            $date = date('Y-m-d H:i:s');
            $update = $_GET['update'];
            $dcrdate = date('d-m-Y');
            $divCode = $_GET['divisionCode'];
            $divisionCode = explode(",", $divCode);
            $desig = $_GET['desig'];
            $sfCode = $_GET['sfCode'];
            $sfName = $res['sfName'];
            $expenseAllowance = $res['allowance'];
            $expenseDistance = $res['distance'];
            $expenseFare = $res['fare'];
            $total = $res['tot'];
            $additionalTot = $res['additionalTot'];
            $wcode = $res['worktype'];
            $wname = $res['worktype_name'];
            $place = $res['place'];
            $placeno = $res['placeno'];
            $sql = "SELECT isNull(max(Sl_No),0)+1 as RwID FROM Trans_FM_Expense_Head";
            $tRw = performQuery($sql);
            $pk = (int) $tRw[0]['RwID'];
            if ($update == 1) {
                updateEntry($sfCode);
            }

            $sql = "insert into Trans_FM_Expense_Head(Sf_Code,Month,Year,sndhqfl,Division_Code,snd_dt,Sf_Name) select '$sfCode',MONTH('$date'),YEAR('$date'),0,$divisionCode[0],'$date'," . $sfName . "";
            performQuery($sql);

            $sql = "insert into Trans_FM_Expense_Detail(DCR_Date,Expense_wtype_Code,Expense_wtype_Name,Place_of_Work,Expense_Place_No,Division_Code,Expense_Allowance,Expense_Distance,Expense_Fare,Created_Date,LastUpdt_Date,Sf_Name,Sf_Code,Expense_Total) select '$dcrdate',$wcode,$wname,$place,$placeno,$divisionCode[0],$expenseAllowance,$expenseDistance,$expenseFare,'$date','$date',$sfName,'$sfCode',$total";
            performQuery($sql);

            $sql = "SELECT * FROM Trans_Expense_Amount_Detail where Month=MONTH('$date') and year=YEAR('$date') and Sf_Code='$sfCode'";
            $tRw = performQuery($sql);
            if (empty($tRw)) {
                $additionalAmount = $additionalTot + $total;
                $sql = "insert into Trans_Expense_Amount_Detail(Sf_Code,Month,Year,Division_Code,Sf_Name,Total_Allowance,Total_Distance,Total_Fare,Total_Expense,Total_Additional_Amt,Grand_Total) select '$sfCode',MONTH('$date'),YEAR('$date'),$divisionCode[0], $sfName,$expenseAllowance,$expenseDistance,$expenseFare,$total,$additionalTot,$additionalAmount";
                performQuery($sql);
            } else {
                $totAllowance = $tRw[0]['Total_Allowance'] + $expenseAllowance;
                $totDistance = $tRw[0]['Total_Distance'] + $expenseDistance;
                $totFare = $tRw[0]['Total_Fare'] + $expenseFare;
                $totalExpense = $tRw[0]['Total_Expense'] + $total;
                $totAdditionalAmt = $tRw[0]['Total_Additional_Amt'] + $additionalTot;
                $grandTotal = $totalExpense + $totAdditionalAmt;
                $slNo = $tRw[0]['Sl_No'];
                $sql = "update Trans_Expense_Amount_Detail set Total_Allowance=$totAllowance,Total_Distance=$totDistance,Total_Fare=$totFare,Total_Expense=$totalExpense,Total_Additional_Amt=$totAdditionalAmt,Grand_Total=$grandTotal where Sl_No='$slNo'";
                performQuery($sql);
            }
            $extraDet = $res['extraDetails'];
            for ($i = 0; $i < count($extraDet); $i++) {
                $parameterName = $extraDet[$i]['parameter'];

                $amount = $extraDet[$i]['amount'];
                $type = $extraDet[$i]['type'];
                if ($type == true)
                    $type = 0;
                else
                    $type = 1;
                if (!empty($parameterName))
                    $sql = "insert into Trans_Additional_Exp(Sf_Code,Month,Year,Division_Code,Created_Date,LastUpdt_Date,Created_By,Parameter_Name,Amount,Cal_Type,Confirmed) select '$sfCode',MONTH('$date'),YEAR('$date'),$divisionCode[0],'$date','$date','$sfCode','$parameterName','$amount','$type',0";
                performQuery($sql);
            }
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "Tour_Plan":
            $divCode = $_GET['divisionCode'];

            $divisionCode = explode(",", $divCode);
            $desig = $_GET['desig'];
            //$market = $data[0]['Tour_Plan']['market'];
            $objective = $data[0]['Tour_Plan']['objective'];
            $tourDate = $data[0]['Tour_Plan']['Tour_Date'];
            $worktype_code = $data[0]['Tour_Plan']['worktype_code'];
            $worktype_name = $data[0]['Tour_Plan']['worktype_name'];
            $worked_with_code = $data[0]['Tour_Plan']['Worked_with_Code'];
            $worked_with_name = $data[0]['Tour_Plan']['Worked_with_Name'];
            $RouteCode = $data[0]['Tour_Plan']['RouteCode'];
            $RouteName = $data[0]['Tour_Plan']['RouteName'];
            $sfName = $data[0]['Tour_Plan']['sfName'];
            $HQ_Code = $data[0]['Tour_Plan']['HQ_Code'];

              $ww=$data[0]['Tour_Plan']['worked_with'];
		$RName=$data[0]['Tour_Plan']['Multiretailername'];
		$RCode=$data[0]['Tour_Plan']['MultiretailerCode'];
            $HQ_Name = str_replace(")", "", str_replace("(", "", $data[0]['Tour_Plan']['HQ_Name']));



            $sql = "delete from Trans_TP_One WHERE SF_Code ='" . $sfCode . "' and Tour_Date=cast($tourDate as datetime)";
            performQuery($sql);
            $sql = "insert into Trans_TP_One(SF_Code,Tour_Month,Tour_Year,Submission_date,Tour_Date,WorkType_Code_B,Worktype_Name_B,Tour_Schedule1,Objective,Worked_With_SF_Code,Division_Code,Territory_Code1,Worked_With_SF_Name,TP_Sf_Name,HQ_Code,HQ_Name,Confirmed,Change_Status,JointWork_Name,Retailer_Code,Retailer_Name) select '" . $sfCode . "',MONTH($tourDate),YEAR($tourDate),getdate(),$tourDate,$worktype_code,$worktype_name,$RouteCode,$objective,$worked_with_code," . $divisionCode[0] . ",$RouteName,$worked_with_name,$sfName,$HQ_Code,$HQ_Name,0,0,$ww,'".$RCode."','".$RName."'";
            performQuery($sql);




 $sql = "insert into Trans_TP_Hist(SF_Code,Tour_Month,Tour_Year,Submission_date,Tour_Date,WorkType_Code_B,Worktype_Name_B,Tour_Schedule1,Objective,Worked_With_SF_Code,Division_Code,Territory_Code1,Worked_With_SF_Name,TP_Sf_Name,HQ_Code,HQ_Name,Confirmed,Change_Status,JointWork_Name,Retailer_Code,Retailer_Name) select '" . $sfCode . "',MONTH($tourDate),YEAR($tourDate),getdate(),$tourDate,$worktype_code,$worktype_name,$RouteCode,$objective,$worked_with_code," . $divisionCode[0] . ",$RouteName,$worked_with_name,$sfName,$HQ_Code,$HQ_Name,0,0,$ww,'".$RCode."','".$RName."'";
            performQuery($sql);

            $resp["success"] = true;
           // $resp["REs"]=$sql;

            echo json_encode($resp);
            die;
            break;
        case "TourPlanSubmit":
            $month = $_GET['month'];
            $year = $_GET['year'];
            $sql = "update Trans_TP_One set Change_Status=1,Confirmed=1 where Tour_Month=$month and Tour_Year=$year and Sf_Code='$sfCode'";
            performQuery($sql);
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;

        case "ExpenseApproval":
            $month = $_GET['month'];
            $year = $_GET['year'];
            $code = $_GET['code'];
        
            $sql = "update Trans_FM_Expense_Head set sndhqfl=1 where sf_Code='$code' and month=$month and year=$year";
            performQuery($sql);
          
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "ExpenseReject":
            $month = $_GET['month'];
            $year = $_GET['year'];
            $code = $_GET['code'];
            $sql = "update Trans_FM_Expense_Head set sndhqfl=2 where sf_Code='$code' and month=$month and year=$year";
            performQuery($sql);
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "DCRSummary":
            $data=$data[0]['DCRSummary'];
            $pcalls=$data['pcalls'];
            $upcalls=$data['upcalls'];
            $tcalls=$data['tcalls'];
            $date=date('Y-m-d');
            $DCR_TLSD=$data['DCR_TLSD'];
            $DCR_LPC=$data['DCR_LPC'];
            $brandwise=$data['brandwise'];
            $brandIds='';
            $brandNames='';
            if($brandwise!=null)
            {
                for ($j = 0; $j < count($brandwise); $j++) {
                    $brandIds = $brandIds . $brandwise[$j]["product_brd_code"] . "~" . $brandwise[$j]["RetailCount"] . "#";
                    $brandNames = $brandNames . $brandwise[$j]["product_brd_sname"] . "~" . $brandwise[$j]["RetailCount"] . "#";
                }
            }
            $sql="update dcr_summary set brand_ec='$brandNames',brand_id='$brandIds',dcr_tlsd=$DCR_TLSD,dcr_lpc=$DCR_LPC,productive_calls=$pcalls,unproductive_cals=$upcalls,total_cals=$tcalls,editFlag=1 where sf_code='$sfCode' and cast(submission_date as date)='$date'";

            performQuery($sql);
             $resp["success"] = true;
            echo json_encode($resp);
            die;
        break;
        case "TPApproval":
            $month = $_GET['month'];
            $year = $_GET['year'];
            $code = $_GET['code'];
            $sql = "insert into Trans_TP(Division_Code,SF_Code,Worked_With_SF_Code,Worked_With_SF_Name,Tour_Date,Tour_Month,Tour_Year,WorkType_Code_B,Worktype_Name_B,WorkType_Code_B1,Worktype_Name_B1,WorkType_Code_B2,Worktype_Name_B2,Objective,Confirmed,Confirmed_Date,Rejection_Reason,Territory_Code1,Territory_Code2,Territory_Code3,TP_Sf_Name,TP_Approval_MGR,Tour_Schedule1,Tour_Schedule2,Tour_Schedule3,Submission_date,Change_Status)
                        select Division_Code,SF_Code,Worked_With_SF_Code,Worked_With_SF_Name,Tour_Date,Tour_Month,Tour_Year,WorkType_Code_B,Worktype_Name_B,WorkType_Code_B1,Worktype_Name_B1,WorkType_Code_B2,Worktype_Name_B2,Objective,Confirmed,GETDATE(),Rejection_Reason,Territory_Code1,Territory_Code2,Territory_Code3,TP_Sf_Name,TP_Approval_MGR,Tour_Schedule1,Tour_Schedule2,Tour_Schedule3,Submission_date,Change_Status
                            from Trans_TP_One where sf_Code='$code' and Tour_Month=$month and Tour_Year=$year";
            $trs = performQuery($sql);
            if (count($trs) > 0) {
                $sql = "delete from Trans_TP_One where sf_Code='$code' and Tour_Month=$month and Tour_Year=$year";
                performQuery($sql);
            }
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "TPReject":
            $month = $_GET['month'];
            $year = $_GET['year'];
            $code = $_GET['code'];
            $sql = "insert into TP_Reject_B_Mgr(SF_Code,Tour_Month,Tour_Year,Reject_date,Division_Code,Rejection_Reason) select '" . $code . "',$month,$year,'" . date('Y-m-d H:i') . "',$Owndiv," . $vals['reason'] . "";
            performQuery($sql);

            $sql = "update Trans_TP_One set Change_Status=2,Confirmed=0,Rejection_Reason=" . $vals['reason'] . " where Tour_Month=$month and Tour_Year=$year and Sf_Code='$code'";
            performQuery($sql);
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "LeaveApproval":
              $leaveid = $_GET['leaveid'];
              $Mrsfcode = $_GET['sfCode'];
            $sql = "update Mas_Leave_Form set Leave_Active_Flag=0,LastUpdt_Date='" .date('Y-m-d H:i:s'). "',Approved_to='" . $Mrsfcode . "' where Leave_Id=$leaveid";
            performQuery($sql);

             $divCode = $_GET['divisionCode'];
            $sql = "SELECT sf_type FROM Mas_Salesforce where Sf_Code='" . $vals['Sf_Code'] . "'";

            $sfType = performQuery($sql);
            $days = $vals['No_of_Days'];
            $date = preg_split("[/]", $vals['From_Date']);
            $date = $date[2] . "-" . $date[1] . "-" . $date[0];
            for ($i = 1; $i <= $days; $i++) {
                $query = "exec ChkandPostLeaveDt 0,'" . $vals['Sf_Code'] . "'," . $sfType[0]['sf_type'] . ",$Owndiv,'$date','','apps'";
                $results = performQuery($query);
                $date = date('Y-m-d', strtotime($date . ' + 1 days'));
            }

      $sqll = "select Leave_Type from Mas_Leave_Form where  Leave_Id= '$leaveid'  and Sf_Code='" . $vals['Sf_Code'] ."'";
      $LeaveType =performQuery($sqll);
    $query = "update MasEntitlement set LeaveAvailability=LeaveAvailability-'" . $days . "',LeaveTaken=LeaveTaken+'" . $days . "' where  SFCode='" . $vals['Sf_Code'] . "' and  DivisionCode='". str_replace(",", "",$divCode)."'  and LeaveCode=" . $LeaveType[0]['Leave_Type']. "";

      performQuery($query);





            break;
        case "LeaveReject":
            $leaveid = $_GET['leaveid'];
           $Mrsfcode = $_GET['sfCode'];
            $sql = "update Mas_Leave_Form set Leave_Active_Flag=1,Rejected_Reason=" . $vals['reason'] . "  ,LastUpdt_Date='" .date('Y-m-d H:i:s'). "',Approved_to='" . $Mrsfcode . "' where Leave_Id=$leaveid ";
            performQuery($sql);
            break;


 case "LeaveFormValidate":
            $leaveid = $_GET['leaveid'];
             $query = "exec iOS_getLvlValidate '$sfCode','" . $vals['From_Date'] . "'," . $vals['To_Date'] . "," . $vals['Leave_Type'] . "";
                 $results = performQuery($query);
             $resp['Msg'] = $results[0]['Msg'];
            $resp["success"] = true;
            echo json_encode($resp);
            die;
                
            break;

  case "DCRMissedDates":
    $sfCode = $_GET['sfCode'];
    $div = $_GET['divisionCode'];
    $Remarks = $_GET['REmarks'];
    $Worktype = $_GET['Worktype'];
    $Typ = $_GET['desig'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $sql = "SELECT type_code,Wtype,FWFlg FROM vwMas_WorkType_App where division_code= '". $Owndiv ."' and SFTyp=(select SF_Type from Mas_Salesforce where sf_Code='".$sfCode."') and  type_code= '". $Worktype ."'";
    $RSr = performQuery($sql);
    $WNm= "";$WFlg= "";
    if(count($RSr)>0){
        $Worktype= $RSr[0]['type_code'];
        $WNm= $RSr[0]['Wtype'];
        $WFlg= $RSr[0]['FWFlg'];
    }
    else
    {
        $sql = "SELECT type_code,Wtype,FWFlg FROM vwMas_WorkType_App where division_code= '". $Owndiv ."' and SFTyp=(select SF_Type from Mas_Salesforce where sf_Code='".$sfCode."') and    Wtype= '". $Worktype ."'";
        $RSr = performQuery($sql);

        $Worktype= $RSr[0]['type_code'];
        $WNm= $RSr[0]['Wtype'];
        $WFlg= $RSr[0]['FWFlg'];
    }
    $sql = "insert into tbMyDayPlan select '" . $sfCode . "','','".$vals['misseddate']."','','".$Remarks."','" . $Owndiv . "','" . $Worktype . "','" . $WFlg . "','','','','','','','','',''";
    performQuery($sql);
    $sql = "exec  svDCRMain_App '" . $sfCode . "','".$vals['misseddate']."','" . $Worktype . "','','".$Owndiv."','".$Remarks."',''";
    performQuery($sql);
    $resp["success"] = true;
    echo json_encode($resp);
    die;
                
    break;



        case "LeaveForm":
             $name = $_GET['sf_name'];
            $divCode = $_GET['divisionCode'];
            $sql = "SELECT LeaveAppproval FROM Access_Master where division_code= '". str_replace(",", "",$divCode)."'";
            $LA = performQuery($sql);
            $leaveType= $LA[0]['LeaveAppproval'];
            $lvappp=2;
            if($leaveType==1) $lvappp=0;
        
            $sql = "SELECT isNull(max(Leave_Id),0)+1 as RwID FROM Mas_Leave_Form";
            $tRw = performQuery($sql);
            $pk = (int) $tRw[0]['RwID'];
            $sql = "insert into Mas_Leave_Form(Leave_Id,Leave_Type,From_Date,To_Date,Reason,sf_code,Division_Code,Leave_Active_Flag,Created_Date,No_of_Days,Address) select '$pk'," . $vals['Leave_Type'] . ",'" . $vals['From_Date'] . "'," . $vals['To_Date'] . "," . $vals['Reason'] . ",'$sfCode','$Owndiv','".$lvappp."','" . date('Y-m-d') . "'," . $vals['No_of_Days'] . "," . $vals['address'] . "";
            performQuery($sql);
            if($leaveType==1){
                $sql = "SELECT sf_type FROM Mas_Salesforce where Sf_Code='$sfCode'";

                $sfType = performQuery($sql);
                $days = $vals['No_of_Days'];
                $date = $vals['From_Date'];
                for ($i = 1; $i <= $days; $i++) {
                    $query = "exec ChkandPostLeaveDt 0,'$sfCode'," . $sfType[0]['sf_type'] . ",$Owndiv,'$date','','apps'";
                    $results = performQuery($query);
                    $date = date('Y-m-d', strtotime($date . ' + 1 days'));
                }

       $sql = "select a.*,b.Leave_SName from MasEntitlement  a inner join mas_Leave_Type   b ON a.LeaveCode = b.Leave_code where a.SFCode='$sfCode' and DivisionCode='". str_replace(",", "",$divCode)."' and a.LeaveCode=" . $vals['Leave_Type'] . "";
            $LeaveAvailability = performQuery($sql);
            $LAC= $LeaveAvailability[0]['LeaveAvailability']-1;
 
     $query = "update MasEntitlement set LeaveAvailability='".$LAC."' where  SFCode='$sfCode' and  DivisionCode='". str_replace(",", "",$divCode)."'  and LeaveCode=" . $vals['Leave_Type'] . "";

      performQuery($query);





            }
            $sql = "SELECT DeviceRegId FROM Access_Table where sf_code=(select Reporting_To_SF from mas_salesforce where Sf_Code='$sfCode')";
            $device = performQuery($sql);
            $reg_id = $device[0]['DeviceRegId'];
            if (!empty($reg_id)) {
                if($leaveType==1)
                    $msg = "Leave Application Received";
                else
                    $msg = "Leave Application Received For Approval";
                send_gcm_notify($reg_id, $msg,"Leave Application");
            }

            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "DCRApproval":
            $date = $_GET['date'];
            $code = $_GET['code'];
            $date = str_replace('/', '-', $date);
            $date = date('Y-m-d', strtotime($date));
            $sql = "exec ApproveDCRByDt '" . $code . "','$date'";
            performQuery($sql);
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "DCRReject":
            $date = $_GET['date'];
            $code = $_GET['code'];
            $date = str_replace('/', '-', $date);
            $date = date('Y-m-d', strtotime($date));
            $sql = "update DCRMain_Temp set Confirmed=2,ReasonforRejection=" . $vals['reason'] . " where Sf_Code='$code' and Activity_Date='$date'";
            performQuery($sql);
            $resp["success"] = true;
            echo json_encode($resp);
            die;
            break;
        case "OrderReturn":
            $sql = "SELECT isNull(max(Trans_SlNo),0)+1 as RwID FROM SalesReturn_Head";
            $tRw = performQuery($sql);
            $pk = (int) $tRw[0]['RwID'];
            $sql = "insert into SalesReturn_Head select ".$pk.",".$pk.",'" . $sfCode . "','" . $vals["StkId"] . "','" . $vals["DrId"] . "','" . date('Y-m-d H:i:s') . "','0',0";
            performQuery($sql);
            $Clim="N";
            $tVal=0;
            for($il=0;$il<count($vals["RetProducts"]);$il++)
            {
                $PDets=$vals["RetProducts"][$il];

                $sql = "SELECT Division_Code FROM Mas_Product_Detail where Product_Detail_Code='" . $PDets["id"] . "'";
                $tRw = performQuery($sql);
                $Divc = (int) $tRw[0]['Division_Code'];

                if($PDets["Qty"]=="" || $PDets["Qty"]==null) $PDets["Qty"]=0;
                if($PDets["PQty"]=="" || $PDets["PQty"]==null) $PDets["PQty"]=0;

                if($PDets["QType"]=="" || $PDets["QType"]==null) $PDets["QType"]="D";
                if($PDets["ClaimType"]=="" || $PDets["ClaimType"]==null) $PDets["ClaimType"]="N";
                if($PDets["remark"]=="" || $PDets["remark"]==null) $PDets["remark"]="";
   if($PDets["S_name"]=="" || $PDets["S_name"]==null) $PDets["S_name"]="";

if($PDets["damagebatch"]=="" || $PDets["damagebatch"]==null) $PDets["damagebatch"]="";
if($PDets["damageorderdate"]=="" || $PDets["damageorderdate"]==null) $PDets["damageorderdate"]="";

                
                $sql = "insert into SalesReturn_Detail select ".$pk.",m.Product_Detail_Code," . $PDets["Qty"] . "," . 
                        $PDets["PQty"] . ",Sample_Erp_Code,isnull(RetailCasePrice,0)RCP,isnull(Retailor_Price,0) RPP,isnull(DistCasePrice,0) DCP,isnull(Distributor_Price,0) DPP ".
                        ",'" . $PDets["S_name"] . "','" . $PDets["ClaimType"] . "','" . $PDets["remark"] . "','" . $PDets["damagebatch"] . "' ,'" . $PDets["damageorderdate"] . "' , '" . $PDets["QType"] . "' from Mas_Product_Detail m left outer join vwProductStateRates v on m.Product_Detail_Code=v.Product_Detail_Code and m.Division_Code=v.Division_Code ".
                        " and v.State_Code=".$SFState." Where m.Product_Detail_Code='" . $PDets["id"] . "'";


                performQuery($sql);

                $rQty=$PDets["Qty"];
                $rPQty=$PDets["PQty"];
                
                if($PDets["Qty"]=="" || $PDets["Qty"]==null) $rQty=0;
                if($PDets["PQty"]=="" || $PDets["PQty"]==null) $rPQty=0;

                $sql = "select (" . $PDets["Qty"] . "*isnull(Sample_Erp_Code,1))+" . $PDets["PQty"] . " PicQty from Mas_Product_Detail m Where m.Product_Detail_Code='" . $PDets["id"] . "'";
                $ar = performQuery($sql);
                $Qtys=$ar[0]["PicQty"];

                //$Qtys=($rQty*$PDets["conversionQty"])+$rPQty;
                $sql = "exec UpdRetReturnStock '".$pk."','" . $vals["StkId"] . "','" . $PDets["id"] . "','" . date('Y-m-d H:i:s') . "','".$PDets["QType"]."'," . $Qtys . ",".$Divc.",'',''";

                performQueryWP($sql,[]);
                
                if($PDets["ClaimType"]=="Y"){
                    $sql = "select " . $Qtys . "*isnull(cast(Retailor_Price as float),0) Val ".
                        " from Mas_Product_Detail m left outer join vwProductStateRates v on m.Product_Detail_Code=v.Product_Detail_Code and m.Division_Code=v.Division_Code ".
                        " and v.State_Code=".$SFState." Where m.Product_Detail_Code='" . $PDets["id"] . "'";

                    performQuery($sql);

                    $oval = performQuery($sql);
                    $tVal = $tVal+$oval[0]['Val'];
                    $Clim="Y";
                }
            }
            if($Clim=="Y"){
                $sql = "insert into Customer_Ledger_Details select '" . $vals["DrId"] . "',".$tVal.",'C',getDate(),'Return',''";
                performQuery($sql);
            }

            break;
        case "DailyInventory":
            $mods=$vals["svMode"];
            $tblnm="Trans_Prod_DailyInventory";
			$SFCd=$vals["SF"];
			if($vals["SF"]=="" || $vals["SF"]==null)
				$SFCd=$vals["sfCode"];
            if($mods=="26"){
                $tblnm="Trans_Prod_DailyEndInventory";
				
				
				$query = "select * from Trans_VanStock_Ledger where from_loc='" . $SFCd . "'  and convert(date,eDate)='" . date('Y-m-d 00:00:00') . "'    and EntryBy='DayEndInv'";
        $DEI = performQuery($query);
			
        if (count($DEI) > 0) {
		
		   $result = array();
            $result['Msg'] = 'Daily End Inventory Already Submitted';
            $result["success"] = true;
            outputJSON($result);
            die; 	
			
			
		}
		 
            }

            $SFCd=$vals["SF"];
            

            $sql = "delete FROM ". $tblnm ." where SFCode='" . $SFCd . "' and EntryDt='" . date('Y-m-d 00:00:00') . "'";
            $tRw = performQuery($sql);
            $pk = (int) $tRw[0]['RwID'];

 


            for($il=0;$il<count($vals["InvProducts"]);$il++)
            {

                $PDets=$vals["InvProducts"][$il];

                $sql = "SELECT Division_Code FROM Mas_Product_Detail where Product_Detail_Code='" . $PDets["id"] . "'";
                $tRw = performQuery($sql);
                $Divc = (int) $tRw[0]['Division_Code'];

                $sql = "SELECT isNull(max(SLNo),0)+1 as RwID FROM ". $tblnm;
                $tRw = performQuery($sql);
                $pk = (int) $tRw[0]['RwID'];

                if($PDets["Totalqty"]=="") $PDets["Totalqty"]=0;
				 if($PDets["Qty"]=="") $PDets["Qty"]=0;
                if($PDets["PQty"]=="") $PDets["PQty"]=0;
                if($vals["AprlFlag"]=="" || $vals["AprlFlag"]==null) $vals["AprlFlag"]=0;


                $sql = "insert into ". $tblnm ." select ".$pk.",'" . $SFCd . "','" . date('Y-m-d 00:00:00') . "',m.Product_Detail_Code," . $PDets["Qty"] . "," . 
                            $PDets["PQty"] . ",Sample_Erp_Code,isnull(RetailCasePrice,0)RCP,isnull(Retailor_Price,0) RPP,isnull(DistCasePrice,0) DCP,isnull(Distributor_Price,0) DPP,'" . $vals["AprlFlag"] . "','" . $vals["StkId"] . "','" . $vals["StkNm"] . "'".
                            "," . $PDets["Totalqty"] . " from Mas_Product_Detail m left outer join vwProductStateRates v on m.Product_Detail_Code=v.Product_Detail_Code and m.Division_Code=v.Division_Code ".
                            " and v.State_Code=".$SFState." Where m.Product_Detail_Code='" . $PDets["id"] . "'";
                performQuery($sql);

                if($mods=="27"){
                    $sql = "select (" . $PDets["Qty"] . "*isnull(Sample_Erp_Code,1))+" . $PDets["PQty"] . " PicQty from Mas_Product_Detail m Where m.Product_Detail_Code='" . $PDets["id"] . "'";
                    $ar = performQuery($sql);
                    $Qtys=$ar[0]["PicQty"];
    
                    $sql = "exec UpdVanStock '".$pk."','" . $vals["StkId"] . "','D','" . $SFCd . "','F','" . date('Y-m-d 00:00:00') . "','" . $PDets["id"] . "','G'," . $Qtys . ",".$Divc.",'" . $vals["StkNm"] . "',''";
                    performQueryWP($sql,[]);
					
					
					
                }
                if($mods=="26"){
                    $sql = "select (" . $PDets["Qty"] . "*isnull(Sample_Erp_Code,1))+" . $PDets["PQty"] . " PicQty from Mas_Product_Detail m Where m.Product_Detail_Code='" . $PDets["id"] . "'";
                    $ar = performQuery($sql);
                    $Qtys=$ar[0]["PicQty"];
    
                    $sql = "exec UpdVanCLStock '".$pk."','" . $SFCd . "','F','" . $vals["StkId"] . "','D','" . date('Y-m-d 00:00:00') . "','" . $PDets["id"] . "','G'," . $Qtys . ",".$Divc.",'" . $vals["StkNm"] . "',''";
                    performQueryWP($sql,[]);
                }

            }
            if($mods=="24"){
                $sql = "SELECT DeviceRegId FROM Access_Table where sf_code=(select Reporting_To_SF from mas_salesforce where Sf_Code='" . $SFCd . "')";
                $device = performQuery($sql);
                $reg_id = $device[0]['DeviceRegId'];
                if (!empty($reg_id)) {
                    $msg = "Daily Inventory Approval Received";
                    send_gcm_notify($reg_id, $msg,"Daily Inventory");
                }
            }
            if($mods=="27"){
                $sql = "SELECT DeviceRegId FROM Access_Table where sf_code='" . $SFCd . "'";
                $device = performQuery($sql);
                $reg_id = $device[0]['DeviceRegId'];
                if (!empty($reg_id)) {
                    //   $msg = $name . " Applied Leave for " . $vals['No_of_Days'] . " days";
                    $msg = "Your Daily Inventory Approved...";
                    send_gcm_notify($reg_id, $msg,"Daily Inventory");
                }
            }
			
			
            break;
        case "InvoiceEntry":
            $SFCd = $_GET['sfCode'];

            $PDet="<ROOT>";
            $TaxDet="<ROOT>";
            for($il=0;$il<count($vals["productSelectedList"]);$il++)
            {
                $PDets=$vals["productSelectedList"][$il];
                $PDet=$PDet."<Prod PCode=\"".$PDets["product"]."\" PName=\"".$PDets["product_Nm"]."\" PRate=\"".$PDets["Rate"]."\" Disc=\"".$PDets["discount"]."\" Free=\"".$PDets["free"]."\" PQty=\"".$PDets["rx_qty"]."\" PValue=\"".$PDets["sample_qty"]."\" />";
                $TxDet=$PDets["taxDet"];
                for($ij=0;$ij<count($TxDet);$ij++)
                {
                    $TaxDet=$TaxDet."<TaxDet TaxId=\"".$TxDet[$ij]["txId"]."\" TaxName=\"".$TxDet[$ij]["txName"]."\" TaxAmt=\"".$TxDet[$ij]["txAmt"]."\" PCode=\"". $PDets["product"] ."\" />";
                    
                }
            }
            $PDet=$PDet."</ROOT>";
            $TaxDet=$TaxDet."</ROOT>";
            $pk = "0";

            $sql = "{call svVanInvoice(?,'" . $SFCd . "','" . $vals["stockist_code"] . "','" . $vals["stockist_name"] . "','" . $vals["id"] . "','" . $vals["name"] . "','" . $vals["vtime"] . "','" . $vals["BillDt"] . "','" . $vals["BillAmt"] . "','" . $vals["TaxAmt"] . "','" . $vals["Disc"] . "','" . $vals["NetAmt"] . "','','".$PDet."','".$TaxDet."','".$vals["Order_No"] ."')}";
            $params = array(array(&$pk, SQLSRV_PARAM_INOUT, SQLSRV_PHPTYPE_STRING(SQLSRV_ENC_CHAR), SQLSRV_SQLTYPE_VARCHAR(50)));
            performQueryWP($sql, $params);

                   /* echo($sql . "<br>");
                    outputJSON($params );*/
            for($il=0;$il<count($vals["productSelectedList"]);$il++)
            {
                $PDets=$vals["productSelectedList"][$il];

                $sql = "SELECT Division_Code FROM Mas_Product_Detail where Product_Detail_Code='" . $PDets["product"] . "'";
                $tRw = performQuery($sql);
                $Divc = (int) $tRw[0]['Division_Code'];

                $Qtys =$PDets["rx_qty"];
                $sql = "exec [UpdVanInvStock] '".$pk."','" . $SFCd . "','F','" . $vals["id"] . "','R','" . date('Y-m-d 00:00:00') . "','" . $PDets["product"] . "','G'," . $Qtys . ",".$Divc.",'','" . $vals["name"] . "'";
                performQueryWP($sql,[]);

                $FQtys =$PDets["free"];
                if($FQtys>0){
                    $sql = "exec [UpdVanInvStock] '".$pk."','" . $SFCd . "','F','" . $vals["id"] . "','R','" . date('Y-m-d 00:00:00') . "','" . $PDets["product"] . "','G'," . $FQtys . ",".$Divc.",'','" . $vals["name"] . " -- FREE'";
                    performQueryWP($sql,[]);
                }
            }
            

            $result = array();
            $result['InvNo'] = $pk;
            $result["success"] = true;
            outputJSON($result);
            die;   
            break;
            
            
            
            
               case "CallPreviewEkey":
                
                
                echo $dataqtwtwetwetewt[0];
        $data1 = json_decode($_POST['data'], true);
          $sfCode = $_GET['sfCode'];
          $divCode = $_GET['divisionCode'];
             $CallPreviewEkey=$data[0]['CallPreviewEkey'];
          
    $result = array();
             $query = "select Trans_Sl_No,(Order_Value+isnull(discount_price,0)) Order_Value ,Order_Value netamount  from Trans_Order_Head where  OrderID='".$CallPreviewEkey."'  union  select Trans_Sl_No,Order_Value,0 from Trans_PriOrder_Head where  OrderID='".$CallPreviewEkey."' union select Trans_Sl_No,Order_Value,0 from Trans_Pragnancy__Head where  OrderID='".$CallPreviewEkey."' ";
           
$Output=performQuery($query);
             
          $result['Output']=$Output; 
           $result['success']=true; 
               outputJSON($result);
die;   
 

        break;
            
            
            
            
            
            
            
        case "DOOR_TO_DOOR":
            $divCode = $_GET['divisionCode'];
            $State_Code = $_GET['State_Code'];
            $PERSIONID=1;
            $Current_Date_And_Time =date("Y-m-d");
            $FWFlg=$data[2]['FWFlg'];
            $Remarks=$data[1]['Remarks'];
            $copen=$data[3]['No0fcoupen'];
            $Retailer=$data[4]['Retailer'];
            $Distributors=$data[5]['Distributors'];
            $Route=$data[6]['Route'];
             
             
            if( $data[count($data)-1]['ActivityCaptures']!=null && $data[count($data)-1]['ActivityCaptures']!="undefined")
            {
                $Event_Captures = $data[count($data)-1]['ActivityCaptures'];
                for ($j = 0; $j < count($Event_Captures); $j++) {                       
                    $ev_imgurl=str_replace("'", "",$Event_Captures[$j]["imgurl"]);
                    $ev_title=str_replace("'", "",$Event_Captures[$j]["title"]);
                    $ev_remarks=str_replace("'", "",$Event_Captures[$j]["remarks"]);
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,DCRDetNo,imgurl,title,remarks,Division_Code,Identification,Insert_Date_Time) values( '".$State_Code."','".$sfCode."','" . $ev_imgurl . "','" . $ev_title . "','" . $ev_remarks . "','". str_replace(",", "",$divCode)."','DTD','".$Current_Date_And_Time."')";
                    performQuery($sql); 
                }
            }
 
            for ($il = 0;$il < count($data[0]['DOOR_TO_DOOR']);$il++) {
                $Pendingbills = $data[0]['DOOR_TO_DOOR'][$il];
                $PromotorName = str_replace("'", "", $Pendingbills["PromotorName"]);
                 $Place = str_replace("'", "", $Pendingbills["Place"]);

                $Taken = str_replace("'", "", $Pendingbills["Taken"]);
                $Issue = str_replace("'", "", $Pendingbills["Issue"]);
                $Stime = str_replace("'", "", $Pendingbills["Stime"]);
                $Etime = str_replace("'", "", $Pendingbills["Etime"]);
                
                
                $sql="insert into Trans_Door_To_Door (PersonID,sfCode, SfName, State_Code, Division_Code, Promotorname, place, taken, issue,starttime,endtime,Remarks,Retailer,Current_Date_And_Time,Distributors,Route)
                        values (  '".$copen."', '".$sfCode."','".$FWFlg."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$PromotorName."','".$Place."','".$Taken."','".$Issue."','".$Stime."','".$Etime."','".$Remarks."','".$Retailer."','".$Current_Date_And_Time."','".$Distributors."','".$Route."')";
                performQuery($sql);
                 
            }
            break;
        case "Inshop_Activity":
            $divCode = $_GET['divisionCode'];
            $State_Code = $_GET['State_Code'];
            $FWFlg=$data[2]['FWFlg'];
            $Remarks=$data[1]['Remarks'];
            $Current_Date_And_Time =date("Y-m-d");
            $Current_Date_And_only =date("Y-m-d");
            $retailername=$data[5]['RetailerName'];
            $Distributors=$data[6]['Distributors'];
            $Route=$data[7]['Route'];
            $Identification='Inshop_Activity';
            if( $data[3]['ActivityCaptures']!=null && $data[3]['ActivityCaptures']!="undefined")
            {
                $Event_Captures = $data[3]['ActivityCaptures'];
                for ($j = 0; $j < count($Event_Captures); $j++) {                       
                    $ev_imgurl=str_replace("'", "",$Event_Captures[$j]["imgurl"]);
                    $ev_title=str_replace("'", "",$Event_Captures[$j]["title"]);
                    $ev_remarks=str_replace("'", "",$Event_Captures[$j]["remarks"]);
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,DCRDetNo,imgurl,title,remarks,Division_Code,Identification,Insert_Date_Time) values( '" . $sfCode . "','" . $State_Code . "','" . $ev_imgurl . "','" . $ev_title . "','" . $ev_remarks . "','". str_replace(",", "",$divCode)."','" . $Identification . "','" . $Current_Date_And_Time . "')";
                    performQuery($sql); 
                }
            }

            for ($il = 0;$il < count($data[0]['Inshop_Activity']);$il++) {
                $Pendingbills = $data[0]['Inshop_Activity'][$il];
                $PromotorName = str_replace("'", "", $Pendingbills["PromotorName"]);
                 
                $Issue = str_replace("'", "", $Pendingbills["Value"]);
                $Stime = str_replace("'", "", $Pendingbills["Stime"]);
                $Etime = str_replace("'", "", $Pendingbills["Etime"]);
                
                $sql="insert into Trans_Inshop_Activity (sfCode, SfName, State_Code, Division_Code, Promotorname,issue,starttime,endtime,Remarks,Retailer_Name,Distributors,Route,InsertDate)
                        values (  '".$sfCode."','".$FWFlg."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$PromotorName."','".$Issue."','".$Stime."','".$Etime."','".$Remarks."','".$retailername."','".$Distributors."','".$Route."','" . $Current_Date_And_only . "')";
                performQuery($sql);
                 
            }
              
            for ($il = 0;$il < count($data[4]['Inshop_Activity_Orders']);$il++) {
                $Inshop_Activity_Orders = $data[4]['Inshop_Activity_Orders'][$il];
                $product_code = str_replace("'", "", $Inshop_Activity_Orders["product_code"]);
                 
                $Product_Rx_Qty = str_replace("'", "", $Inshop_Activity_Orders["Product_Rx_Qty"]);
                $Product_Sample_Qty = str_replace("'", "", $Inshop_Activity_Orders["Product_Sample_Qty"]);
                $Rate = str_replace("'", "", $Inshop_Activity_Orders["Rate"]);
                
                
                $sql="insert into Trans_Inshop_Activity_Orders (sfCode, SfName, State_Code, Division_Code, product_code,Product_Rx_Qty,Product_Sample_Qty,Rate,Insert_Date_And_time,Retailer_Name)
                        values (  '".$sfCode."','".$FWFlg."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$product_code."','".$Product_Rx_Qty."','".$Product_Sample_Qty."','".$Rate."','" . $Current_Date_And_Time . "','" . $retailername . "')";
                performQuery($sql);
            }
            break;
            
                case "Supplier_Master":
                        $divCode = $_GET['divisionCode'];
                        $State_Code = $_GET['State_Code'];
                    
                        $Remarks=$data[1]['Remarks'];
                        $Current_Date_And_Time =date("Y-m-d");
                       
                        $SupplierName=$data[3]['SupplierName'];
                        
                        $Route=$data[4]['Route'];
                       
                        if (!$data[2]['Supplier_Master_Orders']) {
                 $sql="insert into SupplierMaster_Order (sfCode, State_Code, Division_Code,Insert_Date_And_time,Suppliername,Route,Remarks)
                                    values (  '".$sfCode."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$Current_Date_And_Time."','".$SupplierName."','".$Route."','".$Remarks."')";
                            performQuery($sql);
            
        
            } else {
                for ($il = 0;$il < count($data[2]['Supplier_Master_Orders']);$il++) {
                            $Supplier_Master_Orders = $data[2]['Supplier_Master_Orders'][$il];
                            $product_code = str_replace("'", "", $Supplier_Master_Orders["product_code"]);
                             
                            $Product_Rx_Qty = str_replace("'", "", $Supplier_Master_Orders["Product_Rx_Qty"]);
                            $Product_Sample_Qty = str_replace("'", "", $Supplier_Master_Orders["Product_Sample_Qty"]);
                            $Rate = str_replace("'", "", $Supplier_Master_Orders["Rate"]);
                            
                            
                            $sql="insert into SupplierMaster_Order (sfCode, State_Code, Division_Code, product_code,Product_Rx_Qty,Product_Sample_Qty,Rate,Insert_Date_And_time,Suppliername,Route,Remarks)
                                    values (  '".$sfCode."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$product_code."','".$Product_Rx_Qty."','".$Product_Sample_Qty."','".$Rate."','" . $Current_Date_And_Time . "','".$SupplierName."','".$Route."','".$Remarks."')";
                            performQuery($sql);
        
                        }
            }
                          
                        
                        break;
            
            case "Inshop_Activity_CheckIn":
            $divCode = $_GET['divisionCode'];
            $State_Code = $_GET['State_Code'];
            $FWFlg=$data[2]['FWFlg'];
            $Remarks=$data[1]['Remarks'];
            $Current_Date_And_only =date("Y-m-d");
            $retailername=$data[5]['RetailerName'];
            $Distributors=$data[6]['Distributors'];
            $Route=$data[7]['Route'];
            $Identification='Inshop_Activity_CheckIn';
            if( $data[3]['ActivityCaptures']!=null && $data[3]['ActivityCaptures']!="undefined")
            {
                $Event_Captures = $data[3]['ActivityCaptures'];
                for ($j = 0; $j < count($Event_Captures); $j++) {                       
                    $ev_imgurl=str_replace("'", "",$Event_Captures[$j]["imgurl"]);
                    $ev_title=str_replace("'", "",$Event_Captures[$j]["title"]);
                    $ev_remarks=str_replace("'", "",$Event_Captures[$j]["remarks"]);
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,DCRDetNo,imgurl,title,remarks,Division_Code,Identification,Insert_Date_Time) values( '" . $sfCode . "','" . $State_Code . "','" . $ev_imgurl . "','" . $ev_title . "','" . $ev_remarks . "','". str_replace(",", "",$divCode)."','" . $Identification . "','" . $Current_Date_And_Time . "')";
                    performQuery($sql); 
                }
            }

            for ($il = 0;$il < count($data[0]['Inshop_Activity_CheckIn']);$il++) {
                $Pendingbills = $data[0]['Inshop_Activity_CheckIn'][$il];
               
                 
                
                $Stime = str_replace("'", "", $Pendingbills["Stime"]);
                $Etime = str_replace("'", "", $Pendingbills["Etime"]);
                
                $sql="insert into Trans_Inshop_Activity_CheckIn (sfCode, SfName, State_Code, Division_Code,starttime,endtime,Remarks,Retailer_Name,Distributors,Route,InsertDate)
                        values (  '".$sfCode."','".$FWFlg."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$Stime."','".$Etime."','".$Remarks."','".$retailername."','".$Distributors."','".$Route."','" . $Current_Date_And_only . "')";
                performQuery($sql);
                 
            }
              
            for ($il = 0;$il < count($data[4]['Inshop_Activity_Orders']);$il++) {
                $Inshop_Activity_Orders = $data[4]['Inshop_Activity_Orders'][$il];
                $product_code = str_replace("'", "", $Inshop_Activity_Orders["product_code"]);
                 
                $cb_qty = str_replace("'", "", $Inshop_Activity_Orders["cb_qty"]);
                $Product_Sample_Qty = str_replace("'", "", $Inshop_Activity_Orders["Product_Sample_Qty"]);
                $Rate = str_replace("'", "", $Inshop_Activity_Orders["Rate"]);
                
                
                $sql="insert into Trans_Inshop_CheckIn_Orders (sfCode, SfName, State_Code, Division_Code, product_code,Product_Rx_Qty,Product_Sample_Qty,Rate,Insert_Date_And_time,Retailer_Name)
                        values (  '".$sfCode."','".$FWFlg."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$product_code."','".$cb_qty."','".$Product_Sample_Qty."','".$Rate."','" . $Current_Date_And_Time . "','" . $retailername . "')";
                performQuery($sql);
            }
            break;
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
        case "NewContact":
             $divCode = $_GET['divisionCode'];
            


            $State_Code = $_GET['State_Code'];
             
            $FormarName=$data[1]['FormarName'];
            $ContactNumber=$data[2]['ContactNumber'];
            $Remarks=$data[3]['Remarks'];
            $Seeman=$data[4]['Seeman'];
            $Address=$data[5]['Address'];
            $Catogoryformer=$data[6]['Catogoryformer'];
            $Crop=$data[7]['Crop'];
            $Route=$data[8]['Route'];
            $Breed=$data[9]['Breed'];
            $Class=$data[10]['Class'];
            $Ukey=$data[11]['Ukey'];
            $unlisted_cat_code="null";

      $temp = array_keys($data[1]);
   
    $Field = $data[1][$temp[0]];




            $Current_Date_And_Time =date("Y-m-d");

            
           

$sql = "{call  svListedDR_APP('$sfCode','".$Field["FormarName"]."','".$Field["Address"]."','" . $Field["ContactNumber"] . "','".$Field["CityName"]."','".$Field["AreaName"]."','', '','','',".$Field["Catogoryformer"].",".$unlisted_cat_code.",'".$Field["Route"]."','0','" . date('Y-m-d H:i:s') . "',". str_replace(",", "",$divCode)."," .$Field["Class"]. ",0,'','','','','','','','".$Field["Landmark"]."','" . str_replace("'","",$Field["Ukey"]) . "','".$Field["Seeman"]."','".$Field["Breed"]."','".$Field["Crop"]."')}";
performQueryWP($sql,[]);

$sql = "SELECT ListedDrCode from Mas_ListedDr where Ukey=".$Field["Ukey"];
                    $res = performQuery($sql);
                    if (count($res) > 0){
                        $cCode=$res[0]["ListedDrCode"];
                    }
	$sql="insert into NewContact_Dr (ListedDrCode,Ukey,FormarName, DFDairyMP, MonthlyAI,AITCU,AITMP,MCCNFPM,MCCMilkColDaily,VetsMP,CreatedDate,CustomerCategory,PotentialFSD,CurrentlyUFSD,FrequencyOfVisit)
                            values ('".$cCode."','" . str_replace("'","",$Field["Ukey"]) . "', '".$Field["FormarName"]."','". $Field["DFDairyMP"]."','". $Field["MonthlyAI"]."','".$Field["AITCU"]."', '".$Field["AITMP"]."', '".$Field["MCCNFPM"]."','".$Field["MCCMilkColDaily"]."','".$Field["VetsMP"]."',   '" . date('Y-m-d H:i:s') . "',  '".$Field["Catogoryformer"]."','".$Field["DFPFSDM"]."','".$Field["CUFSD"]."','".$Field["FrequencyOfVisit"]."')";
 performQuery($sql);


            if($data[2]['GIFTCARD']!=1){
                    


                    $sql="insert into Trans_Gift_Issue_Details (Sf_code,Division_Code, Name, Ukey,Additional_Prod_Dtls,Created_Date)
                            values ('$sfCode',  ". str_replace(",", "",$divCode).",'". $Field["FormarName"]."','" . str_replace("'","",$Field["Ukey"]) . "','".$data[2]['GIFTCARD']."','" . date('Y-m-d H:i:s') . "')";
                    performQuery($sql);
}
            
            break;

    case "FieldDemo_Activity_Orders":
            $divCode = $_GET['divisionCode'];
            
            $State_Code = $_GET['State_Code'];
             
            $FormarName=$data[1]['FormarName'];
            $ContactNumber=$data[2]['ContactNumber'];
            $Remarks=$data[3]['Remarks'];
            $Current_Date_And_Time =date("Y-m-d");

            $sql = "SELECT isNull(max(ID),0)+1 as RwID FROM Trans_Demo_Head";
            $tRw = performQuery($sql);
            $pk = (int) $tRw[0]['RwID']; 


            $sql="insert into Trans_Demo_Head(ID,sf_code, division_code,formarname,contactnumber,remarks,current_dateand_time)
                values ('".$pk."','".$sfCode."','". str_replace(",", "",$divCode)."','".$FormarName."','".$ContactNumber."','".$Remarks."','".$Current_Date_And_Time."')";
            performQuery($sql);
            
            for ($il = 0;$il < count($data[0]['FieldDemo_Activity_Orders']);$il++) {
                $FieldDemo_Activity_Orders = $data[0]['FieldDemo_Activity_Orders'][$il];
                $product_code = str_replace("'", "", $FieldDemo_Activity_Orders["product_code"]);
                $Product_Rx_Qty = str_replace("'", "", $FieldDemo_Activity_Orders["Product_Rx_Qty"]);
                $Rate = str_replace("'", "", $FieldDemo_Activity_Orders["Rate"]);
                
                
                $sql="insert into Trans_Demo_Samples (TransID,sf_code, division_code, productcode,qty,current_dateand_time)
                        values ('".$pk."',  '".$sfCode."','". str_replace(",", "",$divCode)."','".$product_code."','".$Product_Rx_Qty."','" . $Current_Date_And_Time . "')";
                performQuery($sql);
            }
            break;




        case "BatteryStatus":
            $divCode = $_GET['divisionCode'];
            $State_Code = $_GET['State_Code'];
            $Current_Date_Time=date("Y-m-d h:i:sa");
            $BatteryStatus=$data[0]['BatteryStatus'];
            $sql="insert into Trans_Battery_Status (sf_code,Div_Code,DateandTime,Battery_Status) values ('".$sfCode."','". str_replace(",", "",$divCode)."','".$Current_Date_Time."' ,'".$BatteryStatus."' )";
            performQuery($sql);
            break;
        case "Product_Display_Activity":
            $divCode = $_GET['divisionCode'];
            $State_Code = $_GET['State_Code'];
            $Current_Date_And_Time =date("Y-m-d");
            $FWFlg=$data[2]['FWFlg'];
            $Remarks=$data[1]['Remarks'];
            $copen=$data[3]['No0fcoupen'];
            $Retailer=$data[4]['Retailer'];
            $Distributors=$data[5]['Distributors'];
            $Route=$data[6]['Route'];
            $FieldForceName=$data[7]['FieldForceName'];
            $HQSfCode=$data[8]['HQSfCode'];
            if( $data[3]['ActivityCaptures']!=null && $data[3]['ActivityCaptures']!="undefined")
            {
                $Event_Captures = $data[3]['ActivityCaptures'];
                for ($j = 0; $j < count($Event_Captures); $j++) {                       
                    $ev_imgurl=str_replace("'", "",$Event_Captures[$j]["imgurl"]);
                    $ev_title=str_replace("'", "",$Event_Captures[$j]["title"]);
                    $ev_remarks=str_replace("'", "",$Event_Captures[$j]["remarks"]);
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,DCRDetNo,imgurl,title,remarks,Division_Code,Identification,Insert_Date_Time) values( '".$State_Code."','".$sfCode."','" . $ev_imgurl . "','" . $ev_title . "','" . $ev_remarks . "','". str_replace(",", "",$divCode)."','PD','".$Current_Date_And_Time."')";
                    performQuery($sql); 
                }
            }

            for ($il = 0;$il < count($data[0]['Product_Display_Activity']);$il++) {
                $Pendingbills = $data[0]['Product_Display_Activity'][$il];
                $SDate = str_replace("'", "", $Pendingbills["SDate"]);
                $EDate = str_replace("'", "", $Pendingbills["EDate"]);
                $CurrentVolume = str_replace("'", "", $Pendingbills["CurrentVolume"]);
                $AddVolume = str_replace("'", "", $Pendingbills["AddVolume"]);
                $DiscountAmount = str_replace("'", "", $Pendingbills["DiscountAmount"]);
                $sql="insert into Trans_Product_Display (sfCode, SfName, State_Code, Division_Code, SDate, EDate, CurrentVolume, AddVolume,DiscountAmount,Current_Date_And_Time,Distributors,Route,Activation_Flag,Retailer,Remarks,FieldForceName,HQ_SfCode)
                        values ('".$sfCode."','".$FWFlg."', '".$State_Code."','". str_replace(",", "",$divCode)."','".$SDate."','".$EDate."','".$CurrentVolume."','".$AddVolume."','".$DiscountAmount."','".$Current_Date_And_Time."','".$Distributors."','".$Route."','false','".$Retailer."','".$Remarks."','".$FieldForceName."','".$HQSfCode."')";
                performQuery($sql);
                 
            }
            break;
        case "ProductDisplayApprovals":
            $sfCode = $_GET['sfCode'];
            $flag=$data[0]['ProductDisplayApprovals']['Flag'];
            $FieldforceSfcode=$data[0]['ProductDisplayApprovals']['Sf_Code'];
            $sql = "UPDATE Trans_Product_Display SET Activation_Flag = '".$flag."' WHERE HQ_SfCode = '".$sfCode."' and sfCode=  '".$FieldforceSfcode."'";
            performQuery($sql);
            break;
        case "ProductDisplayReject":
            $sfCode = $_GET['sfCode'];
            $flag=$data[0]['ProductDisplayReject']['Flag'];
            $FieldforceSfcode=$data[0]['ProductDisplayReject']['Sf_Code'];
            $RejectReson=$data[0]['ProductDisplayReject']['Reson'];
            $sql = "UPDATE Trans_Product_Display SET Activation_Flag = '".$flag."',RejectReson= '".$RejectReson."' WHERE HQ_SfCode = '".$sfCode."' and sfCode=  '".$FieldforceSfcode."'";
            performQuery($sql);
            break;
        case "tbRCPADetails":
            $sql = "insert into tbRCPADetails select '" . $sfCode . "','" . date('Y-m-d H:i:s') . "'," . $vals["RCPADt"] . "," .
                    $vals["ChmId"] . "," . $vals["DrId"] . "," . $vals["CmptrName"] . "," . $vals["CmptrBrnd"] . "," . $vals["CmptrPriz"] . "," .
                    $vals["ourBrnd"] . "," . $vals["ourBrndNm"] . "," . $vals["Remark"] . ",'" . $div . "'," . $vals["CmptrQty"] . "," . $vals["CmptrPOB"] . "," . $vals["ChmName"] . "," . $vals["DrName"];
            performQuery($sql);
            break;
        case "tbRemdrCall":
            $sql = "SELECT isNull(max(cast(replace(RwID,'RC/" . $IdNo . "/','') as numeric)),0)+1 as RwID FROM tbRemdrCall where RwID like 'RC/" . $IdNo . "/%'";
            $tRw = performQuery($sql);
            $pk = (int) $tRw[0]['RwID'];

            $sql = "insert into tbRemdrCall select 'RC/" . $IdNo . "/" . $pk . "','" . $sfCode . "','" . date('Y-m-d H:i:s') . "'," . $vals["Doctor_ID"] . "," .
                    $vals["WWith"] . "," . $vals["WWithNm"] . "," . $vals["Prods"] . "," . $vals["ProdsNm"] . "," . $vals["Remarks"] . "," .
                    $vals["location"] . ",'" . $div . "'";
            performQuery($sql);
            break;
         case "Activity_Report_APP":
            $sfCode;
            $div = $_GET['divisionCode'];
            $divs = explode(",", $div . ",");
            $divisionCode = (string) $divs[0];
            $eKeyID=$vals["eKey"];
            $today;
            if($vals["dcr_activity_date"]!=null && $vals["dcr_activity_date"]!=''){
          $today=str_replace("'", "", $vals["dcr_activity_date"]);
              

            }

            $sql = "SELECT * FROM vwActivity_Report where SF_Code='" . $sfCode . "' and lower(Work_Type) <>lower(" . $vals["Worktype_code"] . ")  and cast(activity_date as datetime)=cast('$today' as datetime)";
            $result1 = performQuery($sql);

            $datesumm=$today;

            $routewise="'".$data[1]['Activity_Doctor_Report']['routeWise']."'";
            $docor_code=str_replace("'", "", $data[1]['Activity_Doctor_Report']['doctor_code']);
            $dcr_activity_date=str_replace("'", "", $data[0]['Activity_Report_APP']['dcr_activity_date']);
            $datasf = str_replace("'", "", $vals["DataSF"]);

            // Pending Collection Part
            for ($il = 0;$il < count($data[6]['PENDING_Bills']);$il++) {
                $Pendingbills = $data[6]['PENDING_Bills'][$il];
                $Bill_No = str_replace("'", "", $Pendingbills["Bill_No"]);
                $Bill_Amount = str_replace("'", "", $Pendingbills["Bill_Amount"]);
                $Balance_Amount = str_replace("'", "", $Pendingbills["Balance_Amount"]);
                $Collected_Amount = str_replace("'", "", $Pendingbills["Collected_Amount"]);
                $Bill_Date = str_replace("'", "", $Pendingbills["Bill_Date"]);
                $date_for_database = date ("Y-m-d H:i:s", strtotime($Bill_Date));
                
                $query = "select coll_amt,bal_amt,billamt from mas_pending_bills where sf='" . $datasf . "' and billno='" . $Bill_No . "'";
                $pendinglistt = performQuery($query);
                if (count($pendinglistt) > 0) {
                    for ($i = 0;$i < count($pendinglistt);$i++) {
                        $new = $pendinglistt[$i]['coll_amt'];
                        $billamt=$pendinglistt[$i]['billamt'];
                        $date = date('m/d/Y h:i:s', time());
                        
                        $dateonlyyear=date('y/m/d');
                        if($Collected_Amount>0){
                            $sql = "SELECT isNull(max(cast(replace(Trans_SlNo,'RC/" . $IdNo . "/','') as numeric)),0)+1 as Trans_SlNo FROM Trans_CusBill_CollectionDet where Trans_SlNo like 'RC/" . $IdNo . "/%'";
                            $tRw = performQuery($sql);
                            $pk = (int) $tRw[0]['Trans_SlNo']; 
                            $query="INSERT INTO Trans_CusBill_CollectionDet   ( Trans_SlNo,Cust_Code, ColltDt, BillNo,BillDt,BillAmt,CollAmt,BalAmt,Division_Code,SF_CODE) VALUES (  '".$pk."','".$docor_code."', '".$date."', '".$Bill_No."', '".$date_for_database."', '".$billamt."', '".$Collected_Amount."','".$Balance_Amount."','".$divisionCode."','".$datasf."'  )";
                            performQuery($query);
                        }
                        
                        $query = "UPDATE mas_pending_bills  SET  coll_amt =coll_amt+" .$Collected_Amount . " , bal_amt =bal_amt-" .$Collected_Amount. ",lastupdateddate='".$date."'   where sf='" . $datasf . "' and billno='" . $Bill_No . "' ";
                        performQuery($query);
                    }
                }
            }
            //End Bill Collection Part
            //Checking Routwise Entry Exsists
            if($routewise=="'0'"){
                $townCode = $data[0]['Activity_Report_APP']['Town_code'];
                $sql = "SELECT activity.trans_slno FROM vwActivity_Report activity  inner join vwactivity_msl_details msl on msl.trans_slno=activity.trans_slno where activity.SF_Code='" . $sfCode . "' and cast(activity.activity_date as datetime)=cast('$today' as datetime) and sdp=$townCode";
                $res = performQuery($sql);
                if (count($res) > 0){
                    $result = array();
                    $result['type'] = 2;
                    $result['msg']  = "Already Call Exist..";
                    outputJSON($result);
                    die;   
                }           
            }
            //End Checking Routwise Entry Exsists
            
            $PPXML = '';$stk_code = '';$SUpNo = '';$SUpNm = '';$HPXML='';$pProd = '';$npProd = '';$pGCd = '';$pGNm = '';$pGQty = '';$SPProds = '';$nSPProds = '';$Inps = '';$nInps = '';$vTyp = 0;$DemoTo='';
            
            for ($i = 1; $i < count($data); $i++) {
                $tableData = $data[$i];
                if (isset($tableData['Activity_Doctor_Report'])) {
                    $vTyp = 1;
                    $sql = "SELECT sf_Designation_Short_Name FROM Mas_salesforce where Sf_Code='" . $sfCode . "' ";
            $Desig_Check = performQuery($sql);

if($Desig_Check[0]['sf_Designation_Short_Name']=='AIT'){
     $AITEST=9;
}
                    $DetTB = $tableData['Activity_Doctor_Report'];  
                   $POT=$DetTB["PhoneOrderTypes"];                  
                    if($DetTB['discount_price']==null)
                        $discount_price=0;
                    else
                        $discount_price =$DetTB['discount_price'];

                    if($DetTB['rateMode']==null)
                        $rateMode="Nil";
                    else
                        $rateMode = $DetTB['rateMode'];     

                    $routewise="'".$DetTB['routeWise']."'";
                    // if Routewise then create town as a Doctor name 
                    if($routewise=="'0'")
                    {
                        $sql="select * from Mas_ListedDr where EntryMode_Code=" . $DetTB['doctor_code'] . "";
                        $code=performQuery($sql);
                        $townCode = $data[0]['Activity_Report_APP']['Town_code'];
                        if(count($code)==0){
                                $sql = "{call  svListedDR_APP_Route('$sfCode'," . $DetTB['doctor_name'] . ",'test','',2,null,$townCode,'0','" . date('Y-m-d H:i:s') . "',$Owndiv,16,0,null,'',''," . $DetTB['doctor_code'] . ")}";
                                performQueryWP($sql,[]);    
                        }
                        $sql="select * from Mas_ListedDr where EntryMode_Code=" . $DetTB['doctor_code'] . "";
                        $code=performQuery($sql);
                        $DetTB['doctor_code']=$code[0]['ListedDrCode'];
                    }
                    
                    $cCode = $DetTB["doctor_code"];
                    $sql = "SELECT ListedDrCode from Mas_ListedDr where ListedDr_Profile=" . $cCode;
                    $res = performQuery($sql);
                    if (count($res) > 0){
                        $cCode=$res[0]["ListedDrCode"];
                    }
                    $vTm = $DetTB["Doc_Meet_Time"];
                    $pob = $DetTB["Doctor_POB"];
                    $POB_Value = $DetTB["orderValue"];
                    $DemoTo= $DetTB["demo_given_name"];
                    if($DetTB["net_weight_value"]==null||$DetTB["net_weight_value"]==''||$DetTB["net_weight_value"]=="null"||$DetTB["net_weight_value"]=="undefined"||empty($DetTB["net_weight_value"]))
                        $net_weight_value=0;
                    else
                        $net_weight_value = $DetTB["net_weight_value"];
                    $proc = "svDCRLstDet_App";
                    $sql = "SELECT Doctor_Name name from vwDoctor_Master_APP where Doctor_Code=" . $cCode;
                }
                if (isset($tableData['Activity_Chemist_Report'])) {
                        $vTyp = 2;
                        $DetTB = $tableData['Activity_Chemist_Report'];
                        $cCode = $DetTB["chemist_code"];
                        $vTm = $DetTB["Chm_Meet_Time"];
                        $pob = $DetTB["Chemist_POB"];
                        $sql = "SELECT Chemists_Name name from vwChemists_Master_APP where Chemists_Code=" . $cCode;
                }
                if (isset($tableData['Activity_Stockist_Report'])) {
                    $vTyp = 3;
                    $DetTB = $tableData['Activity_Stockist_Report'];
                    $cCode = $DetTB["stockist_code"];
                    $vTm = $DetTB["Stk_Meet_Time"];
                    $pob = $DetTB["Stockist_POB"];
                    $POT=$DetTB["PhoneOrderTypes"];
                    $Super_Stck_code=$DetTB["Super_Stck_code"];


                    //PhoneOrderTypes
 if($DetTB["Super_Stck_code"]==null||$DetTB["Super_Stck_code"]=="null"||$DetTB["Super_Stck_code"]=="undefined"||empty($DetTB["Super_Stck_code"])){

    $Super_Stck_code='';

 }



                    

         if (!empty($DetTB["doctor_id"]))
                    $doctor_id=$DetTB["doctor_id"];

if (!empty($DetTB["superstockistid"]))
$superstk=$DetTB["superstockistid"];

                    if( $DetTB["orderValue"]==null)
                        $POB_Value=0;
                    else
                        $POB_Value = $DetTB["orderValue"];
                    $sql = "SELECT stockiest_name name from vwstockiest_Master_APP where Distributor_Code=" . $cCode;
                    
                    if(!empty($DetTB["version"])){
                                $sql = "SELECT S_Name name from supplier_master where S_No=$doctor_id";
                                $vTyp = 8;

                }
                    
                }
                if (isset($tableData['Activity_UnListedDoctor_Report'])) {
                        $vTyp = 4;
                        $DetTB = $tableData['Activity_UnListedDoctor_Report'];
                        $cCode = $DetTB["uldoctor_code"];
                        $vTm = $DetTB["UnListed_Doc_Meet_Time"];
                        $pob = $DetTB["UnListed_Doctor_POB"];
                        $POB_Value = $DetTB["orderValue"];
                        $proc = "svDCRUnlstDet_App";
                        $sql = "SELECT unlisted_doctor_name name from vwunlisted_doctor_master_APP where unlisted_doctor_code=" . $cCode;
                }
                if(isset($tableData['DCRDetail_Distributors_Hunting'])){
                        $distHunting=$tableData['DCRDetail_Distributors_Hunting'];
                        $vTyp = 7;
                }
                $tRw = performQuery($sql);

                $cName = str_replace("'", " ", $tRw[0]["name"]);

                if (isset($tableData['Activity_Sample_Report']) || isset($tableData['Activity_Unlistedsample_Report'])) {
                    
                    if (isset($tableData['Activity_Sample_Report']))
                            $samp = $tableData['Activity_Sample_Report'];
                    if (isset($tableData['Activity_Unlistedsample_Report']))
                            $samp = $tableData['Activity_Unlistedsample_Report'];

                    $PPXML = "<ROOT>";
                    for ($j = 0; $j < count($samp); $j++) {
                        //$PPXML = $PPXML . "<Prod Cd=\"".$samp[$j]["product_code"]."\" Nm=\"".$samp[$j]["product_Name"]."\" Q=\"".$samp[$j]["Product_Rx_Qty"]."\" V=\"".$samp[$j]["Product_Sample_Qty"]."\"  fr=\"".$samp[$j]["Product_Sample_Qty"]."\"  Dis=\"".$samp[$j]["Product_Sample_Qty"]."\" />";

                        if ($j < 3) {
                            $pProd = $pProd . (($pProd != "") ? "#" : '') . $samp[$j]["product_code"] . "~" . $samp[$j]["Product_Sample_Qty"] . "$" . $samp[$j]["Product_Rx_Qty"];
                            $npProd = $npProd . (($npProd != "") ? "#" : '') . $samp[$j]["product_Name"] . "~" . $samp[$j]["Product_Sample_Qty"] . "$" . $samp[$j]["Product_Rx_Qty"];
                        } else {
                            $SPProds = $SPProds . $samp[$j]["product_code"] . "~" . $samp[$j]["Product_Sample_Qty"] . "$" . $samp[$j]["Product_Rx_Qty"] . "#";
                            $nSPProds = $nSPProds . $samp[$j]["product_Name"] . "~" . $samp[$j]["Product_Sample_Qty"] . "$" . $samp[$j]["Product_Rx_Qty"] . "#";
                        }
                    }
                    $PPXML = $PPXML . "</ROOT>";
                }

            // if (isset($tableData['Activity_POB_Report'])) {

                    if (isset($tableData['Activity_POB_Report'])){
                        $samp = $tableData['Activity_POB_Report'];
                        for ($j = 0; $j < count($samp); $j++) {
                                $SPProds = $SPProds . $samp[$j]["product_code"] . "~" . $samp[$j]["Qty"] . "#";
                                $nSPProds = $nSPProds . $samp[$j]["product_Name"] . "~" . $samp[$j]["Qty"] . "#";
                        }
                    }
                    if (isset($tableData['Activity_Stk_POB_Report'])){
                        $samp = $tableData['Activity_Stk_POB_Report'];
                        $PPXML = "<ROOT>";
                        for ($j = 0; $j < count($samp); $j++) {
							$cl_qty_stock=$samp[$j]["cb_qty"];
							if($samp[$j]["tax"]==null ||$samp[$j]["tax"]=="")
							{
								$samp[$j]["tax"]=0;
							}
							if($samp[$j]["dis_value"]==null || $samp[$j]["dis_value"]=="")
							{
								$samp[$j]["dis_value"]=0;
							}
							if($samp[$j]["dis"]==null || $samp[$j]["dis"]=="")
							{
								$samp[$j]["dis"]=0;
							}
							$samp[$j]["Productname"]=str_replace("&","and",$samp[$j]["Productname"]);
                            //$PPXML = $PPXML . "<Prod PCode=\"".$samp[$j]["product_code"]."\" PName=\"".$samp[$j]["product_Name"]."\" CQty=\"".$samp[$j]["Qty"]."\" PQty=\"".$samp[$j]["PQty"]."\" CL=\"".$cl_qty_stock."\"   />";
							$PPXML = $PPXML . "<Prod PCode=\"".$samp[$j]["ProductCode"]."\" PName=\"".$samp[$j]["Productname"]."\" Qty=\"".$samp[$j]["Qty"]."\" Rate=\"".$samp[$j]["ProductRate"]."\" Val=\"".$samp[$j]["Val"]."\" Free=\"".$samp[$j]["free"]."\" Dis_value=\"".$samp[$j]["dis_value"]."\" Dis=\"".$samp[$j]["dis"]."\" Off_Pro_Code=\"".$samp[$j]["Off_Pro_code"]."\" Off_Pro_Name=\"".$samp[$j]["Off_Pro_name"]."\" Off_Pro_Unit=\"".$samp[$j]["Off_Pro_Unit"]."\" Productunitcode=\"".$samp[$j]["Productunit"]."\" tax=\"".$samp[$j]["tax_value"]."\" con_fac=\"".$samp[$j]["Con_Fac"]."\" PQty=\"".$samp[$j]["PQty"]."\" CL=\"".$cl_qty_stock."\"  dis_type=\"".$samp[$j]["discount_type"]."\" />";

                            $SPProds = $SPProds . $samp[$j]["product_code"] . "~" . $samp[$j]["Qty"] . "#";
                            $nSPProds = $nSPProds . $samp[$j]["product_Name"] . "~" . $samp[$j]["Qty"] . "#";
                        }
                        $PPXML = $PPXML . "</ROOT>";
                    }
					if(isset($tableData['Json_Head'])){
						$samp = $tableData['Json_Head'];
						//$sql="exec PrimaryCredit_Validation '".$samp[$j]["order_value"]."','".$samp[$j]["stk_code"]."'";
						//$creditresponse=performQuery($sql);						
						$HPXML = "<ROOT>";
						for($j=0;$j<count($samp);$j++){
							$SUpNo=$samp[$j]["sup_no"];
							$SUpNm=$samp[$j]["sup_name"];
							$HPXML = $HPXML . "<Head Sf_Code=\"".$samp[$j]["sf_code"]."\" Stk_Code=\"".$samp[$j]["stk_code"]."\" Order_Date=\"".$samp[$j]["order_date"]."\" Order_Value=\"".$samp[$j]["order_value"]."\"  Div_Code=\"".$samp[$j]["div_code"]."\" Sup_No=\"".$samp[$j]["sup_no"]."\" Sup_Name=\"".$samp[$j]["sup_name"]."\" Bill_Add=\"".$samp[$j]["bill_add"]."\" Ship_Add=\"".$samp[$j]["ship_add"]."\" Exp_Date=\"".$samp[$j]["exp_date"]."\" Com_Add=\"".$samp[$j]["com_add"]."\" Sub_Total=\"".$samp[$j]["sub_tot"]."\" Tax_Total=\"".$samp[$j]["tax_tot"]."\" Dis_Total=\"".$samp[$j]["dis_tot"]."\" sap_code=\"\"  />";
						}
						$HPXML = $HPXML . "</ROOT>";
					}
               // }
                if (isset($tableData['Activity_Input_Report']) || isset($tableData['Activity_Chm_Sample_Report']) || isset($tableData['Activity_Chm_Sample_Report']) || isset($tableData['activity_unlistedGift_Report'])) {
                    if (isset($tableData['Activity_Input_Report']))
                            $inp = $tableData['Activity_Input_Report'];
                    if (isset($tableData['Activity_Chm_Sample_Report']))
                            $inp = $tableData['Activity_Chm_Sample_Report'];
                    if (isset($tableData['Activity_Stk_Sample_Report']))
                            $inp = $tableData['Activity_Stk_Sample_Report'];
                    if (isset($tableData['activity_unlistedGift_Report']))
                            $inp = $tableData['activity_unlistedGift_Report'];

                    for ($j = 0; $j < count($inp); $j++) {
                        if ($j == 0 && ($vTyp == 1 || $vTyp == 4 )) {
                            $pGCd = $inp[$j]["Gift_Code"];
                            $pGNm = $inp[$j]["Gift_Name"];
                            $pGQty = $inp[$j]["Gift_Qty"];
                        } else {
                            $Inps = $Inps . $inp[$j]["Gift_Code"] . "~" . $inp[$j]["Gift_Qty"] . "#";
                            $nInps = $nInps . $inp[$j]["Gift_Name"] . "~" . $inp[$j]["Gift_Qty"] . "#";
                        }
                    }
                }
            }


            $ARCd ="";
            $ARDCd = (strlen($_GET['amc']) == 0) ? "0" : $_GET['amc'];
           
if (!empty($data[1]["Activity_Doctor_Report"]['superstockistid']))
 $superstk=$data[1]["Activity_Doctor_Report"]['superstockistid'];
            $sql = "{call  svDCRMain_App(?,?," . $vals["Worktype_code"] . ",'" . str_replace("'", "", $vals["Town_code"]) . "',?,'" . str_replace("'", "", $vals["Daywise_Remarks"]) . "',?," . $superstk . ")}";
            $params = array(array($sfCode, SQLSRV_PARAM_IN),
                array($today, SQLSRV_PARAM_IN),
                array($Owndiv, SQLSRV_PARAM_IN),
                array(&$ARCd, SQLSRV_PARAM_INOUT, SQLSRV_PHPTYPE_STRING(SQLSRV_ENC_CHAR), SQLSRV_SQLTYPE_NVARCHAR(50)));

            $tr = performQueryWP($sql, $params);
                if (sqlsrv_errors() != null) {
                    
                    outputJSON($params );
                    
                    outputJSON(sqlsrv_errors());
                    die;
                }
            $loc = explode(":", str_replace("'", "", $DetTB["location"]) . ":");
            $lat = $loc[0]; //latitude
            $lng = $loc[1]; //longitude
            $address = getaddress($lat, $lng);
            if ($address) {
                $DetTB["geoaddress"] = $address;
            } else {
                $DetTB["geoaddress"] = "NA";
            }
            if ($vTyp==7) {
                $sql = "exec  svDCRDetailDistirbutorsHunting '$ARCd'," .$distHunting['name'] . "," . $distHunting['contact'] . "," . $distHunting['phone'] . "," .$distHunting["area"] . "," . $vals["Daywise_Remarks"] . "," . $distHunting["address"] . ",'" . date('Y-m-d H:i:s') . "','$sfCode'," . $vals["DataSF"] . ",".$distHunting["Worked_With"]."";
                performQuery($sql);
                $resp["success"] = true;
                echo json_encode($resp);die;
            }
            $sqlsp = "{call  ";
            if ($vTyp != 0 && $vTyp!=7 && $vTyp != 9) {
                if ($vTyp == 2 || $vTyp == 3 || $vTyp == 8)
                    $proc = "svDCRCSHDet_App";

                if ($pob == '')
                    $pob = '0';
                if ($POB_Value == '')
                    $POB_Value = '0';
                $sqlsp = $sqlsp . $proc . " (?,?,?," . $vTyp . ",$cCode,'" . $cName . "'," . $vTm . "," . $pob . ",'" . str_replace("'", "", $DetTB["Worked_With"]) . "',?,?,?,?,";
                if ($vTyp == 1 || $vTyp == 4)
                    $sqlsp = $sqlsp . "?,?,?,?,?,";

                if ($vTyp == 1)
                    $sqlsp = $sqlsp . "'" . str_replace("'", "", $vals["Town_code"]) . "','" . str_replace("'", "", $vals["Daywise_Remarks"]) . "',?,'" . str_replace("'", "", $vals["rx_t"]) . "'," . $DetTB["modified_time"] . ",?,?," . $vals["DataSF"] . ",'" . $DetTB["geoaddress"] . "'," . $POB_Value . ",$net_weight_value,".$DetTB['stockist_code'].",".$DetTB['stockist_name'].",$discount_price,'$rateMode','".$DemoTo."')}";
                else if($vTyp==3){
                    if($DetTB["intrumenttype"]==null)
                        $DetTB["intrumenttype"]="";
                    if($DetTB["date_of_intrument"]==null)
                        $DetTB["date_of_intrument"]="";
                    $sqlsp = $sqlsp . "'" . str_replace("'", "", $vals["Town_code"]) . "','" . str_replace("'", "", $vals["Daywise_Remarks"]) . "',?,'" . str_replace("'", "", $vals["rx_t"]) . "'," . $DetTB["modified_time"] . ",?,?," . $vals["DataSF"] . ",'" . $DetTB["geoaddress"] . "'," . $POB_Value . ",'" . $DetTB["intrumenttype"] . "','" . $DetTB["date_of_intrument"] . "')}";
                }
                else
                    $sqlsp = $sqlsp . "'" . str_replace("'", "", $vals["Town_code"]) . "','" . str_replace("'", "", $vals["Daywise_Remarks"]) . "',?,'" . str_replace("'", "", $vals["rx_t"]) . "'," . $DetTB["modified_time"] . ",?,?," . $vals["DataSF"] . ",'" . $DetTB["geoaddress"] . "'," . $POB_Value . ")}";

                $params = array(array($ARCd, SQLSRV_PARAM_IN),
                    array(&$ARDCd, SQLSRV_PARAM_INOUT, SQLSRV_PHPTYPE_STRING(SQLSRV_ENC_CHAR), SQLSRV_SQLTYPE_NVARCHAR(50)),
                    array($sfCode, SQLSRV_PARAM_IN));
                if ($vTyp == 1 || $vTyp == 4) {
                    array_push($params, array($pProd, SQLSRV_PARAM_IN));
                    array_push($params, array($npProd, SQLSRV_PARAM_IN));
                }

                array_push($params, array($SPProds, SQLSRV_PARAM_IN));
                array_push($params, array($nSPProds, SQLSRV_PARAM_IN));

                if ($vTyp == 1 || $vTyp == 4) {
                    array_push($params, array($pGCd, SQLSRV_PARAM_IN));
                    array_push($params, array($pGNm, SQLSRV_PARAM_IN));
                    array_push($params, array($pGQty, SQLSRV_PARAM_IN));
                }
                array_push($params, array($Inps, SQLSRV_PARAM_IN));
                array_push($params, array($nInps, SQLSRV_PARAM_IN));
                array_push($params, array($Owndiv, SQLSRV_PARAM_IN));
                array_push($params, array($loc[0], SQLSRV_PARAM_IN));
                array_push($params, array($loc[1], SQLSRV_PARAM_IN));

                performQueryWP($sqlsp, $params);
                if (sqlsrv_errors() != null) {
                    
                    outputJSON($params );
                    
                    outputJSON(sqlsrv_errors());
                    die;
                }
                $DCRCode = $ARDCd;

/*                if ($ARDCd == "Exists") {
                    $resp["msg"] = "Call Already Exists";
                    $resp["success"] = false;
                    echo json_encode($resp);
                    die;
                }*/
                if($vTyp == 3  || $vTyp == 8){
                    if($vTyp == 3){

                    //$sqlOrd = "{call svPriOrder(?,$cCode,?," . $POB_Value . ",'" . $DetTB["intrumenttype"] . "','" . $DetTB["date_of_intrument"] . "'," . $pob . ",'" . str_replace("'", "", $vals["Daywise_Remarks"]) . "',?,?,?,'".$eKeyID."','".$POT."',".$Super_Stck_code.",)}";
                 /*  echo $sqlOrd;
				  die; */
					$sqlOrd="{call svAppPrimaryOrder(?,?,?,?,?,?,?)}";
					$params1 = array(
								array($stk_code, SQLSRV_PARAM_IN),
								array($divisionCode, SQLSRV_PARAM_IN),
								array($HPXML, SQLSRV_PARAM_IN),
								array($PPXML, SQLSRV_PARAM_IN),
								array($SUpNo, SQLSRV_PARAM_IN),
								array($SUpNm, SQLSRV_PARAM_IN),
								array($sfCode, SQLSRV_PARAM_IN)
							);
							
				  }else{

		 
                   $sqlOrd = "{call svSuperStockist(?,$cCode,?," . $POB_Value . ",'" . $DetTB["intrumenttype"] . "','" . $DetTB["date_of_intrument"] . "'," . $pob . ",'" . str_replace("'", "", $vals["Daywise_Remarks"]) . "',?,?,?,'".$eKeyID."','".$POT."',)}";
                  $params1 = array(
							array($sfCode, SQLSRV_PARAM_IN),
							array($today, SQLSRV_PARAM_IN),
							array($ARCd, SQLSRV_PARAM_IN),
							array($Owndiv, SQLSRV_PARAM_IN),
							array($PPXML, SQLSRV_PARAM_IN)
						);

}
//echo $sqlOrd;
                    performQueryWP($sqlOrd, $params1);
					$sql = "SELECT Field_Code,Stockist_Name from Mas_Stockist where  Stockist_Code='".$cCode."'";
					$res = performQuery($sql);
					if(count($res)>0){
						$sql = "SELECT DeviceRegId FROM Access_Table where sf_code in('" . str_replace($res[0]["Field_Code"],",","','") . "') and sf_code<>''";
						$device = performQuery($sql);
						for($ik=0;$ik<count($device);$ik++){
						$reg_id = $device[$ik]['DeviceRegId'];
							if (!empty($reg_id)) {
							$msg=$res[0]["Stockist_Name"]." Order placed at ".date('Y-m-d H:i:s');
								sendFCM_notify($reg_id, $msg,"Order Placed","POrderVw","POrderVw");
							}
						}
					}
                }
                
            }

            if( $data[count($data)-3]["Activity_Event_Captures"]!=null && $data[count($data)-3]["Activity_Event_Captures"]!="undefined")
            {
                $Event_Captures = $data[count($data)-3]["Activity_Event_Captures"];
                for ($j = 0; $j < count($Event_Captures); $j++) {                       
                    $ev_imgurl=$Event_Captures[$j]["imgurl"];
                    $ev_title=$Event_Captures[$j]["title"];
                    $ev_remarks=$Event_Captures[$j]["remarks"];
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,DCRDetNo,imgurl,title,remarks,Division_Code,Identification,Insert_Date_Time,lat,lon) select '" . $ARCd . "','" . $ARDCd . "'," . $ev_imgurl . "," . $ev_title . "," . $ev_remarks . ",'" . $Owndiv . "' ,'DCR','".date('Y-m-d H:i:s')."','".$lat."','".$lng."' ";
                    performQuery($sql); 
                }
            }
            $townCode = $data[0]['Activity_Report_APP']['Town_code'];
            $custCode = $cCode;
            $stockistCode = $DetTB['stockist_code'];
			$Discountpercent = $DetTB['Discountpercent'];
            $routeTarget = $data[1]["Activity_Doctor_Report"]['rootTarget'];
            $orderNo = $data[1]["Activity_Doctor_Report"]['Order_No'];
            $orderValue = $data[1]["Activity_Doctor_Report"]['orderValue'];
            $collectedAmount = $data[1]["Activity_Doctor_Report"]['Doctor_POB'];
            $PayType = $data[1]["Activity_Doctor_Report"]['PayType'];
            $PayTypeNm = $data[1]["Activity_Doctor_Report"]['PayTypeNm'];
            $PayDt = $data[1]["Activity_Doctor_Report"]['PayDt'];
            $PayNo= $data[1]["Activity_Doctor_Report"]['PayRefNo'];
            
            if($orderValue=='') $orderValue=0;
            
            if($data[1]["Activity_Doctor_Report"]['net_weight_value']==null||$data[1]["Activity_Doctor_Report"]['net_weight_value']==''||$data[1]["Activity_Doctor_Report"]['net_weight_value']=="null"||$data[1]["Activity_Doctor_Report"]['net_weight_value']=="undefined"||empty($data[1]["Activity_Doctor_Report"]['net_weight_value']))
                $net_weight_value=0;
            else
                $net_weight_value = $data[1]["Activity_Doctor_Report"]['net_weight_value'];

            if($data[1]["Activity_Doctor_Report"]['discount_price']==null)
                $discount_price=0;
            else
                $discount_price = $data[1]["Activity_Doctor_Report"]['discount_price'];

            if($data[1]["Activity_Doctor_Report"]['rateMode']==null)
                $rateMode="Nil";
            else
                $rateMode = $data[1]["Activity_Doctor_Report"]['rateMode'];

            $orderDate = $data[1]["Activity_Doctor_Report"]["Doc_Meet_Time"];
            $transOrderDetails = $data[2]['Activity_Sample_Report'];
			 $Compititor_Product = $data[7]['Compititor_Product'];
			


            if (count($transOrderDetails) > 0 ) {
                $sql = "SELECT OrderID FROM Trans_Order_Head where OrderID='".$eKeyID."' and isnull(OrderID,'')<>''";
                $eRw = performQuery($sql);
                if(count($eRw)<1 || $eKeyID==""){
                    $sql = "SELECT isnull(OrdSl,0)+1 RwID,Division_SName+cast(SLNo as varchar) SLNo FROM Mas_SF_SlNo where SF_Code='" . $sfCode . "'";
                    $tRw = performQuery($sql);
                    $pk =$tRw[0]['SLNo']."-".$tRw[0]['RwID'];
                    $sql = "update Mas_SF_SlNo set OrdSl=".$tRw[0]['RwID']." where SF_Code='" . $sfCode . "'";
                    performQuery($sql);


    
                    $sql = "insert into Trans_Order_Head(Trans_Sl_No,Sf_Code,Cust_Code,Stockist_Code,Route_Target,Order_No,Collected_Amount,Order_Value,Order_Date,Route,DCR_Code,net_weight_value,discount_price,rateMode,Div_ID,OrderID,OrderType,TDPercent) select '" . $pk . "','" . $sfCode . "'," . $custCode . "," . $stockistCode . ",'" . $routeTarget . "'," . $orderNo . ", $collectedAmount , $orderValue," . $orderDate . ", $townCode,'" . $DCRCode . "','$net_weight_value','$discount_price','$rateMode','$Owndiv','".$eKeyID."','".$POT."',".$Discountpercent."";
                    


                    performQuery($sql);
					
					for ($i = 0; $i < count($Compititor_Product); $i++) {
	 

	  $sql = "SELECT isNull(max(cast(Trans_Order_No as numeric)),0)+1 as RwID FROM Trans_Compititor";
                            $tcRw = performQuery($sql);
                            $tcpk1 = (int) $tcRw[0]['RwID'];

$sql = "insert into Trans_Compititor(Trans_Sl_No,Trans_Order_No,Sf_Code,Product_Code,Name,Cat_Id,Qty,Rate,Order_Date,Division_Code,Brand_Code,Type,Ekey,Cust_Code,Route_Code) select '" . $pk . "','" .$tcpk1 . "','" . $sfCode . "','" . $Compititor_Product[$i]['Pid'] . "','" . $Compititor_Product[$i]['Name'] . "','" . $Compititor_Product[$i]['Cat_Id'] . "','" . $Compititor_Product[$i]['Rate'] . "','" . $Compititor_Product[$i]['Qty'] . "',$orderDate,'$Owndiv','" . $Compititor_Product[$i]['bid'] . "','" . $Compititor_Product[$i]['CCType'] . "' ,'".$eKeyID."'," . $custCode . ",$townCode";
                            performQuery($sql);
							
							

	 
								}
					if($AITEST==9){
						 $sql = "select Trans_SlNo  from DCRMain_Trans  where  SF_Code='" . $sfCode . "' and Activity_Date='".date('Y-m-d')."' ";
                $eRw = performQuery($sql);
						
						
                if(count($eRw)>0 || $eKeyID==""){
                  
                    $pkk =$eRw[0]['Trans_SlNo'];
						
						
						  $sql = "insert into Trans_Pragnancy__Head(Trans_Sl_No,Sf_Code,Cust_Code,Stockist_Code,Route_Target,Order_No,Collected_Amount,Order_Value,Order_Date,Route,DCR_Code,net_weight_value,discount_price,rateMode,Div_ID,OrderID,OrderType,Remarks) select '" . $pkk . "','" . $sfCode . "'," . $custCode . "," . $stockistCode . ",'" . $routeTarget . "'," . $orderNo . ", $collectedAmount , $orderValue," . $orderDate . ", $townCode,'" . $DCRCode . "','$net_weight_value','$discount_price','$rateMode','$Owndiv','".$eKeyID."','".$POT."','" . str_replace("'", "", $vals["Daywise_Remarks"]) . "'";
                    
                    performQuery($sql);
				}
					}
    
                    $totalval = 0;$orderValue=0;
					
					
					
                    for ($i = 0; $i < count($transOrderDetails); $i++) {
                        $qty = $transOrderDetails[$i]['Product_Rx_Qty'];
                        $value = $transOrderDetails[$i]['Product_Sample_Qty'];
                        if($transOrderDetails[$i]['free']==null)
                            $free=0;
                        else
                            $free=$transOrderDetails[$i]['free'];
                        if($transOrderDetails[$i]['cb_qty']==null)
                           $cbQty=0;
                        else
                           $cbQty=$transOrderDetails[$i]['cb_qty'];
					   
					    if($transOrderDetails[$i]['Ccb_qty']==null)
                           $Ccb_qty=0;
                        else
                           $Ccb_qty=$transOrderDetails[$i]['Ccb_qty'];
    
	                    if($transOrderDetails[$i]['RcpaId']==null)
                           $RcpaId=0;
                        else
                           $RcpaId=$transOrderDetails[$i]['RcpaId'];
	
	                     if($RcpaId=="") $RcpaId="0";
                        if($cbQty=="") $cbQty="0";
						
						
						 if($Ccb_qty=="") $Ccb_qty="0";
    
                        if($transOrderDetails[$i]['PromoVal']==null)
                           $Promo=0;
                        else
                           $Promo=$transOrderDetails[$i]['PromoVal'];
    
                        if($Promo=="") $Promo="0";
    
        
            if($net_weight=="" || $net_weight==null){

		$sql = "select product_netwt from Mas_Product_Detail where Product_Detail_Code='" . $transOrderDetails[$i]['product_code'] . "'";
                  $Nwt = performQuery($sql);
 		$net_weight=$Nwt[0]['product_netwt'];

            }
			
			
			//$productremarks=($transOrderDetails[$i]['rx_remarks']==null? 0:$transOrderDetails[$i]['rx_remarks']);
                           $productremarks=$transOrderDetails[$i]['rx_remarks'];
                             $productremarks_Id=$transOrderDetails[$i]['rx_remarks_Id'];

                             $remarkspd = 0;
			
    
 
                        if($qty>0 || $free>0   || $cbQty>0  ||  $Ccb_qty>0 || $Promo>0  || $productremarks!==$remarkspd ||  $RcpaId>0 ){
    
			
                           /*  $sql = "SELECT isNull(max(cast(Trans_Order_No as numeric)),0)+1 as RwID FROM Trans_Order_Details";
                            $tRw = performQuery($sql);
                            $pk1 = (int) $tRw[0]['RwID']; */
                            
                              $sql = "SELECT isnull(OrdDsl,0)+1  RwID,Division_SName+cast(SLNo as varchar) SLNo FROM Mas_SF_SlNo where SF_Code='" . $sfCode . "'";
							$tRw = performQuery($sql);
							$SFSl=$tRw[0]['SLNo']."-";
							$pk1 =$SFSl.$tRw[0]['RwID'];
							$sql = "update Mas_SF_SlNo set OrdDsl=".$tRw[0]['RwID']." where SF_Code='" . $sfCode . "'";
							performQuery($sql);


                            if($transOrderDetails[$i]['net_weight']==null||$transOrderDetails[$i]['net_weight']==""||$transOrderDetails[$i]['net_weight']=="null"||$transOrderDetails[$i]['net_weight']=="undefined"||empty($transOrderDetails[$i]['net_weight']))
                                $net_weight=0;
                            else
                                $net_weight = $transOrderDetails[$i]['net_weight'];
        
                        
                            if($transOrderDetails[$i]['discount']==null)
                               $discount=0;
                            else
                               $discount=$transOrderDetails[$i]['discount'];
                            
                            if($transOrderDetails[$i]['discount_price']==null)
                               $discountprice=0;
                            else
                               $discountprice=$transOrderDetails[$i]['discount_price'];
        
                            
                            if($transOrderDetails[$i]['Rate']==null)
                               $rate=0;
                            else
                                $rate=$transOrderDetails[$i]['Rate'];
                            
							
							
							
                            
                            $sql = "select Product_Detail_Code,isnull(Distributor_Discount_Price,'0') DistDisc,isnull(MRP_Price,'0') DistPRate from Mas_Product_State_Rates r inner join Mas_Salesforce s on r.State_Code=s.State_Code and r.Division_Code=cast(left(s.Division_Code,charindex(',',s.Division_Code+',')-1) as int) where Sf_Code='" . $sfCode . "' and Product_Detail_Code='" . $transOrderDetails[$i]['product_code'] . "'";
                            $rateDet = performQuery($sql);
                            $Disc ="0";
                            $DistRate ="0";
                            if(count($rateDet)>0){
                                $Disc = (string) $rateDet[0]['DistDisc'];
                                $DistRate = (string) $rateDet[0]['DistPRate'];
                            }
        
                            
				$FreeP_Code = $transOrderDetails[$i]['FreeP_Code'];
				$Fname = $transOrderDetails[$i]['Fname'];
				
				
                           if($productremarks!==$remarkspd){
							    
							  $P_Code=  $transOrderDetails[$i]['product_code'];
							 $P_Name=  $transOrderDetails[$i]['product_Name'] ;
							  
                           if($productremarks_Id!=null && $productremarks_Id!=''){
                             $sqlPR = "insert into Trans_Product_Remarks( SF_Code,Cust_Code,Route_Code,Trans_Order_No,Trans_Sl_No, Product_Code,Product_Name,Div_ID,Created_Date,Remarks,Remarks_Id) select   '" . $sfCode . "'," . $custCode . ",$townCode,'" . $pk1 . "','" . $pk . "','" . $P_Code . "','" .$P_Name . "','$Owndiv','".date('Y-m-d')."','$productremarks','$productremarks_Id'";
                                performQuery($sqlPR);
						   }
							   
                             }
				
                           
						   
						   
						   
						   
						   
						   
						   
						   
						   
                           if($qty>0 || $free>0   || $cbQty>0 || $Promo>0   ||  $Ccb_qty>0   ||  $RcpaId>0  ){
                         
                            $sql = "insert into Trans_Order_Details( Trans_Order_No, Trans_Sl_No,Product_Code,Product_Name,Quantity,value,Rate,net_weight,free,discount,discount_price,rateMode,DistDisc,DistPicRate,MfgDt,ClStock,PromoVal,Div_ID,Offer_ProductNm,Offer_ProductCd,CClStock) select '" . $pk1 . "','" . $pk . "','" . $transOrderDetails[$i]['product_code'] . "','" . $transOrderDetails[$i]['product_Name'] . "', $qty,$value,$rate,'$net_weight','$free','$discount','$discountprice','$rateMode',$Disc,$DistRate,'" . $transOrderDetails[$i]['Mfg_Date'] . "','". $cbQty  ."',$Promo,'$Owndiv','" . $Fname . "','" . $FreeP_Code . "' ,'". $Ccb_qty  ."'";
                            performQuery($sql);
							
							
							
							
							 if($POT==2){
								 
								  $sql = "SELECT isNull(max(cast(Legr_ID as numeric)),0)+1 as MaxRwID FROM Trans_VanStock_Ledger";
                            $tRww = performQuery($sql);
                            $MaxRwID = (int) $tRww[0]['MaxRwID'];
							 $docor_Name=str_replace("'", "", $data[1]['Activity_Doctor_Report']['doctor_name']);
							$sql = "insert into   Trans_VanStock_Ledger (Legr_ID,from_loc,fLocTyp,To_Loc,tLocTyp,eDate,PCode,GQty,Division_Code,EntryBy,CalcTyp,TNm)  select '" . $MaxRwID . "' , '" . $sfCode . "','F' ," . $custCode . ",'R' , " . $orderDate . ",'" . $transOrderDetails[$i]['product_code'] . "',$qty,'$Owndiv','VanInv','0','" . $docor_Name . "'  ";
							
							
							
							performQuery($sql);
							
						$sql = "update Trans_Prod_DailyInventory   set Totalqty=(select Totalqty-$qty   from Trans_Prod_DailyInventory where sfcode= '" . $sfCode . "' and EntryDt='" . $today . "' and Prod_Code='" . $transOrderDetails[$i]['product_code'] . "')
where sfcode= '" . $sfCode . "' and convert(date,EntryDt)='" . $today . "' and Prod_Code='" . $transOrderDetails[$i]['product_code'] . "' ";
							
							performQuery($sql);	 
							
							
							 }
							
							
							
					}		
							if($AITEST==9){
								
								
								
								 $sql = "SELECT isNull(max(cast(Trans_Order_No as numeric)),0)+1 as RwID FROM Trans_Pragnancy__Details";
                            $tRw = performQuery($sql);
                            $pk11 = (int) $tRw[0]['RwID'];
								
								$sql = "insert into Trans_Pragnancy__Details( Trans_Order_No, Trans_Sl_No,Product_Code,Product_Name,Quantity,value,Rate,net_weight,free,discount,discount_price,rateMode,DistDisc,DistPicRate,MfgDt,ClStock,PromoVal,Div_ID) select '" . $pk11 . "','".$eKeyID."','" . $transOrderDetails[$i]['product_code'] . "','" . $transOrderDetails[$i]['product_Name'] . "', $qty,$value,$rate,'$net_weight','$free','$discount','$discountprice','$rateMode',$Disc,$DistRate,'" . $transOrderDetails[$i]['Mfg_Date'] . "','". $cbQty  ."',$Promo,'$Owndiv'";
                            performQuery($sql);
							}
                        }
                    }
    
                    $sql = "update Trans_Order_Head set Order_Value=((select sum(value) from Trans_Order_Details where Trans_Sl_No=Trans_Order_Head.Trans_Sl_No and Div_ID=Trans_Order_Head.Div_ID)-'$discount_price') where  Trans_Sl_No='" . $pk . "' and Div_ID='$Owndiv'";
                    $resp["HeadQryy"]=$today;
                    performQuery($sql);
                }
            }
          /*  if ($collectedAmount <>"0"){                 
                //$PayTypeNm 
                $sql = "SELECT isNull(max(cast(Sl_No as numeric)),0)+1 as RwID FROM Trans_Payment_Detail";
                $tRw = performQuery($sql);
                $pk1 = (int) $tRw[0]['RwID'];
                $sql = "insert into Trans_Payment_Detail(Sl_No,Sf_Code,Sf_Name,Cust_Id,Cus_Name,Amount,Pay_Mode,Pay_Date,Pay_Ref_No,Remarks,Distributor_Code,Route_code,eDate,PaymentName)  select '" . $pk1 . "','" . $sfCode . "',SF_Name," . $custCode . ",(select  ListedDr_Name from Mas_ListedDr where ListedDrCode=" . $custCode . "),$collectedAmount,'".$PayType."','".$PayDt."','".$PayNo."','" . str_replace("'", "", $vals["Daywise_Remarks"]) . "'," . $stockistCode . ", ".$townCode."," . $orderDate . ",'".$PayTypeNm."' from Mas_Salesforce where SF_Code='" . $sfCode . "'";
                performQuery($sql);
            }
             */   
            $sql = "SELECT Total_Order_Amount as totalOrder,Amt_Collect FROM Order_Collection_Details where Sf_Code='$sfCode' and Stockist_Code=$stockistCode and Cust_Code=$custCode";
            $tRw = performQuery($sql);
            if (!empty($tRw)) {
               // $oldTotalOrder = (int) $tRw[0]['totalOrder'];
                //$oldCollectedAmount = (int) $tRw[0]['Amt_Collect'];
                //$total = $orderValue + $oldTotalOrder;
                //$collectAmount = $collectedAmount + $oldCollectedAmount;
                //$outstand = $total - $collectAmount;
                //$sql = "update Order_Collection_Details set Total_Order_Amount=$total,Out_standing_Amt=$outstand,Amt_Collect=$collectAmount,Last_order_Amt=$orderValue where Sf_Code='$sfCode' and Stockist_Code=$stockistCode and Cust_Code=$custCode";
                //$result = performQuery($sql);
                $resp["success"] = true;
				$resp["Msg"]="Submitted Successfully";
                echo json_encode($resp);
                die;
            } else {
                //$outstandinAmt = $orderValue - $collectedAmount;
                //$sql = "SELECT isNull(max(cast(Trans_Collection_No as numeric)),0)+1 as RwID FROM Order_Collection_Details";
                //$tRw = performQuery($sql);
                //$pk2 = (int) $tRw[0]['RwID'];
//                $sql = "insert into Order_Collection_Details(Trans_Collection_No,Sf_Code,Cust_Code,Stockist_Code,Total_Order_Amount,Amt_Collect,Out_standing_Amt,Last_Order_Amt,Route) select '" . $pk2 . "','" . $sfCode . "'," . $custCode . "," . $stockistCode . ",$orderValue,$collectedAmount,$outstandinAmt,$orderValue, $townCode"
                //performQuery($sql);
				
                $resp["success"] = true;
				$resp["Msg"]="Submitted Successfully";


                echo json_encode($resp);
                die;
            }            
            break;
    }
    $resp["success"] = true;
	$resp["Msg"]="Submitted Successfully";
    echo json_encode($resp);
}

$axn = $_GET['axn'];
$value = explode(":", $axn);
switch ($value[0]) {
    case "getDistReport":
        getDistReport();
        break;
    case "login":
        actionLogin();

        break;
    case "get/track":
            $SF=$_GET['SF_Code'];
        $Dt = str_replace(".000000","",$_GET['Dt']);
        $query = "select * from ((select replace(convert(varchar,Time,9),':000',' ') DtTime,lati lat,Long lng,Trans_Detail_Name,(Select  Doc_Spec_ShortName  from Mas_ListedDr where ListedDrCode=m.Trans_Detail_Info_Code) ChannelName,POB_Value,net_weight_value,GeoAddrs,Time DtTm,0 ordfld,1 typ from vwActivity_MSL_Details m  where  cast(convert(varchar,Time,101) as datetime)=cast(convert(varchar,cast('$Dt' as datetime),101) as datetime) and Time>'$Dt'  and sf_code='$SF')union(select replace(convert(varchar,DtTm,9),':000',' ') DtTime,lat,Lon lng,'','','0','0','',DtTm,1,0 typ from tbTrackLoction where SF_Code='$SF'  and cast(convert(varchar,DtTm,101) as datetime)=cast(convert(varchar,cast('$Dt' as datetime),101) as datetime) and DtTm>'$Dt' and cast(Auc as float)<=30)   union( select replace(convert(varchar,ModTime,9),':000',' ') DtTime,lati lat,Long lng,Trans_Detail_Name, ''ChannelName,POB_Value,'0' net_weight_value,GeoAddrs,ModTime DtTm,0 ordfld,2 typ from vwActivity_CSH_Detail h where  cast(convert(varchar,ModTime,101) as datetime)=cast(convert(varchar,cast('$Dt' as datetime),101) as datetime) and ModTime>'$Dt'   and sf_code='$SF')) as t order by DtTm,ordfld";
        $result = performQuery($query);
        outputJSON($result);
        break;
    case "deleteEntry":
        $data = json_decode($_POST['data'], true);
        $sfCode = $_GET['sfCode'];
        $custCode = "'" . $data['custId'] . "'";
    
        $arc = (isset($data['arc']) && strlen($data['arc']) == 0) ? null : $data['arc'];
        $amc = (isset($data['amc']) && strlen($data['amc']) == 0) ? null : $data['amc'];
 orderDetailsDelete($sfCode, $custCode,$arc, $amc);
        $result = deleteEntry($arc, $amc);
        break;
 case "DeleteNotify":
 $data = json_decode($_POST['data'], true);
 $NotifyID="'" . $data['NotifyID'] . "'";
 $query = "update  vwMasAppNotifyLists  set Active_Flag='0' where  NotifyID=".$NotifyID."";

performQuery($query);
	break;


 case "fileAttachment":
        $sf = $_GET['sf_code'];
        $file = $_FILES['imgfile']['name'];
        $info = pathinfo($file);
        $file_name = basename($file, '.' . $info['extension']);
        $ext = $info['extension'];
        $fileName = $file_name . "_" . $sf . "_" . date('d_m_Y') . "." . $ext;
        $file_src = '../Mail/uploaded/' . $fileName;
        move_uploaded_file($_FILES['imgfile']['tmp_name'], $file_src);
        break;
    case "get/doctorCount":
        outputJSON(getDoctorPCount());
        break;
    case "get/setup":
        outputJSON(getAPPSetups());
        break;
    case "imgupload":
        $sf = $_GET['sf_code'];
        print_r(sf);
        move_uploaded_file($_FILES["imgfile"]["tmp_name"], "../photos/" . $sf . "_" . $_FILES["imgfile"]["name"]);
        break;
    case "get/jointwork":
        outputJSON(getJointWork());
        break;
    case "get/subordinate":
        outputJSON(getSubordinate());
        break;
    case "get/submgr":
        outputJSON(getSubordinateMgr());
        break;
    case "get/uldoctorCount":
        outputJSON(getUlDoctorPCount());
        break;
    case "get/chemistCount":
        outputJSON(getChemistPCount());
        break;
    case "get/stockistCount":
        outputJSON(getStockistPCount());
        break;
case "spLostProducts":
$custCode=$_GET['custCode'];
$stockistCode=$_GET['stockistCode'];
$sfCode=$_GET['sfCode'];
$sql="exec spLostProduct $custCode,$sfCode,$stockistCode";

$det=performQuery($sql);
outputJSON($det);
break;
    case "get/BrndSumm":
        $data = json_decode($_POST['data'], true);
        $desig = $data['desig'];
        outputJSON(getBrandWiseProd($desig));
        break;

    case "get/BrndSummLitters":
        $data = json_decode($_POST['data'], true);
        $desig = $data['desig'];
        outputJSON(getBrandLittersWiseProd($desig));
        break;
    case "table/list":
        $results;
        $data = json_decode($_POST['data'], true);
        $sfCode = $_GET['sfCode'];
        $RSF = $_GET['rSF'];
        $div = $_GET['divisionCode'];
        $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];

        switch (strtolower($data['tableName'])) {
            case "vwproductstaterates":
                $State_Code = $_GET['State_Code'];
                $query = "select * from vwProductStateRates where Division_code='" . $Owndiv . "' and State_Code=$State_Code";
                $results = performQuery($query);
                break;
            case "dcrdetail_distributors_hunting":
                $Dt = date('Y-m-d');
                $sfCode=$_GET['rSF'];
                $sqlH = "SELECT Trans_SlNo FROM vwActivity_Report where SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$Dt' as datetime)";
                $sql = "select * FROM DCRDetail_Distributors_Hunting where Trans_SlNo in (" . $sqlH . ")";
                $results = performQuery($sql);
            
                break;
            case "mas_worktype":
                $query = "exec GetWorkTypes_App '" . $RSF . "'";
                $results = performQuery($query);
                break;
            case "product_master":
                $desig = $data['desig'];
                $results = getProducts($desig);
                break;
            case "mas_product_brand":
                $query = "select product_brd_code,product_brd_sname from mas_product_brand where division_code='" . $Owndiv . "' and Product_Brd_Active_Flag=0";
                $results = performQuery($query);
                break;
            case "taxmaster":
                $query = "exec getTaxMaster '" . $Owndiv . "'";
                $results = performQuery($query);
                break;
            case "prodtaxdets":
                $State_Code = $_GET['State_Code'];
                $query = "exec getTaxProdStruct '" . $State_Code . "','" . $Owndiv . "'";
                $results = performQuery($query);
                break;



 case "pendingbils":
                    $Rsf = $_GET['rSF'];
                    
                    //ttth
                      //$query = "select billno,custcode,sf,billamt,coll_amt,bal_amt,inv_dt from mas_pending_bills  where sf='" . $Rsf . "' ";
                      $query = "select billno,custcode,sf,billamt,coll_amt,bal_amt,inv_dt from mas_pending_bills  where sf='" . $Rsf . "' ";
        $pendinglist = performQuery($query);
        if (count($pendinglist) > 0) {
            for ($i = 0;$i < count($pendinglist);$i++) {
                
                
                if(!($pendinglist[$i]['bal_amt']==0)){
            $pendinglistt[] = array("billno" => $pendinglist[$i]['billno'],"custcode" => $pendinglist[$i]['custcode'], "billamt" => $pendinglist[$i]['billamt'], "coll_amt" => 0,"date" => $pendinglist[$i]['inv_dt']->format('Y-m-d'), "BalanceAmout" => $pendinglist[$i]['bal_amt']);

                }

                
            }
        } else {
            $pendinglistt[] = array("Nopending" => "No Pending Bills");
        }
         $result['PendingList'] = $pendinglistt;
                    
                    //$query = "select billno,sf,billamt,custcode,coll_amt from mas_pending_bills  where sf='" . $Rsf . "' ";
                    //$results = performQuery($query);
                    // $result['PendingList'] = $pendinglistt;
                    outputJSON($result);
                    die;
                break;
                
                case "productcategoryrates":
                
                  $divisionCode=str_replace(",","",$_GET['divisionCode']);
                  $state=$_GET['State_Code'];
                    
                
        $query = "select * from ((select Cate_Code,product_detail_code,discount,rp_base_rate,rp_case_rate,mrp_rate,effective_from_date ,SS_Base_Rate,SS_Case_Rate  from mas_product_category_rates  where division_code='" . $divisionCode . "') union (select 0 Cate_Code,product_detail_code,'0' discount,cast(Retailor_Price as varchar) rp_base_rate,cast(RetailCasePrice as varchar) rp_case_rate,cast(MRP_Price as varchar) mrp_rate,getdate() effective_from_date ,cast(SS_Base_Rate as varchar) SS_Base_Rate,cast(SS_Case_Rate as varchar) SS_Case_Rate from vwProductStateRates where division_code='" . $divisionCode . "' and State_Code='".$state."')) as stb";
        $productcategoryrates = performQuery($query);
        if (count($productcategoryrates) > 0) {
            for ($i = 0;$i < count($productcategoryrates);$i++) {
                $ProductCategoryRates[] = array("Cate_Code" => $productcategoryrates[$i]['Cate_Code'],"Product_Detail_Code" => $productcategoryrates[$i]['product_detail_code'],"Discount" => $productcategoryrates[$i]['discount'], "RP_Base_Rate" => $productcategoryrates[$i]['rp_base_rate'], "RP_Case_Rate" => $productcategoryrates[$i]['rp_case_rate'], "MRP_Rate" => $productcategoryrates[$i]['mrp_rate'], "Effective_From_Date" => $productcategoryrates[$i]['effective_from_date'], "SS_Base_Rate" => $productcategoryrates[$i]['SS_Base_Rate'], "SS_Case_Rate" => $productcategoryrates[$i]['SS_Case_Rate']);  
}
        } else {
            $ProductCategoryRates[] = array("No ProductCategoryRates" => "No ProductCategoryRates");
        }
         $result['ProductCategoryRates'] = $ProductCategoryRates;
                    
                   
                    outputJSON($result);
                    die;
                break;
                
                
                
                
            case "gift_master":
                $query = "exec getAppGift '" . $sfCode . "'";
                $results = performQuery($query);
                break;
			case "sec_category_master":
				$results = stkSecOrderProd();
			break;
            case "category_master":
                $sfCode = $_GET['sfCode'];
				$div = $_GET['divisionCode'];
                $query = "exec GetStockistProdBrand_App '" . $sfCode . "'";
                $results["Brand"] = performQuery($query);
				$sql = "exec getCustomerwiseProdts '" . $sfCode . "'";
				$Product=performQuery($sql); 
				$DT=date("Y-m-d");
				$sql = "exec get_scheme_by_stk '" . $sfCode . "','".$div."','".$DT."'";
				$Scheme=performQuery($sql); 
				for($i=0;$i<count($Product);$i++){
					$pid=$Product[$i]['id'];
					$Qry = "select  UOM_Id id,UOM_Nm name,CnvQty ConQty  from vwMas_MUnitDets where PCode='".$pid."' and Division_Code='" . $div . "'";
					$Uoms=performQuery($Qry);
					$Product[$i]["UOMList"]=$Uoms;
					$new_array = array_values(array_filter($Scheme, function($obj)  use ($pid){
									if(($obj["Product_Code"] === $pid)) return true;
								}));
					if(count($new_array)>0){
						$Product[$i]['SchemeArr']=$new_array;
					}
					else{
						$new_array[] =array('Product_Code' => '','Product_Name' => '','Scheme' => '','Free' => '','Discount' => '','Package' => '','Division_Code' => '','Effective_From' => '',
										'State_Code' => '','ID' => '','Stockist_Code' => '','Effective_To' => '','Scheme_Name' => '','Against' => '','Offer_Product' => '','actFlg' => '',
										'Offer_Product_Name' => '','Scheme_Unit' => '','Free_Unit' => '','offer_product_unit' => '','Sale_Erp_Code' => '','Discount_Type' => '');
						$Product[$i]['SchemeArr']=$new_array;
					}
				}
				$results["Products"]=$Product;
				/*for ($i = 0;$i < count($results);$i++) {
					$cat_Id=$results[$i]['id'];
					//$sql ="select  Product_Detail_Code  id,Product_Detail_Name name,Product_Brd_Code Product_Cat_Code,ROW_NUMBER() OVER (ORDER BY Product_Detail_Code) row_num  from  Mas_Product_Detail  where Product_Brd_Code='".$cat_Id."'";
					//$sql ="select  Product_Detail_Code  id,Product_Detail_Name name,isnull((select isnull( cast(Base_Net_value as int),0) from Mas_Customerwise_Pricing where Product_Code=ms.Product_Detail_Code and Stockist_Code='" . $sfCode . "' ),0) Product_Cat_Code,ROW_NUMBER() OVER (ORDER BY Product_Detail_Code) row_num,Product_Sale_Unit,product_unit,ISNULL(Unit_code,0)Unit_code,ISNULL(Default_UOMQty,0)Default_UOMQty,ISNULL(Default_UOM,0)Default_UOM   from  Mas_Product_Detail ms  where Product_Brd_Code='".$cat_Id."' and Product_Active_Flag='0'";
                 //$sql ="select  Product_Detail_Code  id,Sale_Unit,Product_Detail_Name name,isnull( cast(Sale_Net_value as float),0) Product_Cat_Code,ROW_NUMBER() OVER (ORDER BY Product_Detail_Code) row_num,Product_Sale_Unit,product_unit,ISNULL(Unit_code,0)Unit_code,ISNULL(Default_UOMQty,0)Default_UOMQty,ISNULL(Default_UOM,0)Default_UOM from  Mas_Product_Detail ms inner join Mas_Customerwise_Pricing p on p.Product_Code=ms.Product_Detail_Code and Stockist_Code='" . $sfCode . "' where Product_Brd_Code='".$cat_Id."' and Product_Active_Flag='0'";
				 $Prod=FilterArry($Product,"Product_Brd_Code",$cat_Id);
				 $results[$i]['Product']=$Prod; 
				 $results[$i]['SaleUnit']=$Prod[0]["Sale_Unit"];
            } */
                if ($data['desig'] != stockist) {
                    $dummy = Array(
                        "id" => -1,
                        "name" => "Promoted Products"
                    );
                    array_unshift($results);
                }
                break;
			case "chkprod":
                $sfCode = $_GET['sfCode'];
                $query = "exec GetProdBrand_App '" . $sfCode . "'";
                $results["Brand"] = performQuery($query);
				$sql = "exec getCustomerwiseProdts '" . $sfCode . "'";
				$Product=performQuery($sql);
				/*for ($i = 0;$i < count($results);$i++) {
					$cat_Id=$results[$i]['id'];
					//$sql ="select  Product_Detail_Code  id,Product_Detail_Name name,Product_Brd_Code Product_Cat_Code,ROW_NUMBER() OVER (ORDER BY Product_Detail_Code) row_num  from  Mas_Product_Detail  where Product_Brd_Code='".$cat_Id."'";
					//$sql ="select  Product_Detail_Code  id,Product_Detail_Name name,isnull((select isnull( cast(Base_Net_value as int),0) from Mas_Customerwise_Pricing where Product_Code=ms.Product_Detail_Code and Stockist_Code='" . $sfCode . "' ),0) Product_Cat_Code,ROW_NUMBER() OVER (ORDER BY Product_Detail_Code) row_num,Product_Sale_Unit,product_unit,ISNULL(Unit_code,0)Unit_code,ISNULL(Default_UOMQty,0)Default_UOMQty,ISNULL(Default_UOM,0)Default_UOM   from  Mas_Product_Detail ms  where Product_Brd_Code='".$cat_Id."' and Product_Active_Flag='0'";
                 $Prod=FilterArry($Product,"Product_Brd_Code",$cat_Id);
				 $results[$i]['Product']=$Prod; 
				 $results[$i]['SaleUnit']=$Prod[0]["Sale_Unit"];
				} */
				$results["Products"]=$Product;
                if ($data['desig'] != stockist) {
                    $dummy = Array(
                        "id" => -1,
                        "name" => "Promoted Products"
                    );
                    array_unshift($results);
                }
                break;
            case "vwfolders":
                $folders = array();
                $sql = "select Move_MailFolder_Id id,Move_MailFolder_Name name from Mas_Mail_Folder_Name where Division_code='$Owndiv'";
                $folders = performQuery($sql);
                array_unshift($folders, array("id" => "0", "name" => "Inbox"), array("id" => "Sent", "name" => "Sent Item"), array("id" => "View", "name" => "Viewed"));
                outputJSON($folders);
                die;
                break;
            case "getmailsf":
                $sfCode = $_GET['sfCode'];
                $sfCode="Admin";
                $divCode = $_GET['divisionCode'];
                $sql = "exec getHyrSF_APP '$sfCode'";
                $mailsSF = performQuery($sql);
                //foreach ($mailsSF as $k => $v) {
                  //  $mailsSF[$k] ['id'] = $mailsSF[$k] ['sf_code'];
                   // unset($mailsSF[$k]['sf_code']);
                    //$mailsSF[$k] ['name'] = $mailsSF[$k] ['Sf_Name'];
                  //  unset($mailsSF[$k]['Sf_Name']);
               // }
                outputJSON($mailsSF);
                die;
                break;
            case "gift_master":
                $query = "exec getAppGift '" . $sfCode . "'";
                $results = performQuery($query);
                break;
            case "vwlastupdationdate":
                if ($data['desig'] == stockist) {
                    $query = "select convert(varchar,Last_Updation_Date,103) Last_Updation_Date from Trans_Current_Stock_Details where Stockist_code= '" . $sfCode . "' group by Last_Updation_Date,Stockist_code";
                } else {
                    $query = "select convert(varchar,Last_Updation_Date,103) Last_Updation_Date,cast(Stockist_code as int) stockistCode from Trans_Current_Stock_Details c
                    inner join vwstockiest_Master_APP as tab on tab.stockiest_code=c.Stockist_code
                    where tab.SF_Code='$sfCode' group by c.Last_Updation_Date,c.Stockist_code";
                }
                $results = performQuery($query);
                break;

	


            case "doctor_category":
                $query = "select Doc_Cat_Code id,Doc_Cat_Name name from Mas_Doctor_Category where Division_code='" . $Owndiv . "' and Doc_Cat_Active_Flag=0";
                $results = performQuery($query);
                break;
            case "doctor_specialty":
                $query = "select Doc_Special_Code id,Doc_Special_Name name from Mas_Doctor_Speciality where Division_code='" . $Owndiv . "' and Doc_Special_Active_Flag=0";
                $results = performQuery($query);
                break;
            case "mas_doc_class":
                $query = "select Doc_ClsCode id,Doc_ClsName name from Mas_Doc_Class where Division_code='" . $Owndiv . "' and Doc_Cls_ActiveFlag=0";
                $results = performQuery($query);
                break;
            case "mas_doc_qualification":
                $query = "select Doc_QuaCode id,Doc_QuaName name from Mas_Doc_Qualification where Division_code='" . $Owndiv . "' and Doc_Qua_ActiveFlag=0";
                $results = performQuery($query);
                break;
            case "vwactivity_csh_detail":
                $or = (isset($data['or']) && $data['or'] == 0) ? null : $data['or'];
                $where = isset($data['where']) ? json_decode($data['where']) : null;
                $query = "select * from vwActivity_CSH_Detail where Trans_Detail_Info_Type=" . $or . " and " . join(" or ", $where) . " order by vstTime";
                $results = performQuery($query);
                break;
           case "vwactivity_supercsh_detail":
                $or = (isset($data['or']) && $data['or'] == 0) ? null : $data['or'];
                $where = isset($data['where']) ? json_decode($data['where']) : null;

                
                $query = "select * from vwActivity_SuperCSH_Detail where Trans_Detail_Info_Type=" . $or . " and " . join(" or ", $where) . " order by vstTime";

         
                $results = performQuery($query);
                break;


            case "vwtourplan":
                $CMn=$_GET['CMonth'];
                $CYr=$_GET['CYr'];

                $query = "exec TourPlan_Holyday_Auto_Insert '$sfCode' ,'$CMn','$CYr',' $stateCode'";
                $resultss = performQuery($query);

                $query = "select  * from vwTourPlan where SF_Code='$sfCode' and Tmonth=$CMn and Tyear=$CYr";
                $results = performQuery($query);
                outputJSON($results);
                die;
                break;


            case "vwtourplanfcd":
            $stateCode = $_GET['State_Code'];



                $CMn=$_GET['CMonth'];
                $CYr=$_GET['CYr'];
                $query = "exec TourPlan_Holyday_Auto_Insert '$sfCode' ,'$CMn','$CYr',' $stateCode'";
                $resultss = performQuery($query);


                $query = "select  TOP 1*  from Trans_TP where SF_Code='$sfCode' and Tour_Month=$CMn and Tour_Year=$CYr and Confirmed=1 and 
Worktype_Name_B!='Holiday' and Worktype_Name_B!='Leave' and Worktype_Name_B!='Weekly Off' UNION
               select  TOP 1*  from Trans_TP_One where SF_Code='$sfCode' and Tour_Month=$CMn and Tour_Year=$CYr and Confirmed=1 and 
Worktype_Name_B!='Holiday' and Worktype_Name_B!='Leave' and Worktype_Name_B!='Weekly Off' ";




                $results = performQuery($query);
                outputJSON($results);
				
                die;
                break;

           

            case "vwleavetype":

                $query = "select Leave_Code id,Leave_SName name,Leave_Name from vwLeaveType where Division_code='" . $Owndiv . "'";
                $results = performQuery($query);
                break;

            case "gettpdet":
                $date = date('Y-m-d');
                $query = "select * from vwGetTodayTP where Sf_Code='$RSF'";
                $results = performQuery($query);
                outputJSON($results);
                die;
            case "getexpensedet":
                $date = date('Y-m-d');
                $sfCode = $_GET['rSF'];
                $query = "select Expense_Allowance,Expense_Distance,Expense_Fare,Expense_Total from  Trans_FM_Expense_Detail where cast(Created_Date as date)='$date' and Sf_Code='$sfCode'";
                $head = performQuery($query);
                $query = "select Cal_Type type,Parameter_Name parameter,cast(Amount as int) amount from Trans_Additional_Exp
                    where cast(Created_Date as date)='$date' and Sf_Code='$sfCode'";
                $additional = performQuery($query);
                $results = array();
                if (!empty($head)) {
                    $results['head'] = $head[0];
                    $results['extraDetails'] = $additional;
                }
                outputJSON($results);
                die;
                break;
            case "viewstock":
                  $stockistCode=$_GET['stockistCode'];
                  $sql="select Product_Code,Product_Name,Cl_Qty,pieces from (select date,Product_Code,Product_Name,Cl_Qty,pieces,ROW_NUMBER() 
                   over(partition by Product_Code order by date desc) rw from Trans_Secondary_Sales_Details 
                   where Stockist_Code='$stockistCode') as tbl where rw=1";
                  $results = performQuery($sql);
                  outputJSON($results);die;
                   break;
            case "vwtour_plan_mgr_app":
                $query = "select  * from vwTour_Plan_Mgr_App where SF_Code='$sfCode'";
                $results = performQuery($query);
                outputJSON($results);
                die;
                break;
            default:
                $sfCode = (isset($data['sfCode']) && $data['sfCode'] == 0) ? null : $_GET['sfCode'];
                $divisionCode = (int) $Owndiv;
                //$divisionCode = 1;

                $today = (isset($data['today']) && $data['today'] == 0) ? null : $data['today'];
                $or = (isset($data['or']) && $data['or'] == 0) ? null : $data['or'];
                $wt = (isset($data['wt']) && $data['wt'] == 0) ? null : $data['wt'];
                $tableName = $data['tableName'];
                $coloumns = json_decode($data['coloumns']);
                $desig = $data['desig'];
                $where = isset($data['where']) ? json_decode($data['where']) : null;

                $join = isset($data['join']) ? $data['join'] : null;
                $orderBy = isset($data['orderBy']) ? json_decode($data['orderBy']) : null;
                if (!is_null($or)) {
                    $results = getFromTableWR($tableName, $coloumns, $divisionCode, $sfCode, $orderBy, $where, $join, $today, $wt);
                } else {
                    $results = getFromTable($tableName, $coloumns, $divisionCode, $sfCode, $orderBy, $where, $join, $today, $wt, $desig);
                }
                break;
        }
		    $respon = array();

		$respon['success']=true;
$respon['Data']=$results;

		
echo json_encode($respon);
            die;		
        break;
    case "dcr/updateEntry":
        $data = json_decode($_POST['data'], true);
        $townCode = $data[0]['Activity_Report_APP']['Town_code'];
        $custCode = $data[1]["Activity_Doctor_Report"]['doctor_code'];
        $stockistCode = $data[1]["Activity_Doctor_Report"]['Order_Stk'];
        $sfCode = $_GET['sfCode'];
       
        $arc = (isset($_GET['arc']) && strlen($_GET['arc']) == 0) ? null : $_GET['arc'];
        $amc = (isset($_GET['amc']) && strlen($_GET['amc']) == 0) ? null : $_GET['amc'];
        if (count($data[2]['Activity_Sample_Report']) != 0)
            orderDetailsDelete($sfCode, $custCode,$arc, $amc);
        deleteEntry($arc, $amc);
        addEntry();
        break;
     case "dcr/updateProducts":
        $data = json_decode($_POST['data'], true);
        $townCode = $data['Route'];
        $custCode = $data['Cust_Code'];
        $stockistCode = $data['Stockist'];
        $routeTarget = $data['target'];
        $transOrderDetails = $data['Products'];
        $Trans_Sl_No = $data['Trans_Sl_No'];
        $orderValue = $data['Value'];
        $collectedAmount = $data['POB'];
        $Discountpercent=$data['Discountpercent'];
        $sfCode = $_GET['sfCode'];
        $custCode = $data['Cust_Code'];
        $orderNo = '0';
        $orderDate = date("Y-m-d H:i");
        $DCRCode = $data['DCR_Code'];
        if($data['rateMode']==null)
            $rateMode="Nil";
        else
            $rateMode = $data['rateMode'];  
        if($data['discount_price']==null)
            $discount_price="0";
        else
            $discount_price = $data['discount_price'];
        $query = "select Order_value pob_value,CL_Stock,Div_ID,OrderID,OrderType from Trans_Order_Head WHERE Trans_Sl_No ='" . $Trans_Sl_No . "'";
        $pobValSummoutput=performQuery($query);
        $pobValSumm=$pobValSummoutput[0]['pob_value'];
        $datesumm=date('Y-m-d');
        $CL_Stock=$pobValSummoutput[0]['CL_Stock'];
        $Div_ID=$pobValSummoutput[0]['Div_ID'];
        $OrderID=$pobValSummoutput[0]['OrderID'];
        $OrderType=$pobValSummoutput[0]['OrderType'];
        $query = "update dcr_summary set pob_value=(pob_value-$pobValSumm)+$orderValue WHERE sf_code ='$sfCode' and cast(submission_date as date)='$datesumm'";
        performQuery($query);
        $query = "delete from Trans_Order_Head WHERE Trans_Sl_No ='" . $Trans_Sl_No . "'";
        performQuery($query);
		$query = "delete from Trans_Product_Remarks WHERE Trans_Sl_No ='" . $Trans_Sl_No . "'";
        performQuery($query);
		
		
        $sql = "delete from Trans_Order_Details WHERE Trans_Sl_No='$Trans_Sl_No'";
        performQuery($sql);
        $sql = "select Total_Order_Amount,Out_standing_Amt,Amt_Collect,Last_order_Amt from Order_Collection_Details where Sf_Code='$sfCode' and Cust_Code=$custCode";
        $tr = performQuery($sql);
        $total = $tr[0]['Total_Order_Amount'] - $orderValue;
        $outstand = $tr[0]['Out_standing_Amt'] + $collectedAmount;
        $collectAmount = $tr[0]['Amt_Collect'] - $collectedAmount;
        $sql = "update Order_Collection_Details set Total_Order_Amount=$total,Out_standing_Amt=$outstand,Amt_Collect=$collectAmount where Sf_Code='$sfCode' and Cust_Code=$custCode";
        $tr = performQuery($sql);

        $pk = $Trans_Sl_No;
       
        $totalval = 0;
        $net_weight_value=0;
        $discountpriceall=0;
                 
        $sql = "insert into Trans_Order_Head(Trans_Sl_No,Sf_Code,Cust_Code,Stockist_Code,Route_Target,Order_No,Collected_Amount,Order_Value,Order_Date,Route,DCR_Code,net_weight_value,discount_price,rateMode,CL_Stock,Div_ID,OrderID,OrderType,TDPercent) select '" . $pk . "','" . $sfCode . "'," . $custCode . "," . $stockistCode . ",'" . $routeTarget . "'," . $orderNo . ", $collectedAmount , $orderValue,'" . $orderDate . "', $townCode,'" . $DCRCode . "',$net_weight_value,'".$discount_price."','$rateMode','" . $CL_Stock."','". $Div_ID."','" .$OrderID."','".$OrderType."','".$Discountpercent."'";
   
       performQuery($sql);
        for ($i = 0; $i < count($transOrderDetails); $i++) {
			
			$productremarks=$transOrderDetails[$i]['rx_remarks'];
            $productremarks_Id=$transOrderDetails[$i]['rx_remarks_Id'];

             $remarkspd = 0;
			 $qty = $transOrderDetails[$i]['rx_qty'];
			 $free = $transOrderDetails[$i]['free'];
			    if($transOrderDetails[$i]['cb_qty']==null)
                       $cbQty=0;
                    else
                       $cbQty=$transOrderDetails[$i]['cb_qty'];
				   
				    if($transOrderDetails[$i]['Ccb_qty']==null)
                       $CcbQty=0;
                    else
                       $CcbQty=$transOrderDetails[$i]['Ccb_qty'];
				   
				   $Promo=$transOrderDetails[$i]['PromoVal'];
			   if($qty>0 || $free>0   || $cbQty>0 || $Promo>0   || $CcbQty>0 || $productremarks!==$remarkspd){
		
			
            $sql = "SELECT isNull(max(cast(Trans_Order_No as numeric)),0)+1 as RwID FROM Trans_Order_Details";
            $tRw = performQuery($sql);
            $pk1 = (int) $tRw[0]['RwID'];
           
            $value = $transOrderDetails[$i]['sample_qty'];
            $net_weight = $transOrderDetails[$i]['product_netwt'];
            
            $discount = $transOrderDetails[$i]['discount'];
            $discountprice = $transOrderDetails[$i]['discount_price'];
            $discountpriceall =$discountpriceall+ $transOrderDetails[$i]['discount_price'];
            $net_weight_value=$net_weight_value+$transOrderDetails[$i]['netweightvalue'];
            
            $FreeP_Code = $transOrderDetails[$i]['FreeP_Code'];
            $Fname = $transOrderDetails[$i]['Fname'];

                    if($cbQty=="") $cbQty="0";
					 if($CcbQty=="") $CcbQty="0";
                    $rate=$transOrderDetails[$i]['Rate'];
            $sql = "select Product_Detail_Code,isnull(Distributor_Discount_Price,'0') DistDisc,isnull(MRP_Price,'0') DistPRate from Mas_Product_State_Rates r inner join Mas_Salesforce s on r.State_Code=s.State_Code and r.Division_Code=cast(left(s.Division_Code,charindex(',',s.Division_Code+',')-1) as int) where Sf_Code='" . $sfCode . "' and Product_Detail_Code='" . $transOrderDetails[$i]['product'] . "'";
            $rateDet = performQuery($sql);
            $Disc ="0";
            $DistRate ="0";
            if(count($rateDet)>0){
                $Disc = (string) $rateDet[0]['DistDisc'];
                $DistRate = (string) $rateDet[0]['DistPRate'];
            }
            
			
			           if($productremarks!==$remarkspd){
							  
                           
                             $sqlPR = "insert into Trans_Product_Remarks( SF_Code,Cust_Code,Route_Code,Trans_Order_No,Trans_Sl_No, Product_Code,Product_Name,Div_ID,Created_Date,Remarks,Remarks_Id) select   '" . $sfCode . "'," . $custCode . ",$townCode,'" . $pk1 . "','" . $pk . "','" . $transOrderDetails[$i]['product'] . "','" . $transOrderDetails[$i]['product_Nm'] . "','". $Div_ID."','".date('Y-m-d')."','$productremarks','$productremarks_Id'";
                                performQuery($sqlPR);
							   
                             }
				
			
			
			
			
			   if($qty>0 || $free>0   || $cbQty>0 || $Promo>0  || $CcbQty>0 ){
			
            $sql = "insert into Trans_Order_Details( Trans_Order_No, Trans_Sl_No,Product_Code,Product_Name,Quantity,value,Rate,net_weight,free,discount,discount_price,rateMode,DistDisc,DistPicRate,MfgDt,ClStock,PromoVal,Div_ID,Offer_ProductNm,Offer_ProductCd,CClStock) select '" . $pk1 . "','" . $pk . "','" . $transOrderDetails[$i]['product'] . "','" . $transOrderDetails[$i]['product_Nm'] . "', $qty,$value,$rate,$net_weight,'$free','$discount','$discountprice','$rateMode',$Disc,$DistRate,'" . $transOrderDetails[$i]['Mfg_Date'] . "','" . $cbQty . "','0','". $Div_ID."','0','0','" . $CcbQty . "'";
            performQuery($sql);
			
			
			   }
        }
        }
        $sql = "update Trans_Order_Head  set Collected_Amount=$collectedAmount,Order_Value=$orderValue,net_weight_value=$net_weight_value,Order_Flag='0'   where Trans_Sl_No='" . $pk . "'";
        performQuery($sql);
        $sql = "SELECT Total_Order_Amount as totalOrder,Amt_Collect FROM Order_Collection_Details where Sf_Code='$sfCode' and Stockist_Code=$stockistCode and Cust_Code=$custCode";
        $tRw = performQuery($sql);
        $sql = "select * from vwProductDetails where DCR_Code='$DCRCode'";
        $tr = performQuery($sql);
        $SPProds = '';
        $nSPProds = '';
        $pob = 0;
        $pobVal = 0;
        $net_weight_value=0;
        $discountprize=0;
        for ($k = 0; $k < count($tr); $k++) {
            $SPProds = $SPProds . $tr[$k]['Additional_Prod_Code1'] . "#";
            $nSPProds = $nSPProds . $tr[$k]['Additional_Prod_Dtls1'] . "#";
            $pob = $tr[$k]['POB'] + $pob;
            $pobVal = $tr[$k]['POB_Value'] + $pobVal;
            $net_weight_value = $tr[$k]['net_weight_value'] + $net_weight_value;
            $discountprize = $tr[$k]['discount_price'] + $discount_price;
        }
        $sql = "update DCRDetail_Lst_Trans set POB=" . $pob . ",POB_Value=" . $pobVal . ",Additional_Prod_Code='" . $SPProds . "',Additional_Prod_Dtls='" . $nSPProds . "',Product_Code='',Product_Detail='',net_weight_value=$net_weight_value,discount_price='$discountprize',rateMode='$rateMode' where Trans_Detail_Slno='$DCRCode'";
        performQuery($sql);
        if (!empty($tRw)) {
            $oldTotalOrder = (int) $tRw[0]['totalOrder'];
            $oldCollectedAmount = (int) $tRw[0]['Amt_Collect'];
            $total = $orderValue + $oldTotalOrder;
            $collectAmount = $collectedAmount + $oldCollectedAmount;
            $outstand = $total - $collectAmount;
            $sql = "update Order_Collection_Details set Total_Order_Amount=$total,Out_standing_Amt=$outstand,Amt_Collect=$collectAmount,Last_order_Amt=$orderValue where Sf_Code='$sfCode' and Stockist_Code=$stockistCode and Cust_Code=$custCode";
            $result = performQuery($sql);
            $resp["success"] = true;
            echo json_encode($resp);
            die;
        } else {
            $outstandinAmt = $orderValue - $collectedAmount;
            $sql = "SELECT isNull(max(cast(Trans_Collection_No as numeric)),0)+1 as RwID FROM Order_Collection_Details";
            $tRw = performQuery($sql);
            $pk2 = (int) $tRw[0]['RwID'];
            $sql = "insert into Order_Collection_Details(Trans_Collection_No,Sf_Code,Cust_Code,Stockist_Code,Total_Order_Amount,Amt_Collect,Out_standing_Amt,Last_Order_Amt,Route) select '" . $pk2 . "','" . $sfCode . "'," . $custCode . "," . $stockistCode . ",$orderValue,$collectedAmount,$outstandinAmt,$orderValue, $townCode";
            performQuery($sql);
            $resp["success"] = true;
            echo json_encode($resp);
            die;
        }
        die;
        break;
    case "dcr/callReport":
        outputJSON(getCallReport());
        break;
    case "vwLeaveStatus":
        $sfCode = $_GET['sfCode'];
        $sql = "select * from vwLeaveEntitle where Sf_Code='$sfCode'";
        $leave = performQuery($sql);
        outputJSON($leave);
        break;
    case "vwCheckLeave":
        $sfCode = $_GET['sfCode'];
        $date = date('Y-m-d');
        $sql = "select From_Date,To_Date,No_of_Days from mas_Leave_Form where To_Date>='$date' and sf_code='$sfCode' and Leave_Active_Flag<>1 order by From_Date";
        $leaveDays = performQuery($sql);
        $currentDate = date_create($date);
        $disableDates = array();
        $sql = "SELECT * FROM vwActivity_Report where SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$date' as datetime)";
        $dcrEntry = performQuery($sql);
        if (count($dcrEntry) > 0)
            array_push($disableDates, $currentDate->format('d/m/Y'));
        for ($i = 0; $i < count($leaveDays); $i++) {
            $fromDate = $leaveDays[$i]['From_Date'];
            $toDate = $leaveDays[$i]['To_Date'];
            $noOfDays = $leaveDays[$i]['No_of_Days'];
            if ($currentDate > $fromDate)
                $fromDate = $currentDate;
            $diff = date_diff($fromDate, $toDate, TRUE);
            $days = $diff->format("%a") + 1;
            for ($j = 0; $j < $days; $j++) {
                array_push($disableDates, $fromDate->format('d/m/Y'));
                $fromDate->modify('+1 day');
            }
        }
        outputJSON($disableDates);
        break;
    case "getProductDetailing":
        $div = $_GET['divisionCode'];
        $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
        $sql="select * from vwProductDetailing where Division_Code=$Owndiv";
         $products = performQuery($sql);
        outputJSON($products);
        die;
        break;
    case "getMailsApp":
        $sfCode = $_GET['sfCode'];
        $divCode = $_GET['divisionCode'];
        $folder = $_GET['folder'];
        $month = $_GET['month'];
        $year = $_GET['year'];
        $sql = "exec MailInbox '$sfCode','$divCode','','$folder',$year,$month,'','',''";
        $mails = performQuery($sql);
        outputJSON($mails);
        die;
        break;
    case "vwLeave":
        $sfCode = $_GET['sfCode'];
        $sql = "select * from vwLeave where Reporting_To_SF='$sfCode'";
        $leave = performQuery($sql);
        outputJSON($leave);
        break;
    case "Productdisplayapprovals":
        $sfCode = $_GET['sfCode'];
        $sql = "select * from Trans_Product_Display where HQ_SfCode= '" . $sfCode . "'";
        $leave = performQuery($sql);
        outputJSON($leave);
        break;
        
        
        
    case "vwDcr":
        $sfCode = $_GET['sfCode'];
        $sql = "select d.Plan_Name,d.Trans_SlNo,d.Sf_Code,d.FieldWork_Indicator,d.WorkType_Name,d.Sf_Name,convert(varchar,Activity_Date,103) Activity_Date,s.Reporting_To_SF from DCRMain_Temp d
            inner join Mas_Salesforce s on d.Sf_Code=s.Sf_Code
             where d.Confirmed=1 and s.Reporting_To_SF='$sfCode'";
        $dcr = performQuery($sql);
        outputJSON($dcr);
        break;
    case "vwDcrOne":
        $TransSlNo = $_GET['Trans_SlNo'];
        $sql = "exec getDCRApprovalApp '" . $TransSlNo . "'";
        $dcr = performQuery($sql);
        outputJSON($dcr);

        break;
    case "vwChkTransApproval":
        $sfCode = $_GET['sfCode'];
        $sql = "select * from vwChkTransApproval where Reporting_To_SF='$sfCode'";
        $tp = performQuery($sql);
        outputJSON($tp);
        break;
    case "vwExpenseApproval":
        $sfCode = $_GET['sfCode'];
        $sql = "select * from vwExpenseApproval where Reporting_To_SF='$sfCode'";
        $tp = performQuery($sql);
        outputJSON($tp);
        break;
    case "vwChkTransApprovalOne":
        $sfCode = $_GET['code'];
        $month = $_GET['month'];
        $year = $_GET['year'];
        $sql = "select Objective,Worked_With_SF_Name,Worktype_Name_B,Territory_Code1,convert(varchar,Tour_Date,103) Tour_Date from Trans_TP_One where Sf_Code='$sfCode' order by Tour_Date";
        $tp = performQuery($sql);
        outputJSON($tp);
        break;
    case "vwChkExpenseApprovalOne":
        $sfCode = $_GET['code'];
        $month = $_GET['month'];
        $year = $_GET['year'];
        $sql = "select dcr_date,Expense_wtype_Name,Place_of_Work,Expense_Allowance,Expense_Distance,Expense_Fare,Expense_Total from Trans_FM_Expense_Detail where sf_code='$sfCode' and month(convert (date, dcr_date,103))=$month and year(convert (date, dcr_date,103))=$year order by cast(dcr_date as date)";
     
        $head= performQuery($sql);

        $sql="select Parameter_Name,sum(cast(case when (cal_type=1) then '-' else  '+' end+cast(Amount as varchar(200)) as numeric)) amt from Trans_Additional_Exp where sf_code='$sfCode' and month=$month and year=$year group by Parameter_Name";
        $summary=performQuery($sql);
        $expense=array();
        $expense['head']=$head;
        $expense['summary']=$summary;
        outputJSON($expense);
        break;
    case "tpview":
        getTPview();
        break;
    case "tpviewdt":
        getDtTPview();
        break;
    case "dcr/updRem":
        updEntry();
        break;
    case "error/data":
        $myfile = fopen("../server/errorfile.txt", "a+");
        $txt = "John Doe\n";
        $sqlsp=$_POST['data']." message:".$_GET['msg']."sf_code=".$_GET['sfCode']."\n";
        fwrite($myfile, $sqlsp);
        fclose($myfile);
        die;
        break;
    case "dcr/save":
        addEntry();
        break;
	case "dcr/retailervisit":
		outputJSON(svDMSVisit());
		break;
	case "dcr/secordersave":
		outputJSON(svDmsSecondaryOrder());
	break;
	case "dcr/cancelprimaryorder":
		outputJSON(cancelPrimaryOrder());
		break;
	case "dcr/cancelsecondaryorder":
		outputJSON(cancelSecOrder());
		break;
	case "dcr/dispatchsecondaryorder":
		outputJSON(updateDispatch());
		break;
    case "mailView":
        $id = $_GET['id'];
        $sql = "update Trans_Mail_Detail set Mail_Active_Flag='1' where Open_Mail_Id='$sfCode' and Tras_Sl_No=$num";
        performQuery($sql);
        $result['success'] = true;
        outputJSON($result);
        break;
    case "mailMove":
        $folder = $_GET['folder'];
        $id = $_GET['id'];
        $sql = "update Trans_Mail_Detail set Mail_moved_to=$folder where Open_Mail_Id='$sfCode' and Tras_Sl_No=$num";
        performQuery($sql);
        $result['success'] = true;
        outputJSON($result);
        break;
    case "mailDel":
        $folder = $_GET['folder'];
        $id = $_GET['id'];
        if ($folder == "Sent")
            $sql = "update MailBox_Details set Mail_SentItem_DelFlag=1 where Mail_int_No=$id";
        else
            $sql = "update MailBox_Details set Mail_vc_DeleteFlag=1 where Mail_int_Det_No=$id";

        performQuery($sql);
        $result['success'] = true;
        outputJSON($result);
        break;

    case "createMail":
        $sf = $_GET['sfCode'];
        $date = date('Y-m-d H:i');
        $divCode = $_GET['divisionCode'];
        $file = $_POST['fileName'];
        if (!empty($file)) {
            $info = pathinfo($file);
            $file_name = basename($file, '.' . $info['extension']);
            $ext = $info['extension'];
            $fileName = $file_name . "_" . $sf . "_" . date('d_m_Y') . "." . $ext;
        } else
            $fileName = "";
        $msg1 = urldecode($_POST['message']);
        $msg = trim($msg1, '"');
        $sub1 = urldecode($_POST['subject']);
        $sub = trim($sub1, '"');
        $sql = "insert into MailBox(Mail_Dt_Date, Mail_Vc_From, Mail_Vc_To, Mail_Vc_CC, Mail_Vc_BCC, Mail_Vc_Subject, Mail_Vc_Message, Mail_From, Staffid_Id, Loc_Code, Division_Code, Mail_Attachment)
        select '$date','" . $_POST['from'] . "','" . $_POST['to'] . "'," . $_POST['cc'] . "," . $_POST['bcc'] . ",'$sub','$msg','$sf','$sf','',$divCode,'$fileName'";
        performQuery($sql);
        $sql = "SELECT isNull(max(Mail_int_No),0) as id FROM MailBox";
        $tr = performQuery($sql);
        $maxIntNo = $tr[0]['id'];
        $ToCcBcc = explode(', ', $_POST['ToCcBcc']);
        for ($i = 0; $i < count($ToCcBcc); $i++) {
            if ($ToCcBcc[$i]) {
                //  Mail_int_Det_No,Mail_View_Color
                $sql = "insert into MailBox_Details( Mail_int_No, Mail_ToCcBcc, Mail_Vc_ViewFlag, Mail_vc_DeleteFlag, Staffid_Id, Division_Code, Mail_SentItem_DelFlag,Mail_Moved_To)
                                                   select $maxIntNo,'" . $ToCcBcc[$i] . "',0,0,'" . $sf . "',$divCode,0,0";
                performQuery($sql);
            }
        }
        $result["success"] = true;
        outputJSON($result);
        break;
    case "get/precall":
        getPreCallDet();
        break;
    case "get/currentStock":
        getCurrentStockDet();
        break;

 case "get/currentSSStock":
        getCurrentSSStockDet();
        break;
    case "get/dayplan":
        outputJSON(getDayPlan());
        break;
    case "get/MnthSumm":
        outputJSON(getMonthSummary());
        break;


  case "get/daycallreport":
        outputJSON(getDaycallSummary());
        break;

    case "get/DayRpt":
        outputJSON(getDayReport());
        break;
    case "get/DayReport":
        outputJSON(DayReport());
        break;

case "get/ProductRemarksView":


        outputJSON(ProductRemarksView());
        break;
		case "get/ProductRCPAView":


        outputJSON(ProductRCPAView());
        break;
case "get/ProductRemarksRpt":


        outputJSON(ProductRemarksRpt());
        break;
case "get/ProductRCPARpt":


        outputJSON(ProductRCPARpt());
        break;
case "get/PrimarySalesFpWise":


        outputJSON(PrimarySalesFpWise());
        break;


case "get/PrimarySalesStateWise":


        outputJSON(PrimarySalesStateWise());
        break;


case "get/PrimaryDistitarget":


        outputJSON(PrimarytargetDistiWise());
        break;

case "get/PrimaryProductwise":

        outputJSON(PrimaryProductWise());
        break;

case "get/getSalesforceTargetVsTotal":


        outputJSON(getSalesforceTargetVsTotal());
        break;
		case "get/GetvanLedger":


        outputJSON(GetvanLedger());
        break;
		
		
		case "get/GetvanLedgerView":


        outputJSON(GetvanLedgerView());
        break;
		
		case "get/VisitAnalysisView":


        outputJSON(VisitAnalysisView());
        break;
		
		case "get/AttendanceAnalysisView":


        outputJSON(AttendanceAnalysisView());
        break;
		case "get/getVisitFFView":


        outputJSON(getVisitFFView());
		break;
		case "get/getVisitAttendanceView":


        outputJSON(getVisitAttendanceView());
		
        break;
		case "get/getOrderFFView":


        outputJSON(getOrderFFView());
        break;
		
		case "get/OrderAnalysisView":


        outputJSON(OrderAnalysisView());
		 break;
		 	case "get/OrderAnalysisView":


        outputJSON(OrderAnalysisView());
		 break;
		 
case "get/DayPragnancyReport":
        outputJSON(DayPragnancyReport());
        break;
case "get/UpdateDayPragnancyReport":
        outputJSON(DayUpdatePragnancyReport());
        break;

case "get/TpRouteNotify":
        outputJSON(TpRouteNotify());
        break;

    case "summaryDet":
        outputJSON(getsummary());
        break;
    case "DaySummaryDet":
        outputJSON(getDaySummary());
        break;
    case "get/vwVstDet":
        outputJSON(getVstDets());
        break;
case "get/vwVstPragnancyDet":
        outputJSON(getVstPragnancyDets());
        break;

case "get/vwVstPragnancyDetReport":
        outputJSON(getVstPragnancyDetReport());
        break;
case "get/vwVstPendingPragnancy":
        outputJSON(getVstPendingPragnancy());
        break;
    case "get/vwOrderDetails":
        outputJSON(getVwOrderDet());
        break;
    case "save/trackloc":
        $data1 = json_decode($_POST['data'], true);
        $TrcLocs=$data1[0]['TrackLoction'];
        $sfCode=$TrcLocs['SF_code'];
        $TLocs=$TrcLocs['TLocations'];
        $sql = "select sf_emp_id,Employee_Id from Mas_Salesforce where Sf_Code='$sfCode'";
        $sf = performQuery($sql);
        $empid = $sf[0]['sf_emp_id'];
        $employeeid = $sf[0]['Employee_Id'];
        for($ik=0;$ik<count($TLocs);$ik++){
            $lng = $TLocs[$ik]['Longitude'];
            $lat = $TLocs[$ik]['Latitude'];
            
            $address = getaddress($lat, $lng);
            $sql = "insert into tbTrackLoction(SF_code,Emp_Id,Employee_Id,DtTm,Lat,Lon,Addr,Auc,deg,DvcID) select '$sfCode','$empid','$employeeid','" . $TLocs[$ik]['Time'] . "','$lat','$lng','','" . str_replace("'", "", $TLocs[$ik]['Accuracy']) . "','" . str_replace("'", "", $TLocs[$ik]['Bearing']) . "','" . str_replace("'", "", $TrcLocs['DvcID']) . "'";
            
            performQuery($sql);
        }
        break;
    case "get/calls":
        $sfCode = $_GET['sfCode'];
        $date = date('Y-m-d');
        $result = array();
        $query = "select convert(varchar,h.Activity_Date,103) Adate,order_Value orderValue,h.Trans_SlNo from vwActivity_Report h inner join vwActivity_MSL_Details d on h.Trans_SlNo=d.Trans_SlNo where h.Sf_Code='$sfCode' and h.Activity_Date='$date'";
        $data = performQuery($query);

        $order = 0;
        $orderVal = 0;
        $calls = 0;
        if (count($data) > 0) {
            for ($i = 0; $i < count($data); $i++) {
                if ($data[$i]['orderValue'] > 0) {
                    $order = $order + 1;
                    $orderVal = $orderVal + $data[$i]['orderValue'];
                }
                $Acode = $data[$i]['Trans_SlNo'];
                $Adate = $data[$i]['Adate'];
            }
        }
        $calls = count($data);

    $div = $_GET['divisionCode'];
        $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
        $query = "select top 1 Pln_Date,sf_code,cluster,sf_member_code,(select count(ListedDrCode) from Mas_ListedDr MLDR where  charindex(','+ TP.cluster+',',','+ cast(MLDR.Territory_Code as varchar)+',')>0  and ListedDr_Active_Flag=0 and  division_code='".$Owndiv."' and charindex(','+  TP.sf_member_code+',',','+ sf_code+',')>0 group by Territory_Code) cnt  from TbMyDayPlan TP  where division_code='".$Owndiv."' and sf_code='".$sfCode."' and cast(convert(varchar,Pln_Date,101)as datetime)='".$date."'    order by Pln_Date desc";
        $rccount = performQuery($query);
		
// 		if($Owndiv==20){
// 		$myfile = fopen("../server/Callsfile.txt", "a+");
//         $txt = "John Doe\n";
//         $sqlsp=$query." Calls_Count:". $calls."sf_code=".$_GET['sfCode']."\n";
//         fwrite($myfile, $sqlsp);
//         fclose($myfile);
// 		}
		
		
		
		
        $result['today']['RCCOUNT'] =  $rccount[0]['cnt'];
        $result['today']['order'] = $order;
        $result['today']['orderVal'] = $orderVal;
        $result['today']['calls'] = $calls;
        $result['today']['Adate'] = $Adate;
        $result['today']['Acode'] = $Acode;

        $query = "exec GetTargetAchDy '".$sfCode."','".date('d')."','".date('m')."','".date('Y')."'";
        $order = performQuery($query);
        $result['today']['AQty'] = $order[0]['AQty'];
        $result['today']['AAmt'] = $order[0]['AAmt'];

        $query = "select sum(order_Value) orderValue,count(h.Trans_SlNo) Trans_SlNo from vwActivity_Report h inner join vwActivity_MSL_Details d on h.Trans_SlNo=d.Trans_SlNo where h.Sf_Code='$sfCode' and MONTH(h.Activity_Date) = MONTH(getDate()) and YEAR(h.Activity_Date) = YEAR(getDate())";
        $month = performQuery($query);
        $query = "select count(h.Trans_SlNo) Trans_SlNo from vwActivity_Report h inner join vwActivity_MSL_Details d on h.Trans_SlNo=d.Trans_SlNo where h.Sf_Code='$sfCode' and MONTH(h.Activity_Date) = MONTH(getDate()) and YEAR(h.Activity_Date) = YEAR(getDate()) and POB_Value>0";
        $order = performQuery($query);
        $result['month']['order'] = $order[0]['Trans_SlNo'];
        $result['month']['orderVal'] = $month[0]['orderValue'];
        $result['month']['calls'] = $month[0]['Trans_SlNo'];
        $query = "exec GetTargetAch '".$sfCode."','".date('m')."','".date('Y')."'";
        $order = performQuery($query);

        $result['month']['TQty'] = $order[0]['TQty'];
        $result['month']['TAmt'] = $order[0]['TAmt'];
        $result['month']['AQty'] = $order[0]['AQty'];
        $result['month']['AAmt'] = $order[0]['AAmt'];
        outputJSON($result);
        break;
    case "entry/count":
        $today = date('Y-m-d 00:00:00');
        $sfCode = $_GET['sfCode'];

        $sql = "SELECT Employee_Id,case sf_type when 4 then 'MR' else 'MGR' End SF_Type FROM vwUserDetails where SF_code='" . $sfCode . "'";
        $as = performQuery($sql);
        $SFTyp = (string) $as[0]['SF_Type'];

        $query = "SELECT work_Type worktype_code,Remarks daywise_remarks,Half_Day_FW halfdaywrk,FWFlg from vwActivity_Report H where SF_Code='" . $sfCode . "'  and FWFlg <> 'F'and FWFlg <> 'HG' and FWFlg <> 'DS' and FWFlg <> 'DD' and cast(activity_date as datetime)=cast('$today' as datetime)";
        $data = performQuery($query);
        $result = array();
        if (count($data) > 0) {
            if($data[0]['FWFlg']=='DH'){
                $result["success"] = false;
                $query = "select Count(slno) hunt_count from DCRDetail_Distributors_Hunting D inner join vwActivity_Report H on H.Trans_SlNo=D.Trans_SlNo where H.SF_Code='" . $sfCode . "' and cast(activity_date as datetime)=cast('$today' as datetime)";
                $temp = performQuery($query);
                $result['count'] = $temp[0];
                $result['data'] = $data;
                outputJSON($result);
                die;
            }
            else{
                $result["success"] = false;
                $result['data'] = $data;
                outputJSON($result);
                die;
            }
        }
        $result["success"] = true;
        $result['data'] = getEntryCount();
        outputJSON($result);
        break;
        
    case "get/retailer":
          $id=$_POST['data'];
          $id = str_replace('"', "", $id);
          
           
        
         $query="exec GetRetailerDetails  '" . $id . "' ";
        outputJSON(performQuery($query));
        
        break;



    case "get/misseddates":
            $sfCode = $_GET['sfCode'];
          
           
        
         $query="select CONVERT(VARCHAR(10),Dcr_Missed_Date,111) as Dcr_Missed_Date  from DCR_MissedDates where Sf_Code='".$sfCode ."'  and Status=1 ";
        outputJSON(performQuery($query));
        
        break;
        case "get/AIretailer":
            $UKEY = $_GET['UKEY'];
          $Sf_Code=$_GET['sfCode'];
           
        $sql="select * from Mas_ListedDr where Ukey='" . $UKEY . "'";
        $code=performQuery($sql);
        outputJSON($code[0]);
        
        break;
        case "get/logouttime":
        $sfCode = $_GET['sfCode'];
       
        $data = json_decode($_POST['data'], true);
        $today = date('Y-m-d 00:00:00');
		$div = $_GET['divisionCode'];
        $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
		 
		
		
        
        
        $queryy = "update Attendance_history set End_Time='" .  date("Y-m-d h:i:sa"). "',End_Lat= '" . $data["Lattitude"] . "' ,End_Long ='" . $data["Langitude"] . "'    where  Sf_Code='" . $sfCode . "'  and login_date='".date('Y-m-d')."'";
        performQuery($queryy);
        
        $query = "update TP_Attendance_App set End_Time='" .  date("Y-m-d h:i:sa"). "' ,End_Lat= '" . $data["Lattitude"] . "' ,End_Long ='" . $data["Langitude"] . "'    where  Sf_Code='" . $sfCode . "'  and login_date='".date('Y-m-d')."'";
        performQuery($query);
        
        
        
        $query = "update Login_Time_Table set End_Time='" .  date("Y-m-d h:i:sa"). "' ,End_Lat= '" . $data["Lattitude"] . "' ,End_Long ='" . $data["Langitude"] . "'  where  Sf_Code='" . $sfCode . "'  and login_date='".date('Y-m-d')."'"; 
        
        
        performQuery($query);
		if($data['StopWorkPhoto']!=null && $data['StopWorkPhoto']!="undefined")
            {
                                  
                   
                    
                    $sql = "insert into Activity_Event_Captures(Activity_Report_Code,imgurl,Division_Code,Identification,Insert_Date_Time) values( '" . $sfCode . "','" . $data['StopWorkPhoto']['imgurl'] . "','" . $Owndiv . "','Logout Pic','".$today."')";
                    performQuery($sql); 
     
            }
    
        $result["success"] = true;
        $result["Query"] = $queryy;
        outputJSON($result);
        break;



         case "upd/retailer":
        $data = json_decode($_POST['data'], true);
        $result = array();
        $query = "update Mas_ListedDr set ListedDr_Name='" . $data["name"] . "' , ListedDr_Address1='" . $data["Address"] . "', Doc_ClsCode='" . $data["ClassCode"] . "',doc_special_code='" . $data["SpecCode"] . "', ListedDr_Phone='" . $data["Phone"] . "',Doc_Spec_ShortName='" . $data["SpecName"] . "',Doc_Class_ShortName='" . $data["ClassName"] . "' ,
        
        
        cityname='" . $data["Cityname"] . "' ,areaname='" . $data["AreaName"] . "',contactperson='" . $data["ContactPerson"] . "',designation='" . $data["Designation"] . "',gst='" . $data["GSTno"] . "',pin_code='" . $data["PINcode"] . "',ListedDr_Phone2='" . $data["Phone2"] . "',contactperson2='" . $data["ContactPerson2"] . "',designation2='" . $data["Designation2"] . "',Land_Mark='" . $data["Land_Mark"] . "',contactperson3='" . $data["ContactPerson3"] . "',ListedDr_Phone3='" . $data["Phone3"] . "'
        
        
        
        where  ListedDrCode='" . $data["id"] . "'";
        $temp = performQuery($query);
        $result["success"] = true;
        outputJSON($result);
        break;  
        
        
    case "dcr/delorder":
        $sfCode = $_GET['sfCode'];
        $OrdNo = $_GET['OrdNo'];
        
        $query = "delete from Trans_Order_Details where Trans_Sl_No='".$OrdNo."'";
        performQuery($query);
        $query = "delete from Trans_Order_Head where Trans_Sl_No='".$OrdNo."'";
        performQuery($query);
        $result["success"] = true;
        outputJSON($result);
        break;
    case "get/Attendance":
        $sfCode = $_GET['sfCode'];
        $div = $_GET['divisionCode'];
        $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
        $uRDt = $_GET['rptDt'];
        $query = "exec getMyDayPlanVwSFHry '" . $sfCode . "','" . $Owndiv . "','" . $uRDt . "'";
        outputJSON(performQuery($query));
        break;
    case "get/DYBInv":
        outputJSON(GetDailyInv());
        break;
    case "get/notify":
        outputJSON(GetNotifyLists());
        break;
case "get/TPnotify":
        outputJSON(GetTPNotifyLists());
        break;
    case "get/DAExp":
        outputJSON(getDailyAllow());
        break;
    case "get/Bills":
        outputJSON(getPendingBills());
        break;
    case "get/InvFlags":
        outputJSON(getInvFlags());
        break;
    case "get/Scheme":
        outputJSON(getSchemeDets());
        break;
    case "get/DailySales":
        outputJSON(getDailySalesSummry());
        break;

case "get/Todaycollectionreport":
    $sfCode = $_GET['sfCode'];
    $today = date('Y-m-d');
    $query = "exec GetToday_Collection_details '" . $sfCode . "','".$today."'";

outputJSON(performQuery($query));
        break;


case "get/LeaveAvailabilityCheck":
    $sfCode = $_GET['sfCode'];
    $Year = $_GET['Year'];
    
    $query = "select SFCode,LeaveCode,LeaveValue,LeaveAvailability,LeaveTaken,Leave_SName,Leave_Name from MasEntitlement  inner join mas_Leave_Type l on Leave_code=LeaveCode where SFCode='".$sfCode. "' and year(createDate)='".$Year."'";

outputJSON(performQuery($query)); 
        break;

case 'get/retailercountfdc':
   $sfCode = $_GET['sfCode'];
   $div = $_GET['divisionCode'];
   $today = date('Y-m-d');
   $divs = explode(",", $div . ",");
 $Owndiv = (string) $divs[0];
 $result = array();
  
               

  $query="exec [GET_Total_OutLetdashboard] '" . $Owndiv . "','','" . $sfCode . "','".$today."'";
  $Total_Cal=performQuery($query);
 
  $result['TotalCalls'] = $Total_Cal[0]['Total_Cal'];

  $query="exec [GET_Total_Prodashboard] '" . $Owndiv . "','','" . $sfCode . "','".$today."'";
  $proandunpro=performQuery($query);
  $result['Productive'] =$proandunpro[0]['Productive'];
  $result['Nonproductive'] =$proandunpro[0]['Nonproductive'];
  $query ="select count(distinct ListedDrCode) lrdr_count from Mas_ListedDr Ld where ld.division_code = '" . $Owndiv . "' and cast(CONVERT(varchar, ListedDr_Created_Date, 101) as datetime) = '".$today."'" ;
 $retailercount=performQuery($query);


 $result['Retailercount'] =$retailercount[0]['lrdr_count'];
 outputJSON($result);
    break;


case "save/ver":
    $sfCode = $_GET['sfCode'];
    $ver = $_GET['ver'];
    $today = date('Y-m-d H:i:s');
    $query = "insert into Trans_Version_Used select '" . $sfCode . "','".$today."','".$ver."'";

   outputJSON(performQuery($query)); 
        break;
		
case "save/ProductBrand":
    $sfCode = $_GET['sfCode'];
    $ver = $_GET['ver'];
    $today = date('Y-m-d H:i:s');
	
	$div = $_GET['divisionCode'];
           $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    $data = json_decode($_POST['data'], true);
    $query = "exec Sv_RCPA_Brand '" . $Owndiv . "','".$data["CompiName"]."','".$data["Compi_Code"]."' ,'".$data["Pname"]."' ";

    performQuery($query); 

 $result["success"] = true;
 outputJSON($result);
        break;
		
		
case "get/FieldDemoCategory":
  $div = $_GET['divisionCode'];
           $divs = explode(",", $div . ",");
 $Owndiv = (string) $divs[0];
           
        
         $query="select * from FieldDemoCategory where Divsion_Code='".$Owndiv."'";
        outputJSON(performQuery($query));
break;

case "get/BreedCategory":
  $div = $_GET['divisionCode'];
           $divs = explode(",", $div . ",");
 $Owndiv = (string) $divs[0];
           
        
         $query="select * from FieldDemoBreed where Divsion_Code='".$Owndiv."'";
        outputJSON(performQuery($query));
break;

case "get/SupplierMster":
  $div = $_GET['divisionCode'];
           $divs = explode(",", $div . ",");
 $Owndiv = (string) $divs[0];
            $sfCode = $_GET['sfCode'];
 $SF_code = $_GET['SF_code'];
 /* if($_GET['SF_Type']==0){
 $query ="SELECT sf_code = STUFF((SELECT     ',' + Sf_Code FROM mas_salesforce  where Reporting_To_SF='".$sfCode."'   FOR XML PATH ('')), 1, 1, '' ) FROM mas_salesforce   where Reporting_To_SF='".$sfCode."' " ;
 $SS_Sfcode=performQuery($query);
 $SS_map=$SS_Sfcode[0]['sf_code'].',';
 }else{
	$SS_map= $sfCode;
 } */
  

  
 //$query="select S_No id, S_Name name from supplier_master where charindex(','+sf_Code+',',','+'".$SS_map."'+',')>0  ";
 $query="exec [GetSuperStockistName] '" . $sfCode . "','" .  $Owndiv . "'  ";
 
outputJSON(performQuery($query));
break;

case "get/GiftCard":
        $div = $_GET['divisionCode'];
        $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
         $query="select Gift_Name as name,Gift_Code as id  from Mas_Gift where Division_Code='" . $Owndiv . "'";
        outputJSON(performQuery($query));
break;

case "get/Mas_ClaimType":
       
         $query="select name as name,id,S_name from Mas_ClaimType";
        outputJSON(performQuery($query));
break;

case "get/Mas_RCPA":
$div = $_GET['divisionCode'];
       $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
        $query="select Product_Name as name,Sl_No as id  from Trans_RCPA_Product where Division_Code='" . $Owndiv . "'";

        outputJSON(performQuery($query));
break;
case "get/Mas_RCPA_Brand":
$div = $_GET['divisionCode'];
       $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
        $query="select Brand_Name as name,Sl_No as id ,Product_Id  from Trans_RCPA_Brand where Division_Code='" . $Owndiv . "'";

        outputJSON(performQuery($query));
break;
 case "get/mas_payment":
$div = $_GET['divisionCode'];
       $divs = explode(",", $div . ",");
        $Owndiv = (string) $divs[0];
                $query = "select  id,Name name from Tarns_Payment_Type where Division_code='" . $Owndiv . "' and ActiveFlag=0";
               outputJSON(performQuery($query));
                break;
case "get/geotag":
	outputJSON(ViewGeoTag());
break;				
case "get/Productreason":
     $div = $_GET['divisionCode'];
     $divs = explode(",", $div . ",");
     $Owndiv = (string) $divs[0];
     $Trans_Sl_No = $_GET['Trans_Sl_No'];
     $query = "select Trans_Sl_No,Product_Code,Remarks,Trans_Order_No,Remarks_Id from Trans_Product_Remarks where Trans_Sl_No='" . $Trans_Sl_No . "'";
	 
               outputJSON(performQuery($query));
                break;
case "get/tracksetup";
	      outputJSON(getTrackSetUP());
	      break;
				
case "get/RetailerPaymentCollection":
      $SF_code = $_GET['sfCode'];
      $today = date('Y-m-d');
     $query = "select *  from  Trans_Payment_Detail where    sf_code='" .  $SF_code  . "'  and convert(date,eDate)='" .  $today   . "'";
	 
               outputJSON(performQuery($query));
			   
                break;
case "get/LitterVsValues":
      $SF_code = $_GET['SF_Code'];
	  
      $today =  $_GET['Date'];
	  
      $query = "exec getLittervsValue '" .  $SF_code  . "' ,'" .  $today . "' ";
	 
     outputJSON(performQuery($query));
			   
            break;	
case "get/GoogleLogin":
			 $Email = $_GET['Email'];
			$sql = "SELECT Sf_UserName,Sf_Password  from mas_salesforce where SF_Email='".$Email."' ";
             $res = performQuery($sql);
					
			if (count($res) > 0) {
				$result["Data"]=$res;
				$result["success"]=true;
				 $result["Query"]=$sql;
			}else{
		
			   $result["Query"]=$sql;
			   $result["success"]=false;
			 }
		outputJSON($result);
					
break;
case "get/ViewReport":
		$Sf_code = $_GET['Sf_code'];
		$fromdate = $_GET['fromdate'];
		$todate = $_GET['todate'];
		$sql = "exec get_pri_order '".$Sf_code."','".$fromdate."','".$todate."' ";
		$res = performQuery($sql);
		$result["Data"]=$res;
		$ostatus = array("Credit Raised", "Credit Verified", "Credit Dispatched","Payment Done","Payment Verified","Order Dispatched","Payment Pending");
		foreach($ostatus as $ostval){
			$sum=0;
			$new_array = array_values(array_filter($res, function($obj) use($ostval){
							if(($obj["Order_Status"] === $ostval)) return true;
						}));
			foreach($new_array as $key=>$value){
				if(isset($value["Order_Value"]))
				{					
				 $sum += $value["Order_Value"];
	  
				}
			}
			$result[$ostval]=$sum;
		}
		outputJSON($result);
break;
case "get/ViewReport_Details":
			 $Sf_code = $_GET['Sf_code'];
			 $Order_Id = $_GET['Order_Id'];
			
			 $sql = "exec get_pri_order_dts '".$Order_Id."','".$Sf_code."' ";
             $res = performQuery($sql);
			$result["Data"]=$res;
			outputJSON($result);					
break;
case "get/secviewreport":
		$Sf_code = $_GET['Sf_code'];
		$fromdate = $_GET['fromdate'];
		$todate = $_GET['todate'];
		$sql = "exec get_sec_order_App '".$Sf_code."','".$fromdate."','".$todate."' ";
		$res = performQuery($sql);
		$result["Data"]=$res;
		$ostatus = array("Credit Raised", "Credit Verified", "Credit Dispatched","Payment Done","Payment Verified","Order Dispatched","Payment Pending","Dispatch Pending");
		foreach($ostatus as $ostval){
			$sum=0;
			$new_array = array_values(array_filter($res, function($obj) use($ostval){
							if(($obj["Order_Status"] === $ostval)) return true;
						}));
			foreach($new_array as $key=>$value){
				if(isset($value["Order_Value"]))
				{					
				 $sum += $value["Order_Value"];
	  
				}
			}
			$result[$ostval]=$sum;
		}
	outputJSON($result);					
break;
case "get/secviewreport_details":
			 $Sf_code = $_GET['Sf_code'];
			 $Order_Id = $_GET['Order_Id'];
			
			 $sql = "exec get_sec_order_dets_App '".$Order_Id."','".$Sf_code."' ";
             $res = performQuery($sql);
			$result["Data"]=$res;
	outputJSON($result);
	break;
	case "get/StockistGLogin":
		$Email = $_GET['Email'];  //CASE WHEN  UsrDfd_UserName='HAPHTNSAO2' THEN '18.7357416:78.9700081' ELSE '12.91729615:80.23030043' END 
		//$sql = "SELECT  Username Sf_UserName,ms.State_Code,Stockist_Code Sf_Code,Stockist_Code,Cut_Off_Time,Stockist_Mobile,Stockist_Name Sf_Name,[Password],ms.Division_Code div_Code,md.Division_Code sup_code,md.Division_Name sup_name,ms.Stockist_Address,isnull(md.Division_Add1,'')+','+isnull(md.Division_Add2,'')+','+isnull(Division_City,'')sup_addr from mas_stockist ms inner join access_master am on  charindex(','+  cast(am.division_code as varchar) +',',','+ms.Division_Code+',')>0  inner join   access_table AT on At.Sf_code= ms.Sf_code left join mas_division md on ms.Division_Code=md.Division_Code where  Username='".$Email."'";
		$sql = "exec getloginappdets '$Email',''";
		$res = performQuery($sql);
			
		if (count($res) > 0) {
			$logintype=$res[0]["logintype"];
			$result["Data"]=$res;
			$result["success"]=true;
			$result["logintype"]=$logintype;
			$result["Query"]=$sql;
		}else{
		   $result["Msm"]="Email is Not Valid";
		   $result["success"]=false;  
		}
		outputJSON($result);
		break;
	case "save/pripaymentverify":
		$Sf_code = $_GET['Sf_code'];
		$Order_Id = $_GET['Order_Id'];
		$sql="exec savePaymentVerification '$Order_Id','$Sf_code'";
		performQuery($sql);
		$resp["success"] = true;
		$resp["Query"] =$sql;
		outputJSON($resp);
	break;
	case "get/getprilgorderdets":
		$OrderID = $_GET['OrderID'];
		$sql = "select Trans_POrd_No Slno,Trans_Sl_No OrderID,Product_Code PCode,Product_Name,CQty OldCQty,Gross_Value Oldvalue,Rate,Cl_bal,Product_Unit_Name Unit,
				0 newCQty,0 Newvalue,Free,discount_price,case when Free>0 or discount_price>0 then 'Yes' else 'No' end as SchemeAvail,Offer_Product_Unit,Offer_ProductNm
				from Trans_PriOrder_Details where Trans_Sl_No='$OrderID'";
		$Orders = performQuery($sql);
		if(count($Orders)>0){
			$res["success"]=true;
			$res["Order"]=$Orders;
		}
		else{
			$res["success"]=false;
			$res["Order"]=[];
			$res["msg"]="No Records Found";
		}
		outputJSON($res);
	break;
	case "get/getpripaymentverfiedlg":
		$Sf_code = $_GET['Sf_code'];
		$Div = $_GET['DivCode'];		
		$divs = explode(",", $Div . ",");
		$Owndiv = (string) $divs[0];
		$sql = "exec getVerifiedLGPriamryPayments '$Sf_code','$Owndiv'";
		$res = performQuery($sql);
		if (count($res) > 0) {
			$result["Data"]=$res;
			$result["success"]=true;
		}
		else{
			$result["Data"]=[];
		   $result["success"]=false;
		}
		outputJSON($result);			
	break;
	case "get/getdispatchedpripaymentverfied":
		$Sf_code = $_GET['Sf_code'];
		$Div = $_GET['DivCode'];		
		$divs = explode(",", $Div . ",");
		$Owndiv = (string) $divs[0];
		$sql = "exec getDispatchedPriamryPayments '$Sf_code','$Owndiv'";
		$res = performQuery($sql);
		if (count($res) > 0) {
			$result["Data"]=$res;
			$result["success"]=true;
		}
		else{
			$result["Data"]=[];
		   $result["success"]=false;
		}
		outputJSON($result);			
	break;
	case "get/getpripaymentverfied":
		$Sf_code = $_GET['Sf_code'];
		$Div = $_GET['DivCode'];		
		$divs = explode(",", $Div . ",");
		$Owndiv = (string) $divs[0];
		$sql = "exec getVerifiedPriamryPayments '$Sf_code','$Owndiv'";
		$res = performQuery($sql);
		if (count($res) > 0) {
			$result["Data"]=$res;
			$result["success"]=true;
		}
		else{
			$result["Data"]=[];
		   $result["success"]=false;
		}
		outputJSON($result);			
	break;
	case "save/editpriorderlg":
		outputJSON(svLogisticsEditOrder());
	break;
	case "get/getpripaymentverfication":
		$Sf_code = $_GET['Sf_code'];
		$Div = $_GET['DivCode'];		
		$divs = explode(",", $Div . ",");
		$Owndiv = (string) $divs[0];
		$sql = "exec getVPendingPriamryPayments '$Sf_code','$Owndiv'";
		$res = performQuery($sql);
		if (count($res) > 0) {
			$result["Data"]=$res;
			$result["success"]=true;
		}
		else{
			$result["Data"]=[];
		   $result["success"]=false;
		}
		outputJSON($result);			
	break;
	case "get/paymentkeys":
		$Sf_code = $_GET['Stockist_Code'];
		$Div = $_GET['DivCode'];		
		$divs = explode(",", $Div . ",");
		$Owndiv = (string) $divs[0];
		$sql = "select isnull(KeyID,'asdfagag')KeyID,isnull(Key_Secret,'safg')Key_Secret,Stockist_Code from Mas_Stockist st left outer join Mas_Payment_Keys ky on ky.Sl_No=st.Payment_Key_ID where Stockist_Code='$Sf_code'";
		$res = performQuery($sql);
		if (count($res) > 0) {
			$result["Data"]=$res;
			$result["success"]=true;
		}
		else{
			$result["Data"]=[];
		   $result["success"]=false;
		}
		outputJSON($result);			
	break;	
	case "get/GoogleUserLogin":
		$Username = $_GET['Username'];  
		$Password = $_GET['Password'];
		//$sql = "SELECT  Username Sf_UserName,ms.State_Code,Stockist_Code Sf_Code,Stockist_Code,Cut_Off_Time,Stockist_Mobile,Stockist_Name Sf_Name,[Password],ms.Division_Code div_Code,md.Division_Code sup_code,md.Division_Name sup_name,ms.Stockist_Address,isnull(md.Division_Add1,'')+','+isnull(md.Division_Add2,'')+','+isnull(Division_City,'')sup_addr from mas_stockist ms inner join access_master am on  charindex(','+  cast(am.division_code as varchar) +',',','+ms.Division_Code+',')>0  inner join   access_table AT on At.Sf_code= ms.Sf_code left join mas_division md on ms.Division_Code=md.Division_Code where Username='$Username' and Password='$Password'";
		$sql = "exec getloginappdets '$Username','$Password'";
		$res = performQuery($sql);			
			
		if (count($res) > 0) {
			$logintype=$res[0]["logintype"];
			$result["Data"]=$res;
			$result["success"]=true;
			$result["logintype"]=$logintype;
			$result["Query"]=$sql;
		}else{
			$result["Data"]=[];
		   $result["Msm"]="Check Username and Password";
		   $result["success"]=false;
		}
		outputJSON($result);			
		break;
	case "get/paymentmode":   
		$sql = "select Code,Name from Mas_Payment_Type where PaymentMode='Offline' and CHARINDEX(',1,',','+Division_Code+',')>0";
		$res = performQuery($sql);
		if (count($res) > 0) {
			$result["Data"]=$res;
			$result["success"]=true;
			$result["Query"]=$sql;
		}else{
			$result["Data"]=[];
		   $result["Msm"]="No Data Found";
		   $result["success"]=false;
		}
		outputJSON($result);			
		break;	
		case "upload/paymentimg":
			outputJSON(PrimaryPaymentImgUpload());
		break;	
		case "save/primarypayment":
			outputJSON(PrimaryPaymentSave());
		break;		
		case "save/dispatchpayment":
			outputJSON(DispatchPaymentSave());
		break;		

case "save/newtrack":
    $data = json_decode($_POST['data'], true);
	$sfCode = (string) $data['SF'];
	$sql = "select sf_emp_id,Employee_Id from Mas_Salesforce where Sf_Code='$sfCode'";
            $sf = performQuery($sql);
            $empid = $sf[0]['sf_emp_id'];
            $employeeid = $sf[0]['Employee_Id'];
		$date = (string) $data['date'];
		$lat = (string) $data['latitude'];
		$lng = (string) $data['longitude'];
		$acc = (string) $data['theAccuracy'];
		$bat = (string) $data['Btry'];
		$mock = (string) $data['mock'];

		$sql = "insert into tbTrackLoction(SF_code,Emp_Id,Employee_Id,DtTm,Lat,Lon,Addr,Auc,deg,DvcID,Battery,Mock) select '$sfCode','$empid','$employeeid','" . $date . "','" . $lat . "','" . $lng . "','null','" . $acc . "','null','null','".$bat."','".$mock."'";

 performQuery($sql);
$result["success"]=true;
outputJSON($result);

	break;
	
	case "save/trackdetail";
        outputJSON(svTrackDetail());
        break;	
		
	case "get/ProfileMas_Stockist":
		$stockiest_code= $_GET['Stockist_Code'];
        $stockiest_code = explode(",", $stockiest_code . ",");
		$Ownstockiest_code = (string) $stockiest_code[0];
		$query="exec getStockistProfile '".$Ownstockiest_code."'";
		$res = performQuery($query);			
		if (count($res) > 0) {
			$result["Data"]=$res;
			$result["success"]=true;
		}else{
		   $result["Msg"]="No Records Found";
		   $result["success"]=false;
		}
		outputJSON($result);
	break;	
	case "get/productuom":
    $div = $_GET['divisionCode'];
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    
    $query = "select  UOM_Id id,PCode Product_Code,UOM_Nm name,CnvQty ConQty  from vwMas_MUnitDets where Division_Code='" . $Owndiv . "'";
    $res = performQuery($query);
		if (count($res) > 0) {
			$result["success"]=true;
			$result["Data"]=$res;
		}
		else{
		   $result["success"]=false;
		   $result["Msg"]="No Records Found";
		}
		outputJSON($result);
	break;	
	case "get/finreport":
    $div = $_GET['divisionCode'];
	$fromdate = $_GET['fromdate'];
	$todate = $_GET['todate'];
	
    $divs = explode(",", $div . ",");
    $Owndiv = (string) $divs[0];
    
    $query = "select th.Stockist_Code stockist_Code,st.Stockist_Name,st.ERP_Code,ord.Trans_Sl_No Order_No,
convert(varchar,ord.Order_Date,103) Order_Date,convert(varchar,th.PaymentDate, 103) PaymentDate,
td.Order_Value,isnull(td.Invoice_Amount,0)Invoice_Amount,th.PaymentMode,case when ord.Payment_Flag=0 then 'Verification Pending' 
when ord.Payment_Flag=1 then 'Payment Verified' when ord.Payment_Flag=2 then 'Order Dispatched' end as [Order_Status]
from Trans_PriOrder_Payment_Head th
inner join Trans_PriOrder_Payment_detail td on td.FSlNO=th.SlNo
inner join Trans_PriOrder_Head ord on ord.Trans_Sl_No=td.OrderID
inner join Mas_stockist st on st.Stockist_Code=th.Stockist_Code
			where ord.Division_Code='".$div."' and convert(date,ord.Order_Date) between '".$fromdate."' and '".$todate."'";
    $res = performQuery($query);
		if (count($res) > 0) {
			$result["success"]=true;
			$result["Data"]=$res;
		}
		else{
		   $result["success"]=false;
			$result["Data"]=[];
		   $result["Msg"]="No Records Found";
		}
		outputJSON($result);
	break;
 
}
?>