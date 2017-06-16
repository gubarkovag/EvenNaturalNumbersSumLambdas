package stc06.gubarkov;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResultCounter implements Runnable {
    private Lock lock;
    private Condition condVar;
    private Long startTime;
    private ThreadPoolExecutor service;
    private int result;
    private boolean threadsToBeInterrupted;
    private List<Integer> curStringsSums;

    public ResultCounter(ThreadPoolExecutor service) {
        this.service = service;
        this.curStringsSums = new CopyOnWriteArrayList<>();
        lock = new ReentrantLock();
        condVar = lock.newCondition();
    }

    public List<Integer> getCurStringsSums() {
        return curStringsSums;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Condition getCondVar() {
        return condVar;
    }

    public void setCondVar(Condition condVar) {
        this.condVar = condVar;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    void addToCurStringsSums(int curStringNumberSum) {
        curStringsSums.add(curStringNumberSum);
    }

    boolean isThreadsToBeInterrupted() {
        return threadsToBeInterrupted;
    }

    void setThreadsToBeInterrupted(boolean threadsToBeInterrupted) {
        this.threadsToBeInterrupted = threadsToBeInterrupted;
    }

    public void addToResult(int number) {
        this.result += number;
    }

    @Override
    public void run() {
        while (!service.isTerminated()) {
            try {
                Thread.sleep(500);
                lock.lock();
                try {
                    condVar.signalAll();
                    if (!threadsToBeInterrupted) {
                        curStringsSums.stream().forEach(c -> addToResult(c));
                        curStringsSums.clear();
                    }
                    if (!service.isTerminated()) {
                        System.out.println("Текущая сумма всех положительных чётных чисел: " + result);
                    } else {
                        System.out.println("Итоговая сумма всех положительных чётных чисел: " + result);
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
