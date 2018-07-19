package com.supjain.checkedcircleprogressbar;

import android.graphics.Paint;
import android.view.View;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;

import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.util.Log;


public final class CheckedCircleProgressBar extends View
{
	private static final int DEFAULT_STEP_COUNT = 0;
	private static final int DEFAULT_PROGRESS_COUNT = 0;
	private static final int DEFAULT_MIN_BAR_WIDTH = 5;
	private static final int DEFAULT_PROGRESS_DRAWABLE_ID = R.drawable.ic_check_circle_green_36dp;
	private static final int DEFAULT_CURRENT_STEP_INDICATOR_ID = R.drawable.circle_stroke_green;
	private static final int DEFAULT_EMPTY_STATE_DRAWABLE_ID = R.drawable.circle_stroke_gray;
	private static final int DEFAULT_PROGRESS_COLOR = R.color.style_guide_alert_confirmation;
	private static final int DEFAULT_EMPTY_STATE_COLOR = R.color.style_guide_gray;

	private Paint progressBarPaint;

	private VectorDrawableCompat progressDrawable;
	private VectorDrawableCompat currentStepIndicator;
	private VectorDrawableCompat emptyStateDrawable;

	private int progressDrawableId;
	private int currentStepIndicatorId;
	private int emptyStateDrawableId;
	private int progressColor;
	private int emptyStateColor;

	private int stepCount;
	private int connectingLineCount;
	private int progressCount;
	private int viewTopPadding;
	private int maxViewWidth;
	private int totalVectorDrawableWidth;

	public CheckedCircleProgressBar(Context context)
	{
		super(context);
		setDefaultValues();
		init();
	}

	private void setDefaultValues()
	{
		this.stepCount = DEFAULT_STEP_COUNT;
		this.progressCount = DEFAULT_PROGRESS_COUNT;
		this.progressDrawableId = DEFAULT_PROGRESS_DRAWABLE_ID;
		this.currentStepIndicatorId = DEFAULT_CURRENT_STEP_INDICATOR_ID;
		this.emptyStateDrawableId = DEFAULT_EMPTY_STATE_DRAWABLE_ID;
		this.progressColor = DEFAULT_PROGRESS_COLOR;
		this.emptyStateColor = DEFAULT_EMPTY_STATE_COLOR;
	}

