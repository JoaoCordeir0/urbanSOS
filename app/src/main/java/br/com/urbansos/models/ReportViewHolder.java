package br.com.urbansos.models;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import br.com.urbansos.R;

public class ReportViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView status;
    TextView date;
    MaterialCardView card;

    public ReportViewHolder(View view)
    {
        super(view);
        title = view.findViewById(R.id.report_title);
        status = view.findViewById(R.id.report_status);
        date = view.findViewById(R.id.report_date);
        card = view.findViewById(R.id.card_report);
    }
}
