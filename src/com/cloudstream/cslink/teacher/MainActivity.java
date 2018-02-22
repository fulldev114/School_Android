package com.cloudstream.cslink.teacher;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.cloudstream.cslink.R;
import com.common.Bean.ExpandableListDataPump;
import com.common.SharedPreferFile;
import com.common.utils.ConstantApi;
import com.common.view.CircularImageView;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.service.teacher.NotificationJobService;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements AsyncTaskCompleteListener<String> {

	LinearLayout menuBar;
	private DrawerLayout mDrawerLayout;
	ExpandableListView list;
	String[] Drawerlist_item;
	int fragment;
	Integer[] Drawerlist_image = {R.drawable.img_home, R.drawable.img_profile, R.drawable.img_report, R.drawable.adres_img_admin,
			R.drawable.img_setting,
			R.drawable.img_contact
	}; //R.drawable.img_logout////
	Integer[] Drawerlist_badge = {0, 0, 0, 0, 0, 0};

    /*R.drawable.statistics_ic,
    R.drawable.img_parent,
      R.drawable.message_fb,
            R.drawable.group_message,
            R.drawable.message_fb,
            R.drawable.report_card,*/

	public static TextView title;
	private ActionBarDrawerToggle mDrawerToggle;

	HashMap<String, List<String>> expandableListDetail = null;
	List<String> expandableListTitle = null;
	HashMap<String, List<Integer>> expandableListDetail_img = null;

	boolean b = false;

	String _teacher_id, _teacher_name, _school_id, language, _image;

	String noti_type = "";
	int noti_kid_id = 0, noti_from_id = 0;

	CircularImageView imageView;
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	LinearLayout btnUpdateProfile;
	private FramentProfile fragmentprofile = null;
	private LinearLayout lin_menu;
	private AdapterHomeScreen adap;
	private FragmentAttendance fragmentAttend;
	protected LinearLayout lin_information, lin_emergency;
	private FragmentStatistics fragmentStatic;
	private static TeacherHomeActivity fragmenthome;
	private int pos;
	private int index;
	private int index_child;
	int tem_index[] = new int[2];
	private Childbeans data;
	private TextView textName;
	private UpdaterBroadcastReceiver updateBroadcaseReceiver = null;
	public static int kJobId = 1;
	private JobScheduler jobScheduler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adres_homeactivity);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.setThreadPolicy(policy);
		}
		Intent intent = getIntent();

		ApplicationData.setMainActivity(this);

		if (intent.hasExtra("noti_type")) {        // push noti getted.
			noti_type = intent.getStringExtra("noti_type");
		}
		if (intent.hasExtra("noti_kid_id")) {
			noti_kid_id = intent.getIntExtra("noti_kid_id", 0);
		}
		if (intent.hasExtra("noti_from_id")) {
			noti_from_id = intent.getIntExtra("noti_from_id", 0);
		}
		fragment = intent.getIntExtra("fragment", 0);
		if (noti_type.equals("ch"))
			fragment = 2;

		SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);

		language = sharedpref.getString("language", "");
		_school_id = sharedpref.getString("school_id", "");
		_image = sharedpref.getString("image", "");
		_teacher_id = sharedpref.getString("teacher_id", "");
		_teacher_name = sharedpref.getString("teacher_name", "");

		b = true;

		list = (ExpandableListView) findViewById(R.id.list_slidermenu);
		lin_menu = (LinearLayout) findViewById(R.id.lin_menu);

		String localeString;
		if (language.equalsIgnoreCase("english")) {
			localeString = "en";
		} else {
			localeString = "no";
		}

		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			setBackgroundService();
		}


		Configuration config = getResources().getConfiguration();
		Locale locale = new Locale(localeString);
		config.locale = locale;
		getResources().updateConfiguration(config, getResources().getDisplayMetrics());
		//connectivity of user with xmpp
		SharedPreferFile user = new SharedPreferFile(MainActivity.this);

      /*  LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.header, list, false);*/
		textName = (TextView) findViewById(R.id.textName);
		textName.setText(_teacher_name);
		imageView = (CircularImageView) findViewById(R.id.imgProfile);

		ApplicationData.setProfileImg(imageView, ApplicationData.web_server_url + "uploads/" + _image, MainActivity.this);

		//  list.addHeaderView(header, null, false);

		//remove notification from notification bar
		NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancelAll();

		setActionBarStyle();
		menuBar = (LinearLayout) findViewById(R.id.btnbck);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		btnUpdateProfile = (LinearLayout) findViewById(R.id.btnUpdateProfile);
		lin_information = (LinearLayout) findViewById(R.id.lin_information);
		lin_emergency = (LinearLayout) findViewById(R.id.lin_emergency);

		int width1 = (int) ((float) (getResources().getDisplayMetrics().widthPixels / 1.2));
		DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) lin_menu.getLayoutParams();
		params.width = width1;
		lin_menu.setLayoutParams(params);

		//get value of side menu from string file and images variable to set adapter
		expandableListDetail = ExpandableListDataPump.getData(MainActivity.this);
		expandableListTitle = ExpandableListDataPump.getDatalist(MainActivity.this);
		expandableListDetail_img = ExpandableListDataPump.getDataImage(MainActivity.this);

		// .................get string array from sring.xml..........//
		Drawerlist_item = getResources().getStringArray(R.array.drawer_item);

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
		mDrawerLayout.requestDisallowInterceptTouchEvent(true);

		// loaddrawer();
		menuBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
					mDrawerLayout.closeDrawers();
					btnUpdateProfile.setEnabled(true);
				} else if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

					View view = getCurrentFocus();
					if (view != null) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
					}
					SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
					language = sharedpref.getString("language", "");
					//loaddrawer();
					drawMenu();
					btnUpdateProfile.setEnabled(false);
					mDrawerLayout.openDrawer(lin_menu);
				}
			}
		});

		btnUpdateProfile.setVisibility(View.GONE);
		lin_information.setVisibility(View.GONE);
		lin_emergency.setVisibility(View.GONE);

		btnUpdateProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fragmentprofile != null)
					fragmentprofile.editprofile();
			}
		});

		lin_information.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fragmentAttend != null)
					fragmentAttend.showHelpDlg();
				else if (fragmentStatic != null) {
				}
			}
		});


		list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				switch (groupPosition) {
					case 0:
						pos = 0;
						break;
					case 1:
						pos = 1;
						break;
					case 2:
						pos = 2;
						break;
					case 3:
						pos = 6;
						break;
					case 4:
						pos = 9;
						break;
					case 5:
						pos = 10;
						break;
					case 6:
						pos = 11;
						break;
				}
				if (pos != 2 && pos != 6) {

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							callAction(pos); // your fragment transactions go here
						}
					}, 200);
					mDrawerLayout.closeDrawers();
					list.collapseGroup(2);
					list.collapseGroup(3);
					// list.setSelected(true);
					//list.setSelectedGroup(groupPosition);

				}

				index = list.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition));
				//   index = groupPosition;
				list.setItemChecked(index, true);
				list.setSelected(true);
			}
		});

		list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				list.setItemChecked(index_child, false);
			}
		});

		list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				if (groupPosition == 2) {
					if (childPosition == 0)
						pos = 3;
					else if (childPosition == 1)
						pos = 4;
					else
						pos = 5;

				} else if (groupPosition == 3) {
					if (childPosition == 0)
						pos = 7;
					else if (childPosition == 1)
						pos = 8;
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						callAction(pos); // your fragment transactions go here
					}
				}, 200);
				mDrawerLayout.closeDrawers();

				index_child = list.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
				list.setItemChecked(index, true);

				return true;
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		callAction(0);

      /*  if (fragment > 1 && fragment < 5) {
            callAction(fragment);
        } else if (savedInstanceState == null) {

            Bundle bundle = new Bundle();
            Fragment fragment = new FragmentChat();
            bundle.putString("noti_kid_id", "");
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
//
//			android.app.FragmentManager fragmentManager = getFragmentManager();
//
//			fragmentManager.beginTransaction()
//					.replace(R.id.frame_container, fragment).commit();
        }*/


		IntentFilter filter = new IntentFilter();
		filter.addAction(ApplicationData.BROADCAST_CHAT);
		filter.addAction(ApplicationData.BROADCAST_CHAT_INTERNAL);
		updateBroadcaseReceiver = new UpdaterBroadcastReceiver();
		registerReceiver(updateBroadcaseReceiver, filter);

	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setBackgroundService() {
		if(kJobId>0 && jobScheduler!=null)
			jobScheduler.cancel(kJobId);

		ComponentName mServiceComponent = new ComponentName(this, NotificationJobService.class);
		JobInfo.Builder builder = new JobInfo.Builder(kJobId, mServiceComponent);
		builder.setMinimumLatency(3 * 1000); // wait at least
		builder.setOverrideDeadline(30 * 1000); // maximum delay
		builder.setPersisted(true);
		builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
		builder.setRequiresDeviceIdle(true); // device should be idle
		builder.setRequiresCharging(false); // we don't care if the device is charging or not
		jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
		jobScheduler.schedule(builder.build());
	}

	public void setActionBarTitle(String tit) {
		title.setText(tit);
	}

	public CircularImageView getImageProfileView() {
		return imageView;
	}

	private void drawMenu() {
		// TODO Auto-generated method stub
		SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
		//btnUpdateProfile.setEnabled(false);
		language = sharedpref.getString("language", "");
		_teacher_name = sharedpref.getString("teacher_name", "");
		textName.setText(_teacher_name);

		_image = sharedpref.getString("image", "");
		ApplicationData.setProfileImg(imageView, ApplicationData.web_server_url + "uploads/" + _image, MainActivity.this);
		String localeString;
		if (language.equalsIgnoreCase("english")) {
			localeString = "en";
		} else {
			localeString = "no";
		}

		Configuration config = getResources().getConfiguration();
		Locale locale = new Locale(localeString);
		config.locale = locale;
		getResources().updateConfiguration(config, getResources().getDisplayMetrics());

		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		//btnUpdateProfile.setEnabled(false);
		expandableListDetail_img = ExpandableListDataPump.getDataImage(MainActivity.this);
		expandableListDetail = ExpandableListDataPump.getData(MainActivity.this);
		expandableListTitle = ExpandableListDataPump.getDatalist(MainActivity.this);

		adap = new AdapterHomeScreen(getApplicationContext(),
				expandableListTitle, expandableListDetail, Drawerlist_image, Drawerlist_badge, expandableListDetail_img);

		list.setAdapter(adap);
		adap.notifyDataSetChanged();

       /* if (list.isGroupExpanded(2)&& list.isGroupExpanded(3))
            list.setItemChecked(pos, true);
        else */
		{
			list.setItemChecked(index, true);
			if (pos == 2 || pos == 3 || pos == 4 || pos == 5) {
				list.expandGroup(2);
				//  list.setItemChecked(index, true);
			} else if (pos == 6 || pos == 7 || pos == 8) {
				list.expandGroup(3);
				//   list.setItemChecked(index, true);
			}


		}
		//set selected item when open drawer menu


		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_launcher, R.string.app_name, R.string.app_name) {
			/**
			 * Called when drawer is closed
			 */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("TAG PHOTO");
				invalidateOptionsMenu();
				btnUpdateProfile.setEnabled(true);
			}

			/**
			 * Called when a drawer is opened
			 */
			public void onDrawerOpened(View drawerView) {

				getActionBar().setTitle("TAG PHOTO");
				invalidateOptionsMenu();
			}
		};


	}
