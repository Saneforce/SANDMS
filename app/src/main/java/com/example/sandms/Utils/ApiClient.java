package com.example.sandms.Utils;

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
   public static final String BASE_URL = "http://govind.sanfmcg.com/server/";
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
}
