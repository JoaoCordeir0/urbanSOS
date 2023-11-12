package br.com.urbansos.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
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
import br.com.urbansos.models.Report;
import br.com.urbansos.models.ReportAdapter;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private ArrayList<Report> itens;
    private RecyclerView recycle;

    public HomeFragment() { }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try
        {
            // Carrega os reports que vem da API para a recycleview
            reportsDataInitialize(view);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void reportsDataInitialize(View view) throws JSONException {
        itens = new ArrayList<Report>();
        Main.requestQueue.add((new Volley()).sendRequestGET("/report/list/user/" + (Functions.getCachedAuth()).getString("id"), new IVolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                if (response.length() > 0)
                {
                    for (int c = 0; c < response.length(); c++)
                    {
                        JSONObject r = response.getJSONObject("report" + c);
                        itens.add(new Report(
                                r.getString("title"),
                                r.getString("description"),
                                r.getString("createdAt"),
                                r.getString("image"),
                                r.getString("latitude"),
                                r.getString("longitude"),
                                r.getString("situation"),
                                r.getString("status"),
                                Integer.parseInt(r.getString("userId")),
                                Integer.parseInt(r.getString("cityId"))
                        ));
                    }
                    // Oculta o preloader
                    ((LinearProgressIndicator) view.findViewById(R.id.progressindicator_reports)).setVisibility(View.INVISIBLE);

                    recycle = view.findViewById(R.id.reports_recycleview);
                    recycle.setLayoutManager(new LinearLayoutManager(getContext()));
                    recycle.setAdapter(new ReportAdapter(getContext(), itens));
                }
                else
                {
                    // Oculta o preloader
                    ((LinearProgressIndicator) view.findViewById(R.id.progressindicator_reports)).setVisibility(View.INVISIBLE);
                    // Ativa a mensagem de nenhum report disponivel
                    ((MaterialCardView) view.findViewById(R.id.card_notfound)).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onError(JSONObject response) throws JSONException {
                Functions.alert(getContext(), "Error", "Your reports were unable to load, try reopening the app!", "Ok",true);
            }
        }, (Functions.getCachedAuth()).getString("token"), "report"));
    }
}