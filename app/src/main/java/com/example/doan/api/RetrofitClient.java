package com.example.doan.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofitWithGson = null;
    private static Retrofit retrofitForText = null;
    private static final String PREFS_NAME = "MyAppPrefs";

    // Dùng cho các endpoint trả về JSON
    public static ApiService getApiService(Context context) {
        if (retrofitWithGson == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        String token = prefs.getString("token", null);

                        Request.Builder requestBuilder = originalRequest.newBuilder();
                        if (token != null) {
                            requestBuilder.addHeader("Authorization", "Bearer " + token);
                        }

                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            retrofitWithGson = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:9090/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithGson.create(ApiService.class);
    }

    // Dùng cho các endpoint trả về text/plain nhưng gửi JSON
    public static ApiService getApiServiceForText(Context context) {
        if (retrofitForText == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        String token = prefs.getString("token", null);

                        Request.Builder requestBuilder = originalRequest.newBuilder();
                        if (token != null) {
                            requestBuilder.addHeader("Authorization", "Bearer " + token);
                        }

                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            retrofitForText = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:9090/")
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitForText.create(ApiService.class);
    }
}