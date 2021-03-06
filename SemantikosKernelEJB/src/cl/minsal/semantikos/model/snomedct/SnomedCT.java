package cl.minsal.semantikos.model.snomedct;

import cl.minsal.semantikos.model.PersistentEntity;
import cl.minsal.semantikos.model.relationships.TargetDefinition;

/**
 * Esta clase, representa la terminología internacional estándar SNOMED CT.
 * @author Andrés Farías
 */
public class SnomedCT extends PersistentEntity implements TargetDefinition{

    /** Descripción del concepto */
    private String description;
    private String idSnomedCT;
    private String domain;

    @Override
    public boolean isBasicType() {
        return false;
    }

    @Override
    public boolean isSMTKType() {
        return false;
    }

    @Override
    public boolean isHelperTable() {
        return false;
    }

    @Override
    public boolean isSnomedCTType() {
        return true;
    }

    @Override
    public boolean isCrossMapType() {
        return false;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setIdSnomedCT(String idSnomedCT) {
        this.idSnomedCT = idSnomedCT;
    }

    public String getIdSnomedCT() {
        return idSnomedCT;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }
}
