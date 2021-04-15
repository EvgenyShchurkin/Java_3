import java.util.ArrayList;
import java.util.Arrays;

/*
        a. Есть классы Fruit -> Apple, Orange;(больше фруктов не надо)
        b. Класс Box в который можно складывать фрукты, коробки условно сортируются по типу фрукта, поэтому в одну коробку нельзя сложить и яблоки, и апельсины;
        c. Для хранения фруктов внутри коробки можете использовать ArrayList;
        d. Сделать метод getWeight() который высчитывает вес коробки,
         зная количество фруктов и вес одного фрукта(вес яблока - 1.0f, апельсина - 1.5f, не важно в каких это единицах);
        e. Внутри класса коробка сделать метод compare, который позволяет сравнить текущую коробку с той, которую подадут
        в compare в качестве параметра, true - если их веса равны, false в противном случае(коробки с яблоками мы можем сравнивать с коробками с апельсинами);
        f. Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую коробку(помним про сортировку фруктов,
         нельзя яблоки высыпать в коробку с апельсинами), соответственно в текущей коробке фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;
        g. Не забываем про метод добавления фрукта в коробку.
*/

public class FruitBox <T extends Fruit>{
    ArrayList <T> fruitsAmount;


    public FruitBox(){
        fruitsAmount=new ArrayList<>();
    }
    public FruitBox(T... fruit){
        fruitsAmount = new ArrayList<>(Arrays.asList(fruit));
    }

    public boolean notEmpty(){
        return this.fruitsAmount.size()>0;
    }
    //сделал в тестовых целях
    public int getSize(){
        return fruitsAmount.size();
    }
    public double getBoxWeight(){
        if(notEmpty()) {
            return fruitsAmount.size() * fruitsAmount.get(0).getWeight();
        }
        return 0;
    }
    public FruitBox addFruitToBox(T fruit){
        this.fruitsAmount.add(fruit);
        return this;
    }
    public FruitBox addManyFruitToBox(T... fruit){
        this.fruitsAmount.addAll(new ArrayList<>(Arrays.asList(fruit)));
        return this;
    }
    public FruitBox removeFruit(){
        if(notEmpty()) {
            this.fruitsAmount.remove(this.fruitsAmount.size()-1);
        }
        return this;
    }
    public FruitBox removeAllFruit(){
        if(notEmpty()) {
            this.fruitsAmount.clear();
        }
        return this;
    }

    public boolean compare(FruitBox<?> anotherFruitBox) {
        return Math.abs(this.getBoxWeight() - anotherFruitBox.getBoxWeight()) <= 0.0001f;
    }
    public void putFruitsToAnotherBox(FruitBox<T> anotherFruitBox) { //можно заменить на <? super T>,
        // чтобы была возможность складывать фрукты во фруктовую коробку,
        // но в этом случае метод getBoxWeight нужно переписать
        if(this!=anotherFruitBox) {
            anotherFruitBox.fruitsAmount.addAll(this.fruitsAmount);
            this.fruitsAmount.clear();
            //вначале сделал так:
//            while (this.notEmpty()) {
//                anotherFruitBox.fruitsAmount.add(this.fruitsAmount.remove(this.getSize()-1));
//            }
        }

    }

}
