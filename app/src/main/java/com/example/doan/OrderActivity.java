//package com.example.doan;
//
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.util.Log;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.doan.models.Category;
//import com.example.doan.models.Product;
//import java.util.ArrayList;
//import java.util.List;
//
//public class OrderActivity extends AppCompatActivity {
//
//    private static final String TAG = "OrderActivity";
//    private RecyclerView categoryRecyclerView, productRecyclerView;
//    private CategoryAdapter categoryAdapter;
//    private ProductAdapter productAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order);
//
//        // Ánh xạ RecyclerView cho danh mục
//        categoryRecyclerView = findViewById(R.id.category_recycler_view);
//        if (categoryRecyclerView == null) {
//            Log.e(TAG, "category_recycler_view not found in layout");
//            return; // Thoát nếu không tìm thấy RecyclerView
//        }
//        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        categoryAdapter = new CategoryAdapter(getCategoryList(), new CategoryAdapter.OnCategoryClickListener() {
//            @Override
//            public void onCategoryClick(int categoryId) {
//                productAdapter.updateProducts(getProductsByCategory(categoryId));
//            }
//        });
//        categoryRecyclerView.setAdapter(categoryAdapter);
//
//        // Ánh xạ RecyclerView cho sản phẩm
//        productRecyclerView = findViewById(R.id.product_recycler_view);
//        if (productRecyclerView == null) {
//            Log.e(TAG, "product_recycler_view not found in layout");
//            return; // Thoát nếu không tìm thấy RecyclerView
//        }
//        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Dạng lưới 2 cột
//        productAdapter = new ProductAdapter(this, getProductList());
//        productRecyclerView.setAdapter(productAdapter);
//    }
//
//    // Load danh mục từ database hoặc dữ liệu mẫu
//    private List<Category> getCategoryList() {
//        List<Category> categoryList = new ArrayList<>();
//        try {
//            DatabaseHelper dbHelper = new DatabaseHelper(this);
//            SQLiteDatabase db = dbHelper.getReadableDatabase();
//            Cursor cursor = db.rawQuery("SELECT id, name, image, description FROM categories", null);
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image"));
//                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
//                    categoryList.add(new Category(id, name, imageResId, description));
//                }
//                cursor.close();
//            }
//            db.close();
//        } catch (Exception e) {
//            Log.e(TAG, "Error loading categories from database: " + e.getMessage());
//        }
//
//        // Dữ liệu mẫu nếu database trống
//        if (categoryList.isEmpty()) {
//            Log.d(TAG, "Database empty, using sample category data");
//            categoryList.add(new Category(1, "Bánh ngọt", R.drawable.cat_cake, "Các loại bánh ngọt ngon"));
//            categoryList.add(new Category(2, "Cà Phê", R.drawable.cat_coffee, "Cà phê đậm đà"));
//            categoryList.add(new Category(3, "Cà phê nóng", R.drawable.cat_hot_coffee, "Cà phê nóng thơm lừng"));
//            categoryList.add(new Category(4, "Đá xay", R.drawable.cat_iceblended, "Đồ uống mát lạnh"));
//            categoryList.add(new Category(5, "Phindi", R.drawable.cat_phindi, "Phindi đặc biệt"));
//            categoryList.add(new Category(6, "Trà", R.drawable.cat_tea, "Trà thơm ngon"));
//        }
//        return categoryList;
//    }
//
//    // Load sản phẩm từ database hoặc dữ liệu mẫu
//    private List<Product> getProductList() {
//        List<Product> productList = new ArrayList<>();
//        try {
//            DatabaseHelper dbHelper = new DatabaseHelper(this);
//            SQLiteDatabase db = dbHelper.getReadableDatabase();
//            Cursor cursor = db.rawQuery("SELECT id, name, description, price, image, categoryId, isDeleted FROM products WHERE isDeleted = 0", null);
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
//                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
//                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image"));
//                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
//                    int isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow("isDeleted"));
//                    productList.add(new Product(id, name, description, price, imageResId, categoryId, isDeleted));
//                }
//                cursor.close();
//            }
//            db.close();
//        } catch (Exception e) {
//            Log.e(TAG, "Error loading products from database: " + e.getMessage());
//        }
//
//        // Dữ liệu mẫu nếu database trống
//        if (productList.isEmpty()) {
//            Log.d(TAG, "Database empty, using sample product data");
//            productList.add(new Product(1, "Trà sữa", "Trà sữa thơm ngon", 50000, R.drawable.product1, 6, 0));
//            productList.add(new Product(2, "Cà phê sữa", "Cà phê sữa đậm đà", 45000, R.drawable.product2, 2, 0));
//            productList.add(new Product(3, "Đá xay", "Đá xay mát lạnh", 60000, R.drawable.product3, 4, 0));
//            productList.add(new Product(4, "Matcha", "Matcha nguyên chất", 55000, R.drawable.product4, 6, 0));
//        }
//        return productList;
//    }
//
//    // Lọc sản phẩm theo categoryId
//    private List<Product> getProductsByCategory(int categoryId) {
//        List<Product> filteredList = new ArrayList<>();
//        try {
//            DatabaseHelper dbHelper = new DatabaseHelper(this);
//            SQLiteDatabase db = dbHelper.getReadableDatabase();
//            Cursor cursor = db.rawQuery("SELECT id, name, description, price, image, categoryId, isDeleted FROM products WHERE categoryId = ? AND isDeleted = 0", new String[]{String.valueOf(categoryId)});
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
//                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
//                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image"));
//                    int catId = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
//                    int isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow("isDeleted"));
//                    filteredList.add(new Product(id, name, description, price, imageResId, catId, isDeleted));
//                }
//                cursor.close();
//            }
//            db.close();
//        } catch (Exception e) {
//            Log.e(TAG, "Error filtering products by category: " + e.getMessage());
//        }
//        return filteredList;
//    }
//}
package com.example.doan;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.models.Category;
import com.example.doan.models.Product;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ánh xạ RecyclerView cho danh mục
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        if (categoryRecyclerView == null) {
            Log.e(TAG, "category_recycler_view not found in layout");
            return;
        }
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Category> categoryList = getCategoryList();
        categoryAdapter = new CategoryAdapter(categoryList, categoryId -> {
            Log.d(TAG, "Category clicked: " + categoryId);
            List<Product> filteredProducts = getProductsByCategory(categoryId);
            productAdapter.updateProducts(filteredProducts);
        });
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Ánh xạ RecyclerView cho sản phẩm
        productRecyclerView = findViewById(R.id.product_recycler_view);
        if (productRecyclerView == null) {
            Log.e(TAG, "product_recycler_view not found in layout");
            return;
        }
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, getProductList());
        productRecyclerView.setAdapter(productAdapter);
    }

    // Load danh mục từ database hoặc dữ liệu mẫu
    private List<Category> getCategoryList() {
        List<Category> categoryList = new ArrayList<>();
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, name, image, description FROM categories", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    categoryList.add(new Category(id, name, imageResId, description));
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error loading categories from database: " + e.getMessage());
        }

        // Dữ liệu mẫu nếu database trống
        if (categoryList.isEmpty()) {
            Log.d(TAG, "Database empty, using sample category data");
            categoryList.add(new Category(1, "Bánh ngọt", R.drawable.cat_cake, "Các loại bánh ngọt ngon"));
            categoryList.add(new Category(2, "Cà Phê", R.drawable.cat_coffee, "Cà phê đậm đà"));
            categoryList.add(new Category(3, "Cà phê nóng", R.drawable.cat_hot_coffee, "Cà phê nóng thơm lừng"));
            categoryList.add(new Category(4, "Đá xay", R.drawable.cat_iceblended, "Đồ uống mát lạnh"));
            categoryList.add(new Category(5, "Phindi", R.drawable.cat_phindi, "Phindi đặc biệt"));
            categoryList.add(new Category(6, "Trà", R.drawable.cat_tea, "Trà thơm ngon"));
        }
        return categoryList;
    }

    // Load sản phẩm từ database hoặc dữ liệu mẫu
    private List<Product> getProductList() {
        List<Product> productList = new ArrayList<>();
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, name, description, price, image, categoryId, isDeleted FROM products WHERE isDeleted = 0", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image"));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
                    int isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow("isDeleted"));
                    productList.add(new Product(id, name, description, price, imageResId, categoryId, isDeleted));
                }
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error loading products from database: " + e.getMessage());
        }

        // Dữ liệu mẫu nếu database trống
        if (productList.isEmpty()) {
            Log.d(TAG, "Database empty, using sample product data");
            productList.add(new Product(2, "Trà sữa", "Trà sữa thơm ngon", 50000, R.drawable.product1, 6, 0));
            productList.add(new Product(3, "Cà phê sữa", "Cà phê sữa đậm đà", 45000, R.drawable.product2, 2, 0));
            productList.add(new Product(4, "Đá xay", "Đá xay mát lạnh", 60000, R.drawable.product3, 4, 0));
            productList.add(new Product(5, "Matcha", "Matcha nguyên chất", 55000, R.drawable.product4, 6, 0));
        }
        return productList;
    }

    // Lọc sản phẩm theo categoryId
    private List<Product> getProductsByCategory(int categoryId) {
        List<Product> filteredList = new ArrayList<>();
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, name, description, price, image, categoryId, isDeleted FROM products WHERE categoryId = ? AND isDeleted = 0", new String[]{String.valueOf(categoryId)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow("image"));
                    int catId = cursor.getInt(cursor.getColumnIndexOrThrow("categoryId"));
                    int isDeleted = cursor.getInt(cursor.getColumnIndexOrThrow("isDeleted"));
                    filteredList.add(new Product(id, name, description, price, imageResId, catId, isDeleted));
                }
                cursor.close();
            }
            db.close();
            Log.d(TAG, "Filtered products for category " + categoryId + ": " + filteredList.size());
        } catch (Exception e) {
            Log.e(TAG, "Error filtering products by category: " + e.getMessage());
        }
        return filteredList;
    }
}