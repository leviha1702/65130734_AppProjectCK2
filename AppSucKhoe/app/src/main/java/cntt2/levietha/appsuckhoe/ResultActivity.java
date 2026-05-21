package cntt2.levietha.appsuckhoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView txtResult = findViewById(R.id.txtResult);

        Intent intent = getIntent();

        String painLevel = intent.getStringExtra("painLevel");
        boolean unstable = intent.getBooleanExtra("unstable", false);
        boolean runPain = intent.getBooleanExtra("runPain", false);

    }
}
