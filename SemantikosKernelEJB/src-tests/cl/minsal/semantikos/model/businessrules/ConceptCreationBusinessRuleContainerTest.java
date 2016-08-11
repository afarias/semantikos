package cl.minsal.semantikos.model.businessrules;

import cl.minsal.semantikos.model.*;
import cl.minsal.semantikos.model.exceptions.BusinessRuleException;
import org.junit.Test;

import static cl.minsal.semantikos.model.businessrules.ConceptCreationBusinessRuleContainer.CATEGORY_FARMACOS_MEDICAMENTO_BASICO_NAME;
import static cl.minsal.semantikos.model.businessrules.ConceptCreationBusinessRuleContainer.CATEGORY_FARMACOS_SUSTANCIAS_NAME;

public class ConceptCreationBusinessRuleContainerTest {

    private static final String ADMIN_PROFILE_NAME = "Admin";
    private static final String DESIGNER_PROFILE_NAME = "Diseñador";
    private static final String MODELER_PROFILE_NAME = "Modelador";

    private ConceptCreationBusinessRuleContainer conceptCreationBRC = new ConceptCreationBusinessRuleContainer();
    private Category catFarSus = createCategory(CATEGORY_FARMACOS_SUSTANCIAS_NAME);
    private Category catFarMedBas = createCategory(CATEGORY_FARMACOS_MEDICAMENTO_BASICO_NAME);

    private User userDesigner = createUserProfile(DESIGNER_PROFILE_NAME);
    private User userModeler = createUserProfile(MODELER_PROFILE_NAME);
    private User userAdmin = createUserProfile(ADMIN_PROFILE_NAME);

    @Test(expected = BusinessRuleException.class)
    public void testApply_FarmSubs_Designer() throws Exception {
        ConceptSMTK conceptSMTK = new ConceptSMTK(catFarSus);

        conceptCreationBRC.apply(conceptSMTK, userDesigner);
    }

    @Test(expected = BusinessRuleException.class)
    public void testApply_FarmSubst_Modeler() throws Exception {
        ConceptSMTK conceptSMTK = new ConceptSMTK(catFarSus);

        conceptCreationBRC.apply(conceptSMTK, userModeler);
    }

    @Test(expected = BusinessRuleException.class)
    public void testApply_FarmsMedBas() throws Exception {
        ConceptSMTK conceptSMTK = new ConceptSMTK(catFarMedBas);
        conceptCreationBRC.apply(conceptSMTK, userDesigner);
    }

    @Test(expected = BusinessRuleException.class)
    public void testApply_FarmsMedBas_Modeler() throws Exception {
        ConceptSMTK conceptSMTK = new ConceptSMTK(catFarMedBas);

        conceptCreationBRC.apply(conceptSMTK, userModeler);
    }

    /**
     * Usuarios con rol de Diseñador o Modelador pueden crear conceptos de esta categoria.
     *
     * @throws Exception
     */
    @Test
    public void testBR001_01() throws Exception {
        ConceptSMTK conceptSMTK = new ConceptSMTK(catFarSus);
        conceptCreationBRC.br001creationRights(conceptSMTK, userDesigner);
    }

    /**
     * Usuarios con rol de Diseñador o Modelador pueden crear conceptos de esta categoria.
     *
     * @throws Exception
     */
    @Test
    public void testBR001_02() throws Exception {
        conceptCreationBRC.br001creationRights(new ConceptSMTK(), userModeler);
    }

    /**
     * Usuarios con rol de Diseñador o Modelador pueden crear conceptos de esta categoria.
     * Este test verifica si usuarios con otros roles no lo logran
     *
     */
    @Test(expected = BusinessRuleException.class)
    public void testBR001_03() {
        /* perfil Admin no debe poder crear conceptos */
        conceptCreationBRC.br001creationRights(new ConceptSMTK(catFarSus), userAdmin);
    }

    /**
     * Usuarios con rol de Diseñador o Modelador pueden crear conceptos de esta categoria.
     *
     * @throws Exception
     */
    @Test
    public void testBR002_01() throws Exception {
        conceptCreationBRC.br002creationRights(new ConceptSMTK(catFarMedBas), userDesigner);
    }

    /**
     * Usuarios con rol de Diseñador o Modelador pueden crear conceptos de esta categoria.
     *
     * @throws Exception
     */
    @Test
    public void testBR002_02() throws Exception {
        conceptCreationBRC.br002creationRights(new ConceptSMTK(catFarMedBas), userModeler);
    }

    /**
     * Usuarios con rol de Diseñador o Modelador pueden crear conceptos de esta categoria.
     * Un usuario Admin debe arrojar una excepcion
     * @throws Exception
     */
    @Test(expected = BusinessRuleException.class)
    public void testBR002_03() throws Exception {
        conceptCreationBRC.br002creationRights(new ConceptSMTK(catFarMedBas), createUserProfile(ADMIN_PROFILE_NAME));
    }

    @Test
    public void testApply02() throws Exception {

        Category substancesCategory = createCategory("Fármacos - Sustancias");

        ConceptSMTK conceptSMTK = new ConceptSMTK();
        conceptSMTK.setCategory(substancesCategory);

        DescriptionType fsnDT = DescriptionTypeFactory.getInstance().getFSNDescriptionType();
        Description fsn = new Description("FSN", fsnDT);
        conceptSMTK.addDescription(fsn);

        DescriptionType favDT = DescriptionTypeFactory.getInstance().getFavoriteDescriptionType();
        Description fav = new Description("Preferida", favDT);
        conceptSMTK.addDescription(fav);

        conceptCreationBRC.apply(conceptSMTK, userDesigner);
    }

    private Category createCategory(String categoryName) {
        Category substancesCategory = new Category();
        substancesCategory.setName(categoryName);
        return substancesCategory;
    }

    /**
     * Este método es responsable de crear un usuario con perfil Diseñador.
     *
     * @return Un usuario fresco de perfil diseñador.
     */
    private User createUserProfile(String profileName) {
        User user = new User();
        Profile profile = new Profile();
        profile.setName(profileName);
        user.addProfile(profile);
        return user;
    }
}