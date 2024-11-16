package m.totp;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
	private int uiMode;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiMode = getResources().getConfiguration().uiMode;
		transparentStatusBar();
		initUi();
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (uiMode != newConfig.uiMode) {
			uiMode = newConfig.uiMode;
			transparentStatusBar();
			refreshUi();
		}
	}
	
	protected int getColor(int resId, int resIdNight) {
		return getResources().getColor(isNightMode() ? resIdNight : resId, getTheme());
	}
	
	protected Drawable getDrawable(int resId, int resIdNight) {
		return getResources().getDrawable(isNightMode() ? resIdNight : resId, getTheme());
	}
	
	protected boolean isNightMode() {
		int currentNightMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;
		return (currentNightMode == Configuration.UI_MODE_NIGHT_YES);
	}
	
	protected void initUi() {
	
	}
	
	protected void refreshUi() {
	
	}
	
	private void transparentStatusBar() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
						| LayoutParams.FLAG_SECURE);
		getWindow().getDecorView().setSystemUiVisibility(
				getWindow().getDecorView().getSystemUiVisibility()
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		getWindow().setStatusBarColor(Color.TRANSPARENT);
		getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
	}
	
	protected int dp(int dpx) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpx, getResources().getDisplayMetrics()) + 0.5);
	}
	
}