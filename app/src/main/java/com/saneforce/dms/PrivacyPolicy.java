package com.saneforce.dms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;




public class PrivacyPolicy extends AppCompatActivity {
    TextView privacyWebView;
    TextView privacyWebView1;
    //    CheckBox privacyCheck;
//    Button privacySubmit, privacyDisable;
    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        /*webView*/
        privacyWebView =  findViewById(R.id.privacy_webview);
        privacyWebView1 =  findViewById(R.id.privacy_webview1);
        privacyWebView.setText("Payments, Refunds & Cancellation\n" +
                "\n" +
                "\n" +
                "Payments:\n" +
                "\n" +
                "1.\tOnline Payments can be done by:\n" +
                "Online Payment: Card payment and Net Banking Payment via BillDesk Payment Gateway can be done at Online Payment link on Make Payment page.\n" +
                "\n" +
                "Problem with an order or payment: Please email us at contact@govindmilk.com\n" +
                "\n" +
                "\n" +
                "\n" +
                "Refunds & Cancellation:\n" +
                "\n" +
                "1.\tRefunds initiated will be credited to the account/card from where the transaction had initiated.\n");




        privacyWebView1.setText("Terms & Conditions\n" +
                "\n" +
                "To avail the services offered at www.govindmilk.com , you must agree to the following terms and conditions:\n" +
                "\n" +
                "Please read the term and conditions carefully. While using any current or future services offered by www.govindmilk.com , whether or not included in the Govind Milk & Milk Products Pvt Ltd website, you will abide by these Terms & conditions the guidelines and conditions applicable to such service or business.\n" +
                "\n" +
                "Privacy Policy\n" +
                "Please review our Privacy Policy, which also governs your visit to www.govindmilk.com, to fully understand our practices.\n" +
                "\n" +
                "Electronic Communication\n" +
                "When you visit www.govindmilk.com or send e-mails to us, you are communicating with us electronically. By communicating with us, you consent to receive communication from us electronically. We will communicate with you by e-mail or by posting notices on our website. You agree that all agreements, notices, disclosures, and other communications that we provide to you electronically satisfy any legal requirement that such communication be in writing.\n" +
                "\n" +
                "Prices\n" +
                "All prices posted on this website are subject to change without notice. Prices prevailing at commencement of placing the order will apply. Posted prices do not include convenience fee. In case there are any additional charges or taxes the same will be mentioned on the website.\n" +
                "\n" +
                "Payment\n" +
                "Online Payments can be done by:\n" +
                "Online Payment: Card payment and Net Banking Payment via BillDesk Payment Gateway can be done at Online Payment link on Make Payment page.\n" +
                "\n" +
                "Changes and Cancellation\n" +
                "Changes and Cancellation not allowed.\n" +
                "\n" +
                "Refund\n" +
                "Refunds initiated will be credited to the account/card from where the transaction had initiated.\n" +
                "\n" +
                "\n" +
                "License and Website Access\n" +
                "\n" +
                "General:\n" +
                "www.govindmilk.com grants you a limited license to access and make personal use of this website and not to download (other than page caching) or modify it, or any portion of it, except with express written consent of www.govindmilk.com. \n" +
                "\n" +
                "No license for commercial sale:\n" +
                "This license does not include any resale or commercial use of this website or its content; any collection and use of any product listing, description, or pricing; copying of account information for the benefit of another merchant; or any use of data mining, or similar data gathering and extraction tools.\n" +
                "\n" +
                "No reproduction:\n" +
                "This website or any portion of this website may not be reproduced, duplicated, copies, sold, visited, or otherwise exploited for any commercial purpose without express written consent of www.govindmilk.com.\n" +
                "\n" +
                "No framing:\n" +
                "You may not frame or utilize framing technologies to enclose any trademark, logo, or other proprietary information (including images, text, page layout, or form) of www.govindmilk.com  without the express written consent.\n" +
                "\n" +
                "Metatags:\n" +
                "You may not use any metatags or any other 'hidden text' utilizing Supreme Enterprises name or trademarks without the express written consent of www.govindmilk.com. Any unauthorized use terminates the permission or license granted by www.govindmilk.com.\n" +
                "\n" +
                "\n" +
                "Reviews, Comments, Communications, and other content\n" +
                "\n" +
                "Nature of content:\n" +
                "Visitors to www.govindmilk.com may post content and other communications; and submit suggestions, ideas, comments, questions or other information, as long as the content is not illegal, obscene, threatening, defamatory, invasive of privacy, infringing of intellectual property rights to otherwise injuries to third party or objectionable and does not consist of or contains software virus, political campaigning, commercial solicitation, mass mailing or any form of spam.\n" +
                "False information:\n" +
                "You may not use false email address, impersonate any person or entity, or otherwise mislead as to the origin of a card or other content. www.govindmilk.com reserves the right (but not the obligation) to remove or edit such content but does not regularly review posted contents.\n" +
                "\n" +
                "Rights Granted:\n" +
                "If you do post content or submit material and unless we indicate otherwise, you grant www.govindmilk.com a non-exclusive, royalty free, perpetual, irrevocable, and fully sub-licensed right to use, reproduce, modify, adapt, publish, translate, create derivative work from, distribute, and display such content throughout the world in any media. You grant www.govindmilk.com the right to use the name that you submit in connection with such content if www.govindmilk.com chooses.\n" +
                "\n" +
                "Right Owned:\n" +
                "You represent and warrant that you own all the rights or otherwise or control all of the rights to the content that you post; that the content is accurate; that the use of the content to supply does not violate this policy and will not cause injury to any person or entity and that you will indemnify www.govindmilk.com for all claims resulting from the content you supply. www.govindmilk.com has the right but not the obligation to monitor and edit or remove any activity or content. www.govindmilk.com takes no responsibility and assumes no liability for any content posted by you or any third party.\n" +
                "\n" +
                "Site Policies, Modification, and Severability\n" +
                "Please review our other policies. We reserve the right to make changes to our website, policies, and these Terms and Conditions at any time. If any of these conditions shall be deemed invalid, void, or for any reason unenforceable, that condition shall be deemed severable and will not affect the validity and enforceability of any remaining conditions.\n" +
                "\n" +
                "\n" +
                "Intellectual Property Rights\n" +
                "\n" +
                "Copyright Protection:\n" +
                "All content included on this site, such as text, graphics, logos, button icons, audio clips, digital downloads, data compilations and software, is the property of www.govindmilk.com and protected by the Indian Copyright law. The compilation of all the content on this site is the exclusive property if www.govindmilk.com and protected by Indian Copyright law. All software used in this site is the property of www.govindmilk.com and is protected under the Indian Copyright law.\n" +
                "\n" +
                "Trademarks:\n" +
                "i.\tProtected Marks:\n" +
                "www.govindmilk.com , and other marks indicated on www.govindmilk.com  website are registered trademarks of www.govindmilk.com\n" +
                "\n" +
                "ii.\tProtected Graphics:\n" +
                "All www.govindmilk.com graphics, logos, page headers, button icons, scripts and service names are trademarks or trade dress of www.govindmilk.com.  www.govindmilk.com trademarks and trade dress may not be used in connections with any product or service that is not of www.govindmilk.com.\n" +
                "\n" +
                "Governing Law and Jurisdiction\n" +
                "These terms and conditions will be construed only in accordance with the laws of India. In respect of all matters/disputes arising out of, in connection with or in relation to these terms and conditions or any other conditions on this website, only the competent Courts at Mumbai, Mumbai shall have jurisdiction, to the exclusion of all other courts.\n" +
                "\n" +
                "Disclaimer of warranties and Limitation of Liability\n" +
                "\n" +
                "THIS SITE IS PROVIDED BY Govind Milk & Milk Products Pvt. Ltd. ON AN \"AS IS\" AND \"AS AVAILABLE\" BASIS. Govind Milk & Milk Products Pvt. Ltd. MAKES NO REPRESENTATIONS OR WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED, AS TO THE OPERATION OF THIS SITE OR THE INFORMATION, CONTENT, MATERIALS, OR PRODUCTS INCLUED ON THIS SITE. YOU EXPRESSLY AGREE THAT YOUR USE OF THIS SITE IS AT YOUR SOLE RISK.\n" +
                "\n" +
                "TO THE FULL EXTENT PERMISSIBLE BY APPLICABLE LAW, www.govindmilk.com DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. www.govindmilk.com DOES NOT WARRANT THAT THE SITE, ITS SERVERS, OR EMAIL SENT FROM www.govindmilk.com_ ARE FREE OF VIRUS OR OTHER HARMFUL COMPONENTS. www.govindmilk.com WILL NOT BE LIABLE FOR ANY DAMAGES OF ANY KIND ARISING FROM THE USE OF THIS SITE, INCLUDING, BUT NOT LIMITED TO DIRECT, INDIRECT, INCIDENTAL, PUNITIVE AND CONSEQUENTIAL DAMAGES.\n" +
                " \n" +
                "Privacy Policy\n" +
                "\n" +
                "\n" +
                "We maintain full privacy of your personal information shared with us. We never misuse any of your personal information. Our privacy policy is as given below:\n" +
                "\n" +
                "1.\tCollection and Sharing of customer information: \n" +
                "We collect information from you only after you agree to provide it. Any information you give us is held with care and security. This information is collected primarily to ensure that we ship your products without any hassles.\n" +
                "\n" +
                "We may collect your title, name, gender, email address, postal address, delivery address (if different), telephone number, mobile number, fax number, payment details or bank account details. All this information is processed securely for your protection.\n" +
                "\n" +
                "Your actual order details may be stored with us and you may access this information by logging into your account on the Site. You undertake to treat the personal access data confidentially and not make it available to unauthorized third parties.\n" +
                "\n" +
                "Under no circumstances do we rent, trade or share your personal information that we have collected with any other company for their marketing purposes without your consent. We reserve the right to communicate your personal information to any third party that makes a legally-compliant request for its disclosure.\n" +
                "\n" +
                "2.\tIntellectual Property Rights:\n" +
                "All content included on this site, such as text, graphics, logos, button icons, images and software, is the property of Govind Milk & Milk Products Pvt. Ltd All content on the Site that is not the property of Govind Milk & Milk Products Pvt. Ltd is used with permission.\n");


    }

}