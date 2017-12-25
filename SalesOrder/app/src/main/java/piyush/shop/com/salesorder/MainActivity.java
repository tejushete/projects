package piyush.shop.com.salesorder;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnSearch, btnProduct, btnSupplier, btnPurchaseOrder, btnReviewEdit, btnExit;
    ListView lvSuppliers, lvSuppliersReviewEdit, lvProductReviewEdit;
    SupplierListAdapter mSuppliersAdapter;
    SuppliersReviewEditAdapter mSuppliersReviewEditAdapter;
    ProductListReviewEdit mProductListReviewEditAdapter;
    List<String> suppliersList = new ArrayList<String>();

    RelativeLayout rlSupplierReviewEditMainView, rlSupplierReviewEditProgressBar;

    String supplierSelectedReviewEdit = "";

    private void fetchSuppliersListFromDB() {
        suppliersList = SOUtility.dbHandler.getSuppliersList();
    }

    private void showSupplierListDialog() {
        Log.d("TAG", "on suppliers List  click");

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.supplier_list);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        lvSuppliers = (ListView) dialog.findViewById(R.id.lvSuppliers);
        mSuppliersAdapter = new SupplierListAdapter(MainActivity.this);
        mSuppliersAdapter.activity = MainActivity.this;
        mSuppliersAdapter.mSuppliersList = suppliersList;
        lvSuppliers.setAdapter(mSuppliersAdapter);

        ImageView ivAddNewSupplier = (ImageView) dialog.findViewById(R.id.ivAddNewSupplier);
        ivAddNewSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewSupplierDialog();
            }
        });

        TextView tvAddNewSupplier = (TextView) dialog.findViewById(R.id.tvAddNewSupplier);
        tvAddNewSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewSupplierDialog();
            }
        });

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean validateNewSuppliersField(Dialog view) {

        EditText etName, etAddress, etTelephone, etMobile, etEmail;
        etName = (EditText) view.findViewById(R.id.etName);
        etAddress = (EditText) view.findViewById(R.id.etAddress);
        etTelephone = (EditText) view.findViewById(R.id.etTelephone);
        etMobile = (EditText) view.findViewById(R.id.etMobile);
        etEmail = (EditText) view.findViewById(R.id.etEmail);

        boolean result = true;

        if (etName.getText().toString().isEmpty()) {
            etName.setError("Enter valid name.");
            result = false;
        }

        if (etName.getText().toString().equalsIgnoreCase("Default")) {
            etName.setError("Supplier can not be Default.");
            result = false;
        }

//        if (etAddress.getText().toString().isEmpty()) {
//            etAddress.setError("Enter valid address.");
//            result = false;
//        }
//
//        if (etTelephone.getText().toString().isEmpty()) {
//            etTelephone.setError("Enter valid telephone no.");
//            result = false;
//        }
//
//        if (etMobile.getText().toString().isEmpty()) {
//            etMobile.setError("Enter valid mobile no.");
//            result = false;
//        }
//
//        if (etEmail.getText().toString().isEmpty()) {
//            etEmail.setError("Enter valid email id.");
//            result = false;
//        }
//
        if (etTelephone.getText().toString().isEmpty() == false && android.util.Patterns.PHONE.matcher(etTelephone.getText().toString()).matches() == false) {
            etTelephone.setError("Enter valid telephone no.");
            result = false;
        }

        if (etMobile.getText().toString().isEmpty() == false && (new SOUtility()).isValidPhoneNo(etMobile.getText().toString()) == false) {
            etMobile.setError("Enter valid mobile no.");
            result = false;
        }

        if (etEmail.getText().toString().isEmpty() == false && (new SOUtility()).isValidEmailAddress(etEmail.getText().toString()) == false) {
            etMobile.setError("Enter valid email id.");
            result = false;
        }

        if (result == false) {
            Toast.makeText(MainActivity.this, "Please enter valid values.", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private void showNewSupplierDialog() {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.new_supplier);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateNewSuppliersField(dialog) == true) {
                    suppliersList.add(((EditText) dialog.findViewById(R.id.etName)).getText().toString());

                    Supplier supplier = new Supplier();
                    supplier.setName(((EditText) dialog.findViewById(R.id.etName)).getText().toString().trim());
                    supplier.setAddress(((EditText) dialog.findViewById(R.id.etAddress)).getText().toString().trim());
                    supplier.setTelephone(((EditText) dialog.findViewById(R.id.etTelephone)).getText().toString().trim());
                    supplier.setMobile(((EditText) dialog.findViewById(R.id.etMobile)).getText().toString().trim());
                    supplier.setEmail(((EditText) dialog.findViewById(R.id.etEmail)).getText().toString().trim());

                    SOUtility.dbHandler.addSupplier(supplier);

                    dialog.dismiss();

                    mSuppliersAdapter.notifyDataSetChanged();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void fetchProductListReviewEdit() {

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mProductListReviewEditAdapter.mProductList =
                        SOUtility.dbHandler.getAllProductsBySupplierName(supplierSelectedReviewEdit, true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProductListReviewEditAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        handler.postDelayed(runnable, 20);

    }

    private void showProductListReviewEditDialog() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.product_list_review_edit);

        dialog.setCancelable(false);

        lvProductReviewEdit = (ListView) dialog.findViewById(R.id.lvProductListReviewEdit);
        mProductListReviewEditAdapter = new ProductListReviewEdit(MainActivity.this);
        mProductListReviewEditAdapter.mProductList = null;
        lvProductReviewEdit.setAdapter(mProductListReviewEditAdapter);

        fetchProductListReviewEdit();

        ImageView ivBack = (ImageView) dialog.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private List<String> reviewEditSuppliersList = null;

    private void fetchReviewEditSupplierListByStock() {

        Log.d("TAG1", "fetchReviewEditSupplierListByStock");

        suppliersList = SOUtility.dbHandler.getSuppliersList();

        if (suppliersList == null) return;

        reviewEditSuppliersList = new ArrayList<String>();

        Log.d("TAG1", "suppliersList:" + suppliersList.toString());

        for (String mSupplierName : suppliersList) {

            long productsCount = SOUtility.dbHandler.countProductsBySupplierName(mSupplierName, true);

            Log.d("TAG1", "Supplier: " + mSupplierName + ", productsCount:" + productsCount);

            if (productsCount > 0) {
                reviewEditSuppliersList.add(mSupplierName);
            }
        }

        Log.d("TAG1", "reviewEditSuppliersList:" + reviewEditSuppliersList.toString());

        mSuppliersReviewEditAdapter.suppliersList = reviewEditSuppliersList;
        mSuppliersReviewEditAdapter.notifyDataSetChanged();
    }

    private void deleteOldPurchaseOrderPdfs() {

        String purchaseOrderBasePath = SOUtility.BASE_FILE_PATH + "purchaseOrder/";
        Log.d("TAG", "purchaseOrderBasePath: " + purchaseOrderBasePath);

        File dir = new File(purchaseOrderBasePath);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            Log.d("TAG", children + "");
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    private void showReviewEditSuppliersDialog() {
        Log.d("TAG", "On Click");

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.supplier_list_review_edit);

        dialog.setCancelable(false);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        lvSuppliersReviewEdit = (ListView) dialog.findViewById(R.id.lvSuppliersReviewEdit);
        mSuppliersReviewEditAdapter = new SuppliersReviewEditAdapter(MainActivity.this);
        mSuppliersReviewEditAdapter.suppliersList = null;
        lvSuppliersReviewEdit.setAdapter(mSuppliersReviewEditAdapter);

        rlSupplierReviewEditMainView = (RelativeLayout) dialog.findViewById(R.id.rlSupplierReviewEditMainView);
        rlSupplierReviewEditProgressBar = (RelativeLayout) dialog.findViewById(R.id.rlSupplierReviewEditProgressBar);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchReviewEditSupplierListByStock();
            }
        }, 20);

        ImageView ivExport = (ImageView) dialog.findViewById(R.id.ivExport);
        ivExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reviewEditSuppliersList == null) return;

                rlSupplierReviewEditMainView.setAlpha((float) 0.4);
                rlSupplierReviewEditMainView.setEnabled(false);
                rlSupplierReviewEditProgressBar.setVisibility(View.VISIBLE);

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteOldPurchaseOrderPdfs();
                        for (String mSupplierName : reviewEditSuppliersList) {
                            List<Product> mProductList = SOUtility.dbHandler.getAllProductsBySupplierName(mSupplierName, true);
                            (new DocumentWriter()).dumpSupplierPurchaseOrderIntoPdf(mSupplierName, mProductList);
                            for (Product product : mProductList) {
                                product.setGeneratedOrderQuantity(0);
                                product.setStock(0);
                                SOUtility.dbHandler.updateStockInProduct(product);
                                SOUtility.dbHandler.updateGeneratedOrderInProduct(product);
                            }
                        }

                        fetchReviewEditSupplierListByStock();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                rlSupplierReviewEditMainView.setAlpha(1);
                                rlSupplierReviewEditMainView.setEnabled(true);
                                rlSupplierReviewEditProgressBar.setVisibility(View.GONE);

                                Toast.makeText(MainActivity.this, "Order Generated @: " + SOUtility.BASE_FILE_PATH + "purchaseOrder/",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }, 20);
            }
        });

        lvSuppliersReviewEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "onItemClick:" + position);
                supplierSelectedReviewEdit = reviewEditSuppliersList.get(position);
                showProductListReviewEditDialog();
            }
        });

        dialog.show();
    }

    private static int REQUEST_PERMISSION_CODE = 11;

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        (new SOUtility()).createDataBaseDirectory();
                        SOUtility.dbHandler = new SODatabaseHandler(MainActivity.this);
                        SOUtility.dbHandler.getWritableDatabase();
                        fetchSuppliersListFromDB();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                    }
                }
            }
        }
    }

    public void disableSuppliersReviewEditList() {
        rlSupplierReviewEditMainView.setAlpha((float) 0.4);
        rlSupplierReviewEditMainView.setEnabled(false);
        rlSupplierReviewEditProgressBar.setVisibility(View.VISIBLE);
    }

    public void fetchSuppliersReviewListAndUpdateUI() {
        fetchReviewEditSupplierListByStock();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                rlSupplierReviewEditMainView.setAlpha(1);
                rlSupplierReviewEditMainView.setEnabled(true);
                rlSupplierReviewEditProgressBar.setVisibility(View.GONE);

                Toast.makeText(MainActivity.this, "Order Generated @: " + SOUtility.BASE_FILE_PATH + "purchaseOrder/",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        SOUtility.mainActivity = this;

        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = checkCallingOrSelfPermission(permission);
        Log.d("TAG", "Permission check result:" + res);
        if (res == PackageManager.PERMISSION_GRANTED) {
            (new SOUtility()).createDataBaseDirectory();
            SOUtility.dbHandler = new SODatabaseHandler(MainActivity.this);
            SOUtility.dbHandler.getWritableDatabase();
            fetchSuppliersListFromDB();
            SOUtility.dbHandler.addCategory("Other");
            SOUtility.dbHandler.addSubCategory("Other", "Other");

            Supplier defaultSupplier = new Supplier();
            defaultSupplier.setName("Default");
            defaultSupplier.setEmail("Default");
            defaultSupplier.setMobile("Default");
            defaultSupplier.setTelephone("Default");
            defaultSupplier.setAddress("Default");

            SOUtility.dbHandler.addSupplier(defaultSupplier);
        }

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnProduct = (Button) findViewById(R.id.btnProduct);
        btnSupplier = (Button) findViewById(R.id.btnSupplier);
        btnPurchaseOrder = (Button) findViewById(R.id.btnPurchaseOrder);
        btnReviewEdit = (Button) findViewById(R.id.btnReviewOrder);
        btnExit = (Button) findViewById(R.id.btnExit);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(MainActivity.this, Search.class);
                startActivity(searchIntent);
            }
        });

        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        btnSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics metrics = new DisplayMetrics();
                MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                float yInches= metrics.heightPixels/metrics.ydpi;
                float xInches= metrics.widthPixels/metrics.xdpi;
                double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                if (diagonalInches>=6.5){
                    showSupplierListDialog();
                }else{
                    Intent intent = new Intent(MainActivity.this,SupplierListForMobile.class);
                    startActivity(intent);
                }

            }
        });

        btnPurchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PurchaseOrderActivity.class);
                startActivity(intent);
            }
        });

        btnReviewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReviewEditSuppliersDialog();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}