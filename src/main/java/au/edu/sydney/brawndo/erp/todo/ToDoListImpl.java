package au.edu.sydney.brawndo.erp.todo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

public class ToDoListImpl implements ToDoList{
    private static final int MAXTASK = Integer.MAX_VALUE;
    private ArrayList<Task> taskList = null;
    // store the previous added task
    private Task previousTask = null;

    public ToDoListImpl(){
        this.taskList = new ArrayList<>();
    }

    @Override
    public Task add(Integer id, LocalDateTime dateTime, String location, String description) throws IllegalArgumentException, IllegalStateException {
        if (dateTime == null || location == null || location.isEmpty() || location.length() > 256) {
            throw new IllegalArgumentException("Invalid parameters.");
        }
        // check if the id already in the ToDoList
        if (id != null) {
            int exist = 0;
            for(Task each: taskList){
                if(each.getID() == id){
                    exist = 1;
                    break;
                }
            }
            if(exist == 1){
                throw new IllegalArgumentException("Task with the given id already exists in the ToDoList.");
            }

        }
        //check if id is null, and generate an integer
        int isNull = 0;
        if(id == null){
            isNull = 1;
            Random rand = new Random();
            int num =  rand.nextInt(MAXTASK);
            int reroll = 0;
            while(true) {
                //if generated integer duplicated regenerate the integer.
                for (Task each : taskList) {
                    if (each.getID() == num) {
                        num = rand.nextInt(MAXTASK);
                        reroll = 1;
                        break;
                    }
                }
                // if unique generated integer break the loop.
                if(reroll == 0){
                    break;
                }
                reroll = 0;
            }
            id = num;
        }
        if(isNull == 1 && previousTask != null){
            throw new IllegalStateException("Cannot add a task with a null id after adding a non-null id task.");
        }
        Task task = new TaskImpl(id, dateTime, location, description);
        taskList.add(task);
        if(isNull != 1 && previousTask == null){
            previousTask = task;
        }
        return task;
    }

    @Override
    public boolean remove(int id) {
        boolean remove = false;
        for(Task each: taskList){
            if(each.getID() == id){
                taskList.remove(each);
                remove = true;
                break;
            }
        }
        return remove;


    }

    @Override
    public Task findOne(int id) {
        for(Task each: taskList){
            if(each.getID() == id){
                return each;
            }
        }
        return null;
    }

    @Override
    public List<Task> findAll() {
        ArrayList<Task> copyList = new ArrayList<>();
        int counter = 0;
        for(Task task: taskList){
            copyList.add(counter,task);
            counter++;
        }
        return copyList;
    }

    @Override
    public List<Task> findAll(boolean completed) {
        ArrayList<Task> result = new ArrayList<>();
        for(Task each: taskList){
            if(each.isCompleted() == completed){
                result.add(each);
            }
        }
        return result;
    }

    @Override
    public List<Task> findAll(LocalDateTime from, LocalDateTime to, Boolean completed) throws IllegalArgumentException {
        if (from != null && to != null && to.isBefore(from) ) {
            throw new IllegalArgumentException("To must later than from date");
        }
        if((from != null && to != null) && from == to){
            throw new IllegalArgumentException("To must later than from date");
        }


        List<Task> result = new ArrayList<>();

        if (to != null && from != null) {
            for (Task each : taskList) {
                if (each.getDateTime().isAfter(from) && each.getDateTime().isBefore(to)) {
                    result.add(each);
                }
            }
            return result;
        }else {
            if (from == null && to == null){
//                return findAll(); // need change
                ArrayList<Task> copyList = new ArrayList<>();
                int index = 0;
                for(Task task: taskList){
                    copyList.add(index, task);
                    index++;
                }
                return copyList;

            }else if(to == null){
                for(Task each: taskList){
                    if(each.getDateTime().isAfter(from)){
                        result.add(each);
                    }
                }
            }else{
                for(Task each: taskList){
                    if(each.getDateTime().isBefore(to)){
                        result.add(each);
                    }
                }
            }
        }

        if(completed == null){
            return result;
        }else{
            result.removeIf(each -> each.isCompleted() != completed);
        }

        return result;
    }

