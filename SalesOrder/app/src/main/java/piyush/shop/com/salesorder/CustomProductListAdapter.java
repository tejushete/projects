package piyush.shop.com.salesorder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 23-Jul-17.
 */
class CustomProductListAdapter extends BaseAdapter {

    private Context mContext;
    public List<Product> mProductList;
//    public HashMap<Integer, Bitmap> imagesMap;
//    public HashMap<Integer, Thread> threadsMap;
    public boolean enableSeenMode = false;
    public boolean enableEditMode = false;
    public long oldSize = 0;
    public boolean isSearchActivity = false;

    CustomProductListAdapter(Context context) {
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

    private void showPasswordProtectionDialog(final Product product, int position, final Dialog parentDialog){
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.password_protection);
        dialog.show();

        Button btnPasswordSubmitOK, btnPasswordSubmitCANCEL;

        btnPasswordSubmitOK = (Button)dialog.findViewById(R.id.btnPasswordSubmitOK);
        btnPasswordSubmitCANCEL = (Button)dialog.findViewById(R.id.btnPasswordSubmitCANCEL);

        btnPasswordSubmitOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText etPasswordInput = (EditText)dialog.findViewById(R.id.etPasswordInput);
                String pwd = etPasswordInput.getText().toString();

                if(pwd.equals(SOUtility.PASSWORD)){
                    Toast.makeText(mContext, "Password verified successfully.", Toast.LENGTH_SHORT).show();

                    new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mProductList.remove(product);
                        SOUtility.dbHandler.removeProduct(product);
                        SOUtility.productActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                dialog.dismiss();
                                parentDialog.dismiss();
                            }
                        });
                    }
                }).start();


                }else{
                    etPasswordInput.setError("InCorrect Password.");
                    Toast.makeText(mContext, "Wrong Password Entered.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPasswordSubmitCANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void showProductDeleteConfirmDialog(final Product product, final int position) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.popup_ok_cancel);

        Button btnDialogOK = (Button) dialog.findViewById(R.id.btnDialogOK);
        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);

        final TextView tvDialogContent = (TextView) dialog.findViewById(R.id.tvDialogContent);
        tvDialogContent.setText(tvDialogContent.getText() + " " + product.getName() + " Product?");

        btnDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                        showPasswordProtectionDialog(product, position, dialog);
//                        mProductList.remove(product);
////                        threadsMap.remove(position);
////                        imagesMap.remove(position);
//                        SOUtility.dbHandler.removeProduct(product);
//
//                        SOUtility.productActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                notifyDataSetChanged();
//                                dialog.dismiss();
//                            }
//                        });
//                    }
////                }).start();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.product_view, parent, false);
        }

        final Product product = mProductList.get(position);

        TextView tvProductName = (TextView) row.findViewById(R.id.tvProductName);
        tvProductName.setText(product.getName());

        TextView tvProductDescription = (TextView) row.findViewById(R.id.tvProductDescription);
        tvProductDescription.setText(product.getDescription());

        TextView tvProductCode = (TextView) row.findViewById(R.id.tvProductCode);
        tvProductCode.setText("Code " + product.getCode());

        final ImageView ivProduct = (ImageView) row.findViewById(R.id.ivProduct);
        ivProduct.setImageResource(R.drawable.loading);

        final SOUtility soUtility = new SOUtility();
        Log.d("TAG", product.getPicPath());

        final ImageView ivRemoveProduct = (ImageView)row.findViewById(R.id.ivRemoveProduct);
        final ImageView ivEditProduct = (ImageView)row.findViewById(R.id.ivEditProduct);

        if(enableEditMode == true){
            ivRemoveProduct.setVisibility(View.VISIBLE);
            ivEditProduct.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvProductCode.getLayoutParams();
            params.addRule(RelativeLayout.LEFT_OF, ivEditProduct.getId());
            tvProductCode.setLayoutParams(params);
            tvProductCode.setVisibility(View.VISIBLE);
        }else{
            ivRemoveProduct.setVisibility(View.GONE);
            ivEditProduct.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvProductCode.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tvProductCode.setLayoutParams(params);
            tvProductCode.setVisibility(View.VISIBLE);
        }

        ivRemoveProduct.setTag(position);
        ivRemoveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) ivRemoveProduct.getTag();
                Product product1 = mProductList.get(index);
                showProductDeleteConfirmDialog(product1, position);
            }
        });

        ivEditProduct.setTag(position);
        ivEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   DisplayMetrics metrics = new DisplayMetrics();
                final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                float yInches= metrics.heightPixels/metrics.ydpi;
                float xInches= metrics.widthPixels/metrics.xdpi;
                double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                if (diagonalInches>=6.5){
                    int index = (int) ivEditProduct.getTag();
                    Product product1 = mProductList.get(index);
                    Log.d("<><>","product1"+product1);
                    SOUtility.productActivity.showProductDialog(product1, position);

                }else{
                   Intent intent = new Intent(mContext,AddProductViewForMobile.class);

                   intent.putExtra("EnableStatus",enableEditMode);
                   intent.putExtra("Position",position);
                   mContext.startActivity(intent);

                }


            }
        });

        Bitmap bmp = null;

        Glide.with(mContext)
                .load(new File(product.getPicPath()))
                .override(100,100)
                .into(ivProduct);
//        if(imagesMap == null){
//            imagesMap = new HashMap<Integer, Bitmap>();
//        }

//        bmp = imagesMap.get(position);
//
//        if(bmp != null){
//            ivProduct.setImageBitmap(bmp);
//        }
//        else{
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    long size = mProductList.size();
//                    if (size <= position) return;
//
//                    Product product = mProductList.get(position);
//                    Bitmap fullBmp = soUtility.grabImage(mContext, Uri.parse("file://" + product.getPicPath()));
//                    final Bitmap newBmp = soUtility.getResizedBitmap(fullBmp, 80, 80);
//
//                    imagesMap.put(position, newBmp);
//
//                    if(fullBmp != null) {
//                        fullBmp.recycle();
//                    }

//                    Activity activity;
//                    if (enableSeenMode == true) {
//                        activity = SOUtility.purchaseOrderActivity;
//                    } else {
//                        activity = SOUtility.productActivity;
//                    }
//
//                    if(isSearchActivity == true){
//                        activity = SOUtility.searchActivity;
//                    }

//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ivProduct.setImageBitmap(newBmp);
//                        }
//                    });
//                }
//            });

//            if(threadsMap == null){
//                threadsMap = new HashMap<Integer, Thread>();
//            }
//
//            threadsMap.put(position, thread);
//
//            thread.start();
//        }
        if(enableSeenMode == true){
            RelativeLayout rlMainProductView = (RelativeLayout)row.findViewById(R.id.rlMainProductView);
            if(product.getGeneratedOrderQuantity()>0){
                rlMainProductView.setBackgroundColor(Color.parseColor("#44880000"));
            }else{
                rlMainProductView.setBackgroundColor(Color.parseColor("#00000000"));
            }
        }

        row.setTag(position);

        return row;
    }
}