package cntt2.levietha.kneecare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // Đảm bảo import đúng thư viện này

public class CheckFragment extends Fragment {

    // Khai báo các thành phần giao diện
    Spinner spinnerPain;
    CheckBox cbUnstable, cbRunPain;
    Button btnAnalyze;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Nạp layout của trang kiểm tra (đổi sang fragment_check cho đúng tên file XML của bạn)
        View view = inflater.inflate(R.layout.fragment_check, container, false);

        // 2. Ánh xạ giao diện phải thông qua biến "view"
        spinnerPain = view.findViewById(R.id.spinnerPain);
        cbUnstable = view.findViewById(R.id.cbUnstable);
        cbRunPain = view.findViewById(R.id.cbRunPain);
        btnAnalyze = view.findViewById(R.id.btnAnalyze);

        // 3. Đổ dữ liệu vào Spinner (Sử dụng getActivity() thay cho "this")
        String[] levels = {"Nhẹ", "Trung bình", "Nặng"};
        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_spinner_item,
                    levels
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPain.setAdapter(adapter);
        }

        // 4. Xử lý sự kiện khi bấm nút Phân tích
        // SỬA ĐOẠN NÀY TRONG LỚP CheckFragment.java ĐỂ TRUYỀN SANG FRAGMENT MỚI CHUẨN XỊN:
        btnAnalyze.setOnClickListener(v -> {
            String painLevel = spinnerPain.getSelectedItem().toString();
            boolean unstable = cbUnstable.isChecked();
            boolean runPain = cbRunPain.isChecked();

            // 1. Khởi tạo ResultFragment mới
            ResultFragment resultFrag = new ResultFragment();

            // 2. Đóng gói dữ liệu bằng Bundle để gửi sang Fragment
            Bundle bundle = new Bundle();
            bundle.putString("painLevel", painLevel);
            bundle.putBoolean("unstable", unstable);
            bundle.putBoolean("runPain", runPain);
            resultFrag.setArguments(bundle);

            // 3. Gọi lệnh nạp đè màn hình ngay tại HomeActivity
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, resultFrag)
                        .addToBackStack(null) // Cho phép bấm back quay lại form điền dữ liệu
                        .commit();
            }
        });
        return view;
    }
}