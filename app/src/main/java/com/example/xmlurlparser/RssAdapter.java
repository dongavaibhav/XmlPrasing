package com.example.xmlurlparser;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RssAdapter extends RecyclerView.Adapter<RssAdapter.MyViewHolder> {
    ArrayList<RssItem> feedItems;
    Context context;
    public RssAdapter(Context context, ArrayList<RssItem> feedItems){
        this.feedItems=feedItems;
        this.context=context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.custum_row_news_item,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RssItem current = feedItems.get(position);
        holder.Title.setText(current.getTitle());
        holder.Date.setText(parseDateToddMMyyyy(current.getPubDate()));
        holder.Description.setText(Html.fromHtml(current.getDescription(),Html.FROM_HTML_MODE_COMPACT));
        holder.Link.setText(current.getLink());
//        holder.encloser.setText(current.getEnclosure());
        String imageUrl=current.getImageUrl();
        PicassoClient.downloadImage(context, imageUrl, holder.image);
//        Picasso.with(context).load(current.getImageUrl()).into(holder.image);
    }


    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "EEE, dd MMM yyyy hh:mm:ss Z";
        String outputPattern = "dd/MM/yyyy hh:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title,Link,Description,Date,encloser;
        ImageView image;
        CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            Title= (TextView) itemView.findViewById(R.id.title_text);
            Date= (TextView) itemView.findViewById(R.id.date_text);
            Description= (TextView) itemView.findViewById(R.id.description_text);
            Link= (TextView) itemView.findViewById(R.id.links);
//            encloser= (TextView) itemView.findViewById(R.id.encloser);
            image= (ImageView) itemView.findViewById(R.id.images);
            cardView= (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
