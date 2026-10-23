package com.example.rajatrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;

    private TextView tvTogglePassword;
    private TextView tvForgotPassword;
    private TextView tvRegister;

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hubungkan komponen dari activity_login.xml
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        tvTogglePassword = findViewById(R.id.tvTogglePassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        // Tombol Login
        btnLogin.setOnClickListener(v -> login());

        // Tampilkan atau sembunyikan password
        tvTogglePassword.setOnClickListener(v -> togglePassword());

        // Tombol Lupa Password
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });

        // Tombol Daftar Sekarang
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validasi email kosong
        if (email.isEmpty()) {
            etEmail.setError("Email wajib diisi");
            etEmail.requestFocus();
            return;
        }

        // Validasi format email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }

        // Validasi password kosong
        if (password.isEmpty()) {
            etPassword.setError("Password wajib diisi");
            etPassword.requestFocus();
            return;
        }

        // Login demo: belum menggunakan Firebase Authentication
        Toast.makeText(
                LoginActivity.this,
                "Login berhasil",
                Toast.LENGTH_SHORT
        ).show();

        // Berpindah ke halaman Beranda RajaTrip
        Intent intent = new Intent(
                LoginActivity.this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }

    private void togglePassword() {
        int cursorPosition = etPassword.getSelectionStart();

        if (passwordVisible) {
            // Sembunyikan password
            etPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );

            tvTogglePassword.setText("◉");
            passwordVisible = false;

        } else {
            // Tampilkan password
            etPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );

            tvTogglePassword.setText("⊙");
            passwordVisible = true;
        }

        // Pertahankan jenis huruf
        etPassword.setTypeface(
                android.graphics.Typeface.DEFAULT
        );

        // Pertahankan posisi kursor
        int length = etPassword.getText().length();

        etPassword.setSelection(
                Math.max(0, Math.min(cursorPosition, length))
        );
    }
}
