package ua.bellkross.client.model;


import java.util.ArrayList;

public class Room {

    private int dbID;
    private int serverDbID;
    private int arrayListID;
    private String name;
    private String password;
    private ArrayList<Task> tasks;

    public Room(String name, String password) {
        this.name = name;
        this.password = password;
        this.tasks = new ArrayList<Task>();
    }

    public Room(String name, String password, ArrayList<Task> tasks) {
        this.name = name;
        this.password = password;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        String result = "Room{" +
                "dbID=" + dbID +
                ", serverDbID=" + serverDbID +
                ", arrayListID=" + arrayListID +
                ", name='" + name + '\'' +
                ", password='" + password + "\' \n";

        for (Task item : this.tasks) {
            result += item.toString();
        }

        return result + "};";
    }

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public int getServerDbID() {
        return serverDbID;
    }

    public void setServerDbID(int serverDbID) {
        this.serverDbID = serverDbID;
    }

    public int getArrayListID() {
        return arrayListID;
    }

    public void setArrayListID(int arrayListID) {
        this.arrayListID = arrayListID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
