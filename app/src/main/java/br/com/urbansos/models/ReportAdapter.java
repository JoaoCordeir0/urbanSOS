package br.com.urbansos.models;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.urbansos.R;

public class ReportAdapter extends RecyclerView.Adapter<ReportViewHolder> {
    private Context context;
    private ArrayList<Report> itens;

    public ReportAdapter(Context context, ArrayList<Report> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.reports_list, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position)
    {
        Report report = itens.get(position);

        // Seta a cor do título do card com base no status
        switch(report.getStatus())
        {
            case "Opened":
                holder.title.setTextColor(Color.parseColor("#B3261E")); // RED
                break;
            case "In progress":
                holder.title.setTextColor(Color.parseColor("#0473ea")); // BLUE
                break;
            case "Resolved":
                holder.title.setTextColor(Color.parseColor("#1C9838")); // GREEN
        }

        Date dt = null;
        try
        {
            dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(report.getDate());
        }
        catch (ParseException e) { throw new RuntimeException(e); }

        String dateFormated = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dt);

        holder.title.setText(report.getTitle() + " | " + report.getStatus());
        holder.description.setText(report.getDescription());
        holder.date.setText(dateFormated);

        setFadeAnimation(holder.itemView);
    }

    @Override
    public int getItemCount()
    {
        return this.itens.size();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
