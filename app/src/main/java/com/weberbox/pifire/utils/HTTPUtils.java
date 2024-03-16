package com.weberbox.pifire.utils;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;

import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HTTPUtils {

    public static OkHttpClient createHttpClient(boolean redirects, boolean sslRedirects) {
        return new OkHttpClient.Builder()
                .followRedirects(redirects)
                .followSslRedirects(sslRedirects)
                .build();
    }

    public static Request createHttpRequest(String url, String credentials) {
        Request request;
        if (credentials != null) {
            request = new Request.Builder()
                    .header("Authorization", credentials).url(url).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        return request;
    }

    public static void createHttpGet(Context context, String url, Callback callback) {
        OkHttpClient client = HTTPUtils.createHttpClient(true, true);
        Request request = HTTPUtils.createHttpRequest(url, getCredentials(context));
        client.newCall(request).enqueue(callback);
    }

    public static void createHttpPost(Context context, String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json"));
        OkHttpClient client = HTTPUtils.createHttpClient(true, true);
        Request request = HTTPUtils.createHttpRequest(url, getCredentials(context));
        Request request_body = request.newBuilder().post(body).build();
        client.newCall(request_body).enqueue(callback);
    }

    private static String getCredentials(Context context) {
        String credentials;

        if (Prefs.getBoolean(context.getString(R.string.prefs_server_basic_auth), false)) {
            String username = SecurityUtils.decrypt(context,
                    R.string.prefs_server_basic_auth_user);
            String password = SecurityUtils.decrypt(context,
                    R.string.prefs_server_basic_auth_password);
            credentials = Credentials.basic(username, password);
        } else {
            credentials = null;
        }
        return credentials;
    }
}
