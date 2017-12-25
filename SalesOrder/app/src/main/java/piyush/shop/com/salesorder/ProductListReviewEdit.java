package piyush.shop.com.salesorder;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * Created by hp on 23-Jul-17.
 */
public class ProductListReviewEdit extends BaseAdapter {

    private void showQuantityChangeDialog(final Product product) {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.change_product_quantity_dialog);

        final EditText etQuantity = (EditText)dialog.findViewById(R.id.etQuantity);
        etQuantity.setText(""+product.getGeneratedOrderQuantity());

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etQuantityVal = etQuantity.getText().toString().trim();

                if(etQuantityVal.trim().isEmpty() == true){
                    etQuantity.setError("Quantity can not be empty.");
                    return;
                }

                if(etQuantityVal.trim().equals("0") == true) {
                    etQuantity.setError("Quantity can not be 0.");
                    return;
                }

                product.setGeneratedOrderQuantity(Integer.parseInt(etQuantityVal));

                SOUtility.dbHandler.updateGeneratedOrderInProduct(product);

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public List<Product> mProductList;

    private Context mContext;

    ProductListReviewEdit(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mProductList != null ? mProductList.size() : 0;
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
            row = inflater.inflate(R.layout.single_product_review_edit, parent, false);
        }

        final Product product = mProductList.get(position);

        TextView tvSerial = (TextView) row.findViewById(R.id.tvSerial);
        tvSerial.setText((position + 1) + ") ");

        ImageView ivQuantityChange = (ImageView) row.findViewById(R.id.ivQuantityChange);
        ivQuantityChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityChangeDialog(product);
            }
        });

        TextView tvProductName = (TextView)row.findViewById(R.id.tvProductName);
        tvProductName.setText(product.getName());

        de.hdodenhof.circleimageview.CircleImageView ivProductPhoto = (de.hdodenhof.circleimageview.CircleImageView)row.findViewById(R.id.ivProductPhoto);

//        SOUtility soUtility = new SOUtility();
//        Bitmap fullBmp = soUtility.grabImage(mContext, Uri.parse("file://" + product.getPicPath()));
//        Bitmap newBmp = soUtility.getResizedBitmap(fullBmp, 100, 100);
//
//        fullBmp.recycle();

        Glide.with(mContext)
                .load(new File(product.getPicPath()))
                .override(100,100)
                .into(ivProductPhoto);

//        ivProductPhoto.setImageBitmap(newBmp);

        TextView tvProductCodeReviewEdit = (TextView)row.findViewById(R.id.tvProductCodeReviewEdit);
        tvProductCodeReviewEdit.setText("Code "+product.getCode());

        TextView tvProductPriceValue = (TextView)row.findViewById(R.id.tvProductPriceValue);
        tvProductPriceValue.setText(""+product.getCostPrice());

        TextView tvQuantityPriceValue = (TextView)row.findViewById(R.id.tvQuantityPriceVal);
        tvQuantityPriceValue.setText(""+product.getGeneratedOrderQuantity());

        return row;
    }
}