package com.example.towncenterstore.ui;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.CitiesAdapter;
import com.example.towncenterstore.databinding.ActivityRegisterBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HEAD;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public static Activity registerActivity;
    int countryId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init() {
        registerActivity = this;
        sp = getSharedPreferences(Constants.NAME_SHARED, MODE_PRIVATE);
        editor = sp.edit();
        onClickListeners();
    }

//    private void initList(){
//
//        citiesArrayList = new ArrayList<>();
//        citiesArrayList.add(getResources().getString(R.string.bayt_hanun));
//        citiesArrayList.add(getResources().getString(R.string.mueaskar_jabalia));
//        citiesArrayList.add(getResources().getString(R.string.tala_alhwa));
//        citiesArrayList.add(getResources().getString(R.string.alzahra));
//        citiesArrayList.add(getResources().getString(R.string.alzawayida));
//        citiesArrayList.add(getResources().getString(R.string.almaghazi));
//        citiesArrayList.add(getResources().getString(R.string.alshujaeia));
//        citiesArrayList.add(getResources().getString(R.string.alzaytun));
//        citiesArrayList.add(getResources().getString(R.string.dir_albalah));
//        citiesArrayList.add(getResources().getString(R.string.alnusayrat));
//        citiesArrayList.add(getResources().getString(R.string.khanywns));
//        citiesArrayList.add(getResources().getString(R.string.rafah));
//
//        citiesAdapter = new CitiesAdapter(RegisterActivity.this,citiesArrayList);
//        binding.etSpinnerCities.setAdapter(citiesAdapter);
//    }


    private void onClickListeners() {
        onClickBtnRegister();
        clickSpinner();
        onClickBackIcon();
        onClickTvLogin();
    }

    private void checkData() {
        if (!validUsername(binding.etUsername) || !validEmail(binding.etEmail) || !validatePhone(binding.etPhoneNumber) || !validatePassword(binding.etPassword) || !validateConfirmPassword(binding.etRePassword, binding.etPassword) || !validTitle(binding.etTitleDetails)) {
            Snackbar.make(binding.getRoot(), getResources().getString(R.string.fill_in_the_blank_fields), LENGTH_SHORT).show();
        } else {
            hiddenBtnRegister();
            registerUser(binding.etUsername.getText().toString(), binding.etEmail.getText().toString(), binding.etPassword.getText().toString(), binding.etRePassword.getText().toString(), binding.etPhoneNumber.getText().toString(), binding.etSpinnerCities.getSelectedItem().toString());
        }
    }

    private void onClickBtnRegister() {

        binding.btnConfirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();
            }
        });
    }

    private void visibleBtnRegister() {
        binding.btnConfirmRegister.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenBtnRegister() {
        binding.btnConfirmRegister.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void registerUser(String username, String email, String password, String confirmPassword, String phone, String country) {
        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().registerUser(username, email, password, confirmPassword, phone, country);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                        saveData(binding.etEmail.getText().toString(),binding.etPassword.getText().toString(),countryId);
                        visibleBtnRegister();
                        moveNextScreen(RegisterActivity.this, LoginActivity.class, R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                    } else {
                        Snackbar.make(binding.getRoot(), response.body().getMessage(), LENGTH_SHORT).show();
                        visibleBtnRegister();
                    }

                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Snackbar.make(binding.getRoot(), jsonObject.getString("message") + "", LENGTH_SHORT).show();
                        visibleBtnRegister();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    visibleBtnRegister();
                    Snackbar.make(binding.getRoot(), t.getMessage() + "", LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean validTitle(EditText etTitle) {

        String email = etTitle.getText().toString();

        if (email.isEmpty()) {
            etTitle.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;
        } else {
            etTitle.setError(null);
            return true;

        }
    }

    private boolean validEmail(EditText etEmail) {
       final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;

        } else if (!email.matches(EMAIL_PATTERN)) {
            etEmail.setError(getResources().getString(R.string.email_not_valid));
            return false;

        } else {
            etEmail.setError(null);
            return true;

        }
    }

    private boolean validUsername(EditText etUsername) {

        String username = etUsername.getText().toString().trim();
        final String NO_SPACE = "(?=\\s+$)";

        if (username.isEmpty()) {
            etUsername.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;
        } else if (username.length() >= 20) {
            etUsername.setError(getResources().getString(R.string.username_is_too_long));
            return false;
        } else if (username.matches(NO_SPACE)) {
            etUsername.setError(getResources().getString(R.string.empty_spaces_are_not_allowed));
            return false;

        } else {
            etUsername.setError(null);
            return true;

        }
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
            etConfirmPassword.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;
        } else if (etConfirmPassword.length() < 8) {
            etPassword.setError(getResources().getString(R.string.password_is_too_short));
            return false;
        } else if (!etConfirmPassword.getText().toString().trim().
                equals(etPassword.getText().toString().trim())) {
            etConfirmPassword.setError(getResources().getString(R.string.passwords_doesnt_matches));
            return false;
        } else {
            return true;
        }
    }

    private void clickSpinner() {
        binding.spinnerLayout.setHintEnabled(true);
        binding.etSpinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                countryId = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean validatePhone(EditText etPhone) {
        String phone = etPhone.getText().toString().trim();


        if (phone.isEmpty()) {
            etPhone.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;
        } else if (phone.length() != 13) {
            etPhone.setError(getResources().getString(R.string.the_phone_number_must_contain_13_digits));
            return false;
        } else if (!phone.substring(0, 4).equals("+970") && !phone.substring(0, 4).equals("+972")) {
            etPhone.setError(getResources().getString(R.string.it_must_start_with_970_or_972));
            return false;
        } else if (!phone.substring(4, 6).equals("59") && !phone.substring(4, 6).equals("56")) {
            etPhone.setError(getResources().getString(R.string.the_front_of_the_number_must_be_either_59_or_56));
            return false;
        } else {
            etPhone.setError(null);
            return true;
        }

    }


    private void saveData(String emailValue,String passwordValue, int countryValue) {
        editor.putString(Constants.EMAIL, emailValue);
        editor.putString(Constants.PASSWORD, passwordValue);
        editor.putInt(Constants.COUNTRY_ID, countryValue);;
        editor.apply();
    }

    private void onClickBackIcon() {
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNextScreen(RegisterActivity.this, LoginActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
    }

    private void onClickTvLogin() {
        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNextScreen(RegisterActivity.this, LoginActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
    }

    private void moveNextScreen(Context fromActivity, Class<?> toActivity, int firstAnim,
                                int lastAnim) {
        startActivity(new Intent(fromActivity, toActivity));
        overridePendingTransition(firstAnim, lastAnim);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveNextScreen(RegisterActivity.this, LoginActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }
}