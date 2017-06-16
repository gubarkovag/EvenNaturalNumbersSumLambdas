package stc06.gubarkov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    static int result;

    public static void main(String[] args) {
        List<String> fileNames = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            for (int j = 0; j < 5; j++) {
                String str = "numbers" + i;
                fileNames.add(str + ".txt");
            }
        }

        int fileNamesSize = fileNames.size();
        System.out.println(fileNamesSize);

        ThreadPoolExecutor service = new ThreadPoolExecutor(fileNamesSize, fileNamesSize,
                5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));

        //Thread resultThread = new Thread(ResultCounter::run);
        ResultCounter resultCounter = new ResultCounter(service);
        Thread resultThread = new Thread(resultCounter);

        for (String fileName: fileNames) {
            Runnable runnable = new NumbersContainer(resultCounter, fileName);
            service.execute(runnable);
        }
        service.shutdown();
        resultThread.start();
    }
}
