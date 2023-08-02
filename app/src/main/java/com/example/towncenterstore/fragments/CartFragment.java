package com.example.towncenterstore.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.CartAdapter;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.databinding.FragmentCartBinding;
import com.example.towncenterstore.listeners.ChangeTheQuantityOrDelete;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.orders.Order;
import com.example.towncenterstore.pojo.points.BaseResponseShowPoints;
import com.example.towncenterstore.pojo.points.Points;
import com.example.towncenterstore.pojo.product.cart_product.BaseResponseCart;
import com.example.towncenterstore.pojo.product.cart_product.ProductCart;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.use_point.BaseResponseUsePoint;
import com.example.towncenterstore.pojo.use_point.OrderProduct;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.example.towncenterstore.ui.HomeActivity;
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


public class CartFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    FragmentCartBinding binding;
    List<ProductCart> productCartArrayList = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token;
    CartAdapter cartAdapter;
    OnClick onClick;
    double totalAmount;
    int totalItem;
    private String detailsTitle ;
    private String mapLat;
    private String mapLong;
    int page = 1;
    Context mContext;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onClick = (OnClick) context;
        mContext = context;
    }

    public static CartFragment newInstance(String detailsTitle, String mapLat, String mapLong) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, detailsTitle);
        args.putString(ARG_PARAM2, mapLat);
        args.putString(ARG_PARAM3, mapLong);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailsTitle = getArguments().getString(ARG_PARAM1);
            mapLat = getArguments().getString(ARG_PARAM2);
            mapLong = getArguments().getString(ARG_PARAM3);
            getToken();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        if (!detailsTitle.isEmpty()) {
            binding.locationTv.setText(detailsTitle);
        }else {
            binding.locationTv.setHint(getResources().getString(R.string.locate_location));
        }
        init();

        return binding.getRoot();
    }

    private void init() {
        hiddenContent();
        cartAdapter = new CartAdapter(mContext, new ChangeTheQuantityOrDelete() {
            @Override
            public void change(String process, int position, int id) {
                hiddenContent();
                switch (process) {
//                        زيادة كمية المنتج بمقدار 1
                    case Constants.INCREASE_QUANTITY:
                        increaseTheQuantity(id, token, position);
                        break;
//                            انقاص كمية المنتج بمقدار 1
                    case Constants.LOWER_QUANTITY:
                        lowerTheQuantity(id, token, position);
                        break;
//                            حذف العنصر من السلة
                    case Constants.DELETE_ITEM:
                        page = 1;
                        deleteItem(id, token);
                        break;
                }
            }
        });
        showCartProducts(page, token);
        showTotalPoints(token);
        onClickListeners();

    }

    private void onChangeScroll() {
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    binding.progressLoadMore.setVisibility(View.VISIBLE);
                    showCartProducts(page, token);
                }

            }
        });
    }

    private void visibleContent() {
        binding.rvCartProducts.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvCartProducts.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);

    }

    private void getToken() {
//        جلب التوكن الخاص بالمستخدم
        sp = mContext.getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
    }

    private void onClickListeners() {
//        مسح جميع العناصر في السلة
        onClickBtnClearAll();
//        الانتقال الى واجهة الخريطة لتحديد المكان
        onClickDetailsTitle();
//        اتمام عملية الشراء
        onClickBtnCheckOut();

        onChangeScroll();
    }

    private void onClickBtnClearAll() {
        binding.btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productCartArrayList.size() > 0) {
                    page = 1;
                    hiddenContent();
                    deleteCart(token);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.the_cart_is_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void onClickDetailsTitle() {
        binding.locationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productCartArrayList.size() == 0) {
                    Toast.makeText(mContext, mContext.getString(R.string.the_cart_is_empty), Toast.LENGTH_SHORT).show();
                } else {
                    onClick.onClick("location");
                }
            }
        });

    }

    private void onClickBtnCheckOut() {
        binding.btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.locationTv.getText().toString().isEmpty()) {
                    if (!productCartArrayList.isEmpty()) {
                        showDialogConfirmOrder();
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.the_cart_is_empty), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.enter_the_location), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //    عرض المنتجات في السلة
    private void showCartProducts(int page, String token) {
        binding.btnCheckOut.setEnabled(true);
        Call<BaseResponseCart<Products<ProductCart>>> call = RetrofitClient.getInstance().getRetrofitRequests().pageCart(page, token);
        call.enqueue(new Callback<BaseResponseCart<Products<ProductCart>>>() {
            @Override
            public void onResponse(Call<BaseResponseCart<Products<ProductCart>>> call, Response<BaseResponseCart<Products<ProductCart>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (page == 1 && response.body().getProducts()==null){
                        binding.noSalesLinearLayout.setVisibility(View.VISIBLE);
                    }
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
                            binding.pointCheckBox.setChecked(false);
                            binding.locationTv.setHint(mContext.getString(R.string.locate_location));
                            binding.tvTotalValue.setText("0₪");
                            binding.tvTotalItemsValue.setText("");
                            productCartArrayList.clear();
                            cartAdapter.setCartProductArrayList(productCartArrayList);
                            visibleContent();
                        }
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(requireContext(), jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseCart<Products<ProductCart>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });

    }

    private void setInfoTotal(int totalItem, double totalAmount) {
        binding.tvTotalValue.setText(String.valueOf(totalAmount+"₪"));
        binding.tvTotalItemsValue.setText(String.valueOf(totalItem));
    }

    private void setData(Response<BaseResponseCart<Products<ProductCart>>> response) {

        if (response.body().getProducts().getCurrentPage() == 1) {
            productCartArrayList.clear();
            productCartArrayList = response.body().getProducts().getData();
            totalAmount = response.body().getTotal();
            totalItem = response.body().getProducts().getTotal();
            setInfoTotal(totalItem, totalAmount);


        } else {
            totalAmount = totalAmount + response.body().getTotal();
            setInfoTotal(totalItem, totalAmount);
            productCartArrayList.addAll(response.body().getProducts().getData());
        }
        cartAdapter.setCartProductArrayList(productCartArrayList);
        binding.rvCartProducts.setAdapter(cartAdapter);
        binding.rvCartProducts.setLayoutManager(new LinearLayoutManager(mContext));


    }

    private void deleteItem(int id, String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().deleteItemInCart(id, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        showCartProducts(page, token);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.error_deleted), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);

                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(requireContext(), t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void lowerTheQuantity(int id, String token, int position) {
        Call<BaseResponseCart<ProductCart>> call = RetrofitClient.getInstance().getRetrofitRequests().lowerTheQuantity(id, token);
        call.enqueue(new Callback<BaseResponseCart<ProductCart>>() {
            @Override
            public void onResponse(Call<BaseResponseCart<ProductCart>> call, Response<BaseResponseCart<ProductCart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getMessage() != null && response.body().getObject() != null) {
                        Snackbar.make(binding.getRoot(), response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        productCartArrayList.get(position).setQuantity(response.body().getObject().getQuantity());
                        cartAdapter.setCartProductArrayList(productCartArrayList);

                        totalAmount -= Double.parseDouble(productCartArrayList.get(position).getPrice());

                        binding.tvTotalItemsValue.setText(String.valueOf(totalItem));
                        binding.tvTotalValue.setText(String.valueOf(totalAmount));
                        visibleContent();
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    }

                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseCart<ProductCart>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });
    }

    private void increaseTheQuantity(int id, String token, int position) {
        Call<BaseResponseCart<ProductCart>> call = RetrofitClient.getInstance().getRetrofitRequests().increaseTheQuantity(id, token);
        call.enqueue(new Callback<BaseResponseCart<ProductCart>>() {
            @Override
            public void onResponse(Call<BaseResponseCart<ProductCart>> call, Response<BaseResponseCart<ProductCart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getMessage() != null && response.body().getObject() != null) {
                        Snackbar.make(binding.getRoot(), response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        productCartArrayList.get(position).setQuantity(response.body().getObject().getQuantity());

                        cartAdapter.setCartProductArrayList(productCartArrayList);

                        totalAmount += Double.parseDouble(productCartArrayList.get(position).getPrice());

                        binding.tvTotalItemsValue.setText(String.valueOf(totalItem));
                        binding.tvTotalValue.setText(String.valueOf(totalAmount));
                        visibleContent();
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    }

                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseCart<ProductCart>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });

    }


    private void deleteCart(String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().deleteCart(token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                        showCartProducts(page, token);
                        Toast.makeText(mContext, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });

    }

    private void confirmOrder(String token) {
        Call<BaseResponse<Order>> call = RetrofitClient.getInstance().getRetrofitRequests().confirmOrder(token);
        call.enqueue(new Callback<BaseResponse<Order>>() {
            @Override
            public void onResponse(Call<BaseResponse<Order>> call, Response<BaseResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        editor.putInt(Constants.ORDER_ID, response.body().getOrder().getId());
                        editor.apply();
                        onClick.onClick("receipt");
                        binding.btnCheckOut.setEnabled(true);
                        requireActivity().finish();
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(requireContext(), jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        Log.e("oooo", jsonObject.getString("message") + "");
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        binding.btnCheckOut.setEnabled(true);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Order>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    binding.btnCheckOut.setEnabled(true);
                    visibleContent();
                }
            }
        });

    }

    private void showInvoice(int usePoint, String token) {
        Call<BaseResponseUsePoint<OrderProduct>> call = RetrofitClient.getInstance().getRetrofitRequests().showInvoice(usePoint, token);
        call.enqueue(new Callback<BaseResponseUsePoint<OrderProduct>>() {
            @Override
            public void onResponse(Call<BaseResponseUsePoint<OrderProduct>> call, Response<BaseResponseUsePoint<OrderProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getOrders() != null) {
                        confirmOrder(token);
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        page = 1;
                        showCartProducts(page, token);
                        Toast.makeText(mContext, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        binding.btnCheckOut.setEnabled(true);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseUsePoint<OrderProduct>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    binding.btnCheckOut.setEnabled(true);
                    visibleContent();
                }
            }
        });

    }

    private void addLocation(String location, String lat, String lng, String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().addLocation(location, lat, lng, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                        if (binding.pointCheckBox.isChecked()) {
                            showInvoice(1, token);
                        } else {
                            showInvoice(0, token);
                        }
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        binding.btnCheckOut.setEnabled(true);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    binding.btnCheckOut.setEnabled(true);
                    visibleContent();
                }
            }
        });
    }

    private void showTotalPoints(String token) {
        Call<BaseResponseShowPoints<Products<Points>>> call = RetrofitClient.getInstance().getRetrofitRequests().showTotalPoints(token);
        call.enqueue(new Callback<BaseResponseShowPoints<Products<Points>>>() {
            @Override
            public void onResponse(Call<BaseResponseShowPoints<Products<Points>>> call, Response<BaseResponseShowPoints<Products<Points>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    binding.yourPointsValue.setText(String.valueOf(response.body().getMyPoints()));
                    visibleContent();
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Toast.makeText(mContext, jsonObject.getString("message") + "", Toast.LENGTH_SHORT).show();
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseShowPoints<Products<Points>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    visibleContent();
                }
            }
        });
    }

    private void showDialogConfirmOrder() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_process);

        Button yes = dialog.findViewById(R.id.btnYes);
        Button no = dialog.findViewById(R.id.btnNo);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenContent();
                binding.btnCheckOut.setEnabled(false);

                addLocation(detailsTitle, mapLat, mapLong, token);
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

    }
}