package cl.minsal.semantikos.kernel.daos;

import cl.minsal.semantikos.model.ConceptSMTK;

import javax.ejb.Local;
import java.util.List;

/**
 * @author Gustavo Punucura
 */
@Local
public interface ConceptDAO {

    /**
     * Este método es responsable de recuperar todos los conceptos, sin considerar su categoría, que posean un cierto
     * estado interno.
     *
     * @param states     Los estados que deben satisfacer los conceptos a retornar.
     * @param pageSize   El tamaño de la página.
     * @param pageNumber La página de resultados que se desea obtener.
     *
     * @return
     */
    public List<ConceptSMTK> getAllConcepts(Long[] states, int pageSize, int pageNumber);

    /**
     * Este método es responsable de recuperar los conceptos que coincidan con un cierto patrón (<code>pattern</code>)
     * y que pertenezcan a una o más categorías.
     *
     * @param pattern    El patrón de búsqueda en los términos.
     * @param categories Las categorías en las que se realiza la búsqueda.
     * @param states     Los estados que deben satisfacer los conceptos a retornar.
     * @param pageSize   El tamaño de la página.
     * @param pageNumber La página de resultados que se desea obtener.
     *
     * @return Una lista de <code>ConceptSMTK</code> que cumplen los criterios de búsqueda.
     */
    public List<ConceptSMTK> getConceptByPatternCategory(String[] pattern, Long[] categories, Long[] states, int pageSize, int pageNumber);

    /**
     * Este método es responsable de recuperar los conceptos que pertenecen a un conjunto de categorías.
     *
     * @param categories Las categorías desde donde se extraen los conceptos.
     * @param states     El estado de los conceptos que se desea obtener.
     * @param pageSize   El tamaño de la página.
     * @param pageNumber La página de resultados que se desea obtener.
     *
     * @return Una lista de <code>ConceptSMTK</code> que cumplen los criterios de búsqueda.
     */
    public List<ConceptSMTK> getConceptByCategory(Long[] categories, Long[] states, int pageSize, int pageNumber);

    public List<ConceptSMTK> getConceptByPatternOrConceptIDAndCategory(String PatternOrID, Long[] Category, int pageNumber, int pageSize, Long[] states);

    public int getAllConceptCount(String[] Pattern, Long[] category, Long[] states);

    public int getCountFindConceptID(String Pattern, Long[] category, Long[] states);
}