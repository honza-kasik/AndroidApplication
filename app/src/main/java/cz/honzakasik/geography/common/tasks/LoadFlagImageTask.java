package cz.honzakasik.geography.common.tasks;

import android.content.Context;
import android.graphics.Picture;
import android.os.AsyncTask;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cz.honzakasik.geography.App;
import cz.honzakasik.geography.common.location.country.Country;
import cz.honzakasik.geography.common.utils.PropUtils;

/**
 * Load the flag from the svg source image
 */
public class LoadFlagImageTask extends AsyncTask<Country, Integer, Picture> {

    private final Logger logger = LoggerFactory.getLogger(LoadFlagImageTask.class);

    private Context context;
    private PostExecuteTask<Picture> postExecuteTask;

    private Country country;

    public LoadFlagImageTask(Context context, PostExecuteTask<Picture> postExecuteTask) {
        this.context = context;
        this.postExecuteTask = postExecuteTask;
    }

    protected Picture doInBackground(Country... country) {
        this.country = country[0];
        App appContext = ((App)context.getApplicationContext());
        Picture cacheHit = appContext.getGlobalFlagCache().get(this.country);
        if (cacheHit != null) {
            return cacheHit;
        }
        String path = PropUtils.get("resources.games.flags.path") +
                this.country.getIso2().toLowerCase() + "." +
                PropUtils.get("resources.games.flags.file.suffix");
        logger.debug("Parsing flag image for {} from '{}'.", this.country.getName(), path);
        try {
            SVG svg = SVG.getFromAsset(context.getAssets(), path);
            return svg.renderToPicture();
        } catch (SVGParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Picture picture) {
        ((App)context.getApplicationContext()).getGlobalFlagCache().put(country, picture);
        this.postExecuteTask.run(picture);
    }
}
