package com.saneforce.dms.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    // public static final String BASE_URL = "http://dms.sanfmcg.com/server/";
  // public static final String BASE_URL = "http://hap.sanfmcg.com/server/";

    //1 GOVIND
    //2 AQUA
    public static final int APP_TYPE = 1;
    public static final boolean IS_TEST_MODE = true;

    public static final String BASE_FMCG ="http://fmcg.sanfmcg.com/";
    public static final String BASE_GOVIND ="http://govind.sanfmcg.com/";
    public static final String BASE_HAP ="http://hap.sanfmcg.com/";
    public static String BASE =BASE_GOVIND;

    //  public static final String BASE_URL = "http://govind.sanfmcg.com/server/";//working code commented
    public static final String BASE_URL = BASE+"server/";//server1
    public static final String BASE_WEBVIEW = BASE;//server

//    public static final String BASE_URLS = "http://govind.sanfmcg.com/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {

        if (retrofit == null) {


            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.writeTimeout(61, TimeUnit.SECONDS)
                    .connectTimeout(61, TimeUnit.SECONDS)
                    .readTimeout(61, TimeUnit.SECONDS);

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
            OkHttpClient okHttpClient = builder.build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
            }
        return retrofit;
    }

    public static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    public static Retrofit getClient(String BASE_URLS) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URLS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
