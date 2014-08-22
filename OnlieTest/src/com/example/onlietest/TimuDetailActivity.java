package com.example.onlietest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.Example;
import com.example.speech.ApkInstaller;
import com.example.speech.TtsSettings;
import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;

public class TimuDetailActivity extends Activity implements OnClickListener {

	private TextView subjectNameTV, knowledgeTV, sourcenameTV, questionTV,
			youAnswerShowTV, answerShowTV, resolveShowTV;
	private EditText editAnswer;
	private Button ButtonAnalysis, ButtonBack, ButtonAnswer, tts_play,tts_pause;
	private Example exampledetail;
	

	private static String TAG = "TtsDemo";
	// 语音合成对象
	private SpeechSynthesizer mTts;
	private Toast mToast;
	public static String SPEAKER = "speaker";

	private SharedPreferences mSharedPreferences;
	
	private Handler mHandler;
	private Dialog mLoadDialog;

	private List<Example> exampleData = new ArrayList<Example>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timudetail);

		initview();
		initData();

		// 初始化合成对�?
		mTts = new SpeechSynthesizer(this, mTtsInitListener);
		mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
	}
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        mTts.stopSpeaking(mTtsListener);
	        // 退出时释放连接
	        mTts.destory();
	    }
	private void initview() {

		subjectNameTV = (TextView) findViewById(R.id.subjectNameTV);
		knowledgeTV = (TextView) findViewById(R.id.knowledgeTV);
		sourcenameTV = (TextView) findViewById(R.id.sourcenameTV);
		questionTV = (TextView) findViewById(R.id.questionTV);

		youAnswerShowTV = (TextView) findViewById(R.id.youAnswerShowTV);
		youAnswerShowTV.setVisibility(View.INVISIBLE);
		answerShowTV = (TextView) findViewById(R.id.answerShowTV);
		answerShowTV.setVisibility(View.INVISIBLE);
		resolveShowTV = (TextView) findViewById(R.id.resolveShowTV);
		resolveShowTV.setVisibility(View.INVISIBLE);

		editAnswer = (EditText) findViewById(R.id.editTextAnswer);

		ButtonAnalysis = (Button) findViewById(R.id.ButtonAnalysis);
		ButtonAnalysis.setOnClickListener(this);

		ButtonBack = (Button) findViewById(R.id.ButtonBack);
		ButtonBack.setOnClickListener(this);

		ButtonAnswer = (Button) findViewById(R.id.ButtonAnswer);
		ButtonAnswer.setOnClickListener(this);

		tts_play = (Button) findViewById(R.id.tts_play);
		tts_play.setOnClickListener(this);
		
		tts_pause = (Button) findViewById(R.id.tts_pause);
		tts_pause.setOnClickListener(this);
		
		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);

	}
 	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add(0, 1, 1, "语音设置");
        menu.add(0, 2, 2, "重置");
        return super.onCreateOptionsMenu(menu);
    }
 	
 	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == 1){
           /* Toast t = Toast.makeText(this, "你选的是苹果", Toast.LENGTH_SHORT);
            t.show();*/
        	String speaker = mTts.getParameter(SpeechSynthesizer.LOCAL_SPEAKERS);
			System.out.println("yi xia zai fa yin ren" + speaker);
			Intent intent = new Intent(TimuDetailActivity.this, TtsSettings.class);
			intent.putExtra(SPEAKER, speaker);
			startActivity(intent);
        }else if(item.getItemId() == 2){        	
        	mTts.resumeSpeaking(mTtsListener);
        }
        return true;
    }
	private void initData() {

		mHandler=new Myhandler();
		
		exampledetail = (Example) getIntent().getSerializableExtra("example");
		subjectNameTV.setText(exampledetail.getSubjectName());
		knowledgeTV.setText(exampledetail.getKnowledge());
		sourcenameTV.setText(exampledetail.getSourcename());
		questionTV.setText(exampledetail.getQuestion());
		subjectNameTV.setText(exampledetail.getSubjectName());
		subjectNameTV.setText(exampledetail.getSubjectName());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.ButtonAnalysis:
			resolveShowTV.setVisibility(View.VISIBLE);
			resolveShowTV.setText("答案解析\n" + exampledetail.getResolve());
			break;
		case R.id.ButtonAnswer:
			youAnswerShowTV.setVisibility(View.VISIBLE);
			answerShowTV.setVisibility(View.VISIBLE);
			String answer = editAnswer.getText().toString().trim();
			youAnswerShowTV.setText("你的答案\n" + answer);

			answerShowTV.setText("参考答案\n" + exampledetail.getAnswer());
			break;
		case R.id.ButtonBack:

			finish();

			break;
		case R.id.tts_play:
			/*
			 * Intent intent = new
			 * Intent(TimuDetailActivity.this,com.example.speech.TtsDemo.class);
			 * startActivity(intent);
			 */
			setParam();
			initSpeech();
			tts_pause.setVisibility(View.VISIBLE);
			tts_play.setVisibility(View.GONE);
			
			// 设置参数
			int code = mTts.startSpeaking(exampledetail.getQuestion(),
					mTtsListener);
			if (code != 0) {
				showTip("start speak error : " + code);
			} else
				showTip("start speak success.");
			break;
		case R.id.tts_pause:
			tts_pause.setVisibility(View.GONE);
			tts_play.setVisibility(View.VISIBLE);
			mTts.pauseSpeaking(mTtsListener);
			break;
		default:
			break;
		}

	}

	/**
	 * 初期化监听�?
	 */
	private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code == ErrorCode.SUCCESS) {
				((Button) findViewById(R.id.tts_play)).setEnabled(true);
			}
		}
	};

	/**
	 * 合成回调监听�?
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
		@Override
		public void onBufferProgress(int progress) throws RemoteException {
			Log.d(TAG, "onBufferProgress :" + progress);
			// showTip("onBufferProgress :" + progress);
		}

		@Override
		public void onCompleted(int code) throws RemoteException {
			Log.d(TAG, "onCompleted code =" + code);
			showTip("onCompleted code =" + code);
		}

		@Override
		public void onSpeakBegin() throws RemoteException {
			Log.d(TAG, "onSpeakBegin");
			showTip("onSpeakBegin");
		}

		@Override
		public void onSpeakPaused() throws RemoteException {
			Log.d(TAG, "onSpeakPaused.");
			showTip("onSpeakPaused.");
		}

		@Override
		public void onSpeakProgress(int progress) throws RemoteException {
			Log.d(TAG, "onSpeakProgress :" + progress);
			showTip("onSpeakProgress :" + progress);
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
			Log.d(TAG, "onSpeakResumed.");
			showTip("onSpeakResumed");
		}
	};

	private void showTip(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {
		mTts.setParameter(SpeechConstant.ENGINE_TYPE,
				mSharedPreferences.getString("engine_preference", "local"));

		if (mSharedPreferences.getString("engine_preference", "local")
				.equalsIgnoreCase("local")) {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, mSharedPreferences
					.getString("role_cn_preference", "xiaoyan"));
		} else {
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME, mSharedPreferences
					.getString("role_cn_preference", "xiaoyan"));
		}
		mTts.setParameter(SpeechSynthesizer.SPEED,
				mSharedPreferences.getString("speed_preference", "50"));

		mTts.setParameter(SpeechSynthesizer.PITCH,
				mSharedPreferences.getString("pitch_preference", "50"));

		mTts.setParameter(SpeechSynthesizer.VOLUME,
				mSharedPreferences.getString("volume_preference", "50"));
	}
	
	/**
	 * 如果服务组件没有安装，有两种安装方式。
	 * 1.直接打开语音服务组件下载页面，进行下载后安装。
	 * 2.把服务组件apk安装包放在assets中，为了避免被编译压缩，修改后缀名为mp3，然后copy到SDcard中进行安装。
	 */
	private boolean processInstall(Context context ,String url,String assetsApk){
		// 直接下载方式
//		ApkInstaller.openDownloadWeb(context, url);
		// 本地安装方式
		if(!ApkInstaller.installFromAssets(context, assetsApk)){
		    Toast.makeText(TimuDetailActivity.this, "安装失败", Toast.LENGTH_SHORT).show();
		    return false;
		}
		return true;		
	}

	class Myhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String url = SpeechUtility.getUtility(TimuDetailActivity.this)
						.getComponentUrl();
				String assetsApk = "SpeechService.apk";
				if (processInstall(TimuDetailActivity.this, url, assetsApk)) {
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
				break;
			case 1:
				if (mLoadDialog != null) {
					mLoadDialog.dismiss();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}
	 
	 // 判断手机中是否安装了讯飞语音+
	 private boolean checkSpeechServiceInstall(){
		 String packageName = "com.iflytek.speechcloud";
		 List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		 for(int i = 0; i < packages.size(); i++){
			 PackageInfo packageInfo = packages.get(i);
			 if(packageInfo.packageName.equals(packageName)){
				 return true;
			 }else{
				 continue;
			 }
		 }
		 return false;
	 }
	 
	 private void initSpeech(){
		 
			if(!checkSpeechServiceInstall()){
				final Dialog dialog=new Dialog(this,R.style.dialog);
				
				LayoutInflater inflater = getLayoutInflater();
				View alertDialogView = inflater.inflate(R.layout.superman_alertdialog, null);
				dialog.setContentView(alertDialogView);
				Button okButton = (Button) alertDialogView.findViewById(R.id.ok);
				Button cancelButton = (Button) alertDialogView.findViewById(R.id.cancel);
				TextView comeText=(TextView) alertDialogView.findViewById(R.id.title);
//				SpannableString spanString=new SpannableString(comeText.getText());
//				spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, spanString.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
				comeText.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
//				TextView contentText=(TextView) alertDialogView.findViewById(R.id.content);
				okButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						mLoadDialog = new AlertDialog.Builder(TimuDetailActivity.this).create();
						mLoadDialog.show();
				        // 注意此处要放在show之后 否则会报异常
						mLoadDialog.setContentView(R.layout.loading_process_dialog_anim);
				        Message message=new Message();
				        message.what=0;
				        mHandler.sendMessage(message);
					}
				});
				cancelButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						tts_pause.setVisibility(View.GONE);
						tts_play.setVisibility(View.GONE);
					}
				});
				dialog.show();			
				WindowManager windowManager = getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
				lp.width = (int)(display.getWidth()); //设置宽度
				dialog.getWindow().setAttributes(lp);
				
				return;
			}
			// 设置你申请的应用appid
			SpeechUtility.getUtility(TimuDetailActivity.this).setAppid("4d6774d0");
	 }

}
