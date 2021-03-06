/*
 * Copyright 2014 Chris Renke
 * Modifications Copyright 2015 Fred Grott(GrottWorkShop)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.shareme.gwsspannable.library;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import static java.lang.Math.ceil;


/**
 * A {@link ReplacementSpan} that monospaces single-line text.
 *
 * Do we need to adjust for text sizes under <1f due to the hardware acceleration canvas.drawText
 * bug on 4.x and L devices?
 * Created by fgrott on 9/7/2015.
 */
@SuppressWarnings("unused")
public class MonospaceSpan extends ReplacementSpan {

    private static final String REFERENCE_CHARACTERS = "MW";

    private final String relativeCharacters;

    /**
     * Set the {@code relativeMonospace} flag to true to monospace based on the widest character
     * in the content string; false will base the monospace on the widest width of 'M' or 'W'.
     */
    public MonospaceSpan(boolean relativeMonospace) {
        this.relativeCharacters = relativeMonospace ? null : REFERENCE_CHARACTERS;
    }

    /** Use the widest character from {@code relativeCharacters} to determine monospace width. */
    public MonospaceSpan(String relativeCharacters) {
        this.relativeCharacters = relativeCharacters;
    }

    public MonospaceSpan() {
        this.relativeCharacters = REFERENCE_CHARACTERS;
    }

    /**
     *
     * @param paint the paint object
     * @param text the char sequence
     * @param start the start of the char sequence
     * @param end the end of the char sequence
     * @param fm the font metrics
     * @return the size
     */
    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm != null) paint.getFontMetricsInt(fm);
        return (int) ceil((end - start) * getMonoWidth(paint, text.subSequence(start, end)));
    }

    /**
     *
     * @param canvas the canvas
     * @param text the char sequence
     * @param start the start of the char sequence
     * @param end the end of the char sequence
     * @param x the x
     * @param top the top
     * @param y the y
     * @param bottom the bottom
     * @param paint the paint object
     */
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, Paint paint) {
        CharSequence actualText = text.subSequence(start, end);
        float monowidth = getMonoWidth(paint, actualText);
        for (int i = 0; i < actualText.length(); i++) {
            float textWidth = paint.measureText(actualText, i, i + 1);
            float halfFreeSpace = (textWidth - monowidth) / 2f;
            canvas.drawText(actualText, i, i + 1, x + (monowidth * i) - halfFreeSpace, y, paint);
        }
    }

    /**
     *
     * @param paint the paint object
     * @param text the char sequence
     * @return the max width
     */
    private float getMonoWidth(Paint paint, CharSequence text) {
        text = relativeCharacters == null ? text : relativeCharacters;
        float maxWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            maxWidth = Math.max(paint.measureText(text, i, i + 1), maxWidth);
        }
        return maxWidth;
    }

}
