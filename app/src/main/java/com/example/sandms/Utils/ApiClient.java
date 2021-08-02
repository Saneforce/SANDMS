package com.example.sandms.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    // public static final String BASE_URL = "http://dms.sanfmcg.com/server/";
  // public static final String BASE_URL = "http://hap.sanfmcg.com/server/";
  public static final String BASE_URL = "http://govind.sanfmcg.com/server/";//working code commented
  //  public static final String BASE_URL = "http://fmcg.sanfmcg.com/server/";//server

    public static final String BASE_URLS = "http://govind.sanfmcg.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            }
        return retrofit;
    }
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
