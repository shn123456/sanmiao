package com.sanmiao.wypread.utils;

import android.os.Environment;
import android.util.Log;

import com.sanmiao.wypread.bean.BookBean;
import com.sanmiao.wypread.bean.FileBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * 作者 Yapeng Wang
 * 时间 2017/5/17 0017.
 * 类说明{}
 */

public class FileUtils {

    /** 获取SD路径 **/
    public  String getSDPath() {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.getPath();
        }
        return "/sdcard";
    }

    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

    public static void compress(byte[] data, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);

        try {
            dos.write(data, 0, data.length);

            dos.finish();

            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }

    public static byte[] decompress(InputStream is) {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
        try {
            int i = 1024;
            byte[] buf = new byte[i];

            while ((i = iis.read(buf, 0, i)) > 0) {
                o.write(buf, 0, i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return o.toByteArray();
    }

    /**
     * 获取文件
     */
    List<FileBean> name;

    public List<FileBean> getlic(String path) {
        name = new ArrayList();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path1 = new File(path);
            File[] files = path1.listFiles();// 读取
            getFileName(files);
        }
        return name;

    }

    private void getFileName(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {
                    Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
                            + file.getPath().toString());

                    getFileName(file.listFiles());
                    Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
                            + file.getPath().toString());
                } else {
                    FileBean MangerText= new FileBean();
                    String fileName = file.getName();
                    String s = file.toString();
                    MangerText.setName(file.getName().toString());
                    MangerText.setPath(s);
                    name.add(MangerText);
                }
            }
        }
    }
    //获取PDF/TXT文件
    public List<FileBean> getTxtPdf(){
        name = new ArrayList();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path1 = new File("/mnt/sdcard/");
            File[] files = path1.listFiles();// 读取
            getFileName2(files);
        }
        return name;
    }

    private void getFileName2(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if(file.isDirectory() && "wypread".equals(file.getName()) ||"pdf".equals(file.getName())){
                    return;
                }
                if (file.isDirectory()) {
                    Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
                            + file.getPath().toString());

                    getFileName2(file.listFiles());
                    Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
                            + file.getPath().toString());
                } else {
                    FileBean MangerText= new FileBean();
                    String fileName = file.getName();

                    if(fileName.endsWith(".txt")  || fileName.endsWith(".pdf")){
                        String s = file.toString();
                        MangerText.setName(file.getName().toString());
                        MangerText.setPath(s);
                        name.add(MangerText);
                    }
                }
            }
        }
    }
    //获取PDF/TXT文件
    public List<FileBean> getMP3(){
        name = new ArrayList();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path1 = new File("/mnt/sdcard/");
            File[] files = path1.listFiles();// 读取
            getFileName3(files);
        }
        return name;
    }

    private void getFileName3(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if(file.isDirectory() && "wypread".equals(file.getName()) ||"pdf".equals(file.getName())){
                    return;
                }
                if (file.isDirectory()) {
                    Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
                            + file.getPath().toString());

                    getFileName2(file.listFiles());
                    Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
                            + file.getPath().toString());
                } else {
                    FileBean MangerText= new FileBean();
                    String fileName = file.getName();

                    if(fileName.endsWith(".mp3")){
                        String s = file.toString();
                        MangerText.setName(file.getName().toString());
                        MangerText.setPath(s);
                        name.add(MangerText);
                    }
                }
            }
        }
    }

    //获取PDF/TXT文件
    public List<FileBean> getVideo(){
        name = new ArrayList();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path1 = new File("/mnt/sdcard/");
            File[] files = path1.listFiles();// 读取
            getFileName4(files);
        }
        return name;
    }

    private void getFileName4(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if(file.isDirectory() && "wypread".equals(file.getName()) ||"pdf".equals(file.getName())){
                    return;
                }
                if (file.isDirectory()) {
                    Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
                            + file.getPath().toString());

                    getFileName2(file.listFiles());
                    Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
                            + file.getPath().toString());
                } else {
                    FileBean MangerText= new FileBean();
                    String fileName = file.getName();

                    if(fileName.endsWith(".mp4") || fileName.endsWith(".flv") ||fileName.endsWith(".avi") ||fileName.endsWith(".rmvb") ){
                        String s = file.toString();
                        MangerText.setName(file.getName().toString());
                        MangerText.setPath(s);
                        name.add(MangerText);
                    }
                }
            }
        }
    }




    public  void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
               if (childFiles == null || childFiles.length == 0) {
                   file.delete();
                   return;
               }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

}
