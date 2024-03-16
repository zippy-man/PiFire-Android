package com.weberbox.pifire.interfaces;

import androidx.annotation.NonNull;

import com.weberbox.pifire.enums.HttpResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface HttpCallback {
    void onFailure(@NonNull Call call, @NonNull IOException e);
    void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException;
    void urlFailure(HttpResult result);
}
