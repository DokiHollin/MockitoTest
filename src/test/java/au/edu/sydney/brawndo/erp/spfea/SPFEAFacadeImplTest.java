package au.edu.sydney.brawndo.erp.spfea;
import au.edu.sydney.brawndo.erp.todo.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SPFEAFacadeImplTest {
    private LocalDateTime t;
    private LocalDateTime t2;
    private LocalDateTime t3;
    private Task mockTask;
    private Task mockTask2;
    @InjectMocks
    private SPFEAFacade spfeaFacade;

    @Mock
    private ToDoList toDoList;

    @BeforeEach
    public void setUp() {
        spfeaFacade = new SPFEAFacadeImpl();
        spfeaFacade.setToDoProvider(toDoList);
        toDoList = mock(ToDoListImpl.class);
        t = LocalDateTime.now().plusHours(1);
        t2 = LocalDateTime.now().plusMonths(1);
        t3 = LocalDateTime.now().plusYears(1);
        mockTask = mock(TaskImpl.class);
        mockTask2 = mock(TaskImpl.class);
    }


    @Test
    public void addNewTaskThrowTest() {
        assertThrows(IllegalStateException.class,()->{
            spfeaFacade.setToDoProvider(null);
            spfeaFacade.addNewTask(t, "123", "HOME OFFICE");
        });
        spfeaFacade.setToDoProvider(toDoList);
        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addNewTask(t.minusMonths(1), "123", "HOME OFFICE");

        });

        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addNewTask(t, "", "HOME OFFICE");

        });

        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addNewTask(t, null, "HOME OFFICE");

        });
        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addNewTask(t, "123", "HOMsE OFFIsCE");

        });
        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addNewTask(null, "123", "MOBILE");

        });
        assertThrows(IllegalArgumentException.class, () -> {
            spfeaFacade.addNewTask(t, "123", null);

        });

    }


    @Test
    public void addNewTask_success() {
        spfeaFacade.setToDoProvider(toDoList);
        when(toDoList.add(anyInt(),any(LocalDateTime.class),eq("HOME OFFICE"),eq("description"))).thenReturn(mockTask);
        when(mockTask.getLocation()).thenReturn("HOME OFFICE");
        when(mockTask.getDateTime()).thenReturn(t);
        when(mockTask.getDescription()).thenReturn("description");
        Task task = spfeaFacade.addNewTask(t,"description","HOME OFFICE");

        assertNotNull(task);
        assertFalse(task.isCompleted());
        assertEquals("description",task.getDescription());
        assertEquals("HOME OFFICE",task.getLocation());
        verify(toDoList, times(1)).add(anyInt(),any(LocalDateTime.class),eq("HOME OFFICE"),eq("description"));
    }

    @Test
    public void completeTaskThrowTest(){
        assertThrows(IllegalStateException.class,()->{
            spfeaFacade.setToDoProvider(null);
            spfeaFacade.completeTask(1);
        });
        spfeaFacade.setToDoProvider(toDoList);
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.completeTask(1);
        });
        when(toDoList.findOne(anyInt())).thenReturn(mockTask);
        when(mockTask.isCompleted()).thenReturn(true);

        when(toDoList.add(anyInt(), any(LocalDateTime.class), eq("HOME OFFICE"), eq("description"))).thenAnswer((Answer<Task>) invocation -> {
            Object[] arguments = invocation.getArguments();
            Integer inputInt = (Integer) arguments[0]; // Get the inputted anyInt() value

            when(mockTask.getID()).thenReturn(inputInt); // Return the inputted anyInt() value for getID()
            return mockTask;
        });

        spfeaFacade.addNewTask(t,"description","HOME OFFICE");
        assertThrows(IllegalStateException.class,()->{
            spfeaFacade.completeTask(1);
        });
    }

    @Test
    public void completeTaskTest() {
        spfeaFacade.setToDoProvider(toDoList);
        when(toDoList.add(anyInt(), any(LocalDateTime.class), eq("HOME OFFICE"), eq("description"))).thenAnswer((Answer<Task>) invocation -> {
            Object[] arguments = invocation.getArguments();
            Integer inputInt = (Integer) arguments[0];
            when(mockTask.getID()).thenReturn(inputInt);
            return mockTask;
        });

        when(toDoList.findOne(anyInt())).thenReturn(mockTask);
        when(mockTask.isCompleted()).thenReturn(false);

        Task task = spfeaFacade.addNewTask(t,"description","HOME OFFICE");
        assertFalse(task.isCompleted());
        doAnswer(invocation -> {
            when(mockTask.isCompleted()).thenReturn(true);
//            System.out.println("Calling complete");
            return null;
        }).when(mockTask).complete();

        spfeaFacade.completeTask(task.getID());
        assertTrue(task.isCompleted());
        verify(toDoList, times(1)).add(anyInt(),any(LocalDateTime.class),eq("HOME OFFICE"),eq("description"));
        verify(mockTask,times(1)).complete();
    }

    @Test
    public void testNull(){
        spfeaFacade.setToDoProvider(toDoList);
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addNewTask(null,"123","MOBILE");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addNewTask(LocalDateTime.now().minusMonths(1),"123","MOBILE");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addNewTask(t,"","MOBILE");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addNewTask(t,"123","sss");
        });
    }

    @Test
    public void getAllTaskThrow(){
        assertThrows(IllegalStateException.class,()->{
            spfeaFacade.setToDoProvider(null);
            spfeaFacade.getAllTasks();
        });
        spfeaFacade.setToDoProvider(toDoList);
    }

    @Test
    public void getAllTasksTest() {
        spfeaFacade.setToDoProvider(toDoList);
        when(toDoList.add(anyInt(), any(LocalDateTime.class), eq("HOME OFFICE"), eq("des"))).thenAnswer((Answer<Task>) invocation -> {
            Object[] arguments = invocation.getArguments();
            Integer inputInt = (Integer) arguments[0]; // Get the inputted anyInt() value

            when(mockTask.getID()).thenReturn(inputInt); // Return the inputted anyInt() value for getID()
            return mockTask;
        });
//        int y = anyInt();
        when(toDoList.add(anyInt(), any(LocalDateTime.class), eq("CUSTOMER SITE"), eq("des2"))).thenAnswer((Answer<Task>) invocation -> {
            Object[] arguments = invocation.getArguments();
            Integer inputInt = (Integer) arguments[0]; // Get the inputted anyInt() value

            when(mockTask2.getID()).thenReturn(inputInt); // Return the inputted anyInt() value for getID()
            return mockTask2;
        });
        when(mockTask.isCompleted()).thenReturn(false);
        when(mockTask2.isCompleted()).thenReturn(false);

        when(mockTask.getLocation()).thenReturn("HOME OFFICE");
        when(mockTask.getDateTime()).thenReturn(t);
        when(mockTask.getDescription()).thenReturn("des");

        when(mockTask2.getLocation()).thenReturn("CUSTOMER SITE");
        when(mockTask2.getDateTime()).thenReturn(t2);
        when(mockTask2.getDescription()).thenReturn("des2");

        List<Task> ls = Arrays.asList(mockTask,mockTask2);
        when(toDoList.findAll()).thenReturn(ls);
        spfeaFacade.addNewTask(t,"des","HOME OFFICE");
        spfeaFacade.addNewTask(t2,"des2","CUSTOMER SITE");
        List<Task> tasks = spfeaFacade.getAllTasks();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());;
    }
    //============================================================================================
    @Test
    public void addCustomerExceptionTest(){
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer(null,"a","+()123","asd@.com");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("","a","+()123","asd@.com");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("a",null,"+()123","asd@.com");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("a","","+()123","asd@.com");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("a","b","123","asd.com");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("a","b","+()123","asd.com");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("a","b",null,null);
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("a","b","+()123","");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer("a","b","","123@.com");
        });
    }


    @Test
    public void addCustomerTest() {
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        int customerId = spfeaFacade.addCustomer(fName, lName, phone, email);
        assertNotNull(spfeaFacade.getAllCustomers());
        assertEquals(spfeaFacade.getCustomerID("a","b"), customerId);
    }


    @Test
    public void getCustomerIDThrow(){
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.getCustomerID(null,"a");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.getCustomerID("","a");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.getCustomerID("a","");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.getCustomerID("a",null);
        });
    }

    @Test
    public void getCustomerID_success() {
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        int customerId = spfeaFacade.addCustomer(fName, lName, phone, email);
        Integer get = spfeaFacade.getCustomerID(fName, lName);

        assertEquals(customerId, get);
        assertNull(spfeaFacade.getCustomerID("sad", "alice"));
    }

    @Test
    public void getAllCustomersTest() {
        assertTrue(spfeaFacade.getAllCustomers().isEmpty());
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        spfeaFacade.addCustomer(fName,lName,phone,email);
        List<String> ls = new ArrayList<>();
//        ls.add(lName);
//        ls.add(fName);
        String name = lName + ", " + fName;
        ls.add(name);
        System.out.println(ls);
        System.out.println(spfeaFacade.getAllCustomers());
        assertEquals(ls, spfeaFacade.getAllCustomers());

        String fName2 = "aa";
        String lName2 = "bb";
        String phone2 = "+()1234";
        String email2 = "1234@.com";
        spfeaFacade.addCustomer(fName2,lName2,phone2,email2);
        String name2 = lName2 + ", " + fName2;
        ls.add(name2);
        assertEquals(ls, spfeaFacade.getAllCustomers());
    }

    @Test
    public void getCustomerPhoneThrowTest(){
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.getCustomerPhone(100);
        });
    }

    @Test
    public void getCustomerPhoneNullTest(){
        String fName = "a";
        String lName = "b";
        String phone = null;
        String email = "@.com";
        spfeaFacade.addCustomer(fName,lName,phone,email);
        assertNull(spfeaFacade.getCustomerPhone(spfeaFacade.getCustomerID("a","b")));
    }

    @Test
    public void getCustomerPhoneTest() {
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        spfeaFacade.addCustomer(fName,lName,phone,email);
        assertEquals(phone,spfeaFacade.getCustomerPhone(spfeaFacade.getCustomerID("a","b")));
    }

    @Test
    public void setCustomerPhoneThrowTest(){
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.setCustomerPhone(1,"+()123123");
        });
        String fName = "a";
        String lName = "b";
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer(fName,lName,null,null);
            spfeaFacade.setCustomerPhone(1,"+()123123");
        });
        spfeaFacade.addCustomer(fName,lName,"+()123",null);
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.setCustomerPhone(spfeaFacade.getCustomerID("a","b"),"");
        });

        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer(fName,lName,"+()123",null);
            spfeaFacade.setCustomerPhone(1,null);
        });



    }


    @Test
    public void setCustomerPhoneTest() {
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        spfeaFacade.addCustomer(fName,lName,phone,email);
        spfeaFacade.setCustomerPhone(1,"+()222");
        assertEquals("+()222",spfeaFacade.getCustomerPhone(1));
        assertTrue(spfeaFacade.getCustomerPhone(1).contains("+()"));
    }

    @Test
    public void nullAddTest(){
        String fName = "a";
        String lname = "b";
        String phone = null;
        String email = "123@.com";
        int id = spfeaFacade.addCustomer(fName,lname,phone,email);
        assertThrows(IllegalArgumentException.class,()->{
           spfeaFacade.setCustomerPhone(id,"");
        });
        int id2 = spfeaFacade.addCustomer("d","c","+()123",null);
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.setCustomerEmail(id2,"");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.setCustomerEmail(id2,"123");
        });
    }

    @Test
    public void getCustomerEmailThrowTest() {
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.getCustomerEmail(1);
        });
    }

    @Test
    public void getCustomerEmailTest(){
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = null;
        spfeaFacade.addCustomer(fName,lName,phone,email);
        assertNull(spfeaFacade.getCustomerEmail(1));
        spfeaFacade.setCustomerEmail(1,"213@.com");
        assertTrue(spfeaFacade.getCustomerEmail(1).contains("@"));
    }

    @Test
    public void setCustomerEmailThrowTest() {
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.setCustomerEmail(1,"12344@.com");
        });
        String fName = "a";
        String lName = "b";
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer(fName,lName,null,null);
            spfeaFacade.setCustomerEmail(1,"1@.com23123");
        });

        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer(fName,lName,null,"123@.com");
            spfeaFacade.setCustomerEmail(1,"");
        });
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer(fName,lName,"+()123","123@.com");
            spfeaFacade.setCustomerEmail(1,"");
        });

        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.addCustomer(fName,lName,null,"@123");
            spfeaFacade.setCustomerEmail(1,null);
        });
    }

    @Test
    public void setCustomerEmailTest(){
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        spfeaFacade.addCustomer(fName,lName,phone,email);
        spfeaFacade.setCustomerEmail(1,"111@.com");
        assertEquals("111@.com",spfeaFacade.getCustomerEmail(1));
        spfeaFacade.addCustomer("as","asdas","+()123",null);
        spfeaFacade.setCustomerEmail(2,"1121@.com");
        assertEquals("1121@.com",spfeaFacade.getCustomerEmail(2));
        assertNotNull(spfeaFacade.getAllCustomers());
    }


    @Test
    public void removeCustomerThrowTest() {
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.removeCustomer(1);
        });
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        spfeaFacade.addCustomer(fName,lName,phone,email);
        assertThrows(IllegalArgumentException.class,()->{
            spfeaFacade.removeCustomer(1111111);
        });
    }

    @Test
    public void removeCustomerTest(){
        String fName = "a";
        String lName = "b";
        String phone = "+()123";
        String email = "123@.com";
        spfeaFacade.addCustomer(fName,lName,phone,email);
        assertEquals(1,spfeaFacade.getAllCustomers().size());
        spfeaFacade.removeCustomer(1);
        assertEquals(0,spfeaFacade.getAllCustomers().size());
    }
}