package my.kampretstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;

public class ActivityLogin extends AppCompatActivity {

    private static final String BASE_URL = "https://mhs.politekniklp3i-jkt.ac.id/";
    private AsyncHttpClient client;
    private PersistentCookieStore myCookieStore;
    private ProgressBar progressBar;

    private EditText txtNIM;
    private EditText txtPass;
    private Button btnLogin;

    private static String getAbsoluteUrl(String relativeUrl) {
        if (!relativeUrl.contains("https://")) {
            return BASE_URL + relativeUrl;
        } else {
            return relativeUrl;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        client = new AsyncHttpClient(true, 80, 443);
        myCookieStore = new PersistentCookieStore(getApplicationContext());
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtNIM = (EditText) findViewById(R.id.txtNIM);
        txtPass = (EditText) findViewById(R.id.txtPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String nim = "";
        String pwd = "";
        String savedNim = sharedPref.getString("NIM", nim);
        String savedPwd = sharedPref.getString("PWD", pwd);
        txtNIM.setText(savedNim);
        txtPass.setText(savedPwd);
        progressBar.setVisibility(View.INVISIBLE);
//        try {
//            isLoggedIn();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (txtNIM.getText().toString() != null && txtPass.getText().toString() != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("NIM", txtNIM.getText().toString());
            editor.putString("PWD", txtPass.getText().toString());
            editor.commit();
        }
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) throws NoSuchAlgorithmException, KeyManagementException {
        client.addHeader("Host", "mhs.politekniklp3i-jkt.ac.id");
        client.addHeader("Referer", "https://mhs.politekniklp3i-jkt.ac.id");
        client.setUserAgent("Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:28.0) Gecko/20100101 Firefox/28.0");
        client.setCookieStore(myCookieStore);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        myCookieStore = new PersistentCookieStore(this);
        client.addHeader("Host", "mhs.politekniklp3i-jkt.ac.id");
        client.addHeader("Referer", "https://mhs.politekniklp3i-jkt.ac.id");
        client.setUserAgent("Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:28.0) Gecko/20100101 Firefox/28.0");
        client.setCookieStore(myCookieStore);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void btnLogin_Click(View view) {
        RequestParams params = new RequestParams();
        params.put("useruser", txtNIM.getText().toString());
        params.put("pwdpwd", txtPass.getText().toString());
        params.put("login", "Login+System");
        progressBar.setVisibility(View.VISIBLE);

        post("cekucel.php", params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                client.setEnableRedirects(false);
                btnLogin.setEnabled(false);
                txtNIM.setEnabled(false);
                txtPass.setEnabled(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getApplicationContext(), "Response: " + String.valueOf(statusCode), Toast.LENGTH_SHORT).show();;
                progressBar.setVisibility(View.INVISIBLE);
                if (!String.valueOf(statusCode).equals("302")) {
                    btnLogin.setEnabled(true);
                    txtNIM.setEnabled(true);
                    txtPass.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Something went wrong ...", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getBaseContext(), ActivitySplash.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                progressBar.setMax(Math.round(totalSize));
                progressBar.setProgress(Math.round(bytesWritten * 2));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Toast.makeText(getApplicationContext(), "Response: " + String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                if (statusCode == 200) {
                    Toast.makeText(getApplicationContext(), "Login failed ...", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    txtNIM.setEnabled(true);
                    txtPass.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Intent i = new Intent(getBaseContext(), ActivitySplash.class);
                    startActivity(i);
                    finish();
//                    try {
//                        isLoggedIn();
//                    } catch (KeyManagementException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
    }

    public void btnExit_Click(View view){
        finish();
    }

//    public void isLoggedIn() throws KeyManagementException, NoSuchAlgorithmException {
//        get("indexmhs.php", null, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                super.onStart();
//                client.setEnableRedirects(false);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                btnLogin.setEnabled(true);
//                txtNIM.setEnabled(true);
//                txtPass.setEnabled(true);
//                Toast.makeText(getApplicationContext(), "Connection Error ... (" + String.valueOf(statusCode) + ")", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onProgress(long bytesWritten, long totalSize) {
//                super.onProgress(bytesWritten, totalSize);
//                progressBar.setMax(Math.round(totalSize));
//                progressBar.setProgress(Math.round(bytesWritten * 2));
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
////                Toast.makeText(getApplicationContext(), "Parsing ...", Toast.LENGTH_SHORT).show();
//                if (responseString.contains("Statistik jumlah login :")) {
//                    Document document = Jsoup.parse(responseString);
//                    String nama = document.select("b").get(3).text().trim();
//                    String login = document.select("b").get(2).text().trim();
//                    final Mahasiswa mhs = Mahasiswa.getInstance();
//                    mhs.setNama(nama);
//                    mhs.setNim(txtNIM.getText().toString());
//                    mhs.setLogin("Login : " + login);
//                    try {
//                        get("https://sim.politekniklp3i-jkt.ac.id/AdminPendidikan/timthumb.php?src=fotomahasiswa/" + txtNIM.getText().toString() + "-FOTO.jpg&w=206&h=258&zc=1", null, new BinaryHttpResponseHandler(new String[]{"image/jpeg", "image/png", "image/jpg", "image/jpeg; charset=binary"}) {
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
//                                mhs.setPhoto(binaryData);
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
//                                mhs.setPhoto(null);
//                            }
//
//                            @Override
//                            public void onProgress(long bytesWritten, long totalSize) {
//                                super.onProgress(bytesWritten, totalSize);
//                                progressBar.setMax(Math.round(totalSize));
//                                progressBar.setProgress(Math.round(bytesWritten * 2));
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                super.onFinish();
//                                Toast.makeText(getApplicationContext(), "Welcome cuk ...", Toast.LENGTH_SHORT).show();
//                                Intent i = new Intent(getBaseContext(), MainActivity.class);
////                    i.putExtra("nim",txtNIM.getText().toString());
////                    i.putExtra("nama",nama);
////                    i.putExtra("login",login);
//                                startActivity(i);
//                                btnLogin.setEnabled(true);
//                                txtNIM.setEnabled(true);
//                                txtPass.setEnabled(true);
//                                progressBar.setVisibility(View.INVISIBLE);
//                                finish();
//                            }
//                        });
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    } catch (KeyManagementException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
}
