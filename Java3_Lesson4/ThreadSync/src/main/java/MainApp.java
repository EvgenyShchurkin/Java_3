public class MainApp {
    private static Object obj= new Object();
    private static volatile char currentLetter='A';

    public static void main(String[] args) {
        new Thread(()->{
            try {
                for (int i = 0; i < 5; i++) {
                    synchronized (obj) {
                        while (currentLetter != 'A') {
                            obj.wait();
                        }
                        System.out.print(currentLetter);
                        currentLetter='B';
                        obj.notifyAll();
                    }
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }).start();
        new Thread(()->{
            try {
                for (int i = 0; i < 5; i++) {
                    synchronized (obj) {
                        while (currentLetter != 'B') {
                            obj.wait();
                        }
                        System.out.print(currentLetter);
                        currentLetter = 'C';
                        obj.notifyAll();
                    }
                }
            }catch(InterruptedException e){
                    e.printStackTrace();
            }
        }).start();
        new Thread(()->{
            try {
                for (int i = 0; i < 5; i++) {
                    synchronized (obj) {
                        while (currentLetter != 'C') {
                            obj.wait();
                        }
                        System.out.print(currentLetter);
                        currentLetter='A';
                        obj.notifyAll();
                    }
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }).start();
    }
}
