package com.jhon.app_wolf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button btn_login;
    TextView error,crearcuenta;
    TextInputEditText email,password;

    public String ApiUrl = "https://letjopu.nyc.dom.my.id/api/";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(readToken(MainActivity.this)){
            startActivity(new Intent(MainActivity.this,Home.class));
        }else {


            btn_login = findViewById(R.id.btn_login);
            error = findViewById(R.id.error);
            crearcuenta = findViewById(R.id.crearcuenta);
            email = findViewById(R.id.email);
            password = findViewById(R.id.etPassword);

            btn_login.setOnClickListener(v -> {
                String ema = email.getText().toString();
                String pwd = password.getText().toString();

                login(ema, pwd);
            });

            crearcuenta.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Registro.class)));
        }
    }

    public void login(String email,String password){
        OkHttpClient client = new OkHttpClient();
        String token = "0000";
        RequestBody form = new FormBody.Builder()
                .add("email",email)
                .add("password",password)
                .build();

        Request request = new Request.Builder()
                .url(ApiUrl + "Login")
                .post(form)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    String jsonString = json.toString();
                    if (json.has("mensaje")){
                        final String mensaje = json.getString("mensaje");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                error.setText(mensaje);
                                error.setVisibility(View.VISIBLE);
                            }
                        });
                    } else if (json.has("token")) {
                        final String token = json.getString("token");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                guardarToken(MainActivity.this, token);
                                recreate();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            error.setText("Error al procesar la respuesta del servidor");
                        }
                    });
                }
            }
        });
    }

    public static void guardarToken(Context context, String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public static String obtenerToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public static void eliminarToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.apply();
    }

    // Verificar si el token existe
    public static boolean readToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains(KEY_AUTH_TOKEN) && sharedPreferences.getString(KEY_AUTH_TOKEN, null) != null;
    }
}