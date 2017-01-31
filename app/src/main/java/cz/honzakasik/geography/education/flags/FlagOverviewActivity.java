package cz.honzakasik.geography.education.flags;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;

public class FlagOverviewActivity extends AppCompatActivity {
    //TODO create activity containing scroll view of flags with name of state, after click the sliding layer will pop up

    private Logger logger = LoggerFactory.getLogger(FlagOverviewActivity.class);

    private LinearLayout flagContainer;

    private List<Country> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.overview_flags);
        this.flagContainer = (LinearLayout) this.findViewById(R.id.overview_flag_container);
        this.countries = ((App)getApplicationContext()).getCountries();
        Collections.shuffle(this.countries);
        fillScrollView();
    }

    private void fillScrollView() {
        for (Country country : this.countries) {
            this.flagContainer.addView(createFlagListItem(country));
        }
    }

    private FlagListItem createFlagListItem(Country country) {
        final LayoutInflater inflater = (LayoutInflater)getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FlagListItem flagListItem = (FlagListItem) inflater.inflate(R.layout.overview_flags_list_item, null);
        flagListItem.setCountry(country);
        return flagListItem;
    }
}
