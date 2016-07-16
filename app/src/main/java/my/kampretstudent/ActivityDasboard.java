package my.kampretstudent;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.PersistentCookieStore;

public class ActivityDasboard extends AppCompatActivity {

    private TextView txtNama;
    private TextView txtNIM;
    private TextView txtLogin;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasboard);

        Mahasiswa mhs = Mahasiswa.getInstance();
        txtNama = (TextView) findViewById(R.id.txtNamaMhs);
        txtNIM = (TextView) findViewById(R.id.txtNIMMhs);
        txtLogin = (TextView) findViewById(R.id.txtLoginMhs);
        txtNama.setText(mhs.getNama());
        txtNIM.setText(mhs.getNim());
        txtLogin.setText(mhs.getLogin());
        imgView = (ImageView) findViewById(R.id.imgProfil);
        if (mhs.getPhoto() != null) {
            imgView.setImageBitmap(BitmapFactory.decodeByteArray(mhs.getPhoto(), 0, mhs.getPhoto().length));
        }
    }

    public void doLogout(View view) {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(getApplicationContext());
        myCookieStore.clear();
        Toast.makeText(getApplicationContext(), "Logging Out ...", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getBaseContext(), ActivityLogin.class);
        startActivity(i);
        finish();
    }
}
