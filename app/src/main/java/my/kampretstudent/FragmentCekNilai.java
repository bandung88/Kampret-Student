package my.kampretstudent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCekNilai extends Fragment {

    public FragmentCekNilai() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cek_nilai, container, false);
        ListView lv = (ListView) view.findViewById(R.id.listViewNilai);
        Mahasiswa mhs = Mahasiswa.getInstance();
        List<HashMap<String, String>> data = mhs.getKumulatif();
        View header = inflater.inflate(R.layout.list_header, null);
        View footer = inflater.inflate(R.layout.ads, null);
        int smt = Integer.parseInt(args.getString("semester").substring(args.getString("semester").length()-1));
        ((TextView)header.findViewById(R.id.nilSKS)).setText(data.get(smt-1).get("beban"));
        ((TextView)header.findViewById(R.id.nilKumul)).setText(data.get(smt-1).get("kumul"));
        ((TextView)header.findViewById(R.id.nilIPS)).setText(data.get(smt-1).get("ips"));
        ((TextView)header.findViewById(R.id.nilIPK)).setText(data.get(smt-1).get("ipk"));
        ((TextView)header.findViewById(R.id.nilKET)).setText(data.get(smt-1).get("ketek"));
        lv.addHeaderView(header);
        AdView mAdView = (AdView) footer.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        lv.addFooterView(footer);
        SimpleAdapter listAdapter = new SimpleAdapter(getContext(), mhs.getNilai().get(args.getString("semester")), R.layout.list_nilai,mhs.getId().toArray(new String[mhs.getId().size()]), new int[]{R.id.lstMatkul, R.id.lstSKS, R.id.lstABS,  R.id.lstFRM,  R.id.lstTGS,  R.id.lstPRK,  R.id.lstUTS,  R.id.lstUAS, R.id.lstSP, R.id.lstAKHIR,  R.id.lstMUTU});
        lv.setAdapter(listAdapter);
        return view;
    }

}
