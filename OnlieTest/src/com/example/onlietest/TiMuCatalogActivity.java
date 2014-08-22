package com.example.onlietest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.Example;
import com.example.model.Timu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class TiMuCatalogActivity extends Activity implements OnScrollListener{
	
	private Timu timudetail;
	private int lastIndex=0;//���һ�����ݵĽǱ�
	private int pageLastIndex=0;//һҳ�����һ�����ݵ�����
	private Gson mGson;
	private ListView listView;
	private TextView Totaltext;
	private List<Example> exampleData =new ArrayList<Example>();
	private MyTriansLineAdapter myAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timucataloglist);		
		mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		
		initData();
		
		initview();
		viewAction();
	}
	
	private void initview(){
		
		Totaltext = (TextView)findViewById(R.id.textView);
		
		listView = (ListView) findViewById(R.id.listView);
		
		myAdapter = new MyTriansLineAdapter(TiMuCatalogActivity.this);
        listView.setAdapter(myAdapter);
        listView.setCacheColorHint(Color.TRANSPARENT);
		handler.sendEmptyMessage(1);
		
		/*textView.setText(Integer.toString(getArguments().getInt(
				ARG_SECTION_NUMBER)));*/
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(exampleData!=null){
				Intent intent = new Intent(TiMuCatalogActivity.this,TimuDetailActivity.class);
				intent.putExtra("example", exampleData.get(position));
				startActivity(intent);
				}
			}
		});

	}
	public class MyTriansLineAdapter extends BaseAdapter {
		LayoutInflater inflater;
		
		public MyTriansLineAdapter(Context c) {
			this.inflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return exampleData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_example, null);
				holder = new ViewHolder();
				holder.ItemID =  (TextView) convertView
						.findViewById(R.id.ItemID);				
				holder.subjectNameTV = (TextView)convertView
						.findViewById(R.id.subjectNameTV);
				holder.knowledgeTV = (TextView)convertView
						.findViewById(R.id.knowledgeTV);
				holder.sourcenameTV = (TextView)convertView
						.findViewById(R.id.sourcenameTV);
				holder.questionTypesTV = (TextView)convertView
						.findViewById(R.id.questionTypesTV);
				holder.questionDifficultyTV =  (TextView) convertView
						.findViewById(R.id.questionDifficultyTV);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.ItemID.setText(String.valueOf(position+1)+ ".");
			holder.subjectNameTV.setText(exampleData.get(position).getSubjectName());
			holder.knowledgeTV.setText(exampleData.get(position).getKnowledge());
			holder.sourcenameTV.setText(exampleData.get(position).getSourcename());
			holder.questionTypesTV.setText(exampleData.get(position).getQuestionTypes());
			holder.questionDifficultyTV.setText(exampleData.get(position).getQuestionDifficulty());
					
			return convertView;
		}

		class ViewHolder {
			TextView ItemID,subjectNameTV,knowledgeTV,sourcenameTV,questionTypesTV,questionDifficultyTV;
		}

	}
	
	private void initData(){
		
	timudetail = 	(Timu) getIntent().getSerializableExtra("timu");
	
	getDetailInfo(0,30);
	
	
		
	}
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if(exampleData!=null){
	            Totaltext.setText("�ܹ�����" + exampleData.size() +"����Ŀ¼");
	            }				
				break;
			case 1:
				myAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			
				
		}	
	};
	public String getkeySharePreferences(){
		SharedPreferences preferences = getSharedPreferences("USERKEY",
				Context.MODE_PRIVATE);
		String key = preferences.getString("KEY", "");
		
		return key;
	}
	private void getDetailInfo(int begin,int offset) {
		/* catalog_id=100&pn=0&rn=20*/
		// ������־ʱ��Ľ��ȶԻ���
		final ProgressDialog progressDialog = ProgressDialog.show(this,
				"Loading", "...load...");
		progressDialog.setCancelable(true);

		RequestParams params = new RequestParams();
		params.put("key", getkeySharePreferences());
		params.put("dtype", "json");
		params.put("catalog_id", timudetail.getId());
		params.put("pn", begin+"");
		params.put("rn", offset+"");
		
		TwitterRestClient.post("query", params,
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
										"��ȡ��Ϣʧ�ܣ�������������", Toast.LENGTH_SHORT).show();
							
								return;
							}
							
							JSONArray timuInfoJsonArray = Info.getJSONArray("data");
							for (int i = 0; i < timuInfoJsonArray.length(); i++) {
								
								Example example = mGson.fromJson(timuInfoJsonArray.getString(i),Example.class);
								
								exampleData.add(example);
								

							}	
							handler.sendEmptyMessage(1);

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
								"��ȡ��Ϣʧ�ܣ�������������", Toast.LENGTH_SHORT).show();
					}
				});
	}


	private void viewAction() {
		// TODO Auto-generated method stub		
		listView.setOnScrollListener(this);//��Ҫ��ĳһ���¼���Ӧ�Ļ����ͱ�������������Ӧ���¼�
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
		int visibleItemCount, int totalItemCount) {

		lastIndex=exampleData.size()-1;//�����ݵ����һ�����ݵ���������Ϊ��ҳ������ʾ�����ݵ�������0��ʼ
		pageLastIndex=firstVisibleItem+visibleItemCount-1;//ҳ������ʾ�����һ�����ݵ�����
		//firstVisibleItem   ҳ������ʾ��һ�����ݵ�������
		//visibleItemCount   ҳ������ʾ�Ķ��������ݵĸ���
		//totalItemCount     �������ݵ�������
		Log.d("yujianbin", "onScroll����ִ�� firstVisibleItem����"+firstVisibleItem);
		Log.d("yujianbin", "onScroll����ִ�� visibleItemCount����"+visibleItemCount);
		Log.d("yujianbin", "onScroll����ִ�� totalItemCount����"+totalItemCount);
		}


		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

		if(OnScrollListener.SCROLL_STATE_FLING==scrollState){
		Log.d("yujianbin", "listview���ڹ���");


		//�������ֹͣ�ˣ����ҵ������һ������ʱ���ü�������
		}else if(OnScrollListener.SCROLL_STATE_IDLE==scrollState&&lastIndex==pageLastIndex){
		Log.d("yujianbin", "listviewֹͣ����");//һ�������״̬��������
		Log.d("yujianbin", "lastIndex:::"+lastIndex);
		Log.d("yujianbin", "pageLastIndex:::"+pageLastIndex);
		Toast.makeText(getApplicationContext(),
				"��ȡ�ţ����Ե�", Toast.LENGTH_SHORT).show();	
		             Runnable run=new Runnable() {
		@Override
		public void run() {
		// TODO Auto-generated method stub
		//1����������
		loadData();
		Log.d("yujianbin", "listview��������");//һ�������״̬��������
		// handler.postDelayed(this, 2000);//��run�����б�ʾÿ������ִ��һ��
		}
		};
		handler.postDelayed(run, 1000);
		
		}else if(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL==scrollState){
		Log.d("yujianbin", "������Ļ��ʼ����");

		}
		}

		//��ҳ��ʾ������
		private void loadData(){
			getDetailInfo(lastIndex,30);
		listView.setSelection(lastIndex - 1);
		}
	
}
