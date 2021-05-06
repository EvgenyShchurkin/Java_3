package com.lesson6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Homework {
    public Homework(){
    }
    /*
     Написать метод, который проверяет состав массива из чисел 1 и 4. Если в нем нет хоть одной четверки или единицы,
     то метод вернет false; Написать набор тестов для этого метода (по 3-4 варианта входных данных).
     */

    public static boolean hasArrayOneAndFour(int []arr){
        boolean hasOne=false,hasFour=false;
        if (arr==null){
            return false;
        }
        for (int i = 0; i < arr.length; i++) {
            if(arr[i]==1){
                hasOne=true;
            }
            if(arr[i]==4){
                hasFour=true;
            }
        }
        return hasFour&&hasOne;
    }
/*
2. Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку, иначе в методе
необходимо выбросить RuntimeException. Написать набор тестов для этого метода (по 3-4 варианта входных данных).
 Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
*/

    public static int [] subArray(int[] array){
        if(array==null || array.length==0){
            throw new RuntimeException("Input array should not be null or empty");
        }
        int index=-1;
        for (int i = 0; i < array.length; i++) {
            if(array[i]==4){
                index=i+1;
            }
        }
        if(index == -1){
            throw new RuntimeException("Input array should have at least one element that equals four");
        }
        int[] subArray = new int[array.length-index];
        for (int i = index,j=0; i < array.length && j< subArray.length; i++,j++) {
            subArray[j]=array[i];
        }
        return subArray;
    }


}
