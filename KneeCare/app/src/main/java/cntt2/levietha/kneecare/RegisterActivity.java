package cntt2.levietha.kneecare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText edtRegUsername, edtRegPassword;
    Button btnRegisterSubmit;
    TextView txtGoToLogin; // Thêm biến ánh xạ dòng chữ chuyển đổi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ giao diện
        edtRegUsername = findViewById(R.id.edtRegUsername);
        edtRegPassword = findViewById(R.id.edtRegPassword);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        txtGoToLogin = findViewById(R.id.txtGoToLogin); // Thực hiện ánh xạ

        // Xử lý sự kiện khi ấn dòng chữ "Đã có tài khoản? Đăng nhập ngay"
        txtGoToLogin.setOnClickListener(v -> {
            // Lệnh finish() sẽ đóng màn hình hiện tại lại để quay về MainActivity (Màn hình đăng nhập)
            finish();
        });

        // Xử lý logic nút đăng ký gửi dữ liệu
        btnRegisterSubmit.setOnClickListener(v -> {
            String user = edtRegUsername.getText().toString().trim();
            String pass = edtRegPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu thông tin đăng ký vào bộ nhớ tạm SharedPreferences
            SharedPreferences pref = getSharedPreferences("KneeCareData", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("username", user);
            editor.putString("password", pass);
            editor.apply();

            Toast.makeText(this, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Đăng ký xong tự động quay về màn hình đăng nhập
        });
    }
}