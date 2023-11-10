package br.com.urbansos.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.urbansos.Main;
import br.com.urbansos.R;
import br.com.urbansos.functions.Functions;
import br.com.urbansos.http.Volley;
import br.com.urbansos.interfaces.IVolleyCallback;
import br.com.urbansos.services.GPSTracker;

public class CameraFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private ImageView report_image;
    String[] items = {"Tolerable", "Serious", "Urgent"};
    AutoCompleteTextView autoCompleteReportOptions;
    ArrayAdapter<String> adapterItens;

    public CameraFragment() { }

    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GPSTracker gps = new GPSTracker(getContext());

        // Checa se foi possível recuperar a localização do usuário
        if (gps.canGetLocation())
        {
            String latitude = gps.getLatitude();
            String longitude = gps.getLongitude();
            try {
                // Requisição que retorna a cidade em que o usuário está usando o app.
                Main.requestQueue.add((new Volley()).sendRequestGET("/city/latlng/" + latitude + "/" + longitude, new IVolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        // Desliga o preloader
                        ((LinearProgressIndicator) view.findViewById(R.id.progressindicator_camera)).setVisibility(View.INVISIBLE);

                        JSONObject address = response.getJSONObject("address0");

                        // Caso a cidade não estiver disponivel lista as cidades disponiveis
                        if (address.getString("city").equals("0"))
                        {
                            ((MaterialCardView) view.findViewById(R.id.card_cities_available)).setVisibility(View.VISIBLE);

                            Functions.alert(getContext(), "Warning", address.getString("message"), "Ok",true);

                            // Recupera da API uma listagem das cidades disponiveis para uso do app
                            Main.requestQueue.add((new Volley()).sendRequestGET("/city/list", new IVolleyCallback() {
                                @Override
                                public void onSuccess(JSONObject response) throws JSONException {
                                    if (response.length() > 0)
                                    {
                                        String cities = "";
                                        for (int c = 0; c < response.length(); c++)
                                        {
                                            JSONObject city = response.getJSONObject("city" + c);
                                            cities += city.getString("name") + " - " + city.getString("state") + "\n";
                                        }
                                        ((TextView) view.findViewById(R.id.cities_available_names)).setText(cities.substring(0, cities.length() - 1));
                                    }
                                }
                                @Override
                                public void onError(JSONObject response) throws JSONException {
                                    Functions.alert(getContext(), "Error", response.getString("message"), "Ok",true);
                                }
                            }, (Functions.getCachedAuth()).getString("token"), "city"));
                        }
                        else // Caso a cidade esteja disponivel abre a camera e libera o formulario de report
                        {
                            Functions.setCachedLocation(latitude, longitude, address.getString("city"), address.getString("address"));

                            report_image = view.findViewById(R.id.report_image);

                            // Seta os campos do formulario visiveis
                            ((TextInputLayout) view.findViewById(R.id.input_report_title)).setVisibility(View.VISIBLE);
                            ((TextInputLayout) view.findViewById(R.id.input_report_description)).setVisibility(View.VISIBLE);
                            ((TextInputLayout) view.findViewById(R.id.input_select_level)).setVisibility(View.VISIBLE);
                            ((Button) view.findViewById(R.id.btn_send_report)).setVisibility(View.VISIBLE);

                            // Intent da camera
                            Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(open_camera, 100);

                            autoCompleteReportOptions = view.findViewById(R.id.select_report_options);
                            autoCompleteReportOptions.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.report_options, items));
                        }
                    }
                    @Override
                    public void onError(JSONObject response) throws JSONException {
                        Functions.alert(getContext(), "Error", response.getString("message"), "Ok",true);
                    }
                }, (Functions.getCachedAuth()).getString("token"), "address"));
            }
            catch (JSONException e) { throw new RuntimeException(e); }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap photo = (Bitmap) data.getExtras().get("data");
        report_image.setImageBitmap(photo);
        report_image.setVisibility(View.VISIBLE);

        // Caminho do arquivo
        File file = new File(getRealPathFromURI(getImageUri(getContext(), photo)));

        // Seta em cache o caminho da foto tirada
        Functions.setCachedPhoto(file.getPath());
    }

    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + timeStamp, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri)
    {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}