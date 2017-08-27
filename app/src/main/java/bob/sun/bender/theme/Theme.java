package bob.sun.bender.theme;

import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import bob.sun.bender.utils.AppConstants;

/**
 * Created by bob.sun on 17/05/2017.
 */

public class Theme {

    static private Theme defaultTheme;

    class Icons {
        @SerializedName("next")
        public String icNext;
        @SerializedName("prev")
        public String icPrev;
        @SerializedName("play")
        public String icPlay;
        @SerializedName("menu")
        public String icMenu;
    }

    private Icons icons;
    @SerializedName("wheel_outer")
    private float outer;
    @SerializedName("wheel_inner")
    private float inner;
    @SerializedName("button_size")
    private float buttonSize;
    @SerializedName("wheel_color")
    private String wheelColor;
    @SerializedName("button_color")
    private String buttonColor;
    @SerializedName("background_color")
    private String backgroundColor;
    @SerializedName("card_color")
    private String cardColor;
    @SerializedName("item_color")
    private String itemColor;
    @SerializedName("text_color")
    private String textColor;
    private String name;

    @SerializedName("wheel_shape")
    private String wheelShape;

    private int shape;
    @SerializedName("polygon_sides")
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
        this.icons = new Icons();
        this.icons.icNext = icNext;
        this.icons.icPrev = icPrev;
        this.icons.icPlay = icPlay;
        this.icons.icMenu = icMenu;

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

    public String getName() {
        return name;
    }

    public String getNextIcon() {
        return themeFolder() + icons.icNext;
    }

    public String getPrevIcon() {
        return themeFolder() + icons.icPrev;
    }

    public String getPlayIcon() {
        return themeFolder() + icons.icPlay;
    }

    public String getMenuIcon() {
        return themeFolder() + icons.icMenu;
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

    public int getShape() {
        if (wheelShape == null) {
            return AppConstants.ThemeShapeOval;
        }
        switch (wheelShape) {
            case "rect":
                shape =  AppConstants.ThemeShapeRect;
            break;
            case "oval":
                shape = AppConstants.ThemeShapeOval;
            break;
            case "polygon":
                shape = AppConstants.ThemeShapePolygon;
            break;

            default:
                shape = AppConstants.ThemeShapeOval;
            break;
        }
        return shape;
    }

    public float getOuter() {
        return outer;
    }

    public float getInner() {
        return 1.0f - inner;
    }

    public float getButtonSize() {
        return buttonSize;
    }

    private String themeFolder() {
        return Environment.getExternalStorageDirectory() + AppConstants.themeFolder + name + "/";
    }

    public static @NonNull Theme defaultTheme() {
        if (defaultTheme == null) {
            defaultTheme = new Theme(null, null, null, null, 1.0f, 0.3f, 20, "#FFFFFF", "#000000FF", "#578EBB", "#FFFFFF", "#578EBB", "#000000", AppConstants.ThemeShapeOval, 8);
            defaultTheme.setName("Default");
        }
        return defaultTheme;
    }
}
