package fr.istic.mob.starbk.DataBaseManager;

public class Direction {
    private String route;
    private int textColor;
    private int color;
    private String directionRoutes;

    public Direction(String route, int textColor, int color, String directionRoutes) {
        this.route = route;
        this.textColor = textColor;
        this.color = color;
        this.directionRoutes = directionRoutes;
    }

    public String getDirectionRoutes() {
        return directionRoutes;
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
        return getDirectionRoutes();
    }
}
