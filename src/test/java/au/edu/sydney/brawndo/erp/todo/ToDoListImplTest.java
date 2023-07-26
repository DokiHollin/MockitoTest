package au.edu.sydney.brawndo.erp.todo;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ToDoListImplTest {
    public ToDoList td;
    public LocalDateTime time;
    public LocalDateTime t;
    public LocalDateTime t2;

    @BeforeEach
    public void init(){
        ToDoFactory tf = mock(ToDoFactory.class);
        when(tf.makeToDoList()).thenReturn(new ToDoListImpl());
        td = tf.makeToDoList();
        t = LocalDateTime.now();
        time = LocalDateTime.of(
                2021, 4, 24, 14, 33, 48);
        t2 = t.plusYears(1)
                .plusMonths(1)
                .plusWeeks(1)
                .plusDays(1);
    }

    @Test
    public void addNullTest(){
        //location is null
        assertThrows(IllegalArgumentException.class,()->{
            td.add(1,time,null,"123");
        });
        //description is null
        assertThrows(IllegalArgumentException.class,()->{
            td.add(1,time,"",null);
        });
        //datetime is null
        assertThrows(IllegalArgumentException.class,()->{
            td.add(1,null,"location","123");
        });


    }
    @Test
    public void addExceedLimitTest(){
        char[] array = new char[257];
        Arrays.fill(array, 'e');
        String example = new String(array);
        assertThrows(IllegalArgumentException.class,()->{
            td.add(1,time,example,"123");
        });
    }

    @Test
    public void duplicateIdTest(){
        td.add(1,t,"location","123");
        assertThrows(IllegalArgumentException.class,()->{
            td.add(1,t2,"location2","1234");
        });
    }

    @Test
    public void nullIdTest(){
        td.add(null,t,"location","123");
        assertEquals(1,td.findAll().size());
    }

    @Test
    public void addNullAfterNonNullTest(){
        td.add(null, t, "melbourne", "finding a cafe");
        td.add(123, t, "fish city", "apologising to the fish");
        assertThrows(IllegalStateException.class,()->{
            td.add(null, t, "sydney", "more fishing");
        });


    }

    @Test
    public void addNullAfterNonNullTest2(){
        td.add(1, t, "cat town", "saying hi to the cats");
        assertThrows(IllegalStateException.class,()->{
            td.add(null, t, "dog town", "saying hi to the dogs");
        });
    }

    @Test
    public void removeTrueTest(){
        td.add(1, t, "cat", "s123");
        assertTrue(td.remove(1));
    }
    @Test
    public void removeFalseTest(){
        td.add(1, t, "cat", "hi");
        assertFalse(td.remove(2));
    }
    @Test
    public void removeTest(){
        td.add(null,t,"cat","hi");
        assertTrue(td.remove(td.findAll().get(0).getID()));
    }
    @Test
    public void findOneTest(){
        assertNull(td.findOne(100));
        td.add(1, t, "cat", "saying");
        assertNotNull(td.findOne(1));
    }

    @Test
    public void findAll1Test(){
        List<Task> findAllList = td.findAll();
        td.add(100, t, "cat", "123");
        List<Task> tl = td.findAll();
        //check same task
        assertEquals(100,tl.get(0).getID());
        //check different list object
        assertNotSame(findAllList,td.findAll());
        assertNotEquals(findAllList,td.findAll());

    }

    @Test
    public void findAll2TrueTest(){
        td.add(100, t, "cat", "123");
        td.add(200, t, "cat2", "123");
        List<Task> ls = td.findAll();
        Task completeTask = ls.get(0);
        completeTask.complete();
        List<Task> ls2 = new ArrayList<>();
        ls2.add(completeTask);
        assertEquals(ls2,td.findAll(true));
    }

    @Test
    public void findAll2FalseTest(){
        td.add(100, t, "cat", "123");
        td.add(200, t, "cat2", "123");
        assertEquals(0, td.findAll(true).size());
    }

    @Test
    public void findAll3AllNullTest(){
        td.add(100, t, "cat", "123");
        td.add(200, t, "cat2", "123");
        assertEquals(td.findAll(),td.findAll(null,null,null));
    }

    @Test
    public void findAll3NormalTest(){
        Task task1 = td.add(100, t, "cat", "123");
        td.add(200, t2, "cat2", "123");
        assertEquals(1,td.findAll(t.minusDays(1),t2,null).size());
        assertEquals(task1,td.findAll(t.minusDays(1),t2,null).get(0));
    }

    @Test
    public void findAll3ExceptionTest(){
        assertThrows(IllegalArgumentException.class, ()->{
            td.findAll(t2,t,null);
        });
    }

    @Test
    public void findAll3FromNullTest(){
        Task task1 = td.add(100, t, "cat", "123");
        td.add(200, t2, "cat2", "123");
        assertEquals(task1,td.findAll(null,t2,null).get(0));
        assertEquals(1,td.findAll(null,t2,null).size());
        td.add(300, t2.plusMonths(1), "cat3", "123");
        assertEquals(2,td.findAll(null,t2.plusDays(1),null).size());
    }

    @Test
    public void findAll3ToNullTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "cat2", "123");
        assertEquals(task2,td.findAll(t,null,null).get(0));
        assertEquals(1,td.findAll(t,null,null).size());
        Task task3 = td.add(300, t2.plusMonths(1), "cat3", "123");
        assertEquals(task3,td.findAll(t,null,null).get(1));
        assertEquals(3,td.findAll(t.minusDays(1),null,null).size());
    }

    @Test
    public void findAll3CompleteTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "cat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "cat3", "123");
        assertEquals(0,td.findAll(t.minusDays(1),null,true).size());
        assertEquals(3,td.findAll(t.minusDays(1),null,false).size());
        task2.complete();
        assertEquals(task2,td.findAll(t.minusDays(1),null,true).get(0));
    }

    @Test
    public void findAll4ExceptionTest(){
        Map<Task.Field,String> map = new HashMap<>();
        assertThrows(IllegalArgumentException.class, ()->{
            td.findAll(map,null,null,null,false);
        });

        assertThrows(IllegalArgumentException.class, ()->{
            td.findAll(null,t2,t,null,false);
        });

    }


    @Test
    public void findAll4DateTest1(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "cat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "cat3", "123");
        assertEquals(task1,td.findAll(null,t.minusDays(1),t.plusDays(1),null,false).get(0));
        assertEquals(1,td.findAll(null,t.minusDays(1),t.plusDays(1),null,false).size());
    }

    @Test
    public void findAll4DateTest2(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "123");
        assertEquals(0,td.findAll(null,t.minusDays(1),t.plusDays(1),null,true).size());
    }

    @Test
    public void findAll4DateToNullTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "123");
        assertEquals(task2,td.findAll(null,t,null,null,false).get(0));
        assertEquals(2,td.findAll(null,t,null,null,false).size());
        assertEquals(task3,td.findAll(null,t,null,null,false).get(1));
    }

    @Test
    public void findAll4DateFromNullTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "123");
        assertEquals(task1,td.findAll(null,null,t2,null,false).get(0));
        assertEquals(1,td.findAll(null,null,t2,null,false).size());
    }

    @Test
    public void findAll4CompleteTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "123");
        assertEquals(1,td.findAll(null,t.minusDays(1),t.plusDays(1),true,false).size());
        task2.complete();
        assertEquals(2,td.findAll(null,t.minusDays(1),t.plusDays(1),true,false).size());
        assertEquals(2,td.findAll(null,null,null,false,false).size());
        task1.complete();
        assertEquals(1,td.findAll(null,null,null,false,false).size());
    }

    @Test
    public void findAll4ParamTest1(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "123");
        assertThrows(IllegalArgumentException.class, ()->{
            Map<Task.Field,String> map = new HashMap<>();
            map.put(Task.Field.LOCATION,"someLocation");
            map.put(Task.Field.DESCRIPTION,null);
            td.findAll(map,null,null,null,false);
        });
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"cat");
        assertEquals("cat",td.findAll(map2,null,null,null,false).get(0).getLocation());
        td.remove(100);
        assertEquals(0,td.findAll(map2,null,null,null,false).size());
        map2.put(Task.Field.LOCATION,"sat2");
        assertEquals(1,td.findAll(map2,null,null,null,false).size());
    }

    @Test
    public void findAll4ParamTest2(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "123");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"cat");
        assertEquals(1,td.findAll(map2,null,null,null,true).size());
        assertEquals(0,td.findAll(map2,t,null,null,true).size());
        assertEquals(1,td.findAll(map2,t.minusDays(1),null,null,true).size());
        map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"sat2");
        assertEquals(2,td.findAll(map2,t.minusDays(2),t.plusDays(1),null,false).size());
        assertEquals(3,td.findAll(map2,t.minusDays(2),null,null,false).size());
        assertEquals(1,td.findAll(map2,t.minusDays(2),null,null,true).size());
    }

    @Test
    public void findAll4ParamTest3(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "cat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "cat3", "123");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.DESCRIPTION,"123");
        assertEquals(3,td.findAll(map2,null,null,null,false).size());
        task3.setDescription("1");
        assertEquals(2,td.findAll(map2,null,null,null,false).size());
    }

    @Test
    public void findAll4AndSearchTest1(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "123");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"cat");

        assertEquals(0,td.findAll(map2,t2.minusDays(1),t2.plusDays(1),false,true).size());
        assertEquals(2,td.findAll(map2,t2.minusDays(1),t2.plusDays(1),null,false).size());
        assertEquals(2,td.findAll(map2,t2.minusDays(1),t2.plusDays(1),true,false).size());
        task3.complete();
        assertEquals(3,td.findAll(map2,t2.minusDays(1),t2.plusDays(1),true,false).size());
    }

    @Test
    public void findAll4ContainTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "cat2", "123");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"cat");
        assertEquals(2,td.findAll(map2,null,null,null,false).size());
        assertEquals(2,td.findAll(map2,null,null,null,true).size());
    }

    @Test
    public void findAll4AndSearchTest2(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "13");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.DESCRIPTION,"2");
        assertEquals(2,td.findAll(map2,null,null,null,true).size());
        assertEquals(2,td.findAll(map2,null,null,null,false).size());
        map2.put(Task.Field.LOCATION,"at2");
        assertEquals(1,td.findAll(map2,null,null,null,false).size());
        assertEquals(0,td.findAll(map2,t2,t2.plusMonths(2),null,true).size());
        assertEquals(1,td.findAll(map2,t2.minusDays(1),t2.plusMonths(2),null,true).size());
    }

    @Test
    public void findAll4AndSearchTest3(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "13");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"catt");
        map2.put(Task.Field.DESCRIPTION,"1233");
        assertEquals(0,td.findAll(map2,null,null,null,true).size());
        assertEquals(0,td.findAll(map2,null,null,null,true).size());
    }

    @Test
    public void findAll4AndSearchTest4(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "13");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"at");
        map2.put(Task.Field.DESCRIPTION,"1");
        task3.complete();
        assertEquals(task2,td.findAll(map2,t,t2.plusMonths(2),false,true).get(0));
        assertEquals(1,td.findAll(map2,t,t2.plusMonths(2),false,true).size());
        task2.complete();
        assertEquals(0,td.findAll(map2,t,t2.plusMonths(2),false,true).size());
        assertEquals(2,td.findAll(map2,t,t2.plusMonths(2),true,true).size());
    }

    @Test
    public void clearTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "13");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"at");
        map2.put(Task.Field.DESCRIPTION,"1");
        td.clear();
        assertFalse(td.remove(100));
        assertEquals(0,td.findAll().size());

    }
    @Test
    public void clearTest2(){
        td.add(1, t, "cat town", "saying hi to the cats");

        td.clear();
        assertThrows(IllegalStateException.class,()->{
            td.add(null, t2, "dog town", "saying hi to the dogs");
        });

    }

    @Test
    public void findAll4AndTestExtra(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "13");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,"at");
        map2.put(Task.Field.DESCRIPTION,"1");
        assertEquals(3,td.findAll(map2,null,null,false,true).size());
        assertEquals(3,td.findAll(null,null,null,false,true).size());
    }

    @Test
    public void findAll4AndTestExtra2() {
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        Task task3 = td.add(300, t2.plusMonths(1), "dat3", "13");
        Map<Task.Field,String> map2 = new HashMap<>();
        map2.put(Task.Field.LOCATION,null);
        assertThrows(IllegalArgumentException.class, ()->{
            td.findAll(map2,null,null,null,false);
        });
        assertEquals(2,td.findAll(null,t.minusDays(1),t2.plusMonths(1),false,true).size());
    }

    @Test
    public void findOneExtraTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        assertEquals(task2,td.findOne(200));
    }

    @Test
    public void toSameFromTest(){
        Task task1 = td.add(100, t, "cat", "123");
        Task task2 = td.add(200, t2, "sat2", "123");
        assertThrows(IllegalArgumentException.class,()->{
            td.findAll(t,t,false);
        });

        assertThrows(IllegalArgumentException.class,()->{
            td.findAll(null,t,t,null,false);
        });
    }

    @Test
    public void addTestExtra(){
        Task task1 = td.add(null, t, "cat", "123");
        Task task2 = td.add(null, t2, "sat2", "123");
        Task task3 = td.add(123, t, "fish city", "apologising to the fish");
        assertEquals("fish city",td.findOne(123).getLocation());
        assertThrows(IllegalStateException.class, ()->{
            td.add(null, t, "sydney", "more fishing");
        });
    }

}

