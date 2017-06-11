package bob.sun.bender.theme;

import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;

import bob.sun.bender.utils.AppConstants;

/**
 * Created by bob.sun on 17/05/2017.
 */

public class Theme {

    static private Theme defaultTheme;
    public String icNext, icPrev, icPlay, icMenu;
    public float outer, inner, buttonSize;
    public String wheelColor, buttonColor, backgroundColor, cardColor, itemColor, textColor;
    private String name;
    public int shape;
    public int sides;

    public Theme(String icNext,
                 String icPrev,
                 String icPlay,
                 String icMenu,
                 float outer,
                 float inner,
                 float buttonSize,
                 String wheelColor,
                 String buttonColor,
                 String backgroundColor,
                 String cardColor,
                 String itemColor,
                 String textColor,
                 int shape,
                 int sides) {
        this.icNext = icNext;
        this.icPrev = icPrev;
        this.icPlay = icPlay;
        this.icMenu = icMenu;
        this.outer = outer;
        this.inner = inner;
        this.buttonSize = buttonSize;
        this.wheelColor = wheelColor;
        this.buttonColor = buttonColor;
        this.backgroundColor = backgroundColor;
        this.cardColor = cardColor;
        this.itemColor = itemColor;
        this.textColor = textColor;
        this.shape = shape;
        this.sides = sides;
    }

    public Theme setName(String name) {
        this.name = name;
        return this;
    }

    public String getNextIcon() {
        return themeFolder() + icNext;
    }

    public String getPrevIcon() {
        return themeFolder() + icPrev;
    }

    public String getPlayIcon() {
        return themeFolder() + icPlay;
    }

    public String getMenuIcon() {
        return themeFolder() + icMenu;
    }

    public int getWheelColor() {
        return Color.parseColor(wheelColor);
    }

    public int getButtonColor() {
        return Color.parseColor(buttonColor);
    }

    public int getBackgroundColor() {
        return Color.parseColor(backgroundColor);
    }

    public int getCardColor() {
        return Color.parseColor(cardColor);
    }

    public int getItemColor() {
        return Color.parseColor(itemColor);
    }

    public int getTextColor() {
        return Color.parseColor(textColor);
    }

    private String themeFolder() {
        return Environment.getExternalStorageDirectory() + AppConstants.themeFolder + name + "/";
    }

    public static @NonNull Theme defaultTheme() {
        if (defaultTheme == null) {
            defaultTheme = new Theme(null, null, null, null, 1.0f, 0.5f, 20, "#FFFFFF", "#FFFFFFFF", "#CCCCCC", "#FFFFFF", "#CCCCCC", "#000000", AppConstants.ThemeShapeOval, 0);
            defaultTheme.setName("Default");
        }
        return defaultTheme;
    }
}
