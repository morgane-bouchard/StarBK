package fr.istic.mob.starbk.DataBaseManager;

public class Direction {
    private String route;
    private int textColor;
    private int color;
    private String direction;

    public Direction(String route, int textColor, int color, String direction) {
        this.route = route;
        this.textColor = textColor;
        this.color = color;
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public int getColor() {
        return color;
    }

    public int getTextColor() {
        return textColor;
    }

    public String getRoute() {
        return route;
    }

    @Override
    public String toString(){
        return getDirection();
    }
}
