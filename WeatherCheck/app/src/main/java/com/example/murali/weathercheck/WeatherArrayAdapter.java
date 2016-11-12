package com.example.murali.weathercheck;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.TimeZone;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by murali on 9/19/2016.
 */
public class WeatherArrayAdapter extends ArrayAdapter<Weather>{

    private static class ViewHolder{

        ImageView ConditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView highTextView;
        TextView humidityTextView;

    }

    //stores already downloaded Bitmaps for reuse

    private Map<String,Bitmap> bitmaps = new HashMap<>();
    //constructor to initialize superclass inherited members
    public WeatherArrayAdapter(Context context, List<Weather> forecast){

        super(context,-1,forecast);// -1 indiactes custom view
    }

    //creates a custom views for the list view's item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the weather object for the specified listview position
        Weather day = getItem(position);
        //object that references list items views

        ViewHolder viewHolder;

        //check for resuable ViewHolder from a listview item that scrolled offscreen; otherwise create a new viewholder

        if(convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.list_item,parent,false);

            viewHolder.ConditionImageView = (ImageView)convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = (TextView)convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView)convertView.findViewById(R.id.minTextView);
            viewHolder.highTextView = (TextView)convertView.findViewById(R.id.maxTextView);
            viewHolder.humidityTextView =(TextView)convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        }else{//reuse existing viewholder stored as the list items tag

            viewHolder = (ViewHolder)convertView.getTag();

        }
        //if weather condition icon already downloaded,use it
        //otherwise,download icon in a seperate thread
        if(bitmaps.containsKey(day.iconURL)){
            viewHolder.ConditionImageView.setImageBitmap(bitmaps.get(day.iconURL));
        }else{
            //download display weather image
            new loadImageTask(viewHolder.ConditionImageView).execute(day.iconURL);
        }

        //get other data from weather object & place into views

        Context context =getContext();//for loading string resources

        viewHolder.dayTextView.setText(context.getString(R.string.day_description,day.dayOfWeek,day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp,day.minTemp));
        viewHolder.highTextView.setText(context.getString(R.string.high_temp,day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity,day.humidity));

        return convertView;

    }

    //AsyncTask to load weather condition icon in a seperate thread

    private class loadImageTask extends AsyncTask<String,Void,Bitmap> {

        private ImageView imageView;// displays the thumbnail
        //store the imageview on which to set the downloaded Bitmap

        public loadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        //load image params[0] is the string URL representing the image
        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap = null;
            HttpURLConnection httpURLConnection =null;

            try {

                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                try (InputStream inputStream = httpURLConnection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                httpURLConnection.disconnect();
            }


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
