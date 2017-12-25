package piyush.shop.com.salesorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hp on 23-Jul-17.
 */
public class SODatabaseHandler extends SQLiteOpenHelper {

    private static String DB_PATH = SOUtility.BASE_FILE_PATH + "/database/PurchaseOrder.db";

    static private String CATEGORY_TABLE_NAME = "Categories";
    static private String SUB_CATEGORY_TABLE_NAME = "SubCategories";
    static private String PRODUCT_TABLE_NAME = "Products";
    static private String SUPPLIERS_TABLE_NAME = "Suppliers";
    static private String LAST_PRODUCT_CODE_TABLE_NAME = "LastProductCode";

    static private String KEY_NAME = "name";

    //Product
    static private String KEY_PRICE = "price";
    static private String KEY_DESCRIPTION = "description";
    static private String KEY_CATEGORY = "category";
    static private String KEY_SUB_CATEGORY = "subCategory";
    static private String KEY_PHOTO_PATH = "picPath";
    static private String KEY_SUPPLIER = "supplier";
    static private String KEY_MIN_ORDER_QUANTITY = "minOrderQuantity";
    static private String KEY_MIN_STOCK_QUANTITY = "minStockQuantity";
    static private String KEY_COST_PRICE = "costPrice";
    static private String KEY_SALE_PRICE = "salePrice";
    static private String KEY_MRP = "MRP";
    static private String KEY_CODE = "Code";
    static private String KEY_STOCK = "Stock";
    static private String KEY_GENERATED_ORDER_QUANTITY = "GeneratedOrderQ";
    static private String KEY_SERIAL_NO = "SerialNo";

    //suppliers
    static private String KEY_ADDRESS = "address";
    static private String KEY_TELEPHONE = "telephone";
    static private String KEY_MOBILE = "mobile";
    static private String KEY_EMAIL = "email";

    //Product Unique code Table
    static private String KEY_BASE0 = "base0";
    static private String KEY_BASE1 = "base1";

    public SODatabaseHandler(Context context) {
        super(context, DB_PATH, null, 1);
        getReadableDatabase();
    }

