package com.example.doan.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.doan.models.UserProfileDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String BASE_URL = "http://10.0.2.2:9090/";

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter();

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        context.serialize(src.format(formatter)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                    String dateTimeString = json.getAsString();
                    if (dateTimeString == null) return null;
                    if (dateTimeString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+")) {
                        int dotIndex = dateTimeString.indexOf('.');
                        if (dotIndex != -1) {
                            String fractionPart = dateTimeString.substring(dotIndex + 1);
                            if (fractionPart.length() < 3) {
                                dateTimeString = dateTimeString + "0".repeat(3 - fractionPart.length());
                            } else if (fractionPart.length() > 3) {
                                dateTimeString = dateTimeString.substring(0, dotIndex + 4);
                            }
                            return LocalDateTime.parse(dateTimeString, formatter);
                        }
                    }
                    return LocalDateTime.parse(dateTimeString, formatter);
                })
                .create();
    }

    public static ApiService getApiService(Context context) {
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
                    Log.d("RetrofitClient", "Yêu cầu: " + originalRequest.url() + ", Token: " + token);

                    Request.Builder requestBuilder = originalRequest.newBuilder();
                    if (token != null) {
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                        Log.d("RetrofitClient", "Tiêu đề Authorization: Bearer " + token);
                    }

                    Request newRequest = requestBuilder.build();
                    Response response;
                    try {
                        response = chain.proceed(newRequest);
                    } catch (IOException e) {
                        Log.e("RetrofitClient", "Lỗi kết nối đến " + newRequest.url() + ": " + e.getMessage());
                        throw new IOException("Không thể kết nối đến server: " + e.getMessage(), e);
                    }

                    Log.d("RetrofitClient", "Mã phản hồi: " + response.code());
                    if (!response.isSuccessful() && response.body() != null) {
                        ResponseBody responseBody = response.body();
                        String bodyString = responseBody.string();
                        Log.e("RetrofitClient", "Chi tiết lỗi: " + bodyString);
                        response = response.newBuilder()
                                .body(ResponseBody.create(responseBody.contentType(), bodyString))
                                .build();
                    } else if (!response.isSuccessful()) {
                        // Thêm log chi tiết nếu không có body
                        ResponseBody errorBody = response.body();
                        String errorString = errorBody != null ? errorBody.string() : "Không có body lỗi";
                        Log.e("RetrofitClient", "Lỗi không thành công, mã: " + response.code() + ", chi tiết: " + errorString);
                        response = response.newBuilder()
                                .body(ResponseBody.create(errorBody != null ? errorBody.contentType() : null, errorString))
                                .build();
                    }

                    return response;
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .build();

        return retrofit.create(ApiService.class);
    }

    public static void isTokenValid(Context context, Callback<UserProfileDTO> callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) {
            Log.d("RetrofitClient", "Không tìm thấy token trong SharedPreferences");
            callback.onFailure(null, new IOException("No token found"));
            return;
        }

        Call<UserProfileDTO> call = getApiService(context).getCurrentUser("Bearer " + token);
        call.enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, retrofit2.Response<UserProfileDTO> response) {
                Log.d("RetrofitClient", "Kiểm tra token, mã phản hồi: " + response.code());
                if (response.isSuccessful()) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException("Token invalid, code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                Log.e("RetrofitClient", "Lỗi khi kiểm tra token: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }
}