package piyush.shop.com.salesorder;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by hp on 22-Jul-17.
 */
public class SupplierListAdapter extends BaseAdapter {

    private Context mContext;
    public List<String> mSuppliersList;
    public boolean disableRemoveButton = false;

    SupplierListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mSuppliersList != null ? mSuppliersList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ;
            row = inflater.inflate(R.layout.single_supplier_view, parent, false);
        }

        TextView tvSerial = (TextView) row.findViewById(R.id.tvSerial);
        tvSerial.setText((position + 1) + ") ");

        TextView tvSupplierName = (TextView) row.findViewById(R.id.tvSupplierName);
        tvSupplierName.setText(mSuppliersList.get(position));

        ImageView ivRemoveSupplier = (ImageView) row.findViewById(R.id.ivRemoveSupplier);
        ivRemoveSupplier.setTag(position);
        if (mSuppliersList.get(position).equalsIgnoreCase("Default")) {
            ivRemoveSupplier.setVisibility(View.GONE);
        } else {
            ivRemoveSupplier.setVisibility(View.VISIBLE);
        }

        ivRemoveSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                String supplierName = mSuppliersList.get(index);
                if (supplierName.equalsIgnoreCase("Default")) {
                    Toast toast = Toast.makeText(mContext, "Can not delete Default Supplier", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                showDeleteMainCategoryDialog(supplierName);
            }
        });

        if(disableRemoveButton == true){
            ivRemoveSupplier.setVisibility(View.GONE);
            tvSupplierName.setTextColor(Color.parseColor("#ffffff"));
            tvSerial.setTextColor(Color.parseColor("#aaaaaa"));
        }else{
            tvSupplierName.setTextColor(Color.parseColor("#008800"));
            tvSerial.setTextColor(Color.parseColor("#000099"));
        }

        return row;
    }

    private void showDeleteMainCategoryDialog(final String supplierName) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.popup_ok_cancel);

        Button btnDialogOK = (Button) dialog.findViewById(R.id.btnDialogOK);
        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);

        final TextView tvDialogContent = (TextView) dialog.findViewById(R.id.tvDialogContent);
        tvDialogContent.setText(tvDialogContent.getText() + " " + supplierName + " supplier?");

        btnDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuppliersList.remove(supplierName);
                SOUtility.dbHandler.removeSupplier(supplierName);
                SOUtility.dbHandler.updateSupplierNameInProductFor(supplierName);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
