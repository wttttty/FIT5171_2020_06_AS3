package rockets.mining;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.dataaccess.neo4j.Neo4jDAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RocketMinerUnitTest {
    Logger logger = LoggerFactory.getLogger(RocketMinerUnitTest.class);

    private DAO dao;
    private RocketMiner miner;
    private List<Rocket> rockets;
    private List<LaunchServiceProvider> lsps;
    private List<Launch> launches;

    @BeforeEach
    public void setUp() {
        dao = mock(Neo4jDAO.class);
        miner = new RocketMiner(dao);
        rockets = Lists.newArrayList();

        lsps = Arrays.asList(
                new LaunchServiceProvider("ULA", 1990, "USA"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("ESA", 1975, "Europe ")
        );

        // index of lsp of each rocket
        int[] lspIndex = new int[]{0, 0, 0, 1, 1};
        // 5 rockets
        for (int i = 0; i < 5; i++) {
            rockets.add(new Rocket("rocket_" + i, "ccc","USA", lsps.get(lspIndex[i])));
        }
        // month of each launch
        int[] months = new int[]{1, 6, 4, 3, 4, 11, 6, 5, 12, 5};

        // index of rocket of each launch
        int[] rocketIndex = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};

        // outcome of each launch
        Launch.LaunchOutcome[] outcome = new Launch.LaunchOutcome[]{
                Launch.LaunchOutcome.SUCCESSFUL, Launch.LaunchOutcome.SUCCESSFUL, Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.SUCCESSFUL,
                Launch.LaunchOutcome.SUCCESSFUL, Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.SUCCESSFUL
        };

        // 10 launches
        launches = IntStream.range(0, 10).mapToObj(i -> {
            logger.info("create " + i + " launch in month: " + months[i]);
            Launch l = new Launch();
            l.setLaunchDate(LocalDate.of(2017, months[i], 1));
            l.setLaunchVehicle(rockets.get(rocketIndex[i]));
            l.setLaunchSite("VAFB");
            l.setOrbit("LEO");
            l.setLaunchOutcome(outcome[i]);
            l.setLaunchServiceProvider(lsps.get(i % 3));
            spy(l);
            return l;
        }).collect(Collectors.toList());
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnTopMostRecentLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        sortedLaunches.sort((a, b) -> -a.getLaunchDate().compareTo(b.getLaunchDate()));
        List<Launch> loadedLaunches = miner.mostRecentLaunches(k);
        assertEquals(k, loadedLaunches.size());
        assertEquals(sortedLaunches.subList(0, k), loadedLaunches);
    }

    @DisplayName("should return most Recent successful launches")
    @Test
    public void shouldReturnMostRecentSuccessfulLaunches(){
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> alllaunches = new ArrayList<>(launches);

        List<Launch> successfulLaunches = alllaunches.stream().
                filter(launch -> launch.getLaunchOutcome() == Launch.LaunchOutcome.SUCCESSFUL).
                collect(Collectors.toList());
        List<Launch> loadedLaunches = miner.mostRecentSuccessfulLaunches();

        assertEquals(loadedLaunches, successfulLaunches);
    }

    @DisplayName("should return most successfully launched rockets")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnMostLaunchedRockets(int k){
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> alllaunches = new ArrayList<>(launches);

        List<Launch> successfullaunches = new ArrayList<>(alllaunches);
        alllaunches.forEach(launch -> {
            if(launch.getLaunchOutcome() != Launch.LaunchOutcome.SUCCESSFUL){
                successfullaunches.remove(launch);
            }
        });
        Map<Rocket, List<Launch>> rockets = successfullaunches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchVehicle));
        Map<Rocket, Integer> numOfRockets = new HashMap<Rocket, Integer>();
        rockets.forEach((key, value) -> {
            numOfRockets.put(key, value.size());
        });

        List<Map.Entry<Rocket, Integer>> numOfRocketsList = new ArrayList<Map.Entry<Rocket, Integer>>(numOfRockets.entrySet());
        List<Rocket> mostLaunchedRockets = numOfRocketsList.stream().
                sorted(new Comparator<Map.Entry<Rocket, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Rocket, Integer> o1, Map.Entry<Rocket, Integer> o2) {
                        return o1.getValue()-o2.getValue();
                    }
                }).map(Map.Entry::getKey).limit(k).collect(Collectors.toList());
        List<Rocket> loadedRockets = miner.mostLaunchedRockets(k);

        assertEquals(k, loadedRockets.size());
        assertEquals(mostLaunchedRockets, loadedRockets);
    }

    @DisplayName("should return most reliable launch service providers")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnMostReliableLaunchServiceProviders(int k){
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        List<Launch> alllaunches = new ArrayList<>(launches);
        List<Launch> successfullaunches = new ArrayList<>(alllaunches);
        alllaunches.forEach(launch -> {
            if(launch.getLaunchOutcome() != Launch.LaunchOutcome.SUCCESSFUL){
                successfullaunches.remove(launch);
            }
        });

        // classify launches by providers
        Map<LaunchServiceProvider, List<Launch>> providers = successfullaunches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchServiceProvider));
        // get number of successful launches for each provider
        Map<LaunchServiceProvider, Integer> numOfSuccess = new HashMap<LaunchServiceProvider, Integer>();
        providers.forEach((key, value) -> {
            numOfSuccess.put(key, value.size());
        });
        //get number of total launches for each provider
        Map<LaunchServiceProvider, Integer> numOfTotal = new HashMap<LaunchServiceProvider, Integer>();
        launches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchServiceProvider)).
                forEach((key, value) -> {
                    numOfTotal.put(key, value.size());
                });
        // get percentage of successful launches for each provider
        Map<LaunchServiceProvider, Integer> percentage = new HashMap<LaunchServiceProvider, Integer>();
        numOfTotal.forEach((key, value) -> {
            percentage.put(key, numOfSuccess.get(key) / value);
        });
        // sort by percentage
        List<Map.Entry<LaunchServiceProvider, Integer>> providerList = new ArrayList<Map.Entry<LaunchServiceProvider, Integer>>(percentage.entrySet());
        List<LaunchServiceProvider> mostReliableLaunchServiceProviders = providerList.stream().
                sorted(new Comparator<Map.Entry<LaunchServiceProvider, Integer>>() {
                    @Override
                    public int compare(Map.Entry<LaunchServiceProvider, Integer> o1, Map.Entry<LaunchServiceProvider, Integer> o2) {
                        return o1.getValue()-o2.getValue();
                    }
                }).map(Map.Entry::getKey).limit(k).collect(Collectors.toList());
        List<LaunchServiceProvider> loadedLaunchServiceProviders = miner.mostReliableLaunchServiceProviders(k);

        assertEquals(k, loadedLaunchServiceProviders.size());
        assertEquals(mostReliableLaunchServiceProviders, loadedLaunchServiceProviders);
    }

    @DisplayName("should return the num of successful launches for rockets")
    @Test
    public void shouldNumOfSuccessfulLunchesForRockets(){
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> alllaunches = new ArrayList<>(launches);

        List<Launch> successfulLaunches = alllaunches.stream().
                filter(launch -> launch.getLaunchOutcome() == Launch.LaunchOutcome.SUCCESSFUL).
                collect(Collectors.toList());
        Map<Rocket, List<Launch>> rockets = successfulLaunches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchVehicle));
        Map<Rocket, Integer> numOfRockets = new HashMap<Rocket, Integer>();
        rockets.forEach((key, value) -> {
            numOfRockets.put(key, value.size());
        });
        Map<Rocket, Integer> numOfSuccessfulLunchesForRockets = miner.numOfSuccessfulLunchesForRockets();

        assertEquals(numOfRockets, numOfSuccessfulLunchesForRockets);
    }

    @DisplayName("should return the percentage of successful launches for providers")
    @Test
    public void shouldPercentageOfSuccessfulLaunchesForProviders(){
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        List<Launch> alllaunches = new ArrayList<>(launches);
        List<Launch> successfullaunches = new ArrayList<>(alllaunches);
        alllaunches.forEach(launch -> {
            if(launch.getLaunchOutcome() != Launch.LaunchOutcome.SUCCESSFUL){
                successfullaunches.remove(launch);
            }
        });

        // classify launches by providers
        Map<LaunchServiceProvider, List<Launch>> providers = successfullaunches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchServiceProvider));
        // get number of successful launches for each provider
        Map<LaunchServiceProvider, Integer> numOfSuccess = new HashMap<LaunchServiceProvider, Integer>();
        providers.forEach((key, value) -> {
            numOfSuccess.put(key, value.size());
        });
        //get number of total launches for each provider
        Map<LaunchServiceProvider, Integer> numOfTotal = new HashMap<LaunchServiceProvider, Integer>();
        launches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchServiceProvider)).
                forEach((key, value) -> {
                    numOfTotal.put(key, value.size());
                });
        // get percentage of successful launches for each provider
        Map<LaunchServiceProvider, Integer> percentage = new HashMap<LaunchServiceProvider, Integer>();
        numOfTotal.forEach((key, value) -> {
            percentage.put(key, numOfSuccess.get(key) / value);
        });

        Map<LaunchServiceProvider, Integer> loadedpercentage = miner.percentageOfSuccessfulLaunchesForProviders();
        assertEquals(percentage, loadedpercentage);
    }

    @DisplayName("should return highest revenue launch service prodivers")
    @ParameterizedTest
    @CsvSource({
            "1, 2017",
            "2, 2017",
            "3, 2017"
    })
    public void shouldHighestRevenueLaunchServiceProviders(int k, int year) {
//        when(dao.loadAll(Rocket.class)).thenReturn(rockets);
        when(dao.loadAll(LaunchServiceProvider.class)).thenReturn(lsps);
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        // add price to each launch
        String[] prices = new String[]{"12.11", "40.3", "50.32", "7.8", "123.32", "5.4", "345.43", "567.5", "45443.3", "123.3"};
        for (int i = 0; i < 10; i++) {
            launches.get(i).setPrice(new BigDecimal(prices[i]));
        }
        // init record map and do the record
        HashMap<LaunchServiceProvider, BigDecimal> record = new HashMap<>();
        lsps.forEach(l -> record.put(l, new BigDecimal("0.0")));
        launches.stream()
                .filter(l -> l.getLaunchDate().getYear() == year)
                .forEach(launch -> {
                    BigDecimal currentPrice = record.get(launch.getLaunchVehicle().getManufacturer());
                    record.put(launch.getLaunchVehicle().getManufacturer(), currentPrice.add(launch.getPrice()));
                });

        // sort
        List<LaunchServiceProvider> expectResult = record.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<LaunchServiceProvider> got = miner.highestRevenueLaunchServiceProviders(k, year);
        assertEquals(k, got.size());
        assertEquals(got.subList(0, k), got);
    }

    @DisplayName("should return the dominant country")
    @ParameterizedTest
    @ValueSource(strings = {"LEO", "GTO", "Other"})
    public void shouldReturnDominantCountry(String orbit) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        String[] devices = new String[]{"device01", "device02", "device03"};
        int i = 1;
        for(Launch launch : launches) {
            String[] devices1 = java.util.Arrays.copyOf(devices, i++ % 4);
            Set<String> payload = new HashSet<>(Arrays.asList(devices1));
            launch.setPayload(payload);
        }

        // count the payload of each launch (group by country)
        HashMap<String, Integer> count = new HashMap<>();
        count.put("USA", 0);
        count.put("Europe ", 0);
        for(Launch launch : launches) {
            if(launch.getOrbit().equals(orbit)) {
                LaunchServiceProvider lsp = launch.getLaunchServiceProvider();
                if(lsp.getCountry() == "USA") {
                    int cur = count.get("USA");
                    count.put(lsp.getCountry(), cur + launch.getPayload().size());
                }
                if(lsp.getCountry() == "Europe ") {
                    int cur = count.get("Europe ");
                    count.put(lsp.getCountry(), cur + launch.getPayload().size());
                }
            }
        }
        // find the country who send the most payload to the orbit
        //sort
        Map<String, Integer> sorted = count
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        String expectedDominantCountry = "";
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            expectedDominantCountry = entry.getKey();
            if (expectedDominantCountry != null) {
                break;
            }
        }

        String dominantCountry = miner.dominantCountry(orbit);
        assertEquals(expectedDominantCountry, dominantCountry);
    }

    @DisplayName("should return the most expensive launches")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnTopMostExpensiveLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);

        //initialize the attributes "price" for launches
        String[] prices = new String[]{"50.01", "80.25", "44.07", "66.12", "420.14", "54.21", "3500.12", "453.01", "751.00", "142.78"};
        int i = 0;
        for(Launch launch : launches) {
            launch.setPrice(new BigDecimal(prices[i++]));
        }

        //sort
        sortedLaunches.sort((a, b) -> -a.getPrice().compareTo(b.getPrice()));

        List<Launch> loadedLaunches = miner.mostExpensiveLaunches(k);
        assertEquals(k, loadedLaunches.size());
        assertEquals(sortedLaunches.subList(0, k), loadedLaunches);
    }
}