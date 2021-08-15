package me.petterim1.digitalwatch;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.util.Calendar;

public class DigitalWatch extends CanvasWatchFaceService {

    private static final String[] WEEKDAYS = {"", "su", "ma", "ti", "ke", "to", "pe", "la"};

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        Engine() {
            super(true);
        }

        final Handler updateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    if (shouldRun()) {
                        invalidate();
                        updateTimeHandler.sendEmptyMessageDelayed(0, 1000 - (System.currentTimeMillis() % 1000));
                    }
                }
            }
        };

        Calendar calendar;
        Paint bgPaint;
        Paint datePaint;
        Paint hourPaint;
        Paint minutePaint;
        Paint secondPaint;
        Paint colonPaint;
        float xOffset;
        float yOffset;
        float secondLine;
        float timeWidth;
        float colonWidth;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(DigitalWatch.this).build());
            calendar = Calendar.getInstance();
            Resources res = DigitalWatch.this.getResources();
            xOffset = res.getDimension(R.dimen.digital_x_offset);
            yOffset = res.getDimension(R.dimen.digital_y_offset);
            secondLine = yOffset + res.getDimension(R.dimen.digital_line_height);
            bgPaint = new Paint();
            bgPaint.setColor(Color.BLACK);
            bgPaint.setAntiAlias(false);
            float textSize = res.getDimension(R.dimen.digital_text_size);
            hourPaint = textPaint(Color.WHITE, textSize);
            timeWidth = hourPaint.measureText("Xx");
            colonWidth = hourPaint.measureText("-");
            minutePaint = textPaint(Color.WHITE, textSize);
            secondPaint = textPaint(Color.WHITE, textSize);
            colonPaint = textPaint(Color.GRAY, textSize);
            datePaint = textPaint(Color.GRAY, res.getDimension(R.dimen.digital_date_text_size));
            datePaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void onDestroy() {
            updateTimeHandler.removeMessages(0);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            updateTimer();
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean ambient) {
            super.onAmbientModeChanged(ambient);
            invalidate();
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), bgPaint);
            float x = xOffset;
            canvas.drawText(add0(calendar.get(Calendar.HOUR_OF_DAY)), x, yOffset, hourPaint);
            x += timeWidth;
            canvas.drawText(":", x, yOffset, colonPaint);
            x += colonWidth;
            canvas.drawText(add0(calendar.get(Calendar.MINUTE)), x, yOffset, minutePaint);
            x += timeWidth;
            if (!isInAmbientMode()) {
                canvas.drawText(":", x, yOffset, colonPaint);
                x += colonWidth;
                canvas.drawText(add0(calendar.get(Calendar.SECOND)), x, yOffset, secondPaint);
                canvas.drawText(WEEKDAYS[calendar.get(Calendar.DAY_OF_WEEK)] + ' ' + add0(calendar.get(Calendar.DAY_OF_MONTH)) + '.' + add01(calendar.get(Calendar.MONTH)) + '.', canvas.getWidth() >> 1, secondLine, datePaint);
            }
        }

        private String add0(int n) {
            if (n < 10) return "0" + n;
            return String.valueOf(n);
        }

        private String add01(int n) {
            n++;
            if (n < 10) return "0" + n;
            return String.valueOf(n);
        }

        private Paint textPaint(int color, float size) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            paint.setAntiAlias(true);
            paint.setTextSize(size);
            return paint;
        }

        private void updateTimer() {
            updateTimeHandler.removeMessages(0);
            if (shouldRun()) {
                updateTimeHandler.sendEmptyMessage(0);
            }
        }

        private boolean shouldRun() {
            return isVisible() && !isInAmbientMode();
        }
    }
}
