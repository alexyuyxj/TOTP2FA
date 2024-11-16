package m.totp;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class ProgressView extends View {
	private ProgressUpdater updater;
	private int color;
	private float progress;
	
	public ProgressView(Context context) {
		super(context);
		post(new Runnable() {
			public void run() {
				if (updater != null) {
					float newProgress = updater.onProgressUpdate();
					if (progress != newProgress) {
						progress = newProgress;
						invalidate();
					}
				}
				postDelayed(this, 500);
			}
		});
	}
	
	public void setProgressUpdater(ProgressUpdater updater) {
		this.updater = updater;
	}
	
	public void setProgressColor(int color) {
		this.color = color;
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float right = (progress > 1 ? 1 : progress) * getWidth();
		canvas.clipRect(0, 0, right, getHeight());
		canvas.drawColor(color);
	}
	
	public static interface ProgressUpdater {
		public float onProgressUpdate();
	}

}
