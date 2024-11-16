package m.totp;

import android.content.Intent;
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

public class EditAccountActivity extends BaseActivity {
	private TextView tvTitle;
	private View vBack;
	private TextView tvProvider;
	private EditText etProvider;
	private TextView tvFinish;
	private TextView tvDelete;
	
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
				sendResultAndFinish(RESULT_CANCELED, false);
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
		
		ScrollView sc = new ScrollView(this);
		sc.setBackgroundColor(getColor(R.color.list_bg, R.color.list_bg_night));
		lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.BELOW, tvTitle.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addView(sc, lp);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sc.addView(ll, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
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
		
		LinearLayout llBtn = new LinearLayout(this);
		llBtn.setPadding(dp(50), dp(60), dp(50), 0);
		lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.gravity = Gravity.CENTER_HORIZONTAL;
		ll.addView(llBtn, lpLl);
		
		tvFinish = new TextView(this);
		tvFinish.setPadding(dp(50), dp(15), dp(50), dp(15));
		tvFinish.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvFinish.setText(R.string.finish);
		tvFinish.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		tvFinish.setGravity(Gravity.CENTER);
		tvFinish.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.gravity = Gravity.CENTER_VERTICAL;
		lpLl.weight = 1;
		lpLl.rightMargin = dp(10);
		llBtn.addView(tvFinish, lpLl);
		tvFinish.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (etProvider.getText().length() <= 0) {
					Toast.makeText(view.getContext(), R.string.all_fields_should_not_be_empty, Toast.LENGTH_SHORT).show();
				} else {
					sendResultAndFinish(RESULT_OK, false);
				}
			}
		});
		
		tvDelete = new TextView(this);
		tvDelete.setPadding(0, dp(15), 0, dp(15));
		tvDelete.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvDelete.setText(R.string.delete);
		tvDelete.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		tvDelete.setGravity(Gravity.CENTER);
		tvDelete.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		lpLl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpLl.gravity = Gravity.CENTER_VERTICAL;
		lpLl.weight = 1;
		lpLl.leftMargin = dp(10);
		llBtn.addView(tvDelete, lpLl);
		tvDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				sendResultAndFinish(RESULT_OK, true);
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
		if (tvProvider != null) {
			tvProvider.setTextColor(getColor(R.color.account, R.color.account_night));
		}
		if (etProvider != null) {
			etProvider.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
			etProvider.setBackground(getDrawable(R.drawable.input_bg, R.drawable.input_bg_night));
		}
		if (tvFinish != null) {
			tvFinish.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
			tvFinish.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		}
		if (tvDelete != null) {
			tvDelete.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
			tvDelete.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		}
	}
	
	private void sendResultAndFinish(int result, boolean delete) {
		Intent i = new Intent();
		i.putExtra("delete", delete);
		i.putExtra("id", getIntent().getStringExtra("id"));
		i.putExtra("provider", etProvider.getText().toString());
		setResult(result, i);
		finish();
	}
	
}
