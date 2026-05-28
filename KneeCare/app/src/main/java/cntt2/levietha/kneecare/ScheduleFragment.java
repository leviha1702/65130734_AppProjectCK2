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
import androidx.fragment.app.Fragment;

public class ScheduleFragment extends Fragment {

    TextView txtScheduleContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        txtScheduleContent = view.findViewById(R.id.txtScheduleContent);

        // Gọi hàm đọc dữ liệu AI đã lưu
        loadSavedSchedule();

        return view;
    }

    private void loadSavedSchedule() {
        // Mở bộ nhớ SharedPreferences
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);

        // Lấy chuỗi văn bản AI đã lưu với Key là "saved_ai_schedule"
        String savedContent = pref.getString("saved_ai_schedule", null);

        if (savedContent != null && !savedContent.isEmpty()) {
            // Nếu có dữ liệu AI, hiển thị lên màn hình
            txtScheduleContent.setText(savedContent);
        } else {
            // Nếu chưa có (người dùng chưa kiểm tra lần nào)
            txtScheduleContent.setText("Cảnh báo: Dữ liệu trống!\n\nHãy vào mục 'Kiểm tra khớp gối', nhập triệu chứng để Trợ lý AI thiết kế lịch tập riêng cho bạn.");
        }
    }
}