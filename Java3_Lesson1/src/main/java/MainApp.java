import java.util.ArrayList;
import java.util.Arrays;



public class MainApp {
    public static void main(String[] args) {
        Integer[] arr = {1,2,3,4,5,6};
        System.out.println(Arrays.toString(arr));
        arrayChangeTwoElements(arr,0,3);
        System.out.println(Arrays.toString(arr));

        FruitBox<Apple> appleBox =new FruitBox<>(new Apple(),new Apple());
        FruitBox<Apple> appleBox1 =new FruitBox<>(new Apple(),new Apple(), new Apple());

        FruitBox<Orange> orangeBox =new FruitBox<>(new Orange(),new Orange());
        FruitBox<Orange> orangeBox1 =new FruitBox<>(new Orange(),new Orange(),new Orange());


        System.out.println(orangeBox.getSize());
        System.out.println(orangeBox1.getSize());
        System.out.println(orangeBox.compare(appleBox1));
        orangeBox.putFruitsToAnotherBox(orangeBox1);
        System.out.println(orangeBox.getSize());
        System.out.println(orangeBox1.getSize());
    }

    //1. Написать метод, который меняет два элемента массива местами.(массив может быть любого ссылочного типа);
    public static <T> void arrayChangeTwoElements(T[] array, int sourceElement, int dstElement){
       if(sourceElement<array.length && dstElement< array.length&&sourceElement>=0&&dstElement>=0){
           T t = array[sourceElement];
           array[sourceElement]=array[dstElement];
           array[dstElement]=t;
       }
    }

    //2. Написать метод, который преобразует массив в ArrayList;
    public static <T> ArrayList<T> arrayToArrayList(T[] array){
        return new ArrayList<>(Arrays.asList(array));
    }
}
