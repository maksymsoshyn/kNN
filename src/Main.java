import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static List<Observation> trainData = new ArrayList<>();
    private static double succesChecks = 0, obsNum = 0;
    private static int k;
    private static Scanner userIn = new Scanner(System.in);
    private static BufferedReader fileReader;

    Main(String[] args) {
        parseData(args[0], trainData);
        enterK();
        kNN(args[1]);
        System.out.println(succesChecks);
        System.out.println(computeSucces());
        enterArbitraryVektor();
    }


    public static void main(String[] args) {
        new Main(args);
    }

    private static void parseData(String path, List<Observation> dataStore) {
        try {
            fileReader = new BufferedReader(new FileReader(path));
            fileReader.lines().forEach(i -> {
                Observation obs = new Observation();
                obs.setAttributes(getAttributesFrom(i));
                obs.setDeciseAttribute(getDeciseAttributeFrom(i));
                dataStore.add(obs);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void enterK() {
        while (true) {
            System.out.println("Enter k");
            try {
                k = userIn.nextInt();
                if (k <= 0 || k > trainData.size())
                    throw new InputMismatchException();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Wrong k");
            }
        }
    }

    private static void enterArbitraryVektor() {
        userIn.nextLine();
        while (true) {
            System.out.println("Enter your own vektor");
            System.out.println(classifyStringVector(userIn.nextLine(), trainData));
        }
    }

    private static void kNN(String path) {
        try {
            fileReader = new BufferedReader(new FileReader(path));
            fileReader.lines().forEach(i -> {
                if (classifyStringVector(i, trainData).equals(getDeciseAttributeFrom(i)))
                    succesChecks++;
                obsNum++;
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void findPosition(Observation obs, List<Observation> obsStore) {
        Iterator<Observation> obsIter = obsStore.listIterator();
        if (obsIter.hasNext()) {
            for (int i = 0; obsIter.hasNext(); i++) {
                Observation storedObs = obsIter.next();
                if (obs.getDestTo() < storedObs.getDestTo()) {
                    obsStore.add(i, obs);
                    if (obsStore.size() > k)
                        obsStore.remove(obsStore.size() - 1);
                    break;
                } else if (obsStore.size() < k) {
                    obsStore.add(obs);
                    break;
                }
            }
        } else
            obsStore.add(obs);

    }

    private static String classifyStringVector(String line, List<Observation> train) {
        List<Observation> nearestNeighbours = new LinkedList<>();
        Observation unclassified = new Observation();
        unclassified.setAttributes(getAttributesFrom(line));
        train.forEach(j -> {
            j.setDestTo(unclassified);
            findPosition(j, nearestNeighbours);
        });
        return mostFrequent(nearestNeighbours);
    }

    private static double[] getAttributesFrom(String line) {
        String[] data = line.replaceAll(",", "\\.").split("\\s+|\\t+");
        String[] attributes = Arrays.copyOfRange(data, 0, data.length - 1);
        return Arrays.stream(attributes).filter(j -> !j.equals("")).mapToDouble(Double::parseDouble).toArray();
    }

    private static String getDeciseAttributeFrom(String line) {
        String[] parts = line.split("\\s+|\\t+");
        return parts[parts.length - 1];
    }

    private static String mostFrequent(List<Observation> nearestNeighbours) {
        Map<String, Long> frequency = nearestNeighbours.stream().collect(Collectors.groupingBy(Observation::getDeciseAttribute, Collectors.counting()));
        Set<String> classifiers = frequency.keySet();

        Iterator<String> classifIter = classifiers.iterator();
        String currentClass = classifIter.next();
        while (classifIter.hasNext()) {
            String nextClass = classifIter.next();
            if (frequency.get(currentClass) < frequency.get(nextClass))
                currentClass = nextClass;
            else if (frequency.get(currentClass) == frequency.get(nextClass)) {
                double avg1 = computeAvgOf(nearestNeighbours, currentClass, frequency.get(currentClass));
                double avg2 = computeAvgOf(nearestNeighbours, nextClass, frequency.get(nextClass));
                if (avg1 > avg2)
                    currentClass = nextClass;
            }
        }

        return currentClass;
    }


    private static double computeSucces() {
        return (succesChecks / obsNum) * 100.0;
    }

    private static double computeAvgOf(List<Observation> nearestNeighbours, String group, long groupNum) {
        return nearestNeighbours.stream()
                .filter(i -> i.getDeciseAttribute().equals(group))
                .mapToDouble(Observation::getDestTo)
                .sum() / groupNum;
    }
}
