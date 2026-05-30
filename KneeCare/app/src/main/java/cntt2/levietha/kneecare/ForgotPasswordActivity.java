package cntt2.levietha.kneecare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtForgotEmail;
    Button btnSendEmailSubmit;
    TextView txtBackToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        edtForgotEmail = findViewById(R.id.edtForgotEmail);
        btnSendEmailSubmit = findViewById(R.id.btnSendEmailSubmit);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

        // Quay lại màn hình đăng nhập khi ấn chữ
        txtBackToLogin.setOnClickListener(v -> finish());

        // Xử lý gửi yêu cầu đặt lại mật khẩu lên Cloud
        btnSendEmailSubmit.setOnClickListener(v -> {
            String email = edtForgotEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ Email!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lệnh gọi Firebase tự động gửi mail khôi phục
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "📩 Đã gửi link đặt lại mật khẩu! Vui lòng kiểm tra Hòm thư (hoặc Hộp thư rác) của bạn.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Lỗi hệ thống";
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "❌ Thất bại: " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}