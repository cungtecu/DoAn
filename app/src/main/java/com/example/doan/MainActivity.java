package com.example.doan;

import static com.example.doan.R.*;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

    // khai báo amination cho giỏ hàng
//    private ImageView cartIcon, productImage;
//    private Button addToCartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drips);

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

        // ánh xạ giỏ hàng
//        cartIcon = findViewById(R.id.cartIcon);
//        productImage = findViewById(id.imgProduct);
//        FrameLayout addToCartButton = findViewById(R.id.addToCartButton);
//        // xử lý amination cho giỏ hàng
//        addToCartButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Hiệu ứng giỏ hàng phóng to rồi nhỏ lại
//                YoYo.with(Techniques.Pulse)
//                        .duration(500) // Thời gian chạy (ms)
//                        .repeat(1) // Lặp lại 1 lần
//                        .playOn(cartIcon);
//            }
//        });
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
