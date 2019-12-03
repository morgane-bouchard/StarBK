package fr.istic.mob.starbk;

import android.net.Uri;
import android.provider.BaseColumns;

public interface StarContract {

    String AUTHORITY = "fr.istic.starproviderXY";

    Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    interface BusRoutes {
        String CONTENT_PATH = "busroute";
        Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fr.istic.starproviderXY.busroute";
        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fr.istic.starproviderXY.busroute";

        interface BusRouteColumns extends BaseColumns {
            String SHORT_NAME = "route_short_name";
            String LONG_NAME = "route_long_name";
            String DESCRIPTION = "route_desc";
            String TYPE = "route_type";
            String COLOR = "route_color";
            String TEXT_COLOR = "route_text_color";
        }
    }

    interface Trips {
        String CONTENT_PATH = "trip";
        Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fr.istic.starproviderXY.trip";
        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fr.istic.starproviderXY.trip";

        interface TripColumns extends BaseColumns {
            String ROUTE_ID = "route_id";
            String SERVICE_ID = "service_id";
            String HEADSIGN = "trip_headsign";
            String DIRECTION_ID = "direction_id";
            String BLOCK_ID = "block_id";
            String WHEELCHAIR_ACCESSIBLE = "wheelchair_accessible";
        }
    }

    interface Stops {
        String CONTENT_PATH = "stop";
        Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fr.istic.starproviderXY.stop";
        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fr.istic.starproviderXY.stop";

        interface StopColumns extends BaseColumns {
            String NAME = "stop_name";
            String DESCRIPTION = "stop_desc";
            String LATITUDE = "stop_lat";
            String LONGITUDE = "stop_lon";
            String WHEELCHAIR_BOARDING = "wheelchair_boarding";
        }
    }

    interface StopTimes {
        String CONTENT_PATH = "stoptime";
        Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
        // select stop_time.*, trip.*, calendar.*
        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fr.istic.starproviderXY.stoptime";
        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fr.istic.starproviderXY.stoptime";

        interface StopTimeColumns extends BaseColumns {
            String TRIP_ID = "trip_id";
            String ARRIVAL_TIME = "arrival_time";
            String DEPARTURE_TIME = "departure_time";
            String STOP_ID = "stop_id";
            String STOP_SEQUENCE = "stop_sequence";
        }
    }

    interface Calendar {
        String CONTENT_PATH = "calendar";
        Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fr.istic.starproviderXY.calendar";
        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fr.istic.starproviderXY.calendar";

        interface CalendarColumns extends BaseColumns {
            String MONDAY = "monday";
            String TUESDAY = "tuesday";
            String WEDNESDAY = "wednesday";
            String THURSDAY = "thursday";
            String FRIDAY = "friday";
            String SATURDAY = "saturday";
            String SUNDAY = "sunday";
            String START_DATE = "start_date";
            String END_DATE = "end_date";
        }
    }

    interface RouteDetails {
        String CONTENT_PATH = "routedetails";
        Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
        // select stop.stop_name, stop_time.arrival_time
        String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fr.istic.starproviderXY.routedetails";
        String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fr.istic.starproviderXY.routedetails";
    }
}