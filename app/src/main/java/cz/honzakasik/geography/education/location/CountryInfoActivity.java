package cz.honzakasik.geography.education.location;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.education.location.countryinfotabs.BasicCountryInfoFragment;
import cz.honzakasik.geography.education.location.countryinfotabs.gallery.CountryGalleryFragment;
import cz.honzakasik.geography.education.location.countryinfotabs.NationalSymbolsFragment;

public class CountryInfoActivity extends AppCompatActivity {

    private Logger logger = LoggerFactory.getLogger(CountryInfoActivity.class);

    private Country country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        this.country = EventBus.getDefault().removeStickyEvent(Country.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BasicCountryInfoFragment(), getString(R.string.basic_info));
        adapter.addFragment(new CountryGalleryFragment(), getString(R.string.gallery));
        adapter.addFragment(new NationalSymbolsFragment(), getString(R.string.national_symbols));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public Country getCountry() {
        return country;
    }
}
