package m.totp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import m.totp.QRCodeScanner.OnScanResult;

public class QRCodeScannerActivity extends BaseActivity {
	private TextView tvTitle;
	private View vBack;
	private TextureView tvPreviewer;
	
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
				sendResultAndFinish(RESULT_CANCELED, null, null, null);
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
		
		RelativeLayout rlPreviewer = new RelativeLayout(this);
		rlPreviewer.setBackground(new ColorDrawable(getColor(R.color.font_color, R.color.font_color_night)));
		lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.BELOW, tvTitle.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addView(rlPreviewer, lp);
		
		tvPreviewer = new TextureView(this);
		lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		rlPreviewer.addView(tvPreviewer, lp);
		QRCodeScanner.bind(tvPreviewer, new OnScanResult() {
			public void onResult(String result) {
				try {
					TOTP account = TOTP.fromUri(String.valueOf(System.currentTimeMillis()), result);
					sendResultAndFinish(RESULT_OK, account.getProvider(), account.getAccount(), account.getSecretKey());
				} catch (Throwable t) {}
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
	}
	
	private void sendResultAndFinish(int result, String provider, String account, String secretKey) {
		Intent i = new Intent();
		i.putExtra("provider", provider);
		i.putExtra("account", account);
		i.putExtra("secretKey", secretKey);
		setResult(result, i);
		finish();
	}
}
