package com.example.onlietest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.example.model.Timu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	private Gson mGson;
	private String APPKEY = "46df06009441fc256373d06181c78192";
	private static String UserName = "";
	
	
	private static List<Timu> buslineData = new ArrayList<>();
	private static List<Map<String, List<Timu>>> list = new ArrayList<Map<String, List<Timu>>>();
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	public static String[] ItemString = new String[]{
            "语文",
            "数学",
            "英语",
            "物理",
            "化学",
            "生物",
            "历史",
            "政治",
            "地理",
            
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		if("".equals(getkeySharePreferences())){
			saveSharePreferences(APPKEY);
			showAppkeyDialog();
		}else{
			getDirectoryInfo();			
		}
		
		
	}
	private void init(){
		 JPushInterface.init(getApplicationContext());
	}
	@Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
	private long mExitTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {				
				finish();
			}

			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
//		if (keyCode == KeyEvent.KEYCODE_MENU) {
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}
	
	public String getkeySharePreferences(){
		SharedPreferences preferences = getSharedPreferences("USERKEY",
				Context.MODE_PRIVATE);
		String key = preferences.getString("KEY", "");
		
		return key;
	}
	public void saveSharePreferences(String key) {
		SharedPreferences share = getSharedPreferences("USERKEY", 0);

		share.edit().putString("KEY", key).commit();

		share = null;
	}
	private void showAppkeyDialog() {
		final Dialog dialog = new Dialog(this, R.style.CommonDialog);
		dialog.setContentView(R.layout.smssdk_set_appkey_dialog);
		final EditText etAppKey = (EditText) dialog.findViewById(R.id.et_appkey);
		final EditText etName = (EditText) dialog.findViewById(R.id.et_appsecret);
			etAppKey.setText(getkeySharePreferences());
		   
		//final EditText etAppSecret = (EditText) dialog.findViewById(R.id.et_appsecret);
		OnClickListener ocl = new OnClickListener() {
			public void onClick(View v) {
				if (v.getId() == R.id.btn_dialog_ok) {
					APPKEY = etAppKey.getText().toString().trim();	
					UserName = etName.getText().toString().trim();
					if (TextUtils.isEmpty(APPKEY)) {
						Toast.makeText(v.getContext(), R.string.smssdk_appkey_dialog_title,
								Toast.LENGTH_SHORT).show();
					} else {
						dialog.dismiss();
						saveSharePreferences(APPKEY);
						getDirectoryInfo();
					}
				} else {
					dialog.dismiss();
				}
			}
		};
		dialog.findViewById(R.id.btn_dialog_ok).setOnClickListener(ocl);
		dialog.findViewById(R.id.btn_dialog_cancel).setOnClickListener(ocl);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void getDirectoryInfo() {
		/* routeitem/routeid/1 */
		// 加载日志时候的进度对话框
		final ProgressDialog progressDialog = ProgressDialog.show(this,
				"Loading", "...load...");
		progressDialog.setCancelable(true);

		RequestParams params = new RequestParams();
		params.put("key", APPKEY);
		params.put("dtype", "json");
		TwitterRestClient.post("catalog", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0, arg1);

						if (progressDialog.isShowing()
								&& progressDialog != null) {
							progressDialog.dismiss();
						}
						JSONObject Info = null;
						try {
							Info = new JSONObject(arg1).getJSONObject("result");
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						try {
							if(Info == null){
								if (progressDialog.isShowing()
										&& progressDialog != null) {
									progressDialog.dismiss();
								}
								Toast.makeText(getApplicationContext(),
										"获取信息失败，请检查您的网络", Toast.LENGTH_SHORT).show();
							
								return;
							}
							for (int j = 0; j < ItemString.length; j++){
							Map<String, List<Timu>> map = new  HashMap<String, List<Timu>>();
							JSONArray timuInfoJsonArray = Info.getJSONArray(ItemString[j]);
							List<Timu> yuwenData = new ArrayList<>();
							for (int i = 0; i < timuInfoJsonArray.length(); i++) {
								
								Timu timu = mGson.fromJson(timuInfoJsonArray.getString(i),Timu.class);
								
								yuwenData.add(timu);
								

							}
							map.put(ItemString[j], yuwenData);
							list.add(map);
							
							}					

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);

						if (progressDialog.isShowing()
								&& progressDialog != null) {
							progressDialog.dismiss();
						}
						Toast.makeText(getApplicationContext(),
								"获取信息失败，请检查您的网络", Toast.LENGTH_SHORT).show();
					}
				});
	}
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {/*
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}*/
		mTitle = ItemString[number-1];
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			saveSharePreferences("");
			showAppkeyDialog();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private ListView buslinelistView;
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
			
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			textView.setText(UserName + " 欢迎你使用我们的软件!");
			
			buslinelistView = (ListView) rootView.findViewById(R.id.listView1);
			/*textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));*/
			buslinelistView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					if(buslineData!=null){
					Intent intent = new Intent(getActivity(),TiMuCatalogActivity.class);
					intent.putExtra("timu", buslineData.get(position));
					startActivity(intent);
					}
				}
			});
			
			if(!list.isEmpty())
			handler.sendEmptyMessage(0);
			
			return rootView;
		}
		
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				int index= 1;
				index= getArguments().getInt(ARG_SECTION_NUMBER);
				buslineData = list.get(index-1).get(ItemString[index-1]);
				
				if(buslineData!=null){
	            buslinelistView.setAdapter(new MyTriansLineAdapter(getActivity()));
	            }
				buslinelistView.setCacheColorHint(Color.TRANSPARENT);
					
			}	
		};

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
		
		public class MyTriansLineAdapter extends BaseAdapter {
			LayoutInflater inflater;
			
			public MyTriansLineAdapter(Context c) {
				this.inflater = LayoutInflater.from(c);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return buslineData.size();
			}

			@Override
			public Timu getItem(int arg0) {
				// TODO Auto-generated method stub
				return buslineData.get(arg0);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			 /*
			private String CheCiMingCheng;
			private String CheXingMingCheng;
			private String ShiFaZhan;
			private String ZhongDianZhan;
			private String ShiFaShi;
			private String ZhongDaoShi;
			private String ChaXunZhan;
			private String DaoShi;
			private String FaShi;
			
			*/
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.bus_xianlu_item, null);
					holder = new ViewHolder();
					holder.ItemID =  (TextView) convertView
							.findViewById(R.id.ItemID);
					holder.XianLuMingCheng = (TextView) convertView
							.findViewById(R.id.XianLuMingCheng);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.ItemID.setText(String.valueOf(position+1)+ ".");
				holder.XianLuMingCheng.setText(buslineData.get(position).getCatalog());
				
						
				return convertView;
			}

			class ViewHolder {
				TextView ItemID,XianLuMingCheng;
			}

		}
	}

}
