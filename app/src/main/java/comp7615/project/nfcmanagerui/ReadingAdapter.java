package comp7615.project.nfcmanagerui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by danny on 11/14/2017.
 */

public class ReadingAdapter extends BaseAdapter {

    private final ArrayList readData;

    public ReadingAdapter(Map<String, String> readData) {
        this.readData = new ArrayList();
        this.readData.addAll(readData.entrySet());
    }

    @Override
    public int getCount() {
        return readData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) readData.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        final View item_view;
        if (convertView == null) {
            item_view = inflater.inflate(R.layout.layout_listview_item, container, false);
        } else {
            item_view = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        TextView name = (TextView) item_view.findViewById(R.id.tvItemName);
        TextView value = (TextView) item_view.findViewById(R.id.tvItemValue);

        name.setText(item.getKey());
        value.setText(item.getValue());

        return item_view;
    }
}
