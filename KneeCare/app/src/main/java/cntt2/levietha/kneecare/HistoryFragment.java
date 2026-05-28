package cntt2.levietha.kneecare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    RecyclerView rvHistory;
    TextView txtNoHistory;
    List<HistoryModel> historyList;
    HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        txtNoHistory = view.findViewById(R.id.txtNoHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyList = new ArrayList<>();

        // Gọi hàm load dữ liệu từ SharedPreferences
        loadMedicalHistory();

        return view;
    }

    private void loadMedicalHistory() {
        SharedPreferences pref = getActivity().getSharedPreferences("KneeCareData", Context.MODE_PRIVATE);
        String historyJson = pref.getString("medical_history_list", "[]");

        try {
            JsonArray jsonArray = JsonParser.parseString(historyJson).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                historyList.add(new HistoryModel(
                        obj.get("date").getAsString(),
                        obj.get("symptoms").getAsString(),
                        obj.get("aiResult").getAsString()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (historyList.isEmpty()) {
            txtNoHistory.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            txtNoHistory.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            adapter = new HistoryAdapter(historyList);
            rvHistory.setAdapter(adapter);
        }
    }
}