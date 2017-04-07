package rxjava2.x.examples;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class FluentPattern {
    private static void call1(){
        Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Done";
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(System.out::println, Throwable::printStackTrace);

        try {
            Thread.sleep(2000); // <--- wait for the flow to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void call2(){
        Flowable<String> source = Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Done";
        });

        Flowable<String> runBackground = source.subscribeOn(Schedulers.io());

        Flowable<String> showForeground = runBackground.observeOn(Schedulers.single());

        showForeground.subscribe(System.out::println, Throwable::printStackTrace);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void call3(){
        Flowable.range(1, 10)
                .observeOn(Schedulers.computation())
                .map(v -> v * v)
                .blockingSubscribe(System.out::println);
    }

    private static void call4(){
        Flowable.range(1, 10)
                .flatMap(v ->
                                 Flowable.just(v)
                                         .subscribeOn(Schedulers.computation())
                                         .map(w -> w * w)
                )
                .blockingSubscribe(System.out::println);
    }

    private static void call5() {
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v -> v * v)
                .sequential()
                .blockingSubscribe(System.out::println);
    }

    public static void main(String[] args) {
        call5();
    }
}
