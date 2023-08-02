package com.example.towncenterstore.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.FragmentProfileBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.listeners.OnClick;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.points.BaseResponseShowPoints;
import com.example.towncenterstore.pojo.points.Points;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.general.CornersTransformation;
import com.example.towncenterstore.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentProfileBinding binding;

    String param1,param2;

    OnClick onClick;
    String token;
    int orderCount;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String username = "";
    String email = "";
    String personPhoto = "";
    Context mContext;
    Dialog bottomSheetDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onClick = (OnClick) context;
        mContext = context;
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToken();
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM1);
            param2 = getArguments().getString(ARG_PARAM2);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        hiddenContent();
        init();
        return binding.getRoot();
    }


    private void getToken() {

        sp = getActivity().getSharedPreferences(Constants.NAME_SHARED, Context.MODE_PRIVATE);
        editor = sp.edit();
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
        }

        if (sp.getInt(Constants.ORDER_COUNT, -1) >= 0) {
            orderCount = sp.getInt(Constants.ORDER_COUNT, -1);
        }
        if (!sp.getString(Constants.PERSON_PHOTO, "").equals("")) {
            personPhoto = sp.getString(Constants.PERSON_PHOTO, "");
        }
        if (!sp.getString(Constants.EMAIL, "").equals("")&&!sp.getString(Constants.USERNAME, "").equals("")) {
            email = sp.getString(Constants.EMAIL, "");
            username = sp.getString(Constants.USERNAME, "");
        }



    }


    private void init() {
        bottomSheetDialog = new Dialog(mContext);
        showTotalPoints(token);
        setInfoUser();
        onClickListeners();
    }

    private void setInfoUser() {
        binding.tvPersonName.setText(username);
        binding.tvPersonEmail.setText(email);

        if (!personPhoto.equals("")) {
            Glide.with(mContext).load(personPhoto)
                    .apply(RequestOptions.bitmapTransform(new CornersTransformation(mContext, 40)))
                    .into(binding.imgPersonPhoto);
        } else {
            binding.imgPersonPhoto.setImageDrawable(mContext.getDrawable(R.drawable.profile_image_2));
        }
    }

    private void onClickListeners() {
        onClickEditProfile();
        onClickChangePassword();
        onClickRateUs();
        onClickCallUs();
        onClickNotification();
        onClickLogOut();
        onClickPersonPhoto();
    }

    private void hiddenContent() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.getRoot().setVisibility(View.INVISIBLE);
    }

    private void visibleContent() {
        binding.progress.setVisibility(View.INVISIBLE);
        binding.getRoot().setVisibility(View.VISIBLE);
    }


    private void onClickPersonPhoto() {
        binding.imgPersonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick("person_photo");
            }
        });
    }

    private void onClickEditProfile() {
        binding.imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick("edit_profile");
            }
        });
    }

    private void onClickChangePassword() {
        binding.tvChangePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
    }

    private void onClickRateUs() {
        binding.tvRateUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDialogRateUs();
//                onClick.onClick("rate_us");
                testShare();
            }
        });
    }

    private void onClickCallUs() {
        binding.tvCallUsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick("call_us");
            }
        });
    }

    private void onClickNotification() {
        binding.tvNotificationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClick("notification");
            }
        });
    }

    private void onClickLogOut() {
        binding.imgBackgroundLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogLogout();
            }
        });
    }

    private void showBottomSheetDialog() {


        bottomSheetDialog.setContentView(R.layout.bottom_sheet_change_password);


        EditText oldPassword = bottomSheetDialog.findViewById(R.id.etOldPassword);
        EditText newPassword = bottomSheetDialog.findViewById(R.id.etNewPassword);
        EditText reNewPassword = bottomSheetDialog.findViewById(R.id.etRePassword);
        Button editBtn = bottomSheetDialog.findViewById(R.id.btnSave);


        if (editBtn != null) {
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (oldPassword != null && newPassword != null && reNewPassword != null) {
                        if (!validatePassword(oldPassword) || !validatePassword(newPassword) || !validateConfirmPassword(reNewPassword, newPassword)) {
                            Toast.makeText(mContext, mContext.getString(R.string.entry_error), Toast.LENGTH_SHORT).show();
                        } else {
                            changePassword(oldPassword.getText().toString(), newPassword.getText().toString(), reNewPassword.getText().toString(), token);
                        }
                    }
                }
            });
        }

        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    private void changePassword(String oldPassword, String newPassword, String newPasswordConfirmation, String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().changePassword(oldPassword, newPassword, newPasswordConfirmation, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bottomSheetDialog.dismiss();
                    Toast.makeText(mContext, response.body().getMessage() + "", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validatePassword(EditText etPassword) {
        String password = etPassword.getText().toString().trim();
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        if (password.isEmpty()) {
            etPassword.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;
        }else if (!password.matches(PASSWORD_PATTERN)) {
            etPassword.setError(getResources().getString(R.string.it_must_be_greater_than_8_characters_and_contain_at_least_one_uppercase_letter_one_lowercase_letter_one_number_and_one_symbol));
            return false;
        } else if (password.length() < 8) {
            etPassword.setError(getResources().getString(R.string.password_is_too_short));
            return false;
        } else {
            etPassword.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(EditText etConfirmPassword, EditText etPassword) {
        String rePassword = etConfirmPassword.getText().toString().trim();


        if (rePassword.isEmpty()) {
            etConfirmPassword.setError(mContext.getString(R.string.do_not_leave_the_field_blank));
            return false;
        } else if (etConfirmPassword.length() < 8) {
            etPassword.setError(mContext.getString(R.string.password_is_too_short));
            return false;
        } else if (!etConfirmPassword.getText().toString().trim().
                equals(etPassword.getText().toString().trim())) {
            etConfirmPassword.setError(mContext.getString(R.string.passwords_doesnt_matches));
            return false;
        } else {
            return true;
        }

    }

    private void logoutUser(String token) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().logoutUser(token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    saveDataWhenLogout();
                    onClick.onClick("log_out_profile");
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
                    Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDataWhenLogout() {
        editor.putBoolean(Constants.STATUS_REMEMBER, false);
        editor.putString(Constants.EMAIL, "");
        editor.putString(Constants.PASSWORD, "");
        editor.apply();
    }

    private void showTotalPoints(String token) {
        Call<BaseResponseShowPoints<Products<Points>>> call = RetrofitClient.getInstance().getRetrofitRequests().showTotalPoints(token);
        call.enqueue(new Callback<BaseResponseShowPoints<Products<Points>>>() {
            @Override
            public void onResponse(Call<BaseResponseShowPoints<Products<Points>>> call, Response<BaseResponseShowPoints<Products<Points>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    binding.pointsValueTv.setText(String.valueOf(response.body().getMyPoints()));
                    binding.ordersValueTv.setText(String.valueOf(orderCount));
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

    private void showDialogLogout() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit_app);
        Button yes = dialog.findViewById(R.id.btnYes);
        Button no = dialog.findViewById(R.id.btnNo);
        TextView message = dialog.findViewById(R.id.tvMessage);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser(token);
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

    private void showDialogRateUs() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rateing_app);
        Button cancel = dialog.findViewById(R.id.cancelButton);


        cancel.setOnClickListener(new View.OnClickListener() {
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
    private void testShare() {

        final String appPackageName = String.valueOf(mContext); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }



}