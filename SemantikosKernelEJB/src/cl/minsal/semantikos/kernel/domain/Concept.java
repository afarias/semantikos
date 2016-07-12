package cl.minsal.semantikos.kernel.domain;

import cl.minsal.semantikos.kernel.DAO.implementation.CategoryDAOImpl;
import cl.minsal.semantikos.kernel.DAO.implementation.DescriptionDAOImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 08-07-16.
 */
public class Concept {
    private long idConcept;
    private Long idConceptSct;
    private Long idTipoConcepto;
    private Long idCategoria;
    private boolean revisar;
    private boolean consultar;
    private Long idEstadoConcepto;
    private String nombre;
    private boolean esCompletamenteDefinido;
    private boolean estaPublicado;

    private List<Description> descriptions = new ArrayList<Description>();;

    private Category category;

    public Concept() {
    }

    public Concept(int idCategory, String termino) {

        Concept concept = new Concept();

        DescriptionDAOImpl descriptionDAO = new DescriptionDAOImpl();

        List<DescriptionType> descriptionTypes;
        CategoryDAOImpl categoryDAO= new CategoryDAOImpl();

        Category category = categoryDAO.getCategoryById(idCategory);

        descriptionTypes = descriptionDAO.getAllTypes();

        concept.setConsultar(false);
        concept.setRevisar(false);

        Description FSN = new Description();

        FSN.setAutogeneratedName(true);
        FSN.setCaseSensitive(false);

        FSN.setActive(false);
        FSN.setDescriptionType(descriptionTypes.get(0));
        FSN.setIdDescriptionType(descriptionTypes.get(0).getIdDescriptionType());
        FSN.setTermino(termino+" ("+category.getNombre()+")");

        concept.addDescription(FSN);

        Description preferido = new Description();

        preferido.setAutogeneratedName(false);
        preferido.setCaseSensitive(false);

        preferido.setActive(false);
        preferido.setDescriptionType(descriptionTypes.get(1));
        preferido.setIdDescriptionType(descriptionTypes.get(1).getIdDescriptionType());
        preferido.setTermino(termino);

        concept.setIdCategoria((long) idCategory);
        concept.setCategory(category);

    }

    public long getIdConcept() {
        return idConcept;
    }

    public void setIdConcept(long idConcept) {
        this.idConcept = idConcept;
    }

    public Long getIdConceptSct() {
        return idConceptSct;
    }

    public void setIdConceptoSct(Long idConceptoSct) {
        this.idConceptSct = idConceptoSct;
    }

    public Long getIdTipoConcepto() {
        return idTipoConcepto;
    }

    public void setIdTipoConcepto(Long idTipoConcepto) {
        this.idTipoConcepto = idTipoConcepto;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public boolean isRevisar() {
        return revisar;
    }

    public void setRevisar(boolean revisar) {
        this.revisar = revisar;
    }

    public boolean isConsultar() {
        return consultar;
    }

    public void setConsultar(boolean consultar) {
        this.consultar = consultar;
    }

    public Long getIdEstadoConcepto() {
        return idEstadoConcepto;
    }

    public void setIdEstadoConcepto(Long idEstadoConcepto) {
        this.idEstadoConcepto = idEstadoConcepto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsCompletamenteDefinido() {
        return esCompletamenteDefinido;
    }

    public void setEsCompletamenteDefinido(boolean esCompletamenteDefinido) {
        this.esCompletamenteDefinido = esCompletamenteDefinido;
    }

    public boolean isEstaPublicado() {
        return estaPublicado;
    }

    public void setEstaPublicado(boolean estaPublicado) {
        this.estaPublicado = estaPublicado;
    }

    public List<Description> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<Description> descriptions) {
        this.descriptions = descriptions;
    }

    public void addDescription(Description description) {
        this.descriptions.add(description);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Concept that = (Concept) o;

        if (idConcept != that.idConcept) return false;
        if (revisar != that.revisar) return false;
        if (consultar != that.consultar) return false;
        if (esCompletamenteDefinido != that.esCompletamenteDefinido) return false;
        if (estaPublicado != that.estaPublicado) return false;
        if (idConceptSct != null ? !idConceptSct.equals(that.idConceptSct) : that.idConceptSct != null)
            return false;
        if (idTipoConcepto != null ? !idTipoConcepto.equals(that.idTipoConcepto) : that.idTipoConcepto != null)
            return false;
        if (idCategoria != null ? !idCategoria.equals(that.idCategoria) : that.idCategoria != null) return false;
        if (idEstadoConcepto != null ? !idEstadoConcepto.equals(that.idEstadoConcepto) : that.idEstadoConcepto != null)
            return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idConcept ^ (idConcept >>> 32));
        result = 31 * result + (idConceptSct != null ? idConceptSct.hashCode() : 0);
        result = 31 * result + (idTipoConcepto != null ? idTipoConcepto.hashCode() : 0);
        result = 31 * result + (idCategoria != null ? idCategoria.hashCode() : 0);
        result = 31 * result + (revisar ? 1 : 0);
        result = 31 * result + (consultar ? 1 : 0);
        result = 31 * result + (idEstadoConcepto != null ? idEstadoConcepto.hashCode() : 0);
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (esCompletamenteDefinido ? 1 : 0);
        result = 31 * result + (estaPublicado ? 1 : 0);
        return result;
    }
}
