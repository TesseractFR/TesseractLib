package onl.tesseract.tesseractlib.bdd;

import onl.tesseract.tesseractlib.Config;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static Config config = Config.getInstance();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.setProperty(AvailableSettings.DRIVER, "com.mysql.cj.jdbc.Driver");
            configuration.setProperty(AvailableSettings.URL, "jdbc:mysql://"+config.getDb_host()+":"+config.getDb_port()+"/"+config.getDb_database());
            configuration.setProperty(AvailableSettings.USER,  config.getDb_username());
            configuration.setProperty(AvailableSettings.PASS, config.getDb_password());

            configuration.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL8Dialect");

            // Enable Hibernate's automatic session context management
            configuration.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            // Echo all executed SQL to stdout
            configuration.setProperty(AvailableSettings.SHOW_SQL, "true");

            // Drop and re-create the database schema on startup
            configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");


            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
