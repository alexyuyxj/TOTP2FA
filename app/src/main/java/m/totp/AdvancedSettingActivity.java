package m.totp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdvancedSettingActivity extends BaseActivity {
	private TextView tvTitle;
	private View vBack;
	private ScrollView scBody;
	private TextView tvProvider;
	private EditText etProvider;
	private TextView tvAccount;
	private EditText etAccount;
	private TextView tvSecretKey;
	private EditText etSecretKey;
	private TextView tvDigits;
	private EditText etDigits;
	private TextView tvRefreshInterval;
	private EditText etRefreshInterval;
	private TextView tvAlgorithm;
	private Spinner spAlgorithm;
	private TextView tvTimeBase;
	private EditText etTimeBase;
	private TextView tvAdd;
	
	protected void initUi() {
		RelativeLayout rl = new RelativeLayout(this);
		rl.setFitsSystemWindows(true);
		setContentView(rl);
		
		tvTitle = new TextView(this);
		tvTitle.setId(rl.getChildCount() + 1);
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		tvTitle.setText(R.string.advanced_settings);
		tvTitle.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		tvTitle.setPadding(dp(50), 0, dp(20), 0);
		tvTitle.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, dp(60));
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		rl.addView(tvTitle, lp);
		
		RelativeLayout rlV = new RelativeLayout(this);
		rlV.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				sendResultAndFinish(RESULT_CANCELED);
			}
		});
		rlV.setPadding(dp(20), 0, 0, 0);
		lp = new RelativeLayout.LayoutParams(dp(50), RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_TOP, tvTitle.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_BOTTOM, tvTitle.getId());
		rl.addView(rlV, lp);
		
		vBack = new View(this);
		vBack.setBackground(getDrawable(R.drawable.left, R.drawable.left_night));
		lp = new RelativeLayout.LayoutParams(dp(30), dp(30));
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlV.addView(vBack, lp);
		
		scBody = new ScrollView(this);
		scBody.setBackgroundColor(getColor(R.color.list_bg, R.color.list_bg_night));
		lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.BELOW, tvTitle.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addView(scBody, lp);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		scBody.addView(ll, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		tvProvider = new TextView(this);
		tvProvider.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvProvider.setTextColor(getColor(R.color.account, R.color.account_night));
		tvProvider.setPadding(dp(20), dp(10), 0, dp(10));
		tvProvider.setGravity(Gravity.CENTER_VERTICAL);
		tvProvider.setText(R.string.provider);
		ll.addView(tvProvider, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		etProvider = new EditText(this);
		etProvider.setMaxLines(1);
		etProvider.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		etProvider.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		etProvider.setText(getIntent().getStringExtra("provider"));
		etProvider.setPadding(dp(10), dp(5), dp(10), dp(5));
		etProvider.setGravity(Gravity.CENTER_VERTICAL);
		etProvider.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		LayoutParams lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.leftMargin = dp(20);
		lpLl.rightMargin = dp(20);
		lpLl.bottomMargin = dp(10);
		ll.addView(etProvider, lpLl);
		etProvider.requestFocus();
		
		tvAccount = new TextView(this);
		tvAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvAccount.setTextColor(getColor(R.color.account, R.color.account_night));
		tvAccount.setPadding(dp(20), dp(10), 0, dp(10));
		tvAccount.setGravity(Gravity.CENTER_VERTICAL);
		tvAccount.setText(R.string.account);
		ll.addView(tvAccount, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		etAccount = new EditText(this);
		etAccount.setMaxLines(1);
		etAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		etAccount.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		etAccount.setText(getIntent().getStringExtra("account"));
		etAccount.setPadding(dp(10), dp(5), dp(10), dp(5));
		etAccount.setGravity(Gravity.CENTER_VERTICAL);
		etAccount.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		ll.addView(etAccount, lpLl);
		
		tvSecretKey = new TextView(this);
		tvSecretKey.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvSecretKey.setTextColor(getColor(R.color.account, R.color.account_night));
		tvSecretKey.setPadding(dp(20), dp(10), 0, dp(10));
		tvSecretKey.setGravity(Gravity.CENTER_VERTICAL);
		tvSecretKey.setText(R.string.secret_key);
		ll.addView(tvSecretKey, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		etSecretKey = new EditText(this);
		etSecretKey.setMaxLines(1);
		etSecretKey.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		etSecretKey.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		etSecretKey.setText(getIntent().getStringExtra("secretKey"));
		etSecretKey.setPadding(dp(10), dp(5), dp(10), dp(5));
		etSecretKey.setGravity(Gravity.CENTER_VERTICAL);
		etSecretKey.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		ll.addView(etSecretKey, lpLl);
		
		tvDigits = new TextView(this);
		tvDigits.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvDigits.setTextColor(getColor(R.color.account, R.color.account_night));
		tvDigits.setPadding(dp(20), dp(10), 0, dp(10));
		tvDigits.setGravity(Gravity.CENTER_VERTICAL);
		tvDigits.setText(R.string.digits);
		ll.addView(tvDigits, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		etDigits = new EditText(this);
		etDigits.setMaxLines(1);
		etDigits.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		etDigits.setText("6");
		etDigits.setInputType(InputType.TYPE_CLASS_NUMBER);
		etDigits.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		etDigits.setPadding(dp(10), dp(5), dp(10), dp(5));
		etDigits.setGravity(Gravity.CENTER_VERTICAL);
		etDigits.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		ll.addView(etDigits, lpLl);
		
		tvRefreshInterval = new TextView(this);
		tvRefreshInterval.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvRefreshInterval.setTextColor(getColor(R.color.account, R.color.account_night));
		tvRefreshInterval.setPadding(dp(20), dp(10), 0, dp(10));
		tvRefreshInterval.setGravity(Gravity.CENTER_VERTICAL);
		tvRefreshInterval.setText(R.string.refresh_interval);
		ll.addView(tvRefreshInterval, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		etRefreshInterval = new EditText(this);
		etRefreshInterval.setMaxLines(1);
		etRefreshInterval.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		etRefreshInterval.setText("30");
		etRefreshInterval.setInputType(InputType.TYPE_CLASS_NUMBER);
		etRefreshInterval.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		etRefreshInterval.setPadding(dp(10), dp(5), dp(10), dp(5));
		etRefreshInterval.setGravity(Gravity.CENTER_VERTICAL);
		etRefreshInterval.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		ll.addView(etRefreshInterval, lpLl);
		
		tvAlgorithm = new TextView(this);
		tvAlgorithm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvAlgorithm.setTextColor(getColor(R.color.account, R.color.account_night));
		tvAlgorithm.setPadding(dp(20), dp(10), 0, dp(10));
		tvAlgorithm.setGravity(Gravity.CENTER_VERTICAL);
		tvAlgorithm.setText(R.string.algorithm);
		ll.addView(tvAlgorithm, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		spAlgorithm = new Spinner(this);
		spAlgorithm.setGravity(Gravity.CENTER_VERTICAL);
		spAlgorithm.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		final String[] algorithms = getResources().getStringArray(R.array.algorithms);
		spAlgorithm.setAdapter(new BaseAdapter() {
			public int getCount() {
				return algorithms.length;
			}
			
			public String getItem(int i) {
				return algorithms[i];
			}
			
			public long getItemId(int i) {
				return i;
			}
			
			public View getView(int i, View view, ViewGroup viewGroup) {
				if (view == null) {
					view = createListItemView();
				}
				refreshListItemView(view, getItem(i));
				return view;
			}
		});
		ll.addView(spAlgorithm, lpLl);
		
		tvTimeBase = new TextView(this);
		tvTimeBase.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvTimeBase.setTextColor(getColor(R.color.account, R.color.account_night));
		tvTimeBase.setPadding(dp(20), dp(10), 0, dp(10));
		tvTimeBase.setGravity(Gravity.CENTER_VERTICAL);
		tvTimeBase.setText(R.string.time_base);
		ll.addView(tvTimeBase, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		etTimeBase = new EditText(this);
		etTimeBase.setMaxLines(1);
		etTimeBase.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		etTimeBase.setText("0");
		etTimeBase.setInputType(InputType.TYPE_CLASS_NUMBER);
		etTimeBase.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		etTimeBase.setPadding(dp(10), dp(5), dp(10), dp(5));
		etTimeBase.setGravity(Gravity.CENTER_VERTICAL);
		etTimeBase.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		ll.addView(etTimeBase, lpLl);
		
		LinearLayout llBtn = new LinearLayout(this);
		llBtn.setPadding(dp(50), dp(60), dp(50), 0);
		lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.gravity = Gravity.CENTER_HORIZONTAL;
		ll.addView(llBtn, lpLl);
		
		tvAdd = new TextView(this);
		tvAdd.setPadding(0, dp(15), 0, dp(15));
		tvAdd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvAdd.setText(R.string.add);
		tvAdd.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		tvAdd.setGravity(Gravity.CENTER);
		tvAdd.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.weight = 1;
		lpLl.gravity = Gravity.CENTER_HORIZONTAL;
		llBtn.addView(tvAdd, lpLl);
		tvAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (etProvider.getText().length() <= 0 || etAccount.getText().length() <= 0 || etSecretKey.getText().length() <= 0) {
					Toast.makeText(view.getContext(), R.string.all_fields_should_not_be_empty, Toast.LENGTH_SHORT).show();
				} else {
					sendResultAndFinish(RESULT_OK);
				}
			}
		});
	}
	
	protected void refreshUi() {
		if (tvTitle != null) {
			tvTitle.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		}
		if (vBack != null) {
			vBack.setBackground(getDrawable(R.drawable.left, R.drawable.left_night));
		}
		if (scBody != null) {
			scBody.setBackgroundColor(getColor(R.color.list_bg, R.color.list_bg_night));
		}
		if (tvProvider != null) {
			tvProvider.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (etProvider != null) {
			etProvider.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
			etProvider.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvAccount != null) {
			tvAccount.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (etAccount != null) {
			etAccount.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
			etAccount.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvSecretKey != null) {
			tvSecretKey.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (etSecretKey != null) {
			etSecretKey.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
			etSecretKey.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvDigits != null) {
			tvDigits.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (etDigits != null) {
			etDigits.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
			etDigits.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvRefreshInterval != null) {
			tvRefreshInterval.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (etRefreshInterval != null) {
			etRefreshInterval.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
			etRefreshInterval.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvAlgorithm != null) {
			tvAlgorithm.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (spAlgorithm != null) {
			spAlgorithm.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvTimeBase != null) {
			tvTimeBase.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (etTimeBase != null) {
			etTimeBase.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
			etTimeBase.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvAdd != null) {
			tvAdd.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
			tvAdd.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		}
	}
	
	private View createListItemView() {
		TextView tv = new TextView(this);
		tv.setMaxLines(1);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tv.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		tv.setPadding(dp(10), dp(5), dp(10), dp(5));
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		return tv;
	}
	
	private void refreshListItemView(View view, String algorithm) {
		TextView tv = (TextView) view;
		tv.setText(algorithm);
		tv.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		tv.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
	}
	
	private void sendResultAndFinish(int result) {
		Intent i = new Intent();
		i.putExtra("provider", etProvider.getText().toString());
		i.putExtra("account", etAccount.getText().toString());
		i.putExtra("secretKey", etSecretKey.getText().toString());
		i.putExtra("digits", Integer.parseInt(etDigits.getText().toString()));
		i.putExtra("refreshInterval", Integer.parseInt(etRefreshInterval.getText().toString()) * 1000);
		i.putExtra("algorithm", spAlgorithm.getSelectedItem().toString());
		i.putExtra("timeBase", Long.parseLong(etTimeBase.getText().toString()) * 1000);
		setResult(result, i);
		finish();
	}
}
