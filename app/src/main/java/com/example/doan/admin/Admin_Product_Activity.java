package com.example.doan.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
//import com.example.yourapp.databinding.ActivityAdminProductBinding; // Generated binding class
import com.example.doan.R;
import com.example.doan.models.Product;

import java.util.List;

public class Admin_Product_Activity extends AppCompatActivity {

    //    private ActivityAdminProductBinding binding;
//    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_product); // Liên kết với layout XML
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityAdminProductBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Khởi tạo danh sách và adapter
//        productList = new ArrayList<>();
//        filteredList = new ArrayList<>();
//        productAdapter = new ProductAdapter(filteredList);
//        binding.rcvProduct.setLayoutManager(new LinearLayoutManager(this));
//        binding.rcvProduct.setAdapter(productAdapter);
//
//        // Tải dữ liệu mẫu
//        loadProducts();
//
//        // Thiết lập Spinner
//        setupSpinner();
//
//        // Xử lý sự kiện nút Thêm
//        binding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addProduct();
//            }
//        });
//
//        // Xử lý sự kiện TextView Xóa Tất Cả
//        binding.tvDeleteAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteAllProducts();
//            }
//        });
//    }
//
//    // Xử lý sự kiện khi nhấn icon tìm kiếm (được định nghĩa trong XML)
//    public void onSearchClicked(View view) {
//        String query = binding.edtSearch.getText().toString().trim();
//        searchProducts(query);
//    }
//
//    // Tải dữ liệu mẫu (thay bằng nguồn dữ liệu thực tế)
//    private void loadProducts() {
//        productList.clear();
//        productList.add(new Product("Áo thun", "Quần áo", 150000));
//        productList.add(new Product("Quần jeans", "Quần áo", 300000));
//        productList.add(new Product("Giày thể thao", "Giày dép", 500000));
//        filteredList.clear();
//        filteredList.addAll(productList);
//        productAdapter.notifyDataSetChanged();
//    }
//
//    // Thiết lập Spinner với danh mục sản phẩm
//    private void setupSpinner() {
//        String[] categories = {"Quần áo", "Giày dép", "Phụ kiện"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.spinnerSelectCat.setAdapter(adapter);
//    }
//
//    // Thêm sản phẩm mới
//    private void addProduct() {
//        String name = binding.edtNameProduct.getText().toString().trim();
//        String category = binding.spinnerSelectCat.getSelectedItem().toString();
//        String priceStr = binding.edtPriceProduct.getText().toString().trim();
//
//        // Kiểm tra dữ liệu đầu vào
//        if (name.isEmpty() || priceStr.isEmpty()) {
//            // Hiển thị thông báo lỗi nếu cần (ví dụ: Toast)
//            return;
//        }
//
//        int price = Integer.parseInt(priceStr);
//        Product newProduct = new Product(name, category, price);
//
//        // Thêm vào danh sách và cập nhật RecyclerView
//        productList.add(newProduct);
//        filteredList.clear();
//        filteredList.addAll(productList);
//        productAdapter.notifyDataSetChanged();
//
//        // Xóa các trường nhập sau khi thêm
//        binding.edtNameProduct.setText("");
//        binding.edtPriceProduct.setText("");
//    }
//
//    // Xóa tất cả sản phẩm
//    private void deleteAllProducts() {
//        productList.clear();
//        filteredList.clear();
//        productAdapter.notifyDataSetChanged();
//    }
//
//    // Tìm kiếm sản phẩm dựa trên query
//    private void searchProducts(String query) {
//        filteredList.clear();
//        if (query.isEmpty()) {
//            filteredList.addAll(productList);
//        } else {
//            for (Product product : productList) {
//                if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
//                        String.valueOf(product.getPrice()).contains(query) ||
//                        product.getCategory().toLowerCase().contains(query.toLowerCase())) {
//                    filteredList.add(product);
//                }
//            }
//        }
//        productAdapter.notifyDataSetChanged();
//    }
    }
}
