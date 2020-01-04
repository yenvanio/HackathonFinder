package com.example.hackathonfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.ImageView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder> {

    public List<Event> events = new ArrayList<>();
    public Context context;
    private ShareActionProvider mShareActionProvider;
    private DBHelper db;
    private boolean isFav;

    RVAdapter(List<Event> events, Context context, DBHelper db) {
        this.events = events;
        this.context = context;
        this.db = db;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name, date, place, time, link;
        ImageView fav;

        EventViewHolder(View itemView) {
            super(itemView);
            final Context context = itemView.getContext();
            cv = (CardView) itemView.findViewById(R.id.cv);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, CardViewActivity.class);
                    i.putParcelableArrayListExtra("Events", (ArrayList<? extends Parcelable>) events);
                    i.putExtra("Position", getLayoutPosition());
                    context.startActivity(i);
                }
            });

            ImageView share = (ImageView) itemView.findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, events.get(getLayoutPosition()).title);
                    String shareMessage = events.get(getLayoutPosition()).url;
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    context.startActivity(Intent.createChooser(shareIntent, "Sharing via"));
                }
            });

            fav = (ImageView) itemView.findViewById(R.id.fav);
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (events.get(getLayoutPosition()).isFavorited) {
                        fav.setBackgroundResource(R.drawable.ic_favorite_border_gray_24dp);
                        events.get(getLayoutPosition()).isFavorited = false;
                        db.removeEvent(events.get(getLayoutPosition()).title);
                    } else {
                        fav.setBackgroundResource(R.drawable.ic_favorite_gray_24dp);
                        events.get(getLayoutPosition()).isFavorited = true;
                        db.addEvent(events.get(getLayoutPosition()));
                    }
                    notifyDataSetChanged();
                }
            });


            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);

            place = (TextView) itemView.findViewById(R.id.place);
            time = (TextView) itemView.findViewById(R.id.time);
            link = (TextView) itemView.findViewById(R.id.link);

        }

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        EventViewHolder evh = new EventViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        try {
            eventViewHolder.name.setText(events.get(i).title);
            eventViewHolder.date.setText(events.get(i).startDate + " - " + (events.get(i).endDate));
            eventViewHolder.place.setText(events.get(i).city);
            eventViewHolder.time.setText(events.get(i).length + " hours");
            eventViewHolder.link.setText(events.get(i).url);
            if (events.get(i).isFavorited) {
                eventViewHolder.fav.setBackgroundResource(R.drawable.ic_favorite_gray_24dp);
            }
        } catch (NullPointerException e) {
        }
        //eventViewHolder.placeIcon.setImageResource(events.get(i).);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}