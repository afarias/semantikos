package cl.minsal.semantikos.kernel.components;

import cl.minsal.semantikos.kernel.daos.ConceptDAO;
import cl.minsal.semantikos.kernel.daos.DescriptionDAO;
import cl.minsal.semantikos.kernel.daos.RelationshipDAO;
import cl.minsal.semantikos.model.*;
import cl.minsal.semantikos.model.businessrules.ConceptCreationBusinessRuleContainer;
import cl.minsal.semantikos.model.businessrules.ConceptEditionBusinessRuleContainer;
import cl.minsal.semantikos.model.businessrules.RelationshipEditionBR;
import cl.minsal.semantikos.model.relationships.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import static cl.minsal.semantikos.kernel.daos.DAO.NON_PERSISTED_ID;

/**
 * @author Andrés Farías
 */
@Stateless
public class ConceptManagerImpl implements ConceptManagerInterface {

    /** El logger de la clase */
    private static final Logger logger = LoggerFactory.getLogger(ConceptManagerImpl.class);

    @EJB
    private ConceptDAO conceptDAO;

    @EJB
    private AuditManagerInterface auditManager;

    @EJB
    private DescriptionDAO descriptionDAO;

    @EJB
    private RelationshipDAO relationshipDAO;

    @Override
    public ConceptSMTK getConceptByCONCEPT_ID(String conceptId) {

        /* Se recupera el concepto base (sus atributos) sin sus relaciones ni descripciones */
        ConceptSMTK concept = this.conceptDAO.getConceptByCONCEPT_ID(conceptId);

        /* Se cargan las descripciones del concepto */
        concept.setDescriptions(descriptionDAO.getDescriptionsByConceptID(concept.getId()));

        return concept;
    }

    @Override
    public ConceptSMTK getConceptByID(long id) {

        /* Se recupera el concepto base (sus atributos) sin sus relaciones ni descripciones */
        ConceptSMTK conceptSMTK = this.conceptDAO.getConceptByID(id);

        /* Se cargan las descripciones del concepto */
        conceptSMTK.setDescriptions(descriptionDAO.getDescriptionsByConceptID(conceptSMTK.getId()));

        return conceptSMTK;
    }

    @Override
    public List<ConceptSMTK> findConceptBy(String patternOrConceptID, Long[] categories, int pageNumber, int pageSize) {


        boolean isModeled= false;
        //TODO: Actualizar esto de los estados que ya no va.



        categories = (categories == null) ? new Long[0] : categories;

        patternOrConceptID = standardizationPattern(patternOrConceptID);
        String[] arrayPattern = patternToArray(patternOrConceptID);

        //Búsqueda por categoría y patron o concept ID
        if ((categories.length != 0 && patternOrConceptID != null)) {
            if (patternOrConceptID.length() >= 3) {
                if (arrayPattern.length >= 2) {
                    return conceptDAO.getConceptBy(arrayPattern, categories, isModeled, pageSize, pageNumber);
                } else {
                    return conceptDAO.getConceptBy(arrayPattern[0], categories, pageNumber, pageSize, isModeled);
                }
            }
        }

        //Búsqueda por patron o concept ID
        if ((categories.length == 0 && patternOrConceptID != null)) {
            if (patternOrConceptID.length() >= 3) {
                if (arrayPattern.length >= 2) {
                    return conceptDAO.getConceptBy(arrayPattern, isModeled, pageSize, pageNumber);
                } else {
                    return conceptDAO.getConceptBy(arrayPattern[0], categories, pageNumber, pageSize, isModeled);
                }
            }

        }

        //Búsqueda por categoría
        if (categories.length > 0) {
            return conceptDAO.getConceptBy(categories, isModeled, pageSize, pageNumber);
        }


        //Búsqueda por largo (PageSize y PageNumber)
        return conceptDAO.getConceptsBy(isModeled, pageSize, pageNumber);
    }

    @Override
    public List<ConceptSMTK> findConceptBy(String pattern) {

        List<ConceptSMTK> conceptSMTKList = findConceptBy(pattern, new Long[0], 0, countConceptBy(pattern, new Long[0]));
        if (conceptSMTKList.size() != 0) {
            return conceptSMTKList;
        } else {
            pattern = truncatePattern(pattern);
            return findConceptBy(pattern, new Long[0], 0, countConceptBy(pattern, new Long[0]));
        }

    }

    @Override
    public int countConceptBy(String pattern, Long[] categories) {


        // TODO: arreglar esto (Estados)

        boolean isModeled= false;


        pattern = standardizationPattern(pattern);
        String[] arrayPattern = patternToArray(pattern);

        categories = (categories == null) ? new Long[0] : categories;

        //Cuenta por categoría y patron o concept ID
        if ((categories.length != 0 && pattern != null)) {
            if (pattern.length() >= 3) {
                if (arrayPattern.length >= 2) {
                    return conceptDAO.countConceptBy(arrayPattern, categories, isModeled);
                } else {
                    return conceptDAO.countConceptBy(arrayPattern[0], categories, isModeled);
                }
            }
        }

        //Cuenta por patron o concept ID

        if (pattern != null) {
            if (pattern.length() >= 3) {
                if (arrayPattern.length >= 2) {
                    return conceptDAO.countConceptBy(arrayPattern, new Long[0], isModeled);
                } else {
                    return conceptDAO.countConceptBy(arrayPattern[0], categories, isModeled);
                }
            }
        }

        //Cuenta por categoría
        if (categories.length > 0) {
            return conceptDAO.countConceptBy((String[]) null, categories, isModeled);
        }
        return conceptDAO.countConceptBy((String[]) null, categories, isModeled);

    }

