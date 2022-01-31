package com.cvgs.cvgsapp.advances;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.cvgs.cvgsapp.model.LoginModel;

import java.util.HashMap;

public class SessionManager {

    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String ID_USER = "user";
    public static final String ID_DETAIL = "detail";
    public static final String NAMA = "nama";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(LoginModel loginModel) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(ID_DETAIL, loginModel.getId_detail());
        editor.putString(ID_USER, loginModel.getId_user());
        editor.putString(NAMA, loginModel.getNama());
        editor.putString(EMAIL, loginModel.getEmail());
        editor.putString(ROLE, loginModel.getRole());
        editor.apply();
    }

    public HashMap<String, String> getUserDetail() {
        HashMap<String, String> user = new HashMap<>();
        user.put(ID_USER, sharedPreferences.getString(ID_USER, null));
        user.put(ID_DETAIL, sharedPreferences.getString(ID_DETAIL, null));
        user.put(NAMA, sharedPreferences.getString(NAMA, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(ROLE, sharedPreferences.getString(ROLE, null));
        return user;
    }

    public void logoutSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

}
