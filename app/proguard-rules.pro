# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep public class com.billdesk.sdk.*
#-keep public class com.billdesk.config.*
#-keep public class com.billdesk.utils.URLUtilActivity
#-keep public interface com.billdesk.sdk.LibraryPaymentStatusProtocol{
# public void paymentStatus(java.lang.String,android.app.Activity);
# public void cancelTransaction();
## public void onError(java.lang.Exception arg0);
# public void tryAgain();
#}
#-keep class com.billdesk.sdk.PaymentWebView$JavaScriptInterface{
# public void gotMsg(java.lang.String);
#}
#-keepclassmembers class * {
# @android.webkit.JavascriptInterface <methods>;
#}
#-keepattributes JavascriptInterface
#-keep public class com.billdesk.sdk.PaymentWebView$JavaScriptInterface
#-keep public class * implements
#com.billdesk.sdk.PaymentWebView$JavaScriptInterface
#-keepclassmembers class
#com.billdesk.sdk.PaymentWebView$JavaScriptInterface {
# <methods>;
#}