package cl.minsal.semantikos.kernel.daos;

import cl.minsal.semantikos.model.audit.ConceptAuditAction;

import javax.ejb.Local;
import java.util.List;

/**
 * @author Andrés Farías on 8/23/16.
 */
@Local
public interface AuditDAO {

    /**
     * Este método es responsable de recuperar y retornar en una lista los últimos <code>numberOfChanges</code> cambios
     * que ha tenido un concepto.
     *
     * @param idConcept       El ID del concepto cuyos cambios se desean recuperar.
     * @param numberOfChanges La cantidad de cambios que se desea recuperar (ordenados de más reciente a menos
     *                        reciente).
     * @param changes         Indica si se desean las acciones auditables registradas que son cambios
     *
     * @return Una lista con los últimos <code>numberOfChanges</code> realizados sobre el concepto
     * <code>conceptSMTK</code>
     */
    public List<ConceptAuditAction> getConceptAuditActions(long idConcept, int numberOfChanges, boolean changes);

    /**
     * Este método es responsable de registrar una acción de auditoría (historial) en la base de datos.
     *
     * @param conceptAuditAction La acción de auditoría que se desea registrar.
     */
    public void recordAuditAction(ConceptAuditAction conceptAuditAction);
}
