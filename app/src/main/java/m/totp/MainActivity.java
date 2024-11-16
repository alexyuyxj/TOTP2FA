package m.totp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import m.totp.ProgressView.ProgressUpdater;

public class MainActivity extends BaseActivity {
	private static final int REQ_CODE_ADD = 1;
	private static final int REQ_CODE_EDIT = 2;
	private TextView tvTitle;
	private View vAdd;
	private ListView lvAccounts;
	private ArrayList<TOTP> accounts;
	private long lastBackPressed;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initAccounts();
	}
	
	public void onBackPressed() {
		long time = System.currentTimeMillis();
		if (lastBackPressed > 0 && time - lastBackPressed < 1000) {
			finish();
		} else {
			lastBackPressed = time;
			Toast.makeText(this, R.string.press_back_once_more_to_finish, Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
	
	protected void onResume() {
		super.onResume();
		((BaseAdapter) lvAccounts.getAdapter()).notifyDataSetChanged();
	}
	
	protected void initUi() {
		RelativeLayout rl = new RelativeLayout(this);
		rl.setFitsSystemWindows(true);
		setContentView(rl);
		
		tvTitle = new TextView(this);
		tvTitle.setId(rl.getChildCount() + 1);
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		tvTitle.setText(R.string.app_name);
		tvTitle.setTextColor(getResources().getColor(R.color.title_font_color, getTheme()));
		tvTitle.setPadding(dp(20), 0, dp(20), 0);
		tvTitle.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, dp(60));
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		rl.addView(tvTitle, lp);
		
		RelativeLayout rlV = new RelativeLayout(this);
		rlV.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startActivityForResult(new Intent(view.getContext(), AddAccountActivity.class), REQ_CODE_ADD);
			}
		});
		rlV.setPadding(0, 0, dp(20), 0);
		lp = new LayoutParams(dp(80), LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_TOP, tvTitle.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_BOTTOM, tvTitle.getId());
		rl.addView(rlV, lp);
		
		vAdd = new View(this);
		vAdd.setBackground(getDrawable(R.drawable.add, R.drawable.add_night));
		lp = new LayoutParams(dp(30), dp(30));
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlV.addView(vAdd, lp);
		
		lvAccounts = new ListView(this);
		lvAccounts.setBackgroundColor(getColor(R.color.list_bg, R.color.list_bg_night));
		lvAccounts.setFastScrollEnabled(true);
		lvAccounts.setSelector(new ColorDrawable(0));
		lvAccounts.setDividerHeight(0);
		lvAccounts.setCacheColorHint(0);
		lvAccounts.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				onEditAccount((TOTP) adapterView.getAdapter().getItem(i));
			}
		});
		lvAccounts.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				HashMap<String, View> children = (HashMap<String, View>) view.getTag();
				String otp = ((TextView) children.get("tvOTP")).getText().toString().replace(" ", "");
				copyToClipboard(otp);
				return true;
			}
		});
		lvAccounts.setAdapter(new BaseAdapter() {
			public int getCount() {
				if (accounts == null) {
					return 0;
				} else {
					synchronized (accounts) {
						return accounts.size();
					}
				}
			}
			
			public TOTP getItem(int position) {
				synchronized (accounts) {
					return accounts.get(position);
				}
			}
			
			public long getItemId(int position) {
				return position;
			}
			
			public View getView(int position, View view, ViewGroup viewGroup) {
				if (view == null) {
					view = createListItemView();
				}
				refreshListItemView(view, getItem(position));
				return view;
			}
		});
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.BELOW, tvTitle.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addView(lvAccounts, lp);
	}
	
	protected void refreshUi() {
		if (tvTitle != null) {
			tvTitle.setTextColor(getColor(R.color.title_font_color, R.color.title_font_color_night));
		}
		if (vAdd != null) {
			vAdd.setBackground(getDrawable(R.drawable.add, R.drawable.add_night));
		}
		if (lvAccounts != null) {
			lvAccounts.setBackgroundColor(getColor(R.color.list_bg, R.color.list_bg_night));
			lvAccounts.invalidateViews();
		}
	}
	
	private void initAccounts() {
		accounts = new ArrayList<TOTP>();
		new Thread() {
			public void run() {
				final ArrayList<TOTP> list = readAccountsFromFile();
				runOnUiThread(new Runnable() {
					public void run() {
						synchronized (accounts) {
							accounts.clear();
							accounts.addAll(list);
						}
						new Thread() {
							public void run() {
								refreshOTPs();
							}
						}.start();
					}
				});
			}
		}.start();
	}
	
	private ArrayList<TOTP> readAccountsFromFile() {
		ArrayList<TOTP> accounts = new ArrayList<TOTP>();
		File accountsFile = new File(getFilesDir(), "accounts.json");
		if (accountsFile.exists()) {
			byte[] buf = null;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(accountsFile);
				DataInputStream dis = new DataInputStream(fis);
				buf = new byte[(int) fis.getChannel().size()];
				dis.readFully(buf);
				dis.close();
				fis = null;
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Throwable t) {}
				}
			}
			if (buf != null) {
				try {
					JSONObject json = new JSONObject(new String(buf, "utf-8"));
					JSONArray jAccounts = json.optJSONArray("accounts");
					if (jAccounts != null) {
						for (int i = 0, len = jAccounts.length(); i < len; i++) {
							JSONObject jAccount = jAccounts.optJSONObject(i);
							if (jAccount != null) {
								accounts.add(new TOTP(
										jAccount.getString("id"),
										jAccount.optString("provider", "Unknown Provider"),
										jAccount.optString("account", "Unknown Account"),
										jAccount.getString("secretKey"),
										jAccount.optInt("digits", 6),
										jAccount.optInt("refreshInterval", 30000),
										jAccount.optString("algorithm", "HmacSHA1"),
										jAccount.optLong("timeBase", 0)
								));
							}
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		return accounts;
	}
	
	private void refreshOTPs() {
		HashMap<TOTP, String> otps = new HashMap<TOTP, String>();
		synchronized (accounts) {
			for (TOTP account : accounts) {
				otps.put(account, null);
			}
		}
		
		while (true) {
			boolean refreshUi = false;
			synchronized (accounts) {
				for (TOTP account : accounts) {
					if (otps.containsKey(account)) {
						String lastOTP = otps.get(account);
						try {
							String otp = account.generate();
							if (otp != null && !otp.equals(lastOTP)) {
								if (!refreshUi) {
									refreshUi = true;
								}
								otps.put(account, otp);
							}
						} catch (Throwable t) {
							t.printStackTrace();
						}
					} else {
						otps.put(account, null);
					}
				}
			}
			if (refreshUi) {
				runOnUiThread(new Runnable() {
					public void run() {
						((BaseAdapter) lvAccounts.getAdapter()).notifyDataSetChanged();
					}
				});
			}
			try {
				Thread.sleep(100);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	private View createListItemView() {
		RelativeLayout rl = new RelativeLayout(this);
		rl.setPadding(0, dp(10), 0, 0);
		
		TextView tvProvider = new TextView(this);
		tvProvider.setId(rl.getChildCount() + 1);
		tvProvider.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvProvider.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		tvProvider.setPadding(dp(30), dp(10), dp(30), 0);
		tvProvider.setGravity(Gravity.CENTER_VERTICAL);
		tvProvider.setBackground(new ColorDrawable(getColor(R.color.list_item_bg, R.color.list_item_bg_night)));
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rl.addView(tvProvider, lp);
		
		TextView tvAccount = new TextView(this);
		tvAccount.setId(rl.getChildCount() + 1);
		tvAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tvAccount.setTextColor(getColor(R.color.account, R.color.account_night));
		tvAccount.setPadding(dp(30), 0, dp(30), dp(10));
		tvAccount.setGravity(Gravity.CENTER_VERTICAL);
		tvAccount.setBackground(new ColorDrawable(getColor(R.color.list_item_bg, R.color.list_item_bg_night)));
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.BELOW, tvProvider.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rl.addView(tvAccount, lp);
		
		TextView tvOTP = new TextView(this);
		tvOTP.setId(rl.getChildCount() + 1);
		tvOTP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
		tvOTP.setTextColor(getColor(R.color.otp_color, R.color.otp_color_night));
		tvOTP.setPadding(dp(30), 0, dp(30), dp(15));
		tvOTP.setGravity(Gravity.CENTER_VERTICAL);
		tvOTP.setBackground(new ColorDrawable(getColor(R.color.list_item_bg, R.color.list_item_bg_night)));
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.BELOW, tvAccount.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rl.addView(tvOTP, lp);
		
		View vLeft = new View(this);
		vLeft.setId(rl.getChildCount() + 1);
		vLeft.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		lp = new LayoutParams(dp(10), LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_LEFT, tvProvider.getId());
		lp.addRule(RelativeLayout.ALIGN_TOP, tvProvider.getId());
		lp.addRule(RelativeLayout.ALIGN_BOTTOM, tvOTP.getId());
		rl.addView(vLeft, lp);
		
		View vEdit = new View(this);
		vEdit.setId(rl.getChildCount() + 1);
		vEdit.setBackground(getDrawable(R.drawable.right, R.drawable.right_night));
		lp = new LayoutParams(dp(24), dp(24));
		lp.rightMargin = dp(10);
		lp.addRule(RelativeLayout.ALIGN_RIGHT, tvProvider.getId());
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl.addView(vEdit, lp);
		
		ProgressView pv = new ProgressView(this);
		pv.setProgressColor(getColor(R.color.theme_color, R.color.theme_color_night));
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, dp(3));
		lp.addRule(RelativeLayout.ALIGN_RIGHT, vLeft.getId());
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addView(pv, lp);
		
		HashMap<String, Object> children = new HashMap<String, Object>();
		children.put("tvProvider", tvProvider);
		children.put("tvAccount", tvAccount);
		children.put("tvOTP", tvOTP);
		children.put("vLeft", vLeft);
		children.put("vEdit", vEdit);
		children.put("progress", pv);
		rl.setTag(children);
		return rl;
	}
	
	private void refreshListItemView(View view, TOTP account) {
		HashMap<String, Object> children = (HashMap<String, Object>) view.getTag();
		TextView tvProvider = (TextView) children.get("tvProvider");
		tvProvider.setText(account.getProvider());
		tvProvider.setTextColor(getColor(R.color.font_color, R.color.font_color_night));
		tvProvider.setBackground(new ColorDrawable(getColor(R.color.list_item_bg, R.color.list_item_bg_night)));
		
		TextView tvAccount = (TextView) children.get("tvAccount");
		tvAccount.setText(account.getAccount());
		tvAccount.setTextColor(getColor(R.color.account, R.color.account_night));
		tvAccount.setBackground(new ColorDrawable(getColor(R.color.list_item_bg, R.color.list_item_bg_night)));
		
		TextView tvOTP = (TextView) children.get("tvOTP");
		tvOTP.setTextColor(getColor(R.color.otp_color, R.color.otp_color_night));
		tvOTP.setBackground(new ColorDrawable(getColor(R.color.list_item_bg, R.color.list_item_bg_night)));
		try {
			String otp = account.generate();
			otp = otp.substring(0, account.getDigits() / 2) + " " + otp.substring(account.getDigits() / 2);
			tvOTP.setText(otp);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		View vLeft = (View) children.get("vLeft");
		vLeft.setBackground(new ColorDrawable(getColor(R.color.theme_color, R.color.theme_color_night)));
		View vEdit = (View) children.get("vEdit");
		vEdit.setBackground(getDrawable(R.drawable.right, R.drawable.right_night));
		
		ProgressView pv = (ProgressView) children.get("progress");
		pv.setProgressColor(getColor(R.color.theme_color, R.color.theme_color_night));
		ProgressUpdater updater = (ProgressUpdater) children.get("updater");
		if (updater == null) {
			updater = new ProgressUpdater() {
				public float onProgressUpdate() {
					return ((float) account.getValidTime()) / account.getRefreshInterval();
				}
			};
			children.put("updater", updater);
		}
		pv.setProgressUpdater(updater);
	}
	
	private void copyToClipboard(String otp) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData data = ClipData.newPlainText("OTP", otp);
		clipboard.setPrimaryClip(data);
		Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
	}
	
	private void onEditAccount(TOTP account) {
		Intent i = new Intent(this, EditAccountActivity.class);
		i.putExtra("id", account.getID());
		i.putExtra("provider", account.getProvider());
		startActivityForResult(i, REQ_CODE_EDIT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_CODE_ADD) {
				String algorithm = data.getStringExtra("algorithm");
				addAccount(
						data.getStringExtra("provider"),
						data.getStringExtra("account"),
						data.getStringExtra("secretKey"),
						data.getIntExtra("digit", 6),
						data.getIntExtra("refreshInterval", 30000),
						(algorithm != null && algorithm.length() > 0) ? algorithm : "HmacSHA1",
						data.getLongExtra("timeBase", 0));
			} else if (requestCode == REQ_CODE_EDIT) {
				String id = data.getStringExtra("id");
				for (TOTP account : accounts) {
					if (account.getID().equals(id)) {
						boolean delete = data.getBooleanExtra("delete", false);
						if (delete) {
							deleteAccount(account);
						} else {
							String provider = data.getStringExtra("provider");
							if (provider != null && !provider.equals(account.getProvider())) {
								editeAccount(account, provider);
							}
						}
						break;
					}
				}
			}
		}
	}
	
	private void addAccount(String provider, String account, String secretKey, int digits, int refreshInterval, String algorithm, long timeBase) {
		new Thread() {
			public void run() {
				HashSet<TOTP> set;
				synchronized (accounts) {
					set = new HashSet<TOTP>(accounts);
				}
				set.add(new TOTP(String.valueOf(System.currentTimeMillis()), provider, account, secretKey, digits, refreshInterval, algorithm, timeBase));
				ArrayList<TOTP> list = new ArrayList<TOTP>(set);
				Collections.sort(list, new Comparator<TOTP>() {
					public int compare(TOTP totp, TOTP t1) {
						return totp.getID().compareTo(t1.getID());
					}
				});
				synchronized (accounts) {
					accounts.clear();
					accounts.addAll(list);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						((BaseAdapter) lvAccounts.getAdapter()).notifyDataSetChanged();
					}
				});
				saveAccountsToFile(list);
			}
		}.start();
	}

	private void deleteAccount(final TOTP account) {
		new Thread() {
			public void run() {
				ArrayList<TOTP> list;
				synchronized (accounts) {
					accounts.remove(account);
					list = new ArrayList<TOTP>(accounts);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						((BaseAdapter) lvAccounts.getAdapter()).notifyDataSetChanged();
					}
				});
				saveAccountsToFile(list);
			}
		}.start();
	}
	
	private void editeAccount(final TOTP account, final String provider) {
		new Thread() {
			public void run() {
				ArrayList<TOTP> list;
				synchronized (accounts) {
					accounts.remove(account);
					accounts.add(new TOTP(account.getID(), provider, account.getAccount(), account.getSecretKey(),
							account.getDigits(), account.getRefreshInterval(), account.getAlgorithm(), account.getTimeBase()));
					list = new ArrayList<TOTP>(accounts);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						((BaseAdapter) lvAccounts.getAdapter()).notifyDataSetChanged();
					}
				});
				saveAccountsToFile(list);
			}
		}.start();
	}
	
	private void saveAccountsToFile(ArrayList<TOTP> accounts) {
		JSONObject json = new JSONObject();
		try {
			JSONArray jAccounts = new JSONArray();
			for (TOTP totp : accounts) {
				JSONObject jAccount = new JSONObject();
				jAccount.put("id", totp.getID());
				jAccount.put("provider", totp.getProvider());
				jAccount.put("account", totp.getAccount());
				jAccount.put("secretKey", totp.getSecretKey());
				jAccount.put("digits", totp.getDigits());
				jAccount.put("refreshInterval", totp.getRefreshInterval());
				jAccount.put("algorithm", totp.getAlgorithm());
				jAccount.put("timeBase", totp.getTimeBase());
				jAccounts.put(jAccount);
			}
			json.put("accounts", jAccounts);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		FileOutputStream fos = null;
		try {
			File accountsFile = new File(getFilesDir(), "accounts.json");
			if (!accountsFile.getParentFile().exists()) {
				accountsFile.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(accountsFile);
			fos.write(json.toString().getBytes("utf-8"));
			fos.flush();
			fos.close();
			fos = null;
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Throwable tt) {}
			}
		}
	}
}
