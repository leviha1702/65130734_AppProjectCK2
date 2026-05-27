package cntt2.levietha.kneecare;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeActivity extends AppCompatActivity {

    // Khai báo 6 nút bấm dưới chân menu tự chế
    LinearLayout btnNavHome, btnNavSchedule, btnNavCheck, btnNavChatbot, btnNavHistory, btnNavSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ toàn bộ 6 nút bấm
        btnNavHome = findViewById(R.id.nav_home);
        btnNavSchedule = findViewById(R.id.nav_schedule);
        btnNavCheck = findViewById(R.id.nav_check);
        btnNavChatbot = findViewById(R.id.nav_chatbot);
        btnNavHistory = findViewById(R.id.nav_history);
        btnNavSetting = findViewById(R.id.nav_setting);

        // Mặc định khi mở ứng dụng, nạp ngay Trang chủ (HomeFragment)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // 1. Click nút Trang chủ
        btnNavHome.setOnClickListener(v -> {
            changeFragment(new HomeFragment());
        });

        // 2. Click nút Lịch tập
        btnNavSchedule.setOnClickListener(v -> {
            changeFragment(new ScheduleFragment());
        });

        // 3. Click nút Kiểm tra khớp gối
        btnNavCheck.setOnClickListener(v -> {
            changeFragment(new CheckFragment());
        });

        // 4. Click nút Trợ lý Chatbot AI
        btnNavChatbot.setOnClickListener(v -> {
            // Nạp ResultFragment làm khu vực Chatbot nhận diện dữ liệu thông minh từ Cloud AI
            changeFragment(new ChatbotFragment());
        });

        // 5. Click nút Lịch sử
        btnNavHistory.setOnClickListener(v -> {
            changeFragment(new HistoryFragment());
        });

        // 6. Click nút Cài đặt tài khoản (Đổi mật khẩu / Đăng xuất)
        btnNavSetting.setOnClickListener(v -> {
            changeFragment(new SettingsFragment());
        });
    }

    /**
     * Hàm dùng chung để thực hiện hoán đổi linh hoạt các Fragment
     */
    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}