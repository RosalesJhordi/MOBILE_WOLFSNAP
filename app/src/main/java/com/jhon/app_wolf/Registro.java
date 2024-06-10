package com.jhon.app_wolf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Registro extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";
    Button btn_registro;
    TextInputEditText nombres, apellidos, telefono, emaill, password, pwdConfim;
    public String ApiUrl = "https://letjopu.nyc.dom.my.id/api/";
    private OkHttpClient client;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client = new OkHttpClient();

        btn_registro = findViewById(R.id.btn_registro);
        nombres = findViewById(R.id.etNombres);
        apellidos = findViewById(R.id.etApellidos);
        telefono = findViewById(R.id.etTelefono);
        emaill = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        pwdConfim = findViewById(R.id.etPasswordConfirm);
        error = findViewById(R.id.error);

        btn_registro.setOnClickListener(v -> {
            String nm = nombres.getText().toString();
            String ape = apellidos.getText().toString();
            String tel = telefono.getText().toString();
            String ema = emaill.getText().toString();
            String pwd = password.getText().toString();
            String pwd2 = pwdConfim.getText().toString();

            if (validateInputs(nm, ape, tel, ema, pwd, pwd2)) {
                registro(nm, ape, tel, ema, pwd, pwd2);
            }
        });
    }

    private boolean validateInputs(String nm, String ape, String tel, String ema, String pwd, String pwd2) {
        if (nm.isEmpty() || ape.isEmpty() || tel.isEmpty() || ema.isEmpty() || pwd.isEmpty() || pwd2.isEmpty()) {
            String mensaje = "Todos los campos con requeridos";
            error.setText(mensaje);
            error.setVisibility(View.VISIBLE);
            return false;
        }
        if (!pwd.equals(pwd2)) {
            String mensaje = "Las contraseñas no coinciden";
            error.setText(mensaje);
            error.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public void registro(String nombres, String apellidos, String telefono, String email, String password, String password_confirmation) {
        String token = "0000";
        RequestBody form = new FormBody.Builder()
                .add("nombres", nombres)
                .add("apellidos", apellidos)
                .add("telefono", telefono)
                .add("email", email)
                .add("password", password)
                .add("password_confirmation", password_confirmation)
                .build();

        Request request = new Request.Builder()
                .url(ApiUrl + "Registro")
                .post(form)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> showToast("Connection error"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject json = new JSONObject(responseData);
                            final String token = json.getString("token");
                            MainActivity.guardarToken(Registro.this, token);
                            startActivity(new Intent(Registro.this,Home.class));
                        } catch (JSONException e) {
                            showToast("Response parsing error: " + e.getMessage());
                        }
                    });
                } else {
                    runOnUiThread(() -> showToast("Empty response from server"));
                }
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(Registro.this, message, Toast.LENGTH_SHORT).show());
    }
}
