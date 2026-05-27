package cntt2.levietha.kneecare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryModel> historyList;

    public HistoryAdapter(List<HistoryModel> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int viewType) {
        HistoryModel model = historyList.get(viewType);
        holder.txtDate.setText("📅 " + model.getDate());
        holder.txtSymptoms.setText("🩺 Triệu chứng: " + model.getSymptoms());
        holder.txtResult.setText("📝 AI khuyên: " + model.getAiResult());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtSymptoms, txtResult;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.itemTxtDate);
            txtSymptoms = itemView.findViewById(R.id.itemTxtSymptoms);
            txtResult = itemView.findViewById(R.id.itemTxtResult);
        }
    }
}