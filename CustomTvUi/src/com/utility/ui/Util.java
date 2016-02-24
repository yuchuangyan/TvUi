
package com.utility.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
@SuppressWarnings("deprecation")
public class Util {
	
	/**
	 * 初始化UI适配，至少初始化一次
	 * @param activity
	 */
	public static void init(Context context) {
		ratio = context.getResources().getDisplayMetrics().widthPixels / 1920.0;
	}
	
	/**
	 * 获取经过处理的资源图，包括普通图和.9图
	 * @param context 应用环境
	 * @param id 资源id
	 * @return Drawable格式的资源
	 * <p>
	 * 以1080p为基准，小于此尺寸的缩写，大于此尺寸的放大
	 */
	public static Drawable getCompatibleDrawable(Context context, int id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDensity = 240;
		options.inScreenDensity = (int) (240*ratio);
		options.inTargetDensity = (int) (240*ratio);

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				id, options);
		byte[] ninePathChunk = bitmap.getNinePatchChunk();
		
		if (NinePatch.isNinePatchChunk(ninePathChunk)) {
			NinePatch ninePath = new NinePatch(bitmap, ninePathChunk, null);
			
			return new NinePatchDrawable(ninePath);
		} else {
			return new BitmapDrawable(bitmap);
		}
	}
	
	/**
	 * 将1080p的参数转换到设备使用的参数
	 * @param params 1080p的参数
	 * @return 设备所需的参数
	 */
    public static ViewGroup.LayoutParams convertIn(ViewGroup.LayoutParams params) {
    	ViewGroup.LayoutParams rslt = null;
    	
        if (params instanceof AbsoluteLayout.LayoutParams) {
        	rslt = new AbsoluteLayout.LayoutParams(params);
            AbsoluteLayout.LayoutParams from = (AbsoluteLayout.LayoutParams) params;
            AbsoluteLayout.LayoutParams to = (AbsoluteLayout.LayoutParams) rslt;

            to.x = convertIn(from.x);
            to.y = convertIn(from.y);

            if (AbsoluteLayout.LayoutParams.MATCH_PARENT!=from.width
                    && AbsoluteLayout.LayoutParams.WRAP_CONTENT!=from.width
                    && AbsoluteLayout.LayoutParams.FILL_PARENT!=from.width) {
                to.width = convertIn(from.width);
            }

            if (AbsoluteLayout.LayoutParams.MATCH_PARENT!=from.height
                    && AbsoluteLayout.LayoutParams.WRAP_CONTENT!=from.height
                    && AbsoluteLayout.LayoutParams.FILL_PARENT!=from.height) {
                to.height = convertIn(from.height);
            }
        } else if (params instanceof RelativeLayout.LayoutParams) {
        	rslt = new RelativeLayout.LayoutParams(params);
        	RelativeLayout.LayoutParams from = (RelativeLayout.LayoutParams) params;
            RelativeLayout.LayoutParams to = (RelativeLayout.LayoutParams) rslt;

            to.leftMargin = Util.convertIn(from.leftMargin);
            to.topMargin = Util.convertIn(from.topMargin);
            to.rightMargin = Util.convertIn(from.rightMargin);
            to.bottomMargin = Util.convertIn(from.bottomMargin);
            
            System.arraycopy(from.getRules(), 0, to.getRules(), 0, to.getRules().length);
            
            if (RelativeLayout.LayoutParams.MATCH_PARENT!=from.width
                    && RelativeLayout.LayoutParams.WRAP_CONTENT!=from.width
                    && RelativeLayout.LayoutParams.FILL_PARENT!=from.width) {
                to.width = convertIn(from.width);
            }

            if (RelativeLayout.LayoutParams.MATCH_PARENT!=from.height
                    && RelativeLayout.LayoutParams.WRAP_CONTENT!=from.height
                    && RelativeLayout.LayoutParams.FILL_PARENT!=from.height) {
                to.height = convertIn(from.height);
            }
        }

        return rslt;
    }
    
    /**
	 * 将设备所需的参数转换到1080p的参数
	 * @param params 设备所用的参数
	 * @return 1080p的参数
	 */
    public static ViewGroup.LayoutParams convertOut(ViewGroup.LayoutParams params) {
    	ViewGroup.LayoutParams rslt = null;
    	
        if (params instanceof AbsoluteLayout.LayoutParams) {
        	rslt = new AbsoluteLayout.LayoutParams(params);
            AbsoluteLayout.LayoutParams from = (AbsoluteLayout.LayoutParams) params;
            AbsoluteLayout.LayoutParams to = (AbsoluteLayout.LayoutParams) rslt;

            to.x = convertOut(from.x);
            to.y = convertOut(from.y);

            if (AbsoluteLayout.LayoutParams.MATCH_PARENT!=from.width
                    && AbsoluteLayout.LayoutParams.WRAP_CONTENT!=from.width
                    && AbsoluteLayout.LayoutParams.FILL_PARENT!=from.width) {
                to.width = convertOut(from.width);
            }

            if (AbsoluteLayout.LayoutParams.MATCH_PARENT!=from.height
                    && AbsoluteLayout.LayoutParams.WRAP_CONTENT!=from.height
                    && AbsoluteLayout.LayoutParams.FILL_PARENT!=from.height) {
                to.height = convertOut(from.height);
            }
        } else if (params instanceof RelativeLayout.LayoutParams) {
        	rslt = new RelativeLayout.LayoutParams(params);
        	RelativeLayout.LayoutParams from = (RelativeLayout.LayoutParams) params;
            RelativeLayout.LayoutParams to = (RelativeLayout.LayoutParams) rslt;

            to.leftMargin = Util.convertOut(from.leftMargin);
            to.topMargin = Util.convertOut(from.topMargin);
            to.rightMargin = Util.convertOut(from.rightMargin);
            to.bottomMargin = Util.convertOut(from.bottomMargin);
            
            System.arraycopy(from.getRules(), 0, to.getRules(), 0, to.getRules().length);
            
            if (RelativeLayout.LayoutParams.MATCH_PARENT!=from.width
                    && RelativeLayout.LayoutParams.WRAP_CONTENT!=from.width
                    && RelativeLayout.LayoutParams.FILL_PARENT!=from.width) {
                to.width = convertOut(from.width);
            }

            if (RelativeLayout.LayoutParams.MATCH_PARENT!=from.height
                    && RelativeLayout.LayoutParams.WRAP_CONTENT!=from.height
                    && RelativeLayout.LayoutParams.FILL_PARENT!=from.height) {
                to.height = convertOut(from.height);
            }
        }

        return rslt;
    }
    
    /**
     * clone布局参数
     * @param params 布局参数
     * @return clone出的布局参数
     */
    public static RelativeLayout.LayoutParams cloneLayoutParams(RelativeLayout.LayoutParams params) {
    	RelativeLayout.LayoutParams rslt = new RelativeLayout.LayoutParams(params);
    	
    	rslt.width = params.width;
    	rslt.height = params.height;
    	rslt.leftMargin = Util.convertOut(params.leftMargin);
    	rslt.topMargin = Util.convertOut(params.topMargin);
    	rslt.rightMargin = Util.convertOut(params.rightMargin);
    	rslt.bottomMargin = Util.convertOut(params.bottomMargin);
        int[] src = params.getRules();
        int[] dst = rslt.getRules();
    	System.arraycopy(src, 0, dst, 0, src.length<dst.length ? src.length : dst.length);
        
    	return rslt;
    }
    
    /**
     * clone布局参数
     * @param params 布局参数
     * @return clone出的布局参数
     */
    public static AbsoluteLayout.LayoutParams cloneLayoutParams(AbsoluteLayout.LayoutParams params) {
    	return new AbsoluteLayout.LayoutParams(
    			params.width, params.height, 
    			params.x, params.y);
    }

    /**
     * 将1080p的参数转换到设备使用的参数
     * @param value 1080p的参数
     * @return 设备使用的参数
     */
    public static int convertIn(int value) {
        return (int) (value * ratio);
    }

    /**
     * 将1080p的参数转换到设备使用的参数
     * @param value 1080p的参数
     * @return 设备使用的参数
     */
    public static float convertIn(float value) {
        return (float) (value * ratio);
    }

    /**
     * 将1080p的参数转换到设备使用的参数
     * @param value 1080p的参数
     * @return 设备使用的参数
     */
    public static double convertIn(double value) {
        return (double) (value * ratio);
    }

    /**
	 * 将设备所需的参数转换到1080p的参数
	 * @param value 设备所用的参数
	 * @return 1080p的参数
	 */
    public static int convertOut(int value) {
        return (int) (value / ratio);
    }

    /**
	 * 将设备所需的参数转换到1080p的参数
	 * @param value 设备所用的参数
	 * @return 1080p的参数
	 */
    public static float convertOut(float value) {
        return (float) (value / ratio);
    }

    /**
	 * 将设备所需的参数转换到1080p的参数
	 * @param value 设备所用的参数
	 * @return 1080p的参数
	 */
    public static double convertOut(double value) {
        return (double) (value / ratio);
    }

    /**
     * 设备屏幕宽度与1920的比值
     */
    private static double ratio = 1.0;

}

