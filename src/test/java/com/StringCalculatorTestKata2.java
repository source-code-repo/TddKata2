package com;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.mockito.Mockito.*;

/**
 * I'm trying out Roy Osherove's suggested naming standard for tests
 * in this class.
 * https://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html
 */
@RunWith(MockitoJUnitRunner.class)
public class StringCalculatorTestKata2 {
    private static final String ERROR = "Exception in logging";

    @InjectMocks private StringCalculator sc = new StringCalculator();
    @Mock private Logger logMock;
    @Mock private WebService ws;

    @InjectMocks private MainClass mc = new MainClass();
    @Mock private Scanner s;
    @Mock private PrintStream ps;

    @Before
    public void setup() {
        MainClass.setScanner(s);
        // This stops tests from hanging  because MainClass waits for input
        when(s.nextLine()).thenReturn("1");
    }

    @After
    public void resetSysOut() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    @Test
    public void calcSumResultLogged() {
        sc.add("1,2,3");
        verify(logMock).info("6");
    }

    @Test
    public void calcLoggingExceptionWebServiceNotified() {
        doThrow(new RuntimeException(ERROR)).when(logMock).info("6");
        sc.add("1,2,3");
        verify(ws).errorInLogging(ERROR);
    }

    @Test
    public void calcResultOutputToScreen() {
        System.setOut(ps);
        sc.add("1,2,3");
        verify(ps).println(6);
    }

    @Test
    public void calcCalledFromCliResultOnScreen() {
        System.setOut(ps);
        mc.main(new String[] {"1,2,3"});
        verify(ps).println("The result is 6");
    }

    @Test
    public void calcCalledFromCliPrintedResultPromptsForNextInput() {
        System.setOut(ps);
        mc.main(new String[] {"1,2,3"});
        verify(ps).println("another input please: ");
    }

    @Test
    public void calcCalledFromCliAcceptsInputShowsResult() {
        System.setOut(ps);
        when(s.nextLine()).thenReturn("5,6,7");
        mc.main(new String[] {"1,2,3"});
        verify(ps).println("The result is 18");
    }
}
