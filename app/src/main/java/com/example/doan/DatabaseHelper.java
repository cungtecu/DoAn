package com.example.doan;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "odercoffee.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, "odercoffee", null, 1);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String dbPath = db.getPath();
        System.out.println("Database Path: " + dbPath);
    }
    public void onCreate(SQLiteDatabase db) {
        //  Users
        db.execSQL("CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT UNIQUE, " +
                "password TEXT NOT NULL, " +
                "points INTEGER DEFAULT 0, " +
                "role TEXT NOT NULL DEFAULT 'Customer' )");

        // Categories
        db.execSQL("CREATE TABLE Categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "image TEXT, " +
                "description TEXT )");

        //  Products
        db.execSQL("CREATE TABLE Products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "image TEXT, " +
                "category_id INTEGER, " +
                "FOREIGN KEY (category_id) REFERENCES Categories(id) )");

        //  Orders
        db.execSQL("CREATE TABLE Orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "total_price REAL NOT NULL, " +
                "status TEXT DEFAULT 'Pending', " +
                "order_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES Users(id) )");

        //  OrderDetails
        db.execSQL("CREATE TABLE OrderDetails (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "product_id INTEGER, " +
                "quantity INTEGER NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES Orders(id), " +
                "FOREIGN KEY (product_id) REFERENCES Products(id) )");

        // LoyaltyPoints
        db.execSQL("CREATE TABLE LoyaltyPoints (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "points INTEGER NOT NULL, " +
                "description TEXT, " +
                "date_added DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES Users(id) )");

        // Payments
        db.execSQL("CREATE TABLE Payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "payment_method TEXT NOT NULL, " +
                "payment_status TEXT DEFAULT 'Pending', " +
                "payment_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (order_id) REFERENCES Orders(id) )");

        //  Cart
        db.execSQL("CREATE TABLE Cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL DEFAULT 1, " +
                "added_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE )");

        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS Orders");
        db.execSQL("DROP TABLE IF EXISTS OrderDetails");
        db.execSQL("DROP TABLE IF EXISTS LoyaltyPoints");
        db.execSQL("DROP TABLE IF EXISTS Payments");
        db.execSQL("DROP TABLE IF EXISTS Cart");
        onCreate(db);
    }
    // Thêm người dùng mới
    public void insertUser(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        db.insert("users", null, values);
        db.close();
    }

    // Method to get all products
    private void insertSampleData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        // Thêm danh mục
        values.put("id", 1);
        values.put("name", "Bánh ngọt");
        values.put("image", R.drawable.cat_cake);
        values.put("description", "Các loại bánh ngọt ngon");
        db.insert("categories", null, values);

        values.clear();
        values.put("id", 2);
        values.put("name", "Cà Phê");
        values.put("image", R.drawable.cat_coffee);
        values.put("description", "Cà phê đậm đà");
        db.insert("categories", null, values);

        values.clear();
        values.put("id", 3);
        values.put("name", "Cà phê nóng");
        values.put("image", R.drawable.cat_hot_coffee);
        values.put("description", "Cà phê nóng thơm lừng");
        db.insert("categories", null, values);

        values.clear();
        values.put("id", 4);
        values.put("name", "Đá xay");
        values.put("image", R.drawable.cat_iceblended);
        values.put("description", "Đồ uống mát lạnh");
        db.insert("categories", null, values);

        values.clear();
        values.put("id", 5);
        values.put("name", "Phindi");
        values.put("image", R.drawable.cat_phindi);
        values.put("description", "Phindi đặc biệt");
        db.insert("categories", null, values);

        // Thêm sản phẩm
        values.clear();
        values.put("id", 1);
        values.put("name", "Cà Phê đen đá");
        values.put("description", "Our dark, rich espresso combined with milk and served over ice.");
        values.put("price", 35000);
        values.put("image", R.drawable.product2);
        values.put("categoryId", 2);
        values.put("isDeleted", 0);
        db.insert("products", null, values);

        values.clear();
        values.put("id", 2);
        values.put("name", "Cà phê sữa đá");
        values.put("description", "Trà sữa thơm ngon");
        values.put("price", 40000);
        values.put("image", R.drawable.product1);
        values.put("categoryId", 2);
        values.put("isDeleted", 0);
        db.insert("products", null, values);

        values.clear();
        values.put("id", 3);
        values.put("name", "Bánh tiramisu");
        values.put("description", "Our dark, rich espresso combined with milk and served over ice.");
        values.put("price", 35000);
        values.put("image", R.drawable.tiramisu_cake);
        values.put("categoryId", 1);
        values.put("isDeleted", 0);
        db.insert("products", null, values);

        values.clear();
        values.put("id", 4);
        values.put("name", "Bánh tiramisu");
        values.put("description", "Our dark, rich espresso combined with milk and served over ice.");
        values.put("price", 35000);
        values.put("image", R.drawable.mattcha_cake);
        values.put("categoryId", 1);
        values.put("isDeleted", 0);
        db.insert("products", null, values);
    }
}
