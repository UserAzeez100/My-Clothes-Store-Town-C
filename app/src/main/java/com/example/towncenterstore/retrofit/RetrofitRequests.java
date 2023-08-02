package com.example.towncenterstore.retrofit;

import com.example.towncenterstore.pojo.authentication_profile.BaseResponse;
import com.example.towncenterstore.pojo.authentication_profile.BaseResponseLogin;
import com.example.towncenterstore.pojo.authentication_profile.User;
import com.example.towncenterstore.pojo.category.BaseResponseCategory;
import com.example.towncenterstore.pojo.category.Categories;
import com.example.towncenterstore.pojo.category.Category;
import com.example.towncenterstore.pojo.discounts.BaseResponseDiscounts;
import com.example.towncenterstore.pojo.discounts.DiscountProduct;
import com.example.towncenterstore.pojo.notification.BaseResponseNotification;
import com.example.towncenterstore.pojo.notification.Notification;
import com.example.towncenterstore.pojo.orders.BaseResponseOrders;
import com.example.towncenterstore.pojo.orders.Order;
import com.example.towncenterstore.pojo.orders.filter_orders.BaseResponseFilterOrders;
import com.example.towncenterstore.pojo.orders.show_order.BaseResponseShowOrder;
import com.example.towncenterstore.pojo.orders.show_order.ShowOrder;
import com.example.towncenterstore.pojo.points.BaseResponseShowPoints;
import com.example.towncenterstore.pojo.points.Points;
import com.example.towncenterstore.pojo.product.cart_product.BaseResponseCart;
import com.example.towncenterstore.pojo.product.Favourite_product.BaseResponseFavourite;
import com.example.towncenterstore.pojo.product.cart_product.ProductCart;
import com.example.towncenterstore.pojo.product.Favourite_product.ProductFavourite;
import com.example.towncenterstore.pojo.product.Products;
import com.example.towncenterstore.pojo.product.color_products.BaseResponseColorProduct;
import com.example.towncenterstore.pojo.product.color_products.ProductColors;
import com.example.towncenterstore.pojo.product.details_product.BaseResponseDetailsProduct;
import com.example.towncenterstore.pojo.product.style_product.BaseResponseStyle;
import com.example.towncenterstore.pojo.product.style_product.Style;
import com.example.towncenterstore.pojo.search.BaseResponseSearch;
import com.example.towncenterstore.pojo.search.ProductSearch;
import com.example.towncenterstore.pojo.use_point.BaseResponseUsePoint;
import com.example.towncenterstore.pojo.use_point.OrderProduct;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitRequests {

    ///////////////////////////////////
    //////////////GET/////////////////
    /////////////////////////////////


    ///////////////////////////////////
    //////////////POST////////////////
    /////////////////////////////////
    @FormUrlEncoded
    @POST("auth/home/auth")
    Call<BaseResponse> registerUser(@Field("name") String name,
                                    @Field("email") String email,
                                    @Field("password") String password,
                                    @Field("password_confirmation") String confirmPassword,
                                    @Field("phone") String phone,
                                    @Field("country") String country);


    @FormUrlEncoded
    @POST("auth/access-tokens")
    Call<BaseResponseLogin<User>> loginUser(@Field("email") String email,
                                            @Field("password") String password);

    @POST("auth/edit")
    Call<BaseResponseLogin<User>> showProfile(@Header("Authorization") String token);

    @Multipart
    @POST("auth/update")
    Call<BaseResponse> updateProfile(@Part("name") RequestBody name,
                                     @Part("email") RequestBody email,
                                     @Part("phone") RequestBody phone,
                                     @Part("country") RequestBody country,
                                     @Part MultipartBody.Part image,
                                     @Header("Authorization") String token
    );

    @POST("auth/show_fav")
    Call<BaseResponseFavourite<ProductFavourite>> showFavouriteProducts(@Header("Authorization") String token
    );

    @POST("auth/cart")
    Call<BaseResponseCart<Products<ProductCart>>> pageCart(@Query("page") int page, @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("auth/increase_quantity")
    Call<BaseResponseCart<ProductCart>> increaseTheQuantity(@Field("id") int id,
                                                            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("auth/lower_quantity")
    Call<BaseResponseCart<ProductCart>> lowerTheQuantity(@Field("id") int id,
                                                         @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("auth/home")
    Call<BaseResponseCategory<Categories<Category>>> showCategories(@Query("page") int page,
                                                                    @Header("Authorization") String token, @Field("fcm_token") String fcmToken);

    @FormUrlEncoded
    @POST("auth/products")
    Call<BaseResponseStyle<Products<Style>>> showStyles(@Query("page") int page, @Field("category_id") int categoryId,
                                                        @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/product_items")
    Call<BaseResponseColorProduct<Products<ProductColors>>> showProductColors(@Query("page") int page, @Field("product_id") int styleId,
                                                                              @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/product_item")
    Call<BaseResponseDetailsProduct<ProductColors>> showProductDetails(@Field("product_item_id") int productId,
                                                                       @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/add_to_cart")
    Call<BaseResponse> addToCart(@Field("product_item_id") int productId,
                                 @Field("quantity") int quantity,
                                 @Field("size") String size,
                                 @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/add_fav")
    Call<BaseResponse> addToFavourite(@Field("product_id") int productId,
                                      @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/remove_fav")
    Call<BaseResponse> removeFromFavourite(@Field("product_id") int productId,
                                           @Header("Authorization") String token);

    @POST("auth/notifications")
    Call<BaseResponseNotification<Notification>> showNotifications(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/add_location")
    Call<BaseResponse> addLocation(
            @Field("location") String location,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/show_invoice")
    Call<BaseResponseUsePoint<OrderProduct>> showInvoice(
            @Field("use_point") int usePoint,
            @Header("Authorization") String token);

    @POST("auth/confirm_order")
    Call<BaseResponse<Order>> confirmOrder(
            @Header("Authorization") String token);

    @POST("auth/orders")
    Call<BaseResponseOrders<Products<Order>>> showOrders(@Query("page") int page,
                                                         @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/filter_orders")
    Call<BaseResponseFilterOrders<Order>> filterOrders(@Query("page") int page, @Field("type_order") String typeOrder,
                                                       @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/show_order")
    Call<BaseResponseShowOrder<Products<ShowOrder>>> showOrder(@Field("order_id") int orderId,
                                                               @Header("Authorization") String token);

    @POST("auth/points")
    Call<BaseResponseShowPoints<Products<Points>>> showTotalPoints(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/search")
    Call<BaseResponseSearch<Products<ProductSearch>>> search(@Query("page") int page, @Field("search") String search,
                                                             @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("auth/filter_products")
    Call<List<ProductSearch>> filterProducts(@Field("max") int max,
                                             @Field("min") int min,
                                             @Field("least_requested") int leastRequested,
                                             @Field("most_requested") int mostRequested,
                                             @Header("Authorization") String token);
    @POST("auth/discounts_products")
    Call<BaseResponseDiscounts<DiscountProduct>> showDiscountsProducts(@Header("Authorization") String token);

    //////////////////////////////////
    //////////////PUT////////////////
    ////////////////////////////////
    @FormUrlEncoded
    @PUT("auth/changePassword")
    Call<BaseResponse> changePassword(@Field("current_password") String currentPassword,
                                      @Field("new_password") String newPassword,
                                      @Field("new_password_confirmation") String newPasswordConfirmation,
                                      @Header("Authorization") String token);


    ///////////////////////////////////
    //////////////DELETE//////////////
    /////////////////////////////////

    @DELETE("auth/logout")
    Call<BaseResponse> logoutUser(@Header("Authorization") String token);

    @DELETE("auth/delete_cart")
    Call<BaseResponse> deleteCart(@Header("Authorization") String token);


    @DELETE("auth/delete_product_from_cart")
    Call<BaseResponse> deleteItemInCart(@Query("id") int id, @Header("Authorization") String token);
}
