package piyush.shop.com.salesorder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PurchaseOrderActivity extends Activity implements View.OnClickListener {

    ListView lvProductListPurchaseOrder;
    CustomProductListAdapter mProductAdapter;
    CheckBox cbSupplier, cbCategory, cbSubCategory;
    Spinner spSupplier, spCategory, spSubCategory;

    ImageView ivPurchaseOrderClose;
    Button btnSavePurchaseOrder, btnCancelPurchaseOrder;

    FrameLayout rlProductPreviewPurchaseOrder;
    ImageView ivImagePreview;

    List<String> suppliersList;
    List<String> categoryList;
    List<String> subCategoryList;
    HashMap<String, List<String>> subCategoriesMap;

    boolean suppliersFilerSelected = true;

    void enableMainView() {
        Log.d("TAG", "Enabled Main View");
        RelativeLayout rlProductActionBar = (RelativeLayout) findViewById(R.id.rlProductActionBar);
        RelativeLayout rlFilter = (RelativeLayout) findViewById(R.id.rlFilter);
        ListView lvProductListPurchaseOrder = (ListView) findViewById(R.id.lvProductListPurchaseOrder);

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);

        ivBack.setEnabled(true);
        rlProductActionBar.setEnabled(true);
        rlFilter.setEnabled(true);
        lvProductListPurchaseOrder.setEnabled(true);
    }

    void disableMainView() {
        Log.d("TAG", "Disabled Main View");
        RelativeLayout rlProductActionBar = (RelativeLayout) findViewById(R.id.rlProductActionBar);
        RelativeLayout rlFilter = (RelativeLayout) findViewById(R.id.rlFilter);
        ListView lvProductListPurchaseOrder = (ListView) findViewById(R.id.lvProductListPurchaseOrder);

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);

        ivBack.setEnabled(false);
        rlProductActionBar.setEnabled(false);
        rlFilter.setEnabled(false);
        lvProductListPurchaseOrder.setEnabled(false);
    }

    private void loadProductsBySupplier(String supplierName) {
        Log.d("TAG", "loadProductsBySupplier: " + supplierName);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProductAdapter.notifyDataSetChanged();
            }
        });
        mProductAdapter.mProductList = SOUtility.dbHandler.getAllProductsBySupplierName(supplierName.trim(), false);
        Log.d("TAG", mProductAdapter.mProductList.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProductAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadProductsByCategory(String category, String subCategory) {
        Log.d("TAG", "loadProductsByCategory: category: " + category + " subCategory:" + subCategory);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProductAdapter.notifyDataSetChanged();
            }
        });
        mProductAdapter.mProductList = SOUtility.dbHandler.getAllProductsByCategoryName(category.trim(), subCategory.trim());
        Log.d("TAG", mProductAdapter.mProductList.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProductAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setSpinnerArrayAdapter() {

        final Spinner spMainCategory, spSubCategory, spSupplier;

        spMainCategory = (Spinner) findViewById(R.id.spCategory);
        spSubCategory = (Spinner) findViewById(R.id.spSubCategory);
        spSupplier = (Spinner) findViewById(R.id.spSupplier);

        ArrayAdapter<String> suppliersAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, suppliersList);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, categoryList);
        ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, subCategoriesMap.get(categoryList.get(0)));

        if (categoryAdapter != null && categoryList != null && categoryList.size() > 0) {
            spMainCategory.setAdapter(categoryAdapter);
        }

        if (subCategoryAdapter != null && subCategoryList != null && subCategoryList.size() > 0) {
            spSubCategory.setAdapter(subCategoryAdapter);
        }

        if (suppliersAdapter != null && suppliersList != null && suppliersList.size() > 0) {
            spSupplier.setAdapter(suppliersAdapter);
        }

        spSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedValue = spMainCategory.getSelectedItem().toString();
                subCategoryList = subCategoriesMap.get(selectedValue);

                ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(
                        PurchaseOrderActivity.this, R.layout.spinner_item, subCategoryList);

                if (subCategoryList != null) {
                    spSubCategory.setAdapter(subCategoryAdapter);
                }

                if(subCategoryList.size() == 0){
                    loadProducts();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void showProductInfoDialog(Product product) {

        final Dialog dialog = new Dialog(PurchaseOrderActivity.this);
        dialog.setContentView(R.layout.product_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvName, tvDescription, tvCategory, tvSubCategory, tvSupplier, tvMinOrder, tvMinStock, tvCostPrice, tvSalePrice, tvMRP;

        tvName = (TextView) dialog.findViewById(R.id.tvProductName);
        tvDescription = (TextView) dialog.findViewById(R.id.tvProductDescription);
        tvCostPrice = (TextView) dialog.findViewById(R.id.tvProductCostPrice);
        tvCategory = (TextView) dialog.findViewById(R.id.tvProductCategory);
        tvSubCategory = (TextView) dialog.findViewById(R.id.tvProductSubCategory);
        tvSupplier = (TextView) dialog.findViewById(R.id.tvProductSupplier);
        tvMinOrder = (TextView) dialog.findViewById(R.id.tvProductMinOrderQ);
        tvMinStock = (TextView) dialog.findViewById(R.id.tvProductMinStockQ);
        tvSalePrice = (TextView) dialog.findViewById(R.id.tvProductSalePrice);
        tvMRP = (TextView) dialog.findViewById(R.id.tvProductMRP);

        tvName.setText(" -- ");
        tvDescription.setText(" -- ");
        tvCostPrice.setText(" -- ");
        tvCategory.setText(" -- ");
        tvSubCategory.setText(" -- ");
        tvSupplier.setText(" -- ");
        tvMinOrder.setText(" -- ");
        tvMinStock.setText(" -- ");
        tvSalePrice.setText(" -- ");
        tvMRP.setText(" -- ");

        if (product.getName() != null && product.getName().isEmpty() == false) {
            tvName.setText(product.getName());
        }

        if (product.getDescription() != null && product.getDescription().isEmpty() == false) {
            tvDescription.setText(product.getDescription());
        }

        if (product.getCostPrice() != 0) {
            tvCostPrice.setText(product.getCostPrice() + "");
        }

        if (product.getCategory() != null && product.getCategory().isEmpty() == false) {
            tvCategory.setText(product.getCategory());
        }

        if (product.getSubCategory() != null && product.getSubCategory().isEmpty() == false) {
            tvSubCategory.setText(product.getSubCategory());
        }

        if (product.getSupplier() != null && product.getSupplier().isEmpty() == false) {
            tvSupplier.setText(product.getSupplier());
        }

        if (product.getMinOrderQuantity() != 0) {
            tvMinOrder.setText(product.getMinOrderQuantity() + "");
        }

        if (product.getMinStockQuantity() != 0) {
            tvMinStock.setText(product.getMinStockQuantity() + "");
        }

        if (product.getSalePrice() != 0) {
            tvSalePrice.setText(product.getSalePrice() + "");
        }

        if (product.getMRP() != 0) {
            tvMRP.setText(product.getMRP() + "");
        }

        Button btnProductInfoClose = (Button) dialog.findViewById(R.id.btnProductInfoClose);
        btnProductInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadProducts() {

        Log.d("TAG", "loadProducts:" + suppliersFilerSelected);

        cleanupOldProductList();

        getAvailableMemory();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (suppliersFilerSelected == true) {
                    Object selectedItem = spSupplier.getSelectedItem();
                    if (selectedItem != null) {
                        loadProductsBySupplier(selectedItem.toString().trim());
                    }
                } else {
                    Object selectedItem = spCategory.getSelectedItem();
                    if (selectedItem != null) {
                        loadProductsByCategory(spCategory.getSelectedItem() != null ?spCategory.getSelectedItem().toString().trim(): "",
                                spSubCategory.getSelectedItem() != null ? spSubCategory.getSelectedItem().toString().trim(): "");
                    }
                }
            }
        };

        handler.postDelayed(runnable, 10);
    }

    public void cleanupOldProductList() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int size = mProductAdapter.mProductList != null ? mProductAdapter.mProductList.size() : 0;
                if (size == 0) return;

//                Log.d("1", "1");
                if (mProductAdapter.mProductList != null) {
                    mProductAdapter.mProductList.clear();
                    mProductAdapter.notifyDataSetChanged();
                }

//                Log.d("1", "3");
//                HashMap<Integer, Thread> threads = mProductAdapter.threadsMap;
//                if (threads != null) {
//                    for (int i = 0; i < threads.size(); i++) {
//                        Thread thread = threads.get(i);
//
//                        Log.d("t", "" + i);
//                        if (thread != null && thread.isAlive()) {
//                            thread.interrupt();
//                        }
//                    }
//                    mProductAdapter.threadsMap.clear();
//                }
//
//                Log.d("5", "5");

//                if (mProductAdapter.imagesMap.size() != 0) {
//
//                    Set<Integer> keys = mProductAdapter.imagesMap.keySet();
//                    if (keys == null) return;
//
//                    for (Integer key : keys) {
//                        Bitmap bmp = mProductAdapter.imagesMap.get(key);
//                        if (bmp != null) {
//                            bmp.recycle();
//                        }
//                    }
//                }

//                if(mProductAdapter.imagesMap != null) {
//                    mProductAdapter.imagesMap.clear();
//                }
            }
