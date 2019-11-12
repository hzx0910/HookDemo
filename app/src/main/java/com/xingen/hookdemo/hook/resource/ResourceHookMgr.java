package com.xingen.hookdemo.hook.resource;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * 简单兼容性更好的用法法，资源不存在冲突，不需要特殊处理。但是存在插件、宿主之间资源信息共享问题。
 */
public class ResourceHookMgr {
    private static Resources resources;

    public static void init(Context context, String apkFilePath) {
        preloadResource(context, apkFilePath);
    }

    private synchronized static void preloadResource(Context context, String apkFilePath) {
        try {
            // 先创建AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(
                    assetManager, apkFilePath);
            //在创建Resource
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Drawable getDrawable(String name, String packageName) {
        int imgId = getId(name, "mipmap", packageName);
        if (imgId == 0) {
            imgId = getId(name, "drawable", packageName);
        }
        return resources.getDrawable(imgId);
    }

    public static int getId(String name, String type, String packageName) {
        return resources.getIdentifier(name, type, packageName);
    }
}
