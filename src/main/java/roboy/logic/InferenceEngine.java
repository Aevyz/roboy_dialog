package roboy.logic;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jLabel;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;

import java.util.ArrayList;
import java.util.HashMap;

public interface InferenceEngine {

	// There lies the future of Roboy and the brilliance of his logic
    // TODO: Implement

    /**
     * Basic inference method
     * Infers the property information with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return HashMap containing properties and inferred data/null if NA
     */
    HashMap<Neo4jProperty, String> inferProperties(ArrayList<Neo4jProperty> keys, Interpretation input);

    /**
     * Basic inference method
     * Infers the property information with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param key
     * @return String containing inferred result/null if NA
     */
    String inferProperty(Neo4jProperty key, Interpretation input);

    /**
     * Basic inference method
     * Infers the relationship information with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return HashMap containing relationships and inferred data/null if NA
     */
    HashMap<Neo4jRelationship, String> inferRelationships(ArrayList<Neo4jRelationship> keys, Interpretation input);

    /**
     * Basic inference method
     * Infers the relationship information with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param key
     * @return String containing inferred result/null if NA
     */
    String inferRelationship(Neo4jRelationship key, Interpretation input);

    /**
     * Basic inference method
     * Infers the label information with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param keys
     * @return HashMap containing labels and inferred data/null if NA
     */
    HashMap<Neo4jLabel, String> inferLabels(ArrayList<Neo4jLabel> keys, Interpretation input);

    /**
     * Basic inference method
     * Infers the label information with regard to the requested keys,
     * tries to extract and ground the information from the available Interpretation
     *
     * @param key
     * @return String containing inferred result/null if NA
     */
    String inferLabel(Neo4jLabel key, Interpretation input);

}
