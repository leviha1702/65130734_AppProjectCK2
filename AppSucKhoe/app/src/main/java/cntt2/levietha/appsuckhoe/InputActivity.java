package cntt2.levietha.appsuckhoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class InputActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Spinner spinnerPain = findViewById(R.id.spinnerPain);
        CheckBox cbUnstable = findViewById(R.id.cbUnstable);
        CheckBox cbRunPain = findViewById(R.id.cbRunPain);
        Button btnAnalyze = findViewById(R.id.btnAnalyze);

        btnAnalyze.setOnClickListener(v -> {

            String painLevel = spinnerPain.getSelectedItem().toString();

            boolean unstable = cbUnstable.isChecked();
            boolean runPain = cbRunPain.isChecked();

            // Gửi dữ liệu sang màn Result
            Intent intent = new Intent(InputActivity.this, ResultActivity.class);

            intent.putExtra("painLevel", painLevel);
            intent.putExtra("unstable", unstable);
            intent.putExtra("runPain", runPain);

            startActivity(intent);
        });
    }
}
