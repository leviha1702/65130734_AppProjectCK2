package cntt2.levietha.kneecare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGoToRegister = findViewById(R.id.txtGoToRegister);

        // Xử lý chuyển sang màn hình Đăng ký
        txtGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Xử lý logic Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String inputUser = edtUsername.getText().toString().trim();
            String inputPass = edtPassword.getText().toString().trim();

            // Đọc tài khoản đã lưu trong máy ra để kiểm tra
            SharedPreferences pref = getSharedPreferences("KneeCareData", MODE_PRIVATE);
            String savedUser = pref.getString("username", null);
            String savedPass = pref.getString("password", null);

            if (savedUser == null || savedPass == null) {
                Toast.makeText(this, "Hệ thống chưa có tài khoản nào. Vui lòng ấn Đăng ký!", Toast.LENGTH_LONG).show();
            } else if (inputUser.equals(savedUser) && inputPass.equals(savedPass)) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}