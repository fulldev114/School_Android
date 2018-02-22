package com.cloudstream.cslink.teacher;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;

import java.util.Locale;

public class FragmentSetting extends Fragment {

	LinearLayout _change_pincode, _english, nowrgian, aboutus;
	String _language, _notification;

	ImageView notification;

	TextView lang_english, _lang_aboutus, _changepin, lang_noti;

	public FragmentSetting() {
	}

	int language = 1;

	int sdk = android.os.Build.VERSION.SDK_INT;

	private TextView _tv_english, _tv_nowrgian;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.adres_fragment_setting, container,false);

		SharedPreferences sharedpref = getActivity().getSharedPreferences("adminapp", 0);
		_notification = sharedpref.getString("notification", "");
		findview(rootView);
		return rootView;
	}

	private void findview(View rootView) {

        _tv_english = (TextView) rootView.findViewById(R.id.textView_english);
        _tv_nowrgian = (TextView) rootView.findViewById(R.id.textView_nowrgian);
        _lang_aboutus = (TextView) rootView.findViewById(R.id.textView4);
        _changepin = (TextView) rootView.findViewById(R.id.textView5);
        lang_noti = (TextView) rootView.findViewById(R.id.textView3);
        _english = (LinearLayout) rootView.findViewById(R.id.english);
        nowrgian = (LinearLayout) rootView.findViewById(R.id.norwgian);
        aboutus = (LinearLayout) rootView.findViewById(R.id.aboutus);

        // ///..............All Linear Layouts.......//
        _change_pincode = (LinearLayout) rootView.findViewById(R.id.change_pincode);

		notification = (ImageView) rootView.findViewById(R.id.imageView3);
		setnotification();
		notification.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences sharedpref = getActivity().getSharedPreferences("adminapp", 0);
                _notification = sharedpref.getString("notification", "");

                // ........................sharedpreferences.......................//
                SharedPreferences myPrefs = getActivity().getSharedPreferences("adminapp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();

                if (_notification.equalsIgnoreCase("0")) {
                    notification.setBackgroundResource(R.drawable.offtoggle);// offnotification
                    editor.putString("notification", "1");
                } else if (_notification.equalsIgnoreCase("1")) {
                    notification.setBackgroundResource(R.drawable.ontoggle);// onnotification
                    editor.putString("notification", "0");
                }
                editor.commit();
            }
        });


		_change_pincode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(), ChangePincode.class);
				startActivity(in);
			}
		});


		_english.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ........................sharedpreferences.......................//
				SharedPreferences myPrefs = getActivity().getSharedPreferences("adminapp", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = myPrefs.edit();
				editor.putString("language", "english");
				editor.commit();
				change_lang();
			}
		});

		nowrgian.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
			// ........................sharedpreferences.......................//
				SharedPreferences myPrefs = getActivity().getSharedPreferences("adminapp", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = myPrefs.edit();

				editor.putString("language", "nowrgian");
				editor.commit();

				change_lang();
			}
		});


		aboutus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(), AboutUs.class);
				startActivity(in);
			}
		});

		change_lang();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void change_lang() {
		String localeString = "";

		SharedPreferences myPrefs = getActivity().getSharedPreferences("adminapp", Context.MODE_PRIVATE);

		if (myPrefs.getString("language", "nowrgian").equalsIgnoreCase("english")) {
			localeString = "en";
		} else {
			localeString = "no";
		}

		Configuration config = getResources().getConfiguration();

		Locale locale = new Locale(localeString);
		config.locale = locale;
		getResources().updateConfiguration(config, getResources().getDisplayMetrics());

		((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.str_settings));
		_tv_english.setText(getResources().getString(R.string.str_english));
		lang_noti.setText(getResources().getString(R.string.str_notificatoin));
		_lang_aboutus.setText(getResources().getString(R.string.str_aboutus));
		_tv_nowrgian.setText(getResources().getString(R.string.str_norwegian));
		_changepin.setText(getResources().getString(R.string.change_pincode));
		_tv_english.setTextColor(getResources().getColor(R.color.white_light));
		_tv_nowrgian.setTextColor(getResources().getColor(R.color.white_light));

		if (myPrefs.getString("language", "nowrgian").equalsIgnoreCase("english")) {
			_tv_english.setTextColor(getResources().getColor(R.color.white_light));
			_tv_nowrgian.setTextColor(getResources().getColor(R.color.white_light));
			if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				_english.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cmdn));
				nowrgian.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
			} else {
				_english.setBackground(getResources().getDrawable(R.drawable.btn_cmdn));
				nowrgian.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
			}
		} else {
			_tv_english.setTextColor(getResources().getColor(R.color.white_light));
			_tv_nowrgian.setTextColor(getResources().getColor(R.color.white_light));
			if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				_english.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
				nowrgian.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cmdn));
			} else {
				_english.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
				nowrgian.setBackground(getResources().getDrawable(R.drawable.btn_cmdn));
			}
		}
	}

	private void setnotification() {
		// TODO Auto-generated method stub
		if (_notification.equalsIgnoreCase("0")) {
			notification.setBackgroundResource(R.drawable.ontoggle);
		} else if (_notification.equalsIgnoreCase("1")) {
			notification.setBackgroundResource(R.drawable.offtoggle);
		}
	}

	public void setActionBarStyle() {
		// im=(ImageView)findViewById(R.id.imageView1);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		View cView = getActivity().getLayoutInflater().inflate(R.layout.title, null);
		actionBar.setCustomView(cView);
		actionBar.setDisplayShowHomeEnabled(false);
	}
	public void onLowMemory() {
		super.onLowMemory();
	}
}
