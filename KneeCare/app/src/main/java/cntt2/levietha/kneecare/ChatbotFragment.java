package cntt2.levietha.kneecare;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cntt2.levietha.kneecare.BuildConfig;

import com.google.gson.JsonArray;
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

public class ChatbotFragment extends Fragment {

    private LinearLayout layoutChatContainer;
    private EditText edtChatInput;
    private Button btnChatSend;
    private ScrollView scrollChat;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private static final String cleanApiKey = BuildConfig.GEMINI_API_KEY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        layoutChatContainer = view.findViewById(R.id.layoutChatContainer);
        edtChatInput = view.findViewById(R.id.edtChatInput);
        btnChatSend = view.findViewById(R.id.btnChatSend);
        scrollChat = view.findViewById(R.id.scrollChat);

        addChatBubble("Xin chào! Tôi là chuyên gia trợ lý y tế ảo KneeCare. Bạn đang gặp vấn đề gì ở khớp gối hoặc cần tôi tư vấn bài tập nào không?", false);

        // Bắt sự kiện gửi tin nhắn (Tìm đoạn code này trong onCreateView và thay thế)
        btnChatSend.setOnClickListener(v -> {
            String userText = edtChatInput.getText().toString().trim();
            if (userText.isEmpty()) return;

            // 1. Hiển thị tin nhắn của User lên màn hình (Căn PHẢI)
            addChatBubble(userText, true);
            edtChatInput.setText(""); // Xóa trống ô nhập

            // 2. Hiển thị bong bóng chờ của Bot (Căn TRÁI)
            addChatBubble("KneeCare Bot đang suy nghĩ...", false);

            // 3. 🔥 GỌI ĐÚNG HÀM CÓ SẴN TRONG CODE CỦA BẠN ĐỂ GỬI ĐI
            sendMessageToGemini(userText);

            // 4. 🔥 KÍCH HOẠT BỘ ĐẾM NGƯỢC CHỐNG SPAM LỖI 429
            btnChatSend.setEnabled(false); // Khóa nút Gửi ngay lập tức

            // Đếm ngược 5 giây (5000 mili-giây), mỗi bước giảm 1 giây (1000 mili-giây)
            new android.os.CountDownTimer(5000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Hiển thị số giây đếm ngược lên nút bấm
                    btnChatSend.setText("" + (millisUntilFinished / 1000) + "s");
                }

                @Override
                public void onFinish() {
                    // Hết 5 giây, khôi phục lại nút bấm về trạng thái ban đầu
                    btnChatSend.setEnabled(true);
                    btnChatSend.setText("GỬI");
                }
            }.start();
        });

        return view;
    }

    private void addChatBubble(String message, boolean isUser) {
        if (getActivity() == null) return;

        TextView bubble = new TextView(getActivity());
        bubble.setText(message);
        bubble.setTextSize(15);
        bubble.setTextColor(Color.BLACK);
        bubble.setPadding(30, 20, 30, 20);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(12, 12, 12, 12);

        if (isUser) {
            params.gravity = Gravity.END;
            bubble.setBackground(getActivity().getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
            bubble.getBackground().setTint(Color.parseColor("#C5CAE9"));
        } else {
            params.gravity = Gravity.START;
            bubble.setBackground(getActivity().getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
            bubble.getBackground().setTint(Color.parseColor("#E0E0E0"));
        }

        bubble.setLayoutParams(params);

        getActivity().runOnUiThread(() -> {
            if (!isUser && layoutChatContainer.getChildCount() > 0) {
                View lastView = layoutChatContainer.getChildAt(layoutChatContainer.getChildCount() - 1);
                if (lastView instanceof TextView && ((TextView) lastView).getText().toString().equals("KneeCare Bot đang suy nghĩ...")) {
                    layoutChatContainer.removeViewAt(layoutChatContainer.getChildCount() - 1);
                }
            }

            layoutChatContainer.addView(bubble);
            scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
        });
    }

    private void sendMessageToGemini(String userPrompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + cleanApiKey;

        // Tối ưu prompt để bóc tách thông tin lâm sàng theo đúng mô tả chức năng của đồ án
        String systemContext = "Bạn là trợ lý y tế ảo chuyên sâu về khớp gối KneeCare.\n" +
                "Nhiệm vụ của bạn:\n" +
                "1. Trả lời câu hỏi ngắn gọn, chuẩn y khoa.\n" +
                "2. Luôn trích xuất thông tin nhật ký ở cuối câu theo mẫu sau:\n" +
                "🤖 [AI TRÍCH XUẤT THÔNG TIN NHẬT KÝ]:\n" +
                "- Triệu chứng:\n" +
                "- Vị trí khớp gối:\n" +
                "- Ngữ cảnh diễn ra:\n\n" +
                "Câu hỏi từ người dùng: " + userPrompt;

        // Đóng gói JSON chuẩn cấu trúc API của Google Gemini
        JsonObject textObject = new JsonObject();
        textObject.addProperty("text", systemContext);

        JsonArray partsArray = new JsonArray();
        partsArray.add(textObject);

        JsonObject partsObject = new JsonObject();
        partsObject.add("parts", partsArray);

        JsonArray contentsArray = new JsonArray();
        contentsArray.add(partsObject);

        JsonObject rootObject = new JsonObject();
        rootObject.add("contents", contentsArray);

        RequestBody body = RequestBody.create(rootObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 🔥 ĐÃ SỬA: Bọc trong runOnUiThread để tránh crash app khi mất mạng
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addChatBubble("Lỗi kết nối: " + e.getMessage(), false));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() == null) return;

                // Đọc phản hồi từ server 1 lần duy nhất để tránh crash ứng dụng
                String responseBody = "";
                if (response.body() != null) {
                    responseBody = response.body().string();
                }

                if (response.isSuccessful() && !responseBody.isEmpty()) {
                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        String aiReply = jsonObject.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();

                        final String finalReply = aiReply;
                        getActivity().runOnUiThread(() -> {
                            // Thêm câu trả lời của AI vào giao diện bong bóng chat
                            addChatBubble(finalReply, false);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> addChatBubble("Lỗi xử lý dữ liệu: " + e.getMessage(), false));
                    }
                } else {
                    // 🔥 KHẮC PHỤC LỖI 503: CƠ CHẾ CHATBOT NGOẠI TUYẾN THÔNG MINH
                    if (response.code() == 503 || response.code() == 403 || response.code() == 400) {

                        getActivity().runOnUiThread(() -> {
                            String offlineBotReply = "🤖 [Trợ Lý KneeCare - Chế độ ngoại tuyến]:\n\n" +
                                    "Hiện tại kết nối tới Máy chủ Cloud AI đang bị gián đoạn (Mã lỗi: " + response.code() + "). " +
                                    "Tuy nhiên, bạn đừng lo lắng! Để bảo vệ và chăm sóc đầu gối ngay lúc này, bạn hãy lưu ý các nguyên tắc y khoa cốt lõi sau:\n\n" +
                                    "1. Nếu gối đang chấn thương cấp tính (sưng, nóng, đỏ, đau): Áp dụng ngay giải pháp R.I.C.E (Nghỉ ngơi ➔ Chườm lạnh 15 phút ➔ Băng ép nhẹ ➔ Kê cao chân).\n" +
                                    "2. Tuyệt đối không tự ý bẻ khớp, vặn khớp hoặc nhờ người kéo nắn khi chưa rõ tổn thương.\n" +
                                    "3. Bạn có thể vào mục 'Phác đồ 1 tháng' trong ứng dụng để xem lịch trình tập luyện phục hồi cơ đùi cơ bản đã được tối ưu hóa sẵn.\n\n" +
                                    "👉 Vui lòng thử nhắn lại khi có kết nối mạng ổn định hơn hoặc chuyển sang sử dụng mạng 4G/VPN để mở lại kết nối Cloud AI thực tế nhé!";

                            // Hiển thị nội dung cứu nguy này lên màn hình chat thay vì báo lỗi hệ thống
                            addChatBubble(offlineBotReply, false);

                            Toast.makeText(getActivity(), "⚙️ Đã chuyển đổi Chatbot sang chế độ phản hồi an toàn ngoại tuyến!", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        // Hiển thị lỗi hệ thống khác nếu không phải lỗi nghẽn server 503
                        final String rawError = "Yêu cầu thất bại. Mã lỗi: " + response.code();
                        getActivity().runOnUiThread(() -> addChatBubble(rawError, false));
                    }
                }
            }
        });
    }
}