package piyush.shop.com.salesorder;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;

public class AddProductViewForMobile extends AppCompatActivity {

    TextView tvNoProductsAdded;
    ListView lvProductList;
    ExpandableListView lvCategoryList;

    Product productToModify = null;

    Button btnAddProduct, btnAddCategory;

    List<String> suppliersList;
    List<String> categoryList;
    boolean enableStatus;
    int position;
    List<String> subCategoryList;
    HashMap<String, List<String>> subCategoriesMap;

    static String lastImgDevicePath = null;
    ImageView ivProductDialogPhoto;
    private GoogleApiClient client;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showCapturedImageDialog(final String filePath) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(AddProductViewForMobile.this);
        dialog.setContentView(R.layout.image_confirm_view);
        dialog.setCancelable(false);

        final ImageView ivConfirmImage = (ImageView) dialog.findViewById(R.id.ivConfirmImage);

        Glide.with(AddProductViewForMobile.this)
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

                Glide.with(AddProductViewForMobile.this)
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
                ivProductDialogPhoto.setVisibility(View.GONE);
            }

        });


        btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                ivProductDialogPhoto.setImageBitmap(null);
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

        final Dialog dialog = new Dialog(AddProductViewForMobile.this);
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
                    Toast.makeText(AddProductViewForMobile.this, "No space left in device to save temp image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri outputFileUri;
                outputFileUri = Uri.fromFile(newfile);

                //to fix crash in Andoid N OS
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    outputFileUri = FileProvider.getUriForFile(AddProductViewForMobile.this, AddProductViewForMobile.this.getPackageName() + ".provider", newfile);
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
            Cursor cursor = AddProductViewForMobile.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);

            lastImgDevicePath = filePath;
            if (SOUtility.tempBmp != null) {
                SOUtility.tempBmp.recycle();
                SOUtility.tempBmp = null;
            }
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
                            lastImgDevicePath = SOUtility.BASE_FILE_PATH + "tmp.jpg";
                            showCapturedImageDialog(SOUtility.BASE_FILE_PATH + "tmp.jpg");
                        }
                    });
                }
            }).start();
        }
    }
    private boolean validateProductDetails() {

        boolean result = true;

        EditText etName, etDescription, etPrice, etMinOrderQ, etMinStockQ, etCostPrice, etSalePrice, etMRP;
        etName = (EditText)findViewById(R.id.etName);
        etDescription = (EditText)findViewById(R.id.etDescription);
        etPrice = (EditText)findViewById(R.id.etPrice);
        etMinOrderQ = (EditText)findViewById(R.id.etMinOrderQ);
        etMinStockQ = (EditText)findViewById(R.id.etMinStockQ);
        etCostPrice = (EditText)findViewById(R.id.etCostPrice);
        etSalePrice = (EditText)findViewById(R.id.etSalePrice);
        etMRP = (EditText)findViewById(R.id.etMRP);


        ImageView ivProductDialogPhoto = (ImageView)findViewById(R.id.ivProductDialogPhoto);

        int size = suppliersList != null ? suppliersList.size() : 0;
        if (size == 0) {
            Toast.makeText(AddProductViewForMobile.this, "No Suppliers Added Yet", Toast.LENGTH_SHORT).show();
            result = false;
            return result;
        }

        size = categoryList != null ? categoryList.size() : 0;
        if (size == 0) {
            Toast.makeText(AddProductViewForMobile.this, "No Categories Added Yet", Toast.LENGTH_SHORT).show();
            result = false;
            return result;
        }

        size = subCategoryList != null ? subCategoryList.size() : 0;
        if (size == 0) {
            Toast.makeText(AddProductViewForMobile.this, "No SubCategories Added Yet", Toast.LENGTH_SHORT).show();
            result = false;
            return result;
        }

        if (etName.getText().toString().isEmpty()) {
            etName.setError("Enter valid product name.");
            result = false;
        }
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
        String msg = "Enter valid values.";

        if (result == false) {
            Toast.makeText(AddProductViewForMobile.this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        msg = "Select some photo for the product.";

        if (ivProductDialogPhoto.getVisibility() == View.GONE) {
            Toast.makeText(AddProductViewForMobile.this, msg, Toast.LENGTH_SHORT).show();
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

    public void showProductView(final Product product, final int position) {
        Log.d("TAG", "on click");

        lastImgDevicePath = null;
        isEditModeEnabled = true;

        if (product== null) {
            isEditModeEnabled = false;
        }

        ImageView ivClose = (ImageView)findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Spinner spMainCategory, spSubCategory, spSupplier;
        spMainCategory = (Spinner)findViewById(R.id.spMainCategory);
        spSubCategory = (Spinner)findViewById(R.id.spSubCategory);

        spMainCategory.setTag(position);
        spSubCategory.setTag(position);

        spMainCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("<>", "onitemselected "+position);

                String selectedValue = spMainCategory.getSelectedItem().toString();
                subCategoryList = subCategoriesMap.get(selectedValue);
                Log.d("<>", "selected value:"+selectedValue+" sub:"+subCategoryList.toString());

                int productIndex = 0;

                try {
                    productIndex = (int) parent.getTag();
                }catch (Exception e){
                    e.printStackTrace();
                }

                ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(
                        AddProductViewForMobile.this, R.layout.spinner_item, subCategoryList);

                Product product = null;

                try {
                    Log.d("<>", "index:" + productIndex + ", subCategoryList.size:" + subCategoryList.size());
                    Log.d("<>", " SOUtility.productActivity.mProductAdapter.mProductList.size():" + SOUtility.productActivity.mProductAdapter.mProductList.size());
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    spSubCategory.setAdapter(subCategoryAdapter);
                }catch (Exception e){
                    e.printStackTrace();
                }

                if (subCategoryList.size() > 0) {
                    Log.d("<>", "TEJU, adapter:"+subCategoryAdapter.toString()+" sp:"+spSubCategory.toString());

                    if(SOUtility.productActivity.mProductAdapter.mProductList == null) return;
                    if(productIndex >= SOUtility.productActivity.mProductAdapter.mProductList.size()) return;

                    product = SOUtility.productActivity.mProductAdapter.mProductList.get(productIndex);

                    spSubCategory.setSelection(subCategoryAdapter.getPosition(product.getSubCategory()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSupplier = (Spinner)findViewById(R.id.spSupplier);
        ArrayAdapter<String> suppliersAdapter = null;

        try {
            Log.d("<>", "suppliers List adapter: "+ suppliersList.toString());
            suppliersAdapter = new ArrayAdapter<String>(
                    AddProductViewForMobile.this, R.layout.spinner_item, suppliersList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> categoryAdapter = null;

        try {
            categoryAdapter = new ArrayAdapter<String>(
                    AddProductViewForMobile.this, R.layout.spinner_item, categoryList);
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

        ivProductDialogPhoto = (ImageView)findViewById(R.id.ivProductDialogPhoto);

        ImageView ivAddPhoto = (ImageView)findViewById(R.id.ivAddPhoto);
        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageCapture();
            }
        });

        if (isEditModeEnabled == true) {
            EditText etName, etDescription, etMinOrderQ, etMinStockQ, etCostPrice, etSalePrice, etMRP;
            etName = (EditText)findViewById(R.id.etName);
            etDescription = (EditText)findViewById(R.id.etDescription);
            etMinOrderQ = (EditText)findViewById(R.id.etMinOrderQ);
            etMinStockQ = (EditText)findViewById(R.id.etMinStockQ);
            etCostPrice = (EditText)findViewById(R.id.etCostPrice);
            etSalePrice = (EditText)findViewById(R.id.etSalePrice);
            etMRP = (EditText)findViewById(R.id.etMRP);

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

            Glide.with(AddProductViewForMobile.this)
                    .load(new File(product.getPicPath()))
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .override(100, 100)
                    .into(ivProductDialogPhoto);

            ivProductDialogPhoto.setVisibility(View.VISIBLE);
        }
        Button btnSave = (Button)findViewById(R.id.btnSaveProductDetails);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateProductDetails()== true){
                    final RelativeLayout rlNewProductMainView = (RelativeLayout)findViewById(R.id.rlNewProductMainView);
                    final RelativeLayout rlNewProductProgressBarView
                            = (RelativeLayout)findViewById(R.id.rlNewProductProgressBarView);

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
                            etName = (EditText)findViewById(R.id.etName);
                            etDescription = (EditText)findViewById(R.id.etDescription);
                            etMinOrderQ = (EditText)findViewById(R.id.etMinOrderQ);
                            etMinStockQ = (EditText)findViewById(R.id.etMinStockQ);
                            etCostPrice = (EditText)findViewById(R.id.etCostPrice);
                            etSalePrice = (EditText)findViewById(R.id.etSalePrice);
                            etMRP = (EditText)findViewById(R.id.etMRP);

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
                                    if (isEditModeEnabled == false) {
                                        SOUtility.productActivity.mProductAdapter.mProductList.add(finalProduct);
                                    }
                                    SOUtility.productActivity.mProductAdapter.notifyDataSetChanged();

                                    int size = SOUtility.productActivity.mProductAdapter.mProductList != null ? SOUtility.productActivity.mProductAdapter.mProductList.size() : 0;
                                    if (size > 0) {
                                        SOUtility.productActivity.lvProductList.setVisibility(View.VISIBLE);
                                    }

                                    Log.d("TAG", finalProduct.toString());

                                    rlNewProductMainView.setAlpha(1);
                                    rlNewProductMainView.setEnabled(true);
                                    rlNewProductProgressBarView.setVisibility(View.GONE);
                                    finish();


                                }
                            });
                        }
                    }).start();
                }
            }
        });
        Button btnCancel = (Button)findViewById(R.id.btnDiscardProductDetails);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_product);
            getSupportActionBar().hide();
            ImageView ivClose = (ImageView)findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();

         enableStatus = intent.getBooleanExtra("EnableStatus", false);
         position = intent.getIntExtra("Position",0);

         if(enableStatus == true){
             productToModify = SOUtility.productActivity.mProductAdapter.mProductList.get(position);
         }

           /* ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });*/

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProductView(productToModify, position);
                        }
                    });
                }
            };

            handler.postDelayed(runnable, 0);
            TextView tvAddCategory = (TextView) findViewById(R.id.tvAddCategory);

           /* tvAddCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCategoryDialog();
                }
            });
*/
          /*  btnAddCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCategoryDialog();
                }
            });
*/
           // tvNoProductsAdded = (TextView) findViewById(R.id.tvNoProducts);

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





