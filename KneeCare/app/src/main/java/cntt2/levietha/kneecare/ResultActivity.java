package cntt2.levietha.kneecare;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtResult = findViewById(R.id.txtResult);

        // nhận dữ liệu
        String painLevel = getIntent().getStringExtra("painLevel");
        boolean unstable = getIntent().getBooleanExtra("unstable", false);
        boolean runPain = getIntent().getBooleanExtra("runPain", false);

        // xử lý đơn giản
        String result = "Kết quả:\n";

        if (painLevel.equals("Nhẹ")) {
            result += "- Tập nhẹ\n";
        } else if (painLevel.equals("Trung bình")) {
            result += "- Tập phục hồi\n";
        } else {
            result += "- Nghỉ ngơi\n";
        }

        if (unstable) result += "- Tăng cơ đùi\n";
        if (runPain) result += "- Hạn chế chạy\n";

        txtResult.setText(result);
    }
}
