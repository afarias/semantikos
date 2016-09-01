package cl.minsal.semantikos.util;

import cl.minsal.semantikos.model.*;
import cl.minsal.semantikos.model.relationships.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 01-09-16.
 */
public class ConceptSMTKUtils {

    public static ConceptSMTK clone(ConceptSMTK concept){
        ConceptSMTK clone= new ConceptSMTK(concept.getId(), concept.getConceptID(), concept.getCategory(),
                                           concept.isToBeReviewed(), concept.isToBeConsulted(), concept.isModeled(),
                                           concept.isFullyDefined(), concept.isPublished());
        for (Description description : concept.getValidDescriptions()) {
            clone.addDescription(new Description(description.getDescriptionId(), description.getDescriptionType(),
                                                 description.getTerm(), description.isCaseSensitive(),
                                                 description.isAutogeneratedName(), description.isActive(),
                                                 description.isPublished(), description.getValidityUntil()));
        }
        for (Relationship relationship : concept.getValidRelationships()) {
            clone.addRelationship(new Relationship(relationship.getId(), relationship.getSourceConcept(),
                                                   relationship.getTarget(), relationship.getRelationshipDefinition(),
                                                   null));
        }
        return clone;
    }

    public static boolean equivalent(Description description1, Description description2){
        return description1.getTerm().equals(description2.getTerm()) &&
               description1.isCaseSensitive()==description2.isCaseSensitive() &&
               description1.isAutogeneratedName()==description2.isAutogeneratedName() &&
               description1.getDescriptionType().equals(description2.getDescriptionType());
    }

    public static boolean equivalent(Relationship relationship1, Relationship relationship2){
        return (relationship1.getSourceConcept().getId()==relationship2.getSourceConcept().getId() &&
                relationship1.getRelationshipDefinition().getId()==relationship2.getRelationshipDefinition().getId() &&
                relationship1.getTarget().getId()==relationship2.getTarget().getId());
    }

    public static List<Pair<Description, Description>> getDescriptionsForUpdate(List<Description> initDescriptions, List<Description> finalDescriptions) {

        List<Pair<Description, Description>> descriptionsForUpdate = new ArrayList<Pair<Description, Description>>();

        //Primero se buscan todas las descripciones persistidas originales
        for (Description initDescription : initDescriptions) {
            //Por cada descripción original se busca su descripcion vista correlacionada
            for (Description finalDescription : finalDescriptions) {
                //Si la descripcion correlacionada sufrio alguna modificación agregar el par (init, final)
                if (initDescription.getId() == finalDescription.getId() && !equivalent(initDescription, finalDescription) /*finalDescription.hasBeenModified()*/) {
                    descriptionsForUpdate.add(new Pair(initDescription, finalDescription));
                }
            }
        }

        return descriptionsForUpdate;
    }

    public static List<Description> getDescriptionsForPersist(List<Description> initDescriptions, List<Description> finalDescriptions) {

        List<Description> descriptionsForPersist = new ArrayList<Description>();

        for (Description description : finalDescriptions) {
            if(!description.isPersistent())
                descriptionsForPersist.add(description);
        }

        return descriptionsForPersist;
    }

    public static List<Description> getDescriptionsForDelete(List<Description> initDescriptions, List<Description> finalDescriptions) {

        List<Description> descriptionsForDelete = new ArrayList<Description>();
        boolean isDescriptionFound;

        //Primero se buscan todas las descripciones persistidas originales
        for (Description initDescription : initDescriptions) {
            isDescriptionFound = false;
            //Por cada descripción original se busca su descripcion vista correlacionada
            for (Description finalDescription : finalDescriptions) {
                //Si la descripcion correlacionada no es encontrada, significa que fué eliminada
                if (initDescription.getId() == finalDescription.getId()) {
                    isDescriptionFound = true;
                }
            }
            if(!isDescriptionFound)
                descriptionsForDelete.add(initDescription);
        }

        return descriptionsForDelete;
    }

    public static List<Pair<Relationship, Relationship>> getRelationshipsForUpdate(List<Relationship> initRelationships, List<Relationship> finalRelationships) {

        List<Pair<Relationship, Relationship>> relationshipsForUpdate = new ArrayList<Pair<Relationship, Relationship>>();

        //Primero se buscan todas las descripciones persistidas originales
        for (Relationship initRelationship : initRelationships) {
            //Por cada descripción original se busca su descripcion vista correlacionada
            for (Relationship finalRelationship : finalRelationships) {
                //Si la descripcion correlacionada sufrio alguna modificación agregar el par (init, final)
                if (initRelationship.getId() == finalRelationship.getId() && !equivalent(initRelationship, finalRelationship) /*finalDescription.hasBeenModified()*/) {
                    relationshipsForUpdate.add(new Pair(initRelationship, finalRelationship));
                }
            }
        }

        return relationshipsForUpdate;
    }

    public static List<Relationship> getRelationshipsForPersist(List<Relationship> initRelationships, List<Relationship> finalRelationships) {

        List<Relationship> relationshipsForPersist = new ArrayList<Relationship>();

        for (Relationship relationship : finalRelationships) {
            if(!relationship.isPersistent())
                relationshipsForPersist.add(relationship);
        }

        return relationshipsForPersist;
    }

    public static List<Relationship> getRelationshipsForDelete(List<Relationship> initRelationships, List<Relationship> finalRelationships) {

        boolean isRelationshipFound;
        List<Relationship> relationshipsForDelete = new ArrayList<Relationship>();

        //Primero se buscan todas las descripciones persistidas originales
        for (Relationship initRelationship : initRelationships) {
            isRelationshipFound = false;
            //Por cada descripción original se busca su descripcion vista correlacionada
            for (Relationship finalRelationship : finalRelationships) {
                //Si la descripcion correlacionada no es encontrada, significa que fué eliminada
                if (finalRelationship.getId() == finalRelationship.getId()) {
                    isRelationshipFound = true;
                }
            }
            if(!isRelationshipFound)
                relationshipsForDelete.add(initRelationship);
        }

        return relationshipsForDelete;
    }


}