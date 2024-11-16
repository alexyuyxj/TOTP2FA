package m.totp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

public class QRCodeScanner {
	private TextureView previewer;
	private Handler handler;
	private CameraDevice camera;
	private ImageReader imageReader;
	private OnScanResult listener;
	
	public static void bind(TextureView previewer, OnScanResult listener) {
		new QRCodeScanner(previewer, listener);
	}
	
	private QRCodeScanner(TextureView previewer, OnScanResult listener) {
		this.previewer = previewer;
		this.listener = listener;
		HandlerThread handlerThread = new HandlerThread("camera");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
		previewer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
			public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
				openCamera(width, height);
			}
			
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
			
			}
			
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				closeCamera();
				handler.getLooper().quit();
				return false;
			}
			
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {
			
			}
		});
	}
	
	@SuppressLint({"MissingPermission", "Range"})
	private void openCamera(int width, int height) {
		if (camera == null) {
			String cameraId = null;
			CameraManager cameraManager = (CameraManager) previewer.getContext().getSystemService(Context.CAMERA_SERVICE);
			final int[] previewSize = new int[2];
			try {
				for (String cid : cameraManager.getCameraIdList()) {
					//描述相机设备的属性类
					CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cid);
					//获取是前置还是后置摄像头
					Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
					//使用后置摄像头
					if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
						cameraId = cid;
						StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
						Size[] sizeMap = map.getOutputSizes(SurfaceTexture.class);
						previewSize[0] = sizeMap[0].getWidth();
						previewSize[1] = sizeMap[0].getHeight();
						break;
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			
			if (cameraId != null) {
				try {
					Matrix matrix = new Matrix();
					if ((width >= height && previewSize[0] >= previewSize[1]) || (width <= height && previewSize[0] <= previewSize[1])) {
						matrix.preScale(((float) previewSize[0] * height) / (previewSize[1] * width), 1);
						matrix.preTranslate((previewSize[0] - width) / 2f, 0);
					} else {
						matrix.preScale(1, ((float) previewSize[0] * width) / (previewSize[1] * height));
						matrix.preTranslate(0, (previewSize[1] - height) / 2f);
					}
					previewer.setTransform(matrix);
					
					imageReader = ImageReader.newInstance(previewSize[0], previewSize[1], ImageFormat.YUV_420_888, 2);
					imageReader.setOnImageAvailableListener(new OnImageAvailableListener() {
						public void onImageAvailable(ImageReader reader) {
							Image image = reader.acquireLatestImage();
							if (image == null) {
								return;
							}
							onImagePreview(image);
							image.close();
						}
					}, handler);
					cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
						public void onOpened(CameraDevice camera) {
							try {
								QRCodeScanner.this.camera = camera;
								SurfaceTexture surfaceTexture = previewer.getSurfaceTexture();
								surfaceTexture.setDefaultBufferSize(previewSize[0], previewSize[1]);
								Surface previewSurface = new Surface(surfaceTexture);
								CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
								builder.addTarget(previewSurface);
								builder.addTarget(imageReader.getSurface());
								camera.createCaptureSession(Arrays.asList(previewSurface, imageReader.getSurface()), new StateCallback() {
									public void onConfigured(CameraCaptureSession session) {
										CaptureRequest request = builder.build();
										try {
											session.setRepeatingRequest(request, null, handler);
										} catch (Throwable t) {
											t.printStackTrace();
										}
									}
									
									public void onConfigureFailed(CameraCaptureSession session) {
									
									}
								}, handler);
							} catch (CameraAccessException e) {
								e.printStackTrace();
							}
						}
						
						public void onDisconnected(CameraDevice camera) {
							camera.close();
						}
						
						public void onError(CameraDevice camera, int error) {
							camera.close();
						}
					}, handler);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	private void onImagePreview(Image image) {
		if (listener != null) {
			try {
				Plane yPlane = image.getPlanes()[0];
				GrayLuminanceSource source = new GrayLuminanceSource(image.getWidth(), image.getHeight(), yPlane.getRowStride(), yPlane.getBuffer());
				BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
				HashMap<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
				hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
				hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
				hints.put(DecodeHintType.TRY_HARDER, true);
				Result result = new QRCodeReader().decode(binaryBitmap, hints);
				String text = result.getText();
				if (text != null && text.length() > 0) {
					listener.onResult(text);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	private void closeCamera() {
		if (camera != null) {
			if (imageReader != null) {
				imageReader.close();
				imageReader = null;
			}
			camera.close();
			camera = null;
		}
	}
	
	private class GrayLuminanceSource extends LuminanceSource {
		private int rowStride;
		private ByteBuffer pixels;
		
		protected GrayLuminanceSource(int width, int height, int rowStride, ByteBuffer pixels) {
			super(width, height);
			this.rowStride = rowStride;
			this.pixels = pixels;
		}
		
		public byte[] getRow(int y, byte[] row) {
			int w = getWidth();
			if (row == null || row.length < w) {
				row = new byte[w];
			}
			pixels.position(y * rowStride);
			pixels.get(row, 0, w);
			return row;
		}
		
		public byte[] getMatrix() {
			int w = getWidth();
			int h = getHeight();
			byte[] matrix = new byte[w * h];
			for (int y = 0; y < h; y++) {
				pixels.position(y * rowStride);
				pixels.get(matrix, y * w, w);
			}
			return matrix;
		}
	}
	
	public static interface OnScanResult {
		public void onResult(String result);
	}
	
}
