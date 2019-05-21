
package com.sanmiao.wypread.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.iflytek.cloud.thirdparty.S;
import com.sanmiao.wypread.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class BookPageFactory {

    private File book_file = null;
    private MappedByteBuffer m_mbBuf = null;
    private int m_mbBufLen = 0;
    private int m_mbBufBegin = 50; //50
    private int m_mbBufEnd = 0;
    private String m_strCharsetName = "UTF-8";
    private Bitmap m_book_bg = null;
    private int mWidth;
    private int mHeight;

    private Vector<String> m_lines = new Vector<String>();
    Context context;
    private int m_fontSize = 40;
    private int r_fontSize = 30;
    private int m_textColor =R.color.textColor2;//画笔颜色
    private int m_backColor = 0xffff9e85; // 背景颜色
    private int marginWidth = 30; // 左右与边缘的距离
    private int marginHeight = 100; // 上下与边缘的距离
    private int mLineCount; // 每页可以显示的行数
    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private boolean m_isfirstPage, m_islastPage;
    private int b_FontSize = 25;//底部文字大小
    private int e_fontSize = 5;
    private int spaceSize = 35;//行间距大小
    private int curProgress = 0;//当前的进度
    private String fileName = "";

    // private int m_nLineSpaceing = 5;

    private Paint mPaint;
    private Paint bPaint;//底部文字绘制
    private Paint spactPaint;//行间距绘制
    private Paint titlePaint;//标题绘制
    List<String> list = new ArrayList<>();//获取朗读文字集合


    String readStr="";

    public String getStrPercent1() {
        return strPercent1;
    }

    public void setStrPercent1(String strPercent1) {
        this.strPercent1 = strPercent1;
    }

    String strPercent1="0%";
    //电池电量
    private float mPower = 0f;

    public BookPageFactory(Context context,int w, int h) {
        // TODO Auto-generated constructor stub
        this.context=context;
        mWidth = w;
        mHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Align.LEFT);
//        mPaint.setTypeface(Typeface.DEFAULT);
        //mPaint.setTextSize(30);
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(context.getResources().getColor(m_textColor));
        //mPaint.setTextSkewX(0.1f);//设置斜体
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        int totalSize = m_fontSize+spaceSize;
        mLineCount = (int) ((mVisibleHeight)/ totalSize); // 可显示的行数
        //底部文字绘制
        bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bPaint.setTextAlign(Align.LEFT);
        bPaint.setTextSize(b_FontSize);
        bPaint.setColor(context.getResources().getColor(m_textColor));
        //行间距设置
        spactPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        spactPaint.setTextAlign(Align.LEFT);
        spactPaint.setTextSize(spaceSize);
        spactPaint.setColor(context.getResources().getColor(m_textColor));
        //
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextAlign(Align.LEFT);
        titlePaint.setTextSize(30);
        titlePaint.setColor(context.getResources().getColor(m_textColor));

    }

    public void openbook(String strFilePath) {
        try {
            File file= new File(strFilePath);
            m_strCharsetName=UtilBox.getCharset(file);

            book_file = new File(strFilePath);
            long lLen = book_file.length();
            m_mbBufLen = (int) lLen;
            m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(
                    FileChannel.MapMode.READ_ONLY, 0, lLen);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected byte[] readParagraphBack(int nFromPos) {
        int nEnd = nFromPos;
        int i;
        byte b0, b1;
        if (m_strCharsetName.equals("UTF-16LE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }

        } else if (m_strCharsetName.equals("UTF-16BE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else if(m_strCharsetName.equals("GB2312")){
            i = nEnd - 1;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                if (b0 == 0x0a && i != nEnd - 1) {
                    i++;
                    break;
                }
                i--;
            }
        } else {
            i = nEnd - 1;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                if (b0 == 0x0a && i != nEnd - 1) {
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0)
            i = 0;
        int nParaSize = nEnd - i;
        int j;
        byte[] buf = new byte[nParaSize];
        for (j = 0; j < nParaSize; j++) {
            buf[j] = m_mbBuf.get(i + j);
        }
        return buf;
    }

    // 读取上一段落
    protected byte[] readParagraphForward(int nFromPos) {
        int nStart = nFromPos;
        int i = nStart;
        byte b0, b1;
        // 根据编码格式判断换行
        if (m_strCharsetName.equals("UTF-16LE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < m_mbBufLen) {
                b0 = m_mbBuf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        int nParaSize = i - nStart;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = m_mbBuf.get(nFromPos + i);
        }
        return buf;
    }

    protected Vector<String> pageDown() {
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
            byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 读取一个段落
            m_mbBufEnd += paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
                list.add(strParagraph);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String strReturn = "";
            if (strParagraph.indexOf("\r\n") != -1) {
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.indexOf("\n") != -1) {
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
                if (lines.size() >= mLineCount) {
                    break;
                }
            }
            if (strParagraph.length() != 0) {
                try {
                    m_mbBufEnd -= (strParagraph + strReturn)
                            .getBytes(m_strCharsetName).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }

    protected void pageUp() {
        if (m_mbBufBegin < 0)
            m_mbBufBegin = 0;
        Vector<String> lines = new Vector<String>();
        String strParagraph = "";
        while (lines.size() < mLineCount && m_mbBufBegin > 0) {
            Vector<String> paraLines = new Vector<String>();
            byte[] paraBuf = readParagraphBack(m_mbBufBegin);
            m_mbBufBegin -= paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");

            if (strParagraph.length() == 0) {
                //paraLines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                paraLines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
            }
            lines.addAll(0, paraLines);
        }
        while (lines.size() > mLineCount) {
            try {
                m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        m_mbBufEnd = m_mbBufBegin;
        return;
    }

    public void prePage() throws IOException {
        if (m_mbBufBegin == 0) {
            m_mbBufBegin = 0;
            m_isfirstPage = true;
            return;
        } else
            m_isfirstPage = false;

        m_islastPage = false;
        m_lines.clear();
        list.clear();
        pageUp();
        m_lines = pageDown();
    }

    public int getM_mbBufEnd() {
        return m_mbBufEnd;
    }

    public void setM_mbBufEnd(int m_mbBufEnd) {
        this.m_mbBufEnd = m_mbBufEnd;
    }

    public int getM_mbBufBegin() {
        return m_mbBufBegin;
    }

    public void setM_mbBufBegin(int m_mbBufBegin) {
        this.m_mbBufBegin = m_mbBufBegin;
    }

    public void nextPage() throws IOException {
        if (m_mbBufEnd >= m_mbBufLen) {
            m_islastPage = true;
            return;
        } else
            m_islastPage = false;
        m_lines.clear();
        m_mbBufBegin = m_mbBufEnd;
        if(m_mbBufBegin==0){
            m_isfirstPage =true;
        }else{
            m_isfirstPage =false;
        }

        list.clear();
//        pageDown();
        m_lines = pageDown();
    }

    public  int  getMmbBufBegin(){
        return m_mbBufBegin;
    }


    public String getStrPercent() {
        return strPercent;
    }

    public void setStrPercent(String strPercent) {
        this.strPercent = strPercent;
    }

    public void onDraw(Canvas c , float mPower) {
        this.mPower=mPower;
        if (m_lines.size() == 0){
            list.clear();
            m_lines = pageDown();
        }
        if (m_lines.size() > 0) {
            if (m_book_bg == null)
                c.drawColor(m_backColor);
            else
                c.drawBitmap(m_book_bg, 0, 0, null);
            int y = marginHeight;
            int i = 0;
            for (String strLine : m_lines) {
                y += m_fontSize;
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                c.drawText(strLine, marginWidth, y, mPaint);
                y+=spaceSize;
                if(i!=m_lines.size()-1){
                    c.drawText("", marginWidth, y, spactPaint);
                }
                i++;
            }
        }
        float fPercent = (float) ((m_mbBufBegin) * 1.0 / m_mbBufLen);
        DecimalFormat df = new DecimalFormat("#0.0");
        strPercent= df.format(fPercent * 100) + "%";
        strPercent1=((int)(fPercent * 100)) + "%";
        curProgress = (int)round1(fPercent * 100,0);
        int nPercentWidth = (int) bPaint.measureText("99.9%") + 1;
        c.drawText(strPercent, mWidth - nPercentWidth-20, 30, bPaint);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        c.drawText(str, 10, mHeight-20, bPaint);
        int titleWidth = (int) bPaint.measureText("《"+fileName+"》") + 1;
        c.drawText("《"+fileName+"》", 5, 30, bPaint);

        int battery_left = 80;
        int battery_top = mHeight-40;
        int battery_width = 35;
        int battery_height = 20;

        int battery_head_width = 5;
        int battery_head_height = 5;

        int battery_inside_margin = 3;
        //先画外框
        bPaint.setAntiAlias(true);
        bPaint.setStyle(Paint.Style.STROKE);
        Rect rect = new Rect(battery_left, battery_top,
                battery_left + battery_width, battery_top + battery_height);
        c.drawRect(rect, bPaint);
        float power_percent = mPower / 100.0f;
        bPaint.setStyle(Paint.Style.FILL);
        //画电量
        if(power_percent != 0) {
            int p_left = battery_left + battery_inside_margin;
            int p_top = battery_top + battery_inside_margin;
            int p_right = p_left - battery_inside_margin + (int)((battery_width - battery_inside_margin) * power_percent);
            int p_bottom = p_top + battery_height - battery_inside_margin * 2;
            Rect rect2 = new Rect(p_left, p_top, p_right , p_bottom);
            c.drawRect(rect2, bPaint);
        }
        //画电池头
        int h_left = battery_left + battery_width;
        int h_top = battery_top + battery_height / 2 - battery_head_height / 2;
        int h_right = h_left + battery_head_width;
        int h_bottom = h_top + battery_head_height;
        Rect rect3 = new Rect(h_left, h_top, h_right, h_bottom);
        c.drawRect(rect3, bPaint);

}
    String strPercent="0%";
    public void setPower(Canvas c , float mPower){
        this.mPower=mPower;
        int battery_left = 80;
        int battery_top = mHeight-40;
        int battery_width = 35;
        int battery_height = 20;

        int battery_head_width = 5;
        int battery_head_height = 5;

        int battery_inside_margin = 3;
        //先画外框
        bPaint.setAntiAlias(true);
        bPaint.setStyle(Paint.Style.STROKE);
        Rect rect = new Rect(battery_left, battery_top,
                battery_left + battery_width, battery_top + battery_height);
        c.drawRect(rect, bPaint);

        float power_percent = mPower / 100.0f;
        bPaint.setStyle(Paint.Style.FILL);
        //画电量
        if(power_percent != 0) {
            int p_left = battery_left + battery_inside_margin;
            int p_top = battery_top + battery_inside_margin;
            int p_right = p_left - battery_inside_margin + (int)((battery_width - battery_inside_margin) * power_percent);
            int p_bottom = p_top + battery_height - battery_inside_margin * 2;
            Rect rect2 = new Rect(p_left, p_top, p_right , p_bottom);
            c.drawRect(rect2, bPaint);
        }
        //画电池头
        int h_left = battery_left + battery_width;
        int h_top = battery_top + battery_height / 2 - battery_head_height / 2;
        int h_right = h_left + battery_head_width;
        int h_bottom = h_top + battery_head_height;
        Rect rect3 = new Rect(h_left, h_top, h_right, h_bottom);
        c.drawRect(rect3, bPaint);
    }


    private static double round1(double v, int scale) {
        if (scale < 0)
            return v;
        String temp = "#####0.";
        for (int i = 0; i < scale; i++) {
            temp += "0";
        }
        return Double.valueOf(new java.text.DecimalFormat(temp).format(v));
    }

    public void setBgBitmap(Bitmap BG) {
        if (BG.getWidth() != mWidth || BG.getHeight() != mHeight)
            m_book_bg = Bitmap.createScaledBitmap(BG, mWidth, mHeight, true);
        else
            m_book_bg = BG;
    }

    public int getM_textColor() {
        return m_textColor;
    }

    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
        if(m_textColor ==0)
            m_textColor=R.color.textColor2;


//        mPaint.setColor(context.getResources().getColor(m_textColor));
//        bPaint.setColor(context.getResources().getColor(m_textColor));
//        titlePaint.setColor(context.getResources().getColor(m_textColor));
//        spactPaint.setColor(context.getResources().getColor(m_textColor));
    }

    public String getReadStr() {
        readStr="";
        for (int i =0 ; i<list.size(); i ++){
            readStr += list.get(i);
        }
        return readStr;
    }

    public void setReadStr(String readStr) {
        this.readStr = readStr;
    }

    public boolean isfirstPage() {
        return m_isfirstPage;
    }
    public void setIslastPage(boolean islast){
        m_islastPage = islast;
    }
    public boolean islastPage() {
        return m_islastPage;
    }
    public int getCurPostion() {
        return m_mbBufEnd;
    }

    public int getCurPostionBeg(){
        return m_mbBufBegin;
    }
    public void setBeginPos(int pos) {
        m_mbBufEnd = pos;
        m_mbBufBegin = pos;
    }

    public int getBufLen() {
        return m_mbBufLen;
    }

    public int getCurProgress(){
        return curProgress;
    }
    public String getOneLine() {
        return m_lines.toString().substring(0, 10);
    }

    public void changBackGround(int color) {
        mPaint.setColor(color);
    }

    public void setFontSize(int size) {
        m_fontSize = size;
        mPaint.setTextSize(size);
        int totalSize = m_fontSize+spaceSize;
        mLineCount = (int) (mVisibleHeight / totalSize); // 可显示的行数
    }

    public void setFileName(String fileName){
        fileName = fileName.substring(0,fileName.indexOf("."));
        this.fileName = fileName;
    }





}
