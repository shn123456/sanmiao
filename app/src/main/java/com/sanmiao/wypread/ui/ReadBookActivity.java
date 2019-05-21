package com.sanmiao.wypread.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.textservice.TextServicesManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.adapter.BgColorAdapter;
import com.sanmiao.wypread.adapter.QuiteDetailsListAdapter;
import com.sanmiao.wypread.bean.BGColor;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.utils.BookPage;
import com.sanmiao.wypread.utils.BookPageFactory;
import com.sanmiao.wypread.utils.PageWidget;
import com.sanmiao.wypread.utils.RecycleViewDivider;
import com.sanmiao.wypread.utils.SharedPreferenceUtil;
import com.sanmiao.wypread.utils.UtilBox;
import com.sanmiao.wypread.utils.Workaround;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/8 0008.
 * 类说明{书籍阅读}
 */

public class ReadBookActivity extends BaseActivity {
    PageWidget pageWidget;//贝塞尔曲线
    private SpeechSynthesizer mTts;
    BookPage bookPage;
    final List<BGColor> bgList = new ArrayList<>();//背景颜色集合
    private Bitmap curBitmap, nextBitmap;
    private Canvas curCanvas, nextCanvas;//画布
    private String bookPath, bookName;//图书路径 书名称
    final String[] font = new String[]{"24", "28", "32", "36", "40","44" ,"48", "52","56", "60", "64","68", "72","76",
            "80", "84","88","92", "96"};//字体大小
    int curPostion = 0;
    int fontSize=4;//字体调节
    BookPageFactory pageFactory;//图书工具类
    CommonUtils dbUtils;
    BatteryReceiver batteryReceiver;
    private SynthesizerListener mSynListener;
    String bookId="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Workaround.assistActivity(findViewById(android.R.id.content));
        ButterKnife.inject(this);
        //华为手机菜单监听 (华为手机并且有虚拟按键)
        if(UtilBox.isHUAWEI() && UtilBox.checkDeviceHasNavigationBar(this)){
            getContentResolver().registerContentObserver(Settings.System.getUriFor
                    ("navigationbar_is_min"), true, mNavigationStatusObserver);

        }
//        //让虚拟键盘一直不显示
//        Window window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
//        window.setAttributes(params);
        //获取当前电量
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, intentFilter);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=59196507");
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        bookId=getIntent().getStringExtra("bookID");
        addBg();
        initView();
    }


    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            int navigationBarIsMin = Settings.System.getInt(getContentResolver(),
                    "navigationbar_is_min", 0);
            if (navigationBarIsMin == 1) {
//                Toast.makeText(ReadBookActivity.this, "的商机啊的撒", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(ReadBookActivity.this, "saa", Toast.LENGTH_SHORT).show();
            }
        }
    };



    //初始化视图
    private void initView() {

        bookPath=getIntent().getStringExtra("bookPath");
        bookName=getIntent().getStringExtra("bookName");

        //设置两个画布
        Display display = getWindowManager().getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        curBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        nextBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        curCanvas = new Canvas(curBitmap);
        nextCanvas = new Canvas(nextBitmap);
        pageFactory = new BookPageFactory(this,w, h);//设置宽高
        int bgPosition =SharedPreferenceUtil.getIntData("bgBitmap");
        pageFactory.setBgBitmap(BitmapFactory.decodeResource(getResources(),bgList.get(bgPosition).getUseBg()));//设置背景
        pageFactory.setM_textColor(SharedPreferenceUtil.getIntData("textColor"));//设置字体颜色

        /**书签打开*/
        pageFactory.setFileName(bookName+".txt");
        pageWidget = new PageWidget(this, w, h);
        setContentView(pageWidget);
        bookPage = new BookPage(this, w, h);
        setContentView(bookPage);
        pageFactory.openbook(bookPath);
        //设置字体
        String textSzie=SharedPreferenceUtil.getStringData("textSize");
        if (TextUtils.isEmpty(textSzie))//默认20
            textSzie="4";

        String a = font[Integer.parseInt(textSzie)];
        pageFactory.setFontSize(Integer.parseInt(a));
        // 查询数据库 阅读进度
        if(bookId!=null && bookId.length()<8){
            int bookIDInt=Integer.valueOf(bookId);
            Wypread wypread = new Wypread();
            dbUtils=new CommonUtils(this);
            wypread = dbUtils.listOneWypread(bookIDInt);
            if(wypread.getReadPosition() == null)
                curPostion=0;
            else
                curPostion=wypread.getReadPosition();
        }else if(bookId!=null && bookId.length()>8){
            long bookIDInt=Long.valueOf(bookId);
            Wypread wypread = new Wypread();
            dbUtils=new CommonUtils(this);
            wypread = dbUtils.listOneWypread(bookIDInt);
            if(wypread.getReadPosition() == null)
                curPostion=0;
            else
                curPostion=wypread.getReadPosition();
        }

        int pos = Integer.valueOf(curPostion + "");
        pageFactory.setBeginPos(pos);

        try {
            pageFactory.prePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pageFactory.onDraw(curCanvas,mBatteryLevel);
        bookPage.setBitmaps(curBitmap, nextBitmap);
        bookPage.postInvalidate();
        bookPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                if(mTts!=null)
                    mTts.stopSpeaking();
                if (v == bookPage) {
                    //根据点击坐标点判断是否翻页和弹出菜单
                    double w=UtilBox.getWindowWith(ReadBookActivity.this);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        double downX = event.getX();
                        if (downX < w / 2 - 100 ) {
                            if(pageFactory.getMmbBufBegin()>0){
                                //点击位置
                                bookPage.abortAnimation();
                                bookPage.calcCornerXY(event.getX(), event.getY());
                                pageFactory.onDraw(curCanvas,mBatteryLevel);
                                //从左向右翻
                                try {
                                    pageFactory.prePage();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                pageFactory.onDraw(nextCanvas,mBatteryLevel);
                            }else{
                                Toast.makeText(ReadBookActivity.this, "已经是第一页", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        } else if (downX > w / 2 + 100) {
                            if(pageFactory.getM_mbBufEnd() != pageFactory.getBufLen()){
                                //点击位置
                                bookPage.abortAnimation();
                                bookPage.calcCornerXY(event.getX(), event.getY());
                                pageFactory.onDraw(curCanvas,mBatteryLevel);
                                try {
                                    pageFactory.nextPage();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                pageFactory.onDraw(nextCanvas,mBatteryLevel);
                            }else{
                                Toast.makeText(ReadBookActivity.this, "已经是最后一页", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        } else {
                            showSet();
                            return false;
                        }
                        bookPage.setBitmaps(curBitmap, nextBitmap);
                    }
                    ret = bookPage.doTouchEvent(event);
                    return ret;
                }
                return false;
            }
        });
    }


    //显示一级菜单
    private void  showSet(){
        final Dialog dialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_menu, null);
        TextView back=(TextView) view.findViewById(R.id.menu_back);
        TextView read=(TextView) view.findViewById(R.id.menu_read);
        TextView set=(TextView) view.findViewById(R.id.menu_set);
        //dialog底部弹出
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM);
        //点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkTTS();
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSet2();
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        UtilBox.setMatchWith(dialog);
    }
    private void addBg(){
        BGColor bgColor1= new BGColor(R.mipmap.icon_bg1,R.mipmap.icon_bg1_pre,R.mipmap.bg1);
        BGColor bgColor2= new BGColor(R.mipmap.icon_bg2,R.mipmap.icon_bg2,R.mipmap.bg2);
        BGColor bgColor3= new BGColor(R.mipmap.icon_bg3,R.mipmap.icon_bg3_pre,R.mipmap.bg3);
        BGColor bgColor4= new BGColor(R.mipmap.icon_bg4,R.mipmap.icon_bg4_pre,R.mipmap.bg4);
        BGColor bgColor5= new BGColor(R.mipmap.icon_bg5,R.mipmap.icon_bg5_pre,R.mipmap.bg5);
        BGColor bgColor6= new BGColor(R.mipmap.icon_bg6,R.mipmap.icon_bg6,R.mipmap.bg6);
        bgList.add(bgColor1);
        bgList.add(bgColor2);
        bgList.add(bgColor3);
        bgList.add(bgColor4);
        bgList.add(bgColor5);
        bgList.add(bgColor6);
    }


    //显示二级菜单
    private void  showSet2(){
        final Dialog dialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_set2, null);
        ImageView plus=(ImageView) view.findViewById(R.id.menu_plus);
        ImageView minus=(ImageView) view.findViewById(R.id.menu_minus);
        final TextView size=(TextView) view.findViewById(R.id.menu_size);
        RecyclerView rv= (RecyclerView)view.findViewById(R.id.menu_Rv);
        final String ts=SharedPreferenceUtil.getStringData("textSize");
        if(TextUtils.isEmpty(ts)){
            size.setText("20");
        }else{
            int  i = Integer.valueOf(font[Integer.parseInt(ts)]) /2 ;

            size.setText(i + "");
        }
        //dialog底部弹出
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM);
        //设置背景颜色
        final BgColorAdapter bgColorAdapter;
        //图书布局
        bgColorAdapter = new BgColorAdapter(this, bgList, false);
        GridLayoutManager manager = new GridLayoutManager(this,6);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        rv.setAdapter(bgColorAdapter);
        //选择背景
        bgColorAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < bgList.size(); j++) {
                    bgList.get(j).setCheck(false);
                }
                bgList.get(i).setCheck(true);
                bgColorAdapter.notifyDataSetChanged();
                pageFactory.setBgBitmap(BitmapFactory.decodeResource(getResources(),bgList.get(i).getUseBg()));//设置背景
                //如果背景为黑  字体变白
                if(i ==(bgList.size()-1)){
                    pageFactory.setM_textColor(Color.WHITE);
                    SharedPreferenceUtil.SaveData("textColor",Color.WHITE);
                }else{
                    pageFactory.setM_textColor(Color.BLACK);
                    SharedPreferenceUtil.SaveData("textColor",Color.BLACK);
                }
                SharedPreferenceUtil.SaveData("bgBitmap",i);
                pageFactory.onDraw(curCanvas,mBatteryLevel);
                pageFactory.onDraw(nextCanvas,mBatteryLevel);
                bookPage.setBitmaps(curBitmap, nextBitmap);
                bookPage.postInvalidate();
            }
        });
        if(!TextUtils.isEmpty(ts)){
            fontSize= Integer.valueOf(ts);
        }
        //点击事件
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ReadBookActivity.this, "字体+1", Toast.LENGTH_SHORT).show();
                if (fontSize==18)
                    return;
                fontSize++;
                int in=Integer.valueOf(font[fontSize]);
                size.setText((in/2)+"");
                SharedPreferenceUtil.SaveData("textSize",fontSize);
                refreshUI();
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ReadBookActivity.this, "字体-1", Toast.LENGTH_SHORT).show();
                if(fontSize==0)
                    return;
                fontSize--;
                int in=Integer.valueOf(font[fontSize]);
                size.setText((in/2)+"");
                SharedPreferenceUtil.SaveData("textSize",fontSize);
                refreshUI();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        UtilBox.setMatchWith(dialog);
    }
    /**语音选择*/
    private void checkTTS(){
        final Dialog dialog = new Dialog(this, R.style.MyDialogStyle);
        View view = View.inflate(this, R.layout.dialog_tts, null);
        TextView mandarinMan=(TextView) view.findViewById(R.id.Mandarin_man);
        TextView mandarinWoman=(TextView) view.findViewById(R.id.Mandarin_woman);
        TextView cantonese=(TextView) view.findViewById(R.id.cantonese);
        TextView sichuan=(TextView) view.findViewById(R.id.sichuan);
        TextView dongbei=(TextView) view.findViewById(R.id.dongbei);
        TextView henan=(TextView) view.findViewById(R.id.henan);
        TextView hunan=(TextView) view.findViewById(R.id.hunan);
        TextView no=(TextView) view.findViewById(R.id.tts_no);
        //dialog底部弹出
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity( Gravity.BOTTOM);

        final String str =pageFactory.getReadStr();
        //普通话男
        mandarinMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyu");
                set_mTts();
                mTts.startSpeaking(
                    str, mTtsListener);
            }
        });
        //普通话女
        mandarinWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
                set_mTts();
                mTts.startSpeaking(
                        str, mTtsListener);
            }
        });
        //粤语
        cantonese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaomei");
                set_mTts();
                mTts.startSpeaking(
                        str, mTtsListener);
            }
        });
        //四川话
        sichuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaorong");
                set_mTts();
                mTts.startSpeaking(
                        str, mTtsListener);
            }
        });
        //东北话
        dongbei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqian");
                set_mTts();
                mTts.startSpeaking(
                        str, mTtsListener);
            }
        });
        //河南话
        henan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaokun");
                set_mTts();
                mTts.startSpeaking(
                        str, mTtsListener);
            }
        });
        //湖南话
        hunan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqiang");
                set_mTts();
                mTts.startSpeaking(
                        str, mTtsListener);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        UtilBox.setMatchWith(dialog);
    }
    /**语音合成设置*/
    private void set_mTts() {
        // 设置发音人
        // 设置语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        // 设置音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        // 设置音量0-100
        mTts.setParameter(SpeechConstant.VOLUME, "100");
    }
    /***语音合成回调*/
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        // 缓冲进度回调，arg0为缓冲进度，arg1为缓冲音频在文本中开始的位置，arg2为缓冲音频在文本中结束的位置，arg3为附加信息
        @Override
        public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
            // TODO Auto-generated method stub
        }
        // 会话结束回调接口，没有错误时error为空
        @Override
        public void onCompleted(SpeechError error) {
            mTts.stopSpeaking();
            String str = null;
            try {
                pageFactory.nextPage();
                str =pageFactory.getReadStr();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (pageFactory.islastPage()) {
                Toast.makeText(ReadBookActivity.this, "已经是最后一页", Toast.LENGTH_SHORT).show();
            }
            pageFactory.onDraw(nextCanvas,mBatteryLevel);
            bookPage.setBitmaps(curBitmap, nextBitmap);
            Display display = getWindowManager().getDefaultDisplay();
            int w = display.getWidth();
            int h = display.getHeight();
            bookPage.startAnimation2(w,h,1200);
            bookPage.postInvalidate();
            mTts.startSpeaking(str, mTtsListener);
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
        // 开始播放
        @Override
        public void onSpeakBegin() {
            // TODO Auto-generated method stub
//            Toast.makeText(ReadBookActivity.this, "start", Toast.LENGTH_SHORT).show();
        }
        // 停止播放
        @Override
        public void onSpeakPaused() {
            // TODO Auto-generated method stub
//            Toast.makeText(ReadBookActivity.this, "stop", Toast.LENGTH_SHORT).show();

        }
        // 播放进度回调,arg0为播放进度0-100；arg1为播放音频在文本中开始的位置，arg2为播放音频在文本中结束的位置。
        @Override
        public void onSpeakProgress(int arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub

        }
        // 恢复播放回调接口
        @Override
        public void onSpeakResumed() {
            // TODO Auto-generated method stub
        }
    };

    /**字体加减刷新当前页面*/
    private void refreshUI(){
        pageFactory.setFontSize(Integer.parseInt(font[fontSize]));
        try {
            pageFactory.prePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pageFactory.onDraw(curCanvas,mBatteryLevel);
        pageFactory.onDraw(nextCanvas,mBatteryLevel);
        bookPage.setBitmaps(curBitmap, nextBitmap);
        bookPage.postInvalidate();
    }

    @Override
    public int setBaseView() {
        return R.layout.activity_readbook;
    }

    @Override
    public boolean showTitle() {
        return false;
    }

    @Override
    public String setTitleText() {
        return null;
    }

    @Override
    public boolean showMore() {
        return false;
    }

    //保存书签
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pageFactory!=null){
            curPostion = pageFactory.getCurPostion();
            Wypread wypread = new Wypread();
            if(bookId!=null && bookId.length()<8){
                int pos=Integer.valueOf(bookId);
                //根据ID来修改
                wypread.setId((long)pos);
                //需要重新设置参数,只设置一位其它值变为null
                wypread.setName(getIntent().getStringExtra("name"));
                wypread.setBookPath(bookPath);
                wypread.setImgPath(getIntent().getStringExtra("imgPath"));
                wypread.setWriteName(getIntent().getStringExtra("writer"));
                wypread.setClassfiy(getIntent().getStringExtra("classfiy"));
                //设置阅读进度
                wypread.setReadPosition(curPostion);
                wypread.setIsDown(getIntent().getStringExtra("isDown"));
                wypread.setReadJindu(pageFactory.getStrPercent1());
                dbUtils.updateWypread(wypread);
            }else if(bookId!=null && bookId.length()>8){
                long pos=Long.valueOf(bookId);
                //根据ID来修改
                wypread.setId((long)pos);
                //需要重新设置参数,只设置一位其它值变为null
                wypread.setName(getIntent().getStringExtra("bookName"));
                wypread.setBookPath(bookPath);
                wypread.setImgPath(getIntent().getStringExtra("bookID"));
                wypread.setWriteName(getIntent().getStringExtra("writer"));
                wypread.setClassfiy(getIntent().getStringExtra("classfiy"));
                //设置阅读进度
                wypread.setReadPosition(curPostion);
                wypread.setIsDown(getIntent().getStringExtra("isDown"));
                wypread.setReadJindu(pageFactory.getStrPercent1());
                wypread.setFromFile("book");
                dbUtils.updateWypread(wypread);
            }
        }

        SharedPreferenceUtil.SaveData("textSize",fontSize);
        //销毁电池广播
        unregisterReceiver(batteryReceiver);
        if(mTts !=null){
            mTts.stopSpeaking();
            mTts.destroy();// 退出时释放连接
        }

    }
    /**获取当前电量*/
    private int mBatteryLevel;
    private int mBatteryScale;
    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断它是否是为电量变化的Broadcast Action
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                //获取当前电量
                mBatteryLevel = intent.getIntExtra("level", 0);
                pageFactory.setPower(curCanvas,mBatteryLevel);
                pageFactory.setPower(nextCanvas,mBatteryLevel);
                //电量的总刻度
                mBatteryScale = intent.getIntExtra("scale", 100);
            }
        }
    }
//    /**华为手机菜单显示隐藏监听*/
//    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
//        @Override
//        public void onChange(boolean selfChange) {
//            //TODO: deal with data change
//            Log.i("yyj","selfChange = " + selfChange);
//            int navigationBarIsMin = Settings.System.getInt(getContentResolver(), "navigationbar_is_min", 0);
//        }
//    };

}
