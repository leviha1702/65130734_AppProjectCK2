package cntt2.levietha.kneecare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.IOException;

public class ChatbotFragment extends Fragment {
    EditText edtChatInput;
    Button btnChatSend;
    TextView txtChatLog;
    OkHttpClient client = new OkHttpClient();
    String API_KEY = "AIzaSyBYeRDYKM9NeMK4UVfinIPI7OU-dVw7Qx8";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chatbot, container, false);
        edtChatInput = v.findViewById(R.id.edtChatInput);
        btnChatSend = v.findViewById(R.id.btnChatSend);
        txtChatLog = v.findViewById(R.id.txtChatLog);

        btnChatSend.setOnClickListener(view -> {
            String question = edtChatInput.getText().toString().trim();
            if (!question.isEmpty()) {
                txtChatLog.append("Bạn: " + question + "\n\n");
                edtChatInput.setText("");
                askGemini(question);
            }
        });
        return v;
    }

    private void askGemini(String q) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;
        String json = "{\"contents\":[{\"parts\":[{\"text\":\"Bạn là chuyên gia y tế khớp gối. Trả lời ngắn gọn câu hỏi: " + q + "\"}]}]}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(res).getAsJsonObject();
                    String botReply = jsonObject.getAsJsonArray("candidates").get(0).getAsJsonObject()
                            .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString();

                    getActivity().runOnUiThread(() -> txtChatLog.append("Bot: " + botReply + "\n\n"));
                }
            }
        });
    }
}