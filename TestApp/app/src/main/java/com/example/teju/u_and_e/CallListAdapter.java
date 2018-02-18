package com.example.teju.u_and_e;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CallListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<CallLogItem> mCallLogList;

    CallListAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mCallLogList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View list = view;
        CallLogItem callLogItem =mCallLogList.get(i);


        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (list == null) {
            list = inflater.inflate(R.layout.call_logs, null);
        }

        TextView tvContName = (TextView)list.findViewById(R.id.tvContName);
        TextView tvDate = (TextView)list.findViewById(R.id.tvTimeDate);
        TextView tvTimeDuration = (TextView)list.findViewById(R.id.tvTimeDuration);
        ImageView ivIncomingArrow = (ImageView)list.findViewById(R.id.ivIncomingArrow);

        Log.d(CallListAdapter.class.getSimpleName(), "callLogItem.getNumber(): "+callLogItem.getNumber());
        Log.d(CallListAdapter.class.getSimpleName(), "callLogItem.getDate(): "+callLogItem.getDate());
        Log.d(CallListAdapter.class.getSimpleName(), "getCallType: "+callLogItem.getCallType());

        String duration = "";
        int dur = Integer.parseInt(callLogItem.getCallDuration());
        int remain = dur % 60;
        duration =  ((remain>9)?"":"0") +remain + ""; // seconds

        dur = dur / 60;
        remain = dur % 60;
        duration = ((remain>9)?"":"0") +remain + ":" + duration;

        dur = dur / 60;
        remain = dur % 60;
        duration = ((remain>9)?"":"0") +remain + ":" + duration;

        tvContName.setText(callLogItem.getNumber());
        tvDate.setText(callLogItem.getDate());
        Log.d(CallListAdapter.class.getSimpleName(), "callLogItem.getCallDuration(): "+callLogItem.getCallDuration()+
        " cal: "+duration);

        tvTimeDuration.setText(duration);


        if(Integer.parseInt(callLogItem.getCallType()) == 3) {
            ivIncomingArrow.setImageResource(R.drawable.miscall_arrow);
        }else if(Integer.parseInt(callLogItem.getCallType()) == 1) {
            ivIncomingArrow.setImageResource(R.drawable.incoming_arrow);
        }else if(Integer.parseInt(callLogItem.getCallType()) == 2) {
            ivIncomingArrow.setImageResource(R.drawable.outgoing_arrow);
        }

        return list;
    }
}
