

import cl.minsal.semantikos.kernel.components.CategoryManagerInterface;
import cl.minsal.semantikos.kernel.components.ConceptManagerInterface;
import cl.minsal.semantikos.model.Category;
import cl.minsal.semantikos.model.ConceptSMTK;
import cl.minsal.semantikos.model.Description;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


@ManagedBean
@ViewScoped
public class NavBrowser {


    private List<Category> categories;
    private LazyDataModel<ConceptSMTK> concepts;

    private Long[] selectedCategories;
    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    private Category category;
    private ConceptSMTK conceptSMTK;
    private Description description;
    private ConceptSMTK conceptSelected;

    @EJB
    private CategoryManagerInterface categoryManager;

    @EJB
    private ConceptManagerInterface conceptManager;


    @PostConstruct
    public void init() {


        categories = categoryManager.getCategories();

        concepts = new LazyDataModel<ConceptSMTK>() {
            @Override
            public List<ConceptSMTK> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<ConceptSMTK> conceptSMTKs=null;

                    conceptSMTKs = conceptManager.findConceptByConceptIDOrDescriptionCategoryPageNumber(pattern, selectedCategories, first, pageSize);
                    this.setRowCount(conceptManager.getAllConceptCount(pattern, selectedCategories));


                return conceptSMTKs;
            }

        };


    }


    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public LazyDataModel<ConceptSMTK> getConcepts() {
        return concepts;
    }

    public void setConcepts(LazyDataModel<ConceptSMTK> concepts) {
        this.concepts = concepts;
    }

    public Long[] getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(Long[] selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public CategoryManagerInterface getCategoryManager() {
        return categoryManager;
    }

    public void setCategoryManager(CategoryManagerInterface categoryManager) {
        this.categoryManager = categoryManager;
    }

    public ConceptManagerInterface getConceptManager() {
        return conceptManager;
    }

    public void setConceptManager(ConceptManagerInterface conceptManager) {
        this.conceptManager = conceptManager;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ConceptSMTK getConceptSMTK() {
        return conceptSMTK;
    }

    public void setConceptSMTK(ConceptSMTK conceptSMTK) {
        this.conceptSMTK = conceptSMTK;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public ConceptSMTK getConceptSelected() {
        return conceptSelected;
    }

    public void setConceptSelected(ConceptSMTK conceptSelected) {
        this.conceptSelected = conceptSelected;
    }




}