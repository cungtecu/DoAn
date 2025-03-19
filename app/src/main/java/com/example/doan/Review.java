package com.example.doan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Review {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public Review(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public Review(int anInt, int anInt1, int anInt2, int anInt3, String string, String string1) {
    }

    // Thêm đánh giá mới
    public boolean addReview(int orderId, int userId, int rating, String comment) {
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("user_id", userId);
        values.put("rating", rating);
        values.put("comment", comment);

        long result = db.insert("Reviews", null, values);
        return result != -1;
    }

    // Lấy đánh giá của một đơn hàng
    public List<Review> getReviewsByOrderId(int orderId) {
        List<Review> reviews = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Reviews WHERE order_id = ?", new String[]{String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            do {
                Review review = new Review(
                        cursor.getInt(0),  // id
                        cursor.getInt(1),  // order_id
                        cursor.getInt(2),  // user_id
                        cursor.getInt(3),  // rating
                        cursor.getString(4),  // comment
                        cursor.getString(5)   // created_at
                );
                reviews.add(review);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reviews;
    }
}
