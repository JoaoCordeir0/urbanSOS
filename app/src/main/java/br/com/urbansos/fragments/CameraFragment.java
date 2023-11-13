package br.com.urbansos.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
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
import java.io.IOException;
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
    private TextView report_image_alert;
    private String currentPhotoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
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
            try
            {
                // Requisição que retorna a cidade em que o usuário está usando o app.
                Main.requestQueue.add((new Volley()).sendRequestGET("/city/latlng/" + latitude + "/" + longitude, new IVolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        // Desliga o preloader
                        ((LinearProgressIndicator) view.findViewById(R.id.progressindicator_camera)).setVisibility(View.INVISIBLE);

                        JSONObject address = response.getJSONObject("address0");

                        // Caso a cidade não estiver disponivel lista as cidades disponiveis
                        if (address.getString("city").equals("0") || address.getString("status").equals("0"))
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
                            report_image_alert = view.findViewById(R.id.msg_alert_no_image);

                            // Seta os campos do formulario visiveis
                            ((TextInputLayout) view.findViewById(R.id.input_report_title)).setVisibility(View.VISIBLE);
                            ((TextInputLayout) view.findViewById(R.id.input_report_description)).setVisibility(View.VISIBLE);
                            ((TextInputLayout) view.findViewById(R.id.input_select_level)).setVisibility(View.VISIBLE);
                            ((Button) view.findViewById(R.id.btn_send_report)).setVisibility(View.VISIBLE);

                            // Intent da camera
                            try
                            {
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
                                {
                                    File photoFile = null;
                                    try
                                    {
                                        photoFile = createImageFile();
                                    }
                                    catch (IOException ex) {  }

                                    if (photoFile != null)
                                    {
                                        Uri photoURI = FileProvider.getUriForFile(getContext(), "br.com.urbansos", photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                Functions.alert(getContext(), "Error", "Error when trying to open the camera. Try restarting the app", "Ok",true);
                            }

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
            catch (JSONException e)
            {
                Functions.alert(getContext(), "Error", "App internal error!", "Ok",true);
            }
        }
    }

    private File createImageFile() throws IOException, JSONException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK)
        {
            Functions.setCachedPhoto(currentPhotoPath);

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

            try {
                ExifInterface exifInterface = new ExifInterface(currentPhotoPath);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                bitmap = rotateBitmap(bitmap, orientation);
            } catch (IOException e) {
                e.printStackTrace();
            }

            report_image.setImageBitmap(bitmap);
            report_image.setVisibility(View.VISIBLE);
        }
        else
        {
            report_image_alert.setVisibility(View.VISIBLE);
        }
    }
}