package it.polito.mad.mad_app.model;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucia on 13/05/2017.
 */

public class ImageMethod {
    public static List<Intent> require_image(Uri outputFileUri,PackageManager p) {

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        final List<ResolveInfo> Im =p.queryIntentActivities(pickIntent, 0);
        for(ResolveInfo res : Im) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(pickIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final List<ResolveInfo> Cam =p.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : Cam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        return cameraIntents;
    }

    public static Intent performCrop(Uri imageUrl,PackageManager p) {
        try {
            List<Intent> photoIntents=new ArrayList<>();
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imageUrl, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("return-data", true);
            final List<ResolveInfo> Cam =p.queryIntentActivities(cropIntent, 0);
            for(ResolveInfo res : Cam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(cropIntent);
                intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                photoIntents.add(intent);
            }//
            if(photoIntents.size()>1){
                return photoIntents.get(0);
            }else{
                return cropIntent;
            }
        }
        catch (ActivityNotFoundException anfe) {
            return null;
        }
    }

    public static void circle_image(final Context c, final ImageView i,String p){
        Glide.with(c).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(i) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(c.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                i.setImageDrawable(circularBitmapDrawable);
            }
        });

    }
    public static void circle_image(final Context c, final ImageView i,Uri p){
        Glide.with(c).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(i) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(c.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                i.setImageDrawable(circularBitmapDrawable);
            }
        });

    }
    public static void circle_image(final Context c, final ImageView i,int p){
        final ImageView im=i;
        Glide.with(c).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(im) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(c.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                im.setImageDrawable(circularBitmapDrawable);
            }
        });

    }

    public static void create_image( Uri outputFileUri,Bitmap thePic){
        File f = new File(outputFileUri.getPath());
        if (f.exists()) {
            f.delete();
        }
        f = new File(outputFileUri.getPath());
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        thePic.compress(Bitmap.CompressFormat.PNG, 0 , bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}

