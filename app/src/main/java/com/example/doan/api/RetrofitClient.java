package com.example.doan.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofitWithGson = null;
    private static Retrofit retrofitForText = null;
    private static final String PREFS_NAME = "MyAppPrefs";

    // Dùng cho các endpoint trả về JSON
    public static ApiService getApiService(Context context) {
        if (retrofitWithGson == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, "OkHttp: " + message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        String token = prefs.getString("token", null);
                        Log.d(TAG, "Interceptor: Token = " + token);
                        Request.Builder requestBuilder = originalRequest.newBuilder();
                        // Xóa header Authorization cũ để tránh trùng lặp
                        requestBuilder.removeHeader("Authorization");
                        // Bỏ qua kiểm tra token cho endpoint đăng nhập
                        if (!originalRequest.url().toString().endsWith("/api/users/login")) {
                            if (token != null && !token.isEmpty()) {
                                String authHeader = "Bearer " + token;
                                requestBuilder.addHeader("Authorization", authHeader);
                                Log.d(TAG, "Interceptor: Added Authorization = " + authHeader);
                            } else {
                                Log.w(TAG, "Interceptor: No token found in SharedPreferences");
                                if (context != null) {
                                    Intent intent = new Intent(context, com.example.doan.SigninActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                    throw new IOException("No authentication token found");
                                }
                            }
                        }
                        Request newRequest = requestBuilder.build();
                        Log.d(TAG, "Interceptor: Request URL = " + newRequest.url());
                        return chain.proceed(newRequest);
                    })
                    .addInterceptor(logging)
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
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, "OkHttp: " + message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        String token = prefs.getString("token", null);
                        Log.d(TAG, "Interceptor: Token = " + token);
                        Request.Builder requestBuilder = originalRequest.newBuilder();
                        // Xóa header Authorization cũ để tránh trùng lặp
                        requestBuilder.removeHeader("Authorization");
                        if (token != null && !token.isEmpty()) {
                            String authHeader = "Bearer " + token;
                            requestBuilder.addHeader("Authorization", authHeader);
                            Log.d(TAG, "Interceptor: Added Authorization = " + authHeader);
                        } else {
                            Log.w(TAG, "Interceptor: No token found in SharedPreferences");
                            // Chuyển người dùng về màn hình đăng nhập nếu token không tồn tại
                            if (context != null) {
                                Intent intent = new Intent(context, com.example.doan.SigninActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                                throw new IOException("No authentication token found");
                            }
                        }
                        Request newRequest = requestBuilder.build();
                        Log.d(TAG, "Interceptor: Request URL = " + newRequest.url());
                        return chain.proceed(newRequest);
                    })
                    .addInterceptor(logging)
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