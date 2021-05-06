package com.lesson6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class HomeworkTest {

    @ParameterizedTest
    @MethodSource("arraysProvider")

    void shouldArrayHaveOneAndFour(boolean expect, int [] input){
        Assertions.assertEquals(expect, Homework.hasArrayOneAndFour(input));
    }

    private static Stream<Arguments> arraysProvider(){
        return Stream.of(
                Arguments.arguments(true, new int[]{1,4}),
                Arguments.arguments(false, new int[]{}),
                Arguments.arguments(false, null),
                Arguments.arguments(true, new int[]{1,1,1,1,4,4,4,4}),
                Arguments.arguments(false, new int[]{1,0,1,1,1,1,-4,-4}),
                Arguments.arguments(true, new int[]{0,0,0,0,0,0,0,0,4,1}),
                Arguments.arguments(true, new int[]{0,0,0,0,0,0,0,0,1,4})
        );
    }
    @Test
    void shouldArrayHaveOneAndFour(){
        Assertions.assertEquals(false, Homework.hasArrayOneAndFour(new int[]{2,3,4,5,6}));
    }
    @Test
    void shouldThrowRunTimeException(){
        Assertions.assertThrows(RuntimeException.class, ()->Homework.subArray(new int[]{0,0,0,0,0,0,0,0,1}));
        Assertions.assertThrows(RuntimeException.class, ()->Homework.subArray(null));
    }

    @ParameterizedTest
    @MethodSource("arraysProvider1")
    void testSubArray(int[]expect, int[]input){
        Assertions.assertArrayEquals(expect,Homework.subArray(input));
    }

    private static Stream<Arguments> arraysProvider1(){
        return Stream.of(
                Arguments.arguments(new int[]{}, new int[]{1,4}),
                Arguments.arguments(new int[]{1}, new int[]{0,0,0,0,0,0,0,0,4,1}),
                Arguments.arguments(new int[]{1}, new int[]{1,-4,4,4,1}),
                Arguments.arguments(new int[]{1,0,-4,-4,-4,-4}, new int[]{4,1,0,-4,-4,-4,-4})


        );
    }
}
