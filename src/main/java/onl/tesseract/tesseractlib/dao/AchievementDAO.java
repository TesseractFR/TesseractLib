package onl.tesseract.tesseractlib.dao;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import onl.tesseract.tesseractlib.entity.Achievement;
import onl.tesseract.tesseractlib.bdd.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class AchievementDAO {
    @Getter
    static private final AchievementDAO instance = new AchievementDAO();

    public Achievement getByName(String name) {
        AtomicReference<Achievement> result = new AtomicReference<>();
        DaoUtils.executeInsideTransaction(session -> {
            Query<Achievement> query = session.createQuery("from Achievement where name = :name", Achievement.class);
            query.setParameter("name", name);
            result.set(query.uniqueResult());
        });
        return result.get();
    }

    public List<Achievement> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        return DaoUtils.loadAll(Achievement.class, session);
    }


    }

}