    @Override
    public void persist(@NotNull ConceptSMTK conceptSMTK, User user) {
        logger.debug("El concepto " + conceptSMTK + " será persistido.");

        /* Pre-condición técnica: el concepto no debe estar persistido */
        validatesIsNotPersistent(conceptSMTK);

        /* Pre-condiciones: Reglas de negocio para la persistencia */
        new ConceptCreationBusinessRuleContainer().apply(conceptSMTK, user);

        /* En este momento se está listo para persistir el concepto (sus atributos básicos) */
        conceptDAO.persistConceptAttributes(conceptSMTK, user);

        /* Y se persisten sus descripciones */
        for (Description description : conceptSMTK.getDescriptions()) {
            descriptionDAO.persist(description, user);
        }

        /* Y sus relaciones */
        for (Relationship relationship : conceptSMTK.getRelationships()) {
            relationshipDAO.persist(relationship);
        }

        /* Se deja registro en la auditoría sólo para conceptos modelados */
        if (conceptSMTK.isModeled()) {
            auditManager.recordNewConcept(conceptSMTK, user);
        }
        logger.debug("El concepto " + conceptSMTK + " fue persistido.");
    }

    @Override
    public void publish(@NotNull ConceptSMTK conceptSMTK, User user) {
        conceptSMTK.setPublished(true);
        conceptDAO.update(conceptSMTK);

        if (conceptSMTK.isModeled()) {
            auditManager.recordConceptPublished(conceptSMTK, user);
        }
    }

    @Override
    public void invalidate(@NotNull ConceptSMTK conceptSMTK, @NotNull User user) {

        logger.info("Se dejará no vigente el concepto: " + conceptSMTK);

        /* Se validan las pre-condiciones para eliminar un concepto */
        new ConceptEditionBusinessRuleContainer().preconditionsConceptInvalidation(conceptSMTK, user);

        /* Se invalida el concepto */
        conceptSMTK.setPublished(false);
        conceptSMTK.setValidUntil(new Timestamp(System.currentTimeMillis()));
        conceptDAO.update(conceptSMTK);

        /* Se registra en el historial */
        if (conceptSMTK.isModeled()){
            auditManager.recordConceptInvalidation(conceptSMTK, user);
        }
        logger.info("Se ha dejado no vigente el concepto: " + conceptSMTK);
    }

    @Override
    public void changeCategory(@NotNull ConceptSMTK conceptSMTK, @NotNull Category targetCategory, User user) {
        /* TODO: Validar reglas de negocio */

        /* TODO: Cambiar la categoría y actualizar el cambio */
        Category originalCategory = conceptSMTK.getCategory();

        /* Complex Logic here */

        /* Se registra en el historial si el concepto está modelado */
        if (conceptSMTK.isModeled()) {
            auditManager.recordConceptCategoryChange(conceptSMTK, originalCategory, user);
        }
    }

    @Override
    public void changeTagSMTK(@NotNull ConceptSMTK conceptSMTK, @NotNull TagSMTK tagSMTK, User user) {
        /* Se realizan las validaciones básicas */
        new ConceptEditionBusinessRuleContainer().preconditionsConceptEditionTag(conceptSMTK);

        /* Se realiza la actualización */
        conceptDAO.update(conceptSMTK);
    }

    /**
     * Este método es responsable de actualizar las relaciones respecto a una edición en una relación. Una relación
     * actualizada se compone de dos instancias de <code>Relationship</code> con el mismo identificador único, pero una
     * está marcada para ser actualizada.
     *
     * <p>Para actualizar
     * una relación se revisan aquellas marcadas para actualizar. La relación <em>original</em> tiene un campo que
     * indica que debe ser actualizada <code>isToBeUpdated</code>. Para cada una debe existir otra relación, con el
     * mismo ID, que tiene los cambios, y NO está marcada para ser actualizada.</p>
     *
     * <p>
     * Este método itera sobre las relaciones marcadas para ser actualizadas, busca su par, y si existe:
     * <ul>
     * <li>Aplica reglas de negocio para validar que esté en orden</li>
     * <li>y deja inválida la original, y persiste la nueva.</li>
     * </ul>
     * </p>
     *
     * @param conceptSMTK El concepto cuyas relaciones se quiere actualizar.
     */
    private void updateRelationships(ConceptSMTK conceptSMTK) {

        for (Relationship original : conceptSMTK.getRelationships()) {

            /* Se busca por una relación que tenga que ser actualizada */
            if (original.isToBeUpdated() && original.isPersisted()) {

                /* La relación editada es idéntica en ID pero no está marcada para ser editada */
                Relationship updated = getEditedRelationshipOf(conceptSMTK, original);

                /* Se aplican las reglas de negocio */
                new RelationshipEditionBR().applyRules(original, updated);

                /* Y se actualizan */
                relationshipDAO.invalidate(original);
                relationshipDAO.persist(updated);
            }
        }
    }