//
        });
//        Log.d("0", "0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(PurchaseOrderActivity.class.getSimpleName(), "onDestroy");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                if (mProductAdapter.imagesMap.size() != 0) {
//
//                    Set<Integer> keys = mProductAdapter.imagesMap.keySet();
//                    if (keys == null) return;
//
//                    for (Integer key : keys) {
//                        Bitmap bmp = mProductAdapter.imagesMap.get(key);
//                        if (bmp != null) {
//                            bmp.recycle();
//                        }
//                    }
//                }
//                mProductAdapter.imagesMap.clear();

                cleanupOldProductList();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getAvailableMemory(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        double availableMegs = mi.availMem / 0x100000L;

//Percentage can be calculated for API 16+
        double percentAvail = mi.availMem / (double)mi.totalMem;

        Log.d(PurchaseOrderActivity.class.getSimpleName(), "Memory Available: "+(percentAvail*100)+"%");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order);

        SOUtility.purchaseOrderActivity = this;

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        suppliersList = SOUtility.dbHandler.getSuppliersList();
        categoryList = SOUtility.dbHandler.getCategories();

        subCategoriesMap = new HashMap<String, List<String>>();

        for (String category : categoryList) {
            subCategoriesMap.put(category, SOUtility.dbHandler.getSubCategories(category));
        }

        setSpinnerArrayAdapter();

        rlProductPreviewPurchaseOrder = (FrameLayout) findViewById(R.id.rlProductPreviewPurchaseOrder);
        ivPurchaseOrderClose = (ImageView) findViewById(R.id.ivCloseImagePreviewPurchaseOrder);
        ivImagePreview = (ImageView) findViewById(R.id.ivImagePreview);

        ivPurchaseOrderClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "called cancel");
                rlProductPreviewPurchaseOrder.setVisibility(View.GONE);
                ivImagePreview.setImageBitmap(null);

                if (SOUtility.tempBmp != null) {
                    SOUtility.tempBmp.recycle();
                    SOUtility.tempBmp = null;
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                mProductAdapter.notifyDataSetChanged();

                Log.d("TAG", "calling enable main view");
                enableMainView();
            }
        });

        btnSavePurchaseOrder = (Button) findViewById(R.id.btnSaveImPreview);
        btnCancelPurchaseOrder = (Button) findViewById(R.id.btnCancelImPreview);

        btnSavePurchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlProductPreviewPurchaseOrder.setVisibility(View.GONE);
            }
        });

        btnCancelPurchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlProductPreviewPurchaseOrder.setVisibility(View.GONE);
            }
        });

        lvProductListPurchaseOrder = (ListView) findViewById(R.id.lvProductListPurchaseOrder);
        mProductAdapter = new CustomProductListAdapter(PurchaseOrderActivity.this);
        mProductAdapter.mProductList = null;
        mProductAdapter.enableSeenMode = true;
        lvProductListPurchaseOrder.setAdapter(mProductAdapter);

        lvProductListPurchaseOrder.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
