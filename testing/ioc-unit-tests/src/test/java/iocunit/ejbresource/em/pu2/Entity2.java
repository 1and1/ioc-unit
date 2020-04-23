package iocunit.ejbresource.em.pu2;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author aschoerk
 */
@Entity
public class Entity2 {
    public Entity2() {

    }

    public Entity2(final String name) {
        this.name = name;
    }

    @Id
    public int id;

    public String name;
}
