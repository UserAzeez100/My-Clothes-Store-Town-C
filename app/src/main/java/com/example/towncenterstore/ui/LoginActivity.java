package com.example.towncenterstore.ui;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.towncenterstore.R;
import com.example.towncenterstore.databinding.ActivityLoginBinding;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponseLogin;
import com.example.towncenterstore.pojo.authentication_profile.User;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public static Activity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginActivity = this;

        init();

    }

    private void init() {
        sp = getSharedPreferences(Constants.NAME_SHARED, MODE_PRIVATE);
        editor = sp.edit();
        checkDataLogin(sp.getString(Constants.EMAIL, ""), sp.getString(Constants.PASSWORD, ""));
        onClickListeners();

    }

    private void onClickListeners() {
        onClickBtnLogin();
        onClickBtnForget();
        onClickBtnRegister();
    }

    private void visibleBtnRegister() {
        binding.btnConfirmLogin.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void hiddenBtnRegister() {
        binding.btnConfirmLogin.setVisibility(View.INVISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void onClickBtnLogin() {
        binding.btnConfirmLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();
            }
        });
    }

    private void checkData() {
        if (!validEmail(binding.etEmail) || !validatePassword(binding.etPassword)) {
            Toast.makeText(loginActivity, getResources().getString(R.string.fill_in_the_blank_fields), Toast.LENGTH_SHORT).show();
        } else {
            hiddenBtnRegister();
            loginUser(binding.etEmail.getText().toString(), binding.etPassword.getText().toString());
        }
    }

    private void loginUser(String email, String password) {
        Call<BaseResponseLogin<User>> call = RetrofitClient.getInstance().getRetrofitRequests().loginUser(email, password);
        call.enqueue(new Callback<BaseResponseLogin<User>>() {
            @Override
            public void onResponse(Call<BaseResponseLogin<User>> call, Response<BaseResponseLogin<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus() && response.body().getData() != null) {
                        saveData(response.body().getData().getToken(),response.body().getData().getEmail(),response.body().getData().getName(),response.body().getData().getImage());
                        visibleBtnRegister();
                        moveNextScreen(LoginActivity.this, HomeActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                    } else {
                        visibleBtnRegister();
                        Snackbar.make(binding.getRoot(), response.body().getMessage(), LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Snackbar.make(binding.getRoot(), jsonObject.getString("message")+"", LENGTH_SHORT).show();
                        visibleBtnRegister();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseLogin<User>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Snackbar.make(binding.getRoot(), t.getMessage()+"", LENGTH_SHORT).show();
                    visibleBtnRegister();
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

    private boolean validEmail(EditText etEmail) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;

        } else if (!email.matches(emailPattern)) {
            etEmail.setError(getResources().getString(R.string.email_not_valid));
            return false;

        } else {
            etEmail.setError(null);
            return true;

        }
    }

    private void onClickBtnRegister() {
        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNextScreen(LoginActivity.this, RegisterActivity.class, R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
    }

    private void onClickBtnForget() {
        binding.tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, VerifyAccountActivity.class));

            }
        });
    }

    private void checkDataLogin(String email, String password) {
        if (!email.equals("") && !password.equals("")) {
            binding.etEmail.setText(email);
            binding.etPassword.setText(password);
        } else {
            binding.etEmail.setText("");
            binding.etPassword.setText("");
        }
    }

    private void removeData() {
        editor.putString(Constants.EMAIL, "");
        editor.putString(Constants.PASSWORD, "");
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeData();
    }


    private void saveData(String tokenValue,String email,String username,String image) {
        editor.putString(Constants.TOKEN_LOGIN_USER, "Bearer " + tokenValue);
        editor.putString(Constants.EMAIL, email);
        editor.putString(Constants.USERNAME,username);
        editor.putString(Constants.PERSON_PHOTO, image);
        editor.putBoolean(Constants.STATUS_REMEMBER, true);
        editor.apply();
    }

    private void moveNextScreen(Context fromActivity, Class<?> toActivity, int firstAnim, int lastAnim) {
        startActivity(new Intent(fromActivity, toActivity));
        overridePendingTransition(firstAnim, lastAnim);
        finish();
    }
}