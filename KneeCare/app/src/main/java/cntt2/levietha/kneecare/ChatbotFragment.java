package cntt2.levietha.kneecare;

import android.graphics.Color;
import android.os.Bundle;
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

    // API Key Gemini ổn định của bạn
    private static final String GEMINI_API_KEY = "AIzaSyBYeRDYKM9NeMK4UVfinIPI7OU-dVw7Qx8";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        // Ánh xạ giao diện
        layoutChatContainer = view.findViewById(R.id.layoutChatContainer);
        edtChatInput = view.findViewById(R.id.edtChatInput);
        btnChatSend = view.findViewById(R.id.btnChatSend);
        scrollChat = view.findViewById(R.id.scrollChat);

        // Thêm câu chào mặc định ban đầu của Bot (Nằm bên TRÁI)
        addChatBubble("Xin chào! Tôi là chuyên gia trợ lý y tế ảo KneeCare. Bạn đang gặp vấn đề gì ở khớp gối hoặc cần tôi tư vấn bài tập nào không?", false);

        // Bắt sự kiện gửi tin nhắn
        btnChatSend.setOnClickListener(v -> {
            String userText = edtChatInput.getText().toString().trim();
            if (userText.isEmpty()) return;

            // 1. Hiển thị tin nhắn của User lên màn hình (Căn PHẢI)
            addChatBubble(userText, true);
            edtChatInput.setText(""); // Xóa trống ô nhập

            // 2. Hiển thị bong bóng chờ của Bot (Căn TRÁI)
            addChatBubble("KneeCare Bot đang suy nghĩ...", false);

            // 3. Tiến hành gửi dữ liệu lên Cloud AI
            sendMessageToGemini(userText);
        });

        return view;
    }

    /**
     * Hàm tự động vẽ Bong bóng chat (Chat Bubble) chuẩn thực tế
     * @param message Nội dung chữ
     * @param isUser true nếu là Người dùng (Căn phải), false nếu là Bot AI (Căn trái)
     */
    private void addChatBubble(String message, boolean isUser) {
        if (getActivity() == null) return;

        // Tạo một TextView mới làm bong bóng
        TextView bubble = new TextView(getActivity());
        bubble.setText(message);
        bubble.setTextSize(15);
        bubble.setTextColor(Color.BLACK);
        bubble.setPadding(30, 20, 30, 20);

        // Thiết lập kích thước và lề (Margin) cho bong bóng
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(12, 12, 12, 12);

        if (isUser) {
            // Người dùng: Đẩy sang PHẢI, nền xanh Indigo nhạt nhã nhặn
            params.gravity = Gravity.END;
            bubble.setBackground(getActivity().getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
            bubble.getBackground().setTint(Color.parseColor("#C5CAE9")); // Màu Indigo nhạt thanh lịch
        } else {
            // Trợ lý Bot: Đẩy sang TRÁI, nền xám trắng
            params.gravity = Gravity.START;
            bubble.setBackground(getActivity().getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
            bubble.getBackground().setTint(Color.parseColor("#E0E0E0")); // Màu xám trắng sạch sẽ
        }

        bubble.setLayoutParams(params);

        // Đổ vào khung chứa layout lớn
        getActivity().runOnUiThread(() -> {
            // Nếu tin nhắn trước đó là trạng thái đang xử lý, xóa nó đi trước khi nạp kết quả thật
            if (!isUser && layoutChatContainer.getChildCount() > 1) {
                View lastView = layoutChatContainer.getChildAt(layoutChatContainer.getChildCount() - 1);
                if (lastView instanceof TextView && ((TextView) lastView).getText().toString().equals("KneeCare Bot đang suy nghĩ...")) {
                    layoutChatContainer.removeViewAt(layoutChatContainer.getChildCount() - 1);
                }
            }

            layoutChatContainer.addView(bubble);
            // Tự động cuộn xuống đáy khi có tin nhắn mới
            scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
        });
    }

    private void sendMessageToGemini(String userPrompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

        // Tạo cấu trúc prompt chuyên sâu ép AI đóng vai chuyên gia y tế
        String systemContext = "Bạn là trợ lý y tế ảo chuyên sâu về khớp gối KneeCare. Hãy trả lời câu hỏi sau một cách ngắn gọn, chuẩn y khoa và dễ hiểu: " + userPrompt;

        JsonObject textObject = new JsonObject();
        textObject.addProperty("text", systemContext);

        JsonObject partsObject = new JsonObject();
        com.google.gson.JsonArray partsArray = new com.google.gson.JsonArray();
        partsArray.add(textObject);
        partsObject.add("parts", partsArray);

        com.google.gson.JsonArray contentsArray = new com.google.gson.JsonArray();
        contentsArray.add(partsObject);

        JsonObject rootObject = new JsonObject();
        rootObject.add("contents", contentsArray);

        RequestBody body = RequestBody.create(rootObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                addChatBubble("Lỗi kết nối: " + e.getMessage(), false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonObject = JsonParser.parseString(response.body().string()).getAsJsonObject();
                        String aiReply = jsonObject.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("content")
                                .getAsJsonArray("parts")
                                .get(0).getAsJsonObject()
                                .get("text").getAsString();

                        // Hiển thị câu trả lời xịn từ Gemini (Căn trái)
                        addChatBubble(aiReply, false);
                    } catch (Exception e) {
                        addChatBubble("Lỗi xử lý dữ liệu: " + e.getMessage(), false);
                    }
                } else {
                    addChatBubble("Hệ thống quá tải hoặc API Key gặp sự cố. Mã lỗi: " + response.code(), false);
                }
            }
        });
    }
}