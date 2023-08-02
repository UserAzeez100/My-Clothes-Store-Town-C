package com.example.towncenterstore.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ActivitySearchBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.GoToProductDetails;
import com.example.towncenterstore.adapters.ProductsSearchAdapter;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.search.BaseResponseSearch;
import com.example.towncenterstore.pojo.search.ProductSearch;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token;
    int page = 1;
    List<ProductSearch> productSearchList = new ArrayList<>();
    ProductsSearchAdapter productsSearchAdapter;
    String searchWord;

    public static Activity searchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();

    }

    private void init() {
        searchActivity = this;
        productsSearchAdapter = new ProductsSearchAdapter(SearchActivity.this, new GoToProductDetails() {
            @Override
            public void setData(int position, int id) {
                saveInfoProduct(position, id);
                moveNextScreen(SearchActivity.this, ProductDetailActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);

            }
        });
        getToken();
        onClickListeners();
        visibleContent();
    }

    private void onClickListeners() {
        onClickSearchBox();
        onChangeScroll();
        onClickFilter();
        onClickBtnBack();
    }

    private void onClickBtnBack() {
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                finish();
            }
        });
    }

    private void onChangeScroll() {
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    binding.progressLoadMore.setVisibility(View.VISIBLE);
                    showSearchResults(page, searchWord, token);
                }

            }
        });
    }


    private void onClickSearchBox() {
        binding.etSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (!binding.etSearchBox.getText().toString().isEmpty() && !binding.etSearchBox.getText().toString().contains(" ")) {
                        page = 1;
                        searchWord = binding.etSearchBox.getText().toString();
                        hiddenContent();
                        binding.noResultSearchLinearLayout.setVisibility(View.INVISIBLE);
                        showSearchResults(page, searchWord, token);
                    } else {
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.enter_the_search_words), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

    }

    private void getToken() {
        sp = getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
    }

    private void visibleContent() {
        binding.rvProducts.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvProducts.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void showSearchResults(int page, String search, String token) {
        Call<BaseResponseSearch<Products<ProductSearch>>> call = RetrofitClient.getInstance().getRetrofitRequests().search(page, search, token);
        call.enqueue(new Callback<BaseResponseSearch<Products<ProductSearch>>>() {
            @Override
            public void onResponse(Call<BaseResponseSearch<Products<ProductSearch>>> call, Response<BaseResponseSearch<Products<ProductSearch>>> response) {

                if (response.isSuccessful() && response.body() != null ) {
                    if (page > 1) {
                        if (response.body().getProducts() != null) {
                            binding.progressLoadMore.setVisibility(View.INVISIBLE);
                            setData(response);
                            visibleContent();
                        } else {
                            binding.progressLoadMore.setVisibility(View.INVISIBLE);
                            visibleContent();
                        }
                    } else {
                        if (response.body().getProducts() != null) {
                            binding.progressLoadMore.setVisibility(View.INVISIBLE);
                            setData(response);
                            visibleContent();
                        } else {
                            binding.progressLoadMore.setVisibility(View.INVISIBLE);
                            productSearchList.clear();
                            productsSearchAdapter.setProducts(productSearchList);
                            visibleContent();
                            binding.noResultSearchLinearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(SearchActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseSearch<Products<ProductSearch>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(SearchActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });

    }

    private void setData(Response<BaseResponseSearch<Products<ProductSearch>>> response) {

        if (response.body().getProducts().getCurrentPage() == 1) {
            productSearchList.clear();
            productSearchList = response.body().getProducts().getData();

        } else {

            productSearchList.addAll(response.body().getProducts().getData());
        }
        productsSearchAdapter.setProducts(productSearchList);
        binding.rvProducts.setAdapter(productsSearchAdapter);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));


    }


    public void onClickFilter() {
        binding.imgSearchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
    }


    private void showBottomSheetDialog() {


        final Dialog bottomSheetDialog = new Dialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_filter);

        TextView arrangeBy = bottomSheetDialog.findViewById(R.id.tvArrangeBy);
        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.radioGroup);
        RadioButton mostRequested = bottomSheetDialog.findViewById(R.id.rbMostOrder);
        RadioButton leastRequested = bottomSheetDialog.findViewById(R.id.rbLeastRequested);
        RadioButton highestPrice = bottomSheetDialog.findViewById(R.id.rbHighestPrice);
        RadioButton lowestPrice = bottomSheetDialog.findViewById(R.id.rbLowestPrice);
        Button confirmFilterBtn = bottomSheetDialog.findViewById(R.id.btnConfirmFilter);

        confirmFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenContent();
                binding.etSearchBox.setText("");
                binding.etSearchBox.setHint(getResources().getString(R.string.o_search_categories_or_product));

                if (mostRequested.isChecked()) {
                    filterProducts(0, 0, 0, 1, token);
                    bottomSheetDialog.dismiss();
                } else if (leastRequested.isChecked()) {
                    filterProducts(0, 0, 1, 0, token);
                    bottomSheetDialog.dismiss();
                } else if (highestPrice.isChecked()) {
                    filterProducts(1, 0, 0, 0, token);
                    bottomSheetDialog.dismiss();
                } else if (lowestPrice.isChecked()) {
                    filterProducts(0, 1, 0, 0, token);
                    bottomSheetDialog.dismiss();

                }

            }
        });


        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void filterProducts(int max, int min, int leastRequested, int mostRequested, String token) {
        Call<List<ProductSearch>> call = RetrofitClient.getInstance().getRetrofitRequests().filterProducts(max, min, leastRequested, mostRequested, token);
        call.enqueue(new Callback<List<ProductSearch>>() {
            @Override
            public void onResponse(Call<List<ProductSearch>> call, Response<List<ProductSearch>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isEmpty()) {
                        productSearchList.clear();
                        productsSearchAdapter.setProducts(response.body());
                        binding.rvProducts.setAdapter(productsSearchAdapter);
                        binding.rvProducts.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                        visibleContent();
                    } else {
                        visibleContent();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(SearchActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductSearch>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(SearchActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    visibleContent();
                }
            }
        });

    }


    private void saveInfoProduct(int position, int id) {
        editor.putInt(Constants.PRODUCT_ID, id);
        editor.apply();
    }

    private void moveNextScreen(Context fromActivity, Class<?> toActivity, int firstAnim, int lastAnim) {
        startActivity(new Intent(fromActivity, toActivity));
        overridePendingTransition(firstAnim, lastAnim);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        finish();
    }
}
