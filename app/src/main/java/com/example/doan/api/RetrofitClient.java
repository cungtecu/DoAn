package com.example.doan.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
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
    private static final String TAG = "RetrofitClient";

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
                        } else {
                            Log.w(TAG, "No token found in SharedPreferences");
                        }

                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            Gson gson = createGson();

            retrofitWithGson = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:9090/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
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
                        } else {
                            Log.w(TAG, "No token found in SharedPreferences");
                        }

                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            Gson gson = createGson();

            retrofitForText = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:9090/")
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitForText.create(ApiService.class);
    }

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter();

    // Tạo Gson với hỗ trợ LocalDateTime
    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        src == null ? null : context.serialize(src.format(formatter)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    if (json.isJsonNull() || json.getAsString() == null || json.getAsString().isEmpty()) {
                        return null;
                    }
                    String dateTimeString = json.getAsString();
                    try {
                        return LocalDateTime.parse(dateTimeString, formatter);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse LocalDateTime: " + dateTimeString, e);
                        throw new RuntimeException("Invalid date format: " + dateTimeString, e);
                    }
                })
                .create();
    }
}