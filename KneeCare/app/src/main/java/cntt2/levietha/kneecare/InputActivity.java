package cntt2.levietha.kneecare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class InputActivity extends AppCompatActivity {

    Spinner spinnerPain;
    CheckBox cbUnstable, cbRunPain;
    Button btnAnalyze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // ánh xạ
        spinnerPain = findViewById(R.id.spinnerPain);
        cbUnstable = findViewById(R.id.cbUnstable);
        cbRunPain = findViewById(R.id.cbRunPain);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        // set dữ liệu spinner
        String[] levels = {"Nhẹ", "Trung bình", "Nặng"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                levels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPain.setAdapter(adapter);

        // xử lý nút
        btnAnalyze.setOnClickListener(v -> {

            String painLevel = spinnerPain.getSelectedItem().toString();
            boolean unstable = cbUnstable.isChecked();
            boolean runPain = cbRunPain.isChecked();

            Intent intent = new Intent(InputActivity.this, ResultActivity.class);

            intent.putExtra("painLevel", painLevel);
            intent.putExtra("unstable", unstable);
            intent.putExtra("runPain", runPain);

            startActivity(intent);
        });
    }
}
