package m.totp;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class AddAccountActivity extends BaseActivity {
	private static final int REQ_CODE_SCAN = 1;
	private static final int REQ_CODE_ADVANCED = 2;
	
	private TextView tvTitle;
	private View vBack;
	private View vAdvanced;
	private ScrollView scBody;
	private TextView tvProvider;
	private EditText etProvider;
	private TextView tvAccount;
	private EditText etAccount;
	private TextView tvSecretKey;
	private EditText etSecretKey;
	private TextView tvScan;
	private TextView tvAdd;
	
	protected void initUi() {
		RelativeLayout rl = new RelativeLayout(this);
		rl.setFitsSystemWindows(true);
		setContentView(rl);
		
		tvTitle = new TextView(this);
		tvTitle.setId(rl.getChildCount() + 1);
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		tvTitle.setText(R.string.add_account);
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
				sendResultAndFinish(RESULT_CANCELED, null);
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
		
		rlV = new RelativeLayout(this);
		rlV.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(view.getContext(), AdvancedSettingActivity.class);
				i.putExtra("provider", etProvider.getText().toString());
				i.putExtra("account", etAccount.getText().toString());
				i.putExtra("secretKey", etSecretKey.getText().toString());
				startActivityForResult(i, REQ_CODE_ADVANCED);
			}
		});
		rlV.setPadding(dp(2), dp(2), dp(22), dp(2));
		lp = new RelativeLayout.LayoutParams(dp(80), RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_TOP, tvTitle.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_BOTTOM, tvTitle.getId());
		rl.addView(rlV, lp);
		
		vAdvanced = new View(this);
		vAdvanced.setBackground(getDrawable(R.drawable.advanced, R.drawable.advanced_night));
		lp = new RelativeLayout.LayoutParams(dp(26), dp(26));
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlV.addView(vAdvanced, lp);
		
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
		etSecretKey.setPadding(dp(10), dp(5), dp(10), dp(5));
		etSecretKey.setGravity(Gravity.CENTER_VERTICAL);
		etSecretKey.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		ll.addView(etSecretKey, lpLl);
		
		LinearLayout llBtn = new LinearLayout(this);
		llBtn.setPadding(dp(50), dp(60), dp(50), 0);
		lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.gravity = Gravity.CENTER_HORIZONTAL;
		ll.addView(llBtn, lpLl);
		
		tvScan = new TextView(this);
		tvScan.setPadding(0, dp(15), 0, dp(15));
		tvScan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvScan.setText(R.string.scan_qr_code);
		tvScan.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		tvScan.setGravity(Gravity.CENTER);
		tvScan.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.weight = 1;
		lpLl.gravity = Gravity.CENTER_VERTICAL;
		lpLl.rightMargin = dp(10);
		llBtn.addView(tvScan, lpLl);
		tvScan.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (ContextCompat.checkSelfPermission(view.getContext(), permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[] {permission.CAMERA}, 1);
				} else {
					startActivityForResult(new Intent(view.getContext(), QRCodeScannerActivity.class), REQ_CODE_SCAN);
				}
			}
		});
		
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
		lpLl.leftMargin = dp(10);
		llBtn.addView(tvAdd, lpLl);
		tvAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (etProvider.getText().length() <= 0 || etAccount.getText().length() <= 0 || etSecretKey.getText().length() <= 0) {
					Toast.makeText(view.getContext(), R.string.all_fields_should_not_be_empty, Toast.LENGTH_SHORT).show();
				} else {
					sendResultAndFinish(RESULT_OK, null);
				}
			}
		});
		
//		etProvider.setText("GitHub");
//		etAccount.setText("alexyuyxj");
//		etSecretKey.setText("2QVO2YJ5CNO6NSJH");
	}
	
	protected void refreshUi() {
		if (tvTitle != null) {
			tvTitle.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		}
		if (vBack != null) {
			vBack.setBackground(getDrawable(R.drawable.left, R.drawable.left_night));
		}
		if (vAdvanced != null) {
			vAdvanced.setBackground(getDrawable(R.drawable.advanced, R.drawable.advanced_night));
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
		if (tvScan != null) {
			tvScan.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
			tvScan.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		}
		if (tvAdd != null) {
			tvAdd.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
			tvAdd.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		}
	}
	
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (permission.CAMERA.equalsIgnoreCase(permissions[0])) {
			if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
				Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
			} else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startActivityForResult(new Intent(this, QRCodeScannerActivity.class), REQ_CODE_SCAN);
			}
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_CODE_SCAN) {
				etProvider.setText(data.hasExtra("provider") ? data.getStringExtra("provider") : "");
				etAccount.setText(data.hasExtra("account") ? data.getStringExtra("account") : "");
				etSecretKey.setText(data.hasExtra("secretKey") ? data.getStringExtra("secretKey") : "");
			} else if (requestCode == REQ_CODE_ADVANCED) {
				sendResultAndFinish(RESULT_OK, data);
			}
		}
	}
	
	private void sendResultAndFinish(int result, Intent i) {
		if (i == null) {
			i = new Intent();
			i.putExtra("provider", etProvider.getText().toString());
			i.putExtra("account", etAccount.getText().toString());
			i.putExtra("secretKey", etSecretKey.getText().toString());
		}
		setResult(result, i);
		finish();
	}
	
}