    public void addProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_DESCRIPTION, product.getDescription());
        values.put(KEY_CATEGORY, product.getCategory().trim());
        values.put(KEY_SUB_CATEGORY, product.getSubCategory().trim());
        values.put(KEY_PHOTO_PATH, product.getPicPath());
        values.put(KEY_SUPPLIER, product.getSupplier().trim());
        values.put(KEY_MIN_ORDER_QUANTITY, product.getMinOrderQuantity());
        values.put(KEY_MIN_STOCK_QUANTITY, product.getMinStockQuantity());
        values.put(KEY_COST_PRICE, product.getCostPrice());
        values.put(KEY_SALE_PRICE, product.getSalePrice());
        values.put(KEY_MRP, product.getMRP());
        values.put(KEY_CODE, product.getCode());
        values.put(KEY_STOCK, product.getStock());
        values.put(KEY_GENERATED_ORDER_QUANTITY, product.getGeneratedOrderQuantity());

        Log.d("TAG", "product table addition result: " + db.insert(PRODUCT_TABLE_NAME, null, values));

        db.close();
    }

    public void updateStockInProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " + KEY_STOCK + " = '" + product.getStock() +
                "' WHERE " + KEY_CODE + "= ?" + " and " + KEY_NAME + "= ?";

        db.execSQL(strSQL, new String[]{product.getCode(), product.getName()});
        db.close();
    }

    public void updateSuppliersDetails(Supplier supplier){

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + SUPPLIERS_TABLE_NAME +
                " SET " + KEY_ADDRESS + " =  ?, "+
                KEY_TELEPHONE +" = ?, "+
                KEY_MOBILE +" = ?, "+
                KEY_EMAIL +" = ?  "+
                " WHERE " + KEY_NAME + "= ?";

        try {
            db.execSQL(strSQL, new String[]{
                    supplier.getAddress(),
                    supplier.getTelephone(),
                    supplier.getMobile(),
                    supplier.getEmail(),
                    supplier.getName()});
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void updateProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME +
                " SET " + KEY_STOCK + " = '" + product.getStock() +"' , "+
                KEY_NAME +" ='"+product.getName().trim()+"' , "+
                KEY_DESCRIPTION +" ='"+product.getDescription().trim()+"' , "+
                KEY_SUPPLIER +" ='"+product.getSupplier().trim()+"' , "+
                KEY_CATEGORY +" ='"+product.getCategory().trim()+"' , "+
                KEY_SUB_CATEGORY +" ='"+product.getSubCategory().trim()+"' , "+
                KEY_PRICE +" ='"+product.getPrice()+"' , "+
                KEY_MIN_ORDER_QUANTITY +" ='"+product.getMinOrderQuantity()+"' , "+
                KEY_MIN_STOCK_QUANTITY +" ='"+product.getMinStockQuantity()+"' , "+
                KEY_COST_PRICE +" ='"+product.getCostPrice()+"' , "+
                KEY_SALE_PRICE +" ='"+product.getSalePrice()+"' , "+
                KEY_PHOTO_PATH +" ='"+product.getPicPath()+"' , "+
                KEY_MRP +" ='"+product.getMRP()+"' , "+
                KEY_GENERATED_ORDER_QUANTITY +" ='"+product.getGeneratedOrderQuantity()+"'  "+
                " WHERE " + KEY_CODE + "= '" + product.getCode()+"'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void updateCategoryInProducts(String category, String newCategory) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " + KEY_CATEGORY + " = '" + newCategory.trim() + "' " +
                " WHERE " + KEY_CATEGORY + "= '" + category.trim() + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void updateSubCategoryInProducts(String subCategory, String newSubCategory) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " +
                KEY_SUB_CATEGORY + " = '" + newSubCategory.trim() + "' " +
                " WHERE " + KEY_SUB_CATEGORY + "= '" + subCategory.trim() + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    private void updateOtherCategoryVal() {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " +
                KEY_CATEGORY + " = '" + "Other" + "' " +
                " WHERE " + KEY_SUB_CATEGORY + "= '" + "Other" + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    private void updateOtherSubCategoryVal() {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " +
                KEY_SUB_CATEGORY + " = '" + "Other" + "' " +
                " WHERE " + KEY_CATEGORY + "= '" + "Other" + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void removeCategoryInProducts(String category) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " +
                KEY_SUB_CATEGORY + " = '" + "Other" + "' " +
                " WHERE " + KEY_CATEGORY + "= '" + category + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();

        updateOtherCategoryVal();
    }

    public void removeSubCategoryInProducts(String subCategory) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " + KEY_CATEGORY + " = '" + "Other" + "' " +
                " WHERE " + KEY_SUB_CATEGORY + "= '" + subCategory.trim() + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
        updateOtherSubCategoryVal();
    }

    public void updateSupplierNameInProductFor(String supplierName) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " + KEY_SUPPLIER + " = '" + "Default" + "'"
                + " WHERE " + KEY_SUPPLIER + "= '" + supplierName.trim() + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void updateGeneratedOrderInProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE " + PRODUCT_TABLE_NAME + " SET " + KEY_GENERATED_ORDER_QUANTITY + " = '" + product.getGeneratedOrderQuantity() +
                "' WHERE " + KEY_CODE + "= '" + product.getCode() + "' and " + KEY_NAME + "= '" + product.getName() + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public List<Product> getAllProducts() {

        List<Product> productList = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + PRODUCT_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            return null;
        }

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setName(cursor.getString(0));
                product.setPrice(cursor.getFloat(1));
                product.setDescription(cursor.getString(2));
                product.setCategory(cursor.getString(3));
                product.setSubCategory(cursor.getString(4));
                product.setPicPath(cursor.getString(5));
                product.setSupplier(cursor.getString(6));
                product.setMinOrderQuantity(cursor.getInt(7));
                product.setMinStockQuantity(cursor.getInt(8));
                product.setCostPrice(cursor.getFloat(9));
                product.setSalePrice(cursor.getFloat(10));
                product.setMRP(cursor.getFloat(11));
                product.setCode(cursor.getString(12));
                product.setStock(cursor.getInt(13));
                product.setGeneratedOrderQuantity(cursor.getInt(14));
                productList.add(product);
            } while (cursor.moveToNext());
        }

        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                String lhsCode, rhsCode;
                lhsCode = lhs.getCode().substring(1);
                rhsCode = rhs.getCode().substring(1);

                int iLhsCode, iRhsCode;

                iLhsCode = Integer.parseInt(lhsCode);
                iRhsCode = Integer.parseInt(rhsCode);

                return (iLhsCode > iRhsCode)?1:0;            }
        });

        return productList;
    }

    public long countProductsBySupplierName(String supplierName, boolean stockWise) {

        long count = 0;
        String selectQuery = "SELECT  * FROM " + PRODUCT_TABLE_NAME + " WHERE " + KEY_SUPPLIER + "= ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{supplierName});

        if (cursor == null) {
            return count;
        }

        if (cursor.moveToFirst()) {
            do {
                int order = cursor.getInt(14);

                if (order > 0) {
                    count++;
                }

            } while (cursor.moveToNext());
        }

        return count;
    }

    public List<Product> getAllProductsBySupplierName(String supplierName, boolean orderWise) {

        List<Product> productList = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + PRODUCT_TABLE_NAME + " WHERE " + KEY_SUPPLIER + "=  ?";

        Log.d("TAG", selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{supplierName});

        if (cursor == null) {
            return null;
        }


        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                Log.d("TAG", ""+i++);
                Product product = new Product();
                product.setName(cursor.getString(0));
                product.setPrice(cursor.getFloat(1));
                product.setDescription(cursor.getString(2));
                product.setCategory(cursor.getString(3));
                product.setSubCategory(cursor.getString(4));
                product.setPicPath(cursor.getString(5));
                Log.d("TAG", "product Name:" + product.getName() + ", product pic path:" + product.getPicPath());
                product.setSupplier(supplierName);
                product.setMinOrderQuantity(cursor.getInt(7));
                product.setMinStockQuantity(cursor.getInt(8));
                product.setCostPrice(cursor.getFloat(9));
                product.setSalePrice(cursor.getFloat(10));
                product.setMRP(cursor.getFloat(11));
                product.setCode(cursor.getString(12));
                product.setStock(cursor.getInt(13));
                int order = cursor.getInt(14);
                product.setGeneratedOrderQuantity(order);

                if (orderWise == true) {
                    if (order > 0) {
                        productList.add(product);
                    }
                } else {
                    productList.add(product);
                }

            } while (cursor.moveToNext());

        }

        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {

                String lhsCode, rhsCode;
                lhsCode = lhs.getCode().substring(1);
                rhsCode = rhs.getCode().substring(1);

                int iLhsCode, iRhsCode;

                iLhsCode = Integer.parseInt(lhsCode);
                iRhsCode = Integer.parseInt(rhsCode);

                return (iLhsCode > iRhsCode)?1:0;
            }
        });

        return productList;
    }

    public List<Product> getAllProductsByCategoryName(String category, String subCaterogy) {

        List<Product> productList = new ArrayList<Product>();
        String selectQuery = "SELECT  * FROM " + PRODUCT_TABLE_NAME + " WHERE " + KEY_CATEGORY + "= ?" + " and " +
                KEY_SUB_CATEGORY + "= ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{category, subCaterogy});

        if (cursor == null) {
            return null;
        }

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setName(cursor.getString(0));
                product.setPrice(cursor.getFloat(1));
                product.setDescription(cursor.getString(2));
                product.setCategory(category);
                product.setSubCategory(subCaterogy);
                product.setPicPath(cursor.getString(5));
                product.setSupplier(cursor.getString(6));
                product.setMinOrderQuantity(cursor.getInt(7));
                product.setMinStockQuantity(cursor.getInt(8));
                product.setCostPrice(cursor.getFloat(9));
                product.setSalePrice(cursor.getFloat(10));
                product.setMRP(cursor.getFloat(11));
                product.setCode(cursor.getString(12));
                product.setStock(cursor.getInt(13));
                product.setGeneratedOrderQuantity(cursor.getInt(14));
                productList.add(product);
            } while (cursor.moveToNext());
        }

        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                String lhsCode, rhsCode;
                lhsCode = lhs.getCode().substring(1);
                rhsCode = rhs.getCode().substring(1);

                int iLhsCode, iRhsCode;

                iLhsCode = Integer.parseInt(lhsCode);
                iRhsCode = Integer.parseInt(rhsCode);

                return (iLhsCode > iRhsCode)?1:0;            }
        });

        return productList;
    }

    public Product getProductByCode(String code) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PRODUCT_TABLE_NAME, new String[]{
                        KEY_NAME,
                        KEY_PRICE,
                        KEY_DESCRIPTION,
                        KEY_CATEGORY,
                        KEY_SUB_CATEGORY,
                        KEY_PHOTO_PATH,
                        KEY_SUPPLIER,
                        KEY_MIN_ORDER_QUANTITY,
                        KEY_MIN_STOCK_QUANTITY,
                        KEY_COST_PRICE,
                        KEY_SALE_PRICE,
                        KEY_MRP,
                        KEY_STOCK,
                        KEY_GENERATED_ORDER_QUANTITY
                }, KEY_CODE + "=?",
                new String[]{code}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        Product product = new Product();
        product.setCode(code);
        product.setName(cursor.getString(0));
        product.setPrice(cursor.getFloat(1));
        product.setDescription(cursor.getString(2));
        product.setCategory(cursor.getString(3));
        product.setSubCategory(cursor.getString(4));
        product.setPicPath(cursor.getString(5));
        product.setSupplier(cursor.getString(6));
        product.setMinOrderQuantity(cursor.getInt(7));
        product.setMinStockQuantity(cursor.getInt(8));
        product.setCostPrice(cursor.getFloat(9));
        product.setSalePrice(cursor.getFloat(10));
        product.setMRP(cursor.getFloat(11));
        product.setStock(cursor.getInt(13));
        product.setGeneratedOrderQuantity(cursor.getInt(14));

        return product;
    }

    public List<Product> getProductByName(String name) {

        List<Product> productList = new ArrayList<Product>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PRODUCT_TABLE_NAME, new String[]{
                        KEY_PRICE,
                        KEY_DESCRIPTION,
                        KEY_CATEGORY,
                        KEY_SUB_CATEGORY,
                        KEY_PHOTO_PATH,
                        KEY_SUPPLIER,
                        KEY_MIN_ORDER_QUANTITY,
                        KEY_MIN_STOCK_QUANTITY,
                        KEY_COST_PRICE,
                        KEY_SALE_PRICE,
                        KEY_MRP,
                        KEY_STOCK,
                        KEY_GENERATED_ORDER_QUANTITY
                }, KEY_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor == null) {
            return null;
        }

        if (cursor.moveToFirst()) {
            Product product = new Product();
            product.setPrice(cursor.getFloat(1));
            product.setDescription(cursor.getString(2));
            product.setCategory(cursor.getString(3));
            product.setSubCategory(cursor.getString(4));
            product.setPicPath(cursor.getString(5));
            product.setSupplier(cursor.getString(6));
            product.setMinOrderQuantity(cursor.getInt(7));
            product.setMinStockQuantity(cursor.getInt(8));
            product.setCostPrice(cursor.getFloat(9));
            product.setSalePrice(cursor.getFloat(10));
            product.setMRP(cursor.getFloat(11));
            product.setCode(cursor.getString(12));
            product.setStock(cursor.getInt(13));
            product.setGeneratedOrderQuantity(cursor.getInt(14));
            productList.add(product);
        }

        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                String lhsCode, rhsCode;
                lhsCode = lhs.getCode().substring(1);
                rhsCode = rhs.getCode().substring(1);

                int iLhsCode, iRhsCode;

                iLhsCode = Integer.parseInt(lhsCode);
                iRhsCode = Integer.parseInt(rhsCode);

                return (iLhsCode > iRhsCode)?1:0;            }
        });

        return productList;
    }

    public void addCategory(String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);

        Log.d("TAG", "category table addition result: " + db.insert(CATEGORY_TABLE_NAME, null, values));

        db.close();
    }

    public void removeCategory(String category) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "DELETE FROM " + CATEGORY_TABLE_NAME +
                " WHERE " + KEY_NAME + "='" + category + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void removeSubCategories(String category) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "DELETE FROM " + SUB_CATEGORY_TABLE_NAME +
                " WHERE " + KEY_CATEGORY + "='" + category + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void removeSubCategory(String category, String subCategory) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "DELETE FROM " + SUB_CATEGORY_TABLE_NAME +
                " WHERE " + KEY_CATEGORY + "='" + category + "' and " + KEY_NAME + "='" + subCategory + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void removeSupplier(String supplierName) {

        if (supplierName.equalsIgnoreCase("Default") == true) return;

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "DELETE FROM " + SUPPLIERS_TABLE_NAME +
                " WHERE " + KEY_NAME + "= ?";

        try {
            db.execSQL(strSQL, new String[]{supplierName});
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public void removeProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "DELETE FROM " + PRODUCT_TABLE_NAME +
                " WHERE " + KEY_CODE + "='" + product.getCode() + "'";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public List<String> getCategories() {

        List<String> categories = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + CATEGORY_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0).trim());
            } while (cursor.moveToNext());
        }

        return categories;
    }

    public void addSubCategory(String name, String category) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_CATEGORY, category);

        Log.d("TAG", "sub category table addition result: " + db.insert(SUB_CATEGORY_TABLE_NAME, null, values));

        db.close();
    }

    private void addLastProductCode(String code) {

        SQLiteDatabase db = this.getWritableDatabase();
        String last = code.substring(1);
        String first = code.charAt(0) + "";

        ContentValues values = new ContentValues();
        values.put(KEY_BASE0, last);
        values.put(KEY_BASE1, first);
        values.put(KEY_SERIAL_NO, "0");

        Log.d("TAG", "product table addition result: " + db.insert(LAST_PRODUCT_CODE_TABLE_NAME, null, values));

        db.close();
    }

    public void deleteLastProductCode() {
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "DELETE  FROM " + LAST_PRODUCT_CODE_TABLE_NAME + ";";

        try {
            db.execSQL(strSQL);
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public String getNextProductCode() {

        String productCode = "A1";

        String selectQuery = "SELECT  * FROM " + LAST_PRODUCT_CODE_TABLE_NAME + " WHERE " + KEY_SERIAL_NO + "= '0'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            String finalCode = "A1";
            Log.d("TAG", "getNextProductCode: finalCode: " + finalCode);
            addLastProductCode(finalCode);
            return finalCode;
        }

        if (cursor.moveToFirst()) {
            String base0 = cursor.getString(0);
            String base1 = cursor.getString(1);

            char first = base1.charAt(0);
            int base = 1;

            if (base0.equals("65535")) {
                first = (char) (first + 1);
            } else {
                base = Integer.parseInt(base0);
                base++;
            }

            Log.d("TAG", "" + first);
            String finalCode = "" + first + "" + base;
            Log.d("TAG", "getNextProductCode: finalCode: " + finalCode);

            deleteLastProductCode();
            addLastProductCode(finalCode);

            return finalCode;
        }

        cursor.close();
        Log.d("TAG", "getNextProductCode: finalCode: " + productCode);
        addLastProductCode(productCode);

        return productCode;
    }

    public List<String> getSubCategories(String category) {

        List<String> subCategories = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + SUB_CATEGORY_TABLE_NAME + " WHERE " + KEY_CATEGORY + "= ?";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                subCategories.add(cursor.getString(0).trim());
            } while (cursor.moveToNext());
        }

        return subCategories;
    }

    public void addSupplier(Supplier supplier) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, supplier.getName());
        values.put(KEY_ADDRESS, supplier.getAddress());
        values.put(KEY_TELEPHONE, supplier.getTelephone());
        values.put(KEY_MOBILE, supplier.getMobile());
        values.put(KEY_EMAIL, supplier.getEmail());

        try {
            Log.d("TAG", "supplier table addition result: " + db.insert(SUPPLIERS_TABLE_NAME, null, values));
        }catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }

    public List<String> getSuppliersList() {

        List<String> suppliers = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + SUPPLIERS_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                suppliers.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        Log.d("APP", suppliers.toString());

        return suppliers;
    }

    public Supplier getSupplier(String supplierName) {

        String selectQuery = "SELECT  * FROM " + SUPPLIERS_TABLE_NAME +
                " WHERE " + KEY_NAME + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{supplierName});

        if (cursor == null) return null;

        Supplier supplier = new Supplier();

        if (cursor.moveToFirst()) {
            supplier.setName(cursor.getString(0));
            supplier.setAddress(cursor.getString(1));
            supplier.setTelephone(cursor.getString(2));
            supplier.setMobile(cursor.getString(3));
            supplier.setEmail(cursor.getString(4));
        }

        return supplier;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                PRODUCT_TABLE_NAME + "(" +
                KEY_NAME + " TEXT," +
                KEY_PRICE + " FLOAT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_CATEGORY + " TEXT," +
                KEY_SUB_CATEGORY + " TEXT," +
                KEY_PHOTO_PATH + " TEXT," +
                KEY_SUPPLIER + " INTEGER," +
                KEY_MIN_ORDER_QUANTITY + " INTEGER," +
                KEY_MIN_STOCK_QUANTITY + " INTEGER," +
                KEY_COST_PRICE + " FLOAT," +
                KEY_SALE_PRICE + " FLOAT," +
                KEY_MRP + " TEXT, " +
                KEY_CODE + " TEXT, " +
                KEY_STOCK + " INTEGER, " +
                KEY_GENERATED_ORDER_QUANTITY + " INTEGER, " +
                "PRIMARY KEY(" + KEY_CODE + ", " + KEY_NAME + ")" + ");";

        db.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS " +
                CATEGORY_TABLE_NAME + "(" +
                KEY_NAME + " TEXT," +
                "PRIMARY KEY(" + KEY_NAME + ")" + ");";

        db.execSQL(CREATE_CATEGORIES_TABLE);

        String CREATE_SUB_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS " +
                SUB_CATEGORY_TABLE_NAME + "(" +
                KEY_NAME + " TEXT," +
                KEY_CATEGORY + " TEXT," +
                "PRIMARY KEY(" + KEY_NAME + ", " + KEY_CATEGORY + ")" + ");";

        db.execSQL(CREATE_SUB_CATEGORIES_TABLE);

        String CREATE_SUPPLIERS_TABLE = "CREATE TABLE " +
                SUPPLIERS_TABLE_NAME + "(" +
                KEY_NAME + " TEXT," +
                KEY_ADDRESS + " TEXT," +
                KEY_TELEPHONE + " TEXT," +
                KEY_MOBILE + " TEXT," +
                KEY_EMAIL + " TEXT," +
                "PRIMARY KEY(" + KEY_NAME + ", " + KEY_EMAIL + ", " + KEY_MOBILE + ")" + ");";

        db.execSQL(CREATE_SUPPLIERS_TABLE);

        String CREATE_LAST_PRODUCT_CODE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                LAST_PRODUCT_CODE_TABLE_NAME + "(" +
                KEY_BASE0 + " TEXT," +
                KEY_BASE1 + " TEXT," +
                KEY_SERIAL_NO + " TEXT," +
                "PRIMARY KEY(" + KEY_SERIAL_NO + ")" + ");";

        db.execSQL(CREATE_LAST_PRODUCT_CODE_TABLE);

        Log.d("TAG", "Created tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
