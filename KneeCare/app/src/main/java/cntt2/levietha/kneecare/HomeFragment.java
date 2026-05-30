package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HomeFragment extends Fragment {

    TextView txtWelcome, txtStatusSummary;
    CardView btnMenuSchedule, btnMenuCheck, btnMenuHistory, btnMenuResult;

    // Khai báo thêm các nút chức năng cho Thư viện bài tập phục hồi
    Button btnDoneEx1, btnDoneEx2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ các thành phần cũ
        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtStatusSummary = view.findViewById(R.id.txtStatusSummary);
        btnMenuSchedule = view.findViewById(R.id.btnMenuSchedule);
        btnMenuCheck = view.findViewById(R.id.btnMenuCheck);
        btnMenuHistory = view.findViewById(R.id.btnMenuHistory);
        btnMenuResult = view.findViewById(R.id.btnMenuResult);

        // Ánh xạ thành phần Thư viện bài tập mới bổ sung
        btnDoneEx1 = view.findViewById(R.id.btnDoneEx1);
        btnDoneEx2 = view.findViewById(R.id.btnDoneEx2);

        // 1. Hiển thị tên người dùng thân thiện từ SharedPreferences
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
        String username = pref.getString("username", "Người dùng");
        txtWelcome.setText("Xin chào, " + username + "!");

        // 2. LOGIC THỰC TẾ: Đọc bản ghi lịch sử mới nhất để hiển thị tóm tắt lên thẻ sức khỏe ở trang chủ
        String historyJson = pref.getString("medical_history_list", "[]");
        try {
            JsonArray jsonArray = JsonParser.parseString(historyJson).getAsJsonArray();
            if (jsonArray.size() > 0) {
                JsonObject latestRecord = jsonArray.get(0).getAsJsonObject();
                String date = latestRecord.get("date").getAsString();
                String symptoms = latestRecord.get("symptoms").getAsString();
                txtStatusSummary.setText("Chẩn đoán gần nhất (" + date + "):\n" + symptoms + "\n👉 Chọn mục 'Kết quả AI' hoặc 'Lịch sử' để xem chi tiết bài tập.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. LOGIC MỚI: Quản lý trạng thái hoàn thành bài tập (Tính năng Thư viện phục hồi)
        setupExerciseStatus("ex1_status", btnDoneEx1, "Bài 1: Tập cơ đùi trước");
        setupExerciseStatus("ex2_status", btnDoneEx2, "Bài 2: Giãn cơ bắp chân");

        // 4. Xử lý sự kiện click liên kết mượt mà chuyển tab hoặc nạp đè Fragment
        btnMenuSchedule.setOnClickListener(v -> navigateToFragment(new ScheduleFragment()));
        btnMenuCheck.setOnClickListener(v -> navigateToFragment(new CheckFragment()));
        btnMenuHistory.setOnClickListener(v -> navigateToFragment(new HistoryFragment()));
        btnMenuResult.setOnClickListener(v -> navigateToFragment(new ResultFragment()));

        return view;
    }

    /**
     * Hàm xử lý logic lưu trạng thái hoàn thành bài tập vào bộ nhớ máy SharedPreferences
     */
    private void setupExerciseStatus(String key, Button btn, String exerciseName) {
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);

        // Đọc trạng thái cũ lên để hiển thị giao diện phù hợp
        boolean isDone = pref.getBoolean(key, false);
        updateButtonUI(btn, isDone);

        btn.setOnClickListener(v -> {
            boolean currentStatus = pref.getBoolean(key, false);
            boolean newStatus = !currentStatus; // Đảo trạng thái khi bấm nút

            // Lưu trạng thái mới vào ổ cứng dưới thiết bị
            pref.edit().putBoolean(key, newStatus).apply();
            updateButtonUI(btn, newStatus);

            if (newStatus) {
                Toast.makeText(getActivity(), "Đã hoàn thành " + exerciseName + " 🎉 Kỷ luật rất tốt!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hàm phụ trợ thay đổi giao diện nút bấm theo trạng thái hoàn thành
     */
    private void updateButtonUI(Button btn, boolean isDone) {
        if (isDone) {
            btn.setText("✓ Đã xong");
            btn.setBackgroundColor(Color.parseColor("#2ECC71")); // Đổi sang màu xanh lá cây
            btn.setTextColor(Color.WHITE);
        } else {
            btn.setText("Hoàn thành");
            btn.setBackgroundColor(Color.parseColor("#3498DB")); // Màu xanh dương mặc định
            btn.setTextColor(Color.WHITE);
        }
    }

    /**
     * Hàm phụ trợ dùng để hoán đổi Fragment nhanh ngay trong luồng hoạt động
     */
    private void navigateToFragment(Fragment targetFragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}