package com.schedule.georgesamuel.schedule;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> taskList = new ArrayList<>();
    private Context context;
    private RecyclerViewClickListener clickListener;

    public TaskAdapter(List<Task> list, RecyclerViewClickListener listener){
        this.taskList = list;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view, clickListener);
        context = viewGroup.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Task task = taskList.get(i);

        if(task.getColor().equals("Red"))
            myViewHolder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red));
        else if(task.getColor().equals("Blue"))
            myViewHolder.cardView.setBackgroundColor(context.getResources().getColor(R.color.blue));
        else
            myViewHolder.cardView.setBackgroundColor(context.getResources().getColor(R.color.yellow));

        myViewHolder.title.setText(task.getTitle());
        myViewHolder.content.setText(task.getContent());
        myViewHolder.date.setText(task.getDate());
        myViewHolder.time.setText(task.getTime());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        TextView content;
        TextView date;
        TextView time;
        CardView cardView;
        private RecyclerViewClickListener clickListener;

        public MyViewHolder(View view, RecyclerViewClickListener recyclerViewClickListener){
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            cardView = (CardView) view.findViewById(R.id.cardView);

            clickListener = recyclerViewClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }
}
