package my.kampretstudent;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.PersistentCookieStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listHeader;
    HashMap<String, List<String>> listChild;
    int lastColored;

    private TextView txtNama;
    private TextView txtNIM;
    private TextView txtLogin;
    private ImageView imgView;
    boolean doubleBackToExitPressedOnce = false;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                Intent i = new Intent(getApplicationContext(), ActivitySplash.class);
                startActivity(i);
                finish();
                return true;
            case R.id.action_back:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        expListView = (ExpandableListView) findViewById(R.id.navigationmenu);
        if(expListView==null){
            expListView = (ExpandableListView) navigationView.findViewById(R.id.navigationmenu);
        }
        prepareData();
        listAdapter = new ExpandableListAdapter(getApplicationContext(), listHeader, listChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(!listAdapter.isHasChild(groupPosition)){
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    String title = ((TextView)v.findViewById(R.id.lblListHeader)).getText().toString();
                    if(title.equals("Home")){
                        goHome(null);
                    }else if(title.equals("Profile")) {
                        Toast.makeText(getApplicationContext(), "Menu belum jadi cuk!", Toast.LENGTH_SHORT).show();
                    }else if(title.equals("About")) {
                        new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppDrawer_Dialog))
                                .setTitle("Kampret Student v3.1")
                                .setMessage("Aplikasi yang memudahkan Mahasiswa LP3I Jakarta untuk melihat nilai selama masa perkuliahan, aplikasi ini dibuat semata-mata hanya untuk iseng dan eksplorasi saja. \nSekian.\n\nCopyright 2016 @Rizkie MW Dev's\n\n\nwww.mangkokweb.id")
                                .setPositiveButton("OK", null)
                                .setIcon(R.drawable.ic_help_black_24dp).show();
                    }else if(title.equals("Logout")){
                        new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppDrawer_Dialog))
                                .setMessage("Yakin mau Logout?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        PersistentCookieStore mycookie = new PersistentCookieStore(getApplicationContext());
                                        mycookie.clear();
                                        Intent i = new Intent(getBaseContext(), ActivityLogin.class);
                                        startActivity(i);
                                        Toast.makeText(getApplicationContext(), "Logged out ...", Toast.LENGTH_LONG).show();
                                        finish();
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }else if(title.equals("Exit")){
                        new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppDrawer_Dialog))
                                .setTitle("Confirm Please")
                                .setMessage("Exit?")
                                .setIcon(R.drawable.ic_report_problem_black_24dp)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast.makeText(getApplicationContext(), "Bye ...", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                    return true;
                }
                return false;
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Fragment fragment = null;
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                String title = ((TextView)v.findViewById(R.id.lblListItem)).getText().toString();
                if(title.contains("Semester")){
                    Bundle bundle = new Bundle();
                    bundle.putString("semester", title);
                    if(Mahasiswa.getInstance().getNilai().get(title)!=null) {
                        fragment = new FragmentCekNilai();
                    }else{
                        fragment = new FragmentNoNilai();
                    }
                    fragment.setArguments(bundle);
                }
                if (fragment != null) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(String.valueOf(title)).commit();
                }
                getSupportActionBar().setTitle("Nilai " + title);
                return false;
            }
        });
        Mahasiswa mhs = Mahasiswa.getInstance();
        txtNama = (TextView) navigationView.getHeaderView(0).findViewById(R.id.Nama_Mhs);
        txtNIM = (TextView) navigationView.getHeaderView(0).findViewById(R.id.NIM_Mhs);
        txtLogin = (TextView) navigationView.getHeaderView(0).findViewById(R.id.Login_Kali);
        txtNama.setText(mhs.getNama());
        txtNIM.setText(mhs.getNim());
        txtLogin.setText(mhs.getLogin());
        imgView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgPhoto);
        if (mhs.getPhoto() != null) {
            Bitmap img = BitmapFactory.decodeByteArray(mhs.getPhoto(), 0, mhs.getPhoto().length);
            imgView.setImageBitmap(img);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Mahasiswa.getInstance().getError().size()>0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu2, menu);
        }
        return true;
    }

    private void prepareData() {
        listHeader = new ArrayList<String>();
        listChild = new HashMap<String, List<String>>();

        listHeader.add("Home");
        listHeader.add("Lihat Nilai");
//        listHeader.add("Profile");
        listHeader.add("About");
        listHeader.add("Logout");
//        listHeader.add("Exit");

        List<String> listNilai = new ArrayList<String>();
        for(int i=1;i<=Mahasiswa.getInstance().getNilai().size();i++) {
            listNilai.add("Semester "+i);
        }

        listChild.put(listHeader.get(1), listNilai);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        String title = "Kampret Student";

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
                fragmentManager.executePendingTransactions();
                if(fragmentManager.getBackStackEntryCount()>0) {
                    title = "Nilai " + fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                }
                getSupportActionBar().setTitle(title);
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(keyCode == KeyEvent.KEYCODE_MENU){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = getSupportActionBar().getTitle().toString();

        item.setChecked(true);

        if (id == R.id.nav_logout) {
            PersistentCookieStore mycookie = new PersistentCookieStore(getApplicationContext());
            mycookie.clear();
            Intent i = new Intent(getBaseContext(), ActivityLogin.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Logged out ...", Toast.LENGTH_LONG).show();
            finish();
        } else if (id == R.id.nav_exit) {
            Toast.makeText(getApplicationContext(), "Sankyu cuk ...", Toast.LENGTH_LONG).show();
            finish();
        } else if (id == R.id.nav_nilai) {
//            Toast.makeText(getApplicationContext(), "Nilai belom keluar cuk ...", Toast.LENGTH_LONG).show();
            fragment = new FragmentCekNilai();
            Mahasiswa mhs = Mahasiswa.getInstance();
            mhs.getNilai();
        }

        if (fragment != null && !title.equals("") && !title.equals(item.getTitle().toString())) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(String.valueOf(id)).commit();
        }

        getSupportActionBar().setTitle(item.getTitle().toString());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goHome(View view){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if(!getSupportActionBar().getTitle().toString().equals("Kampret Student")) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//            for (int i = 0; i < navigationView.getMenu().size(); i++){
//                if(navigationView.getMenu().getItem(i).isChecked()){
//                    navigationView.getMenu().getItem(i).setChecked(false);
//                }
//            }
            getSupportActionBar().setTitle("Kampret Student");
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setStrokeWidth(12);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
