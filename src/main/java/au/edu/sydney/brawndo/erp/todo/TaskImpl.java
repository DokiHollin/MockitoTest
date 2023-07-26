package au.edu.sydney.brawndo.erp.todo;

import java.time.LocalDateTime;

public class TaskImpl implements Task{
    private int id;
    private java.time.LocalDateTime dateTime;
    private java.lang.String location;
    private java.lang.String description;

    private boolean complete = false;

    public TaskImpl(int id, java.time.LocalDateTime dateTime, java.lang.String location, java.lang.String description){
        this.id = id;
        if(dateTime == null){
            throw new IllegalArgumentException("dateTime cannot be null.");
        }
        this.dateTime = dateTime;
        if(location == null || location.length() >= 256){
            throw new IllegalArgumentException("location cannot be null and not longer than 256 characters");
        }
        this.location = location;
        this.description = description;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getDescription() {
        if(description == null){
            return null;
        }
        return description;
    }

    @Override
    public boolean isCompleted() {
        return complete;
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) throws IllegalArgumentException {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime may not be null");
        }

        this.dateTime = dateTime;
    }

    @Override
    public void setLocation(String location) throws IllegalArgumentException {
        if (location == null || location.isEmpty() || location.length() > 256) {
            throw new IllegalArgumentException("may not be null, empty, and must be 256 characters or less");
        }

        this.location = location;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void complete() throws IllegalStateException {
        if (complete) {
            throw new IllegalStateException("Task already completed");
        }

        this.complete = true;
    }

    @Override
    public String getField(Field field) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("Field may not be null");
        }
        return switch (field) {
            case LOCATION -> location;
            case DESCRIPTION -> description;
        };
    }
}