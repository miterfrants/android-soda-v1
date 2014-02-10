package com.planb.soda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.widget.Button;


@SuppressLint({ "DrawAllocation", "ViewConstructor" })
public class ArrowButton extends Button {
	public ArrowButton(Context context) {
		super(context);
		this.setBackgroundColor(0x00FFFFFF);
		
		Path path=new Path();
		path.setFillType(Path.FillType.EVEN_ODD);
		int base=(int) (ShareVariable.screenW*0.0625);
		path.moveTo(base/2, 0);
		path.lineTo(base, base);
		path.lineTo(base/2,(float) (base*0.75));
		path.lineTo(0, base);
		path.lineTo(base/2,0);
		path.close();
		
		ShapeDrawable d=new ShapeDrawable(new PathShape(path, base, base));
		Paint paint = d.getPaint();
		paint.setColor(0xff33b5e5);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		this.setBackground(d);
	}
}
