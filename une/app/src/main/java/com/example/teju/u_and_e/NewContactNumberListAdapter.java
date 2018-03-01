package com.example.teju.u_and_e;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class NewContactNumberListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<NewNumber> mNumberList;

    NewContactNumberListAdapter(Context c) {
        mContext = c;
    }

    private boolean validatePhoneDetails(String phone){

        if(phone == null || phone.isEmpty() == true) return false;

        return utility.validatePhoneNumber(phone);
    }

    @Override
    public int getCount() {
        return mNumberList.size();
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
        final NewNumber number = mNumberList.get(i);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (list == null) {
            list = inflater.inflate(R.layout.new_contact_number_item, null);
        }

        final EditText etPhone = list.findViewById(R.id.etPhone);;
        final ImageView ivAddNumber = list.findViewById(R.id.ivAddNumber);;
        final ImageView ivRemoveNumber = list.findViewById(R.id.ivRemoveNumber);;

        final View row = list;

        etPhone.setText(number.getNumber());

        row.setTag(i);
        etPhone.setTag(i);
        ivAddNumber.setTag(i);
        ivRemoveNumber.setTag(i);

        if(i == mNumberList.size() - 1){
            etPhone.requestFocus();
        }

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String num = charSequence.toString();
                int index = (int) row.getTag();
                NewNumber numO = mNumberList.get(index);
                numO.setNumber(num);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        if(mNumberList.size() == (i+1)){
            ivAddNumber.setVisibility(View.VISIBLE);
            ivAddNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText etPhone;

                    etPhone = row.findViewById(R.id.etPhone);

                    if(false == validatePhoneDetails(etPhone.getText().toString())){
                        etPhone.setError("Enter Valid Number");
                        return;
                    }

                    NewNumber num = new NewNumber();
                    mNumberList.add(mNumberList.size(), num);
                    notifyDataSetChanged();
                }
            });
        }else{
            ivAddNumber.setVisibility(View.INVISIBLE);
        }

        if(mNumberList.size() == 1){
            ivRemoveNumber.setVisibility(View.GONE);
        }else{
            ivRemoveNumber.setVisibility(View.VISIBLE);
        }

        ivRemoveNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                Log.d("Remove Index", index+""+", "+etPhone.getText().toString());
                mNumberList.remove(index);
                notifyDataSetChanged();
            }
        });

        return list;
    }
}
