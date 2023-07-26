//package au.edu.sydney.brawndo.erp.spfea;
//
//import au.edu.sydney.brawndo.erp.todo.Task;
//import au.edu.sydney.brawndo.erp.todo.ToDoList;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SPFEAFacadeImp implements SPFEAFacade{
//    ToDoList toDoList;
//    int taskNum;
//    public SPFEAFacadeImp(){
//
//    }
//    @Override
//    public void setToDoProvider(ToDoList provider) {
//        if(provider == null){
//            throw new IllegalStateException();
//        }
//        this.toDoList = provider;
//        taskNum = 0;
//    }
//
//    @Override
//    public Task addNewTask(LocalDateTime dateTime, String description, String location) throws IllegalArgumentException, IllegalStateException {
//        ArrayList<String> locationLs = new ArrayList<>();
//        locationLs.add("HOME OFFICE");
//        locationLs.add("CUSTOMER SITE");
//        locationLs.add("MOBILE");
//        if(!locationLs.contains(location)){
//            throw new IllegalArgumentException();
//        }
//        if(dateTime == null || dateTime.isBefore(LocalDateTime.now()) || description == null || description.equals("")){
//            throw new IllegalArgumentException();
//        }
//
//        return toDoList.add(taskNum++,dateTime,location,description);
//
//    }
//
//    @Override
//    public void completeTask(int id) throws IllegalArgumentException, IllegalStateException {
////        for (int i = 0; i < toDoList.findAll().size(); i++) {
////            System.out.println("Hello");
////            if (toDoList.findAll().get(i).getID() == id) {
////                toDoList.findAll().get(i).complete();
////
////            }
////        }
//
//        Task t = toDoList.findOne(id);
//        if(t == null){
//            throw new IllegalArgumentException();
//        }
//        if(t.isCompleted()){
//            throw new IllegalStateException();
//        }
//        t.complete();
//    }
//
//    @Override
//    public List<Task> getAllTasks() throws IllegalStateException {
//        if(toDoList == null){
//            throw new IllegalStateException();
//        }
//        return toDoList.findAll();
//    }
//
//    @Override
//    public int addCustomer(String fName, String lName, String phone, String email) throws IllegalArgumentException {
//        return 0;
//    }
//
//    @Override
//    public Integer getCustomerID(String fName, String lName) throws IllegalArgumentException {
//        return null;
//    }
//
//    @Override
//    public List<String> getAllCustomers() {
//        return null;
//    }
//
//    @Override
//    public String getCustomerPhone(int id) throws IllegalArgumentException {
//        return null;
//    }
//
//    @Override
//    public void setCustomerPhone(int id, String phone) throws IllegalArgumentException {
//
//    }
//
//    @Override
//    public String getCustomerEmail(int id) throws IllegalArgumentException {
//        return null;
//    }
//
//    @Override
//    public void setCustomerEmail(int id, String email) throws IllegalArgumentException {
//
//    }
//
//    @Override
//    public void removeCustomer(int id) throws IllegalArgumentException {
//
//    }
//}

package au.edu.sydney.brawndo.erp.spfea;

import au.edu.sydney.brawndo.erp.todo.Task;
import au.edu.sydney.brawndo.erp.todo.ToDoList;

import java.time.LocalDateTime;
import java.util.*;

public class SPFEAFacadeImpl implements SPFEAFacade{
    int taskNum;
    private ToDoList provider = null;
    private Map<Integer, Map<String, String>> customers;
    private int nextID;
    public SPFEAFacadeImpl(){
        customers = new HashMap<>();
        nextID = 1;
    }

    @Override
    public void setToDoProvider(ToDoList provider) {
        this.provider = provider;
        taskNum = 0;
    }

    @Override
    public Task addNewTask(LocalDateTime dateTime, String description, String location) throws IllegalArgumentException, IllegalStateException {
        if(provider == null){
            throw new IllegalStateException();
        }

        if(dateTime == null || dateTime.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException();
        }

        if(description == null || description.isEmpty()){
            throw new IllegalArgumentException();
        }

        if(!Arrays.asList("HOME OFFICE", "CUSTOMER SITE", "MOBILE").contains(location)){
            throw new IllegalArgumentException();
        }

        return provider.add(taskNum++, dateTime, location, description);
    }

