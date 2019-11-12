package com.phodal.pepper.powermock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemClassUser.class})
public class SystemClassUserTest {
    @Test
    public void assertThatMockingOfNonFinalSystemClassesWorks() throws Exception {
        mockStatic(URLEncoder.class);

        when(URLEncoder.encode("string", "utf8")).thenReturn("something");

        assertEquals("something", new SystemClassUser().performEncode());

    }

    @Test
    public void assertThatMockingOfTheRuntimeSystemClassWorks() throws Exception {
        mockStatic(Runtime.class);

        Runtime runtimeMock = mock(Runtime.class);
        Process processMock = mock(Process.class);

        when(Runtime.getRuntime()).thenReturn(runtimeMock);
        when(runtimeMock.exec("command")).thenReturn(processMock);

        assertSame(processMock, new SystemClassUser().executeCommand());
    }

    @Test
    public void assertThatMockingOfFinalSystemClassesWorks() throws Exception {
        mockStatic(System.class);

        when(System.getProperty("property")).thenReturn("my property");

        assertEquals("my property", new SystemClassUser().getSystemProperty());
    }

    @Test
    public void assertThatPartialMockingOfFinalSystemClassesWorks() throws Exception {
        spy(System.class);

        doReturn(2L).when(System.class);
        System.nanoTime();

        new SystemClassUser().doMoreComplicatedStuff();

        assertEquals("2", System.getProperty("nanoTime"));
    }

    @Test
    public void assertThatMockingOfCollectionsWork() throws Exception {
        List<?> list = new LinkedList<Object>();
        mockStatic(Collections.class);

        Collections.shuffle(list);

        new SystemClassUser().shuffleCollection(list);

        verifyStatic(Collections.class, times(2));
        Collections.shuffle(list);
    }

    @Test
    public void assertThatPartialMockingOfFinalSystemClassesWorksForNonVoidMethods() throws Exception {
        spy(System.class);

        doReturn("my property").when(System.class);
        System.getProperty("property");

        final SystemClassUser systemClassUser = new SystemClassUser();
        systemClassUser.copyProperty("to", "property");

        assertEquals("my property", System.getProperty("to"));
    }

    @Test
    public void assertThatMockingStringWorks() throws Exception {
        mockStatic(String.class);
        final String string = "string";
        final String args = "args";
        final String returnValue = "returnValue";

        when(String.format(string, args)).thenReturn(returnValue);

        final SystemClassUser systemClassUser = new SystemClassUser();
        assertEquals(systemClassUser.format(string, args), returnValue);
    }

    @Test
    public void mockingStaticVoidMethodWorks() throws Exception {
        mockStatic(Thread.class);
        doNothing().when(Thread.class);
        Thread.sleep(anyLong());

        long startTime = System.currentTimeMillis();
        final SystemClassUser systemClassUser = new SystemClassUser();
        systemClassUser.threadSleep();
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 5000);
    }

    @Test
    public void mockingURLWorks() throws Exception {
        URL url = mock(URL.class);
        URLConnection urlConnectionMock = mock(URLConnection.class);

        when(url.openConnection()).thenReturn(urlConnectionMock);

        URLConnection openConnection = url.openConnection();

        assertSame(openConnection, urlConnectionMock);
    }

    @Test
    public void mockingUUIDWorks() throws Exception {
        // given
        final UUID mock = mock(UUID.class);
        mockStatic(UUID.class);
        given(UUID.randomUUID()).willReturn(mock);
        given(mock.toString()).willCallRealMethod();

        // when
        String actual = new SystemClassUser().generatePerishableToken();

        // then
        assertEquals("00000000000000000000000000000000", actual);
    }

    @Test
    public void mockingNewURLWorks() throws Exception {
        // Given
        final URL url = mock(URL.class);
        whenNew(URL.class).withArguments("some_url").thenReturn(url);

        // When
        final URL actual = new SystemClassUser().newURL("some_url");

        // Then
        assertSame(url, actual);
    }

    @Test
    public void mockingStringBuilder() throws Exception {
        // Given
        final StringBuilder mock = mock(StringBuilder.class);
        whenNew(StringBuilder.class).withNoArguments().thenReturn(mock);
        when(mock.toString()).thenReturn("My toString");

        // When
        final StringBuilder actualStringBuilder = new SystemClassUser().newStringBuilder();
        final String actualToString = actualStringBuilder.toString();


        // Then
        assertSame(mock, actualStringBuilder);
        assertEquals("My toString", actualToString);
    }

    @Test(expected = IllegalStateException.class)
    public void triggerMockedCallFromInterfaceTypeInsteadOfConcreteType() throws Exception {
        StringBuilder builder = mock(StringBuilder.class);
        when(builder.length()).then(new Answer<StringBuilder>() {
            public StringBuilder answer(InvocationOnMock invocation) throws Throwable {
                throw new IllegalStateException("Can't really happen");
            }
        });
        new SystemClassUser().lengthOf(builder);
    }
}