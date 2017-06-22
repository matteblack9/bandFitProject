package com.bandfitproject.chat;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bandfitproject.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.bandfitproject.login.LoginActivity.user;


public class ChatAdapter extends ArrayAdapter<ChatData> {
    private final static int TYPE_ME = 0;
    private final static int TYPE_ANOTHER = 1;
    private final static int TYPE_ADMIN = 2;
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());
    SoundPool pool;
    int ddok;

    public ChatAdapter(Context context, int resource) {
        super(context, resource);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        LayoutInflater inflator = LayoutInflater.from(getContext());

        if( convertView == null) {
            if(viewType == TYPE_ME) {
                convertView = setMySelfView(inflator);
            }
            // 추가됨 없애버리자 안되면 //
            else if(viewType == TYPE_ADMIN) {
                convertView = setADMINView(inflator);
            }
            else {
                convertView = setYourView(inflator);
            }
        }

        if (convertView.getTag() instanceof ViewHolderAnother) {
            ((ViewHolderAnother)convertView.getTag()).setData(position);
            /*if (viewType != TYPE_ANOTHER) {
                convertView = setYourView(inflator);
            }
            ((ViewHolderAnother)convertView.getTag()).setData(position);*/
        }
        else if (convertView.getTag() instanceof  ViewHolderADMIN) {
            ((ViewHolderADMIN)convertView.getTag()).setData(position);
        }
        else {
            ((ViewHolderOwn)convertView.getTag()).setData(position);
            /*if (viewType != TYPE_ME) {
                convertView = setMySelfView(inflator);
            }
            ((ViewHolderOwn)convertView.getTag()).setData(position);*/
        }

        return convertView;
    }

    private View setMySelfView(LayoutInflater inflater) {
        View convertView = inflater.inflate(R.layout.chat_own, null);
        ViewHolderOwn holder = new ViewHolderOwn();
        holder.bindView(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    private View setYourView(LayoutInflater inflater) {
        View convertView = inflater.inflate(R.layout.chat_you, null);
        ViewHolderAnother holder = new ViewHolderAnother();
        holder.bindView(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    private View setADMINView(LayoutInflater inflater) {
        View convertView = inflater.inflate(R.layout.chat_server, null);
        ViewHolderADMIN holder = new ViewHolderADMIN();
        holder.bindView(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    private class ViewHolderADMIN {
        private TextView mTxtMessage;
        private void bindView(View convertView) {
            mTxtMessage = (TextView)convertView.findViewById(R.id.server_txt_message);
        }
        private void setData(int posiotion) {
            ChatData chatData = getItem(posiotion) ;
            mTxtMessage.setText(chatData.message);
        }
    }

    private class ViewHolderOwn {
        private TextView mTxtMessage;
        private TextView mTxtTime;

        private void bindView(View convertView) {
            mTxtMessage = (TextView)convertView.findViewById(R.id.txt_message);;
            mTxtTime = (TextView)convertView.findViewById(R.id.txt_time);
        }
        private void setData(int posiotion) {
            ChatData chatData = getItem(posiotion) ;
            mTxtMessage.setText(chatData.message);
            mTxtTime.setText(mSimpleDateFormat.format(chatData.time));
        }
    }

    private class ViewHolderAnother {
        private TextView mTxtUserName;
        private TextView mTxtMessage;
        private TextView mTxtTime;

        private void bindView(View convertView) {
            mTxtUserName = (TextView)convertView.findViewById(R.id.txt_userName);
            mTxtMessage = (TextView)convertView.findViewById(R.id.txt_message);
            mTxtTime = (TextView)convertView.findViewById(R.id.txt_time);
        }
        private void setData(int posiotion) {
            ChatData chatData = getItem(posiotion) ;
            mTxtUserName.setText(chatData.userName);
            mTxtMessage.setText(chatData.message);
            mTxtTime.setText(mSimpleDateFormat.format(chatData.time));
        }
    }
    @Override
    public int getViewTypeCount() {
        return 3;
    }

    /**
     * 리스트뷰 아이디랑 로그인한 아이디가 일치하면 TYPE_ME
     * 일치하지 않으면 TYPE_ANOTHER
     */
    @Override
    public int getItemViewType(int position) {

        String getName = getItem(position).userName;
        if (getName.equals(user.getId()))
            return TYPE_ME;
        else if (getName.equals("ADMIN"))
            return TYPE_ADMIN;
        else {
//            pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//            ddok = pool.load(getContext(), R.raw.sound_out, 1);
//            pool.play(ddok, 1, 1, 0, 0, 1);
            return TYPE_ANOTHER;
        }
    }
}