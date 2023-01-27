package iocunit.ejbresource.em;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author aschoerk
 */
@Entity
public class Entity3 {
    public Entity3() {

    }

    public Entity3(final String name) {
        this.name = name;
    }

    @Id
    public int id;

    public String name;
}
