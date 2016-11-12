package com.example.murali.weathercheck;


import java.text.NumberFormat;
import  java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.TimeZone;

/**
 * Created by murali on 9/18/2016.
 */
public class Weather {

    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;


    public Weather(long timeStamp,double minTemp,double maxTemp,double humidity,String description,String iconName) {
// NumberFormat to formate double temperatures rounded to integers

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumIntegerDigits(0);

        this.dayOfWeek = currentTimeStampToday(timeStamp);
        this.minTemp = numberFormat.format(minTemp)+"°F";
        this.maxTemp = numberFormat.format(maxTemp)+"°F";
        this.humidity =numberFormat.getPercentInstance().format(humidity/100.0);
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/"+iconName+".png";

    }
    //convert time stamp to a days name(e.g.MONDAY,TUESDAY)
    private static String currentTimeStampToday(long timeStamp){

        Calendar calendar = Calendar.getInstance();//create calender
        calendar.setTimeInMillis(timeStamp*1000);//get time
        TimeZone tz= TimeZone.getDefault();//get device time zone
        //adjust time for device time zone
        calendar.add(Calendar.MILLISECOND,tz.getOffset(calendar.getTimeInMillis()));
        //simpledateformate that returns the days name
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        return dateFormat.format(calendar.getTime());

    }


}
