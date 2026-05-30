package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth; // Bắt buộc thêm thư viện Firebase Auth

public class RegisterActivity extends AppCompatActivity {

    EditText edtRegUsername, edtRegPassword;
    Button btnRegisterSubmit;
    TextView txtGoToLogin;

    // Khai báo đối tượng Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        edtRegUsername = findViewById(R.id.edtRegUsername);
        edtRegPassword = findViewById(R.id.edtRegPassword);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        txtGoToLogin = findViewById(R.id.txtGoToLogin);

        txtGoToLogin.setOnClickListener(v -> finish());

        btnRegisterSubmit.setOnClickListener(v -> {
            String email = edtRegUsername.getText().toString().trim(); // Firebase Auth bắt buộc tài khoản phải là định dạng Email
            String pass = edtRegPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự bảo mật!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 TIẾN HÀNH ĐĂNG KÝ TÀI KHOẢN LÊN FIREBASE ĐÁM MÂY
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // 1. Đăng ký Cloud thành công -> Tiến hành bóc tách tên hiển thị và mã sinh viên lưu đệm cục bộ
                            SharedPreferences pref = getSharedPreferences("KneeCareData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();

                            // Cắt chuỗi lấy phần trước chữ @ để làm tên hiển thị thân thiện ở Trang chủ
                            String friendlyName = email.split("@")[0];

                            editor.putString("username", friendlyName);
                            editor.putString("student_id", "65130734_LeVietHa"); // Gắn mã định danh làm khóa ngoại đồng bộ hệ thống
                            editor.putBoolean("isLoggedIn", true); // Bật cờ trạng thái đăng nhập thành công
                            editor.apply();

                            Toast.makeText(this, "🎉 Đăng ký tài khoản trên Cloud Firebase thành công!", Toast.LENGTH_LONG).show();
                            finish(); // Đóng màn hình quay về trang đăng nhập hoặc trang chủ
                        } else {
                            // Nếu Firebase từ chối (Ví dụ: Trùng email, sai định dạng email...)
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(this, "❌ Đăng ký thất bại: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}