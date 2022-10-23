import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        OrderBook book = new OrderBook();

        try {
            book.parse("input.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
