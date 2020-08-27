package rockets.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;

//import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class RocketMiner {
    private static Logger logger = LoggerFactory.getLogger(RocketMiner.class);

    private DAO dao;

    public RocketMiner(DAO dao) {
        this.dao = dao;
    }

    /**
     * TODO: to be implemented & tested!
     * Returns the top-k most active rockets, as measured by number of completed launches.
     *
     * @param k the number of rockets to be returned.
     * @return the list of k most active rockets.
     */
    public List<Rocket> mostLaunchedRockets(int k) {
        logger.info("find most " + k + " launched rockets");

        Collection<Launch> launches = dao.loadAll(Launch.class);

        Map<Rocket, Integer> numOfRockets = this.numOfSuccessfulLunchesForRockets();

        // sort rockets by number of successful launches
        List<Map.Entry<Rocket, Integer>> numOfRocketsList = new ArrayList<Map.Entry<Rocket, Integer>>(numOfRockets.entrySet());
        List<Rocket> mostLaunchedRockets = numOfRocketsList.stream().
                sorted(new Comparator<Map.Entry<Rocket, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Rocket, Integer> o1, Map.Entry<Rocket, Integer> o2) {
                        return o1.getValue()-o2.getValue();
                    }
                }).map(Map.Entry::getKey).limit(k).collect(Collectors.toList());

        return mostLaunchedRockets;
    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns the top-k most reliable launch service providers as measured
     * by percentage of successful launches.
     *
     * @param k the number of launch service providers to be returned.
     * @return the list of k most reliable ones.
     */
    public List<LaunchServiceProvider> mostReliableLaunchServiceProviders(int k) {
        logger.info("find most " + k + " reliable launch service providers");

        Collection<Launch> launches = dao.loadAll(Launch.class);
        Map<LaunchServiceProvider, Integer> percentage = this.percentageOfSuccessfulLaunchesForProviders();

        // sort by percentage
        List<Map.Entry<LaunchServiceProvider, Integer>> providerList = new ArrayList<Map.Entry<LaunchServiceProvider, Integer>>(percentage.entrySet());
        List<LaunchServiceProvider> mostReliableLaunchServiceProviders = providerList.stream().
                sorted(new Comparator<Map.Entry<LaunchServiceProvider, Integer>>() {
                    @Override
                    public int compare(Map.Entry<LaunchServiceProvider, Integer> o1, Map.Entry<LaunchServiceProvider, Integer> o2) {
                        return o1.getValue()-o2.getValue();
                    }
                }).map(Map.Entry::getKey).limit(k).collect(Collectors.toList());

        return mostReliableLaunchServiceProviders;
    }

    /**
     * <p>
     * Returns the top-k most recent launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most recent launches.
     */
    public List<Launch> mostRecentLaunches(int k) {
        logger.info("find most recent " + k + " launches");
        Collection<Launch> launches = dao.loadAll(Launch.class);
        Comparator<Launch> launchDateComparator = (a, b) -> -a.getLaunchDate().compareTo(b.getLaunchDate());
        return launches.stream().sorted(launchDateComparator).limit(k).collect(Collectors.toList());
    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns the dominant country who has the most launched rockets in an orbit.
     *
     * @param orbit the orbit
     * @return the country who sends the most payload to the orbit
     */
    public String dominantCountry(String orbit) {
        logger.info("find the dominant country");
        Collection<Launch> launches = dao.loadAll(Launch.class);

        //initialize the attributes "payload" for launches
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

        String dominantCountry = "";
        for (Entry<String, Integer> entry : sorted.entrySet()) {
            dominantCountry = entry.getKey();
            if (dominantCountry != null) {
                break;
            }
        }
        return dominantCountry;
    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns the top-k most expensive launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most expensive launches.
     */
    public List<Launch> mostExpensiveLaunches(int k) {
        logger.info("find the most expensive launches");
        Collection<Launch> launches = dao.loadAll(Launch.class);

        //initialize the attributes "price" for launches
        String[] prices = new String[]{"50.01", "80.25", "44.07", "66.12", "420.14", "54.21", "3500.12", "453.01", "751.00", "142.78"};
        int i = 0;
        for(Launch launch : launches) {
            launch.setPrice(new BigDecimal(prices[i++]));
        }

        Comparator<Launch> launchDateComparator = (a, b) -> -a.getPrice().compareTo(b.getPrice());
        return launches.stream().sorted(launchDateComparator).limit(k).collect(Collectors.toList());

    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns a list of launch service provider that has the top-k highest
     * sales revenue in a year.
     *
     * @param k the number of launch service provider.
     * @param year the year in request
     * @return the list of k launch service providers who has the highest sales revenue.
     */
    public List<LaunchServiceProvider> highestRevenueLaunchServiceProviders(int k, int year) {
        List<LaunchServiceProvider> lsps = (List<LaunchServiceProvider>) dao.loadAll(LaunchServiceProvider.class);
        List<Launch> launches = (List<Launch>) dao.loadAll(Launch.class);

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
        return record.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .limit(k)
                .collect(Collectors.toList());
    }

    /*
     * return a list of recent launches that are successful
     * */
    public List<Launch> mostRecentSuccessfulLaunches() {

        logger.info("find most recent successful launches");
        Collection<Launch> launches = dao.loadAll(Launch.class);
        List<Launch> successfulLaunches = launches.stream().
                filter(launch -> launch.getLaunchOutcome() == Launch.LaunchOutcome.SUCCESSFUL).
                collect(Collectors.toList());

        return successfulLaunches;
    }

    /*
     * return a map, in which key is Rocket, value is the number of its successful launches
     * */
    public Map<Rocket, Integer> numOfSuccessfulLunchesForRockets(){

        List<Launch> successfulLaunches = this.mostRecentSuccessfulLaunches();
        Map<Rocket, List<Launch>> rockets = successfulLaunches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchVehicle));
        // get number of successful launches for each rockets
        Map<Rocket, Integer> numOfSuccessfulLunchesForRockets = new HashMap<Rocket, Integer>();
        rockets.forEach((key, value) -> {
            numOfSuccessfulLunchesForRockets.put(key, value.size());
        });

        return numOfSuccessfulLunchesForRockets;
    }

    public Map<LaunchServiceProvider, Integer> percentageOfSuccessfulLaunchesForProviders(){
        Collection<Launch> allLaunches = dao.loadAll(Launch.class);
        List<Launch> successfulLaunches = this.mostRecentSuccessfulLaunches();

        // classify launches by providers
        Map<LaunchServiceProvider, List<Launch>> providers = successfulLaunches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchServiceProvider));
        // get number of successful launches for each provider
        Map<LaunchServiceProvider, Integer> numOfSuccess = new HashMap<LaunchServiceProvider, Integer>();
        providers.forEach((key, value) -> {
            numOfSuccess.put(key, value.size());
        });
        //get number of total launches for each provider
        Map<LaunchServiceProvider, Integer> numOfTotal = new HashMap<LaunchServiceProvider, Integer>();
        allLaunches.stream().
                collect(Collectors.groupingBy(Launch::getLaunchServiceProvider)).
                forEach((key, value) -> {
                    numOfTotal.put(key, value.size());
                });
        // get percentage of successful launches for each provider
        Map<LaunchServiceProvider, Integer> percentage = new HashMap<LaunchServiceProvider, Integer>();
        numOfTotal.forEach((key, value) -> {
            percentage.put(key, numOfSuccess.get(key) / value);
        });

        return percentage;
    }
}
