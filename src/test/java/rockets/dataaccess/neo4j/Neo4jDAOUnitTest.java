package rockets.dataaccess.neo4j;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import rockets.dataaccess.DAO;
import rockets.model.*;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Neo4jDAOUnitTest {
    private static final String TEST_DB = "target/test-data/test-db";

    private DAO dao;
    private Session session;
    private SessionFactory sessionFactory;

    private LaunchServiceProvider esa;
    private LaunchServiceProvider spacex;
    private LaunchServiceProvider okb;
    private Rocket rocket;
    private PayLoad payload;
    private RocketFamily rf;

    @BeforeAll
    public void initializeNeo4j() {
        EmbeddedDriver driver = createEmbeddedDriver(TEST_DB);

        sessionFactory = new SessionFactory(driver, User.class.getPackage().getName());
        session = sessionFactory.openSession();
        dao = new Neo4jDAO(sessionFactory);
    }

    @BeforeEach
    public void setup() {
        esa = new LaunchServiceProvider("ESA", 1970, "Europe");
        okb = new LaunchServiceProvider("OKB", 1946, "Russia");
        spacex = new LaunchServiceProvider("SpaceX", 2002, "USA");
        rocket = new Rocket("F9", "Block 5","USA", spacex);
        payload = new PayLoad("satellite", "sputnik 1", okb);
        rf = new RocketFamily("space");
    }

    private static EmbeddedDriver createEmbeddedDriver(String fileDir) {
        File file = new File(fileDir);
        Configuration configuration = new Configuration.Builder()
                .uri(file.toURI().toString()) // For Embedded
                .build();
        EmbeddedDriver driver = new EmbeddedDriver();
        driver.configure(configuration);
        return driver;
    }

    @Test
    public void shouldCreateNeo4jDAOSuccessfully() {
        assertNotNull(dao);
    }

    @Test
    public void shouldCreateARocketSuccessfully() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");
        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull(graphRocket.getId());
        assertEquals(rocket, graphRocket);
        LaunchServiceProvider manufacturer = graphRocket.getManufacturer();

        assertNotNull(manufacturer.getId());
        assertEquals(rocket.getWikilink(), graphRocket.getWikilink());
        assertEquals(spacex, manufacturer);
    }

    @Test
    public void shouldCreateAPayLoadSuccessfully() {
        payload.setWikilink("https://en.wikipedia.org/wiki/Payload");
        PayLoad graphPayLoad = dao.createOrUpdate(payload);
        assertNotNull(graphPayLoad.getId());
        assertEquals(payload, graphPayLoad);
        LaunchServiceProvider manufacturer = graphPayLoad.getManufacturer();
        assertNotNull(manufacturer.getId());
        assertEquals(payload.getWikilink(), graphPayLoad.getWikilink());
        assertEquals(okb, manufacturer);
    }

    @Test
    public void shouldCreateARocketFamilySuccessfully() {
        rf.setWikilink("https://en.wikipedia.org/wiki/Sputnik_(rocket)");
        RocketFamily graphRocketFamily = dao.createOrUpdate(rf);
        assertNotNull(graphRocketFamily.getId());
        assertEquals(rf, graphRocketFamily);
        Set<Rocket> rockets = graphRocketFamily.getRockets();
        assertNotNull(rockets);
        assertEquals(rf.getWikilink(), graphRocketFamily.getWikilink());
    }



    @Test
    public void shouldUpdateRocketAttributeSuccessfully() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");

        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull(graphRocket.getId());
        assertEquals(rocket, graphRocket);

        String newLink = "http://adifferentlink.com";
        rocket.setWikilink(newLink);
        dao.createOrUpdate(rocket);
        graphRocket = dao.load(Rocket.class, rocket.getId());
        assertEquals(newLink, graphRocket.getWikilink());
    }

    @Test
    public void shouldUpdatePayLoadAttributeSuccessfully() {
        payload.setWikilink("https://en.wikipedia.org/wiki/Payload");
        PayLoad graphPayLoad = dao.createOrUpdate(payload);
        assertNotNull(graphPayLoad.getId());
        assertEquals(payload, graphPayLoad);

        String newLink = "http://adifferentlink.com";
        payload.setWikilink(newLink);
        dao.createOrUpdate(payload);
        graphPayLoad = dao.load(PayLoad.class, payload.getId());
        assertEquals(newLink, graphPayLoad.getWikilink());
    }

    @Test
    public void shouldUpdateRocketFamilyAttributeSuccessfully() {
        rf.setWikilink("https://en.wikipedia.org/wiki/Sputnik_(rocket)");
        RocketFamily graphRf = dao.createOrUpdate(rf);
        assertNotNull(graphRf.getId());
        assertEquals(rf, graphRf);

        String newLink = "http://adifferentlink.com";
        rf.setWikilink(newLink);
        dao.createOrUpdate(rf);
        graphRf = dao.load(RocketFamily.class, rf.getId());
        assertEquals(newLink, graphRf.getWikilink());
    }

    @Test
    public void shouldNotSaveTwoSameRockets() {
        assertNull(spacex.getId());

        Rocket rocket1 = new Rocket("F9", "Block 5", "USA", spacex);
        Rocket rocket2 = new Rocket("F9", "Block 5","USA", spacex);
        assertEquals(rocket1, rocket2);
        dao.createOrUpdate(rocket1);
        assertNotNull(spacex.getId());
        Collection<Rocket> rockets = dao.loadAll(Rocket.class);
        assertEquals(1, rockets.size());
        Collection<LaunchServiceProvider> manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, manufacturers.size());
        dao.createOrUpdate(rocket2);
        manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, manufacturers.size());
        rockets = dao.loadAll(Rocket.class);
        assertEquals(1, rockets.size());
    }

    @Test
    public void shouldLoadAllRockets() {
        Set<Rocket> rockets = Sets.newHashSet(
                new Rocket("Ariane4","Block 5", "France", esa),
                new Rocket("F5", "Block 5","USA", spacex),
                new Rocket("BFR", "Block 5","USA", spacex)
        );

        for (Rocket r : rockets) {
            dao.createOrUpdate(r);
        }

        Collection<Rocket> loadedRockets = dao.loadAll(Rocket.class);
        assertEquals(rockets.size(), loadedRockets.size());
        for (Rocket r : rockets) {
            assertTrue(rockets.contains(r));
        }
    }

    @Test
    public void shouldCreateALaunchSuccessfully() {
        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(2017, 1, 1));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchSite("VAFB");
        launch.setOrbit("LEO");
        dao.createOrUpdate(launch);

        Collection<Launch> launches = dao.loadAll(Launch.class);
        assertFalse(launches.isEmpty());
        assertTrue(launches.contains(launch));
    }


    @Test
    public void shouldUpdateLaunchAttributesSuccessfully() {
        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(2017, 1, 1));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchSite("VAFB");
        launch.setOrbit("LEO");
        dao.createOrUpdate(launch);

        Collection<Launch> launches = dao.loadAll(Launch.class);

        Launch loadedLaunch = launches.iterator().next();
        assertNull(loadedLaunch.getFunction());

        launch.setFunction("experimental");
        dao.createOrUpdate(launch);
        launches = dao.loadAll(Launch.class);
        assertEquals(1, launches.size());
        loadedLaunch = launches.iterator().next();
        assertEquals("experimental", loadedLaunch.getFunction());
    }

    @Test
    public void shouldDeleteRocketWithoutDeleteLSP() {
        dao.createOrUpdate(rocket);
        assertNotNull(rocket.getId());
        assertNotNull(rocket.getManufacturer().getId());
        assertFalse(dao.loadAll(Rocket.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
        dao.delete(rocket);
        assertTrue(dao.loadAll(Rocket.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
    }

    @Test
    public void shouldDeletePayLoadWithoutDeleteLSP() {
        dao.createOrUpdate(payload);
        assertNotNull(payload.getId());
        assertNotNull(payload.getManufacturer().getId());
        assertFalse(dao.loadAll(PayLoad.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
        dao.delete(payload);
        assertTrue(dao.loadAll(PayLoad.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
    }

    @AfterEach
    public void tearDown() {
        session.purgeDatabase();
    }

    @AfterAll
    public void closeNeo4jSession() {
        session.clear();
        sessionFactory.close();
    }
}