package retrieval.testvector;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This class implement a Associative Array with XML structure
 * @author Loic Rollus
 **/
public class AssociativeArrayXML {

    /** Document XML of Associative Array **/
    private org.jdom.Document document;
    /** List of Test Association **/
    private List<TestAssoc> testsAssociation;
    private String name;

    /**
     * Build a list of Generic Associative Array
     * Each item i of the array will be [key[i] ; value[i]; position[i]
     * @param   name   Name of array
     * @param   keys   Array of keys
     * @param   values   Array of Value
     * @param   position  Array of position
     */
    public AssociativeArrayXML(String name, Object[] keys, Object[] values, Object[] position) {

        this.name = name;
        Element racine = new Element("vector");
        document = new Document(racine);
        Attribute n = new Attribute("store", name);
        racine.setAttribute(n);

        for (int i = 0; i < keys.length; i++) {
            Element keyvalue = new Element("value");
            racine.addContent(keyvalue);
            Attribute k = new Attribute("key", keys[i].toString());
            keyvalue.setAttribute(k);
            Attribute v = new Attribute("value", values[i].toString());
            keyvalue.setAttribute(v);
            Attribute p = new Attribute("position", position[i].toString());
            keyvalue.setAttribute(p);
        }
        testsAssociation = new ArrayList<TestAssoc>();
        for (int i = 0; i < keys.length; i++) {
            testsAssociation.add(new TestAssoc(keys[i], values[i], position[i]));
        }
    }

    /**
     * Save the array into the file path f
     * @param   f   Path of the file
     * @exception  IOException  if file path f is incorrect
     */
    public void saveXML(String f) throws IOException {
        XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
        sortie.output(document, new FileOutputStream(f + "/" + name + ".xml"));
    }
}

/**
 * A simple association between two objects
 * @author Loic Rollus
 **/
class TestAssoc implements Serializable {

    /** Object key **/
    public Object a;
    /** Object value **/
    public Object b;
    /** Object position **/
    public Object c;

    /**
     * Construct a new association between a and b
     * @param   a   Object key
     * @param   b   Object value
     * @param   c   Object value bis
     */
    public TestAssoc(Object a, Object b, Object c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
