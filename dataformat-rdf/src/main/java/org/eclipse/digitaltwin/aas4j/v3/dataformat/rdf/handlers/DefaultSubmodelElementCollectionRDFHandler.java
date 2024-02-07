package org.eclipse.digitaltwin.aas4j.v3.dataformat.rdf.handlers;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.rdf.*;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.rdf.handlers.partial.DefaultHasDataSpecificationRDFPartialHandler;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.rdf.handlers.partial.DefaultHasSemanticsRDFPartialHandler;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.rdf.handlers.partial.DefaultQualifiableRDFPartialHandler;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.rdf.handlers.partial.DefaultReferableRDFPartialHandler;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSubmodelElementCollectionRDFHandler implements RDFHandler<SubmodelElementCollection> {
    @Override
    public RDFSerializationResult toModel(SubmodelElementCollection object) {
        Model model = ModelFactory.createDefaultModel();
        if (object == null) {
            return new DefaultRDFHandlerResult(model, ResourceFactory.createResource());
        }
        Resource subject = model.createResource();
        model.add(subject, RDF.type, AASNamespace.Types.SubmodelElementCollection);
        if (object.getValue() != null && object.getValue().isEmpty() == false) {
            int index = 0;
            for (SubmodelElement item : object.getValue()) {
                RDFSerializationResult resultItem = new DefaultSubmodelElementRDFHandler().toModel(item);
                resultItem.getResource().addLiteral(AASNamespace.index, index);
                model.add(subject, AASNamespace.SubmodelElementCollection.value, resultItem.getResource());
                // It is important where to put model.add
                model.add(resultItem.getModel());
                index = index + 1;
            }
        }
        //HasDataSpecification
        new DefaultHasDataSpecificationRDFPartialHandler().partialToModel(object, model, subject);
        //HasSemantics
        new DefaultHasSemanticsRDFPartialHandler().partialToModel(object, model, subject);
        //Qualifiable
        new DefaultQualifiableRDFPartialHandler().partialToModel(object, model, subject);
        //Referable
        new DefaultReferableRDFPartialHandler().partialToModel(object, model, subject);
        return new DefaultRDFHandlerResult(model, subject);
    }

    @Override
    public SubmodelElementCollection fromModel(Model model, Resource subjectToParse) throws IncompatibleTypeException {
        if (!model.contains(subjectToParse, RDF.type, AASNamespace.Types.SubmodelElementCollection)) {
            throw new IncompatibleTypeException("SubmodelElementCollection");
        }
        DefaultSubmodelElementCollection.Builder builder = new DefaultSubmodelElementCollection.Builder();
        if (model.contains(subjectToParse, AASNamespace.SubmodelElementCollection.value)) {
            NodeIterator nodeIterator = model.listObjectsOfProperty(subjectToParse, AASNamespace.ConceptDescription.isCaseOf);
            Map<Integer, SubmodelElement> keysMap = new HashMap<>();
            nodeIterator.forEachRemaining(node -> {
                SubmodelElement key = null;
                try {
                    key = new DefaultSubmodelElementRDFHandler().fromModel(model, node.asResource());
                } catch (IncompatibleTypeException e) {
                    throw new RuntimeException(e);
                }
                int index = model.getProperty(node.asResource(), AASNamespace.index).getInt();
                keysMap.put(index, key);
            });
            List<SubmodelElement> references = new ArrayList<>();
            for (int index = 0; index < keysMap.keySet().size(); index++) {
                references.add(keysMap.get(index));
            }
            builder.value(references);
        }
        SubmodelElementCollection object = builder.build();
        //HasDataSpecification
        new DefaultHasDataSpecificationRDFPartialHandler().partialFromModel(object, model, subjectToParse);
        //HasSemantics
        new DefaultHasSemanticsRDFPartialHandler().partialFromModel(object, model, subjectToParse);
        //Qualifiable
        new DefaultQualifiableRDFPartialHandler().partialFromModel(object, model, subjectToParse);
        //Referable
        new DefaultReferableRDFPartialHandler().partialFromModel(object, model, subjectToParse);
        return object;
    }
}
