package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    TextView txtUserDisplay;
    EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    Button btnChangePassword, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Ánh xạ các thành phần giao diện
        txtUserDisplay = view.findViewById(R.id.txtUserDisplay);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = view.findViewById(R.id.edtConfirmNewPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Đọc thông tin tên tài khoản từ bộ nhớ máy
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
        String currentUsername = pref.getString("username", "Người dùng");
        txtUserDisplay.setText("Tên người dùng: " + currentUsername);

        // 1. Xử lý Logic đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            String oldPass = edtOldPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirmPass = edtConfirmNewPassword.getText().toString().trim();

            String savedPass = pref.getString("password", "");

            // Kiểm tra tính hợp lệ dữ liệu đầu vào
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ các trường!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!oldPass.equals(savedPass)) {
                Toast.makeText(getActivity(), "Mật khẩu hiện tại không chính xác!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 4) {
                Toast.makeText(getActivity(), "Mật khẩu mới phải từ 4 ký tự trở lên!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(getActivity(), "Xác nhận mật khẩu mới không trùng khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu mật khẩu mới đè lên mật khẩu cũ trong SharedPreferences
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("password", newPass);
            editor.apply();

            Toast.makeText(getActivity(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();

            // Xóa sạch text trong form sau khi đổi thành công
            edtOldPassword.setText("");
            edtNewPassword.setText("");
            edtConfirmNewPassword.setText("");
        });

        // 2. Xử lý logic Đăng xuất
        // Tìm đến sự kiện Click của nút Đăng xuất (Logout) của bạn và thay bằng đoạn này:
        btnLogout.setOnClickListener(v -> {
            // 1. ĐĂNG XUẤT TRÊN CLOUD: Hủy phiên làm việc trực tuyến của Firebase Auth
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();

            // 2. XÓA CỜ LOCAL: Chuyển cờ isLoggedIn về false và xóa dữ liệu đệm trong máy
            android.content.SharedPreferences prefCheck = getActivity().getSharedPreferences("KneeCareData", android.content.Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefCheck.edit();
            editor.putBoolean("isLoggedIn", false); // Ép cờ tự động đăng nhập về false
            editor.putString("username", "Người dùng"); // Trả tên hiển thị về mặc định
            editor.apply(); // Lưu lại ổ cứng dưới thiết bị

            android.widget.Toast.makeText(getActivity(), "👋 Đã đăng xuất tài khoản an toàn!", android.widget.Toast.LENGTH_SHORT).show();

            // 3. ĐIỀU HƯỚNG CHUẨN: Quay trở về màn hình Đăng nhập gốc (MainActivity)
            android.content.Intent intent = new Intent(getActivity(), MainActivity.class);
            // Xóa sạch các màn hình chạy ngầm trước đó để tránh người dùng bấm nút Back quay lại trang chủ
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish(); // Đóng hẳn màn hình HomeActivity hiện tại
            }
        });

        return view;
    }
}