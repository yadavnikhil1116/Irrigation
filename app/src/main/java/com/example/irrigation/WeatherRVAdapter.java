package com.example.irrigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherRVModel model = weatherRVModelArrayList.get(position);
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.iconimg);
        holder.temptxt.setText(model.getTemperature()+"℃");
        holder.windSpeed.setText(model.getWindSpeed()+"Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm a");
        try{
            Date t = input.parse(model.getTime());
            holder.timetxt.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView timetxt, temptxt, windSpeed;
        private ImageView iconimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timetxt = itemView.findViewById(R.id.timetxt);
            temptxt = itemView.findViewById(R.id.temptxt);
            windSpeed = itemView.findViewById(R.id.windSpeedtxt);
            iconimg = itemView.findViewById(R.id.iconimg);
        }
    }
}
