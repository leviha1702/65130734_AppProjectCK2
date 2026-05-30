package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtGoToRegister;

    TextView txtForgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGoToRegister = findViewById(R.id.txtGoToRegister);

        // Kết nối biến Java với ID bên file XML
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // TỰ ĐỘNG ĐĂNG NHẬP (Auto-Login)
        SharedPreferences prefCheck = getSharedPreferences("KneeCareData", MODE_PRIVATE);
        boolean isLoggedIn = prefCheck.getBoolean("isLoggedIn", false);
        if (isLoggedIn && mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Chuyển sang màn hình Đăng ký
        txtGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Xử lý logic Đăng nhập trực tuyến qua Firebase
        btnLogin.setOnClickListener(v -> {
            String email = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            SharedPreferences loginPref = getSharedPreferences("KneeCareData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = loginPref.edit();

                            String friendlyName = email.split("@")[0];

                            editor.putString("username", friendlyName);
                            editor.putString("student_id", "65130734_LeVietHa");
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            Toast.makeText(this, "🚀 Đăng nhập hệ thống Cloud thành công!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "❌ Đăng nhập thất bại: Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}