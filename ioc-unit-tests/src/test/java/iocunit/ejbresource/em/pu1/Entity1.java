package iocunit.ejbresource.em.pu1;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author aschoerk
 */
@Entity
public class Entity1 {
    public Entity1() {

    }

    public Entity1(final String name) {
        this.name = name;
    }

    @Id
    public int id;

    public String name;
}
