package my.kampretstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ActivitySplash extends AppCompatActivity {

    public AsyncHttpClient client;
    public PersistentCookieStore myCookieStore;
    SharedPreferences sharedPref;
    ProgressBar progressBar;
    private Mahasiswa mhs;
    int total = 0;
    TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        mhs = Mahasiswa.getInstance();
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        mhs.client = new AsyncHttpClient(true, 80, 443);
        mhs.client.setTimeout(120000);
        mhs.client.setConnectTimeout(120000);
        mhs.client.setResponseTimeout(120000);
        mhs.myCookieStore = new PersistentCookieStore(getApplicationContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            isLoggedIn();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void isLoggedIn() throws KeyManagementException, NoSuchAlgorithmException {
        mhs.get("indexmhs.php", null, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                mhs.client.setEnableRedirects(false);
                txtStatus.setText("Hello, wait a minutes ...");
//                Toast.makeText(getApplicationContext(), "Checking Session", Toast.LENGTH_SHORT).show();
                super.onStart();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                progressBar.setMax(progressBar.getMax() + Math.round(totalSize));
                progressBar.setProgress(progressBar.getProgress() + Math.round(bytesWritten));
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Connection Error ... (" + String.valueOf(statusCode) + ")", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getBaseContext(), ActivityLogin.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Toast.makeText(getApplicationContext(), "Parsing ...", Toast.LENGTH_SHORT).show();
                if (responseString.contains("Statistik jumlah login :")) {
                    Document document = Jsoup.parse(responseString);
                    String nama = document.select("b").get(3).text().trim();
                    String login = document.select("b").get(2).text().trim();
                    String photoLink = document.select("img").get(2).attr("src").toString().trim();
                    mhs.setNama(nama);
                    String nim = "";
                    String savedNim = sharedPref.getString("NIM", nim);
                    mhs.setNim(savedNim);
                    mhs.setLogin("Login : " + login);
                    try {
                        mhs.get(photoLink, null, new BinaryHttpResponseHandler(new String[]{"image/jpeg", "image/png", "image/jpg", "image/jpeg; charset=binary"}) {
                            @Override
                            public void onStart() {
                                txtStatus.setText("Just a minutes minutes ...");
//                                Toast.makeText(getApplicationContext(), "Fetching Profile", Toast.LENGTH_SHORT).show();
                                mhs.client.setEnableRedirects(false);
                                super.onStart();
                            }

                            @Override
                            public void onProgress(long bytesWritten, long totalSize) {
                                progressBar.setMax(progressBar.getMax() + Math.round(totalSize));
                                progressBar.setProgress(progressBar.getProgress() + Math.round(bytesWritten));
                                super.onProgress(bytesWritten, totalSize);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                                mhs.setPhoto(binaryData);
                                Mahasiswa.getInstance().getError().clear();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                                mhs.setPhoto(null);
                                Mahasiswa.getInstance().getError().add(1);
                                Toast.makeText(getApplicationContext(), "Photo gagal di load! (Error: "+statusCode+")", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFinish() {
                                txtStatus.setText("More more minutes ... :v");
//                                Toast.makeText(getApplicationContext(), "Fetching Nilai", Toast.LENGTH_LONG).show();
                                ambilNilai();
                                super.onFinish();
                            }
                        });
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                } else {
//                    if(!responseString.contains("Muhamad Rizki")){
//                        Toast.makeText(getApplicationContext(), "NOt LOGGED IN", Toast.LENGTH_SHORT).show();
//                    }
                    Intent i = new Intent(getBaseContext(), ActivityLogin.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void ambilNilai(){
        try {
            mhs.get("trasnkrip.php", null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Mahasiswa.getInstance().getError().add(2);
                    Toast.makeText(getApplicationContext(), "Gagal ambil nilai! (Error: " + statusCode + ")", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    progressBar.setMax(progressBar.getMax() + Math.round(totalSize));
                    progressBar.setProgress(progressBar.getProgress() + Math.round(bytesWritten));
                    super.onProgress(bytesWritten, totalSize);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Mahasiswa.getInstance().getError().clear();
                    Document document = Jsoup.parse(responseString);
                    Element table = document.select("table").get(5);
                    Elements trs = table.select("tr");
                    trs.remove(0);
                    String smester = "";
                    List<HashMap<String, String>> nilai = new ArrayList<HashMap<String, String>>();
                    List kumulList = mhs.getKumulatif();
                    HashMap kumul = new HashMap();
                    for (int i=0; i<trs.size();i++) {
                        Elements tds = trs.get(i).select("td");
                        if(tds.size()==1){
                            if(smester!="" ) {
                                mhs.getNilai().put(smester, nilai);
                                nilai = new ArrayList<HashMap<String, String>>();
                            }
                            if(kumul.size()!=0){
                                kumulList.add(kumul);
                                kumul = new HashMap();
//                                kumul.clear();
                            }
                            if(tds.get(0).text().contains("Semester : ")) {
                                smester = tds.get(0).text().replace(": ", "").trim();
                            }

                        }else if(tds.size()==13 && !trs.get(i).text().contains("MATAKULIAH")){
                            HashMap<String, String> nilainya = new HashMap<String, String>();
                            nilainya.put(mhs.getId().get(0), tds.get(2).text().trim());
                            for (int a = 3; a < 13; a++) {
                                nilainya.put(mhs.getId().get(a - 2), tds.get(a).text().trim());
                            }
                            nilai.add(nilainya);
//                            nilainya.clear();
                        }else{
                            if(tds.size()==1) continue;
                            if(tds.size()==3) kumul.put("ketek", tds.get(2).text().trim());
                            if(trs.get(i).text().contains("SKS")) {
                                kumul.put("beban", tds.get(1).text().trim());
                            }else if(trs.get(i).text().contains("Jumlah")) {
                                kumul.put("kumul", tds.get(1).text().trim());
                            }else if(trs.get(i).text().contains("(IPS)")) {
                                kumul.put("ips", tds.get(1).text().trim());
                            }else if(trs.get(i).text().contains("(IPK)")){
                                kumul.put("ipk", tds.get(1).text().trim());
                            }
                            if(i==trs.size()-1){
                                kumulList.add(kumul);
                            }
                        }
                    }
                }

                @Override
                public void onFinish() {
                        Toast.makeText(getApplicationContext(), "Welcome "+mhs.getNama()+"!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
//                    i.putExtra("nim",txtNIM.getText().toString());
//                    i.putExtra("nama",nama);
//                    i.putExtra("login",login);
                        startActivity(i);
                        finish();
                    super.onFinish();
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
