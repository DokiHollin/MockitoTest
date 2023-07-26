package au.edu.sydney.brawndo.erp.todo;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ToDoFactoryTest {
    @Test
    public void makeToDoListTest(){
        ToDoFactory tf = Mockito.mock(ToDoFactory.class);
        ToDoListImpl mockTest = Mockito.mock(ToDoListImpl.class);
        when(tf.makeToDoList()).thenReturn(mockTest);
        assertNotNull(tf.makeToDoList());
    }

    @Test
    public void makeToDoListTest2(){
        ToDoFactory tfT = new ToDoFactory();
        ToDoList tdls = tfT.makeToDoList();
        assertNotNull(tdls);
    }
}