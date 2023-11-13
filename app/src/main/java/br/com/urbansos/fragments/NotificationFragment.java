package br.com.urbansos.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.urbansos.Main;
import br.com.urbansos.R;
import br.com.urbansos.functions.Functions;
import br.com.urbansos.http.Volley;
import br.com.urbansos.interfaces.IVolleyCallback;
import br.com.urbansos.models.Notification;
import br.com.urbansos.models.NotificationAdapter;

public class NotificationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private ArrayList<Notification> itens;
    private RecyclerView recycle;

    public NotificationFragment() { }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try
        {
            // Carrega as notificações que vem da API para a recycleview
            notificationsDataInitialize(view);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void notificationsDataInitialize(View view) throws JSONException {
        itens = new ArrayList<Notification>();
        Main.requestQueue.add((new Volley()).sendRequestGET("/notification/list/" + (Functions.getCachedAuth()).getString("id"), new IVolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                if (response.length() > 0)
                {
                    for (int c = 0; c < response.length(); c++)
                    {
                        JSONObject r = response.getJSONObject("notification" + c);
                        itens.add(new Notification(
                                r.getString("title"),
                                r.getString("createdAt")
                        ));
                    }
                    // Oculta o preloader
                    ((LinearProgressIndicator) view.findViewById(R.id.progressindicator_notifications)).setVisibility(View.INVISIBLE);

                    recycle = view.findViewById(R.id.notifications_recycleview);
                    recycle.setLayoutManager(new LinearLayoutManager(getContext()));
                    recycle.setAdapter(new NotificationAdapter(getContext(), itens));
                }
                else
                {
                    // Oculta o preloader
                    ((LinearProgressIndicator) view.findViewById(R.id.progressindicator_notifications)).setVisibility(View.INVISIBLE);
                    // Ativa a mensagem de nenhuma notificação disponivel
                    ((MaterialCardView) view.findViewById(R.id.card_no_notifications)).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onError(JSONObject response) throws JSONException {
                Functions.alert(getContext(), "Error", "Your notifications were unable to load, try reopening the app!", "Ok",true);
            }
        }, (Functions.getCachedAuth()).getString("token"), "notification"));
    }
}