package com.ni___ckel.yesnoshake;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface ApiService {

    @GET("api")
    Single<AnswerFromBall> loadAnswerFromBall8();

}
