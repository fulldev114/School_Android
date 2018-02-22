package com.common.utils;

/**
 * Created by Administrator on 11/16/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {


//    public static String hashBitmap(Bitmap bmp){
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        String hash = DigestUtils.md5Hex(byteArray);
//        return hash;
//    }
//
//    public static String hashBitmap(byte[] bytes){
//        if ( bytes == null )
//            return null;
//
//        String hash = DigestUtils.md5Hex(bytes);
//        return hash;
//    }

    public static Bitmap resizeBitmap(Bitmap bmp, int width, int height) {
        Bitmap result = null;
        if ( bmp != null ) {
            if ( bmp.getWidth() != width || bmp.getHeight() != height ) {
                result = Bitmap.createScaledBitmap(bmp, width, height, true);
            }
            else{
                result = bmp;
            }
        }
        else{
            result = null;
        }

        return result;
    }

    public static Bitmap getBitmapFromFile(String filePath) {
        Bitmap res = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try{
            res = BitmapFactory.decodeFile(filePath, options);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError ome) {
            ome.printStackTrace();
        }
        return res;
    }

    public static Bitmap makePhotoCoverBmp(Bitmap bitmap) {
        Bitmap res = null;
        if ( bitmap == null )
            return null;

        if ( bitmap.getWidth() > GlobalConstrants.PHOTO_COVER_SIZE || bitmap.getHeight() > GlobalConstrants.PHOTO_COVER_SIZE ) {
            int width, height;
            if ( bitmap.getWidth() >= bitmap.getHeight() ) {
                width = GlobalConstrants.PHOTO_COVER_SIZE;
                height = width * bitmap.getHeight() / bitmap.getWidth();
            }
            else{
                height = GlobalConstrants.PHOTO_COVER_SIZE;
                width = height * bitmap.getWidth() / bitmap.getHeight();
            }
            try{
                res = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }catch(Exception e){
                e.printStackTrace();
            }
            return res;
        }
        else{
            return bitmap;
        }
    }

    public static byte[] convertBmpToBytes(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();

        try {
            stream.close();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static boolean writeBmpToFile(Bitmap bm, String filePath) {
        File file = new File(filePath);
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, 80, bos);
            byte[] bitmapdata = bos.toByteArray();
            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            fos = null;
            return true;
        }catch(Exception e){
            //DebugConfig.error("BitmapUtil", "writeBmpToFile",  e);
            Log.e("BitmapUtil","exception : "+e.getMessage());
        }
        return false;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = (float) (bitmap.getHeight() / 1.5);
        } else {
            r = (float) (bitmap.getWidth() / 1.5);
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap decodeUri(Context context, Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);
        // BitmapFactory.decodeFile(selectedImage,o);
        final int REQUIRED_SIZE = 90;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = scale;

        // BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(selectedImage), null, opts);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(new File(selectedImage.getPath()).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, opts.outWidth, opts.outHeight, matrix, true);

        return BitmapFactory.decodeStream(
                context.getContentResolver().openInputStream(selectedImage), null, opts);
        //BitmapFactory.decodeFile(selectedImage, o2);
    }

    public static Bitmap landtoport(String imagpath) {
        Bitmap rotatedBitmap = null;

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagpath, bounds);

        final int REQUIRED_SIZE = 90;

        int width_tmp = bounds.outWidth;
        int height_tmp = bounds.outHeight;

        //      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 1200.0f;
        float maxWidth = 1000.0f;
        float imgRatio = width_tmp / height_tmp;
        float maxRatio = maxWidth / maxHeight;

        //      width and height values are set maintaining the aspect ratio of the image

        if (height_tmp > maxHeight || width_tmp > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / height_tmp;
                width_tmp = (int) (imgRatio * width_tmp);
                height_tmp = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / width_tmp;
                height_tmp = (int) (imgRatio * height_tmp);
                width_tmp = (int) maxWidth;
            } else {
                height_tmp = (int) maxHeight;
                width_tmp = (int) maxWidth;

            }
        }

        int scale = 1;
       /* while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }*/

        BitmapFactory.Options opts = new BitmapFactory.Options();
        //  opts.inSampleSize = scale;

        // setting inSampleSize value allows to load a scaled down version of the original image

        opts.inSampleSize = calculateInSampleSize(opts, width_tmp, height_tmp);

        //      inJustDecodeBounds set to false to load the actual bitmap
        opts.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inTempStorage = new byte[16 * 1024];


        // BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(imagpath, opts);

        try {
            rotatedBitmap = Bitmap.createBitmap(width_tmp, height_tmp, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

      /*  ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagpath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);*/

        float ratioX = width_tmp / (float) opts.outWidth;
        float ratioY = height_tmp / (float) opts.outHeight;
        float middleX = width_tmp / 2.0f;
        float middleY = height_tmp / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bm, middleX - bm.getWidth() / 2, middleY - bm.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagpath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }

            //  rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, opts.outWidth, opts.outHeight, matrix, true);
            rotatedBitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
