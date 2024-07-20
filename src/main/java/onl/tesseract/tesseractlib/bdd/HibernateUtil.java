package onl.tesseract.tesseractlib.bdd;

import lombok.Getter;
import onl.tesseract.tesseractlib.Config;
import onl.tesseract.tesseractlib.entity.Achievement;
import onl.tesseract.tesseractlib.entity.Title;
import onl.tesseract.tesseractlib.entity.TPlayerInfo;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.Action;
import org.jetbrains.annotations.NotNull;

public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Config config = Config.getInstance();
            if (config == null || config.getDb_host() == null) throw new RuntimeException("Configuration manquante");

            Configuration configuration = setConfiguration(config);

            // Enregistrer les classes d'entité
            configuration.addAnnotatedClass(TPlayerInfo.class);
            configuration.addAnnotatedClass(Achievement.class);
            configuration.addAnnotatedClass(Title.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static @NotNull Configuration setConfiguration(Config config) {
        Configuration configuration = new Configuration();
        configuration.setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
        configuration.setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:mysql://" + config.getDb_host() + ":" + config.getDb_port() + "/" + config.getDb_database());
        configuration.setProperty(AvailableSettings.JAKARTA_JDBC_USER, config.getDb_username());
        configuration.setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, config.getDb_password());

        configuration.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL8Dialect");

        // Enable Hibernate's automatic session context management
        configuration.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");

        // Echo all executed SQL to stdout
        configuration.setProperty(AvailableSettings.SHOW_SQL, "false");
        configuration.setProperty(AvailableSettings.FORMAT_SQL, "true");

        // Drop and re-create the database schema on startup
        configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.ACTION_VALIDATE);
        return configuration;
    }

}
