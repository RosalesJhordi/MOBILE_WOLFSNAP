package com.jhon.app_wolf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
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

public class Home extends AppCompatActivity {

    public String token;
    public String ApiUrl = "https://letjopu.nyc.dom.my.id/api/";
    private static final String TAG = "HomeActivity";
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        button = findViewById(R.id.button);
        token = MainActivity.obtenerToken(Home.this);
        token(token);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.eliminarToken(Home.this);
                startActivity(new Intent(Home.this,MainActivity.class));
            }
        });
    }

    public void token(String token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody form = new FormBody.Builder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(ApiUrl + "AuthToken")
                .post(form)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(Home.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d(TAG, "Response: " + responseData);

                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(responseData);
                        JSONObject user = json.getJSONObject("user");
                        String nombre = user.getString("nombres");
                        Toast.makeText(Home.this, "Bienvenido " +nombre, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(Home.this, "Error en la respuesta JSON", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
    }
}