/*package com.bandfitproject.board;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.R;
import com.bandfitproject.data.BoardData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bandfitproject.login.LoginActivity.user;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<BoardData> items;
    int item_layout;

    public RecyclerAdapter(Context context, List<BoardData> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_list_item,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BoardData item = items.get(position);
        String type = "[" + item.type + "]" ;
        holder.text_type.setText(type);
        holder.text_topic.setText(item.topic);
        holder.text_date.setText(item.date);
        String people = item.engaged_people + " / " + item.need_people;
        holder.text_people.setText(people);

        holder.engageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.engaging_board.contains(item.chat_room_name)) {
                } else {
                    user.engaging_board.add(item.chat_room_name);
                    BandFitDataBase.getInstance().push_Engaging_Board(user.engaging_board, item);
                }
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, item.topic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview) CardView cardView;
        @BindView(R.id.board_textview_type) TextView text_type;
        @BindView(R.id.board_textview_topic) TextView text_topic;
        @BindView(R.id.board_textview_date) TextView text_date;
        @BindView(R.id.board_textview_people) TextView text_people;
        @BindView(R.id.btn_engage) Button engageBtn;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
    public void addItem(BoardData add_item) {
        items.add(add_item);
        notifyItemInserted(items.size() - 1);
    }
}
*/