//    private void loaddrawer() {
//        if (!GlobalConstrants.isWifiConnected(MainActivity.this)) {
//            return;
//        }
//
//        String url = ApplicationData.getlanguageAndApi(MainActivity.this, ConstantApi.GET_UNREADMSG) +
//                "id=" + _teacher_id;
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
//                Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String flag = response.getString("flag");
//                            if (Integer.parseInt(flag) == 1) {
//                                Drawerlist_badge[1] = Integer.valueOf(response.getString("badge"));
//                            } else {
//                                Drawerlist_badge[1] = 0;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Drawerlist_badge[2] = 0;
//                        }
//                        drawMenu();
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                ApplicationData.showToast(MainActivity.this, getResources().getString(R.string.str_network_error), false);
//                Drawerlist_badge[1] = 0;
//                drawMenu();
//            }
//        });
//
//        queue.add(jsObjRequest);
//    }

	public void setActionBarStyle() {

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		View cView = getLayoutInflater().inflate(R.layout.adres_title, null);

		actionBar.setCustomView(cView);
		actionBar.setDisplayShowHomeEnabled(false);

		title = (TextView) cView.findViewById(R.id.textView1);
		title.setText(this.getResources().getString(R.string.str_message));
	}

	public void callAction(int pos) {
		btnUpdateProfile.setEnabled(true);
		Fragment fragment = null;
		fragmentprofile = null;
		fragmentAttend = null;
		fragmentStatic = null;
		fragmenthome = null;
		Bundle bundle = new Bundle();
		ApplicationData.ishomebuttonview = false;
		if (pos == 0 || pos == 1 || pos == 9 || pos == 10) {  // set title
			int index = pos;
			if (pos == 9) index = 4;
			else if (pos == 10) index = 5;
			title.setText(getResources().getStringArray(R.array.drawer_item)[index]);
		}
		title.setTextColor(getResources().getColor(R.color.white_light));
		btnUpdateProfile.setVisibility(View.GONE);
		lin_information.setVisibility(View.GONE);
		lin_emergency.setVisibility(View.GONE);
		switch (pos) {
			case 0:
				fragmenthome = new TeacherHomeActivity();
				lin_emergency.setVisibility(View.VISIBLE);
				list.setItemChecked(0, true);
				break;
			case 1:
               /* bundle.putString("teacher_id", _teacher_id);
                bundle.putString("teacher_name", _teacher_name);*/

				btnUpdateProfile.setVisibility(View.VISIBLE);
				break;
			case 2:
				break;
			case 3:
				bundle.putInt("flag", 0);//0
				fragment = new FragmentReport();
				fragment.setArguments(bundle);
				lin_information.setVisibility(View.GONE);
				break;
			case 4:
				bundle.putInt("flag", 1);
				fragment = new FragmentReport();
				fragment.setArguments(bundle);
				lin_information.setVisibility(View.VISIBLE);
				break;
			case 5:
				bundle.putInt("flag", 2);
				fragment = new FragmentReport();
				fragment.setArguments(bundle);
				lin_information.setVisibility(View.GONE);
				break;
			case 6:

				break;
			case 7:
				bundle.putInt("flag", 1);
				fragmentStatic = new FragmentStatistics();
				fragmentStatic.setArguments(bundle);
				lin_information.setVisibility(View.GONE);
				break;
			case 8:
				bundle.putInt("flag", 2);
				fragmentStatic = new FragmentStatistics();
				fragmentStatic.setArguments(bundle);
				lin_information.setVisibility(View.GONE);
               /* fragmentAttend = new FragmentAttendance();
                fragmentAttend.setArguments(bundle);
                lin_information.setVisibility(View.VISIBLE);*/
				break;
			case 9:
				fragment = new FragmentSetting();
				break;
			case 10:
				fragment = new FragmentContactUs();
				break;
			case 11:
				break;

		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			mDrawerLayout.closeDrawer(lin_menu);
		} else if (pos == 1) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("teacher_id", _teacher_id);
			btnUpdateProfile.setEnabled(true);
			ETechAsyncTask task = new ETechAsyncTask(MainActivity.this, this, ConstantApi.GET_PROFILE, map);
			task.execute(ApplicationData.main_url + ConstantApi.GET_PROFILE + ".php?");

		}
       /* else if(fragmentAttend!=null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragmentAttend).commit();

            mDrawerLayout.closeDrawer(lin_menu);

        }*/
		else if (fragmentStatic != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragmentStatic).commit();
			//   list.setItemChecked(pos, true);
			mDrawerLayout.closeDrawer(lin_menu);
		} else if (fragmenthome != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragmenthome).commit();
			//   list.setItemChecked(pos, true);
			mDrawerLayout.closeDrawer(lin_menu);
		}
        /*else {
            if (pos == 11)
                ApplicationData.doLogout(this);

        }*/
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//      ApplicationData.setMainActivity(null);
		//      Background_work.set_background_time();
		ApplicationData.ispincode = false;
		//      b = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		//ApplicationData.setMainActivity(null);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		ApplicationData.ispincode = true;

		SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);

		language = sharedpref.getString("language", "");
		String localeString;
		if (language.equalsIgnoreCase("english")) {
			localeString = "en";
		} else {
			localeString = "no";
		}

		Configuration config = getResources().getConfiguration();
		Locale locale = new Locale(localeString);
		config.locale = locale;
		locale.setDefault(locale);
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

		if (fragmenthome != null) {
			fragmenthome.setbadge();
		}
		list.setAdapter(adap);

		ApplicationData.setMainActivity(MainActivity.this);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApplicationData.setMainActivity(null);
		unregisterReceiver(updateBroadcaseReceiver);

		expandableListDetail = null;
		expandableListTitle = null;
		expandableListDetail_img = null;
		_teacher_id="";_teacher_name=""; _school_id=""; language=""; _image="";
		noti_type = "";
		imageView=null;
		btnUpdateProfile=null;
		fragmentprofile = null;
		lin_menu=null;
		adap=null;
		fragmentAttend=null;
		lin_information=null; lin_emergency=null;
		fragmentStatic=null;
		data=null;
		textName=null;
		updateBroadcaseReceiver = null;
		jobScheduler=null;
		System.gc();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {

		if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.EDGE_LEFT) {
			View view = getCurrentFocus();
			if (view != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		}


		return super.dispatchTouchEvent(e);
	}

	@Override
	public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
		try {

			if (statusCode == ETechAsyncTask.COMPLETED) {
				JSONObject jObject = new JSONObject(responseMsg);

				try {

					if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_PROFILE)) {
						String flag = jObject.getString("flag");


						if (Integer.parseInt(flag) == 1) {
							JSONArray details = jObject.getJSONArray("profileDetails");
							JSONObject c = details.getJSONObject(0);
							data = new Childbeans();
							data.teacher_id = c.getString("user_id");
							data.image = c.getString("image");
							data.name = c.getString("name");
							data.emailaddress = c.getString("emailaddress");
							data.mobile1 = c.getString("mobile");
							data.class_name = c.getString("Classincharge");
							data.subject_name = c.getString("classsubject");

						} else if (flag.equalsIgnoreCase("0")) {
							String msg = jObject.getString("msg");
							ApplicationData.showToast(MainActivity.this, msg, false);
						} else {
							ApplicationData.showToast(MainActivity.this, getResources().getString(R.string.msg_operation_error), false);
						}
					}
				} catch (Exception e) {
					Log.e("OfferCategory", "onTaskComplete() " + e, e);
				}

				Bundle bundle = new Bundle();
				bundle.putString("teacher_id", _teacher_id);
				bundle.putString("teacher_name", _teacher_name);
				bundle.putSerializable("data", data);
				fragmentprofile = new FramentProfile();
				fragmentprofile.setArguments(bundle);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragmentprofile).commit();
				mDrawerLayout.closeDrawer(lin_menu);

			} else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
				try {
					ApplicationData.showToast(MainActivity.this, R.string.msg_operation_error, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);

		language = sharedpref.getString("language", "");
		String localeString;
		if (language.equalsIgnoreCase("english")) {
			localeString = "en";
		} else {
			localeString = "no";
		}

		Configuration config = getResources().getConfiguration();
		Locale locale = new Locale(localeString);
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

		expandableListDetail_img = ExpandableListDataPump.getDataImage(MainActivity.this);
		expandableListDetail = ExpandableListDataPump.getData(MainActivity.this);
		expandableListTitle = ExpandableListDataPump.getDatalist(MainActivity.this);
		adap.updatedata(expandableListTitle, expandableListDetail, expandableListDetail_img);

	}


	public static void sendchatbadge() {
		if (fragmenthome != null) {
			fragmenthome.setbadge();

		}
	}

	public class UpdaterBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == null)
				return;
			if (action.equals(ApplicationData.BROADCAST_CHAT) || action.equalsIgnoreCase(ApplicationData.BROADCAST_CHAT_INTERNAL)) {
				Childbeans newmessg = (Childbeans) intent.getSerializableExtra("newMessage");
				String childid = intent.getStringExtra("childid");

				if (fragmenthome != null) {
					fragmenthome.setbadge();
				}
			}
		}
	}

}