	public CheckedCircleProgressBar(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs);
	}

	public CheckedCircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs)
	{
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.compatibilityProgressBar);
		stepCount = a.getInt(R.styleable.compatibilityProgressBar_stepCount, DEFAULT_STEP_COUNT);
		progressCount = a.getInt(R.styleable.compatibilityProgressBar_progressCount, DEFAULT_PROGRESS_COUNT);
		progressColor = a.getColor(R.styleable.compatibilityProgressBar_progressColor, getResources().getColor(DEFAULT_PROGRESS_COLOR));
		emptyStateColor = a.getColor(R.styleable.compatibilityProgressBar_emptyStateColor, getResources().getColor(DEFAULT_EMPTY_STATE_COLOR));
		progressDrawableId = a.getResourceId(R.styleable.compatibilityProgressBar_progressDrawable, DEFAULT_PROGRESS_DRAWABLE_ID);
		currentStepIndicatorId = a.getResourceId(R.styleable.compatibilityProgressBar_currentStepIndicator, DEFAULT_CURRENT_STEP_INDICATOR_ID);
		emptyStateDrawableId = a.getResourceId(R.styleable.compatibilityProgressBar_emptyStateDrawable, DEFAULT_EMPTY_STATE_DRAWABLE_ID);

		a.recycle();
		init();
	}

	private void init()
	{
		progressBarPaint = new Paint();
		progressBarPaint.setStyle(Paint.Style.FILL);
		progressBarPaint.setColor(progressColor);

		progressDrawable = VectorDrawableCompat.create(getResources(), progressDrawableId, null);
		currentStepIndicator = VectorDrawableCompat.create(getResources(), currentStepIndicatorId, null);
		emptyStateDrawable = VectorDrawableCompat.create(getResources(), emptyStateDrawableId, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		totalVectorDrawableWidth = calculateTotalVectorDrawableWidth(viewWidth);

		int viewHeight = measureHeight();
		viewHeight = resolveSize(viewHeight, heightMeasureSpec);

		setMeasuredDimension(viewWidth, viewHeight);
	}

	private int measureHeight()
	{
		viewTopPadding = getPaddingTop();
		int desiredHeight = (totalVectorDrawableWidth / stepCount) + viewTopPadding + getPaddingBottom();
		return desiredHeight;
	}

	private int calculateTotalVectorDrawableWidth(int viewWidth)
	{
		connectingLineCount = stepCount - 1;
		int totalVectorDrawableWidthRequired = progressDrawable.getMinimumWidth() * progressCount
				+ currentStepIndicator.getMinimumWidth() * 1
				+ emptyStateDrawable.getMinimumWidth() * (stepCount - progressCount - 1);
		int expectedViewWidth = totalVectorDrawableWidthRequired + (DEFAULT_MIN_BAR_WIDTH * connectingLineCount);
		maxViewWidth = viewWidth - getPaddingLeft() - getPaddingRight();

		if(maxViewWidth < expectedViewWidth)
		{
			int offset = expectedViewWidth - maxViewWidth;
			totalVectorDrawableWidthRequired = totalVectorDrawableWidthRequired - offset;
		}

		return totalVectorDrawableWidthRequired;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// Number of total steps should be minimum 2, to show a progress bar
		if(stepCount >= 2 && progressCount >= 0 && stepCount >= progressCount)
		{
			int vectorWidth = totalVectorDrawableWidth / stepCount;
			int barWidth = (maxViewWidth - totalVectorDrawableWidth) / connectingLineCount;
			int vectorHeight = vectorWidth + viewTopPadding;

			int barTopPadding = (vectorHeight + viewTopPadding) / 2;
			int filledBarHeight = barTopPadding + 8; // height of the filled bar = 8px
			int emptyBarHeight = barTopPadding + 3; // height of the empty bar = 3px
			int offset = 5; // Offset added to bar to connect them properly end-to-end with vector drawables
			int startingPoint = getPaddingLeft();
			int endpoint = startingPoint + vectorWidth;

			for(int i = 0; i < progressCount; i++)
			{
				progressDrawable.setBounds(startingPoint, viewTopPadding, endpoint, vectorHeight);
				progressDrawable.draw(canvas);

				if(i < (connectingLineCount))
				{
					startingPoint = startingPoint + vectorWidth - offset;
					endpoint = startingPoint + barWidth + 2 * offset;
					canvas.drawRect(startingPoint, barTopPadding, endpoint, filledBarHeight, progressBarPaint);
					startingPoint = startingPoint + barWidth + offset;
					endpoint = startingPoint + vectorWidth;
				}
			}

			if(progressCount < stepCount)
			{
				currentStepIndicator.setBounds(startingPoint, viewTopPadding, endpoint, vectorHeight);
				currentStepIndicator.draw(canvas);
				startingPoint = startingPoint + vectorWidth - offset;
				endpoint = startingPoint + barWidth + 2 * offset;

				progressBarPaint.setColor(emptyStateColor);
				for (int i = progressCount; i < connectingLineCount; i++)
				{
					canvas.drawRect(startingPoint, barTopPadding, endpoint, emptyBarHeight,progressBarPaint);
					startingPoint = startingPoint + barWidth + offset;
					endpoint = startingPoint + vectorWidth;
					emptyStateDrawable.setBounds(startingPoint, viewTopPadding, endpoint, vectorHeight);
					emptyStateDrawable.draw(canvas);
					startingPoint = startingPoint + vectorWidth - offset;
					endpoint = startingPoint + barWidth + 2 * offset;
				}
			}
		} else {
			Log.e(getClass().getSimpleName(), "Segment-count or Progress-count value not set properly ! ");
		}
	}

	public void setTotalStepCount(int stepCount)
	{
		this.stepCount = stepCount;
	}

	public void setProgressCount(int progressCount)
	{
		this.progressCount = progressCount;
	}

	public int getTotalStepCount()
	{
		return this.stepCount;
	}

	public int getProgressCount()
	{
		return this.progressCount;
	}

	public void setProgressDrawableId(int progressDrawableId)
	{
		this.progressDrawableId = progressDrawableId;
	}

	public void setCurrentStepIndicatorId(int currentStepIndicatorId)
	{
		this.currentStepIndicatorId = currentStepIndicatorId;
	}

	public void setEmptyStateDrawableId(int emptyStateDrawableId)
	{
		this.emptyStateDrawableId = emptyStateDrawableId;
	}

	public void setProgressColor(int progressColor)
	{
		this.progressColor = progressColor;
	}

	public void setEmptyStateColor(int emptyStateColor)
	{
		this.emptyStateColor = emptyStateColor;
	}
}