//                Log.d(PurchaseOrderActivity.class.getSimpleName(), "recycling view");
//                final ImageView ivProduct = (ImageView) view.findViewById(R.id.ivProduct);
//                if (ivProduct != null) {
//                    Bitmap bmp = ((BitmapDrawable) ivProduct.getDrawable()).getBitmap();
//                    if (bmp != null) {
//                        bmp.recycle();
//                    }
//                    ivProduct.setImageBitmap(null);

//                    int index = (int) view.getTag();
//                    mProductAdapter.imagesMap.put(index, null);
//                }
            }
        });

        cbSupplier = (CheckBox) findViewById(R.id.cbSuppliersFilter);
        cbCategory = (CheckBox) findViewById(R.id.cbCategoryFilter);
        cbSubCategory = (CheckBox) findViewById(R.id.cbSubCategoryFilter);

        spSupplier = (Spinner) findViewById(R.id.spSupplier);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        spSubCategory = (Spinner) findViewById(R.id.spSubCategory);

        cbSupplier.setOnClickListener(this);
        cbCategory.setOnClickListener(this);
        cbSubCategory.setOnClickListener(this);

        loadProducts();

        lvProductListPurchaseOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                disableMainView();

                final Product product = mProductAdapter.mProductList.get(position);
                String pic_path = product.getPicPath();

                Button btnSaveImPreview, btnCancelImPreview;
                btnSaveImPreview = (Button) findViewById(R.id.btnSaveImPreview);
                btnCancelImPreview = (Button) findViewById(R.id.btnCancelImPreview);
                final EditText etStockAvailable = (EditText) findViewById(R.id.etStockAvailable);

                final TextView tvDProductName, tvDProductDescription;

                tvDProductName = (TextView)findViewById(R.id.tvDProductName);
                tvDProductDescription = (TextView)findViewById(R.id.tvDProductDescription);

                tvDProductName.setText(product.getName());
                tvDProductDescription.setText(product.getDescription());

                etStockAvailable.setText("");
                Log.d("TAG", "purchase Order:" + product.getGeneratedOrderQuantity() + " stock:" + product.getStock());
                if (product.getGeneratedOrderQuantity() > 0) {
                    etStockAvailable.setText(product.getStock() + "");
                }

                etStockAvailable.setTag(position);

                final ImageView ivProductInfo = (ImageView) findViewById(R.id.ivProductInfo);
                ivProductInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int index = (int) etStockAvailable.getTag();
                        Product product = mProductAdapter.mProductList.get(index);

                        showProductInfoDialog(product);
                    }
                });

                final ImageView ivNextProduct = (ImageView) findViewById(R.id.ivNextProduct);
                final ImageView ivPrevProduct = (ImageView) findViewById(R.id.ivPrevProduct);

                ivPrevProduct.setVisibility(View.VISIBLE);
                ivNextProduct.setVisibility(View.VISIBLE);

                ivNextProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int index = (int) etStockAvailable.getTag();

                        Log.d("TAG", "ivNextProduct onClickListener index:" + index);

                        if (index == (mProductAdapter.mProductList.size() - 1)) {
                            Toast toast = Toast.makeText(PurchaseOrderActivity.this, "No Next products", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }

                        Product product = mProductAdapter.mProductList.get(index + 1);
                        String pic_path = product.getPicPath();

                        etStockAvailable.setTag(index + 1);
                        etStockAvailable.setText("");

                        tvDProductName.setText(product.getName());
                        tvDProductDescription.setText(product.getDescription());

                        if (product.getGeneratedOrderQuantity() > 0) {
                            etStockAvailable.setText(product.getStock() + "");
                        }

                        Bitmap bmp = (new SOUtility()).grabImage(PurchaseOrderActivity.this, Uri.parse("file://" + pic_path));
                        ivImagePreview.setImageBitmap(bmp);

                        if (SOUtility.tempBmp != null) {
                            SOUtility.tempBmp.recycle();
                            SOUtility.tempBmp = null;
                        }

                        SOUtility.tempBmp = bmp;

//                        ivPrevProduct.setVisibility(View.VISIBLE);
//                        ivNextProduct.setVisibility(View.VISIBLE);
//
//                        if(index == (mProductAdapter.mProductList.size()-2)){
//                            ivNextProduct.setVisibility(View.GONE);
//                        }else {
//                            ivNextProduct.setVisibility(View.VISIBLE);
//                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        View view = getCurrentFocus();
                        if (view != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });

                ivPrevProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int index = (int) etStockAvailable.getTag();

                        Log.d("TAG", "ivPrevProduct onClickListener index:" + index);

                        if (index == 0) {
                            Toast toast = Toast.makeText(PurchaseOrderActivity.this, "No Previous products", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }

                        Product product = mProductAdapter.mProductList.get(index - 1);
                        String pic_path = product.getPicPath();

                        etStockAvailable.setTag(index - 1);
                        etStockAvailable.setText("");

                        tvDProductName.setText(product.getName());
                        tvDProductDescription.setText(product.getDescription());

                        if (product.getGeneratedOrderQuantity() > 0) {
                            etStockAvailable.setText(product.getStock() + "");
                        }

                        Bitmap bmp = (new SOUtility()).grabImage(PurchaseOrderActivity.this, Uri.parse("file://" + pic_path));
                        ivImagePreview.setImageBitmap(bmp);

                        if (SOUtility.tempBmp != null) {
                            SOUtility.tempBmp.recycle();
                            SOUtility.tempBmp = null;
                        }

                        SOUtility.tempBmp = bmp;

                        ivPrevProduct.setVisibility(View.VISIBLE);
                        ivNextProduct.setVisibility(View.VISIBLE);

//                        if(index == 1){
//                            ivPrevProduct.setVisibility(View.GONE);
//                        }else {
//                            ivPrevProduct.setVisibility(View.VISIBLE);
//                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        View view = getCurrentFocus();
                        if (view != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });

                etStockAvailable.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        String etStockAvailableVal = etStockAvailable.getText().toString();

                        if (etStockAvailableVal.isEmpty() == true) return;

                        int index = (int) etStockAvailable.getTag();
                        Product product = mProductAdapter.mProductList.get(index);
                        product.setStock(Integer.parseInt(etStockAvailableVal));
                        SOUtility.dbHandler.updateStockInProduct(product);

                        //generate order
                        int generatedOrderQuantity = 0;
                        int diff = product.getMinStockQuantity() - product.getStock();

                        Log.d("TAG", "purchase Order: diff: " + diff + ", minStock: " + product.getMinStockQuantity() +
                                " stock: " + product.getStock());

                        if (diff > 0) {
                            float division = diff / product.getMinOrderQuantity();
                            division = division == 0 ? 1 : (float) Math.ceil(division);
                            generatedOrderQuantity = (int) (division * product.getMinOrderQuantity());
                        }

                        Log.d("TAG", "generatedOrderQuantity: " + generatedOrderQuantity);

                        product.setGeneratedOrderQuantity(generatedOrderQuantity);
                        SOUtility.dbHandler.updateGeneratedOrderInProduct(product);
                    }
                });

                btnSaveImPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String etStockAvailableVal = etStockAvailable.getText().toString().trim();

                        if (etStockAvailableVal.isEmpty() == true) {
                            etStockAvailable.setError("Stock value can not be empty.");
                            return;
                        }

