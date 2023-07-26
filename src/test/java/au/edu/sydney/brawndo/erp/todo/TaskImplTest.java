package au.edu.sydney.brawndo.erp.todo;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TaskImplTest {
    public LocalDateTime t;
    public LocalDateTime t2;
    public TaskImpl rightNowTask;
    public TaskImpl secondTask;
    public LocalDateTime localDateTime3;
    @BeforeEach
    public void init(){
        t = LocalDateTime.now();
        localDateTime3 = LocalDateTime.of(
                2021, 4, 24, 14, 33, 48);
        t2 = t.plusYears(1)
                .plusMonths(1)
                .plusWeeks(1)
                .plusDays(1);
        rightNowTask = new TaskImpl(1,t,"location1","first");
        secondTask = new TaskImpl(2,t2,"location2","second");
    }

    @Test
    public void getIdTest(){
        assertEquals(1,rightNowTask.getID());
    }

    @Test
    public void getDateTimeTest(){
        assertEquals(t,rightNowTask.getDateTime());
        assertEquals(t2,secondTask.getDateTime());

    }

    @Test
    public void getLocationTest(){
        assertEquals("location1",rightNowTask.getLocation());
        assertEquals("location2",secondTask.getLocation());

    }

    @Test
    public void getDescriptionTest(){
        TaskImpl task = new TaskImpl(3,t,"exampleLocation",null);
        assertNull(task.getDescription());
        assertEquals("first",rightNowTask.getDescription());
    }

    @Test
    public void isCompleteTest(){
        assertFalse(rightNowTask.isCompleted());
    }

    @Test
    public void completeTest(){
        rightNowTask.complete();
        assertTrue(rightNowTask.isCompleted());
        assertThrows(IllegalStateException.class, ()->{rightNowTask.complete();});

    }

    @Test
    public void setLocationTest(){
        assertThrows(IllegalArgumentException.class, ()->{
            rightNowTask.setLocation(null);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            rightNowTask.setLocation("");
        });

    }
    @Test
    public void setLocationExceedLimit(){
        char[] array = new char[257];
        Arrays.fill(array, 'e');
        String example = new String(array);

        assertThrows(IllegalArgumentException.class, ()->{
            rightNowTask.setLocation(example);
        });
    }

    @Test
    public void setLocationCorrect(){
        rightNowTask.setLocation("locationExample");
        assertEquals("locationExample",rightNowTask.getLocation());
    }

    @Test
    public void setDateTest(){
        assertThrows(IllegalArgumentException.class, ()->{
            rightNowTask.setDateTime(null);
        });

    }

    @Test
    public void setDateCorrect(){
        LocalDateTime time = LocalDateTime.of(
                2021, 4, 24, 14, 33, 48);
        rightNowTask.setDateTime(localDateTime3);
        assertEquals(time,rightNowTask.getDateTime());
    }

    @Test
    public void setDescriptionTest(){
        rightNowTask.setDescription(null);
        assertNull(rightNowTask.getDescription());
        rightNowTask.setDescription("23");
        assertEquals("23",rightNowTask.getDescription());
    }

    @Test
    public void getFieldTest() {
        Task.Field test = null;
        assertThrows(IllegalArgumentException.class, () -> {
            rightNowTask.getField(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            rightNowTask.getField(test);
        });
    }
    @Test
    public void getFieldDescriptionTest(){
        Task.Field test = Task.Field.DESCRIPTION;
        assertEquals("first",rightNowTask.getField(test));
    }

    @Test
    public void getFieldLocationTest(){
        Task.Field test1 = Task.Field.LOCATION;
        assertEquals("location1",rightNowTask.getField(test1));
    }
}