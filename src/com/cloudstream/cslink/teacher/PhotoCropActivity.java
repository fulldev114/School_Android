package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.common.utils.BitmapUtil;
import com.common.utils.GlobalConstrants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiscCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

public class PhotoCropActivity extends Activity {
    private static float density = 1;

    public static int dp(int value) {
        return (int)(Math.max(1, density * value));
    }

    private class PhotoCropView extends FrameLayout {

        Paint rectPaint = null;
        Paint circlePaint = null;
        Paint halfPaint = null;
        float rectSizeX = 640;
        float rectSizeY = 368;
        float ratio = rectSizeX / rectSizeY;
        float rectX = -1, rectY = -1;
        int draggingState = 0;
        float oldX = 0, oldY = 0;
        int bitmapWidth, bitmapHeight, bitmapX, bitmapY;
        int viewWidth, viewHeight;
        boolean freeform;

        public PhotoCropView(Context context) {
            super(context);
            init();
        }

        public PhotoCropView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public PhotoCropView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
        	rectPaint = new Paint();
            rectPaint.setColor(0x3ffafafa);
            rectPaint.setStrokeWidth(dp(2));
            rectPaint.setStyle(Paint.Style.STROKE);
            circlePaint = new Paint();
            circlePaint.setColor(0xffffffff);
            halfPaint = new Paint();
            halfPaint.setColor(0xc8000000);
            setBackgroundColor(0xff333333);

            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    int cornerSide = dp(14);
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (rectX - cornerSide < x && rectX + cornerSide > x && rectY - cornerSide < y && rectY + cornerSide > y) {
                            draggingState = 1;
                        } else if (rectX - cornerSide + rectSizeX < x && rectX + cornerSide + rectSizeX > x && rectY - cornerSide < y && rectY + cornerSide > y) {
                            draggingState = 2;
                        } else if (rectX - cornerSide < x && rectX + cornerSide > x && rectY - cornerSide + rectSizeY < y && rectY + cornerSide + rectSizeY > y) {
                            draggingState = 3;
                        } else if (rectX - cornerSide + rectSizeX < x && rectX + cornerSide + rectSizeX > x && rectY - cornerSide + rectSizeY < y && rectY + cornerSide + rectSizeY > y) {
                            draggingState = 4;
                        } else if (rectX < x && rectX + rectSizeX > x && rectY < y && rectY + rectSizeY > y) {
                            draggingState = 5;
                        } else {
                            draggingState = 0;
                        }
                        if (draggingState != 0) {
                            PhotoCropView.this.requestDisallowInterceptTouchEvent(true);
                        }
                        oldX = x;
                        oldY = y;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        draggingState = 0;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && draggingState != 0) {
                        float diffX = x - oldX;
                        float diffY = y - oldY;
                        if (draggingState == 5) {
                            rectX += diffX;
                            rectY += diffY;

                            if (rectX < bitmapX) {
                                rectX = bitmapX;
                            } else if (rectX + rectSizeX > bitmapX + bitmapWidth) {
                                rectX = bitmapX + bitmapWidth - rectSizeX;
                            }
                            if (rectY < bitmapY) {
                                rectY = bitmapY;
                            } else if (rectY + rectSizeY > bitmapY + bitmapHeight) {
                                rectY = bitmapY + bitmapHeight - rectSizeY;
                            }
                        } else {
                            if (draggingState == 1) {
                                if (rectSizeX - diffX < 160) {
                                    diffX = rectSizeX - 160;
                                }
                                if (rectX + diffX < bitmapX) {
                                    diffX = bitmapX - rectX;
                                }
                                if (!freeform) {
                                    if (rectY + diffX < bitmapY) {
                                        diffX = bitmapY - rectY;
                                    }
                                    rectX += diffX;
                                    rectY += diffX;
                                    rectSizeX -= diffX;
                                    rectSizeY -= diffX;
                                } else {
                                    if (rectSizeY - diffY < 160) {
                                        diffY = rectSizeY - 160;
                                    }
                                    if (rectY + diffY < bitmapY) {
                                        diffY = bitmapY - rectY;
                                    }
                                    rectX += diffX;
                                    rectY += diffY;
                                    rectSizeX -= diffX;
                                    rectSizeY -= diffY;
                                }
                            } else if (draggingState == 2) {
                                if (rectSizeX + diffX < 160) {
                                    diffX = -(rectSizeX - 160);
                                }
                                if (rectX + rectSizeX + diffX > bitmapX + bitmapWidth) {
                                    diffX = bitmapX + bitmapWidth - rectX - rectSizeX;
                                }
                                if (!freeform) {
                                    if (rectY - diffX < bitmapY) {
                                        diffX = rectY - bitmapY;
                                    }
                                    rectY -= diffX;
                                    rectSizeX += diffX;
                                    rectSizeY += diffX;
                                } else {
                                    if (rectSizeY - diffY < 160) {
                                        diffY = rectSizeY - 160;
                                    }
                                    if (rectY + diffY < bitmapY) {
                                        diffY = bitmapY - rectY;
                                    }
                                    rectY += diffY;
                                    rectSizeX += diffX;
                                    rectSizeY -= diffY;
                                }
                            } else if (draggingState == 3) {
                                if (rectSizeX - diffX < 160) {
                                    diffX = rectSizeX - 160;
                                }
                                if (rectX + diffX < bitmapX) {
                                    diffX = bitmapX - rectX;
                                }
                                if (!freeform) {
                                    if (rectY + rectSizeX - diffX > bitmapY + bitmapHeight) {
                                        diffX = rectY + rectSizeX - bitmapY - bitmapHeight;
                                    }
                                    rectX += diffX;
                                    rectSizeX -= diffX;
                                    rectSizeY -= diffX;
                                } else {
                                    if (rectY + rectSizeY + diffY > bitmapY + bitmapHeight) {
                                        diffY = bitmapY + bitmapHeight - rectY - rectSizeY;
                                    }
                                    rectX += diffX;
                                    rectSizeX -= diffX;
                                    rectSizeY += diffY;
                                    if (rectSizeY < 160) {
                                        rectSizeY = 160;
                                    }
                                }
                            } else if (draggingState == 4) {
                                if (rectX + rectSizeX + diffX > bitmapX + bitmapWidth) {
                                    diffX = bitmapX + bitmapWidth - rectX - rectSizeX;
                                }
                                if (!freeform) {
                                    if (rectY + rectSizeX + diffX > bitmapY + bitmapHeight) {
                                        diffX = bitmapY + bitmapHeight - rectY - rectSizeX;
                                    }
                                    rectSizeX += diffX;
                                    rectSizeY += diffX;
                                } else {
                                    if (rectY + rectSizeY + diffY > bitmapY + bitmapHeight) {
                                        diffY = bitmapY + bitmapHeight - rectY - rectSizeY;
                                    }
                                    rectSizeX += diffX;
                                    rectSizeY += diffY;
                                }
                                if (rectSizeX < 160) {
                                    rectSizeX = 160;
                                }
                                if (rectSizeY < 160) {
                                    rectSizeY = 160;
                                }
                            }
                        }

                        oldX = x;
                        oldY = y;
                        invalidate();
                    }
                    return true;
                }
            });
        }

        private void updateBitmapSize() {
            if (viewWidth == 0 || viewHeight == 0 || imageToCrop == null) {
                return;
            }
            float percX = (rectX - bitmapX) / bitmapWidth;
            float percY = (rectY - bitmapY) / bitmapHeight;
            float percSizeX = rectSizeX / bitmapWidth;
            float percSizeY = rectSizeY / bitmapHeight;
            float w = imageToCrop.getWidth();
            float h = imageToCrop.getHeight();
            
            float scaleX = viewWidth / w;
            float scaleY = viewHeight / h;

            if (scaleX > scaleY) {
            	bitmapHeight = viewHeight;
            	bitmapWidth = (int)Math.ceil(w * scaleY);
            } else {
            	bitmapWidth = viewWidth;
            	bitmapHeight = (int)Math.ceil(h * scaleX);
            }
            
            bitmapX = (viewWidth - bitmapWidth) / 2 + dp(14);
            bitmapY = (viewHeight - bitmapHeight) / 2 + dp(14);

            if (rectX == -1 && rectY == -1) {
                if (freeform) {
                    if (bitmapWidth/bitmapHeight > ratio) {
                    	rectY = bitmapY;
                        rectSizeX = bitmapHeight * ratio;
                        rectSizeY = bitmapHeight;
                        rectX = (viewWidth - rectSizeX) / 2 + dp(14);
                    } else {
                    	rectX = bitmapX;
                        rectSizeX = bitmapWidth;
                        rectSizeY = bitmapWidth / ratio;
                        rectY = (viewHeight - rectSizeY) / 2 + dp(14);
                    }
                } else {
                    if (bitmapWidth > bitmapHeight) {
                        rectY = bitmapY;
                        rectX = (viewWidth - bitmapHeight) / 2 + dp(14);
                        rectSizeX = bitmapHeight;
                        rectSizeY = bitmapHeight;
                    } else {
                        rectX = bitmapX;
                        rectY = (viewHeight - bitmapWidth) / 2 + dp(14);
                        rectSizeX = bitmapWidth;
                        rectSizeY = bitmapWidth;
                    }
                }
            } else {
                rectX = percX * bitmapWidth + bitmapX;
                rectY = percY * bitmapHeight + bitmapY;
                rectSizeX = percSizeX * bitmapWidth;
                rectSizeY = percSizeY * bitmapHeight;
            }
            invalidate();
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            viewWidth = right - left - dp(28);
            viewHeight = bottom - top - dp(28);
            updateBitmapSize();
        }

        public Bitmap getBitmap() {
            float percX = (rectX - bitmapX) / bitmapWidth;
            float percY = (rectY - bitmapY) / bitmapHeight;
            float percSizeX = rectSizeX / bitmapWidth;
            float percSizeY = rectSizeY / bitmapWidth;
            int x = (int)(percX * imageToCrop.getWidth());
            int y = (int)(percY * imageToCrop.getHeight());
            int sizeX = (int)(percSizeX * imageToCrop.getWidth());
            int sizeY = (int)(percSizeY * imageToCrop.getWidth());
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x + sizeX > imageToCrop.getWidth()) {
                sizeX = imageToCrop.getWidth() - x;
            }
            if (y + sizeY > imageToCrop.getHeight()) {
                sizeY = imageToCrop.getHeight() - y;
            }
            try {
                return Bitmap.createBitmap(imageToCrop, x, y, sizeX, sizeY);
            } catch (Throwable e) {
                System.gc();
                try {
                    return Bitmap.createBitmap(imageToCrop, x, y, sizeX, sizeY);
                } catch (Throwable e2) {
                
                }
            }
            return null;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (drawable != null) {
                drawable.setBounds(bitmapX, bitmapY, bitmapX + bitmapWidth, bitmapY + bitmapHeight);
                drawable.draw(canvas);
            }
            canvas.drawRect(bitmapX, bitmapY, bitmapX + bitmapWidth, rectY, halfPaint);
            canvas.drawRect(bitmapX, rectY, rectX, rectY + rectSizeY, halfPaint);
            canvas.drawRect(rectX + rectSizeX, rectY, bitmapX + bitmapWidth, rectY + rectSizeY, halfPaint);
            canvas.drawRect(bitmapX, rectY + rectSizeY, bitmapX + bitmapWidth, bitmapY + bitmapHeight, halfPaint);

            canvas.drawRect(rectX, rectY, rectX + rectSizeX, rectY + rectSizeY, rectPaint);

            int side = dp(1);
            canvas.drawRect(rectX + side, rectY + side, rectX + side + dp(20), rectY + side * 3, circlePaint);
            canvas.drawRect(rectX + side, rectY + side, rectX + side * 3, rectY + side + dp(20), circlePaint);

            canvas.drawRect(rectX + rectSizeX - side - dp(20), rectY + side, rectX + rectSizeX - side, rectY + side * 3, circlePaint);
            canvas.drawRect(rectX + rectSizeX - side * 3, rectY + side, rectX + rectSizeX - side, rectY + side + dp(20), circlePaint);

            canvas.drawRect(rectX + side, rectY + rectSizeY - side - dp(20), rectX + side * 3, rectY + rectSizeY - side, circlePaint);
            canvas.drawRect(rectX + side, rectY + rectSizeY - side * 3, rectX + side + dp(20), rectY + rectSizeY - side, circlePaint);

            canvas.drawRect(rectX + rectSizeX - side - dp(20), rectY + rectSizeY - side * 3, rectX + rectSizeX - side, rectY + rectSizeY - side, circlePaint);
            canvas.drawRect(rectX + rectSizeX - side * 3, rectY + rectSizeY - side - dp(20), rectX + rectSizeX - side, rectY + rectSizeY - side, circlePaint);

            for (int a = 1; a < 3; a++) {
                canvas.drawRect(rectX + rectSizeX / 3 * a, rectY + side, rectX + side + rectSizeX / 3 * a, rectY + rectSizeY - side, circlePaint);
                canvas.drawRect(rectX + side, rectY + rectSizeY / 3 * a, rectX - side + rectSizeX, rectY + rectSizeY / 3 * a + side, circlePaint);
            }
        }
    }
    
    private Bitmap imageToCrop;
    private BitmapDrawable drawable;

    private PhotoCropView view;
    private boolean sameBitmap = false;
    private boolean doneButtonPressed = false;
    private String bitmapKey;

    private final static int done_button = 1;

    /*public PhotoCropActivity(Bundle args) {
        super(args);
    }

    public PhotoCropActivity(Bundle args, Bitmap bitmap, String key) {
        super(args);
        imageToCrop = bitmap;
        bitmapKey = key;
        if (imageToCrop != null && key != null) {
            ImageLoader.getInstance().incrementUseCount(key);
        }
    }*/
    
    private ViewGroup rootView;
    public static Point displaySize = new Point();
    private static Boolean isTablet = null;
    Activity mActivity = null;

    private View lytBack;
    private TextView btnDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = this;
        ApplicationData.initImageLoader(mActivity);

        density = getResources().getDisplayMetrics().density;
        isTablet = mActivity.getResources().getBoolean(R.bool.isTablet);
        try {
            WindowManager manager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    if(android.os.Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                }
            }
        } catch (Exception e) {
        }

    	setContentView(R.layout.adres_photocropactivity);

        rootView = (ViewGroup)findViewById(R.id.rootView);
    	lytBack = (View)findViewById(R.id.btnbck);
        lytBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationData.isphoto=false;
                finish();
            }
        });

        btnDone = (TextView)findViewById(R.id.txtDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = view.getBitmap();
                BitmapUtil.writeBmpToFile(bitmap, GlobalConstrants.LOCAL_PATH + "croppedPhotoTmp.png");
                if (bitmap == imageToCrop) {
                    sameBitmap = true;
                }else{
                    if ( bitmap != null && !bitmap.isRecycled() ) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("croppedPhotoPath", GlobalConstrants.LOCAL_PATH + "croppedPhotoTmp.png");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    	Intent intent = getIntent();
        String photoPath = intent.getStringExtra("photoPath");
        
        if (photoPath == null ) {
        	finish();
            return;
        }
        
        int size = 0;
        if (isTablet) {
            size = dp(520);
        } else {
            size = Math.max(displaySize.x, displaySize.y);
        }
        
        DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
        
        if ( photoPath.startsWith("file:///mnt/sdcard/")) {
        	
        	try{
    			MemoryCacheUtils.removeFromCache(photoPath, ImageLoader.getInstance().getMemoryCache());
    			DiscCacheUtils.removeFromCache(photoPath, ImageLoader.getInstance().getDiskCache());
    		}catch(Exception e){
    			e.printStackTrace();
    		}
        	
        	imageToCrop = ImageLoader.getInstance().loadImageSync(photoPath, options);
         /*   BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(photoPath, btmapOptions);
            imageToCrop=bm;*/
        	
        	if (imageToCrop == null) {
	        	finish();
	            return;
	        }
	        initImage();
        }else{
	        ImageLoader.getInstance().loadImage(photoPath, options, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					finish();
		            return;
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View imageView, Bitmap loadedImage) {
					imageToCrop = loadedImage;                  
			        if (imageToCrop == null) {
			        	finish();
			            return;
			        }
			        initImage();
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					finish();
		            return;
				}
			});
        }
    }
    
    private void initImage() {
    	int nh = (int) ( imageToCrop.getHeight() * (640.0 / imageToCrop.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(imageToCrop, 640, nh, true);
        if ( scaled != imageToCrop ) {
        	if ( imageToCrop != null && !imageToCrop.isRecycled() )
        		imageToCrop.recycle();
        	imageToCrop = null;
        }
        
        imageToCrop = scaled;
        drawable = new BitmapDrawable(imageToCrop);
        
        view = new PhotoCropView(PhotoCropActivity.this);
        Intent intent = getIntent();
        String group = intent.getStringExtra("group");
        if (group != null && group.equals("group"))
        	view.freeform = true;
        rootView.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	 drawable = null;
         if (imageToCrop != null && !sameBitmap) {
             imageToCrop.recycle();
             imageToCrop = null;
         }
    }

    public void onLowMemory() {
        super.onLowMemory();
    }
}