    /**
     * Este método es responsable de ubicar una relación <em>editada</em> a partir de otra relación. La relación
     * editada
     * es idéntica en ID pero no está marcada para ser editada.
     *
     * @param conceptSMTK El concepto con las relaciones en donde se busca la editada.
     *
     * @return La relación editada a partir de la relación <code>original</code>.
     */
    private Relationship getEditedRelationshipOf(ConceptSMTK conceptSMTK, Relationship original) {

        if (!conceptSMTK.getRelationships().contains(original)) {
            throw new EJBException("No se encontró la relación original en el concepto fuente. Relación: " + original + ". Concepto: " + conceptSMTK);
        }

        for (Relationship relationship : conceptSMTK.getRelationships()) {
            if (relationship.getId() == original.getId() && !relationship.isToBeUpdated()) {
                return relationship;
            }
        }

        throw new EJBException("No se encontró la relación editada de la relación " + original);
    }

    /**
     * Este método es responsable de validar que el concepto no se encuentre persistido.
     *
     * @param conceptSMTK El concepto sobre el cual se realiza la validación de persistencia.
     *
     * @throws javax.ejb.EJBException Se arroja si el concepto tiene un ID de persistencia.
     */
    private void validatesIsNotPersistent(ConceptSMTK conceptSMTK) throws EJBException {
        long id = conceptSMTK.getId();
        if (id != NON_PERSISTED_ID) {
            throw new EJBException("El concepto ya se encuentra persistido. ID=" + id);
        }
    }

    /**
     * Este método es responsable de validar que el concepto se encuentre persistido.
     *
     * @param conceptSMTK El concepto sobre el cual se realiza la validación de persistencia.
     *
     * @throws javax.ejb.EJBException Se arroja si el concepto no tiene un ID de persistencia.
     */
    private void validatesIsPersistent(ConceptSMTK conceptSMTK) throws EJBException {
        long id = conceptSMTK.getId();
        if (id == NON_PERSISTED_ID) {
            throw new EJBException("El concepto no se encuentra persistido. ID=" + id);
        }
    }

    @Override
    public ConceptSMTK merge(ConceptSMTK conceptSMTK) {

        // TODO: Por Implementar
        return null;
    }

    @Override
    public String generateConceptId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<Description> getDescriptionsBy(ConceptSMTK concept) {
        return descriptionDAO.getDescriptionsByConceptID(concept.getId());
    }

    @Override
    public List<Relationship> loadRelationships(ConceptSMTK concept) {
        List<Relationship> relationships = relationshipDAO.getRelationshipsBySourceConcept(concept.getId());
        concept.setRelationships(relationships);
        return relationships;
    }

    @Override
    public List<ConceptSMTK> getConceptDraft() {
        return conceptDAO.getConceptDraft();
    }

    /**
     * Método encargado de convertir un string en una lista de string.
     *
     * @param pattern patrón de texto
     *
     * @return retorna una lista de String
     */

    private String[] patternToArray(String pattern) {
        if (pattern != null) {
            StringTokenizer st;
            String token;
            st = new StringTokenizer(pattern, " ");
            ArrayList<String> listPattern = new ArrayList<>();
            int count = 0;

            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (token.length() >= 3) {
                    listPattern.add(token.trim());
                }
                if (count == 0 && listPattern.size() == 0) {
                    listPattern.add(token.trim());
                }
                count++;
            }
            return listPattern.toArray(new String[listPattern.size()]);
        }
        return new String[0];
    }

    /**
     * Método de normalización del patrón de búsqueda, lleva las palabras a minúsculas,
     * le quita los signos de puntuación y ortográficos
     *
     * @param pattern patrón de texto a normalizar
     *
     * @return patrón normalizado
     */
    //TODO: Falta quitar los StopWords (no se encuentran definidos)
    private String standardizationPattern(String pattern) {

        if (pattern != null) {
            pattern = Normalizer.normalize(pattern, Normalizer.Form.NFD);
            pattern = pattern.toLowerCase();
            pattern = pattern.replaceAll("[^\\p{ASCII}]", "");
            pattern = pattern.replaceAll("\\p{Punct}+", "");
        }
        return pattern;
    }

    /**
     * Método encargado de truncar a un largo de 3 las palabras del String ingresado
     *
     * @param pattern arreglo de palabras
     *
     * @return arreglo de String con las palabras truncadas
     */
    private String truncatePattern(String pattern) {
        pattern = standardizationPattern(pattern);
        String[] arrayToPattern = patternToArray(pattern);

        for (int i = 0; i < arrayToPattern.length; i++) {
            pattern = arrayToPattern[i].substring(0, 3) + " ";
        }
        return pattern;
    }
}
