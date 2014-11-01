package retrieval.server.index;

import java.util.Map;

/**
 * An association with picture path, picture thumb path and similarity between
 * picture and request image.
 * @author Rollus Loic
 */
public class ResultSim implements java.lang.Comparable {

    private Long id;
    private Map<String,String> properties;
    private double similarities;

    /**
     * Constructor for a result object
     * @param picturePath Picture path
     * @param similarities Similarity between picture and request image
     */
    public ResultSim(Long id, Map<String,String> properties, double similarities) {
        this.id = id;
        this.properties = properties;
        this.similarities = similarities;
    }

    /**
     * Return String value for this object
     * @return String value
     */
    @Override
    public String toString() {
        return getId() + " ### " + getSimilarities();
    }

    /**
     * Compare this with object other
     * @param   other   Object that must be compare width this
     * @return   0 if this is equal to other, 1 if other is bigger than this
     * else -1
     **/
    public int compareTo(Object other) {
        double nombre1 = ((ResultSim) other).getSimilarities();
        double nombre2 = this.getSimilarities();
        if (nombre1 > nombre2) {
            return 1;
        } else if (nombre1 == nombre2) {
            return 0;
        } else {
            return -1;
        }
    }

//    /**
//     * Get picture path
//     * @return Picture path
//     */
//    public String getPicturePath() {
//        return picturePath;
//    }
//
//    /**
//     * Set Picture path
//     * @param picturePath Picture path
//     */
//    public void setPicturePath(String picturePath) {
//        this.picturePath = picturePath;
//    }

    /**
     * Get similarities
     * @return Similarities
     */
    public double getSimilarities() {
        return similarities;
    }

    /**
     * Set similarities
     * @param similarities Similarities
     */
    public void setSimilarities(double similarities) {
        this.similarities = similarities;
    }

    /**
     * Divide similarities by divideBy
     * @param divideBy Divisor
     */
    public void divideSimilarities(double divideBy) {
        this.similarities = similarities/divideBy;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the properties
     */
    public Map<String,String> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Map<String,String> properties) {
        this.properties = properties;
    }
}