    @Override
    public List<Task> findAll(Map<Task.Field, String> params, LocalDateTime from, LocalDateTime to, Boolean completed, boolean andSearch) throws IllegalArgumentException {
        if (from != null && to != null && to.isBefore(from)) {
            throw new IllegalArgumentException("To must later than from date");
        }
        if((from != null && to != null) && from == to){
            throw new IllegalArgumentException("To must later than from date");
        }

        List<Task> dateFilter = new ArrayList<>();

        //filter date first
        boolean filteredDate = true;
        if(from == null && to == null){
            filteredDate = false;
        }else{
            if(from != null && to != null){
                for(Task each: taskList){
                    if(each.getDateTime().isAfter(from) && each.getDateTime().isBefore(to)){
                        dateFilter.add(each);
                    }
                }
            }else if(from == null){
                for(Task each: taskList){
                    if(each.getDateTime().isBefore(to)){
                        dateFilter.add(each);
                    }
                }
            }else{
                for(Task each: taskList){
                    if(each.getDateTime().isAfter(from)){
                        dateFilter.add(each);
                    }
                }
            }
        }
        //filter complete
        List<Task> completeFilter = new ArrayList<>();
        if(completed != null){
            for(Task each: taskList){
                if(each.isCompleted() == completed){
                    completeFilter.add(each);
                }
            }
        }
        //filter param
        List<Task> paramFilter = new ArrayList<>();
        if(params != null) {
            if(params.entrySet().size() == 0){
                throw new IllegalArgumentException("Param value cannot be null");
            }
            if(params.entrySet().size() == 1){
                Iterator<Map.Entry<Task.Field, String>> iterator = params.entrySet().iterator();
                Map.Entry<Task.Field, String> entry = iterator.next();
                Task.Field field = entry.getKey();
                String value = entry.getValue();
                if (value == null || field == null) {
                    throw new IllegalArgumentException("Param value cannot be null");
                }
                for (Task each : taskList) {
                    if (each.getField(field).contains(value)) {
                        paramFilter.add(each);
                    }
                }
            }else{
                Iterator<Map.Entry<Task.Field, String>> iterator = params.entrySet().iterator();
                Map.Entry<Task.Field, String> entry = iterator.next();
                Task.Field field1 = entry.getKey();
                String value1 = entry.getValue();
                Map.Entry<Task.Field, String> entry2 = iterator.next();
                Task.Field field2 = entry2.getKey();
                String value2 = entry2.getValue();
                if (value1 == null || field1 == null || value2 == null || field2 == null) {
                    throw new IllegalArgumentException("Param value cannot be null");
                }
                for (Task each : taskList) {
                    if (each.getField(field1).contains(value1) && each.getField(field2).contains(value2)) {
                        paramFilter.add(each);
                    }
                }
//                for (Map.Entry<Task.Field, String> entry : params.entrySet()) {
//                    Task.Field field = entry.getKey();
//                    String value = entry.getValue();
//                    if (value == null || field == null) {
//                        throw new IllegalArgumentException("Param value cannot be null");
//                    }
//                    for (Task each : taskList) {
//                        if (each.getField(field).contains(value)) {
//                            paramFilter.add(each);
//                        }
//                    }
//                }
            }

        }


        ArrayList<Task> result = new ArrayList<>();
        //if andsearch we need make sure all filters have passed
        if(andSearch){
            // datefilter shoud work
            if(to != null || from != null) {
                for (Task each : dateFilter) {
                    //all filter works
                    if(completed != null && params != null){
                        if (completeFilter.contains(each) && paramFilter.contains(each)) {
                            result.add(each);
                        }
                    // only datefilter and param filter works
                    }else if(params != null){
                        if (paramFilter.contains(each)) {
                            result.add(each);
                        }
                    //only datefilter and completed filter works
                    }else if(completed != null){
                        if (completeFilter.contains(each)) {
                            result.add(each);
                        }
                    }

                }
            // date is null, complete filter works
            }else if(completed != null){
                if(params == null){
                    result.addAll(completeFilter);
                }else{
                    for(Task each: taskList){
                        if(paramFilter.contains(each) && completeFilter.contains(each)){
                            result.add(each);
                        }
                    }
                }
            // only paramfilter works
            }else{
                result.addAll(paramFilter);
            }

        }else{
            result.addAll(completeFilter);
            for(Task each: paramFilter){
                if(!result.contains(each)){
                    result.add(each);
                }
            }

            for(Task each: dateFilter){
                if(!result.contains(each)){
                    result.add(each);
                }
            }
        }
        return result;


    }

    @Override
    public void clear() {
        this.taskList = new ArrayList<>();
    }
}