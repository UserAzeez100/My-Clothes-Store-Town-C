package com.example.towncenterstore.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.CategoryAdapter;
import com.example.towncenterstore.databinding.FragmentHomeBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.general.MyEvent;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.listeners.OnClickItemCategoryOrProduct;
import com.example.towncenterstore.pojo.slider.SliderData;
import com.example.towncenterstore.adapters.SlidesImagesWorksAdapter;
import com.example.towncenterstore.pojo.category.BaseResponseCategory;
import com.example.towncenterstore.pojo.category.Categories;
import com.example.towncenterstore.pojo.category.Category;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.example.towncenterstore.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smarteist.autoimageslider.SliderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FragmentHomeBinding binding;
    OnClick onClick;
    CategoryAdapter categoryAdapter;
    int page = 1;
    List<Category> categoryList = new ArrayList<>();
    String fcmToken;

    private String mParam1;
    private String mParam2;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String token, username;
    Context mContext;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onClick = (OnClick) context;
        mContext = context;
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        init();

//        Button crashButton = new Button(mContext);
//        crashButton.setText("Test Crash");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash"); // Force a crash
//            }
//        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    private void init() {
        binding.tvNumberNotification.setVisibility(View.INVISIBLE);
        binding.tvPersonName.setText(username);
        indecatour();
        setFCMToken();
        hiddenContent();
        onClickListeners();
    }

    private void onClickListeners() {
        onclickSearchBox();
        onChangeScroll();
        onClickIconNotification();
        onClickSalePage();
    }

    private void getToken() {
        sp = mContext.getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }
        if (!sp.getString(Constants.USERNAME, "").equals("")) {
            username = sp.getString(Constants.USERNAME, "");
        }
    }

    private void visibleContent() {
        binding.rvCategories.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenContent() {
        binding.rvCategories.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }


    private void showCategories(String token, int page, String fcmToken) {
        Call<BaseResponseCategory<Categories<Category>>> call = RetrofitClient.getInstance().getRetrofitRequests().showCategories(page, token, fcmToken);
        call.enqueue(new Callback<BaseResponseCategory<Categories<Category>>>() {
            @Override
            public void onResponse(Call<BaseResponseCategory<Categories<Category>>> call, Response<BaseResponseCategory<Categories<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getCategories() != null) {
                        binding.progressLoadMore.setVisibility(View.INVISIBLE);
                        setData(response);
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
            public void onFailure(Call<BaseResponseCategory<Categories<Category>>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                    binding.progressLoadMore.setVisibility(View.INVISIBLE);
                    visibleContent();
                }

            }
        });

    }

    private void setData(Response<BaseResponseCategory<Categories<Category>>> response) {

        if (response.body().getCategories().getCurrentPage() == 1) {
            categoryAdapter = new CategoryAdapter(mContext, new OnClickItemCategoryOrProduct() {
                @Override
                public void onClick(int position) {
                    saveInfoCategory(response.body().getCategories().getData().get(position).getName(), response.body().getCategories().getData().get(position).getId());
                    onClick.onClick("category");
                }
            }
            );
            categoryList = response.body().getCategories().getData();
        } else {
            categoryList.addAll(response.body().getCategories().getData());
        }
        categoryAdapter.setCategories(categoryList);
        binding.rvCategories.setAdapter(categoryAdapter);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(mContext, 2));

        if (response.body().getUser() != null) {
            setInfoUser(response.body().getNotifications());
            saveInfoUser(response.body().getOrdersCount(), response.body().getUser().getImage(), response.body().getUser().getName(), response.body().getUser().getEmail());
        }


    }


    private void onClickIconNotification() {
        binding.imgIconNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tvNumberNotification.setText(String.valueOf(0));
                binding.tvNumberNotification.setVisibility(View.INVISIBLE);
                onClick.onClick("icon_notification");
            }
        });
    }

    private void onClickSalePage() {
        binding.saleLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick("sale_page");
            }
        });
    }

    private void onclickSearchBox() {
        binding.imgSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick("search_box");
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
                    showCategories(token, page, fcmToken);

                }
            }
        });
    }

    private void saveInfoUser(int orderCount, String image, String username, String email) {
        editor.putInt(Constants.ORDER_COUNT, orderCount);
        editor.putString(Constants.PERSON_PHOTO, image);
        editor.putString(Constants.USERNAME, username);
        editor.putString(Constants.EMAIL, email);
        editor.apply();
    }

    private void setInfoUser(int notificationsCount) {
        if (notificationsCount > 0) {
            binding.tvNumberNotification.setText(String.valueOf(notificationsCount));
            binding.tvNumberNotification.setVisibility(View.VISIBLE);

        } else {
            binding.tvNumberNotification.setVisibility(View.INVISIBLE);
        }
    }

    private void saveInfoCategory(String categoryName, int categoryId) {
        editor.putInt(Constants.CATEGORY_ID, categoryId);
        editor.putString(Constants.CATEGORY_NAME, categoryName);
        editor.apply();
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void indecatour() {
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

        sliderDataArrayList.add(new SliderData(mContext.getDrawable(R.drawable.slider1i)));
        sliderDataArrayList.add(new SliderData(mContext.getDrawable(R.drawable.slider2)));
        sliderDataArrayList.add(new SliderData(mContext.getDrawable(R.drawable.slider3)));


        SlidesImagesWorksAdapter adapter = new SlidesImagesWorksAdapter(mContext, sliderDataArrayList);

        binding.sliderImages.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        binding.sliderImages.setSliderAdapter(adapter);

        binding.sliderImages.setScrollTimeInSec(5);

        binding.sliderImages.setAutoCycle(true);

        binding.sliderImages.startAutoCycle();
    }

    public void setFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
//                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());

                            return;
                        }
                        fcmToken = task.getResult();
//                        Log.e("tokenFCM", fcmToken);
                        showCategories(token, page, fcmToken);
                    }
                });
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(MyEvent event) {
        if (event.getType() == 0) {
            if (binding.tvNumberNotification.getVisibility() == View.VISIBLE) {
                int x = Integer.parseInt(binding.tvNumberNotification.getText().toString());
                int y = x + 1;
                binding.tvNumberNotification.setText(String.valueOf(y));

            } else {
                binding.tvNumberNotification.setText(String.valueOf(1));

                HomeActivity.homeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvNumberNotification.setVisibility(View.VISIBLE);

                    }
                });
            }
        }
    }
}