//                        if (etStockAvailableVal.equals("0")) {
//                            etStockAvailable.setError("Stock value can not be 0.");
//                            return;
//                        }
//
//                        product.setStock(Integer.parseInt(etStockAvailableVal));
//                        SOUtility.dbHandler.updateStockInProduct(product);
//
//                        rlProductPreviewPurchaseOrder.setVisibility(View.GONE);
//                        ivImagePreview.setImageBitmap(null);
//
//                        if (SOUtility.tempBmp != null) {
//                            SOUtility.tempBmp.recycle();
//                            SOUtility.tempBmp = null;
//                        }
//
//                        //generate order
//                        int generatedOrderQuantity = 0;
//                        int diff = product.getMinStockQuantity() - product.getStock();
//
//                        Log.d("TAG", "purchase Order: diff: " + diff + ", minStock: " + product.getMinStockQuantity() +
//                                " stock: " + product.getStock());
//
//                        if (diff > 0) {
//                            float division = diff / product.getMinOrderQuantity();
//                            division = division == 0 ? 1 : (float) Math.ceil(division);
//                            generatedOrderQuantity = (int) (division * product.getMinOrderQuantity());
//                        }
//
//                        Log.d("TAG", "generatedOrderQuantity: " + generatedOrderQuantity);
//
//                        product.setGeneratedOrderQuantity(generatedOrderQuantity);
//                        SOUtility.dbHandler.updateGeneratedOrderInProduct(product);
//
//                        etStockAvailable.setText("");
//
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//                        View view = getCurrentFocus();
//                        if (view != null) {
//                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                        }
                    }
                });

                btnCancelImPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("TAG", "called cancel");
                        rlProductPreviewPurchaseOrder.setVisibility(View.GONE);
                        ivImagePreview.setImageBitmap(null);

                        if (SOUtility.tempBmp != null) {
                            SOUtility.tempBmp.recycle();
                            SOUtility.tempBmp = null;
                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        View view = getCurrentFocus();
                        if (view != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        mProductAdapter.notifyDataSetChanged();

                        Log.d("TAG", "calling enable main view");
                        enableMainView();
                    }
                });

                Bitmap bmp = (new SOUtility()).grabImage(PurchaseOrderActivity.this, Uri.parse("file://" + pic_path));
                ivImagePreview.setImageBitmap(bmp);

                if (SOUtility.tempBmp != null) {
                    SOUtility.tempBmp.recycle();
                    SOUtility.tempBmp = null;
                }

                SOUtility.tempBmp = bmp;
                rlProductPreviewPurchaseOrder.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (rlProductPreviewPurchaseOrder.getVisibility() == View.VISIBLE) {
            rlProductPreviewPurchaseOrder.setVisibility(View.GONE);
            ivImagePreview.setImageBitmap(null);

            if (SOUtility.tempBmp != null) {
                SOUtility.tempBmp.recycle();
                SOUtility.tempBmp = null;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            mProductAdapter.notifyDataSetChanged();

            enableMainView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

        cbSupplier.setChecked(false);
        cbCategory.setChecked(false);
        cbSubCategory.setChecked(false);

        spSupplier.setVisibility(View.GONE);
        spCategory.setVisibility(View.GONE);
        spSubCategory.setVisibility(View.GONE);

        Log.d("TAG", v.getId() + ", " + v);

        switch (v.getId()) {
            case R.id.cbSuppliersFilter:
                cbSupplier.setChecked(true);
                spSupplier.setVisibility(View.VISIBLE);
                suppliersFilerSelected = true;
                break;
            case R.id.cbCategoryFilter:
                cbCategory.setChecked(true);
                spCategory.setVisibility(View.VISIBLE);
                spSubCategory.setVisibility(View.VISIBLE);
                suppliersFilerSelected = false;
                break;
        }

        loadProducts();
    }
}