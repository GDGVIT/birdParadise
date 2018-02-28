package com.example.dell.birdingsimplified.Interfaces;

import com.example.dell.birdingsimplified.Models.NearbyBirdsModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;


public interface LocationBird_API {

    //https://api.myjson.com/bins/t0mb9

    String BASE_URL = "https://api.myjson.com/bins/";

    @GET("t0mb9")
    Call<List<NearbyBirdsModel>> getBirds();

}
