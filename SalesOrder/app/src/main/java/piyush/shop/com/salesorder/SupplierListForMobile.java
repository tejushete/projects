package piyush.shop.com.salesorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SupplierListForMobile extends Activity {

    ListView lvSuppliers;
    SupplierListAdapter mSuppliersAdapter;
    List<String> suppliersList;
    String nameOfSupplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supplier_list);

        lvSuppliers = (ListView)findViewById(R.id.lvSuppliers);
        mSuppliersAdapter = new SupplierListAdapter(SupplierListForMobile.this);
        mSuppliersAdapter.activity = SupplierListForMobile.this;
        suppliersList = SOUtility.dbHandler.getSuppliersList();
        mSuppliersAdapter.mSuppliersList = suppliersList;
        lvSuppliers.setAdapter(mSuppliersAdapter);

        lvSuppliers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SupplierListForMobile.this,NewSupplierPageForMobile.class);

                Supplier supplierName = SOUtility.dbHandler.getSupplier(suppliersList.get(position));

                Log.d("<><>",supplierName.getName()+", "+supplierName.getAddress()+
                ", "+supplierName.getTelephone()+", "+supplierName.getMobile()+
                ", "+supplierName.getEmail());

                intent.putExtra("isEditModeOn", true);

                intent.putExtra("name", supplierName.getName());
                intent.putExtra("address", supplierName.getAddress());
                intent.putExtra("telephone", supplierName.getTelephone());
                intent.putExtra("mobile", supplierName.getMobile());
                intent.putExtra("email", supplierName.getEmail());

                startActivityForResult(intent,0);
            }
        });

        ImageView ivAddNewSupplier = (ImageView)findViewById(R.id.ivAddNewSupplier);
        ivAddNewSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupplierListForMobile.this,NewSupplierPageForMobile.class);
                startActivityForResult(intent,0);
            }
        });

        TextView tvAddNewSupplier = (TextView)findViewById(R.id.tvAddNewSupplier);
        tvAddNewSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupplierListForMobile.this,NewSupplierPageForMobile.class);
                startActivityForResult(intent,0);
            }
        });
        ImageView ivClose = (ImageView)findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0 && resultCode==RESULT_OK){
            nameOfSupplier=data.getStringExtra("suppliername");
            mSuppliersAdapter.mSuppliersList.add(nameOfSupplier);
            mSuppliersAdapter.notifyDataSetChanged();
        }

    }
}
