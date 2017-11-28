package ua.bellkross.client.model;


import java.util.ArrayList;

public class ArrayListRooms {

    private static ArrayList<Room> instance;

    private ArrayListRooms(){
        instance = new ArrayList<>();
    }

    public static ArrayList<Room> getInstance(){
        if (instance == null) {
            new ArrayListRooms();
            return instance;
        } else {
            return instance;
        }
    }

    @Override
    public String toString() {
        String result = "Rooms { ";
        for (Room item : instance) {
            result += item.toString() + "\n";
        }

        return result + "};";
    }
}
