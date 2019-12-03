package fr.istic.mob.starbk.DataBaseManager;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private String roadName;
    private String longName;
    private int color;
    private String textColor;
    private List<Direction> directions;

    public Route(String routeName, String longName, int color, String textColor) {
        this.roadName = routeName;
        this.longName = longName;
        this.color = color;
        this.textColor = textColor;
        directions = new ArrayList<>();
    }

    public String getTextColor() {
        return textColor;
    }

    public int getColor() {
        return color;
    }

    public String getRoadName() {
        return roadName;
    }

    public List<Direction> getDirections() {
        String[] splits = longName.contains("<>") ? longName.split("<>") : longName.split("->");
        for (String s : splits) {
            s = s.trim();
            directions.add(new Direction(roadName, Color.parseColor(textColor), color, s));
        }

        return directions;
    }
}
