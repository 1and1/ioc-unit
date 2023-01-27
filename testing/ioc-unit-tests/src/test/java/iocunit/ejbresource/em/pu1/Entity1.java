package iocunit.ejbresource.em.pu1;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
