package cl.minsal.semantikos.designmodelweb.mbeans;

import cl.minsal.semantikos.kernel.components.AuditManagerInterface;
import cl.minsal.semantikos.kernel.components.ConceptManagerInterface;
import cl.minsal.semantikos.model.ConceptSMTK;
import cl.minsal.semantikos.model.audit.AuditableEntity;
import cl.minsal.semantikos.model.audit.ConceptAuditAction;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * Created by des01c7 on 24-08-16.
 */

@ManagedBean(name = "historyConcept")
@ViewScoped
public class historyConceptBean {

    @EJB
    AuditManagerInterface auditManager;

    @EJB
    ConceptManagerInterface conceptManager;

    private List<ConceptAuditAction> auditAction;

    private ConceptSMTK conceptSMTK;

    @PostConstruct
    public void init(){
        conceptSMTK=conceptManager.getConceptByID(1);
        auditAction=auditManager.getConceptAuditActions(conceptSMTK,10,true);
    }

    public List<ConceptAuditAction> getAuditAction() {
        return auditAction;
    }

    public void setAuditAction(List<ConceptAuditAction> auditAction) {
        this.auditAction = auditAction;
    }

    public ConceptSMTK getConceptSMTK() {
        return conceptSMTK;
    }

    public void setConceptSMTK(ConceptSMTK conceptSMTK) {
        this.conceptSMTK = conceptSMTK;
    }
}
