package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout fragment_main_menu vào đối tượng view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ các thành phần
        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtStatusSummary = view.findViewById(R.id.txtStatusSummary);
        btnMenuSchedule = view.findViewById(R.id.btnMenuSchedule);
        btnMenuCheck = view.findViewById(R.id.btnMenuCheck);
        btnMenuHistory = view.findViewById(R.id.btnMenuHistory);
        btnMenuResult = view.findViewById(R.id.btnMenuResult);

        // 1. Hiển thị tên người dùng thân thiện từ SharedPreferences
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
        String username = pref.getString("username", "Người dùng");
        txtWelcome.setText("Xin chào, " + username + "!");

        // 2. LOGIC THỰC TẾ: Đọc bản ghi lịch sử mới nhất để hiển thị tóm tắt lên thẻ sức khỏe ở trang chủ
        String historyJson = pref.getString("medical_history_list", "[]");
        try {
            JsonArray jsonArray = JsonParser.parseString(historyJson).getAsJsonArray();
            if (jsonArray.size() > 0) {
                // Lấy bản ghi đầu tiên (Mới nhất)
                JsonObject latestRecord = jsonArray.get(0).getAsJsonObject();
                String date = latestRecord.get("date").getAsString();
                String symptoms = latestRecord.get("symptoms").getAsString();
                txtStatusSummary.setText("Chẩn đoán gần nhất (" + date + "):\n" + symptoms + "\n👉 Chọn mục 'Kết quả AI' hoặc 'Lịch sử' để xem chi tiết bài tập.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Xử lý sự kiện click liên kết mượt mà chuyển tab hoặc nạp đè Fragment
        btnMenuSchedule.setOnClickListener(v -> {
            navigateToFragment(new ScheduleFragment());
        });

        btnMenuCheck.setOnClickListener(v -> {
            navigateToFragment(new CheckFragment());
        });

        btnMenuHistory.setOnClickListener(v -> {
            navigateToFragment(new HistoryFragment());
        });

        btnMenuResult.setOnClickListener(v -> {
            navigateToFragment(new ResultFragment());
        });

        return view;
    }

    /**
     * Hàm phụ trợ dùng để hoán đổi Fragment nhanh ngay trong luồng hoạt động
     */
    private void navigateToFragment(Fragment targetFragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .addToBackStack(null) // Đẩy vào stack để khi bấm Back vật lý sẽ quay về màn hình 4 nút gốc này
                    .commit();
        }
    }
}