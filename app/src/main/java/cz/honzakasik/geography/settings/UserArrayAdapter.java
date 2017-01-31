package cz.honzakasik.geography.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.honzakasik.geography.common.users.User;


public class UserArrayAdapter extends ArrayAdapter<User> {

    private LayoutInflater inflater;

    public UserArrayAdapter(Context context, List<User> objects) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.textView.setText(getItem(position).getNickName());

        return convertView;
    }

    static class ViewHolder {

        TextView textView;

        private ViewHolder(View rootView) {
            textView = (TextView) rootView.findViewById(android.R.id.text1);
        }
    }
}
