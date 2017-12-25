package piyush.shop.com.salesorder;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ProductActivity extends Activity {

    TextView tvNoProductsAdded;
    ListView lvProductList;
   public CustomProductListAdapter mProductAdapter;
   Dialog dialog;

    ExpandableListView lvCategoryList;
   public CompleteCategoryAdapter mCategoryAdapter;
    Button btnAddProduct, btnAddCategory;

    List<String> suppliersList;
    List<String> categoryList;
    List<String> subCategoryList;
    HashMap<String, List<String>> subCategoriesMap;

    static String lastImgDevicePath = null;

//    Bitmap btmHolder = null;

    ImageView ivProductDialogPhoto; //to be used in only image confirm dialog
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public void cleanupOldProductList() {

        //kill all threads
        //remove all bitmaps

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int size = mProductAdapter.mProductList != null ? mProductAdapter.mProductList.size() : 0;
                if (size == 0) return;

                Log.d("1", "1");
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
//                Log.d("1", "4");
//                if (mProductAdapter.imagesMap == null) return;
//
//                Log.d("5", "5");
//                mProductAdapter.imagesMap.clear();
            }

        });
//        Log.d("0", "0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                if (mProductAdapter.imagesMap == null || mProductAdapter.imagesMap.size() == 0) return;
//
//                Set<Integer> keys = mProductAdapter.imagesMap.keySet();
//                if (keys == null) return;
//
//                for (Integer key : keys) {
//                    Bitmap bmp = mProductAdapter.imagesMap.get(key);
//                    if (bmp != null) {
//                        bmp.recycle();
//                    }
//                }

                cleanupOldProductList();
//                mProductAdapter.imagesMap.clear();
            }
        });
    }

    private void fetchProductsListFromDb() {
        Handler handler = new Handler();

        cleanupOldProductList();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                mProductAdapter.mProductList = SOUtility.dbHandler.getAllProducts();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProductAdapter.notifyDataSetChanged();
                        int size = mProductAdapter.mProductList != null ? mProductAdapter.mProductList.size() : 0;
                        if (size > 0) {
                            tvNoProductsAdded.setVisibility(View.GONE);
                            lvProductList.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };

        handler.postDelayed(runnable, 100);
    }

    private void showMainCategoryInputDialog() {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.setContentView(R.layout.main_category_input_view);

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etCategory = (EditText) dialog.findViewById(R.id.etCategory);
                String etCategoryVal = etCategory.getText().toString().trim();
                if (etCategoryVal.isEmpty() == true) {
                    etCategory.setError("Enter valid category Name");
                    return;
                }

                categoryList.add(etCategoryVal);

                SOUtility.dbHandler.addCategory(etCategoryVal);
                mCategoryAdapter.notifyDataSetChanged();

                dialog.dismiss();
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

    private void showCapturedImageDialog(final String filePath) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.setContentView(R.layout.image_confirm_view);
        dialog.setCancelable(false);

        final ImageView ivConfirmImage = (ImageView) dialog.findViewById(R.id.ivConfirmImage);

        Glide.with(ProductActivity.this)
                .load(new File(filePath))
                .override(100,100)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(ivConfirmImage);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivConfirmImage.setImageBitmap(null);
                ivProductDialogPhoto.setImageBitmap(null);
                ivProductDialogPhoto.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnRetake = (Button) dialog.findViewById(R.id.btnRetake);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivConfirmImage.setImageBitmap(null);
                dialog.dismiss();

                SOUtility soUtility = new SOUtility();
//                if (btmHolder != null) {
//                    btmHolder.recycle();
//                    btmHolder = null;
//                }

                Glide.with(ProductActivity.this)
                        .load(new File(filePath))
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .override(100, 100)
                        .into(ivProductDialogPhoto);

                ivProductDialogPhoto.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivConfirmImage.setImageBitmap(null);
                dialog.dismiss();

                ivProductDialogPhoto.setImageBitmap(null);
//                if (btmHolder != null) {
//                    btmHolder.recycle();
//                    btmHolder = null;
//                }

                ivProductDialogPhoto.setVisibility(View.GONE);

//                if (SOUtility.tempBmp != null) {
//                    SOUtility.tempBmp.recycle();
//                    SOUtility.tempBmp = null;
//                }
            }

        });

        btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                ivProductDialogPhoto.setImageBitmap(null);
