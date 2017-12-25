package piyush.shop.com.salesorder;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Search extends Activity implements View.OnClickListener {

    SearchView searchView;
    CheckBox cbSupplier, cbProducts;
    ListView lvSearchProduct, lvSearchSupplier;
    CustomProductListAdapter mProductsAdapter;
    SupplierListAdapter mSuppliersAdapter;

    boolean mSuppliersFilterSet = false;

    void showProductInfoDialog(Product product) {

        final Dialog dialog = new Dialog(Search.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeProductsListView();
    }

    void showSupplierInfoDialog(Supplier supplier) {

        final Dialog dialog = new Dialog(Search.this);
        dialog.setContentView(R.layout.supplier_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvName, tvAddress, tvTelephone, tvMobile, tvEmail;

        tvName = (TextView) dialog.findViewById(R.id.tvSupplierName);
        tvAddress = (TextView) dialog.findViewById(R.id.tvSupplierAddress);
        tvTelephone = (TextView) dialog.findViewById(R.id.tvSupplierTelephone);
        tvMobile = (TextView) dialog.findViewById(R.id.tvSupplierMobile);
        tvEmail = (TextView) dialog.findViewById(R.id.tvSupplierEmail);

        tvName.setText(" -- ");
        tvAddress.setText(" -- ");
        tvTelephone.setText(" -- ");
        tvMobile.setText(" -- ");
        tvEmail.setText(" -- ");

        if (supplier.getName() != null && supplier.getName().isEmpty() == false) {
            tvName.setText(supplier.getName());
        }

        if (supplier.getAddress() != null && supplier.getAddress().isEmpty() == false) {
            tvAddress.setText(supplier.getAddress());
        }

        if (supplier.getTelephone() != null && supplier.getTelephone().isEmpty() == false) {
            tvTelephone.setText(supplier.getTelephone() + "");
        }

        if (supplier.getMobile() != null && supplier.getMobile().isEmpty() == false) {
            tvMobile.setText(supplier.getMobile());
        }

        if (supplier.getEmail() != null && supplier.getEmail().isEmpty() == false) {
            tvEmail.setText(supplier.getEmail());
        }

        Button btnSupplierInfoClose = (Button) dialog.findViewById(R.id.btnSupplierInfoClose);
        btnSupplierInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SOUtility.searchActivity = this;

        setContentView(R.layout.activity_search);

        ImageView ivBack = (ImageView)findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.clearFocus();

        cbSupplier = (CheckBox) findViewById(R.id.cbSearchSupplier);
        cbProducts = (CheckBox) findViewById(R.id.cbSearchProducts);

        cbSupplier.setOnClickListener(this);
        cbProducts.setOnClickListener(this);

        lvSearchProduct = (ListView) findViewById(R.id.lvSearchProduct);
        mProductsAdapter = new CustomProductListAdapter(Search.this);
        mProductsAdapter.mProductList = null;
        mProductsAdapter.isSearchActivity = true;
        lvSearchProduct.setAdapter(mProductsAdapter);
//        lvSearchProduct.setRecyclerListener(new AbsListView.RecyclerListener() {
//            @Override
//            public void onMovedToScrapHeap(View view) {
//                Log.d(Search.class.getSimpleName(), "recycling view");
//                final ImageView ivProduct = (ImageView) view.findViewById(R.id.ivProduct);
//                if (ivProduct != null) {
//                    ivProduct.setImageBitmap(null);
//                }
//            }
//        });

        lvSearchProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = mProductsAdapter.mProductList.get(position);
                showProductInfoDialog(product);
            }
        });

        lvSearchSupplier = (ListView) findViewById(R.id.lvSearchSupplier);
        mSuppliersAdapter = new SupplierListAdapter(Search.this);
        mSuppliersAdapter.activity = Search.this;
        mSuppliersAdapter.mSuppliersList = null;
        mSuppliersAdapter.disableRemoveButton = true;
        lvSearchSupplier.setAdapter(mSuppliersAdapter);

        lvSearchSupplier.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String supplierName = mSuppliersAdapter.mSuppliersList.get(position);
                Supplier supplier = SOUtility.dbHandler.getSupplier(supplierName);
                showSupplierInfoDialog(supplier);
            }
        });

        showProductListView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (mSuppliersFilterSet == true) {
                    filterSuppliers(query);
                } else {
                    filterProducts(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        fetchAllProducts();
    }

    public void cleanupOldProductList() {

        //kill all threads
        //remove all bitmaps

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int size = mProductsAdapter.mProductList != null ? mProductsAdapter.mProductList.size() : 0;
                if (size == 0) return;

                Log.d("1", "1");
                if (mProductsAdapter.mProductList != null) mProductsAdapter.mProductList.clear();

//                Log.d("1", "3");
//                HashMap<Integer, Thread> threads = mProductsAdapter.threadsMap;
//                if (threads != null) {
//                    for (int i = 0; i < threads.size(); i++) {
//                        Thread thread = threads.get(i);
//
//                        Log.d("t", "" + i);
//                        if (thread != null && thread.isAlive()) {
//                            thread.interrupt();
//                        }
//                    }
//                    mProductsAdapter.threadsMap.clear();
//                }

//                Log.d("1", "4");
//                if (mProductsAdapter.imagesMap == null) return;
//
//                Log.d("5", "5");
//                mProductsAdapter.imagesMap.clear();
            }

        });
