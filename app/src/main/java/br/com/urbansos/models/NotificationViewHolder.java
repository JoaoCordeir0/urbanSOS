package br.com.urbansos.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.urbansos.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    ImageView icon;

    public NotificationViewHolder(View view)
    {
        super(view);
        title = view.findViewById(R.id.notification_title);
        icon = view.findViewById(R.id.notification_icon);
    }
}
