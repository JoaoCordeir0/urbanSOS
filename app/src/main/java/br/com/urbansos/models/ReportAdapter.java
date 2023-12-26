package br.com.urbansos.models;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.urbansos.Main;
import br.com.urbansos.R;
import br.com.urbansos.fragments.CameraFragment;
import br.com.urbansos.functions.Functions;
import br.com.urbansos.http.Volley;
import br.com.urbansos.interfaces.IVolleyCallback;

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
                holder.status.setTextColor(Color.parseColor("#B3261E")); // RED
                break;
            case "In progress":
                holder.status.setTextColor(Color.parseColor("#0473ea")); // BLUE
                break;
            case "Resolved":
                holder.status.setTextColor(Color.parseColor("#1C9838")); // GREEN
        }

        Date dt = null;
        try
        {
            dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(report.getDate());
        }
        catch (ParseException e) { throw new RuntimeException(e); }

        String dateFormated = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dt);

        holder.title.setText(report.getTitle());
        holder.status.setText("Status: " + report.getStatus());
        holder.date.setText("Created in: " + dateFormated);

        // Cria popup com informações do card quando clicado
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Infla o layout personalizado
                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_report, null);

                // Configura os elementos de interface do usuário no layout personalizado
                CircularProgressIndicator preloader = dialogView.findViewById(R.id.progressindicator_popup_reports);
                ImageView dialogImg = dialogView.findViewById(R.id.popup_img_report);
                TextView dialogTitle = dialogView.findViewById(R.id.popup_title_report);
                TextView dialogDescription = dialogView.findViewById(R.id.popup_description_report);
                TextView dialogDate = dialogView.findViewById(R.id.popup_date_report);
                TextView dialogSituation = dialogView.findViewById(R.id.popup_situation_report);
                TextView dialogStatus = dialogView.findViewById(R.id.popup_status_report);
                Button btnDialogClose = dialogView.findViewById(R.id.popup_btn_close);

                // Tenta pegar a imagem do celular do usuario, caso não encontre, pega do servidor AWS S3
                File storageDir = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                String imgPath = storageDir + "/" + report.getImage();

                if ((new File(imgPath)).exists())
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                    ExifInterface exifInterface = null;
                    try
                    {
                        exifInterface = new ExifInterface(imgPath);
                    }
                    catch (IOException e) { }

                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    dialogImg.setImageBitmap(CameraFragment.rotateBitmap(bitmap, orientation));
                }
                else
                {
                    Glide.with(view.getContext())
                            .asBitmap()
                            .load("https://s3.sa-east-1.amazonaws.com/urbansos.images/" + report.getImage())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    preloader.setVisibility(View.INVISIBLE);
                                    dialogImg.setImageBitmap(resource);
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) { }
                            });
                }

                dialogTitle.setText("Title: " + report.getTitle());
                dialogDescription.setText("Description: " + report.getDescription());
                dialogDate.setText("Created in: " + dateFormated);
                dialogSituation.setText("Situation: " + report.getSituation());
                dialogStatus.setText("Status: " + report.getStatus());

                // Cria o AlertDialog com o layout personalizado
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(dialogView);

                final AlertDialog alertDialog = builder.create();

                btnDialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss(); // Fecha o AlertDialog
                    }
                });

                alertDialog.show();
            }
        });

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
