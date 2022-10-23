import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class RandomInput {

    public static void main(String[] args) {
        writeRandomInput(100);
    }

    public static void writeRandomInput(int size) {
        List<String> queries = new ArrayList<>();
        String[] keyWords = new String[] {"u", "q", "o", "bid", "ask", "best_bid", "best_ask", "size", "buy", "sell"};

        for (int i = 0; i < size; i++) {
            String randomCommand = keyWords[getRandomNumber(0, 2)];
            StringJoiner joiner = new StringJoiner(",");

            if (randomCommand.equals("u")) {
                int randomSize = getRandomNumber(1, 150);
                int randomPrice;
                String randomDestination = keyWords[getRandomNumber(3, 4)];

                if (randomDestination.equals("ask")) {
                    randomPrice = getRandomNumber(40, 80);
                } else {
                    randomPrice = getRandomNumber(10, 39);
                }

                joiner.add(randomCommand);
                joiner.add("" + randomPrice);
                joiner.add("" + randomSize);
                joiner.add(randomDestination);
            } else if (randomCommand.equals("q")) {
                String randomDestination = keyWords[getRandomNumber(5, 7)];

                joiner.add(randomCommand);
                joiner.add(randomDestination);
                if (randomDestination.equals("size")) {
                    int randomPrice = getRandomNumber(10, 80);
                    joiner.add("" + randomPrice);
                }
            } else {
                String randomDestination = keyWords[getRandomNumber(8, 9)];
                int randomSize = getRandomNumber(1, 150);

                joiner.add(randomCommand);
                joiner.add(randomDestination);
                joiner.add("" + randomSize);
            }

            queries.add(joiner.toString());
        }

        try {
            Files.write(Paths.get("input.txt"), queries);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getRandomNumber(int a, int b) {
        return (int)(Math.random() * ((b - a) + 1)) + a;
    }
}
