package piyush.shop.com.salesorder;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hp on 22-Jul-17.
 */
public class SOUtility {

    static public String BASE_FILE_PATH = Environment.getExternalStorageDirectory() + "/PurchaseOrder/";
    static public Bitmap tempBmp;
    static public SODatabaseHandler dbHandler;
    static public MainActivity mainActivity;
    static public ProductActivity productActivity;
    static public AddProductViewForMobile addProductViewForMobile;
    static public PurchaseOrderActivity purchaseOrderActivity;
    static public Search searchActivity;

    static public final String PASSWORD = "2614";

    public boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }

    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    public void createImageBaseDirectory() {
        File tempDir;
        tempDir = new File(BASE_FILE_PATH);
        tempDir.mkdirs();
    }

    public void createPicsDirectory() {
        File tempDir;
        tempDir = new File(BASE_FILE_PATH);
        tempDir.mkdirs();

        tempDir = new File(BASE_FILE_PATH+"/pics/");
        tempDir.mkdirs();
    }

    public void createPurchaseOrderDirectory() {
        File tempDir;
        tempDir = new File(BASE_FILE_PATH);
        tempDir.mkdirs();

        tempDir = new File(BASE_FILE_PATH+"/purchaseOrder/");
        tempDir.mkdirs();
    }

    public void createDataBaseDirectory() {
        File tempDir;

        Log.d("TAG", "creating base directory");
        tempDir = new File(BASE_FILE_PATH);
        tempDir.mkdirs();

        Log.d("TAG", "creating database directory");
        tempDir = new File(BASE_FILE_PATH+"/database/");
        tempDir.mkdirs();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public void writeBitMapIntoFile(Bitmap bmp, String filename){

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        if(image == null) return null;
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public void copy(File src, File dst) {
        Log.d("ProductInfo", "Copy");
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValidPhoneNo(CharSequence target) {
        final String phoneNumPattern = "^\\d{10}$";
        Pattern pattern = Pattern.compile(phoneNumPattern);
        Matcher matcher = pattern.matcher(target);

        if (matcher.matches() == true) {
            return true;
        }

        return false;
    }

    public double availableMemoryMB() {
        double max = Runtime.getRuntime().maxMemory() / 1024;
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        return (max - memoryInfo.getTotalPss()) / 1024;
    }

    public File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/PurchaseOrder/");
        if (tempDir.exists()) {
            tempDir.delete();
        }
        tempDir.mkdirs();

        return File.createTempFile(part, ext, tempDir);
    }

    public Bitmap grabImage(Context context, Uri mImageUri) {
        context.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = context.getContentResolver();
        Bitmap bitmap = null;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SOUTility", "Exception");
        }

        return bitmap;
    }
}
