package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.SalesInvoiceAdapter;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.databinding.ActivityReceiptBinding;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponseLogin;
import com.example.towncenterstore.pojo.authentication_profile.User;
import com.example.towncenterstore.pojo.orders.show_order.BaseResponseShowOrder;
import com.example.towncenterstore.pojo.orders.show_order.ShowOrder;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.general.CornersTransformation;
import com.example.towncenterstore.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptActivity extends AppCompatActivity {

    ActivityReceiptBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token = "";
    String personPhoto = "";
    List<ShowOrder> showOrderArrayList = new ArrayList<>();
    int orderId;

    public static Activity receiptActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        receiptActivity = this;
        getToken();
        showOrder(orderId, token);
        hiddenContent();
        onClickListeners();
    }

    private void visibleContent() {
        binding.getRoot().setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.getRoot().setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void onClickListeners() {
        onClickBtnDone();
    }

    private void getToken() {
//        جلب التوكن الخاص بالمستخدم
        sp = getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);

        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
            if (sp.getInt(Constants.ORDER_ID, -1) != -1) {
                orderId = sp.getInt(Constants.ORDER_ID, -1);
            }
            if (!sp.getString(Constants.PERSON_PHOTO, "").equals("")) {
                personPhoto = sp.getString(Constants.PERSON_PHOTO, "");
            }


        }

    }

    private void showOrder(int orderId, String token) {
        Call<BaseResponseShowOrder<Products<ShowOrder>>> call = RetrofitClient.getInstance().getRetrofitRequests().showOrder(orderId, token);
        call.enqueue(new Callback<BaseResponseShowOrder<Products<ShowOrder>>>() {
            @Override
            public void onResponse(Call<BaseResponseShowOrder<Products<ShowOrder>>> call, Response<BaseResponseShowOrder<Products<ShowOrder>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getOrders() != null) {
                        showProfile(token);
                        setDataOrder(response);
                        visibleContent();
                    } else {
                        visibleContent();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ReceiptActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseShowOrder<Products<ShowOrder>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ReceiptActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showProfile(String token) {
        Call<BaseResponseLogin<User>> call = RetrofitClient.getInstance().getRetrofitRequests().showProfile(token);
        call.enqueue(new Callback<BaseResponseLogin<User>>() {
            @Override
            public void onResponse(Call<BaseResponseLogin<User>> call, Response<BaseResponseLogin<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getUser() != null) {
                        setInfoPerson(response.body().getUser().getName(), response.body().getUser().getPhone());
                    } else {
                        Toast.makeText(ReceiptActivity.this, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(ReceiptActivity.this, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseLogin<User>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(ReceiptActivity.this, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setInfoPerson(String username, String phone) {
        binding.tvPersonName.setText(username);
        binding.tvPersonPhone.setText(phone);

    }

    private void setDataOrder(Response<BaseResponseShowOrder<Products<ShowOrder>>> response) {
        showOrderArrayList.clear();
        if (response.body().getOrders().getData() != null) {
            showOrderArrayList = response.body().getOrders().getData();

            SalesInvoiceAdapter salesInvoiceAdapter = new SalesInvoiceAdapter(showOrderArrayList, ReceiptActivity.this);
            binding.rvSales.setAdapter(salesInvoiceAdapter);
            binding.rvSales.setLayoutManager(new LinearLayoutManager(this));
            binding.tvTotalValue.setText(response.body().getFinalPrice() + "₪");
            binding.tvDiscountValue.setText(response.body().getDiscount() + "₪");
            binding.tvOrderId.setText("Order# " + orderId);
            binding.tvReceiptDate.setText(response.body().getDate());
            if (!personPhoto.equals("")){
                Glide.with(ReceiptActivity.this).load(personPhoto)
                .apply(RequestOptions.bitmapTransform(new CornersTransformation(getBaseContext(), 30)))
                        .into(binding.imgPersonImage);
            }else {
                binding.imgPersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person));
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeActivity.homeActivity.finish();
        startActivity(new Intent(ReceiptActivity.this,HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        finish();
    }

    private void onClickBtnDone() {
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.homeActivity.finish();
                startActivity(new Intent(ReceiptActivity.this,HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                finish();

            }
        });
    }
}