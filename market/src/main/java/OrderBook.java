import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class OrderBook {
    private final Map<Integer, Integer> ask = new TreeMap<>();
    private final Map<Integer, Integer> bid = new TreeMap<>(Collections.reverseOrder());
    private final List<String> queryPrints = new ArrayList<>();

    public void parse(String path) throws IOException {
        ask.clear();
        bid.clear();
        queryPrints.clear();

        BufferedReader reader = new BufferedReader(new FileReader(path));

        if (path.endsWith(".txt")) {
            String line = reader.readLine();

            while (line != null) {
                try {
                    String[] params = line.split(",");
                    if (params.length == 4 && params[0].equals("u")) {
                        int price = Integer.parseInt(params[1]);
                        int size = Integer.parseInt(params[2]);
                        update(price, size, params[3]);

                    } else if (params.length == 3 && params[0].equals("o")) {
                        int size = Integer.parseInt(params[2]);
                        order(size, params[1]);

                    } else if (params[0].equals("q")) {
                        if (params.length == 2 &&
                                (params[1].equals("best_bid") || params[1].equals("best_ask"))) {
                            print(params[1]);
                        } else if (params.length == 3 && params[1].equals("size")) {
                            int price = Integer.parseInt(params[2]);
                            print(price);
                        }
                    }

                } catch (NumberFormatException ignored) {
                } finally {
                    line = reader.readLine();
                }
            }
        }

        reader.close();
        Files.write(Paths.get("output.txt"), queryPrints);
    }

    private void update(int price, int size, String target) {
        if (target.equals("bid")) {
            bid.put(price, size);
        } else if (target.equals("ask")) {
            ask.put(price, size);
        }
    }

    private void order(int size, String target) {
        Map<String, Map<Integer, Integer>> targets = Map.of("buy", ask, "sell", bid);
        Map<Integer, Integer> map = targets.get(target);
        Set<Integer> keysToDelete = new HashSet<>();

        if (map != null) {
            for (Integer key : map.keySet()) {
                Integer value = map.get(key);
                if (value > size) {
                    map.put(key, value - size);
                    size = 0;
                } else {
                    size -= value;
                    map.put(key, 0);
                    keysToDelete.add(key);
                }

                if (size == 0) {
                    break;
                }
            }

            keysToDelete.forEach(map::remove);
        }
    }

    private void print(String target) {
        Map<String, Map<Integer, Integer>> targets = Map.of("best_ask", ask, "best_bid", bid);
        Map<Integer, Integer> map = targets.get(target);

        if (map != null && map.size() > 0) {
            Integer bestPrice = map.keySet().stream().findFirst().get();
            Integer size = map.get(bestPrice);

            queryPrints.add(bestPrice + "," + size);
        }
    }

    private void print(int price) {
        Integer sizeOfBidPrice = bid.get(price);
        Integer size = sizeOfBidPrice != null ? sizeOfBidPrice : ask.get(price);

        queryPrints.add(String.valueOf(size != null ? size : 0));
    }
}
