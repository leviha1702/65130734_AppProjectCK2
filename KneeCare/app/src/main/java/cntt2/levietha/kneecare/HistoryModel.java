package cntt2.levietha.kneecare;

public class HistoryModel {
    private String date;
    private String symptoms;
    private String aiResult;

    public HistoryModel(String date, String symptoms, String aiResult) {
        this.date = date;
        this.symptoms = symptoms;
        this.aiResult = aiResult;
    }

    public String getDate() { return date; }
    public String getSymptoms() { return symptoms; }
    public String getAiResult() { return aiResult; }
}