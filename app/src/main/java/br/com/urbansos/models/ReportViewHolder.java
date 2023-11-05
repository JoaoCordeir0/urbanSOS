package br.com.urbansos.models;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.urbansos.R;

public class ReportViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView description;
    TextView date;

    public ReportViewHolder(View view)
    {
        super(view);
        title = view.findViewById(R.id.report_title);
        description = view.findViewById(R.id.report_description);
        date = view.findViewById(R.id.report_date);
    }
}
