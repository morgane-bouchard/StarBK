package fr.istic.mob.starbk;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.istic.mob.starbk.ServicesManager.HttpRequest;
import fr.istic.mob.starbk.ServicesManager.Unzipper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DATASET_BASE_URL = "https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-busmetro-horaires-gtfs-versions-td&sort=-debutvalidite";

    private MyApplication app;
    private ProgressDialog downloadProgress;
    private ProgressDialog installationProgress;
    private Spinner routesSpinner;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private TextView mDisplayTime;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        mDisplayTime = (TextView) findViewById(R.id.tvTime);
        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int hour = cal.get(Calendar.HOUR_OF_DAY);
                final int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mDisplayTime.setText(hour+":"+minute);
                    }
                }, 0, 0, false);
                dialog.show();
            }
        });

        applicationSetUp();
        checkConfiguration();

        MyApplication app = (MyApplication) getApplication();

        final List<String> routes = new ArrayList<>();
        Cursor routesCursor = app.getDataSore().getBusRoutes();
        while (routesCursor.moveToNext()) {
            routes.add(routesCursor.getString(routesCursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.SHORT_NAME)));
        }

        Spinner routesSpinner = findViewById(R.id.spinner_lignes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item ,routes);
        routesSpinner.setAdapter(adapter);
//        routesSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Spinner directionSpinner = findViewById(R.id.spinner_direction);
//                String routeName = routes.get(i);
//                // requet pour recuperer directions
//                List<String> directions = new ArrayList<>(Arrays.asList("Kone", "Bouchard"));
//                ArrayAdapter<String> directionAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item ,directions);
//                directionSpinner.setAdapter(directionAdapter);
//            }
//        });

    }

    private void download(Bundle bundle) {
        String mimetype = bundle.getString("mimetype");
        final String url = bundle.getString("url");

        HttpRequest.get(url, new BinaryHttpResponseHandler(new String[]{mimetype}) {

            @Override
            public void onStart() {
                super.onStart();
                downloadProgress.show();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);

                downloadProgress.setProgress((int) (bytesWritten * 100 / totalSize));
                downloadProgress.getCurrentFocus();
            }

            @Override
            public void onFinish() {
                super.onFinish();

                downloadProgress.dismiss();

                updateDatabase();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                String filename = url.substring(url.lastIndexOf("/") + 1, url.length());

                try {
                    handleBinaryData(binaryData, filename);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                Log.e(TAG, "download", error);
            }
        });
    }

    private void handleBinaryData(byte[] binaryData, String filename) throws IOException {
        File file = new File(getFilesDir(), filename);
        FileOutputStream out = new FileOutputStream(file);
        out.write(binaryData);
        out.close();

        // unzip
        String filesDir = file.getAbsolutePath().replace(".zip", "");
        Unzipper.unzip(file.getAbsolutePath(), filesDir);

        app.getDataSore().setFilesDir(filesDir);
    }

    private void updateDatabase() {
        new InstallationHandler().execute(true);
    }

    private class InstallationHandler extends AsyncTask<Boolean, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            installationProgress.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            app.getDataSore().updateDatabase();

            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            installationProgress.dismiss();

            try {
                createConfigFile();
            } catch (JSONException | IOException e) {
                Log.e(TAG, "Error while creating config file", e);
            }
        }
    }

    private AsyncHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                download(
                        handlingResponse(response)
                );
            } catch (JSONException e) {
                Log.e(TAG, "mResponseHandler", e);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.e(TAG, "mResponseHandler request failure", throwable);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createConfigFile() throws JSONException, IOException {
        String filename = "star_config";
        JSONObject fileContent = new JSONObject();
        fileContent.put("fist_time", "anymore");
        try (FileOutputStream out = openFileOutput(filename, Context.MODE_PRIVATE)) {
            out.write(fileContent.toString().getBytes());
        }
    }

    private Bundle handlingResponse(JSONObject response) throws JSONException {
        JSONObject records = (JSONObject) response.getJSONArray("records").get(0);
        JSONObject fields = (JSONObject) records.get("fields");
        JSONObject file = (JSONObject) fields.get("fichier");
        String filename = file.getString("filename");
        filename = filename.replace(".zip", "");
        String mimetype = file.getString("mimetype");
        String url = fields.getString("url");

        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putString("mimetype", mimetype);
        bundle.putString("url", url);

        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applicationSetUp();
        checkConfiguration();

        MyApplication app = (MyApplication) getApplication();

        final List<String> routes = new ArrayList<>();
        Cursor routesCursor = app.getDataSore().getBusRoutes();
        while (routesCursor.moveToNext()) {
            routes.add(routesCursor.getString(routesCursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.SHORT_NAME)));
        }

        routesSpinner = findViewById(R.id.spinner_lignes);
        ArrayAdapter<String> adapterRoutes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item ,routes);
        adapterRoutes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routesSpinner.setAdapter(adapterRoutes);


        routesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Test clicked", Toast.LENGTH_SHORT).show();
                //STring elemt = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Log.d('');
                Spinner directionSpinner = findViewById(R.id.spinner_direction);
                String routeName = routes.get(i);
                // requet pour recuperer directions
                List<String> directions = new ArrayList<>(Arrays.asList("Kone", "Bouchard"));
                ArrayAdapter<String> directionAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item ,directions);
                directionSpinner.setAdapter(directionAdapter);
            }
        });

    }

    private boolean isFirstTime() {
        List<String> fileList = Arrays.asList(fileList());

        return !fileList.contains("star_config");
    }

    private void applicationSetUp() {
        app = (MyApplication) getApplication();

        downloadProgress = new ProgressDialog(this);
        downloadProgress.setMessage("Installation... step 1 of 2");
        downloadProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadProgress.setMax(100);
        downloadProgress.setIndeterminate(false);

        installationProgress = new ProgressDialog(this);
        installationProgress.setMessage("Installation... step 2 of 2.\nit can take a long time");
        installationProgress.setIndeterminate(false);
    }
    private void checkConfiguration() {
        if (isFirstTime()) {
            HttpRequest.get(DATASET_BASE_URL, responseHandler);
        }
    }
}
