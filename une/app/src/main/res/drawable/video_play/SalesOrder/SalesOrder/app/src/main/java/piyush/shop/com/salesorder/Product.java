package piyush.shop.com.salesorder;

import android.util.Log;

/**
 * Created by hp on 23-Jul-17.
 */
public class Product {

    private String Name;
    private float price;
    private String description;
    private String category;
    private String subCategory;
    private String picPath;
    private String supplier;
    private int minOrderQuantity;
    private int minStockQuantity;
    private float costPrice;
    private float salePrice;
    private float MRP;
    private String code;
    private int stock;
    private int generatedOrderQuantity;

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public int getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(int minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public int getMinStockQuantity() {
        return minStockQuantity;
    }

    public void setMinStockQuantity(int minStockQuantity) {
        this.minStockQuantity = minStockQuantity;
    }

    public float getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(float costPrice) {
        this.costPrice = costPrice;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public float getMRP() {
        return MRP;
    }

    public void setMRP(float MRP) {
        this.MRP = MRP;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getGeneratedOrderQuantity() {
        return generatedOrderQuantity;
    }

    public void setGeneratedOrderQuantity(int generatedOrderQuantity) {
        this.generatedOrderQuantity = generatedOrderQuantity;
    }

    public void dumpProduct(){
        Log.d("ProductInfo", "Name:"+this.getName()+"<>");
        Log.d("ProductInfo", "Description:"+this.getDescription()+"<>");
        Log.d("ProductInfo", "Category:"+this.getCategory()+"<>");
        Log.d("ProductInfo", "SubCategory:"+this.getSubCategory()+"<>");
        Log.d("ProductInfo", "Supplier:"+this.getSupplier()+"<>");
    }
}
