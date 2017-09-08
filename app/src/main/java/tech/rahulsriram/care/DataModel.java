package tech.rahulsriram.care;

/**
 * Created by jebineinstein on 22/8/16.
 */
public class DataModel {

    String number;
    String name;
    String latitude;
    String longitude;
    String item;
    String description;
    String itemid;

    public DataModel(String number,String name,String latitude,String longitude,String version, String id_, String fitemid) {
        this.number=number;
        this.name = name;
        this.latitude=latitude;
        this.longitude=longitude;
        this.item = version;
        this.description = id_;
        this.itemid = fitemid;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getItem() {
        return item;
    }

    public String getDescription() {
        return description;
    }

    public String getitemid() {
        return itemid;
    }
}