    @Override
    public void completeTask(int id) throws IllegalArgumentException, IllegalStateException {
        if(provider == null){
            throw new IllegalStateException();
        }

        Task t = provider.findOne(id);
        if(t == null){
            throw new IllegalArgumentException();
        }

        if(t.isCompleted()){
            throw new IllegalStateException();
        }

        t.complete();
    }

    @Override
    public List<Task> getAllTasks() throws IllegalStateException {
        if(provider == null){
            throw new IllegalStateException();
        }

        return provider.findAll();
    }

    @Override
    public int addCustomer(String fName, String lName, String phone, String email) throws IllegalArgumentException {
        if(fName == null || fName.isEmpty() || lName == null || lName.isEmpty()){
            throw new IllegalArgumentException();
        }
        if (email != null && email.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if((phone == null) && (email == null)){
            throw new IllegalArgumentException();
        }

        if(phone != null && (!phone.matches("^[()+\\d]*$") || phone.isEmpty())){
            throw new IllegalArgumentException();
        }

        if (email != null && (!email.contains("@"))) {
            throw new IllegalArgumentException();
        }



        for(Map<String, String> info : customers.values()){
            if(info.get("fName").equals(fName) && info.get("lName").equals(lName)){
                throw new IllegalArgumentException();
            }
        }

        int id = nextID;
        nextID++;

        Map<String, String> custInfo = new HashMap<>();
        custInfo.put("fName", fName);
        custInfo.put("lName", lName);
        if(phone != null){
            custInfo.put("phone", phone);
        }

        if(email != null){
            custInfo.put("email", email);
        }

        customers.put(id, custInfo);

        return id;
    }

    @Override
    public Integer getCustomerID(String fName, String lName) throws IllegalArgumentException {
        if(fName == null || fName.isEmpty() || lName == null || lName.isEmpty()){
            throw new IllegalArgumentException();
        }

        for(Map.Entry<Integer, Map<String, String>> entry : customers.entrySet()){
            Map<String, String> info = entry.getValue();
            if(info.get("fName").equals(fName) && info.get("lName").equals(lName)){
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public List<String> getAllCustomers() {
        List<String> names = new ArrayList<>();
        for(Map<String, String> info : customers.values()){
            String name = info.get("lName") + ", " + info.get("fName");
            names.add(name);
        }
        return names;
    }

    @Override
    public String getCustomerPhone(int id) throws IllegalArgumentException {
        if(!customers.containsKey(id)){
            throw new IllegalArgumentException();
        }

        Map<String, String> info = customers.get(id);
        return info.get("phone");
    }

    @Override
    public void setCustomerPhone(int id, String phone) throws IllegalArgumentException {
        if(!customers.containsKey(id)){
            throw new IllegalArgumentException();
        }

        Map<String, String> info = customers.get(id);

        if(phone == null && info.get("email") == null){
            throw new IllegalArgumentException();
        }

        if(phone != null && (phone.isEmpty() || !phone.matches("^[()+\\d]*$"))){
            throw new IllegalArgumentException();
        }

        info.put("phone", phone);
    }

    @Override
    public String getCustomerEmail(int id) throws IllegalArgumentException {
        if(!customers.containsKey(id)){
            throw new IllegalArgumentException();
        }

        Map<String, String> info = customers.get(id);
        return info.get("email");
    }

    @Override
    public void setCustomerEmail(int id, String email) throws IllegalArgumentException {
        if(!customers.containsKey(id)){
            throw new IllegalArgumentException();
        }

        Map<String, String> info = customers.get(id);

        if(email == null && info.get("phone") == null){
            throw new IllegalArgumentException();
        }

        if(email != null && (!email.contains("@"))){
            throw new IllegalArgumentException();
        }

        info.put("email", email);
    }

    @Override
    public void removeCustomer(int id) throws IllegalArgumentException {
        if(!customers.containsKey(id)){
            throw new IllegalArgumentException();
        }

        customers.remove(id);
    }
}
