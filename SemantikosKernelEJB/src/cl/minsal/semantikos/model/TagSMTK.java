package cl.minsal.semantikos.model;

/**
 * @author Andrés Farías on 9/4/16.
 */
public class TagSMTK extends PersistentEntity {

    /** El nombre del Tag Semantikos */
    private String name;

    public TagSMTK(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Un Tag Semántikos se dirá válido, por ahora, si está persistido.
     *
     * @return <code>true</code> si es válido y <code>false</code> sino.
     */
    public boolean isValid() {

        /* Si el tag está persistido es válido */
        return this.isPersistent();
    }
}