package cn.jiguang.imui.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dowin.imageviewer.DraweePagerAdapter;
import com.dowin.imageviewer.ImageLoader;
import com.dowin.imageviewer.MultiTouchViewPager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.List;

import cn.jiguang.imui.commons.models.IMediaFile;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by dowin on 2017/8/21.
 */

public class PhotoViewPagerViewUtil {
    static ImageLoader imageLoader = new ImageLoader<IMediaFile>() {
        @Override
        public void load(final PhotoDraweeView photoDraweeView, IMediaFile o) {
            photoDraweeView.setThumbImage(o.getThumbPath());
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(o.getUrl()));//"res:///" + id
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());
        }
    };

    public static void show(Activity mActivity, List<IMediaFile> list, int curIndex) {
        final Dialog dialog = new Dialog(mActivity, com.dowin.imageviewer.R.style.ImageDialog);
        View view = mActivity.getLayoutInflater().inflate(com.dowin.imageviewer.R.layout.activity_viewpager, null);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            }
        });
//        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        final MultiTouchViewPager viewPager = (MultiTouchViewPager) view.findViewById(com.dowin.imageviewer.R.id.view_pager);
        viewPager.setBackgroundColor(Color.BLACK);
        final DraweePagerAdapter adapter = new DraweePagerAdapter(list, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        }, imageLoader);
        viewPager.setAdapter(adapter);
        final TextView indexText = (TextView) view.findViewById(com.dowin.imageviewer.R.id.pager_index);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indexText.setText(String.format("%d/%d", position + 1, adapter.getCount()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(curIndex);
        indexText.setText(String.format("%d/%d", curIndex + 1, adapter.getCount()));
        //        indicator.setViewPager(viewPager);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lay = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View decorView = mActivity.getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(rect);
        lay.height = dm.heightPixels - rect.top;
        lay.width = dm.widthPixels;
        dialog.show();
    }
}
