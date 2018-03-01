package piyush.shop.com.salesorder;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by hp on 23-Jul-17.
 */
public class SuppliersReviewEditAdapter extends BaseAdapter {

    private Context mContext;
    public List<String> suppliersList;

    SuppliersReviewEditAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return suppliersList != null ? suppliersList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private void deleteOldPurchaseOrderPdf(String supplierName) {

        String purchaseOrderBasePath = SOUtility.BASE_FILE_PATH + "purchaseOrder/" + supplierName + ".pdf";
        Log.d("TAG", "purchaseOrderBasePath: " + purchaseOrderBasePath);

        File file = new File(purchaseOrderBasePath);
        file.delete();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.supplier_view_review_edit, parent, false);
        }

        TextView tvSerial = (TextView) row.findViewById(R.id.tvSerial);
        tvSerial.setText((position + 1) + ") ");

        TextView tvSupplierName = (TextView) row.findViewById(R.id.tvSupplierName);
        tvSupplierName.setText(suppliersList.get(position));

        final ImageView ivSupplierPOExport = (ImageView) row.findViewById(R.id.ivSupplierPOExport);
        ivSupplierPOExport.setTag(position);
        ivSupplierPOExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (suppliersList == null) return;

                int index = (int) ivSupplierPOExport.getTag();
                final String mSupplierName = suppliersList.get(index);

                SOUtility.mainActivity.disableSuppliersReviewEditList();

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteOldPurchaseOrderPdf(mSupplierName);
                        List<Product> mProductList = SOUtility.dbHandler.getAllProductsBySupplierName(mSupplierName, true);
                        (new DocumentWriter()).dumpSupplierPurchaseOrderIntoPdf(mSupplierName, mProductList);
                        for (Product product : mProductList) {
                            product.setGeneratedOrderQuantity(0);
                            product.setStock(0);
                            SOUtility.dbHandler.updateStockInProduct(product);
                            SOUtility.dbHandler.updateGeneratedOrderInProduct(product);
                        }

                        SOUtility.mainActivity.fetchSuppliersReviewListAndUpdateUI();
                    }
                }
                        , 20);
            }
        });

        return row;
    }
}