package com.example.rajatrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etRegisterEmail;
    private EditText etPhone;
    private EditText etRegisterPassword;
    private EditText etConfirmPassword;

    private TextView tvBackRegister;
    private TextView tvLoginFromRegister;
    private TextView tvToggleRegisterPassword;
    private TextView tvToggleConfirmRegisterPassword;

    private Button btnRegister;
    private CheckBox cbTerms;

    private boolean registerPasswordVisible = false;
    private boolean confirmPasswordVisible = false;


    private FirebaseAuth firebaseAuth;
    private static final String TAG = "RajaTripRegister";

    // GANTI dengan alamat IPv4 laptop kamu.
    private static final String API_URL =
            "http://192.168.1.201/rajatrip_api/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();

        // Hubungkan komponen XML
        etFullName = findViewById(R.id.etFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etPhone = findViewById(R.id.etPhone);
        etRegisterPassword =
                findViewById(R.id.etRegisterPassword);
        etConfirmPassword =
                findViewById(R.id.etConfirmPassword);

        tvBackRegister = findViewById(R.id.tvBackRegister);
        tvLoginFromRegister =
                findViewById(R.id.tvLoginFromRegister);

        tvToggleRegisterPassword =
                findViewById(R.id.tvToggleRegisterPassword);
        tvToggleConfirmRegisterPassword =
                findViewById(R.id.tvToggleConfirmRegisterPassword);

        btnRegister = findViewById(R.id.btnRegister);
        cbTerms = findViewById(R.id.cbTerms);

        // Kembali ke Login
        tvBackRegister.setOnClickListener(v -> backToLogin());
        tvLoginFromRegister.setOnClickListener(v -> backToLogin());

        // Tampilkan atau sembunyikan password
        tvToggleRegisterPassword.setOnClickListener(
                v -> togglePassword(true)
        );

        tvToggleConfirmRegisterPassword.setOnClickListener(
                v -> togglePassword(false)
        );

        // Validasi pendaftaran
        btnRegister.setOnClickListener(v -> register());


        // Tombol sosial
        findViewById(R.id.btnGoogle).setOnClickListener(
                v -> showSocialMessage("Google")
        );

        findViewById(R.id.btnFacebook).setOnClickListener(
                v -> showSocialMessage("Facebook")
        );

        findViewById(R.id.btnApple).setOnClickListener(
                v -> showSocialMessage("Apple")
        );

    }

    private void register() {
        String name = etFullName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password =
                etRegisterPassword.getText().toString();
        String confirmPassword =
                etConfirmPassword.getText().toString();

        // Validasi nama
        if (name.isEmpty()) {
            etFullName.setError("Nama lengkap wajib diisi");
            etFullName.requestFocus();
            return;
        }

        // Validasi email
        if (email.isEmpty()) {
            etRegisterEmail.setError("Email wajib diisi");
            etRegisterEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.setError("Format email tidak valid");
            etRegisterEmail.requestFocus();
            return;
        }

        // Validasi nomor telepon
        if (phone.isEmpty()) {
            etPhone.setError("Nomor telepon wajib diisi");
            etPhone.requestFocus();
            return;
        }

        String digitsOnly = phone.replaceAll("[^0-9]", "");

        if (digitsOnly.length() < 10 || digitsOnly.length() > 15) {
            etPhone.setError("Masukkan nomor telepon yang valid");
            etPhone.requestFocus();
            return;
        }

        // Validasi password
        if (password.isEmpty()) {
            etRegisterPassword.setError("Password wajib diisi");
            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etRegisterPassword.setError(
                    "Password minimal 8 karakter"
            );
            etRegisterPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            etRegisterPassword.setError(
                    "Tambahkan minimal satu huruf besar"
            );
            etRegisterPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[a-z].*")) {
            etRegisterPassword.setError(
                    "Tambahkan minimal satu huruf kecil"
            );
            etRegisterPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[0-9].*")) {
            etRegisterPassword.setError(
                    "Tambahkan minimal satu angka"
            );
            etRegisterPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[^a-zA-Z0-9\\s].*")) {
            etRegisterPassword.setError(
                    "Tambahkan minimal satu karakter khusus"
            );
            etRegisterPassword.requestFocus();
            return;
        }

        // Validasi konfirmasi password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError(
                    "Konfirmasi password wajib diisi"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Password tidak sama"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        // Persetujuan syarat dan ketentuan
        if (!cbTerms.isChecked()) {
            Toast.makeText(
                    this,
                    "Setujui Syarat dan Ketentuan terlebih dahulu",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Validasi berhasil, tetapi akun belum disimpan
        createFirebaseAccount(name, email, phone, password);
    }

    private void createFirebaseAccount(
            String name,
            String email,
            String phone,
            String password
    ) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Mendaftarkan...");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Jika akun Firebase sudah terbuat tetapi profil MySQL
        // gagal disimpan sebelumnya, coba sinkronkan profil lagi.
        if (currentUser != null
                && email.equalsIgnoreCase(currentUser.getEmail())) {

            syncProfileToApi(name, phone, currentUser);
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        String message = "Pendaftaran Firebase gagal.";

                        Exception exception = task.getException();

                        if (exception != null) {
                            String detail = exception.getMessage();

                            if (detail != null) {
                                if (detail.contains("already in use")) {
                                    message = "Email sudah terdaftar.";
                                } else if (detail.contains("network")) {
                                    message = "Periksa koneksi internet.";
                                } else if (detail.contains("WEAK_PASSWORD")) {
                                    message = "Password terlalu lemah.";
                                }
                            }

                            Log.e(TAG, "Firebase registration failed", exception);
                        }

                        restoreRegisterButton();

                        Toast.makeText(
                                RegisterActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user == null) {
                        restoreRegisterButton();

                        Toast.makeText(
                                RegisterActivity.this,
                                "Akun dibuat, tetapi pengguna tidak ditemukan.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    syncProfileToApi(name, phone, user);
                });
    }

    private void syncProfileToApi(
            String name,
            String phone,
            FirebaseUser user
    ) {
        user.getIdToken(false)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String idToken = task.getResult().getToken();

                        if (idToken == null || idToken.isEmpty()) {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Firebase ID Token kosong",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        sendProfileToApi(name, phone, idToken);
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Tidak diketahui";

                        Toast.makeText(
                                RegisterActivity.this,
                                "Gagal mengambil Firebase ID Token: " + error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void sendProfileToApi(
            String name,
            String phone,
            String idToken
    ) {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + idToken
                );

                JSONObject requestBody = new JSONObject();
                requestBody.put("full_name", name);
                requestBody.put("phone", phone);

                byte[] body = requestBody.toString()
                        .getBytes(StandardCharsets.UTF_8);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(body);
                    output.flush();
                }

                int statusCode = connection.getResponseCode();

                InputStream inputStream;

                if (statusCode >= 200 && statusCode < 300) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                StringBuilder response = new StringBuilder();

                if (inputStream != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    inputStream,
                                    StandardCharsets.UTF_8
                            ))) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                }

                JSONObject jsonResponse =
                        new JSONObject(response.toString());

                Log.d(TAG, "API URL: " + API_URL);
                Log.d(TAG, "HTTP status: " + statusCode);
                Log.d(TAG, "Respons PHP: " + response);

                boolean success =
                        statusCode >= 200
                                && statusCode < 300
                                && jsonResponse.optBoolean("success", false);

                String message = jsonResponse.optString(
                        "message",
                        "Server tidak memberikan pesan."
                );

                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Pendaftaran berhasil!",
                                Toast.LENGTH_LONG
                        ).show();

                        firebaseAuth.signOut();
                        backToLogin();

                    } else {
                        restoreRegisterButton();

                        Toast.makeText(
                                RegisterActivity.this,
                                "Profil belum tersimpan: " + message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "PHP API registration error", e);

                runOnUiThread(() -> {
                    restoreRegisterButton();

                    Toast.makeText(
                            RegisterActivity.this,
                            "Tidak dapat terhubung ke server. "
                                    + "Periksa alamat IP, Apache, dan koneksi.",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void restoreRegisterButton() {
        runOnUiThread(() -> {
            btnRegister.setEnabled(true);
            btnRegister.setText("Daftar Sekarang");
        });
    }

    private void togglePassword(boolean isRegisterPassword) {
        EditText target;
        TextView toggle;
        boolean visible;

        if (isRegisterPassword) {
            target = etRegisterPassword;
            toggle = tvToggleRegisterPassword;
            visible = registerPasswordVisible;
        } else {
            target = etConfirmPassword;
            toggle = tvToggleConfirmRegisterPassword;
            visible = confirmPasswordVisible;
        }

        int cursorPosition = target.getSelectionStart();

        if (visible) {
            target.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
            toggle.setText("◉");
        } else {
            target.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            toggle.setText("⊙");
        }

        target.setTypeface(android.graphics.Typeface.DEFAULT);

        int length = target.getText().length();
        target.setSelection(
                Math.max(0, Math.min(cursorPosition, length))
        );

        if (isRegisterPassword) {
            registerPasswordVisible = !visible;
        } else {
            confirmPasswordVisible = !visible;
        }
    }

    private void showSocialMessage(String provider) {
        Toast.makeText(
                this,
                "Pendaftaran dengan " + provider
                        + " belum dihubungkan.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void backToLogin() {
        Intent intent = new Intent(
                RegisterActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    private void testFirebaseOnly(String email, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Menguji Firebase...");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Daftar Sekarang");

                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        String uid = user != null ? user.getUid() : "-";

                        Toast.makeText(
                                RegisterActivity.this,
                                "FIREBASE BERHASIL. UID: " + uid,
                                Toast.LENGTH_LONG
                        ).show();

                        Log.d("RajaTripFirebase",
                                "Akun berhasil dibuat, UID: " + uid);

                    } else {
                        Exception error = task.getException();

                        String message = error != null
                                ? error.getMessage()
                                : "Error tidak diketahui";

                        Toast.makeText(
                                RegisterActivity.this,
                                "FIREBASE GAGAL: " + message,
                                Toast.LENGTH_LONG
                        ).show();

                        Log.e("RajaTripFirebase",
                                "Pembuatan akun gagal", error);
                    }
                });
    }
}
