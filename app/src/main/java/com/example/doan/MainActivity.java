package com.example.doan;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.BestSellerAdapter;
import com.example.doan.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    khai báo banner
    private ViewPager2 viewPager;
    private List<Integer> imageList = Arrays.asList(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
    );
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final long SLIDE_DELAY = 3000; // Điều chỉnh tốc độ cuộn (5 giây)


//    khai báo sản phẩm bán chạy
    private RecyclerView recyclerBestSeller;
    private BestSellerAdapter bestSellerAdapter;
    private List<Product> bestSellerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //    xử lý baner
        // Ánh xạ ViewPager2
        viewPager = findViewById(R.id.viewPager);
        BannerAdapter adapter = new BannerAdapter(imageList);
        viewPager.setAdapter(adapter);

        // Thêm hiệu ứng lướt chậm
        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(0.7f + (1 - Math.abs(position))); // Mờ dần khi lướt
            page.setTranslationX(-position * 100); // Dịch chuyển chậm hơn
            page.setScaleY(0.85f + (1 - Math.abs(position)) * 0.15f); // Thu nhỏ lại khi lướt qua
        });

        // Thiết lập auto-scroll vô tận
        setupAutoScroll();

        // DatabaseHelper (nếu cần)
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("Database Path", "Database stored at: " + db.getPath());


        //  xử lý sản phâẩm bán chạy cuộn ngang
        // Tạo danh sách sản phẩm bán chạy
        bestSellerList = new ArrayList<>();
        bestSellerList.add(new Product(R.drawable.product1, "Trà sữa", "50.000"));
        bestSellerList.add(new Product(R.drawable.product2, "Cà phê sữa", "45.000"));
        bestSellerList.add(new Product(R.drawable.product3, "Đá xay", "60.000"));
        bestSellerList.add(new Product(R.drawable.product4, "Matcha", "55.000"));

        // Ánh xạ RecyclerView
        recyclerBestSeller = findViewById(R.id.recyclerBestSeller);
        recyclerBestSeller.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Gán Adapter
        bestSellerAdapter = new BestSellerAdapter(bestSellerList);
        recyclerBestSeller.setAdapter(bestSellerAdapter);
    }

    //Phương thức tự động cuộn banner vô tận
    private void setupAutoScroll() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = viewPager.getCurrentItem() + 1;
                if (nextItem >= imageList.size()) {
                    viewPager.setCurrentItem(0, true); // Quay lại ảnh đầu tiên (vô tận)
                } else {
                    viewPager.setCurrentItem(nextItem, true);
                }
                handler.postDelayed(this, SLIDE_DELAY);
            }
        };
        handler.postDelayed(runnable, SLIDE_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

}
