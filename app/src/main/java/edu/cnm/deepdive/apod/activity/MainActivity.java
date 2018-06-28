package edu.cnm.deepdive.apod.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ProgressBar;
import edu.cnm.deepdive.apod.R;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

  private WebView webView;
  private String apiKey;
  private ProgressBar progressSpinner;
  private FloatingActionButton jumpDate;
  private Calendar calendar;

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
    // TODO Add steps to save state on rotation.
  }

  private void setupWebView() {
    // TODO Set up the WebView object.
  }

  private void setupService() {
    // TODO Instantiate the GSON, Retrofit, and service objects.
  }

  private void setupUI() {
    // TODO Set up the progress spinner and date selector button.
  }

  private void setupDefault(Bundle savedInstanceState) {
    // TODO Using current (or saved) date as the default, set calendar and URL.
  }

}
