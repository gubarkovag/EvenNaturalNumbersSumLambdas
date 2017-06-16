package stc06.gubarkov;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by admin on 16.06.2017.
 */
public class NumbersContainer implements Runnable {
    private ResultCounter resultCounter;
    private String fileName;

    public NumbersContainer() {
    }

    public NumbersContainer(ResultCounter resultCounter, String fileName) {
        this.resultCounter = resultCounter;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            readLinesFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readLinesFromFile() throws IOException {
        String fileNameLine;
        try (BufferedReader bin =
                     new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            int curStrNum = 0;

            while ((fileNameLine = bin.readLine()) != null) {
                curStrNum++;
                resultCounter.getLock().lock();
                try {
                    if (resultCounter.isThreadsToBeInterrupted()) {
                        System.out.println("Выполнение потока чтения " +
                                " данных из файла " + fileName + " прервано");
                        break;
                    }
                    resultCounter.getCondVar().await();
                    int sum = countCurStringNumbersSum(fileNameLine);
                    if (sum == -1) {
                        resultCounter.setThreadsToBeInterrupted(true);
                        System.out.println("Некорректное значение в строке номер " +
                                curStrNum + " в файле " + fileName);
                    } else {
                        resultCounter.addToCurStringsSums(sum);
                        //resultCounter.addToResult(sum);
                    }
                } finally {
                    resultCounter.getLock().unlock();
                }
            }
        }/* catch (IOException ex) {
            ex.printStackTrace();
        }*/ catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public int countCurStringNumbersSum(String string) {
        String[] stringNumbers = string.split(" ");
        int curStringNumbersSum = 0;
        for (String stringNumber : stringNumbers) {
            if (!isReadNumberCorrect(stringNumber)) {
                return -1;
            } else {
                int number = Integer.parseInt(stringNumber);
                if (isEvenNaturalNumber(number)) {
                    curStringNumbersSum += number;
                }
            }
        }
        return curStringNumbersSum;
    }

    private boolean isReadNumberCorrect(String stringNumber) {
        return stringNumber.matches("-?\\d+");
    }

    private boolean isEvenNaturalNumber(int number) {
        return number % 2 == 0 && number > 0;
    }
}
