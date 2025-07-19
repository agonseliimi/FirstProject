import java.util.*;
import java.util.concurrent.*;

class Provimi {
    volatile boolean aktiv = true;
}

class Profesor {
    Provimi provimi;

    Profesor(Provimi provimi) {
        this.provimi = provimi;
    }

    void start_Provimi() {
        System.out.println("Provimi ka filluar");
    }

    void stop_Provimi() {
        provimi.aktiv = false;
        System.out.println("provimi ka mbaruar");
    }

    static class Student implements Runnable {
        int id;
        int piket = 0;
        long koha_Fillimit;
        long koha_Mbarimit;
        Provimi provimi;
        Random random = new Random();

        Student(int id, Provimi provimi) {
            this.id = id;
            this.provimi = provimi;
        }

        public void run() {
            try {
                koha_Fillimit = System.currentTimeMillis();
                for (int i = 1; i <= 10; i++) {
                    if (!provimi.aktiv) break;

                    Thread.sleep(500 + random.nextInt(1000));

                    boolean sakt = random.nextBoolean();
                    if (sakt) piket += 10;
                }
                koha_Mbarimit = System.currentTimeMillis();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int get_Nota() {
            if (piket < 50) return 5;
            if (piket < 60) return 6;
            if (piket < 70) return 7;
            if (piket < 80) return 8;
            if (piket < 90) return 9;
            return 10;
        }

        long get_Koha() {
            return (koha_Mbarimit - koha_Fillimit) / 1000;
        }
    }

    public class FirstProject {
        public static void main(String[] args) throws InterruptedException {
            Provimi provimi = new Provimi();
            Profesor profesor = new Profesor(provimi);
            ExecutorService exe = Executors.newFixedThreadPool(20);

            List<Student> studentet = new ArrayList<>();

            for (int i = 1; i <= 20; i++) {
                Student s = new Student(1000 + i, provimi);
                studentet.add(s);
                exe.execute(s);
            }

            profesor.start_Provimi();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            profesor.stop_Provimi();

            exe.shutdown();
            exe.awaitTermination(1, TimeUnit.MINUTES);

            System.out.println("Rezultatet: ");
            for (Student s : studentet) {
                System.out.println("ID: " + s.id + " \nPikt: " + s.piket + "\nNota: " + s.get_Nota() +
                        "\nKoha: " + s.get_Koha() + " sek");
            }
        }
    }
}
