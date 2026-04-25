package com.example.mybusinessplus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class SalesLineChartView extends View {

    private Paint linePaint, fillPaint;
    private Path linePath, fillPath;
    private float[] dataPoints = {}; // Holds the dynamic data
    private float maxValue = 0;

    public SalesLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Setup the Line style (Your Brand Gold)
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#FFD100"));
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        // Setup the Fill style (Gradient below the line)
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);

        linePath = new Path();
        fillPath = new Path();
    }

    // Method to push new data from your Activity/Database
    public void setData(float[] data) {
        this.dataPoints = data;
        this.maxValue = 0;
        // Find the highest point to scale the chart dynamically
        for (float val : data) {
            if (val > maxValue) maxValue = val;
        }
        // Add a 20% buffer to the top so the line doesn't hit the ceiling
        maxValue = maxValue * 1.2f;

        // Trigger a redraw of the screen
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Create the gradient effect from Gold to Transparent based on view height
        fillPaint.setShader(new LinearGradient(0, 0, 0, h,
                Color.parseColor("#66FFD100"), // Semi-transparent Gold
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataPoints == null || dataPoints.length < 2) return;

        float width = getWidth();
        float height = getHeight();
        float xStep = width / (dataPoints.length - 1);

        linePath.reset();
        fillPath.reset();

        // Start at the bottom left for the fill
        fillPath.moveTo(0, height);

        for (int i = 0; i < dataPoints.length; i++) {
            float x = i * xStep;
            // Calculate Y position (invert it because Y=0 is the top of the screen)
            float y = height - ((dataPoints[i] / maxValue) * height);

            if (i == 0) {
                linePath.moveTo(x, y);
                fillPath.lineTo(x, y);
            } else {
                linePath.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
        }

        // Close the fill path by drawing a line to the bottom right, then bottom left
        fillPath.lineTo(width, height);
        fillPath.close();

        // Draw the gradient fill, then the sharp line on top
        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(linePath, linePaint);
    }
}