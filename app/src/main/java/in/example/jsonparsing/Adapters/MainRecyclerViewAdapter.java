package in.example.jsonparsing.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.example.jsonparsing.Models.FeedbackModel;
import in.example.jsonparsing.R;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.MyViewHolder> {

    ItemClickListener mClickListener;
    ItemLongClickListner mLongLinster;
    Context context;
    List<FeedbackModel> list;

    public MainRecyclerViewAdapter(Context context, List<FeedbackModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_activity_recyclerview, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.item_main_question.setText(list.get(i).getTitle());
        myViewHolder.item_main_option_one.setText(list.get(i).getOptionOne());
        myViewHolder.item_main_option_two.setText(list.get(i).getOptionTwo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setLongClickListener(ItemLongClickListner listener) {
        this.mLongLinster = listener;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemLongClickListner {
        void onItemLongClick(View view, int position);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        TextView item_main_question, item_main_option_one, item_main_option_two;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_main_question = itemView.findViewById(R.id.item_main_question);
            item_main_option_one = itemView.findViewById(R.id.item_main_option_one);
            item_main_option_two = itemView.findViewById(R.id.item_main_option_two);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongLinster != null) {
                mLongLinster.onItemLongClick(v, getAdapterPosition());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
