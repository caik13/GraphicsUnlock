package com.caik13.graphicsunlock;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class NineView extends View {

	private Context mContext;
	public static final int RADIUS = 100;
	private int height, width;
	private Paint mPaint;
	private boolean isInit = true;
	private Point firstPoint = null;
	private Point[][] points = new Point[3][3];
	private int moveX, moveY;
	private Paint linePaint;
	private Path path = new Path();
	private OnCompleteListener onCompleteListener;
	private String pwdKey = "123456789";
	private String pwd = "";
	char[] pwdKeys = new char[9];
	private Map<String, String> pwdMap = new LinkedHashMap<String, String>();

	public NineView(Context context) {
		this(context, null);
	}

	public NineView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setStyle(Paint.Style.STROKE);
	}

	private void initPwd() {
		byte[] pwdBytes = pwdKey.getBytes();
		for (int i = 0; i < pwdBytes.length; i++) {
			pwdKeys[i] = (char) pwdBytes[i];
		}
	}

	private void initOneViews() {
		initPwd();
		int m = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				points[i][j] = new Point(width / 4 * (j + 1), (height - width)
						/ 2 + width / 4 * (i + 1), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
								width / 32, mContext.getResources().getDisplayMetrics()), pwdKeys[m]);
				m++;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isInit) {
			isInit = false;
			width = getWidth();
			height = getHeight();
			initOneViews();
		}

		if (firstPoint != null) {
			canvas.drawPath(path, linePaint);
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (points[i][j].isSelected()) {
					mPaint.setColor(Color.RED);
					canvas.drawCircle(points[i][j].getCx(),
							points[i][j].getCy(), points[i][j].getRadius(),
							mPaint);
				} else {
					mPaint.setColor(Color.BLACK);
					canvas.drawCircle(points[i][j].getCx(),
							points[i][j].getCy(), points[i][j].getRadius(),
							mPaint);
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			moveX = (int) event.getX();
			moveY = (int) event.getY();
			clearDraw();
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (points[i][j].isInMyPlace(moveX, moveY)) {
						points[i][j].setSelected(true);
						firstPoint = points[i][j];
						pwdMap.put(String.valueOf(points[i][j].getPwd()),"");
						path.moveTo(moveX, moveY);
						break;
					}
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			moveX = (int) event.getX();
			moveY = (int) event.getY();
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (points[i][j].isInMyPlace(moveX, moveY)) {
						points[i][j].setSelected(true);
						pwdMap.put(String.valueOf(points[i][j].getPwd()),"");
						path.lineTo(points[i][j].getCx(), points[i][j].getCy());
					}
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			//
			String str = "";
			for (String ss : pwdMap.keySet()) {
				str += ss;
			}
			if (str.equals(pwd)) {
				if (null != onCompleteListener) {
					onCompleteListener.onComplete(str);
				}
			}else{
				
			}
			System.out.println(str);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void clearDraw() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				points[i][j].setSelected(false);
			}
		}
	}
	
	public void setPwdKey(String str) {
		this.pwdKey = str;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
		this.onCompleteListener = onCompleteListener;
	}

	public interface OnCompleteListener {
		public void onComplete(String str);
	}

	class Point {
		private int radius;
		private float cx, cy;
		private boolean selected = false;
		private char pwd;

		public Point(float cx, float cy, int radius, char pwd) {
			this.cx = cx;
			this.cy = cy;
			this.radius = radius;
			this.pwd = pwd;
		}

		public int getRadius() {
			return radius;
		}

		public void setRadius(int radius) {
			this.radius = radius;
		}

		public float getCx() {
			return cx;
		}

		public void setCx(float cx) {
			this.cx = cx;
		}

		public float getCy() {
			return cy;
		}

		public void setCy(float cy) {
			this.cy = cy;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public char getPwd() {
			return pwd;
		}

		public void setPwd(char pwd) {
			this.pwd = pwd;
		}

		public boolean isInMyPlace(int x, int y) {
			boolean xb = x < (cx + radius) && x > (cx - radius);
			boolean yb = y < (cy + radius) && y > (cy - radius);
			return (xb && yb);
		}

	}
}
