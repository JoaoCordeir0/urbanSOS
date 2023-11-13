package br.com.urbansos.models;

import android.content.Context;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
    private Context context;
    private ArrayList<Notification> itens;

    public NotificationAdapter(Context context, ArrayList<Notification> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.notifications_list, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position)
    {
        Notification notification = itens.get(position);

        Date dt = null;
        try
        {
            dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(notification.getDate());
        }
        catch (ParseException e) { throw new RuntimeException(e); }

        String dateFormated = new SimpleDateFormat("dd/MM HH:mm").format(dt);

        holder.title.setText(dateFormated + " - " + notification.getTitle());

        if (notification.getTitle().toLowerCase().indexOf("resolved") != -1)
        {
            holder.icon.setImageResource(R.drawable.resolved);
        }

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
