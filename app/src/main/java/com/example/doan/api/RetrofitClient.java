package com.example.doan.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;

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

    // Định dạng ngày giờ từ chuỗi JSON, hỗ trợ số chữ số mili giây linh hoạt
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 9, true) // Hỗ trợ từ 0 đến 9 chữ số mili giây
            .optionalEnd()
            .toFormatter();

    // Tạo Gson với hỗ trợ LocalDateTime
    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        context.serialize(src.format(formatter)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    String dateTimeString = json.getAsString();
                    if (dateTimeString == null) {
                        return null;
                    }

                    // Xử lý các định dạng không chuẩn
                    if (dateTimeString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+")) {
                        int dotIndex = dateTimeString.indexOf('.');
                        if (dotIndex != -1) {
                            // Tính số chữ số mili giây
                            String fractionPart = dateTimeString.substring(dotIndex + 1);
                            if (fractionPart.length() < 3) {
                                // Thêm số 0 để đủ 3 chữ số mili giây
                                dateTimeString = dateTimeString + "0".repeat(3 - fractionPart.length());
                            } else if (fractionPart.length() > 3) {
                                // Cắt bớt để giữ 3 chữ số mili giây
                                dateTimeString = dateTimeString.substring(0, dotIndex + 4);
                            }
                            return LocalDateTime.parse(dateTimeString, formatter);
                        }
                    }
                    return LocalDateTime.parse(dateTimeString, formatter);
                })
                .create();
    }

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
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
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
                    .addConverterFactory(GsonConverterFactory.create(createGson()))
                    .build();
        }
        return retrofitForText.create(ApiService.class);
    }
}