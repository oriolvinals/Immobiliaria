package com.uvic.ad32021.ovinals_hvezentan.Adaptadors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.uvic.ad32021.ovinals_hvezentan.Entitats.Propietat;
import com.uvic.ad32021.ovinals_hvezentan.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Adapter_Propietat extends ArrayAdapter<Propietat>{
    public int resourceId;
    public Adapter_Propietat (Context context, int resource, List<Propietat> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        Propietat actual = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(this.resourceId, parent, false);
        }

        TextView nom = (TextView)convertView.findViewById(R.id.propNom);
        TextView ubi = (TextView)convertView.findViewById(R.id.propUbi);
        TextView preu = (TextView)convertView.findViewById(R.id.propPreu);
        TextView area = (TextView)convertView.findViewById(R.id.propArea);

        nom.setText(actual.getNom());
        ubi.setText(actual.getUbicacio());
        preu.setText(String.valueOf(actual.getPreu()) + " €");
        area.setText(actual.getArea() + " m²");

        ImageView img = (ImageView)convertView.findViewById(R.id.imageView);
        try{
            byte [] encodeByte= Base64.decode(actual.getImatge(),Base64.DEFAULT);
            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            img.setImageBitmap(bitmap);
        }catch(Exception e){
            e.getMessage();
        }
        
        return convertView;
    }
}