//                if (btmHolder != null) {
//                    btmHolder.recycle();
//                    btmHolder = null;
//                }

                ivProductDialogPhoto.setVisibility(View.GONE);

                if (SOUtility.tempBmp != null) {
                    SOUtility.tempBmp.recycle();
                    SOUtility.tempBmp = null;
                }
                showImageCapture();
            }
        });

        dialog.show();
    }

    private static final int SELECT_PICTURE = 1, SELECT_CAMERA_PICTURE = 2;

    private void showImageCapture() {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.setContentView(R.layout.take_image);

        ImageView ivGallery = (ImageView) dialog.findViewById(R.id.ivGallery);
        ImageView ivCamera = (ImageView) dialog.findViewById(R.id.ivCamera);

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
                dialog.dismiss();
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                String file_path = "";

                (new SOUtility()).createImageBaseDirectory();
                file_path = SOUtility.BASE_FILE_PATH + "tmp.jpg";

                File newfile = new File(file_path);
                try {
                    newfile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProductActivity.this, "No space left in device to save temp image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri outputFileUri;
                outputFileUri = Uri.fromFile(newfile);

                //to fix crash in Andoid N OS
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    outputFileUri = FileProvider.getUriForFile(ProductActivity.this, ProductActivity.this.getPackageName() + ".provider", newfile);
                }

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, SELECT_CAMERA_PICTURE);
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("TAG", "request code:" + requestCode + ", resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = ProductActivity.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);

            lastImgDevicePath = filePath;
//            Bitmap selectedphoto = BitmapFactory.decodeFile(filePath);
//
//            try {
//                ExifInterface exifInterface = new ExifInterface(filePath);
//                int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                int rotationInDegrees = (new SOUtility()).exifToDegrees(rotation);
//                Log.d("TAG", "rotation: " + rotationInDegrees);
//
//                if (rotationInDegrees != 0) {
//                    Matrix matrix = new Matrix();
//                    matrix.preRotate(rotationInDegrees);
//                    Bitmap bmp = Bitmap.createBitmap(selectedphoto, 0, 0, selectedphoto.getWidth(), selectedphoto.getHeight(), matrix, true);
//                    selectedphoto.recycle();
//                    selectedphoto = bmp;
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (SOUtility.tempBmp != null) {
                SOUtility.tempBmp.recycle();
                SOUtility.tempBmp = null;
            }

//            Log.d("TAG", SOUtility.tempBmp + "");

//            SOUtility.tempBmp = selectedphoto;
            cursor.close();

            showCapturedImageDialog(filePath);

        } else if (requestCode == SELECT_CAMERA_PICTURE && resultCode == RESULT_OK) {
            Log.d("TAG", "Got image from Camera");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (SOUtility.tempBmp != null) {
                                SOUtility.tempBmp.recycle();
                                SOUtility.tempBmp = null;
                            }

                            if (ivProductDialogPhoto != null) {
                                ivProductDialogPhoto.setImageBitmap(null);
                                ivProductDialogPhoto.setVisibility(View.GONE);
                            }

//                            SOUtility.tempBmp = (new SOUtility()).grabImage(ProductActivity.this, Uri.parse("file://" + SOUtility.BASE_FILE_PATH + "tmp.jpg"));
//
//                            try {
//                                ExifInterface exifInterface = new ExifInterface(SOUtility.BASE_FILE_PATH + "tmp.jpg");
//                                int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                                int rotationInDegrees = (new SOUtility()).exifToDegrees(rotation);
//                                Log.d("TAG", "rotation: " + rotationInDegrees);
//
//                                if (rotationInDegrees != 0) {
//                                    Matrix matrix = new Matrix();
//                                    matrix.preRotate(rotationInDegrees);
//                                    Bitmap bmp = Bitmap.createBitmap(SOUtility.tempBmp, 0, 0, SOUtility.tempBmp.getWidth(), SOUtility.tempBmp.getHeight(), matrix, true);
//
//                                    SOUtility.tempBmp.recycle();
//                                    SOUtility.tempBmp = bmp;
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }

                            lastImgDevicePath = SOUtility.BASE_FILE_PATH + "tmp.jpg";
                            showCapturedImageDialog(SOUtility.BASE_FILE_PATH + "tmp.jpg");
                        }
                    });
                }
            }).start();
        }
    }

    private boolean validateProductDetails(Dialog view) {

        boolean result = true;

        EditText etName, etDescription, etPrice, etMinOrderQ, etMinStockQ, etCostPrice, etSalePrice, etMRP;
        etName = (EditText) view.findViewById(R.id.etName);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        etPrice = (EditText) view.findViewById(R.id.etPrice);
        etMinOrderQ = (EditText) view.findViewById(R.id.etMinOrderQ);
        etMinStockQ = (EditText) view.findViewById(R.id.etMinStockQ);
        etCostPrice = (EditText) view.findViewById(R.id.etCostPrice);
        etSalePrice = (EditText) view.findViewById(R.id.etSalePrice);
        etMRP = (EditText) view.findViewById(R.id.etMRP);

        ImageView ivProductDialogPhoto = (ImageView) view.findViewById(R.id.ivProductDialogPhoto);

        int size = suppliersList != null ? suppliersList.size() : 0;
        if (size == 0) {
            Toast.makeText(ProductActivity.this, "No Suppliers Added Yet", Toast.LENGTH_SHORT).show();
            result = false;
            return result;
        }

        size = categoryList != null ? categoryList.size() : 0;
        if (size == 0) {
            Toast.makeText(ProductActivity.this, "No Categories Added Yet", Toast.LENGTH_SHORT).show();
            result = false;
            return result;
        }

        size = subCategoryList != null ? subCategoryList.size() : 0;
        if (size == 0) {
            Toast.makeText(ProductActivity.this, "No SubCategories Added Yet", Toast.LENGTH_SHORT).show();
            result = false;
            return result;
        }

        if (etName.getText().toString().isEmpty()) {
            etName.setError("Enter valid product name.");
            result = false;
        }

//        if (etDescription.getText().toString().isEmpty()) {
//            etDescription.setError("Enter valid product description.");
//            result = false;
//        }

//        if (etPrice.getText().toString().isEmpty()) {
//            etPrice.setError("Enter valid product price.");
//            result = false;
//        }

        if (etMinOrderQ.getText().toString().isEmpty()) {
            etMinOrderQ.setError("Enter valid min order quantity.");
            result = false;
        }

        if (etMinStockQ.getText().toString().isEmpty()) {
            etMinStockQ.setError("Enter valid min stock quantity.");
            result = false;
        }

        if (etCostPrice.getText().toString().isEmpty()) {
            etCostPrice.setError("Enter valid cost price.");
            result = false;
        }

//        if (etSalePrice.getText().toString().isEmpty()) {
//            etSalePrice.setError("Enter valid sale price.");
//            result = false;
//        }

//        if (etMRP.getText().toString().isEmpty()) {
//            etMRP.setError("Enter valid MRP Value.");
//            result = false;
//        }

        String msg = "Enter valid values.";

        if (result == false) {
            Toast.makeText(ProductActivity.this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        msg = "Select some photo for the product.";

        if (ivProductDialogPhoto.getVisibility() == View.GONE) {
            Toast.makeText(ProductActivity.this, msg, Toast.LENGTH_SHORT).show();
            result = false;
            return result;
        }

        return result;
    }

    boolean isEditModeEnabled;

    String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public void showProductDialog(final Product product, final int position) {
        Log.d("TAG", "on click");

        lastImgDevicePath = null;

        isEditModeEnabled = true;

        if (product == null) {
            isEditModeEnabled = false;
        }

        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.setContentView(R.layout.new_product);
        dialog.setCancelable(false);

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

        final Spinner spMainCategory, spSubCategory, spSupplier;
        spMainCategory = (Spinner) dialog.findViewById(R.id.spMainCategory);
        spSubCategory = (Spinner) dialog.findViewById(R.id.spSubCategory);

        spMainCategory.setTag(position);
        spSubCategory.setTag(position);

        spMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedValue = spMainCategory.getSelectedItem().toString();
                subCategoryList = subCategoriesMap.get(selectedValue);

                int productIndex = 0;

                try {
                    productIndex = (int) parent.getTag();
                }catch (Exception e){
                    e.printStackTrace();
                }

                ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(
                        ProductActivity.this, R.layout.spinner_item, subCategoryList);

                Product product = null;

                if(productIndex >= mProductAdapter.mProductList.size()) return;
                product = mProductAdapter.mProductList.get(productIndex);

                if (subCategoryList != null) {
                    spSubCategory.setAdapter(subCategoryAdapter);
                    spSubCategory.setSelection(subCategoryAdapter.getPosition(product.getSubCategory()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSupplier = (Spinner) dialog.findViewById(R.id.spSupplier);

        ArrayAdapter<String> suppliersAdapter = null;

        try {
            suppliersAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, suppliersList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> categoryAdapter = null;

        try {
            categoryAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, categoryList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> subCategoryAdapter = null;

        try {
            subCategoryAdapter = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, subCategoriesMap.get(categoryList.get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (categoryAdapter != null && categoryList != null) {
            spMainCategory.setAdapter(categoryAdapter);
        }

        if (subCategoryAdapter != null && subCategoryList != null) {
            spSubCategory.setAdapter(subCategoryAdapter);
        }

        if (suppliersAdapter != null && suppliersList != null) {
             spSupplier.setAdapter(suppliersAdapter);
        }

        ivProductDialogPhoto = (ImageView) dialog.findViewById(R.id.ivProductDialogPhoto);

        ImageView ivAddPhoto = (ImageView) dialog.findViewById(R.id.ivAddPhoto);
        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageCapture();
            }
        });

        if (isEditModeEnabled == true) {
            EditText etName, etDescription, etMinOrderQ, etMinStockQ, etCostPrice, etSalePrice, etMRP;
            etName = (EditText) dialog.findViewById(R.id.etName);
            etDescription = (EditText) dialog.findViewById(R.id.etDescription);
            etMinOrderQ = (EditText) dialog.findViewById(R.id.etMinOrderQ);
            etMinStockQ = (EditText) dialog.findViewById(R.id.etMinStockQ);
            etCostPrice = (EditText) dialog.findViewById(R.id.etCostPrice);
            etSalePrice = (EditText) dialog.findViewById(R.id.etSalePrice);
            etMRP = (EditText) dialog.findViewById(R.id.etMRP);

            etName.setText(" -- ");
            etDescription.setText(" -- ");
            etCostPrice.setText(" -- ");
            etMinOrderQ.setText(" -- ");
            etMinStockQ.setText(" -- ");
            etSalePrice.setText(" -- ");
            etMRP.setText(" -- ");

            etName.setEnabled(false);
            if (product.getName() != null && product.getName().isEmpty() == false) {
                etName.setText(product.getName());
            }

            if (product.getDescription() != null && product.getDescription().isEmpty() == false) {
                etDescription.setText(product.getDescription());
            }

            if (product.getCostPrice() != 0) {
                etCostPrice.setText(product.getCostPrice() + "");
            }

            if (product.getMinOrderQuantity() != 0) {
                etMinOrderQ.setText(product.getMinOrderQuantity() + "");
            }

            if (product.getMinStockQuantity() != 0) {
                etMinStockQ.setText(product.getMinStockQuantity() + "");
            }

            if (product.getSalePrice() != 0) {
                etSalePrice.setText(product.getSalePrice() + "");
            }

            if (product.getMRP() != 0) {
                etMRP.setText(product.getMRP() + "");
            }

            int pos = ((ArrayAdapter<String>) spMainCategory.getAdapter()).getPosition(product.getCategory().trim());
            Log.d("TAG", "old Category:" + product.getCategory().trim() + ", pos:" + pos);
            spMainCategory.setSelection(pos);

            pos = ((ArrayAdapter<String>) spSupplier.getAdapter()).getPosition(product.getSupplier().trim());
            spSupplier.setSelection(pos);

//            SOUtility.tempBmp = (new SOUtility()).grabImage(ProductActivity.this, Uri.parse("file://" + product.getPicPath()));
//            if (btmHolder != null) {
//                btmHolder.recycle();
//                btmHolder = null;
//            }
//            btmHolder = (new SOUtility()).getResizedBitmap(SOUtility.tempBmp, 100, 100);
//            ivProductDialogPhoto.setImageBitmap(btmHolder);

            Glide.with(ProductActivity.this)
                    .load(new File(product.getPicPath()))
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .override(100, 100)
                    .into(ivProductDialogPhoto);

            ivProductDialogPhoto.setVisibility(View.VISIBLE);
        }

        Button btnSave = (Button) dialog.findViewById(R.id.btnSaveProductDetails);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateProductDetails(dialog) == true) {

                    final Dialog view = dialog;
                    final RelativeLayout rlNewProductMainView = (RelativeLayout) view.findViewById(R.id.rlNewProductMainView);
                    final RelativeLayout rlNewProductProgressBarView
                            = (RelativeLayout) view.findViewById(R.id.rlNewProductProgressBarView);

                    rlNewProductMainView.setAlpha((float) 0.4);
                    rlNewProductMainView.setEnabled(false);
                    rlNewProductProgressBarView.setVisibility(View.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Product product2 = product;
                            if (isEditModeEnabled == false) {
                                product2 = new Product();
                            }

                            EditText etName, etDescription, etMinOrderQ, etMinStockQ, etCostPrice, etSalePrice, etMRP;
                            etName = (EditText) view.findViewById(R.id.etName);
                            etDescription = (EditText) view.findViewById(R.id.etDescription);
                            etMinOrderQ = (EditText) view.findViewById(R.id.etMinOrderQ);
                            etMinStockQ = (EditText) view.findViewById(R.id.etMinStockQ);
                            etCostPrice = (EditText) view.findViewById(R.id.etCostPrice);
                            etSalePrice = (EditText) view.findViewById(R.id.etSalePrice);
                            etMRP = (EditText) view.findViewById(R.id.etMRP);

                            product2.setName(etName.getText().toString());
                            product2.setDescription(etDescription.getText().toString());
                            product2.setPrice(0);

                            product2.setCategory(spMainCategory.getSelectedItem().toString().trim());
                            product2.setSubCategory(spSubCategory.getSelectedItem().toString().trim());

                            product2.setSupplier(spSupplier.getSelectedItem().toString().trim());

                            try {
                                product2.setMinOrderQuantity(Integer.parseInt(etMinOrderQ.getText().toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                product2.setMinStockQuantity(Integer.parseInt(etMinStockQ.getText().toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                product2.setCostPrice(Float.parseFloat(etCostPrice.getText().toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String salePrice = etSalePrice.getText().toString();

                            if (salePrice.isEmpty() == true) {
                                product2.setSalePrice(0);
                            } else {
                                try {
                                    product2.setSalePrice(Float.parseFloat(etSalePrice.getText().toString()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            String mrp = etMRP.getText().toString();
                            if (mrp.isEmpty() == true) {
                                product2.setMRP(0);
                            } else {
                                try {
                                    product2.setMRP(Float.parseFloat(etMRP.getText().toString()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (isEditModeEnabled == false) {
                                String productCode = SOUtility.dbHandler.getNextProductCode();
                                Log.d("TAG", "productCode:" + productCode);
                                product2.setCode(productCode);
                            }
//                            else{
//                                mProductAdapter.imagesMap.remove(position);
//                            }

                            //copy photo to a path.
                            String pic_path = null;

                            if (isEditModeEnabled == true) {
                                pic_path = product2.getPicPath();
                                File file = new File(pic_path);
                                pic_path = SOUtility.BASE_FILE_PATH + "pics/" + product2.getName() + "_" + product2.getCode() + "_" + getTimeStamp() + "_" + ".jpg";
                                pic_path = pic_path.replaceAll(" ", "_");
                                try {
                                    (new SOUtility()).copy(new File(product2.getPicPath()), new File(pic_path));
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                product2.setPicPath(pic_path);
                                try {
                                    Log.d("DeleteOp", "ret:" + file.getAbsoluteFile().delete());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                pic_path = SOUtility.BASE_FILE_PATH + "pics/" + product2.getName() + "_" + product2.getCode() + "_" + getTimeStamp() + "_" + ".jpg";
                                pic_path = pic_path.replaceAll(" ", "_");
                            }

//                            (new SOUtility()).writeBitMapIntoFile(SOUtility.tempBmp, pic_path);
                            Log.d("ProductInfo", "lastImgDevicePath:"+lastImgDevicePath+", pic_path:"+pic_path);

                            try {
                                if(lastImgDevicePath != null) {
                                    (new SOUtility()).copy(new File(lastImgDevicePath), new File(pic_path));
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            lastImgDevicePath = null;

                            if (SOUtility.tempBmp != null) {
                                SOUtility.tempBmp.recycle();
                                SOUtility.tempBmp = null;
                            }

                            product2.setPicPath(pic_path);

                            product2.dumpProduct();

                            if (isEditModeEnabled == true) {
                                SOUtility.dbHandler.updateProduct(product2);
                            } else {
                                SOUtility.dbHandler.addProduct(product2);
                            }


                            final Product finalProduct = product2;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ivProductDialogPhoto.setImageBitmap(null);

//                                    if (btmHolder != null) {
//                                        btmHolder.recycle();
//                                        btmHolder = null;
//                                    }

                                    if (isEditModeEnabled == false) {
                                        mProductAdapter.mProductList.add(finalProduct);
                                    }
                                    mProductAdapter.notifyDataSetChanged();

                                    int size = mProductAdapter.mProductList != null ? mProductAdapter.mProductList.size() : 0;
                                    if (size > 0) {
                                        tvNoProductsAdded.setVisibility(View.GONE);
                                        lvProductList.setVisibility(View.VISIBLE);
                                    }

                                    Log.d("TAG", finalProduct.toString());

                                    rlNewProductMainView.setAlpha(1);
                                    rlNewProductMainView.setEnabled(true);
                                    rlNewProductProgressBarView.setVisibility(View.GONE);

                                    dialog.dismiss();
                                }
                            });

                        }
                    }).start();
                }
            }
        });


        Button btnCancel = (Button) dialog.findViewById(R.id.btnDiscardProductDetails);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showCategoryDialog() {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(ProductActivity.this);
        dialog.setContentView(R.layout.add_category);
        dialog.setCancelable(false);

        lvCategoryList = (ExpandableListView) dialog.findViewById(R.id.lvCategory);

        mCategoryAdapter = new CompleteCategoryAdapter(ProductActivity.this);
        mCategoryAdapter.mCategoryList = categoryList;
        mCategoryAdapter.productActivityContext = this;
        mCategoryAdapter.mSubCategoryMap = subCategoriesMap;
        lvCategoryList.setAdapter(mCategoryAdapter);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageView ivAddNewCategory = (ImageView) dialog.findViewById(R.id.ivAddNewCategory);
        ivAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainCategoryInputDialog();
            }
        });

        TextView tvAddNewCategory = (TextView) dialog.findViewById(R.id.tvAddNewCategory);
        tvAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainCategoryInputDialog();
            }
        });

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_activity);

        SOUtility.productActivity = this;

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        (new SOUtility()).createPicsDirectory();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                suppliersList = SOUtility.dbHandler.getSuppliersList();
                categoryList = SOUtility.dbHandler.getCategories();

                subCategoriesMap = new HashMap<String, List<String>>();

                for (String category : categoryList) {
                    subCategoriesMap.put(category, SOUtility.dbHandler.getSubCategories(category));
                }
            }
        };

        handler.postDelayed(runnable, 10);

        btnAddProduct = (Button) findViewById(R.id.btnAddProduct);
        btnAddCategory = (Button) findViewById(R.id.btnAddCategory);

        TextView tvAddProduct = (TextView) findViewById(R.id.tvAddProduct);
        TextView tvAddCategory = (TextView) findViewById(R.id.tvAddCategory);

        tvAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = new DisplayMetrics();
                ProductActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                float yInches= metrics.heightPixels/metrics.ydpi;
                float xInches= metrics.widthPixels/metrics.xdpi;
                double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                if (diagonalInches>=6.5){
                    showProductDialog(null, 0);
                }else{
                   Intent intent = new Intent(ProductActivity.this,AddProductViewForMobile.class);
                    startActivity(intent);
                }

            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics metrics = new DisplayMetrics();
                ProductActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                float yInches= metrics.heightPixels/metrics.ydpi;
                float xInches= metrics.widthPixels/metrics.xdpi;
                double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);
                if (diagonalInches>=6.5){

                  showProductDialog(null, 0);


                }else{
                    Intent intent = new Intent(ProductActivity.this,AddProductViewForMobile.class);
                    startActivity(intent);
                }
            }
        });

        tvAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });

        tvNoProductsAdded = (TextView) findViewById(R.id.tvNoProducts);
        lvProductList = (ListView) findViewById(R.id.lvProducts);
        mProductAdapter = new CustomProductListAdapter(ProductActivity.this);

        mProductAdapter.mProductList = null;
        mProductAdapter.enableEditMode = true;
        lvProductList.setAdapter(mProductAdapter);

        lvProductList.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
//                Log.d(ProductActivity.class.getSimpleName(), "recycling view");
//                int index = (int) view.getTag();
//
//                final ImageView ivProduct = (ImageView) view.findViewById(R.id.ivProduct);
//
//                Thread thread = mProductAdapter.threadsMap.get(index);
//                if(thread != null){
//                    if(thread.isAlive()){
//                        thread.interrupt();
//                    }
//                    mProductAdapter.threadsMap.put(index, null);
//                }
//
//                if (ivProduct != null) {
//                    Bitmap bmp = mProductAdapter.imagesMap.get(index);
//                    if (bmp != null) {
//                        bmp.recycle();
//                    }
//                    ivProduct.setImageBitmap(null);
//
//                    mProductAdapter.imagesMap.put(index, null);
//                }
            }
        });

        fetchProductsListFromDb();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Product Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://piyush.shop.com.salesorder/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Product Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://piyush.shop.com.salesorder/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
