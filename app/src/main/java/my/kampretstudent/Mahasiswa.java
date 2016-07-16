package my.kampretstudent;

import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rizkie on 11/07/2016.
 */

public class Mahasiswa {

    public AsyncHttpClient client;
    public PersistentCookieStore myCookieStore;

    private static Mahasiswa instance = null;

    private String nama;
    private String nim;
    private String login;
    private byte[] photo;
    private List<Integer> error = new ArrayList<>();
    private HashMap<String, List<HashMap<String, String>>> nilai = new HashMap<>();
    private List<String> id = new ArrayList<>();
    private List<HashMap<String, String>> kumulatif = new ArrayList<>();

    public List<Integer> getError() {
        return error;
    }

    public void setError(List<Integer> error) {
        this.error = error;
    }

    public HashMap<String, List<HashMap<String, String>>> getNilai() {
        return nilai;
    }

    public void setNilai(HashMap<String, List<HashMap<String, String>>> nilai) {
        this.nilai = nilai;
    }

    public List<HashMap<String, String>> getKumulatif() {
        return kumulatif;
    }

    public void setKumulatif(List<HashMap<String, String>> kumulatif) {
        this.kumulatif = kumulatif;
    }

    public List<String> getId() {
        if(this.id.size() < 1) {
            this.id.add("lstMatkul");
            this.id.add("lstSKS");
            this.id.add("lstABS");
            this.id.add("lstFRM");
            this.id.add("lstTGS");
            this.id.add("lstPRK");
            this.id.add("lstUTS");
            this.id.add("lstUAS");
            this.id.add("lstSP");
            this.id.add("lstAKHIR");
            this.id.add("lstMUTU");
        }
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public Mahasiswa() {
    }

    public static Mahasiswa getInstance() {
        if (instance == null) {
            instance = new Mahasiswa();
        }
        return instance;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) throws NoSuchAlgorithmException, KeyManagementException {
        if (!url.contains("https://")) {
            client.addHeader("Host", "mhs.politekniklp3i-jkt.ac.id");
            client.addHeader("Referer", "https://mhs.politekniklp3i-jkt.ac.id");
        } else {
            client.addHeader("Host", "sim.politekniklp3i-jkt.ac.id");
            client.addHeader("Referer", "https://mhs.politekniklp3i-jkt.ac.id/indexmhs.php");
        }
        client.setUserAgent("Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:28.0) Gecko/20100101 Firefox/28.0");
        client.setCookieStore(myCookieStore);
        if (!url.contains("https://")) {
            client.get("https://mhs.politekniklp3i-jkt.ac.id/" + url, params, responseHandler);
        } else {
            client.get(url, params, responseHandler);
        }
    }
}
