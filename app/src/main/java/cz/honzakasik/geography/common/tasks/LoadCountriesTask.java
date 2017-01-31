package cz.honzakasik.geography.common.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.location.country.CountryExtractor;

public class LoadCountriesTask extends AsyncTask<CountryExtractor, Integer, List<Country>> {

    private PostExecuteTask<List<Country>> postExecuteTask;

    public LoadCountriesTask(PostExecuteTask<List<Country>> postExecuteTask) {
        this.postExecuteTask = postExecuteTask;
    }

    @Override
    protected List<Country> doInBackground(CountryExtractor... params) {
        CountryExtractor extractor = params[0];
        List<Country> countries = null;
        try {
            countries = extractor.getAllCountries();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countries;
    }

    @Override
    protected void onPostExecute(List<Country> result) {
        super.onPostExecute(result);
        this.postExecuteTask.run(result);
    }

}
