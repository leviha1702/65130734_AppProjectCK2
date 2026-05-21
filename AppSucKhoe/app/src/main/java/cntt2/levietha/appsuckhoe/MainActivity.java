package cntt2.levietha.appsuckhoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView btnInput = findViewById(R.id.btnInput);
        TextView btnExercise = findViewById(R.id.btnExercise);
        TextView btnHistory = findViewById(R.id.btnHistory);
        TextView btnExit = findViewById(R.id.btnExit);

        btnInput.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InputActivity.class);
            startActivity(intent);
        });
    }
}