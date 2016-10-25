package lt.com.water;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.b.b;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class WaterActivity extends Activity{

    private WebView webview;
    private int  i=0;
    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调</span>
    private Uri imageUri;
    private long mExitTime;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_water);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isAvailable())
        {
            TastyToast.makeText(getApplicationContext(), "请先打开网络数据", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            //当前有可用网络
           WaterActivity.this.finish();
        }
        else
        {
            //当前无可用网络
            getStringUrl();
            initView();

        }

    }

    //获取控件
    private void initView() {
        webview=(WebView)this.findViewById(R.id.waterview);

        WebSettings s =webview.getSettings();
        s.setBuiltInZoomControls(true);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSavePassword(true);
        s.setSaveFormData(true);
        s.setJavaScriptEnabled(true);
        s.setGeolocationEnabled(true);
        s.setUseWideViewPort(true);
        s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");     // enable Web Storage: localStorage, sessionStorage
        s.setDomStorageEnabled(true);
        //运行webview通过URI获取安卓文件
        s.setAllowFileAccess(true);
        webview.addJavascriptInterface(new DemoJavaScriptInterface(WaterActivity.this), "Android");
        webview.requestFocus();

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);

            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                take();
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                if (message != null) {

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                result.cancel();
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                take();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                take();
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                take();
            }
        });

        if (webview!=null){

            webview.loadUrl(url);

            webview.reload();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            }
            else  if (mUploadMessage != null) {
                Log.e("result",result+"");
                if(result==null){
                    mUploadMessage.onReceiveValue(imageUri);
                    mUploadMessage = null;

                    Log.e("imageUri",imageUri+"");
                }else {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
            }
        }
    }


    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {

            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if(results!=null){
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }else{
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }

        return;
    }

    private void take(){
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");

        if (! imageStorageDir.exists()){
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i,"Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        WaterActivity.this.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }


    //获取返回按钮 返回上一个webview
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    final class DemoJavaScriptInterface {

        private  Context context;

        DemoJavaScriptInterface(Context context) {
            this.context=context;

        }
        @JavascriptInterface
        public void showToast(String uid) {
            Intent intent=new Intent();
            intent.putExtra("uid", uid);
            intent.setClass(WaterActivity.this, MyWater.class);
            startActivity(intent);
        }

        //微信朋友圈
        @JavascriptInterface
        public void showWeiXinFriend(String h5url,String imagePath,String title) {
           String Platform="Wechat";
           showShare(Platform, h5url, imagePath, title);

        }

        //微信分享
        @JavascriptInterface
        public void showWeiXin(String h5url,String imagePath,String title) {
            String Platform="WechatMoments";
            showShare(Platform,h5url,imagePath,title);

        }



        //新浪微博
        @JavascriptInterface
        public void showWeibo(String h5url,String imagePath,String title) {
            String Platform="SinaWeibo";
            showShare(Platform,h5url,imagePath,title);

        }

        //复制
        @JavascriptInterface
        public void showFuzhi(String h5url,String imagePath,String title) {
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            ClipData myClip;
            String text =h5url;
            myClip = ClipData.newPlainText("text", text);
            myClipboard.setPrimaryClip(myClip);

            Toast.makeText(getApplicationContext(),"复制成功",Toast.LENGTH_SHORT).show();
        }



        @JavascriptInterface
        public void showMyWater(String uid) {

            WaterActivity.this.finish();

        }

    }


    /**
     * 从别的页面传值 返回h5首页
     */

    public void getStringUrl(){

        try{
            Intent intent=this.getIntent();
            url=intent.getStringExtra("url");
            if ("".endsWith(url)||url==""|url==null){

                url="http://nanchang.shlantian.cn:8080/water/view/appH5/index.html";

            }

        }catch (Exception e){
            url="http://nanchang.shlantian.cn:8080/water/view/appH5/index.html";
            e.printStackTrace();
        }
    }

    //第三方分享 框架实现的方法
    private void showShare(String Platform, final String h5url, final String imagePath, final String title ) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用

        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://"+h5url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://"+h5url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://"+h5url);

        oks.setImageUrl("http://nanchang.shlantian.cn:8080/water/" + imagePath);

        // 是否直接分享（true则直接分享）
        oks. setSilent(true) ;
        // 指定分享平台，和slient一起使用可以直接分享到指定的平台

        oks. setPlatform(Platform) ;


        // 去除注释可通过OneKeyShareCallback来捕获快捷分享的处理结果
        // oks.setCallback(new OneKeyShareCallback());
        //通过OneKeyShareCallback来修改不同平台分享的内容


        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {

                if ("QZone".equals(platform.getName())) {
                    paramsToShare.setTitle(null);
                    paramsToShare.setTitleUrl("http://" + h5url);
                }
                if ("SinaWeibo".equals(platform.getName())) {
                    paramsToShare.setUrl(null);
                    paramsToShare.setText("分享文本 http://www.baidu.com");
                }
                if ("Wechat".equals(platform.getName())) {
                    paramsToShare.setTitle("" + title);
                    paramsToShare.setUrl(h5url);
                    paramsToShare.setImageUrl("http://nanchang.shlantian.cn:8080/water/" + imagePath);
                }
                if ("WechatMoments".equals(platform.getName())) {
                   // Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ssdk_logo);
                    paramsToShare.setTitle("" + title);
                    paramsToShare.setUrl(h5url);
                    paramsToShare.setImageUrl("http://nanchang.shlantian.cn:8080/water/" + imagePath);
                }
            }
        });

      // 启动分享GUI
        oks.show(this);
    }

}
