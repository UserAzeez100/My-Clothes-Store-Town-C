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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.ProductsAdapterOrders;
import com.example.towncenterstore.databinding.FragmentOrdersBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.pojo.orders.BaseResponseOrders;
import com.example.towncenterstore.pojo.orders.Order;
import com.example.towncenterstore.pojo.orders.filter_orders.BaseResponseFilterOrders;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.retrofit.RetrofitClient;
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

public class OrdersFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    OnClick onClick;
    FragmentOrdersBinding binding;
    String token;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String filter;
    Context mContext;

    List<Order> orderArrayList = new ArrayList<>();
    ProductsAdapterOrders productsAdapterOrders;

    private String mParam1;
    private String mParam2;
    int page = 1;
    boolean visible = true;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onClick = (OnClick) context;
        mContext = context;
    }

    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            getToken();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        init();

        return binding.getRoot();
    }


    private void init() {
        hiddenContent();
        showOrders(token, page);
        onClickFilter();
        onChangeScroll();
        onClickBtnAllOrders();
        onClickBtnPending();
        onClickBtnComplete();
        onClickBtnOnWay();

    }


    private void onChangeScroll() {
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    binding.progressLoadMore.setVisibility(View.VISIBLE);
                    showOrders(token, page);
                }

            }
        });
    }


    private void visibleContent() {
        binding.rvOrders.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvOrders.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void getToken() {
        sp = mContext.getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");

        }
    }


    private void showOrders(String token, int page) {
        Call<BaseResponseOrders<Products<Order>>> call = RetrofitClient.getInstance().getRetrofitRequests().showOrders(page, token);
        call.enqueue(new Callback<BaseResponseOrders<Products<Order>>>() {
            @Override
            public void onResponse(Call<BaseResponseOrders<Products<Order>>> call, Response<BaseResponseOrders<Products<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (page == 1 && response.body().getOrders().getData().isEmpty()) {
                        binding.noOrdersLinearLayout.setVisibility(View.VISIBLE);
                    }
                    if (response.body().getOrders() != null) {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        setData(response);
                        visibleContent();

                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                        binding.noOrdersLinearLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<BaseResponseOrders<Products<Order>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });

    }

    private void setData(Response<BaseResponseOrders<Products<Order>>> response) {

        if (response.body().getOrders().getCurrentPage() == 1) {
            productsAdapterOrders = new ProductsAdapterOrders(mContext, new OnClickItemCategoryOrProduct() {
                @Override
                public void onClick(int position) {
                    editor.putInt(Constants.ORDER_ID, orderArrayList.get(position).getId());
                    editor.apply();
                    onClick.onClick("receipt");

                }
            });
            orderArrayList = response.body().getOrders().getData();
        } else {

            orderArrayList.addAll(response.body().getOrders().getData());
        }
        productsAdapterOrders.setOrderList(orderArrayList);
        binding.rvOrders.setAdapter(productsAdapterOrders);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(mContext));


    }


    private void setDataFilterOrders(Response<BaseResponseFilterOrders<Order>> response) {


        if (response.body().getCurrentPage() == 1) {
            productsAdapterOrders = new ProductsAdapterOrders(mContext, new OnClickItemCategoryOrProduct() {
                @Override
                public void onClick(int position) {
                    editor.putInt(Constants.ORDER_ID, orderArrayList.get(position).getId());
                    editor.apply();
                    onClick.onClick("receipt");
                }
            });
            orderArrayList = response.body().getData();
        } else {

            orderArrayList.addAll(response.body().getData());
        }
        productsAdapterOrders.setOrderList(orderArrayList);
        binding.rvOrders.setAdapter(productsAdapterOrders);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(mContext));
    }


    public void onClickFilter() {


        binding.imgSearchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible) {
                    binding.filterLinearLayout.setVisibility(View.VISIBLE);
                    visible = false;
                } else {
                    binding.filterLinearLayout.setVisibility(View.GONE);
                    visible = true;
                }
            }
        });

    }

    private void onClickBtnAllOrders() {
        binding.btnAllOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.noOrdersLinearLayout.setVisibility(View.INVISIBLE);
                returnBaseSettings();
                visible = true;
                binding.filterLinearLayout.setVisibility(View.GONE);
                showOrders(token, page);

                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            showOrders(token, page);
                        }

                    }
                });


            }
        });

    }

    private void onClickBtnPending() {

        binding.btnPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.noOrdersLinearLayout.setVisibility(View.INVISIBLE);
                returnBaseSettings();
                visible = true;
                binding.filterLinearLayout.setVisibility(View.GONE);
                filterOrders("pending", token, page);

                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            filterOrders("pending", token, page);
                        }

                    }
                });


            }
        });

    }


    private void onClickBtnOnWay() {
        binding.btnOnWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.noOrdersLinearLayout.setVisibility(View.INVISIBLE);
                returnBaseSettings();
                visible = true;
                binding.filterLinearLayout.setVisibility(View.GONE);
                filterOrders("on_way", token, page);

                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            Log.e("filter", filter + "");
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            filterOrders("on_way", token, page);
                        }

                    }
                });

            }
        });

    }

    private void onClickBtnComplete() {
        binding.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.noOrdersLinearLayout.setVisibility(View.INVISIBLE);
                returnBaseSettings();
                visible = true;
                binding.filterLinearLayout.setVisibility(View.GONE);
                filterOrders("complete", token, page);


                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            filterOrders("complete", token, page);
                        }

                    }
                });

            }
        });

    }


    private void filterOrders(String typeOrder, String token, int page) {
        Call<BaseResponseFilterOrders<Order>> call = RetrofitClient.getInstance().getRetrofitRequests().filterOrders(page, typeOrder, token);
        call.enqueue(new Callback<BaseResponseFilterOrders<Order>>() {
            @Override
            public void onResponse(Call<BaseResponseFilterOrders<Order>> call, Response<BaseResponseFilterOrders<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (page == 1 && response.body().getData().isEmpty()) {
                        binding.noOrdersLinearLayout.setVisibility(View.VISIBLE);
                    }
                    if (response.body().getData() != null) {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        setDataFilterOrders(response);
                        visibleContent();
                    } else {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();


                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Snackbar.make(binding.getRoot(), jsonObject.getString("message"), Snackbar.LENGTH_LONG).show();
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        visibleContent();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseFilterOrders<Order>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Snackbar.make(binding.getRoot(), t.getMessage(), Snackbar.LENGTH_LONG).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }
            }
        });
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_order_filter);
        TextView waitOrder = dialog.findViewById(R.id.tvPendingOrders);
        TextView wayOrder = dialog.findViewById(R.id.tvOnTheWayOrders);
        TextView completeOrder = dialog.findViewById(R.id.tvCompleteOrders);
        TextView allOrders = dialog.findViewById(R.id.tvAllOrders);
        ImageView close = dialog.findViewById(R.id.imgFilterCloseIcon);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        allOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnBaseSettings();
                dialog.dismiss();
                showOrders(token, page);

                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            showOrders(token, page);
                        }

                    }
                });


            }
        });
        waitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnBaseSettings();
                dialog.dismiss();
                filterOrders("pending", token, page);

                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            filterOrders("pending", token, page);
                        }

                    }
                });


            }
        });

        wayOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnBaseSettings();
                dialog.dismiss();
                filterOrders("on_way", token, page);

                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            Log.e("filter", filter + "");
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            filterOrders("on_way", token, page);
                        }

                    }
                });

            }
        });

        completeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnBaseSettings();
                dialog.dismiss();
                filterOrders("complete", token, page);
                binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                            page++;
                            binding.progressLoadMore.setVisibility(View.VISIBLE);
                            filterOrders("complete", token, page);
                        }

                    }
                });

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void returnBaseSettings() {
        orderArrayList.clear();
        page = 1;
        hiddenContent();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("onDetach", "onDetach");
    }

}