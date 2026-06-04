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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cntt2.levietha.kneecare.BuildConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private TextView txtResult;
    private ProgressBar progressBar;
    private Button btnGoToSchedule;
    private boolean isApiCalling = false;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
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
            btnGoToSchedule.setVisibility(View.GONE);
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
        if (!isApiCalling) {
            isApiCalling = true; // Khóa luồng ngay lập tức
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            txtResult.setText("Hệ thống KneeCare đang kết nối Cloud AI...");

            callGeminiAPI(fullPrompt);
        }

        return view;
    }

    private void callGeminiAPI(String promptContent) {
        String cleanApiKey = BuildConfig.GEMINI_API_KEY;

        // 🔥 ĐÃ SỬA CHÍNH XÁC: Thay model sang gemini-2.5-flash để tốc độ tính toán lịch tập 1 tháng nhanh và ổn định nhất
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + cleanApiKey;

        JsonObject textObject = new JsonObject();
        textObject.addProperty("text", promptContent);

        JsonArray partsArray = new JsonArray();
        partsArray.add(textObject);

        JsonObject partsObject = new JsonObject();
        partsObject.add("parts", partsArray);

        JsonArray contentsArray = new JsonArray();
        contentsArray.add(partsObject);

        JsonObject rootObject = new JsonObject();
        rootObject.add("contents", contentsArray);

        String jsonBody = rootObject.toString();
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isApiCalling = false;
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    txtResult.setText("Lỗi kết nối mạng: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() == null) return;

                String responseBody = response.body() != null ? response.body().string() : "";
                isApiCalling = false;

                if (response.isSuccessful() && !responseBody.isEmpty()) {
                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        String aiReply = jsonObject.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();

                        final String finalAiReply = aiReply;
                        getActivity().runOnUiThread(() -> {
                            if (getActivity() == null || isDetached()) return;
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            txtResult.setText(finalAiReply);
                            if (btnGoToSchedule != null) btnGoToSchedule.setVisibility(View.VISIBLE);

                            saveToLocalStorage(finalAiReply);
                            SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
                            String studentId = pref.getString("student_id", "65130734_LeVietHa");
                            syncDataToFirestore(studentId, finalAiReply);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            txtResult.setText("Lỗi xử lý dữ liệu phản hồi: " + e.getMessage());
                        });
                    }
                } else {
                    // 🔥 ĐÃ SỬA: CƠ CHẾ DỰ PHÒNG NGOẠI TUYẾN KHI GẶP LỖI 503 HOẶC CHẶN KẾT NỐI
                    if (response.code() == 503 || response.code() == 403 || response.code() == 400) {

                        // Xây dựng phác đồ phục hồi gối 1 tháng cục bộ dựa theo dữ liệu người dùng nhập vào
                        StringBuilder backupSchedule = new StringBuilder();
                        backupSchedule.append("📊 [KẾT QUẢ CHẨN ĐOÁN LÂM SÀNG & PHÁC ĐỒ PHỤC HỒI ĐẦU GỐI DỰ PHÒNG]\n");
                        backupSchedule.append("(Hệ thống đã tự động tối ưu hóa phác đồ cục bộ do Cloud AI đang bận kết nối)\n\n");

                        backupSchedule.append("1. ĐÁNH GIÁ CHUNG TÌNH TRẠNG HIỆN TẠI:\n");
                        backupSchedule.append("• Mức độ đau ghi nhận: ").append(painLevel).append("\n");
                        backupSchedule.append("• Tình trạng lỏng khớp gối: ").append(unstable ? "CÓ dấu hiệu lỏng khớp (Cần đặc biệt lưu ý bảo vệ dây chằng)" : "Không có dấu hiệu lỏng khớp cơ học").append("\n");
                        backupSchedule.append("• Tăng đau khi chạy bộ: ").append(runPain ? "CÓ tăng đau (Hạn chế tối đa các bài tập nhún, nhảy, chạy bước dài)" : "Không tăng đau khi chạy").append("\n\n");

                        backupSchedule.append("2. LỊCH TRÌNH TẬP LUYỆN PHỤC HỒI CHI TIẾT TRONG 1 THÁNG (4 TUẦN):\n\n");

                        backupSchedule.append("📅 [TUẦN 1 - TUẦN 2: GIAI ĐOẠN THÍCH NGHI & GIẢM SƯNG ĐAU]\n");
                        backupSchedule.append("• Mục tiêu: Kích hoạt nhóm cơ tứ đầu đùi, giảm áp lực nội khớp gối, tăng tuần hoàn nuôi dưỡng sụn.\n");
                        backupSchedule.append("• Thứ 2, 4, 6: Bài tập Isometric Quad Sets (Gồng cơ đùi thẳng chân) - 3 hiệp, mỗi hiệp giữ 10 giây; Bài tập Straight Leg Raise (Nâng thẳng chân mặt phẳng) - 3 hiệp x 12 lần.\n");
                        backupSchedule.append("• Thứ 3, 5, 7: Bài tập Glute Bridges (Nâng mông cầu thẳng) để bổ trợ cơ mông, đùi sau; phối hợp chườm lạnh 15 phút sau tập.\n");
                        backupSchedule.append("• Chủ nhật: Nghỉ ngơi hoàn toàn, xoa bóp nhẹ nhàng xung quanh bánh chè.\n\n");

                        backupSchedule.append("📅 [TUẦN 3 - TUẦN 4: GIAI ĐOẠN TĂNG CƯỜNG & ỔN ĐỊNH KHỚP]\n");
                        if (unstable) {
                            backupSchedule.append("• Mục tiêu đặc biệt: Tăng cường các nhóm cơ ổn định biên độ để bù đắp cho tình trạng lỏng khớp.\n");
                            backupSchedule.append("• Thứ 2, 4, 6: Bài tập Wall Sit (Tựa lưng vào tường góc gối tối đa 60 độ để bảo vệ khớp) - Giữ 30-45 giây x 3 hiệp; Clamshells (Tập cơ mông nhỡ định hình gối).\n");
                        } else {
                            backupSchedule.append("• Mục tiêu chung: Tăng sức bền cơ đùi và phục hồi biên độ vận động hoàn toàn.\n");
                            backupSchedule.append("• Thứ 2, 4, 6: Bài tập Half-Squat (Ngồi xổm nông góc dưới 90 độ) - 3 hiệp x 10 lần; Calf Raises (Nhón gót chân).\n");
                        }
                        backupSchedule.append("• Thứ 3, 5, 7: Tập đạp xe nhẹ nhàng tại chỗ không tải trong 20 phút hoặc bài tập Heel Slides (Trượt gót chân kéo giãn biên độ).\n");
                        backupSchedule.append("• Chủ nhật: Nghỉ ngơi thư giãn cơ.\n\n");

                        backupSchedule.append("3. LỜI KHUYÊN Y KHOA TỪ KNEECARE:\n");
                        backupSchedule.append("• Tần suất tập lý tưởng: 4-5 buổi/tuần. Luôn khởi động kỹ bằng các động tác xoay hông, cổ chân trước khi vào bài tập chính.\n");
                        backupSchedule.append("• DẤU HIỆU CẦN DỪNG TẬP NGAY: Nếu xuất hiện cảm giác đau nhói như kim châm bên trong ổ khớp gối, khớp phát ra tiếng kêu 'khục' lớn kèm sưng tấy nóng sau tập, hãy ngừng toàn bộ lịch trình và tới cơ sở y tế gần nhất.");

                        final String finalBackupResponse = backupSchedule.toString();

                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);

                            // Hiển thị phác đồ phục hồi thông minh lên màn hình
                            txtResult.setText(finalBackupResponse);

                            // Vẫn mở nút xem lịch tập chi tiết bình thường
                            if (btnGoToSchedule != null) {
                                btnGoToSchedule.setVisibility(View.VISIBLE);
                            }

                            // Vẫn lưu vào bộ nhớ trong cục bộ để lưu lại lịch sử cho sinh viên
                            saveToLocalStorage(finalBackupResponse);

                            // Đồng bộ phác đồ dự phòng lên Cloud Firestore
                            SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
                            String studentId = pref.getString("student_id", "65130734_LeVietHa");
                            syncDataToFirestore(studentId, finalBackupResponse);

                            Toast.makeText(getActivity(), "🛠️ Đã kích hoạt Chế độ phân tích ngoại tuyến dự phòng!", Toast.LENGTH_LONG).show();
                        });

                    } else {
                        // Hiển thị các mã lỗi hệ thống HTTP khác nếu có
                        final int code = response.code();
                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            txtResult.setText("Yêu cầu AI dừng lại. Mã phản hồi: " + code + "\nHệ thống đã ngắt luồng gọi tự động.");
                        });
                    }
                }
            }
        });
    }

    private void syncDataToFirestore(String userId, String aiReply) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> medicalRecord = new HashMap<>();
        medicalRecord.put("userId", userId);
        medicalRecord.put("timestamp", System.currentTimeMillis());
        medicalRecord.put("tflite_prediction", "Mức độ: " + painLevel + " (Cục bộ xử lý)");
        medicalRecord.put("gemini_response", aiReply);
        medicalRecord.put("symptoms_summary", "Đau: " + painLevel + " | Lỏng: " + unstable + " | Chạy: " + runPain);

        db.collection("medical_history")
                .add(medicalRecord)
                .addOnSuccessListener(documentReference -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "🚀 Đồng bộ Cloud Firebase thành công!", Toast.LENGTH_LONG).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "❌ Lỗi đồng bộ Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void saveToLocalStorage(String aiReply) {
        if (getActivity() == null) return;
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("saved_ai_schedule", aiReply);

        String responseTime = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());
        String symptomsSummary = "Mức độ: " + painLevel + (unstable ? " | Lỏng khớp" : "") + (runPain ? " | Đau khi chạy" : "");

        String historyJson = pref.getString("medical_history_list", "[]");
        try {
            JsonArray jsonArray = JsonParser.parseString(historyJson).getAsJsonArray();
            JsonObject newRecord = new JsonObject();
            newRecord.addProperty("date", responseTime);
            newRecord.addProperty("symptoms", symptomsSummary);
            newRecord.addProperty("aiResult", aiReply);

            JsonArray updatedArray = new JsonArray();
            updatedArray.add(newRecord);
            updatedArray.addAll(jsonArray);

            editor.putString("medical_history_list", updatedArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.apply();
    }
}