package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cntt2.levietha.kneecare.BuildConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.firebase.firestore.FirebaseFirestore; // Thêm thư viện Firebase

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultFragment extends Fragment {

    TextView txtResult;
    ProgressBar progressBar;
    Button btnGoToSchedule;

    private final java.util.concurrent.TimeUnit TimeUnit = java.util.concurrent.TimeUnit.SECONDS;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit)
            .writeTimeout(60, TimeUnit)
            .readTimeout(60, TimeUnit)
            .build();


    private String painLevel = "Nhẹ";
    private boolean unstable = false;
    private boolean runPain = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        txtResult = view.findViewById(R.id.txtResult);
        progressBar = view.findViewById(R.id.progressBar);
        btnGoToSchedule = view.findViewById(R.id.btnGoToSchedule);

        if (btnGoToSchedule != null) {
            btnGoToSchedule.setVisibility(View.GONE); // Ẩn nút lúc đang load
            btnGoToSchedule.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ScheduleFragment())
                            .commit();
                }
            });
        }

        Bundle args = getArguments();
        if (args != null) {
            painLevel = args.getString("painLevel", "Nhẹ");
            unstable = args.getBoolean("unstable", false);
            runPain = args.getBoolean("runPain", false);
        }

        // Prompt ép AI lên lịch chi tiết trong vòng 1 tháng
        String fullPrompt = "Bạn là chuyên gia trợ lý y tế ảo KneeCare. Nhiệm vụ của bạn là phân tích dữ liệu tình trạng đầu gối " +
                "và trả về kết quả cấu trúc cụ thể rõ ràng:\n" +
                "1. Đánh giá chung tình trạng hiện tại.\n" +
                "2. Thiết kế một LỊCH TRÌNH TẬP LUYỆN PHỤC HỒI CHI TIẾT TRONG VÒNG 1 THÁNG (4 TUẦN). Chia rõ mục tiêu cho Tuần 1-2 (Giai đoạn thích nghi) và Tuần 3-4 (Giai đoạn tăng cường), kèm theo các bài tập cụ thể cho từng ngày.\n" +
                "3. Lời khuyên y khoa về tần suất tập và các dấu hiệu cần dừng tập.\n\n" +
                "Thông số hiện tại của bệnh nhân:\n" +
                "- Mức độ đau: " + painLevel + "\n" +
                "- Triệu chứng lỏng khớp: " + (unstable ? "Có bị lỏng khớp" : "Không bị lỏng khớp") + "\n" +
                "- Tăng đau khi vận động chạy bộ: " + (runPain ? "Có tăng đau" : "Không tăng đau") + "\n\n" +
                "Hãy phân tích bằng văn phong y khoa, rõ ràng, gạch đầu dòng mạch lạc.";

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        txtResult.setText("Hệ thống KneeCare đang kết nối Cloud AI để thiết lập phác đồ và lên lịch tập 1 tháng cho bạn...");

        callGeminiAPI(fullPrompt);

        return view;
    }

    private void callGeminiAPI(String promptContent) {
        // Sử dụng API Key sạch của bạn
        String cleanApiKey = BuildConfig.GEMINI_API_KEY;
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + cleanApiKey;

        // --- CẤU TRÚC LẠI JSON CHUẨN ĐỂ TRÁNH LỖI 400 ---
        JsonObject textObject = new JsonObject();
        textObject.addProperty("text", promptContent);

        com.google.gson.JsonArray partsArray = new com.google.gson.JsonArray();
        partsArray.add(textObject);

        JsonObject partsObject = new JsonObject();
        partsObject.add("parts", partsArray);

        com.google.gson.JsonArray contentsArray = new com.google.gson.JsonArray();
        contentsArray.add(partsObject);

        JsonObject rootObject = new JsonObject();
        rootObject.add("contents", contentsArray);

        // Chuyển đối tượng sang chuỗi JSON chuẩn hóa (Tự động xử lý ký tự đặc biệt và xuống dòng)
        String jsonBody = rootObject.toString();

        // Khai báo MediaType chuẩn xác, ép mã hóa UTF-8 để không lỗi font
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

        // Tạo Request với đầy đủ Header định danh ngăn chặn lỗi 403 và 400
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("x-goog-api-client", "genai-android")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    txtResult.setText("Lỗi kết nối mạng: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        String aiReply = jsonObject.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();

                        getActivity().runOnUiThread(() -> {
                            // Kiểm tra an toàn trước khi xử lý giao diện để tránh văng app
                            if (getActivity() == null || isDetached() != false) return;

                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            txtResult.setText(aiReply);

                            if (btnGoToSchedule != null) {
                                btnGoToSchedule.setVisibility(View.VISIBLE);
                            }

                            // Thực hiện lưu dữ liệu cục bộ an toàn
                            saveToLocalStorage(aiReply);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            txtResult.setText("Lỗi xử lý dữ liệu phản hồi: " + e.getMessage());
                        });
                    }
                } else {
                    // Đọc chi tiết phản hồi lỗi từ Google để debug nếu vẫn bị từ chối
                    String errorBody = response.body() != null ? response.body().string() : "Không có chi tiết lỗi";
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        txtResult.setText("Yêu cầu thất bại.\nMã lỗi HTTP: " + response.code() + "\nChi tiết cấu trúc lỗi: " + errorBody);
                    });
                }
            }
        });
    }

    /**
     * Hàm đẩy dữ liệu lên Cloud Firestore Database (Mục số 2 & Lớp Server trên sơ đồ)
     */
    private void syncDataToFirestore(String userId, String aiReply) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Đóng gói dữ liệu chuẩn cấu trúc thực tế
        Map<String, Object> medicalRecord = new HashMap<>();
        medicalRecord.put("userId", userId);
        medicalRecord.put("timestamp", System.currentTimeMillis());
        medicalRecord.put("tflite_prediction", "Mức độ: " + painLevel + " (Cục bộ xử lý)");
        medicalRecord.put("gemini_response", aiReply);
        medicalRecord.put("symptoms_summary", "Đau: " + painLevel + " | Lỏng: " + unstable + " | Chạy: " + runPain);

        // Đẩy lên collection "medical_history" trên Firebase Cloud
        db.collection("medical_history")
                .add(medicalRecord)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Đồng bộ dữ liệu Firebase Server thành công!");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi đồng bộ Firebase: " + e.getMessage());
                });
    }

    /**
     * Hàm lưu trữ cục bộ (Mục số 4 trên sơ đồ)
     */
    private void saveToLocalStorage(String aiReply) {
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("saved_ai_schedule", aiReply);

        String responseTime = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());
        String symptomsSummary = "Mức độ: " + painLevel + (unstable ? " | Lỏng khớp" : "") + (runPain ? " | Đau khi chạy" : "");

        String historyJson = pref.getString("medical_history_list", "[]");
        try {
            com.google.gson.JsonArray jsonArray = JsonParser.parseString(historyJson).getAsJsonArray();
            JsonObject newRecord = new JsonObject();
            newRecord.addProperty("date", responseTime);
            newRecord.addProperty("symptoms", symptomsSummary);
            newRecord.addProperty("aiResult", aiReply);

            com.google.gson.JsonArray updatedArray = new com.google.gson.JsonArray();
            updatedArray.add(newRecord);
            updatedArray.addAll(jsonArray);

            editor.putString("medical_history_list", updatedArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.apply();
    }
}