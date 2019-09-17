package com.mrappstore.mushfik.friends.api.services;

import com.mrappstore.mushfik.friends.activity.LoginActivity;
import com.mrappstore.mushfik.friends.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface UserInterface {

    @POST("login")
    Call<Integer>signin(@Body LoginActivity.UserInfo userInfo);

    @GET("loadownprofile")
    Call<User> loadownprofile(@QueryMap Map<String,String> params);

    @POST("poststatus")
    Call<Integer>uploadStatus(@Body MultipartBody requestBody);

    @POST("uploadImage")
    Call<Integer>uploadImage(@Body MultipartBody requestBody);

    @GET("search")
    Call<List<User>>search(@QueryMap Map<String,String> params);

}
