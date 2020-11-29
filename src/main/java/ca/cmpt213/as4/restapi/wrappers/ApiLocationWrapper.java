package ca.cmpt213.as4.restapi.wrappers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ApiLocationWrapper {
    public int x;
    public int y;

    // MAY NEED TO CHANGE PARAMETERS HERE TO SUITE YOUR PROJECT
    public static ApiLocationWrapper makeFromCellLocation(Point cell) {
        ApiLocationWrapper location = new ApiLocationWrapper();
        location.x = (int)cell.getX();
        location.y = (int)cell.getY();
        return location;
    }

    // MAY NEED TO CHANGE PARAMETERS HERE TO SUITE YOUR PROJECT
    public static List<ApiLocationWrapper> makeFromCellLocations(Iterable<Point> cells) {
        List<ApiLocationWrapper> locations = new ArrayList<>();

        for(Point point : cells){
            locations.add(ApiLocationWrapper.makeFromCellLocation(point));
        }

        return locations;
    }
}