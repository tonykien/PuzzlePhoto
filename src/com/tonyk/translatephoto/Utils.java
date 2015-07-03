package com.tonyk.translatephoto;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class Utils {

	public static Bitmap resizeBitmap(String pathFile, int requiredSize) {
		int orientation;
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathFile, o);

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 <= requiredSize || height_tmp / 2 <= requiredSize) break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize TODO
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bm = null;
			try {
				bm = BitmapFactory.decodeFile(pathFile, o2);
			} catch (OutOfMemoryError e1) {
				Log.e("TAG", "ERROR = " + e1.getMessage());
			}

			if (bm == null) {
				return bm;
			}
			Log.d("resizeBitmap", "WH: " + bm.getWidth() + ", " + bm.getHeight());

			Bitmap bitmap = bm;

			// ============================================================
			ExifInterface exif = new ExifInterface(pathFile);

			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

			Matrix m = new Matrix();

			if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
				m.postRotate(180);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
				bm.recycle();
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				m.postRotate(90);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
				bm.recycle();
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				m.postRotate(270);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
				bm.recycle();
			}

			return bitmap;
		} catch (OutOfMemoryError e) {
			Log.e("TAG", "ERROR = " + e.getMessage());
		} catch (FileNotFoundException e) {
			Log.e("TAG", "ERROR = " + e.getMessage());
		} catch (IOException e) {
			Log.e("TAG", "ERROR = " + e.getMessage());
		}
		return null;
	}
	
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
