package cz.honzakasik.geography.common.utils;

import android.content.Context;

import java.lang.reflect.Field;

import cz.honzakasik.geography.R;
import cz.honzakasik.geography.common.location.country.Country;

public class ResHelper {

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getLocalizedCountryName(Country country, Context context) {
        int countryNameResId = getResId(country.getIso2().toLowerCase().concat(PropUtils
                                .get("resources.country.string.name.official.suffix")),
                R.string.class);
        return context.getResources().getString(countryNameResId);
    }

}
