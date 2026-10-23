package com.example.rajatrip;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private TextView tvBackLogin;
    private TextView tvToggleNewPassword;
    private TextView tvToggleConfirmPassword;

    private Button btnSavePassword;

    private boolean newPasswordVisible = false;
    private boolean confirmPasswordVisible = false;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private Dialog successDialog;
    private boolean isLeavingPage = false;

    private final Runnable returnToLoginRunnable = () -> {
        if (!isFinishing() && !isDestroyed()) {
            if (successDialog != null && successDialog.isShowing()) {
                successDialog.dismiss();
            }
            backToLogin();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Hubungkan komponen XML
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        tvBackLogin = findViewById(R.id.tvBackLogin);
        tvToggleNewPassword =
                findViewById(R.id.tvToggleNewPassword);
        tvToggleConfirmPassword =
                findViewById(R.id.tvToggleConfirmPassword);

        btnSavePassword = findViewById(R.id.btnSavePassword);

        // Tombol kembali ke login
        tvBackLogin.setOnClickListener(v -> backToLogin());

        // Tampilkan atau sembunyikan password
        tvToggleNewPassword.setOnClickListener(
                v -> togglePassword(true)
        );

        tvToggleConfirmPassword.setOnClickListener(
                v -> togglePassword(false)
        );

        // Tombol simpan password
        btnSavePassword.setOnClickListener(v -> savePassword());
    }

    private void savePassword() {
        String password = etNewPassword.getText()
                .toString();

        String confirmPassword = etConfirmPassword.getText()
                .toString();

        // Validasi password kosong
        if (password.isEmpty()) {
            etNewPassword.setError(
                    "Password baru wajib diisi"
            );
            etNewPassword.requestFocus();
            return;
        }

        // Minimal 8 karakter
        if (password.length() < 8) {
            etNewPassword.setError(
                    "Password minimal 8 karakter"
            );
            etNewPassword.requestFocus();
            return;
        }

        // Harus memiliki huruf besar
        if (!password.matches(".*[A-Z].*")) {
            etNewPassword.setError(
                    "Tambahkan minimal satu huruf besar"
            );
            etNewPassword.requestFocus();
            return;
        }

        // Harus memiliki huruf kecil
        if (!password.matches(".*[a-z].*")) {
            etNewPassword.setError(
                    "Tambahkan minimal satu huruf kecil"
            );
            etNewPassword.requestFocus();
            return;
        }

        // Harus memiliki angka
        if (!password.matches(".*[0-9].*")) {
            etNewPassword.setError(
                    "Tambahkan minimal satu angka"
            );
            etNewPassword.requestFocus();
            return;
        }

        // Harus memiliki karakter khusus
        if (!password.matches(".*[^a-zA-Z0-9\\s].*")) {
            etNewPassword.setError(
                    "Tambahkan minimal satu karakter khusus"
            );
            etNewPassword.requestFocus();
            return;
        }

        // Konfirmasi password wajib diisi
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError(
                    "Konfirmasi password wajib diisi"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        // Password harus sama
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Password tidak sama"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        // Validasi berhasil: tampilkan popup custom
        showSuccessPopup();
    }

    private void showSuccessPopup() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        successDialog = new Dialog(this);
        successDialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );
        successDialog.setContentView(
                R.layout.dialog_password_success
        );
        successDialog.setCancelable(false);

        Window window = successDialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            window.addFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
            );

            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.width = (int) (
                    getResources().getDisplayMetrics().widthPixels
                            * 0.84f
            );

            params.height =
                    WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            params.dimAmount = 0.65f;

            window.setAttributes(params);
        }

        // Tombol X
        TextView closeButton =
                successDialog.findViewById(R.id.btnCloseSuccess);

        closeButton.setOnClickListener(v -> {
            handler.removeCallbacks(returnToLoginRunnable);
            successDialog.dismiss();
            backToLogin();
        });

        successDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(returnToLoginRunnable);
        });

        successDialog.show();

        if (successDialog.getWindow() != null) {
            successDialog.getWindow().setLayout(
                    (int) (
                            getResources().getDisplayMetrics()
                                    .widthPixels * 0.84f
                    ),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }

        // Otomatis kembali ke login setelah 3 detik
        handler.postDelayed(returnToLoginRunnable, 3000);
    }

    private void togglePassword(boolean isNewPassword) {
        EditText target;
        TextView toggle;
        boolean visible;

        if (isNewPassword) {
            target = etNewPassword;
            toggle = tvToggleNewPassword;
            visible = newPasswordVisible;
        } else {
            target = etConfirmPassword;
            toggle = tvToggleConfirmPassword;
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

        if (isNewPassword) {
            newPasswordVisible = !visible;
        } else {
            confirmPasswordVisible = !visible;
        }
    }

    private void backToLogin() {
        if (isLeavingPage) {
            return;
        }

        isLeavingPage = true;
        handler.removeCallbacks(returnToLoginRunnable);

        Intent intent = new Intent(
                ForgotPasswordActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(returnToLoginRunnable);

        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
        }

        super.onDestroy();
    }
}
