package piyush.shop.com.salesorder;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewSupplierPageForMobile extends AppCompatActivity {

    String nameOfSupplier;
    boolean isEditModeOn = false;

    private boolean validateNewSupplierFielsds(){
        EditText etName, etAddress, etTelephone, etMobile, etEmail;
        etName = (EditText)findViewById(R.id.etName);
        etAddress = (EditText)findViewById(R.id.etAddress);
        etTelephone = (EditText)findViewById(R.id.etTelephone);
        etMobile = (EditText)findViewById(R.id.etMobile);
        etEmail = (EditText)findViewById(R.id.etEmail);

        boolean result = true;

        if (etName.getText().toString().isEmpty()) {
            etName.setError("Enter valid name.");
            result = false;
        }

        if (etName.getText().toString().equalsIgnoreCase("Default")) {
            etName.setError("Supplier can not be Default.");
            result = false;
        }
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
            Toast.makeText(NewSupplierPageForMobile.this, "Please enter valid values.", Toast.LENGTH_SHORT).show();
        }

        return result;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_supplier);

        ImageView ivClose = (ImageView) findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button btnSave = (Button)findViewById(R.id.btnSave);
        Button btnCancel = (Button)findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateNewSupplierFielsds() == true) {
                    ///suppliersList.add(((EditText)findViewById(R.id.etName)).getText().toString());

                    Supplier supplier = new Supplier();
                    EditText etName = (EditText)findViewById(R.id.etName);
                    supplier.setName(((EditText)findViewById(R.id.etName)).getText().toString().trim());
                    supplier.setAddress(((EditText)findViewById(R.id.etAddress)).getText().toString().trim());
                    supplier.setTelephone(((EditText)findViewById(R.id.etTelephone)).getText().toString().trim());
                    supplier.setMobile(((EditText)findViewById(R.id.etMobile)).getText().toString().trim());
                    supplier.setEmail(((EditText)findViewById(R.id.etEmail)).getText().toString().trim());

                    Log.d("<><>",supplier.getName()+", "+supplier.getAddress()+
                            ", "+supplier.getTelephone()+", "+supplier.getMobile()+
                            ", "+supplier.getEmail());

                    if(isEditModeOn == false) {
                        SOUtility.dbHandler.addSupplier(supplier);
                    }else{
                        SOUtility.dbHandler.updateSuppliersDetails(supplier);
                    }

                    nameOfSupplier= etName.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("suppliername", nameOfSupplier);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        isEditModeOn = intent.getBooleanExtra("isEditModeOn", false);

        if(isEditModeOn == true){
            String name = intent.getStringExtra("name");
            String address = intent.getStringExtra("address");
            String telephone = intent.getStringExtra("telephone");
            String mobile = intent.getStringExtra("mobile");
            String email = intent.getStringExtra("email");

            Log.d("<><>",name+", "+address+
                    ", "+telephone+", "+mobile+
                    ", "+email);

            ((EditText)findViewById(R.id.etName)).setText(name);
            findViewById(R.id.etName).setEnabled(false);
            ((EditText)findViewById(R.id.etAddress)).setText(address);
            ((EditText)findViewById(R.id.etTelephone)).setText(telephone);
            ((EditText)findViewById(R.id.etMobile)).setText(mobile);
            ((EditText)findViewById(R.id.etEmail)).setText(email);
        }
    }
    private static int REQUEST_PERMISSION_CODE = 11;

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
                        SOUtility.dbHandler = new SODatabaseHandler(NewSupplierPageForMobile.this);
                        SOUtility.dbHandler.getWritableDatabase();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                    }
                }
            }
        }
    }

}
