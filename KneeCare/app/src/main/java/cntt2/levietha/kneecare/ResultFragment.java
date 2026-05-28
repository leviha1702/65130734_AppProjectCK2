package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // Đảm bảo dùng đúng thư viện AndroidX

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
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

    // Cấu hình OkHttpClient tối ưu thời gian chờ Timeout 60s cho Fragment
    private final java.util.concurrent.TimeUnit TimeUnit = java.util.concurrent.TimeUnit.SECONDS;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit)
            .writeTimeout(60, TimeUnit)
            .readTimeout(60, TimeUnit)
            .build();

    // API Key của bạn (Nên giữ API Key sạch này)
    private static final String GEMINI_API_KEY = "AIzaSyBYeRDYKM9NeMK4UVfinIPI7OU-dVw7Qx8";

    // Khai báo biến lưu thông số triệu chứng để dùng khi lưu lịch sử
    private String painLevel = "Nhẹ";
    private boolean unstable = false;
    private boolean runPain = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Nạp giao diện XML fragment_result
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        // 2. Ánh xạ giao diện thông qua đối tượng view
        txtResult = view.findViewById(R.id.txtResult);
        progressBar = view.findViewById(R.id.progressBar);

        // 3. Đọc dữ liệu triệu chứng (Được truyền từ CheckFragment qua Bundle thay vì Intent)
        Bundle args = getArguments();
        if (args != null) {
            painLevel = args.getString("painLevel", "Nhẹ");
            unstable = args.getBoolean("unstable", false);
            runPain = args.getBoolean("runPain", false);
        }

        // 4. Thiết lập Prompt gửi cho AI
        String fullPrompt = "Bạn là chuyên gia trợ lý y tế ảo KneeCare. Nhiệm vụ của bạn là phân tích dữ liệu tình trạng đầu gối " +
                "và trả về kết quả cấu trúc cụ thể: 1. Đánh giá chung tình trạng; 2. Liệt kê cụ thể danh mục bài tập phục hồi phù hợp; " +
                "3. Lập một Lịch trình tập luyện (Lịch tập cụ thể từng ngày trong tuần).\n\n" +
                "Thông số hiện tại của bệnh nhân:\n" +
                "- Mức độ đau: " + painLevel + "\n" +
                "- Triệu chứng lỏng khớp: " + (unstable ? "Có bị lỏng khớp" : "Không bị lỏng khớp") + "\n" +
                "- Tăng đau khi vận động chạy bộ: " + (runPain ? "Có tăng đau" : "Không tăng đau") + "\n\n" +
                "Hãy phân tích bằng văn phong y khoa, phân cấp rõ ràng bằng các dấu gạch đầu dòng.";

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        txtResult.setText("Hệ thống KneeCare đang kết nối trực tiếp Cloud AI để phân tích và lên lịch tập vật lý trị liệu cho bạn...");

        // 5. Triển khai gọi kết nối AI
        callGeminiAPI(fullPrompt);

        return view;
    }

    private void callGeminiAPI(String promptContent) {
        // SỬA LỖI 404/429: Chuyển sang endpoint v1beta và chạy dòng mô hình 1.5-flash chuẩn y khoa, miễn phí ổn định
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + GEMINI_API_KEY;

        JsonObject textObject = new JsonObject();
        textObject.addProperty("text", promptContent);

        JsonObject partsObject = new JsonObject();
        com.google.gson.JsonArray partsArray = new com.google.gson.JsonArray();
        partsArray.add(textObject);
        partsObject.add("parts", partsArray);

        com.google.gson.JsonArray contentsArray = new com.google.gson.JsonArray();
        contentsArray.add(partsObject);

        JsonObject rootObject = new JsonObject();
        rootObject.add("contents", contentsArray);

        String jsonBody = rootObject.toString();
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
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

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        String aiReply = jsonObject.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();

                        // Đổ dữ liệu lên màn hình và kích hoạt lưu trữ
                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            txtResult.setText(aiReply);

                            // ĐỒNG BỘ: Tự động lưu lịch tập và lưu vào danh sách Lịch sử
                            saveToSystemStorage(aiReply);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            txtResult.setText("Lỗi cấu trúc dữ liệu: " + e.getMessage());
                        });
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);

                        if (response.code() == 429) {
                            txtResult.setText("Hạn mức API Key hiện tại đã cạn kiệt.\n\nVui lòng thay một API Key sạch khác từ Google AI Studio.");
                        } else {
                            txtResult.setText("Google AI từ chối yêu cầu.\nMã lỗi HTTP: " + response.code() + "\nChi tiết: " + errorBody);
                        }
                    });
                }
            }
        });
    }

    /**
     * Hàm tự động đóng gói lưu trữ lịch tập hiện tại và lưu lịch sử khám bệnh
     */
    private void saveToSystemStorage(String aiReply) {
        if (getActivity() == null) return;

        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // 1. Lưu đè lịch tập mới nhất phục vụ cho tab ScheduleFragment
        editor.putString("saved_ai_schedule", aiReply);

        // 2. Đóng gói lưu vào danh sách lịch sử (HistoryFragment)
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