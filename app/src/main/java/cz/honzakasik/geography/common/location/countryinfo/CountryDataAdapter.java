package cz.honzakasik.geography.common.location.countryinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import cz.honzakasik.geography.R;

public class CountryDataAdapter extends RecyclerView.Adapter<CountryDataAdapter.ViewHolder> {

    private final Logger logger = LoggerFactory.getLogger(CountryDataAdapter.class);

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView labelView;
        private TextView subtitleView;

        public ViewHolder(View itemView) {
            super(itemView);

            labelView = (TextView) itemView.findViewById(R.id.item_title);
            subtitleView = (TextView) itemView.findViewById(R.id.item_subtitle);
        }

        public TextView getLabelView() {
            return labelView;
        }

        public TextView getSubtitleView() {
            return subtitleView;
        }

    }

    private List<LabelValuePair> data;
    private Context context;

    public CountryDataAdapter(List<LabelValuePair> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View rowView = inflater.inflate(R.layout.list_item_two_lines, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LabelValuePair item = data.get(position);

        holder.getLabelView().setText(item.getLabel());

        if (item.getValue() == null) {
            throw new IllegalStateException("Value cannot be null!");
        }

        holder.getSubtitleView().setText((CharSequence) item.getValue());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Context getContext() {
        return context;
    }
}
