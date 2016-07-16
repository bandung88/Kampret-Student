package my.kampretstudent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Rizkie on 14/07/2016.
 */
public class ListNilaiAdapter extends ArrayAdapter<HashMap<String, String>> {
    public ListNilaiAdapter(Context context, int resource, List<HashMap<String, String>> objects) {
        super(context, resource, objects);
    }
}
