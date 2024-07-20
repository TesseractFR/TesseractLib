package onl.tesseract.tesseractlib.dao;

import lombok.extern.slf4j.Slf4j;
import onl.tesseract.tesseractlib.entity.Title;
import onl.tesseract.tesseractlib.bdd.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

@Slf4j
public class TitleDAO {

    public List<Title> getTitles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Title ", Title.class).list();
        }
    }

    public Title getTitleByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Title.class, name);
        }
    }


}
