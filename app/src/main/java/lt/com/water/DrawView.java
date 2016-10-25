package lt.com.water;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;


public class DrawView extends View{

	private String names;
	private int XPoint = 100; // 原点的X坐标
	private int YPoint = 400; // 原点的Y坐标
	private int XScale = 100; // X的刻度长度
	private int XLength = 200; // X轴的长度
	private int YLength = 200; // Y轴的长度
	List<DrawBean> listData;//数据
	float maxValue = 50;//x軸上面的最大的值

	public DrawView(Context context) {
		super(context);
		YLength = getScreenSize(context,true) / 5;
		YPoint = YLength + 100;
		XLength = getScreenSize(context, false) - 100;
		XPoint=getScreenSize(context,true)/20;
		XScale=getScreenSize(context,true)/15;

	}

	public void initData(List<DrawBean> list,String names){
		this.names=names;
		this.listData = list;
		if(listData != null && listData.size() > 0){
			XLength = (listData.size()) * XScale;
//			XLength = lenght > XLength ? lenght : XLength;
			setLayoutParams(new LayoutParams(XLength + 150, YLength + 150));
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setAntiAlias(true);// 去锯齿
		paint.setColor(Color.parseColor("#7d7a7d"));// 颜色
		paint.setStrokeWidth(2);

		canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);//横线

		if(listData != null && listData.size() > 0){//竖线
			for (int i = 0; i < listData.size() ; i++) {

				canvas.drawLine(XPoint + XScale * i, YPoint, XPoint + XScale * i, 100, paint);

				paint.setTextSize(20);

				canvas.drawText(listData.get((i)).xName, XPoint + XScale * i  - 10, YPoint + 20, paint);

				if(i==0){

					canvas.drawText(listData.get((0)).maxValue, XPoint + XScale * (i) - 10, 90, paint);

				}else{

					canvas.drawText("", XPoint + XScale * (i) - 10, 90, paint);
				}


			}

			for (int i = 0; i < listData.size(); i++) {
				if(i < (listData.size() - 1) ){
					paint.setColor(Color.parseColor("#2b84c2"));
					paint.setTextSize(4);
					canvas.drawLine(XPoint + XScale * i, YPoint -value2Scale(listData.get(i).currentValue) ,
							XPoint + XScale * (i + 1) , YPoint -value2Scale(listData.get(i + 1).currentValue), paint);

					paint.setColor(Color.parseColor("#00bf86"));
					paint.setTextSize(2);
					canvas.drawLine(XPoint + XScale * i, YPoint - value2Scale(listData.get(i).currentValueB),
							XPoint + XScale * (i + 1) , YPoint -value2Scale(listData.get(i + 1).currentValueB), paint);

				}
			}
		}
	}

	//根據值獲取刻度
	float value2Scale(float value){
		if(value == 0){
			return 0;
		}

		float v1 = (YLength) / maxValue;//y軸劃分為n等分

		return value * v1;
	}

	public  int getScreenSize(Context context, boolean isHeight) {

		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(dm);

		return isHeight ? dm.heightPixels : dm.widthPixels;
	}


}
