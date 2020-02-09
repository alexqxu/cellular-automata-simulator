package cellsociety.config;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import org.xml.sax.SAXException;


/**
 * Validates an XML configuration file against a defined XML Schema File (XSD)
 * @author Alex Xu, aqx
 */
public class XMLValidator {
    public static final String XSD_SCHEMA_FILEPATH = "src\\cellsociety\\config\\schema.xsd";

    /**
     * Validates a XML file against the XSD file that is given as part of the program.
     * @param xmlFile
     * @return true if the document structure is valid, false otherwise.
     */
    public static boolean validateXMLStructure(File xmlFile){
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(XSD_SCHEMA_FILEPATH));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));
        }catch(IOException | SAXException e){
            return false;
        }
        return true;
    }
}