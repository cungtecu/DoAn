//package com.example.doan;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//import com.example.doan.models.Product;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//
//    private ViewPager2 viewPager;
//    private List<Integer> imageList = Arrays.asList(
//            R.drawable.banner1,
//            R.drawable.banner2,
//            R.drawable.banner3
//    );
//    private Handler handler = new Handler();
//    private Runnable runnable;
//    private static final long SLIDE_DELAY = 3000;
//
//    private BestSellerAdapter bestSellerAdapter;
//    private List<Product> bestSellerList;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        // Xử lý ViewPager2 (Banner)
//        viewPager = view.findViewById(R.id.viewPager);
//        BannerAdapter adapter = new BannerAdapter(imageList);
//        viewPager.setAdapter(adapter);
//
//        viewPager.setPageTransformer((page, position) -> {
//            page.setAlpha(0.7f + (1 - Math.abs(position)));
//            page.setTranslationX(-position * 100);
//            page.setScaleY(0.85f + (1 - Math.abs(position)) * 0.15f);
//        });
//
//        setupAutoScroll();
//
//        // Xử lý RecyclerView (Sản phẩm bán chạy)
//        bestSellerList = new ArrayList<>();
//        bestSellerList.add(new Product(1, "Trà sữa", "Trà sữa thơm ngon", 50000, R.drawable.product1 , 1, 0));
//        bestSellerList.add(new Product(2, "Cà phê sữa", "Cà phê sữa đậm đà", 45000, R.drawable.product2, 1, 0));
//        bestSellerList.add(new Product(3, "Đá xay", "Đá xay mát lạnh", 60000, R.drawable.product3, 1, 0));
//        bestSellerList.add(new Product(4, "Matcha", "Matcha nguyên chất", 55000, R.drawable.product4, 1, 0));
//
//        RecyclerView recyclerBestSeller = view.findViewById(R.id.recyclerBestSeller);
//        recyclerBestSeller.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        bestSellerAdapter = new BestSellerAdapter(bestSellerList);
//        recyclerBestSeller.setAdapter(bestSellerAdapter);
//
//        return view;
//    }
//
//    // xử lý cộn banner
//    private void setupAutoScroll() {
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                int nextItem = viewPager.getCurrentItem() + 1;
//                if (nextItem >= imageList.size()) {
//                    viewPager.setCurrentItem(0, true);
//                } else {
//                    viewPager.setCurrentItem(nextItem, true);
//                }
//                handler.postDelayed(this, SLIDE_DELAY);
//            }
//        };
//        handler.postDelayed(runnable, SLIDE_DELAY);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        handler.removeCallbacks(runnable);
//    }
//}
package com.example.doan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.doan.models.Product;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private List<Integer> imageList = Arrays.asList(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
    );
    private Handler handler = new Handler(Looper.getMainLooper()); // Đảm bảo chạy trên main thread
    private Runnable runnable;
    private static final long SLIDE_DELAY = 3000; // 3 giây

    private BestSellerAdapter bestSellerAdapter;
    private List<Product> bestSellerList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Xử lý ViewPager2 (Banner)
        viewPager = view.findViewById(R.id.viewPager);
        BannerAdapter adapter = new BannerAdapter(imageList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2); // Tải trước 2 trang bên cạnh để mượt hơn

        // Tinh chỉnh PageTransformer (đơn giản hơn để giảm lag)
        viewPager.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setAlpha(1 - absPos * 0.3f); // Giảm độ mờ để nhẹ hơn
            page.setScaleY(0.9f + (1 - absPos) * 0.1f); // Giảm scale để tối ưu
        });

        setupAutoScroll();

        // Xử lý RecyclerView (Sản phẩm bán chạy)
        bestSellerList = new ArrayList<>();
        bestSellerList.add(new Product(1, "Trà sữa", "Trà sữa thơm ngon", 50000, R.drawable.product1, 1, 0));
        bestSellerList.add(new Product(2, "Cà phê sữa", "Cà phê sữa đậm đà", 45000, R.drawable.product2, 1, 0));
        bestSellerList.add(new Product(3, "Đá xay", "Đá xay mát lạnh", 60000, R.drawable.product3, 1, 0));
        bestSellerList.add(new Product(4, "Matcha", "Matcha nguyên chất", 55000, R.drawable.product4, 1, 0));

        RecyclerView recyclerBestSeller = view.findViewById(R.id.recyclerBestSeller);
        recyclerBestSeller.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestSellerAdapter = new BestSellerAdapter(bestSellerList);
        recyclerBestSeller.setAdapter(bestSellerAdapter);

        return view;
    }

    // Xử lý cuộn banner
    private void setupAutoScroll() {
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % imageList.size(); // Tự động quay lại đầu khi hết
                viewPager.setCurrentItem(nextItem, true); // Smooth scroll
                handler.postDelayed(this, SLIDE_DELAY);
            }
        };
        handler.postDelayed(runnable, SLIDE_DELAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable); // Dọn dẹp để tránh memory leak
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Tạm dừng khi Fragment không hiển thị
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, SLIDE_DELAY); // Tiếp tục khi Fragment hiển thị lại
    }
}