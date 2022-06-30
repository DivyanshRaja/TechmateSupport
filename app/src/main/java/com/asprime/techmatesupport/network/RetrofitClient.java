package com.asprime.techmatesupport.network;

import com.asprime.techmatesupport.BuildConfig;
import com.asprime.techmatesupport.utils.CommonUtils;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getApiClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(40, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .header("Authorization", "Bearer " + CommonUtils.encryptQR())
                                .build();
                        return chain.proceed(newRequest);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BuildConfig.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getMultipartApiClient() {
        Retrofit retrofit1 = null;
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();

        retrofit1 = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.FILE_POST_URL)
                .build();
        return retrofit1;
    }
}
