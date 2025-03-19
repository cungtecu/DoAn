package com.example.doan;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "odercoffee.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, "odercoffee", null, 5);
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
                "role TEXT NOT NULL DEFAULT 'Customer','Admin', " +
                "reset_code TEXT, " +
                "reset_expiry DATETIME, " +
                "reset_status TEXT DEFAULT 'Unused' )");

        // Categories
        db.execSQL("CREATE TABLE Categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "description TEXT )");

        //  Products
        db.execSQL("CREATE TABLE Products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "price REAL NOT NULL, " +
                "image TEXT, " +
                "category_id INTEGER, " +
                "is_deleted INTEGER DEFAULT 0, " +
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
                "itemtotalprice REAL NOT NULL, " +
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
// LICH SU
        db.execSQL("CREATE TABLE PriceHistory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_id INTEGER NOT NULL, " +
                "old_price REAL NOT NULL, " +
                "new_price REAL NOT NULL, " +
                "changed_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE )");

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
        // Point
        db.execSQL("CREATE TABLE SystemConfig (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "from_value_price REAL NOT NULL, " +
                "to_value_point REAL NOT NULL, " +
                "from_date DATETIME NOT NULL, " +
                "thru_date DATETIME, " +
                "currency_id INTEGER NOT NULL, " +
                "FOREIGN KEY (currency_id) REFERENCES Currencies(id) )");


        // Currencies
        db.execSQL("CREATE TABLE Currencies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "currency_code TEXT NOT NULL UNIQUE, " +
                "currency_name TEXT NOT NULL )");
        // Review
        db.execSQL("CREATE TABLE Reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "rating INTEGER CHECK (rating >= 1 AND rating <= 5), " +
                "comment TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE)");




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS Orders");
        db.execSQL("DROP TABLE IF EXISTS OrderDetails");
        db.execSQL("DROP TABLE IF EXISTS LoyaltyPoints");
        db.execSQL("DROP TABLE IF EXISTS PriceHistory");
        db.execSQL("DROP TABLE IF EXISTS Payments");
        db.execSQL("DROP TABLE IF EXISTS Cart");
        db.execSQL("DROP TABLE IF EXISTS Currencies");
        db.execSQL("DROP TABLE IF EXISTS SystemConfig");
        db.execSQL("DROP TABLE IF EXISTS Reviews");
        onCreate(db);
    }
}
