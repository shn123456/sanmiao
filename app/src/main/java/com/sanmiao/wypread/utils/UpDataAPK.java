package com.sanmiao.wypread.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.sanmiao.wypread.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * 
 * @author 李军帅
 * 更新apk
 * 2016/5/26
 * change 李军帅 2016/5/30
 * 修改二手家电压缩包的名字
 *
 */


public class UpDataAPK {

	private Context context;//应用上下文对象
	private String upDataMsg = "有新的版本，是否立即下载";
	private String APKURL;//服务器上的版本号
	private Dialog noticeDialog;//提示对话框
	private Dialog downloadDialog;//下载对话框
	
	
	private File file;//apk下载到的文件
	
	private ProgressBar progressBar;//进度条
	private static final int DOWN_UPDATA = 1;//正在下载数据
	private static final int DOWN_OVER = 2;//下载完成
	private int progress;//当前下载进度
	private Thread downloadThread;//下载apk启动子线程
	private boolean interceptFlag = false;
	private Activity a;//调用本类的activity

	public UpDataAPK(Context context, String URL, Activity a) {
		this.context = context;
		this.APKURL = URL;
		this.a = a;
	}

	public void checkUpdataInfo() {
		showNoticeDialog();
	}
	
	/**
	 * 是否更新
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("版本检测");
		builder.setMessage(upDataMsg);
		builder.setPositiveButton("是", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				downloadAPK();
				UtilBox.showDialog(context,"正在更新");
//				showDonloadDialog();
			}
		});
		builder.setNegativeButton("否", new OnClickListener() {


			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

//	/**
//	 * 下载apk的对话框
//	 */
//	private void showDonloadDialog() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("更新版本");
//		final LayoutInflater inflater = LayoutInflater.from(context);
//		View v = inflater.inflate(R.layout.progressbar, null);
//		progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
//		builder.setView(v);
//		builder.setNegativeButton("取消", new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				// TODO Auto-generated method stub
//				arg0.dismiss();
//				interceptFlag = true;
//			}
//		});
//		downloadDialog = builder.create();
//		downloadDialog.setCanceledOnTouchOutside(false);
//		downloadDialog.show();
//		downloadAPK();//下载apk
//	}

	/**
	 * 下载apk
	 */
	private void downloadAPK() {
		downloadThread = new Thread(runnable);
		downloadThread.start();
	}

	TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		public X509Certificate[] getAcceptedIssuers(){return null;}
		public void checkClientTrusted(X509Certificate[] certs, String authType){}
		public void checkServerTrusted(X509Certificate[] certs, String authType){}
	}};

	/**
	 * 子线程下载apk到本地文件夹
	 */
	private Runnable runnable = new Runnable() {

		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
//				HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
//				SSLContext sc = SSLContext.getInstance("TLS");
//				sc.init(null, trustAllCerts, new SecureRandom());
//				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				URL url = new URL(APKURL);
				HttpURLConnection connecticn = (HttpURLConnection) url.openConnection();

				connecticn.connect();
				int length = connecticn.getContentLength();
				InputStream is = connecticn.getInputStream();
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					String path= Environment.getExternalStorageDirectory().getAbsolutePath();
					file=new File(path+"/Read");
//					file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"esjd.apk");
				}else{
					file=new File(context.getCacheDir()+"/Read");
				}
				if(!file.exists()){
					file.mkdirs();
				}
				file =new File(file, "Read.apk");
				if(!file.exists()){
					file.createNewFile();
				}
				
				FileOutputStream fos = new FileOutputStream(file);
				int count = 0;
				byte[] bt = new byte[1024];
				do {
					int numRead = is.read(bt);
					count += numRead;
					progress = (int) (((float) count / length) * 100);
					handler.sendEmptyMessage(DOWN_UPDATA);
					if (numRead <= 0) {
						handler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(bt, 0, numRead);
				} while (!interceptFlag);
				fos.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				UtilBox.dismissDialog();
//				downloadDialog.dismiss();
			}
		}
	};

	/**
	 * 安装apk
	 */
	private void installAPK() {
		File apkFile = new File(file.getAbsolutePath());
		if (!apkFile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
//		for (Activity activity: Lib_StaticClass.activities
//			 ) {
//			activity.finish();
//
//		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {

			switch (message.what) {
			case DOWN_UPDATA:
				//正在下载，显示进度
				progressBar.setProgress(progress);

				break;
			case DOWN_OVER:
				//下载完成
//				downloadDialog.dismiss();
				UtilBox.dismissDialog();
				installAPK();//安装apk
				break;
			}
		}
	};
}
