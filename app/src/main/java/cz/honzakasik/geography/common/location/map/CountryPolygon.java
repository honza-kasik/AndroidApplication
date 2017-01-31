package cz.honzakasik.geography.common.location.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.map.layer.overlay.Polygon;

import static cz.honzakasik.geography.common.location.map.PaintUtils.createColor;
import static cz.honzakasik.geography.common.location.map.PaintUtils.getPaint;

class CountryPolygon extends Polygon {

    private boolean highlighted;
    private final int originalFillColor;
    private int currentFillColor;

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted, int fillColor, long duration) {
        this.highlighted = highlighted;
        if (highlighted) {
            highlight(fillColor, duration);
        } else {
            removeHighlight();
        }
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (highlighted) {
            highlight();
        } else {
            removeHighlight();
        }
    }

    public void setHighlighted(boolean highlighted, int fillColor) {
        this.highlighted = highlighted;
        if (highlighted) {
            highlight(fillColor);
        } else {
            removeHighlight();
        }
    }

    public CountryPolygon(int colorFill, Paint paintStroke, GraphicFactory graphicFactory) {
        super(getPaint(colorFill), paintStroke, graphicFactory);
        this.originalFillColor = colorFill;
    }

    private void highlight() {
        setFillPaintAndRequestRedraw(createColor(255, 0, 0));
    }

    private void highlight(int fillColor) {
        setFillPaintAndRequestRedraw(fillColor);
    }

    private void highlight(int fillColor, long animationDuration) {
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                originalFillColor,
                fillColor);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setFillPaintAndRequestRedraw((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(animationDuration);
        valueAnimator.start();
        Paint paint = PaintUtils.getPaint(PaintUtils.createColor(0, 0, 0));
        paint.setStrokeWidth(5);
        setPaintStroke(paint);
    }

    public void blinkAndHighlight(final int fillColor, final long animationDuration) {
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                originalFillColor,
                fillColor);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setFillPaintAndRequestRedraw((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                highlight(fillColor, animationDuration);
            }
        });
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setDuration(animationDuration);
        valueAnimator.start();
    }

    private void removeHighlight() {
        setFillPaintAndRequestRedraw(this.originalFillColor);
    }

    /**
     *
     * @return true if polygon have not got its original color
     */
    public boolean isPainted() {
        return currentFillColor != originalFillColor;
    }

    private void setFillPaintAndRequestRedraw(int fillColor) {
        this.currentFillColor = fillColor;
        setPaintFill(getPaint(fillColor));
        requestRedraw();
    }
}
