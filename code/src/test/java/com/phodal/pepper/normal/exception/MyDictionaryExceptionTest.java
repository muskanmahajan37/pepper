package com.phodal.pepper.normal.exception;

import org.junit.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class MyDictionaryExceptionTest {
    @Test(expected = NullPointerException.class)
    public void whenConfigNonVoidReturnMethodToThrowEx_thenExIsThrown() {
        MyDictionary dictMock = mock(MyDictionary.class);
        when(dictMock.getMeaning(anyString()))
                .thenThrow(NullPointerException.class);

        dictMock.getMeaning("word");
    }

    @Test(expected = IllegalStateException.class)
    public void whenConfigVoidReturnMethodToThrowEx_thenExIsThrown() {
        MyDictionary dictMock = mock(MyDictionary.class);
        doThrow(IllegalStateException.class)
                .when(dictMock)
                .add(anyString(), anyString());

        dictMock.add("word", "meaning");
    }
}
