package com.webonise.urbanfarmers.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Webonise Lab on 04/09/15.
 */
public class DateUtil {

    private final String TAG = getClass().getName();
    private final String DATE_FORMAT_TO_DISPLAY = "dd MMMM, yyyy";
    private final String DATE_FORMAT_FROM_API = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String TIME_FORMAT_FOR_LIST = "hh:mm a";

    public String getFormattedDateFromTimeStamp(String timeStamp) {

        SimpleDateFormat receivedFormat = new SimpleDateFormat(DATE_FORMAT_FROM_API);
        SimpleDateFormat displayFormat = null;
        Date date = null;
        try {
            date = receivedFormat.parse(timeStamp);
            displayFormat = new SimpleDateFormat(DATE_FORMAT_TO_DISPLAY);
            return displayFormat.format(date);
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
        return null;
    }

    public long getTimeInMillisFromTimeStamp(String timeStamp) {

        long timeInMillis = 0l;

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_FROM_API);
        String dateInString = timeStamp;
        Date date = null;
        try {
            date = sdf.parse(dateInString);
            timeInMillis = date.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return timeInMillis;
    }


    /**
     * Function to get formatted date as sent in API
     *
     * @param dateInString - Time in milliseconds
     * @return String formattedTimeToDisplay
     */
    public String getFormattedTime(String dateInString) {
        SimpleDateFormat receivedFormat = new SimpleDateFormat(DATE_FORMAT_FROM_API);
        SimpleDateFormat deliverFormat = new SimpleDateFormat(TIME_FORMAT_FOR_LIST);
        Date date = null;
        try {
            date = receivedFormat.parse(dateInString);
            return deliverFormat.format(date);
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
        return null;
    }
    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
