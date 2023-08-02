package com.example.towncenterstore.ui;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.towncenterstore.R;
import com.example.towncenterstore.adapters.CitiesAdapter;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.databinding.ActivityEditProfileBinding;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponseLogin;
import com.example.towncenterstore.pojo.authentication_profile.User;
import com.example.towncenterstore.retrofit.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String token;
    Uri profileImgUri;
    Bitmap bitmap;
    int countryId;
    ArrayList<String> citiesArrayList;
    CitiesAdapter citiesAdapter;
    public static Activity editProfileActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();

    }

    @Override
    protected void onStart() {
        super.onStart();

        getImage();
    }

    private void getImage() {
        ActivityResultLauncher<String> img = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    profileImgUri = result;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(EditProfileActivity.this.getContentResolver(), result);
                        Glide.with(EditProfileActivity.this).load(bitmap).circleCrop().into(binding.imgPersonPhoto);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        binding.imgPersonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img.launch("image/*");

            }
        });
    }

    private void init() {
        editProfileActivity = this;
        initList();
        sp = getSharedPreferences(Constants.NAME_SHARED, MODE_PRIVATE);
        editor = sp.edit();
        getCountryId();
        onClickListeners();
        if (getToken()) {
            showProfile(token);
        }
    }

    private void initList(){

        citiesArrayList = new ArrayList<>();
        citiesArrayList.add(getResources().getString(R.string.bayt_hanun));
        citiesArrayList.add(getResources().getString(R.string.mueaskar_jabalia));
        citiesArrayList.add(getResources().getString(R.string.tala_alhwa));
        citiesArrayList.add(getResources().getString(R.string.alzahra));
        citiesArrayList.add(getResources().getString(R.string.alzawayida));
        citiesArrayList.add(getResources().getString(R.string.almaghazi));
        citiesArrayList.add(getResources().getString(R.string.alshujaeia));
        citiesArrayList.add(getResources().getString(R.string.alzaytun));
        citiesArrayList.add(getResources().getString(R.string.dir_albalah));
        citiesArrayList.add(getResources().getString(R.string.alnusayrat));
        citiesArrayList.add(getResources().getString(R.string.khanywns));
        citiesArrayList.add(getResources().getString(R.string.rafah));

        citiesAdapter = new CitiesAdapter(EditProfileActivity.this,citiesArrayList);
        binding.editCity.setAdapter(citiesAdapter);
    }

    private void getCountryId() {
        if (sp.getInt(Constants.COUNTRY_ID, -1) > -1) {
            countryId = sp.getInt(Constants.COUNTRY_ID, -1);
        }
    }


    private void onClickListeners() {
        onClickBtnSave();
        onClickBtnBack();
        clickSpinner();
    }


    private void showProfile(String token) {
        Call<BaseResponseLogin<User>> call = RetrofitClient.getInstance().getRetrofitRequests().showProfile(token);
        call.enqueue(new Callback<BaseResponseLogin<User>>() {
            @Override
            public void onResponse(Call<BaseResponseLogin<User>> call, Response<BaseResponseLogin<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setData(response.body().getUser().getImage(), response.body().getUser().getName(), response.body().getUser().getEmail(), response.body().getUser().getPhone());
                } else {
                    Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponseLogin<User>> call, Throwable t) {
                if (t.getMessage() != null) {
                    Snackbar.make(binding.getRoot(), t.getMessage() + "", LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setData(String image, String username, String email, String phone) {
        binding.etUsername.setText(username);
        binding.etEmail.setText(email);
        binding.etPhoneNumber.setText(phone);
        binding.editCity.setSelection(countryId);
        if (!image.equals("")) {
            Glide.with(EditProfileActivity.this).load(image).circleCrop().into(binding.imgPersonPhoto);
        }else {
            binding.imgPersonPhoto.setImageDrawable(getResources().getDrawable(R.drawable.avtar));
        }
    }

    private void updateProfile(String username, String email, String phone, String country, String token) {

        RequestBody requestFile;
        MultipartBody.Part body = null;
        if (profileImgUri != null) {
            requestFile = RequestBody.create(MediaType.parse("image/*"), bitmapToBytes());
            body = MultipartBody.Part.createFormData("image", "image_file.jpg", requestFile);
        }
        RequestBody profileUsername = RequestBody.create(MediaType.parse("multipart/form-data"), username);
        RequestBody profileEmail = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody profilePhone = RequestBody.create(MediaType.parse("multipart/form-data"), phone);
        RequestBody profileCountry = RequestBody.create(MediaType.parse("multipart/form-data"), country);


        Call<BaseResponse> call = RetrofitClient.getInstance().getRetrofitRequests().updateProfile(profileUsername, profileEmail, profilePhone, profileCountry, body, token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getMessage() != null) {
                        Snackbar.make(binding.getRoot(), response.body().getMessage() + "", LENGTH_SHORT).show();
                    finish();
                    }
                } else {
                    try {
                        String error = new String(response.errorBody().bytes(), StandardCharsets.UTF_8);
                        JSONObject jsonObject = new JSONObject(error);
                        Snackbar.make(binding.getRoot(), jsonObject.getString("message") + "", LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Snackbar.make(binding.getRoot(), t.getMessage() + "", LENGTH_SHORT).show();
                }
            }
        });

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

    private boolean validUsername(EditText etUsername) {

        String username = etUsername.getText().toString().trim();
        String noSpace = "(?=\\s+$)";

        if (username.isEmpty()) {
            etUsername.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;
        } else if (username.length() >= 20) {
            etUsername.setError(getResources().getString(R.string.username_is_too_long));
            return false;
        } else if (username.matches(noSpace)) {
            etUsername.setError(getResources().getString(R.string.empty_spaces_are_not_allowed));
            return false;

        } else {
            etUsername.setError(null);
            return true;

        }
    }

    private boolean validatePhone(EditText etPhone) {
        String phone = etPhone.getText().toString().trim();


        if (phone.isEmpty()) {
            etPhone.setError(getResources().getString(R.string.do_not_leave_the_field_blank));
            return false;
        } else if (phone.length() != 13) {
            etPhone.setError(getResources().getString(R.string.password_is_too_short));
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

    private void checkData() {
        if (!validUsername(binding.etUsername) || !validEmail(binding.etEmail) || !validatePhone(binding.etPhoneNumber)) {
            Snackbar.make(binding.getRoot(), getResources().getString(R.string.fill_in_the_blank_fields), Snackbar.LENGTH_LONG).show();
        } else {
            editor.putString(Constants.USERNAME, binding.etUsername.getText().toString());
            editor.putString(Constants.EMAIL, binding.etEmail.getText().toString());
            editor.apply();
            updateProfile(binding.etUsername.getText().toString(), binding.etEmail.getText().toString(), binding.etPhoneNumber.getText().toString(), binding.editCity.getSelectedItem().toString(), token);
        }
    }

    private void onClickBtnSave() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnSave.setVisibility(View.INVISIBLE);
                binding.progress.setVisibility(View.VISIBLE);
                checkData();
            }
        });
    }


    private byte[] bitmapToBytes() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }

    private boolean getToken() {
        if (!sp.getString(Constants.TOKEN_LOGIN_USER, "").equals("")) {
            token = sp.getString(Constants.TOKEN_LOGIN_USER, "");
            return true;
        } else {
            Toast.makeText(editProfileActivity, "Error Token", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void clickSpinner() {
        binding.editCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt(Constants.COUNTRY_ID, i);
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void onClickBtnBack() {
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void moveNextScreen(Context fromActivity, Class<?> toActivity, int firstAnim, int lastAnim) {
        startActivity(new Intent(fromActivity, toActivity));
        overridePendingTransition(firstAnim, lastAnim);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}