package edu.cnm.deepdive.apod.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.apod.BuildConfig;
import edu.cnm.deepdive.apod.R;
import edu.cnm.deepdive.apod.fragment.DateTimePickerFragment;
import edu.cnm.deepdive.apod.fragment.DateTimePickerFragment.Mode;
import edu.cnm.deepdive.apod.model.Apod;
import edu.cnm.deepdive.apod.service.ApodService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String CALENDAR_KEY = "calendar";
  private static final String URL_KEY = "url";


  private WebView webView;
  private String apiKey;
  private ProgressBar progressSpinner;
  private FloatingActionButton jumpDate;
  private Calendar calendar;
  private ApodService service;
  private Apod apod;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupWebView();
    setupService();
    setupUI();
    setupDefault(savedInstanceState);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(CALENDAR_KEY, calendar.getTimeInMillis());
    outState.putString(URL_KEY, webView.getUrl());
  }

  private void setupWebView() {
    webView = findViewById(R.id.web_view);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        progressSpinner.setVisibility(View.GONE);
        if (apod != null) {
          Toast.makeText(MainActivity.this, apod.getTitle(), Toast.LENGTH_LONG).show();
        }
      }
    });
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(true);
    settings.setDisplayZoomControls(false);
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
  }

  private void setupService() {
    Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setDateFormat(DATE_FORMAT)
        .create();
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
    service = retrofit.create(ApodService.class);
    apiKey = BuildConfig.API_KEY;
  }

  private void setupUI() {
    progressSpinner = findViewById(R.id.progress_spinner);
    progressSpinner.setVisibility(View.GONE);
    jumpDate = findViewById(R.id.jump_date);
    jumpDate.setOnClickListener(view -> pickDate());

  }

  private void setupDefault(Bundle savedInstanceState) {
    calendar = Calendar.getInstance();
    if (savedInstanceState != null) {
      calendar.setTimeInMillis(savedInstanceState.getLong(CALENDAR_KEY));
      progressSpinner.setVisibility(View.VISIBLE);
      webView.loadUrl(savedInstanceState.getString(URL_KEY));
    } else {
      new ApodTask().execute();
    }
  }

  private void pickDate() {
    DateTimePickerFragment fragment = new DateTimePickerFragment();
    fragment.setMode(Mode.DATE);
    fragment.setPassthrough(false);
    fragment.setCalendar(calendar);
    fragment.setOnChangeListener(cal -> new ApodTask().execute(cal.getTime()));
    fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
  }

  private class ApodTask extends AsyncTask<Date, Void, Apod> {

    private Date date;

    @Override
    protected void onPreExecute() {
      progressSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Apod apod) {
      webView.loadUrl(apod.getUrl());
      MainActivity.this.apod = apod;
    }

    @Override
    protected void onCancelled(Apod apod) {
      Context context = MainActivity.this;
      progressSpinner.setVisibility(View.GONE);
      Toast.makeText(context, context.getString(R.string.error_message, date), Toast.LENGTH_LONG).show();
    }

    @Override
    protected Apod doInBackground(Date... dates) {
      Apod apod = null;
      try {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        date = dates.length == 0 ? calendar.getTime() : dates[0];
        Response<Apod> result = service.get(apiKey, format.format(date)).execute();
        if (result.isSuccessful()) {
          apod = result.body();
          calendar.setTime(date);
        }
      } catch (IOException e) {
        // Do nothing; apod is already null.
      } finally {
        if (apod == null) {
          cancel(true);
        }
      }
      return apod;
    }
  }

}
