package com.sanmiao.wypread.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.sanmiao.wypread.R;
import com.sanmiao.wypread.Wypread;
import com.sanmiao.wypread.dao.CommonUtils;
import com.sanmiao.wypread.utils.UtilBox;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/23 0023.
 * 类说明{}
 */

public class PDFActivity extends BaseActivity implements OnPageChangeListener
        , OnLoadCompleteListener, OnDrawListener {

    @InjectView(R.id.pdf_view)
    PDFView pdfView;
    @InjectView(R.id.pdf_tv)
    TextView pdfTv;
    @InjectView(R.id.last_page)
    TextView lastPage;
    @InjectView(R.id.next_page)
    TextView nextPage;
    String bookId = "";
    String jindu = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        displayFromFile1(getIntent().getStringExtra("filePath"), getIntent().getStringExtra("bookName"));
        bookId = getIntent().getStringExtra("bookID");
    }

    @OnClick({R.id.last_page, R.id.next_page})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.last_page:
                pdfView.jumpTo((pdfView.getCurrentPage() - 1) <= 0 ? 0 : (pdfView.getCurrentPage() - 1));
                pdfView.jumpTo(pdfView.getCurrentPage() - 1);
                break;
            case R.id.next_page:
                //pdfView.jumpTo((pdfView.getCurrentPage() + 1) > (pdfView.getPageCount()) ? pdfView.getPageCount() : (pdfView.getCurrentPage() + 1));
                pdfView.jumpTo(pdfView.getCurrentPage() + 1);
                break;
        }
    }

    /**
     * 获取打开网络的pdf文件
     * @param fileUrl
     * @param fileName
     */
    private void displayFromFile1(String fileUrl, String fileName) {
//        showProgress();
        File f =new File(fileUrl);
        pdfView.fromFile(f).defaultPage(0).onPageChange(this).swipeHorizontal(true).load();
    }

    /**
     * 翻页回调
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {

        pdfTv.setText(page+"/"+pageCount);
        int jd =  page*100 / pageCount;
        jindu = jd+"%";
    }

    /**
     * 加载完成回调
     *
     * @param nbPages 总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
//        Toast.makeText(PDFActivity.this, "加载完成" + nbPages, Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示对话框
     */
    private void showProgress() {
        UtilBox.showDialog(this, "加载数据中,请稍候");
    }

    /**
     * 关闭等待框
     */
    private void hideProgress() {
        UtilBox.dismissDialog();
    }
    @Override
    public int setBaseView() {
        return R.layout.activity_pdf01;
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

    CommonUtils dbUtils;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wypread wypread = new Wypread();
        if(bookId!=null){
            try {
                int pos=Integer.valueOf(bookId);
                dbUtils=new CommonUtils(this);
                //根据ID来修改
                wypread.setId((long)pos);
                //需要重新设置参数,只设置一位其它值变为null
                wypread.setName(getIntent().getStringExtra("name"));
                wypread.setBookPath(getIntent().getStringExtra("filePath"));
                wypread.setImgPath(getIntent().getStringExtra("imgPath"));
                wypread.setWriteName(getIntent().getStringExtra("writer"));
                wypread.setClassfiy(getIntent().getStringExtra("classfiy"));
                //设置阅读进度
                wypread.setIsDown(getIntent().getStringExtra("isDown"));
                wypread.setReadJindu(jindu);
                dbUtils.updateWypread(wypread);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