//        Log.d("0", "0");
    }

    public void removeProductsListView() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                if (mProductsAdapter.imagesMap == null || mProductsAdapter.imagesMap.size() == 0)
//                    return;
//
//                Set<Integer> keys = mProductsAdapter.imagesMap.keySet();
//                if (keys == null) return;
//
//                for (Integer key : keys) {
//                    Bitmap bmp = mProductsAdapter.imagesMap.get(key);
//                    if (bmp != null) {
//                        bmp.recycle();
//                    }
//                }

                cleanupOldProductList();
//                mProductsAdapter.imagesMap.clear();
                mProductsAdapter.notifyDataSetChanged();
                lvSearchProduct.setVisibility(View.GONE);
            }
        });
    }

    private void removeSuppliersListView() {

        if(mSuppliersAdapter.mSuppliersList != null) {
            mSuppliersAdapter.mSuppliersList.clear();
            mSuppliersAdapter.notifyDataSetChanged();
        }

        lvSearchSupplier.setVisibility(View.GONE);
    }

    private void showProductListView() {
        lvSearchSupplier.setVisibility(View.GONE);
        lvSearchProduct.setVisibility(View.VISIBLE);
    }

    private void showSuppliersListView() {
        lvSearchProduct.setVisibility(View.GONE);
        lvSearchSupplier.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.cbSearchProducts:
                mSuppliersFilterSet = false;
                cbSupplier.setChecked(false);
                cbProducts.setChecked(true);
                removeSuppliersListView();
                showProductListView();
                break;
            case R.id.cbSearchSupplier:
                cbProducts.setChecked(false);
                cbSupplier.setChecked(true);
                mSuppliersFilterSet = true;
                removeProductsListView();
                showSuppliersListView();
                break;
        }
    }

    List<Product> mAllProductList;
    List<String> mAllSuppliersList;

    private void hideProgressBar() {
        Log.d("TAG", "hiding progress bar");

        RelativeLayout rlSearchMainView = (RelativeLayout) findViewById(R.id.rlSearchMainView);
        RelativeLayout rlSearchProgressBarView = (RelativeLayout) findViewById(R.id.rlSearchProgressBarView);

        rlSearchMainView.setAlpha(1);
        rlSearchMainView.setEnabled(true);
        rlSearchProgressBarView.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        Log.d("TAG", "showing progress bar");
        RelativeLayout rlSearchMainView = (RelativeLayout) findViewById(R.id.rlSearchMainView);
        RelativeLayout rlSearchProgressBarView = (RelativeLayout) findViewById(R.id.rlSearchProgressBarView);

        rlSearchMainView.setAlpha((float) 0.6);
        rlSearchMainView.setEnabled(false);
        rlSearchProgressBarView.setVisibility(View.VISIBLE);
    }

    private void fetchAllProducts() {
        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAllProductList = SOUtility.dbHandler.getAllProducts();
                Log.d("TAG", "mProduct List Size:"+mAllProductList.size());
                fetchAllSuppliers();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        }).start();
    }

    private void fetchAllSuppliers() {
        mAllSuppliersList = SOUtility.dbHandler.getSuppliersList();
    }

    private void filterProducts(final String query) {

        if (query == null || query.isEmpty() == true) return;

        showProgressBar();
        cleanupOldProductList();

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mProductsAdapter.mProductList == null) {
                    mProductsAdapter.mProductList = new ArrayList<Product>();
                }

                int size = mAllProductList.size();
                for (int i = 0; i < size; i++) {
                    Product product = mAllProductList.get(i);
                    String name = product.getName();
                    Log.d(query, name);
                    SOUtility soUtility = new SOUtility();
                    if (soUtility.containsIgnoreCase(name, query) == true) {
                        Log.d("TAG", "adding product");
                        mProductsAdapter.mProductList.add(product);
                    }
                }

                Log.d("TAG", "mProduct list size:"+mProductsAdapter.mProductList.size());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProductListView();
                        mProductsAdapter.notifyDataSetChanged();
                        hideProgressBar();
                    }
                });
            }
        }).start();
    }

    private void filterSuppliers(final String query) {

        if (query == null || query.isEmpty() == true) return;

        showProgressBar();

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSuppliersAdapter.mSuppliersList == null) {
                    mSuppliersAdapter.mSuppliersList = new ArrayList<String>();
                }

                mSuppliersAdapter.mSuppliersList.clear();

                int size = mAllSuppliersList.size();

                Log.d("TAG", "supplierList size:"+size);

                for (int i = 0; i < size; i++) {
                    String supplierName = mAllSuppliersList.get(i);
                    Log.d(query, supplierName);
                    SOUtility soUtility = new SOUtility();
                    if (soUtility.containsIgnoreCase(supplierName, query) == true) {
                        Log.d("TAG", "added supplier");
                        mSuppliersAdapter.mSuppliersList.add(supplierName);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSuppliersListView();
                        mProductsAdapter.notifyDataSetChanged();
                        hideProgressBar();
                    }
                });
            }
        }).start();
    }
}